package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange

/**
 * 编辑动作的纯函数实现。所有函数都接收完整文本与选区，
 * 返回新的文本与选区，便于：
 *  1. 单元测试（无需 Compose 环境；
 *  2. 工具栏按钮、键盘快捷键、续行逻辑共用同一套变换。
 */
internal data class MarkdownEditResult(
    val text: String,
    val selection: TextRange,
)

// region 辅助：行范围

/** 返回选区覆盖到的首行起点与末行终点（闭区间坐标，可直接用于 substring 边界）。 */
internal fun currentLineRange(text: String, selection: TextRange): Pair<Int, Int> {
    val start = selection.min.coerceIn(0, text.length)
    val end = selection.max.coerceIn(0, text.length)
    val lineStart = text.lastIndexOf('\n', startIndex = start).let { if (it < 0) 0 else it + 1 }
    val lineEnd = text.indexOf('\n', startIndex = end).let { if (it < 0) text.length else it }
    return lineStart to lineEnd
}

/** 返回选区覆盖的所有行的 [lineStart, lineEnd) 列表。 */
internal fun selectedLineRanges(text: String, selection: TextRange): List<Pair<Int, Int>> {
    val (firstStart, _) = currentLineRange(text, TextRange(selection.min, selection.min))
    var cursor = firstStart
    val result = mutableListOf<Pair<Int, Int>>()
    while (cursor <= text.length) {
        val nl = text.indexOf('\n', startIndex = cursor)
        val end = if (nl < 0) text.length else nl
        result.add(cursor to end)
        if (nl < 0 || end >= text.length) break
        cursor = nl + 1
        if (cursor > selection.max) break
    }
    if (result.isEmpty()) result.add(0 to text.length)
    return result
}
// endregion

// region 行内包裹：加粗 / 斜体 / 删除线 / 行内代码

/**
 * 用 [prefix]/[suffix] 包裹选区。无选区时插入 `prefix+suffix` 并把光标置于中间；
 * 有选区时包裹后在内容末尾落点。重复包裹会撤销（若选区已恰好被包裹）。
 */
internal fun wrapSelection(
    text: String,
    selection: TextRange,
    prefix: String,
    suffix: String = prefix,
): MarkdownEditResult {
    val start = selection.min.coerceIn(0, text.length)
    val end = selection.max.coerceIn(0, text.length)
    val hasSelection = start != end
    return if (hasSelection) {
        val inner = text.substring(start, end)
        // 已包裹则撤销
        if (inner.startsWith(prefix) && inner.endsWith(suffix) && inner.length >= prefix.length + suffix.length) {
            val unwrapped = inner.substring(prefix.length, inner.length - suffix.length)
            val newText = text.substring(0, start) + unwrapped + text.substring(end)
            MarkdownEditResult(newText, TextRange(start, start + unwrapped.length))
        } else {
            val newText = text.substring(0, start) + prefix + inner + suffix + text.substring(end)
            MarkdownEditResult(newText, TextRange(start, start + prefix.length + inner.length))
        }
    } else {
        val newText = text.substring(0, start) + prefix + suffix + text.substring(end)
        MarkdownEditResult(newText, TextRange(start + prefix.length, start + prefix.length))
    }
}
// endregion

// region 行级前缀：列表 / 引用

/**
 * 为选区覆盖的每一行切换前缀（如 `- ` / `> `）。
 * - [ordered] = true 时使用自增数字前缀 `1. `、`2. ` …
 * - 已全部带该前缀则移除，否则全部添加。
 * 选区更新为覆盖变更后的所有行。
 */
internal fun toggleLinePrefix(
    text: String,
    selection: TextRange,
    prefix: String,
    ordered: Boolean = false,
): MarkdownEditResult {
    val ranges = selectedLineRanges(text, selection)
    val allPrefixed = ranges.all { (start, _) ->
        text.startsWith(prefixLineFor(prefix, 1, ordered), startIndex = start)
    }
    val sb = StringBuilder()
    var idx = 1
    var firstNewStart = -1
    var lastNewEnd = -1
    var writeCursor = 0
    for ((start, end) in ranges) {
        // 复制区间外的内容
        sb.append(text, writeCursor, start)
        val lineContent = text.substring(start, end)
        val newLine = if (allPrefixed) {
            // 移除已有前缀
            removeKnownListPrefix(lineContent)
        } else {
            // 先剥除可能的已有列表前缀，再统一添加
            val stripped = removeKnownListPrefix(lineContent)
            prefixLineFor(prefix, idx, ordered) + stripped
        }
        if (firstNewStart < 0) firstNewStart = sb.length
        sb.append(newLine)
        lastNewEnd = sb.length
        idx++
        writeCursor = end
    }
    sb.append(text, writeCursor, text.length)
    val newSel = if (firstNewStart < 0) selection else TextRange(firstNewStart, lastNewEnd)
    return MarkdownEditResult(sb.toString(), newSel)
}

