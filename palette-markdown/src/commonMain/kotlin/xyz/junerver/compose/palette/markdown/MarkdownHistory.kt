package xyz.junerver.compose.palette.markdown

import kotlin.time.TimeSource

/**
 * 编辑器撤销 / 重做历史栈（纯逻辑、无 Compose 依赖，便于单元测试）。
 *
 * 设计要点：
 * - 每次变更产生一个 [Entry]，包含文本与光标。
 * - 连续的"打字"[pushTyping] 会在阈值窗口内合并为同一个 Entry（Ctrl+Z 按词/段回退，而非逐字符）。
 * - 工具栏、快捷键、粘贴等结构化操作走 [commit]，永远开启新的历史项。
 * - [undo]/[redo] 在 past/present/future 之间移动，不破坏另一侧栈。
 * - 容量有上限，避免超长编辑会话内存膨胀。
 */

/** 撤销栈中的一个历史项（纯数据：文本 + 选区）。 */
public data class MarkdownHistoryEntry(
    val text: String,
    val selection: MarkdownSelection,
)

public class MarkdownHistory(
    initial: MarkdownHistoryEntry,
    private val capacity: Int = DEFAULT_CAPACITY,
    private val coalesceMs: Long = DEFAULT_COALESCE_MS,
    private val now: () -> Long = { defaultNow() },
) {
    private data class Entry(val value: MarkdownHistoryEntry, val time: Long, val coalescing: Boolean = false)

    private val past = ArrayDeque<Entry>()
    private val future = ArrayDeque<Entry>()
    private var present: Entry = Entry(initial, now())

    /** 当前值（文本 + 光标）。 */
    public val current: MarkdownHistoryEntry get() = present.value

    /** 是否可撤销。 */
    public val canUndo: Boolean get() = past.isNotEmpty()

    /** 是否可重做。 */
    public val canRedo: Boolean get() = future.isNotEmpty()

    /**
     * 记录一次"打字"变更：若距上次变更在 [coalesceMs] 窗口内，且新值是旧值的光标连续扩展
     * （仅追加或仅删除相邻字符），则合并到当前项 —— 但合并序列的**起点**会把变更前状态压入
     * past，使一次撤销能回退整段连续输入，而非逐字符。
     */
    public fun pushTyping(next: MarkdownHistoryEntry) {
        if (shouldCoalesce(present.value, next, present.time)) {
            // 若当前 present 尚未处于合并序列，先把"变更前"状态入栈，作为本次合并的撤销锚点。
            if (!present.coalescing) {
                pushPresentToPast()
            }
            present = Entry(next, now(), coalescing = true)
        } else {
            commit(next)
        }
    }

    /**
     * 强制提交一个独立历史项（用于工具栏、快捷键、粘贴、拖拽等结构化操作）。
     * 清空 future（一旦有新输入，重做分支即失效）。相同文本不产生新项。
     */
    public fun commit(next: MarkdownHistoryEntry) {
        if (next.text == present.value.text && next.selection == present.value.selection) return
        pushPresentToPast()
        future.clear()
        present = Entry(next, now())
    }

    /** 把当前 present 压入 past（带容量淘汰），不变更 present 自身。 */
    private fun pushPresentToPast() {
        past.addLast(present)
        if (past.size > capacity) past.removeFirst()
    }

    /**
     * 外部受控值同步：用新值覆盖 present，且不产生历史项、不清 future
     * （用于父组件回写、程序化设值，不应污染用户可撤销的历史）。
     */
    public fun sync(next: MarkdownHistoryEntry) {
        present = Entry(next, now())
    }

    /** 撤销一步，返回新当前值；栈空时不变更。 */
    public fun undo(): MarkdownHistoryEntry {
        if (!canUndo) return present.value
        future.addLast(present)
        present = past.removeLast()
        return present.value
    }

    /** 重做一步，返回新当前值；栈空时不变更。 */
    public fun redo(): MarkdownHistoryEntry {
        if (!canRedo) return present.value
        past.addLast(present)
        present = future.removeLast()
        return present.value
    }

    /** 清空全部历史，重置为给定值（用于切换文档）。 */
    public fun reset(value: MarkdownHistoryEntry) {
        past.clear()
        future.clear()
        present = Entry(value, now())
    }

    /**
     * 判断 [next] 是否可与 [prev] 合并：
     * 1. 时间窗口未过；
     * 2. 文本差异为"在光标处仅追加或仅删除相邻字符"；
     * 3. 新光标紧邻变更点（连续输入）。
     */
    private fun shouldCoalesce(prev: MarkdownHistoryEntry, next: MarkdownHistoryEntry, prevTime: Long): Boolean {
        if (now() - prevTime > coalesceMs) return false
        val p = prev.text
        val n = next.text
        val pc = prev.selection.end
        val nc = next.selection.end
        if (pc < 0 || pc > p.length) return false
        if (nc < 0 || nc > n.length) return false
        val delta = n.length - p.length
        return when {
            // 追加 1 个字符
            delta == 1 && n.startsWith(p) && nc == pc + 1 -> true
            // 一次粘贴/输入多个字符（光标连续前移）
            delta > 1 && n.startsWith(p) && nc == pc + delta -> true
            // 删除 1 个字符（退格 / 删除键）
            delta == -1 && p.startsWith(n) && nc == pc - 1 -> true
            // 一次删除多个相邻字符
            delta < -1 && p.startsWith(n) && nc == pc + delta -> true
            else -> false
        }
    }

    public companion object {
        public const val DEFAULT_CAPACITY = 500
        public const val DEFAULT_COALESCE_MS = 600L
    }
}

/** 跨平台单调时钟：返回自首次调用以来的相对毫秒数，用于合并阈值判定（无需绝对时间）。 */
private val monotonicStart: TimeSource.Monotonic.ValueTimeMark by lazy { TimeSource.Monotonic.markNow() }

private fun defaultNow(): Long = monotonicStart.elapsedNow().inWholeMilliseconds