private fun prefixLineFor(prefix: String, index: Int, ordered: Boolean): String =
    if (ordered) "$index. " else prefix

/** 移除行首已知的列表 / 引用前缀（`-`/`*`/`+`/`数字.`/`>`），返回剩余内容。 */
private fun removeKnownListPrefix(line: String): String {
    val matched = ListLinePrefixRegex.find(line) ?: return line
    return line.substring(matched.range.last + 1)
}

private val ListLinePrefixRegex = Regex("""^[ \t]*(?:[-*+]\s+|\d+[.)]\s+|>\s+)+""")

// endregion

// region 任务列表项

private val TaskPrefixRegex = Regex("""^([ \t]*[-*+]\s+)(\[[ xX]]\s+)?(.*)$""")

/** 在选区所在行切换任务列表项前缀 `- [ ] ` / `- [x] `。若不是列表项则先转为无序列表再标记。 */
internal fun toggleTaskItem(text: String, selection: TextRange): MarkdownEditResult {
    val (lineStart, lineEnd) = currentLineRange(text, selection)
    val line = text.substring(lineStart, lineEnd)
    val match = TaskPrefixRegex.matchEntire(line)
    val newLine = if (match != null) {
        val marker = match.groupValues[1]
        val existing = match.groupValues[2]
        val rest = match.groupValues[3]
        if (existing.isBlank()) {
            "$marker[ ] $rest"
        } else if (existing.lowercase().contains("x")) {
            "$marker$rest"
        } else {
            "${marker}[x] $rest"
        }
    } else {
        "- [ ] $line"
    }
    val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
    val selStart = lineStart + newLine.length
    return MarkdownEditResult(newText, TextRange(selStart, selStart))
}
// endregion

// region 标题

/** 设置选区所在行的标题层级。[level]=0 表示清除标题。level 取 1..6。 */
internal fun setHeadingLevel(text: String, selection: TextRange, level: Int): MarkdownEditResult {
    val ranges = selectedLineRanges(text, selection)
    val sb = StringBuilder()
    var writeCursor = 0
    var firstStart = -1
    var lastEnd = -1
    for ((start, end) in ranges) {
        sb.append(text, writeCursor, start)
        val content = removeHeadingPrefix(text.substring(start, end))
        val newLine = if (level <= 0) content else "${"#".repeat(level.coerceIn(1, 6))} $content"
        if (firstStart < 0) firstStart = sb.length
        sb.append(newLine)
        lastEnd = sb.length
        writeCursor = end
    }
    sb.append(text, writeCursor, text.length)
    val newSel = if (firstStart < 0) selection else TextRange(firstStart, lastEnd)
    return MarkdownEditResult(sb.toString(), newSel)
}

private val HeadingPrefixRegex = Regex("""^\s{0,3}#{1,6}\s+""")

private fun removeHeadingPrefix(line: String): String {
    val matched = HeadingPrefixRegex.find(line) ?: return line.trimStart()
    return line.substring(matched.range.last + 1)
}
// endregion

// region 插入：链接 / 图片 / 代码块 / 表格 / 分隔线 / 换行

/**
 * 在光标处插入 [snippet]。若 [selectInside] 非空，则将其作为占位文本插入并选中
 * （如 `[text](url)` 中的 `text` 或 `url`）。
 */
internal fun insertText(
    text: String,
    selection: TextRange,
    snippet: String,
    selectInside: IntRange? = null,
): MarkdownEditResult {
    val start = selection.min.coerceIn(0, text.length)
    val end = selection.max.coerceIn(0, text.length)
    // 保证块级插入（代码块/表格/分隔线）独占行：前后补换行
    val isBlock = snippet.startsWith("```") || snippet.startsWith("|") || snippet.startsWith("---") || snippet.startsWith("***")
    val prefix = if (isBlock && start > 0 && text[start - 1] != '\n') "\n" else ""
    val suffix = if (isBlock && end < text.length && text[end] != '\n') "\n" else ""
    val inserted = prefix + snippet + suffix
    val newText = text.substring(0, start) + inserted + text.substring(end)
    val newSel = if (selectInside == null) {
        TextRange(start + inserted.length, start + inserted.length)
    } else {
        val s = start + prefix.length + selectInside.first
        val e = start + prefix.length + selectInside.last + 1
        TextRange(s, e)
    }
    return MarkdownEditResult(newText, newSel)
}

/** 默认表格模板（2 列 2 行占位）。 */
internal val defaultTableSnippet: String =
    "| Column A | Column B |\n| --- | --- |\n| cell | cell |"

/** 默认代码块模板。 */
internal fun defaultCodeFence(language: String = ""): String =
    "```$language\n\n```\n"
// endregion

// region 缩进：Tab / Shift+Tab

private const val INDENT_UNIT = "    " // 4 空格，兼容 fenced code 缩进代码块

/** 缩进选区覆盖的所有行。[forward]=true 缩进，false 反缩进。 */
internal fun indent(text: String, selection: TextRange, forward: Boolean): MarkdownEditResult {
    val ranges = selectedLineRanges(text, selection)
    val sb = StringBuilder()
    var writeCursor = 0
    var firstStart = -1
    var lastEnd = -1
    for ((start, end) in ranges) {
        sb.append(text, writeCursor, start)
        val line = text.substring(start, end)
        val newLine = if (forward) {
            INDENT_UNIT + line
        } else {
            when {
                line.startsWith(INDENT_UNIT) -> line.substring(INDENT_UNIT.length)
                line.startsWith("  ") -> line.substring(2)
                line.startsWith("\t") -> line.substring(1)
                line.startsWith(" ") -> line.substring(1)
                else -> line
            }
        }
        if (firstStart < 0) firstStart = sb.length
        sb.append(newLine)
        lastEnd = sb.length
        writeCursor = end
    }
    sb.append(text, writeCursor, text.length)
    val newSel = if (firstStart < 0) selection else TextRange(firstStart, lastEnd)
    return MarkdownEditResult(sb.toString(), newSel)
}
// endregion

// region 回车续行：列表 / 引用

private val ContinuablePrefixRegex = Regex("""^([ \t]*)((?:[-*+]\s+)|(\d+[.)]\s+)|(>\s+)+)(.*)$""")

/**
 * 回车续行逻辑。返回 null 表示放行默认换行；非 null 表示用其替换回车输入。
 * 规则：
 *  - 当前行是 `- ` / `1. ` / `> ` 等续行前缀且内容为空 → 清除该前缀（结束列表）。
 *  - 否则延续前缀；有序列表数字自增。
 */
internal fun continueOnEnter(text: String, selection: TextRange): MarkdownEditResult? {
    val cursor = selection.max.coerceIn(0, text.length)
    val lineStart = text.lastIndexOf('\n', startIndex = cursor).let { if (it < 0) 0 else it + 1 }
    val lineUpToCursor = text.substring(lineStart, cursor)
    val match = ContinuablePrefixRegex.matchEntire(lineUpToCursor) ?: return null
    val indent = match.groupValues[1]
    val marker = match.groupValues[2]
    val content = match.groupValues[5]
    // 空内容（仅前缀）→ 清除当前前缀，仅插入换行
    if (content.isBlank()) {
        val newText = text.substring(0, lineStart) + text.substring(cursor)
        return MarkdownEditResult(newText, TextRange(lineStart, lineStart))
    }
    val nextMarker = if (marker.trim().matches(Regex("""\d+[.)]"""))) {
        val trimmed = marker.trim()
        val num = trimmed.removeSuffix(".").removeSuffix(")").toIntOrNull()?.plus(1) ?: 1
        val sep = if (trimmed.endsWith(".")) "." else ")"
        "$num$sep "
    } else {
        marker
    }
    val insert = "\n$indent$nextMarker"
    val newText = text.substring(0, cursor) + insert + text.substring(cursor)
    val newCursor = cursor + insert.length
    return MarkdownEditResult(newText, TextRange(newCursor, newCursor))
}
// endregion
