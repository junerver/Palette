# Markdown 编辑器解耦与控制反转 — 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 markdown 编辑器 UI 从「逻辑 + UI 写死」重构为「核心纯逻辑（下沉 palette-markdown）+ 控制反转 hook + 渲染原子（public）+ 薄默认 UI 封装（三槽 + PSegmented）」三层架构，支持开箱即用 / 半定制 / 全定制三种使用姿势。

**Architecture:** 见 `docs/superpowers/specs/2026-07-01-markdown-editor-decoupling-design.md`。核心思路：①`MarkdownEditActions`/`MarkdownHistory` 迁到 `palette-markdown`，引入 Compose-free 的 `MarkdownSelection`；②`palette` 新增桥接层 + `useMarkdownEditorController` hook（仿 compose-hooks `useForm`）；③Viewer 内部 composable `private→public` 成为渲染原子；④`PMarkdownEditor` 重写为「控制器 + 三槽」，默认 mode-switch 从 `PToggleGroup` 迁到 `PSegmented`。

**Tech Stack:** Kotlin Multiplatform、Compose Multiplatform、compose-hooks（`useState`/`useRef`/`useCreation`/`useLatestState`）、kotlin.test。

**关键约束（来自 AGENTS.md）：**
- Windows 环境用 `.\gradlew.bat`，**禁止** `./gradlew`
- 状态管理优先用 `compose-hooks`（`useState`/`useRef` 等），非必要不用原生 `remember { mutableStateOf }`
- 组件逻辑改动必须先补/先写测试（TDD），提交前至少 `:palette:desktopTest` 必过
- 提交信息前缀：`feat:` / `fix:` / `refactor:` / `chore:` / `docs:`
- 默认样式从 `PaletteTheme` / `PaletteDefaults` / 组件级 token 派生

---

## 文件结构

### 新建文件

| 路径 | 职责 |
|---|---|
| `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActions.kt` | 核心层：纯函数编辑变换（迁移自 `palette`，`TextRange→MarkdownSelection`，`internal→public`） |
| `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistory.kt` | 核心层：撤销/重做栈（迁移自 `palette`，存值改 `Pair<String,MarkdownSelection>`，`internal→public`） |
| `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActionsTest.kt` | 核心层编辑变换测试（迁移自 `palette` 的 `MarkdownEditActionsLogicTest.kt`，断言改 `MarkdownSelection`） |
| `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistoryTest.kt` | 核心层撤销栈测试（迁移自 `palette` 的 `MarkdownHistoryLogicTest.kt`） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridge.kt` | 桥接层：`TextRange↔MarkdownSelection` / `TextFieldValue↔Pair<String,MarkdownSelection>` 互转（internal） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorController.kt` | 控制反转层：`useMarkdownEditorController` hook + `MarkdownEditorController` 类 + `MarkdownEditorScope` + `Modifier.markdownEditorKeyBindings` |

### 修改文件

| 路径 | 改动 |
|---|---|
| `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownModels.kt` | 末尾新增 `MarkdownSelection` / `MarkdownEditResult`（public，Compose-free） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditActions.kt` | **删除**（已迁到核心层） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownHistory.kt` | **删除**（已迁到核心层） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditor.kt` | 重写：`PMarkdownEditor` 签名精简（删 `mode`/`onModeChange`/`editLabel`/`previewLabel`/`splitLabel`，加三槽 + scope）；`PMarkdownEditorImpl` 改为调 `useMarkdownEditorController` + 默认实现用 `PSegmented`；`toggleTaskCheckbox` 保留（viewer 任务勾选用） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownViewer.kt` | `MarkdownBlock`/`MarkdownBlocks`/`InlineMarkdownText`/`toAnnotatedContent`/`DefaultInlineImage` 等 `private→public` |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownFormatToolbar.kt` | `MarkdownToolbarAction` / `MarkdownHeadingLevel` 保持 public（原本就 public，仅确认） |
| `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/Palette.kt` | 新增 markdown 导出 |
| `palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditActionsLogicTest.kt` | **删除**（已迁到核心层） |
| `palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownHistoryLogicTest.kt` | **删除**（已迁到核心层） |
| `palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridgeTest.kt` | **新建**：桥接互转 round-trip 测试 |
| `palette/src/desktopTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorControllerUiTest.kt` | **新建**：controller hook 的 Desktop UI 测试 |
| `app/src/commonMain/kotlin/xyz/junerver/compose/palette/demo/MarkdownDemo.kt` | 删除 `editLabel`/`previewLabel`/`splitLabel` 参数；新增「全定制编辑器」demo 节 |
| `docs-site/docs/features/markdown.md` | 扩充架构与自定义文档 |

---

## 实现顺序总览

任务分 8 个，按依赖顺序：

1. **Task 1**：核心层新增 `MarkdownSelection`/`MarkdownEditResult` 类型 + 测试（独立、最底层）
2. **Task 2**：核心层迁移 `MarkdownEditActions` + 迁移测试
3. **Task 3**：核心层迁移 `MarkdownHistory` + 迁移测试
4. **Task 4**：`palette` 桥接层 + 测试（依赖 Task 1-3）
5. **Task 5**：Viewer 渲染原子 `private→public`（独立于 Task 4，可并行）
6. **Task 6**：`useMarkdownEditorController` hook + controller（依赖 Task 4）
7. **Task 7**：`PMarkdownEditor` 重写（依赖 Task 6 + Task 5），含 PToggleGroup→PSegmented
8. **Task 8**：统一导出 + demo 迁移 + docs + 全量验证

---

## Task 1: 核心层新增 Compose-free 选区/编辑结果类型

**Files:**
- Modify: `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownModels.kt`（末尾追加）
- Test: `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownSelectionTest.kt`（新建）

- [ ] **Step 1: 写失败测试**

新建 `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownSelectionTest.kt`：

```kotlin
package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownSelectionTest {

    @Test
    fun length_isDifferenceBetweenEndAndStart() {
        assertEquals(5, MarkdownSelection(2, 7).length)
        assertEquals(0, MarkdownSelection(4, 4).length)
    }

    @Test
    fun empty_isZeroZero() {
        assertEquals(0, MarkdownSelection.Empty.start)
        assertEquals(0, MarkdownSelection.Empty.end)
    }

    @Test
    fun markdownEditResult_holdsTextAndSelection() {
        val r = MarkdownEditResult("new text", MarkdownSelection(0, 3))
        assertEquals("new text", r.text)
        assertEquals(MarkdownSelection(0, 3), r.selection)
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `.\gradlew.bat :palette-markdown:test --tests "*MarkdownSelectionTest*"`
Expected: 编译失败，`MarkdownSelection` / `MarkdownEditResult` 未定义。

- [ ] **Step 3: 写最小实现**

在 `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownModels.kt` **末尾**追加：

```kotlin
/**
 * Compose-free 选区：等价于 Compose 的 [androidx.compose.ui.text.TextRange]，但核心层
 * 不依赖 Compose。start<=end（collapsed 表示纯光标）。UI 层负责与 TextRange 互转。
 */
data class MarkdownSelection(
    val start: Int,
    val end: Int,
) {
    val min: Int get() = if (start <= end) start else end
    val max: Int get() = if (start <= end) end else start
    val length: Int get() = max - min

    companion object {
        val Empty = MarkdownSelection(0, 0)
    }
}

/**
 * 所有编辑变换的统一返回：新文本 + 新选区。
 * 核心层纯函数返回此类型，UI 层据此更新 TextFieldValue。
 */
data class MarkdownEditResult(
    val text: String,
    val selection: MarkdownSelection,
)
```

> 说明：`min`/`max` 与 Compose `TextRange.min`/`.max` 语义一致，便于桥接层直转。

- [ ] **Step 4: 运行测试验证通过**

Run: `.\gradlew.bat :palette-markdown:test --tests "*MarkdownSelectionTest*"`
Expected: 3 个测试全 PASS。

- [ ] **Step 5: 提交**

```bash
git add palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownModels.kt palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownSelectionTest.kt
git commit -m "feat(markdown): add Compose-free MarkdownSelection/EditResult types in core"
```

---

## Task 2: 核心层迁移 MarkdownEditActions（纯函数）

**Files:**
- Create: `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActions.kt`
- Create: `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActionsTest.kt`
- (Task 8 会删除 `palette` 模块的旧文件)

**迁移要点：**
- 包名 `xyz.junerver.compose.palette.markdown`（核心层）
- 删 `import androidx.compose.ui.text.TextRange`
- 所有 `TextRange` → `MarkdownSelection`
- 所有 `MarkdownEditResult` 引用核心层版本（同包）
- `internal` → `public`（顶层函数与 `defaultTableSnippet`/`defaultCodeFence`）

- [ ] **Step 1: 迁移测试（先写测试，断言改 MarkdownSelection）**

新建 `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActionsTest.kt`。从 `palette` 的 `MarkdownEditActionsLogicTest.kt`（343 行，45 个测试）迁移，改动规则：
- 包名改为 `xyz.junerver.compose.palette.markdown`
- 删 `import androidx.compose.ui.text.TextRange`
- 所有 `TextRange(a, b)` → `MarkdownSelection(a, b)`
- 其余断言（`r.text`、字符串比较）不变

迁移后的测试结构示例（前 3 个，其余同理逐个迁移全部 45 个）：

```kotlin
package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkdownEditActionsTest {

    // region wrapSelection
    @Test
    fun wrapSelection_bold_noSelection_insertsEmptyMarkerAndCaretInside() {
        val r = wrapSelection("hello", MarkdownSelection(0, 0), "**")
        assertEquals("****hello", r.text)
        assertEquals(MarkdownSelection(2, 2), r.selection)
    }

    @Test
    fun wrapSelection_bold_withSelection_wrapsAndCaretAfterContent() {
        val r = wrapSelection("hello world", MarkdownSelection(0, 5), "**")
        assertEquals("**hello** world", r.text)
        assertEquals(MarkdownSelection(0, 7), r.selection)
    }

    @Test
    fun wrapSelection_bold_alreadyWrapped_unwraps() {
        val r = wrapSelection("**hi**", MarkdownSelection(0, 6), "**")
        assertEquals("hi", r.text)
        assertEquals(MarkdownSelection(0, 2), r.selection)
    }
    // ... 迁移原文件剩余 42 个测试，逐个把 TextRange(a,b) 改成 MarkdownSelection(a,b)
    // 涉及的函数：wrapSelection / toggleLinePrefix / toggleTaskItem / setHeadingLevel
    //             / insertText / indent / continueOnEnter / defaultTableSnippet / defaultCodeFence
}
```

> 工程师注意：原 `MarkdownEditActionsLogicTest.kt` 有 45 个 `@Test`，全部迁移。除 `TextRange→MarkdownSelection` 外无其他逻辑改动。`setHeadingLevel(text, selection, level)` 的第三个参数 `level: Int` 保持不变（核心层不依赖 `MarkdownHeadingLevel` 枚举——该枚举是 UI 概念，留 palette 模块）。

- [ ] **Step 2: 运行测试验证失败**

Run: `.\gradlew.bat :palette-markdown:test --tests "*MarkdownEditActionsTest*"`
Expected: 编译失败，`wrapSelection` 等未定义。

- [ ] **Step 3: 迁移实现**

新建 `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActions.kt`。基于原 `palette` 版本（309 行），改动：
- 包名 → `package xyz.junerver.compose.palette.markdown`
- 删 `import androidx.compose.ui.text.TextRange`
- `internal data class MarkdownEditResult` → **删除**（用同包核心层的 public 版本，Task 1 已建）
- 所有函数 `internal` → `public`（`currentLineRange`/`selectedLineRanges`/`wrapSelection`/`toggleLinePrefix`/`toggleTaskItem`/`setHeadingLevel`/`insertText`/`indent`/`continueOnEnter`）
- `defaultTableSnippet` / `defaultCodeFence` 的 `internal` → `public`
- 所有签名与函数体内的 `TextRange` → `MarkdownSelection`
- `selection.min` / `selection.max` 用法不变（核心层 `MarkdownSelection` 已提供同名属性）

完整迁移后文件结构（关键签名）：

```kotlin
package xyz.junerver.compose.palette.markdown

// region 辅助：行范围
public fun currentLineRange(text: String, selection: MarkdownSelection): Pair<Int, Int> { ... }
public fun selectedLineRanges(text: String, selection: MarkdownSelection): List<Pair<Int, Int>> { ... }
// endregion

public fun wrapSelection(
    text: String,
    selection: MarkdownSelection,
    prefix: String,
    suffix: String = prefix,
): MarkdownEditResult { ... }

public fun toggleLinePrefix(
    text: String,
    selection: MarkdownSelection,
    prefix: String,
    ordered: Boolean = false,
): MarkdownEditResult { ... }

public fun toggleTaskItem(text: String, selection: MarkdownSelection): MarkdownEditResult { ... }

public fun setHeadingLevel(text: String, selection: MarkdownSelection, level: Int): MarkdownEditResult { ... }

public fun insertText(
    text: String,
    selection: MarkdownSelection,
    snippet: String,
    selectInside: IntRange? = null,
): MarkdownEditResult { ... }

public val defaultTableSnippet: String =
    "| Column A | Column B |\n| --- | --- |\n| cell | cell |"

public fun defaultCodeFence(language: String = ""): String = "```$language\n\n```\n"

public fun indent(text: String, selection: MarkdownSelection, forward: Boolean): MarkdownEditResult { ... }

public fun continueOnEnter(text: String, selection: MarkdownSelection): MarkdownEditResult? { ... }
```

> 工程师注意：函数体**逐行**从原文件复制，仅替换类型名 `TextRange`→`MarkdownSelection`、删除 `internal`/删除 data class 定义。`prefixLineFor`/`removeKnownListPrefix`/`ListLinePrefixRegex`/`TaskPrefixRegex`/`HeadingPrefixRegex`/`removeHeadingPrefix`/`INDENT_UNIT`/`ContinuablePrefixRegex` 这些 private 辅助原样保留。

- [ ] **Step 4: 运行测试验证通过**

Run: `.\gradlew.bat :palette-markdown:test --tests "*MarkdownEditActionsTest*"`
Expected: 45 个测试全 PASS。

- [ ] **Step 5: 提交**

```bash
git add palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActions.kt palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownEditActionsTest.kt
git commit -m "feat(markdown): migrate MarkdownEditActions to core module (public, MarkdownSelection)"
```

---

## Task 3: 核心层迁移 MarkdownHistory（撤销栈）

**Files:**
- Create: `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistory.kt`
- Create: `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistoryTest.kt`

**迁移要点：**
- 包名 `xyz.junerver.compose.palette.markdown`
- 撤销栈存值从 `TextFieldValue` 改为 `MarkdownHistoryEntry(text: String, selection: MarkdownSelection)`（纯数据）
- `internal` → `public`
- 删 `import androidx.compose.ui.text.input.TextFieldValue`

- [ ] **Step 1: 迁移测试**

新建 `palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistoryTest.kt`。从 `palette` 的 `MarkdownHistoryLogicTest.kt`（190 行，14 个测试）迁移，改动规则：
- 包名 → `xyz.junerver.compose.palette.markdown`
- 删 `import androidx.compose.ui.text.TextRange` / `TextFieldValue`
- helper `tf(text, caret)` 改为返回 `MarkdownHistoryEntry`：

```kotlin
package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class FakeClock(var t: Long = 0L, val step: Long = 0L) : () -> Long {
    override fun invoke(): Long {
        val current = t
        if (step > 0L) t += step
        return current
    }
    fun set(time: Long) { t = time }
}

class MarkdownHistoryTest {

    private fun entry(text: String, caret: Int = text.length) =
        MarkdownHistoryEntry(text, MarkdownSelection(caret, caret))

    private fun history(initial: String = "", clock: () -> Long = { 0L }) =
        MarkdownHistory(initial = entry(initial), coalesceMs = 500L, now = clock)

    @Test
    fun initial_state_cannotUndoOrRedo() {
        val h = history("hi")
        assertEquals("hi", h.current.text)
        assertFalse(h.canUndo)
        assertFalse(h.canRedo)
    }

    @Test
    fun commit_createsNewEntry_enablesUndo() {
        val h = history("a")
        h.commit(entry("b"))
        assertEquals("b", h.current.text)
        assertTrue(h.canUndo)
        assertFalse(h.canRedo)
    }
    // ... 迁移原文件剩余 12 个测试，tf()→entry()，断言用 h.current.text / h.current.selection
}
```

> 工程师注意：原 `MarkdownHistoryLogicTest.kt` 有 14 个 `@Test`，全部迁移。`FakeClock` 一并迁移。断言 `h.current.selection` 现在是 `MarkdownSelection` 类型，比较时用 `MarkdownSelection(a,b)`。

- [ ] **Step 2: 运行测试验证失败**

Run: `.\gradlew.bat :palette-markdown:test --tests "*MarkdownHistoryTest*"`
Expected: 编译失败，`MarkdownHistory`/`MarkdownHistoryEntry` 未定义。

- [ ] **Step 3: 迁移实现**

新建 `palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistory.kt`。改动：
- 包名 → `package xyz.junerver.compose.palette.markdown`
- 删 `import androidx.compose.ui.text.input.TextFieldValue`
- `internal class MarkdownHistory` → `public class MarkdownHistory`
- `Entry` 改名 `MarkdownHistoryEntry` 并提为 **public** data class（核心层没有 `TextFieldValue`，用纯数据承载文本+选区）：

```kotlin
package xyz.junerver.compose.palette.markdown

import kotlin.time.TimeSource

/** 撤销栈中的一个历史项（纯数据：文本 + 选区）。 */
public data class MarkdownHistoryEntry(
    val text: String,
    val selection: MarkdownSelection,
)

/**
 * 编辑器撤销 / 重做历史栈（纯逻辑、无 Compose 依赖）。
 *
 * 设计要点：
 * - 每次变更产生一个 [MarkdownHistoryEntry]。
 * - 连续"打字"[pushTyping] 会在阈值窗口内合并（Ctrl+Z 按词/段回退，而非逐字符）。
 * - 工具栏、快捷键等结构化操作走 [commit]，永远开启新的历史项。
 * - [undo]/[redo] 在 past/present/future 之间移动。
 * - 容量有上限，避免超长编辑会话内存膨胀。
 */
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

    /** 当前值（文本 + 选区）。 */
    public val current: MarkdownHistoryEntry get() = present.value

    public val canUndo: Boolean get() = past.isNotEmpty()
    public val canRedo: Boolean get() = future.isNotEmpty()

    public fun pushTyping(next: MarkdownHistoryEntry) {
        if (shouldCoalesce(present.value, next, present.time)) {
            if (!present.coalescing) pushPresentToPast()
            present = Entry(next, now(), coalescing = true)
        } else {
            commit(next)
        }
    }

    public fun commit(next: MarkdownHistoryEntry) {
        if (next.text == present.value.text && next.selection == present.value.selection) return
        pushPresentToPast()
        future.clear()
        present = Entry(next, now())
    }

    private fun pushPresentToPast() {
        past.addLast(present)
        if (past.size > capacity) past.removeFirst()
    }

    public fun sync(next: MarkdownHistoryEntry) {
        present = Entry(next, now())
    }

    public fun undo(): MarkdownHistoryEntry {
        if (!canUndo) return present.value
        future.addLast(present)
        present = past.removeLast()
        return present.value
    }

    public fun redo(): MarkdownHistoryEntry {
        if (!canRedo) return present.value
        past.addLast(present)
        present = future.removeLast()
        return present.value
    }

    public fun reset(value: MarkdownHistoryEntry) {
        past.clear()
        future.clear()
        present = Entry(value, now())
    }

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
            delta == 1 && n.startsWith(p) && nc == pc + 1 -> true
            delta > 1 && n.startsWith(p) && nc == pc + delta -> true
            delta == -1 && p.startsWith(n) && nc == pc - 1 -> true
            delta < -1 && p.startsWith(n) && nc == pc + delta -> true
            else -> false
        }
    }

    public companion object {
        public const val DEFAULT_CAPACITY = 500
        public const val DEFAULT_COALESCE_MS = 600L
    }
}

private val monotonicStart: TimeSource.Monotonic.ValueTimeMark by lazy { TimeSource.Monotonic.markNow() }

private fun defaultNow(): Long = monotonicStart.elapsedNow().inWholeMilliseconds
```

- [ ] **Step 4: 运行测试验证通过**

Run: `.\gradlew.bat :palette-markdown:test --tests "*MarkdownHistoryTest*"`
Expected: 14 个测试全 PASS。

- [ ] **Step 5: 全量核心层测试回归**

Run: `.\gradlew.bat :palette-markdown:test`
Expected: 全部 PASS（含原有的 parser/renderer/frontmatter/toc 测试 + 新增 3 个文件）。

- [ ] **Step 6: 提交**

```bash
git add palette-markdown/src/commonMain/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistory.kt palette-markdown/src/commonTest/kotlin/xyz/junerver/compose/palette/markdown/MarkdownHistoryTest.kt
git commit -m "feat(markdown): migrate MarkdownHistory to core module (MarkdownHistoryEntry, public)"
```

---

## Task 4: palette 桥接层

**Files:**
- Create: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridge.kt`
- Test: `palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridgeTest.kt`

**职责：** 在 `palette` 模块内提供 `TextRange`/`TextFieldValue` 与核心层 `MarkdownSelection`/`MarkdownHistoryEntry` 的互转，让 UI 层（controller、editor）能调用核心层纯函数，而核心层不沾 Compose。

- [ ] **Step 1: 写失败测试**

新建 `palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridgeTest.kt`：

```kotlin
package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.palette.markdown.MarkdownHistoryEntry
import xyz.junerver.compose.palette.markdown.MarkdownSelection
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownBridgeTest {

    @Test
    fun textRange_toMarkdownSelection_preservesStartEnd() {
        assertEquals(MarkdownSelection(2, 7), TextRange(2, 7).toMarkdownSelection())
        assertEquals(MarkdownSelection(5, 5), TextRange(5, 5).toMarkdownSelection())
    }

    @Test
    fun markdownSelection_toTextRange_preservesStartEnd() {
        assertEquals(TextRange(3, 9), MarkdownSelection(3, 9).toTextRange())
    }

    @Test
    fun roundTrip_textRange_isStable() {
        val original = TextRange(4, 10)
        val roundTrip = original.toMarkdownSelection().toTextRange()
        assertEquals(original, roundTrip)
    }

    @Test
    fun textFieldValue_toCoreEntry_preservesTextAndSelection() {
        val tfv = TextFieldValue("hello", TextRange(1, 3))
        val entry = tfv.toCoreEntry()
        assertEquals("hello", entry.text)
        assertEquals(MarkdownSelection(1, 3), entry.selection)
    }

    @Test
    fun coreEntry_toTextFieldValue_preservesTextAndSelection() {
        val entry = MarkdownHistoryEntry("world", MarkdownSelection(0, 2))
        val tfv = entry.toTextFieldValue()
        assertEquals("world", tfv.text)
        assertEquals(TextRange(0, 2), tfv.selection)
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `.\gradlew.bat :palette:testDebugUnitTest --tests "*MarkdownBridgeTest*"`
Expected: 编译失败，`toMarkdownSelection` 等未定义。

- [ ] **Step 3: 写实现**

新建 `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridge.kt`：

```kotlin
package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.palette.markdown.MarkdownHistoryEntry
import xyz.junerver.compose.palette.markdown.MarkdownSelection

/**
 * 桥接层：在 UI 层（依赖 Compose 的 TextFieldValue/TextRange）与核心层（Compose-free 的
 * MarkdownSelection/MarkdownHistoryEntry）之间互转。核心层因此保持零 Compose 依赖。
 */
internal fun TextRange.toMarkdownSelection(): MarkdownSelection = MarkdownSelection(start, end)

internal fun MarkdownSelection.toTextRange(): TextRange = TextRange(start, end)

internal fun TextFieldValue.toCoreEntry(): MarkdownHistoryEntry =
    MarkdownHistoryEntry(text, selection.toMarkdownSelection())

internal fun MarkdownHistoryEntry.toTextFieldValue(): TextFieldValue =
    TextFieldValue(text, selection.toTextRange())
```

- [ ] **Step 4: 运行测试验证通过**

Run: `.\gradlew.bat :palette:testDebugUnitTest --tests "*MarkdownBridgeTest*"`
Expected: 5 个测试全 PASS。

- [ ] **Step 5: 提交**

```bash
git add palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridge.kt palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownBridgeTest.kt
git commit -m "feat(markdown): add bridge layer for TextRange↔MarkdownSelection conversion"
```

---

## Task 5: Viewer 渲染原子 private → public

**Files:**
- Modify: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownViewer.kt`

**职责：** 把 `MarkdownBlock`/`MarkdownBlocks`/`InlineMarkdownText`/`DefaultInlineImage` 从 `private` 提为 `public`，作为可复用渲染原子。**函数体不变**，仅改可见性。`toAnnotatedContent` 作为 public 扩展保留。

> 注意：`MarkdownBlock`/`MarkdownBlocks` 当前依赖若干仍需 private 的辅助（`MarkdownListBlock`/`MarkdownTableRow`/`TaskCheckbox`/`toTextAlign`/`normalizedCellCount`/`MarkdownAnnotatedContent`/标签常量）。这些辅助**保持 private**——它们是渲染原子的内部实现细节，不对外暴露。仅把「用户组合渲染时要直接调用的入口」提为 public。

- [ ] **Step 1: 改可见性（无需测试改动，渲染逻辑不变，已有 MarkdownUiTest 覆盖回归）**

修改 `MarkdownViewer.kt`：

1. `MarkdownBlocks`（约 line 162）：`private fun MarkdownBlocks` → `fun MarkdownBlocks`
2. `MarkdownBlock`（约 line 205）：`private fun MarkdownBlock` → `fun MarkdownBlock`
3. `DefaultInlineImage`（约 line 453）：`private fun DefaultInlineImage` → `fun DefaultInlineImage`
4. `InlineMarkdownText`（约 line 521）：`private fun InlineMarkdownText` → `fun InlineMarkdownText`
5. `toAnnotatedContent`（约 line 567）：`private fun List<MarkdownInlineNode>.toAnnotatedContent` → `fun List<MarkdownInlineNode>.toAnnotatedContent`

每个 public composable 补 KDoc 说明其作为「渲染原子」的用途与参数。

`MarkdownBlocks` 的参数中，`taskCheckboxEnabled`/`nextTaskIndex`/`onHeadingPositioned` 等「内部协调」参数对自定义用户不友好——给它们保留默认值（已有），用户通常只传 `blocks` + 回调即可。

- [ ] **Step 2: 验证编译 + 既有 UI 测试回归**

Run: `.\gradlew.bat :palette:desktopTest --tests "*MarkdownUiTest*"`
Expected: PASS（渲染逻辑未变，仅可见性改动）。

- [ ] **Step 3: 提交**

```bash
git add palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownViewer.kt
git commit -m "feat(markdown): expose viewer render atoms (MarkdownBlock/Blocks/InlineMarkdownText) as public"
```

---

## Task 6: useMarkdownEditorController hook + controller

**Files:**
- Create: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorController.kt`
- Test: `palette/src/desktopTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorControllerUiTest.kt`

**职责：** 控制反转核心——`useMarkdownEditorController()` 返回 `MarkdownEditorController`（状态 + 操作函数），仿 compose-hooks `useForm`。键盘快捷键抽成 `Modifier.markdownEditorKeyBindings(controller)`。

- [ ] **Step 1: 写失败测试（Desktop UI 测试）**

新建 `palette/src/desktopTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorControllerUiTest.kt`：

```kotlin
package xyz.junerver.compose.palette.components.markdown

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

class MarkdownEditorControllerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun applyAction_bold_wrapsSelectionAndRecordsHistory() {
        var observed: String? = null
        rule.setContent {
            PaletteMaterialTheme {
                val controller = useMarkdownEditorController(
                    initialValue = androidx.compose.ui.text.input.TextFieldValue("hi"),
                )
                observed = controller.value.text
                // 触发加粗
                PButton(onClick = { controller.applyAction(MarkdownToolbarAction.Bold) }) {
                    Text("bold-btn")
                }
            }
        }
        rule.onNodeWithText("bold-btn").performClick()
        rule.waitForIdle()
        assertEquals("**hi**", observed)
    }

    @Test
    fun undo_afterBold_restoresOriginalText() {
        var observed: String? = null
        rule.setContent {
            PaletteMaterialTheme {
                val controller = useMarkdownEditorController(
                    initialValue = androidx.compose.ui.text.input.TextFieldValue("hi"),
                )
                observed = controller.value.text
                PButton(onClick = { controller.applyAction(MarkdownToolbarAction.Bold) }) { Text("do-bold") }
                PButton(onClick = { controller.undo() }) { Text("do-undo") }
            }
        }
        rule.onNodeWithText("do-bold").performClick()
        rule.waitForIdle()
        assertEquals("**hi**", observed)
        rule.onNodeWithText("do-undo").performClick()
        rule.waitForIdle()
        assertEquals("hi", observed)
    }

    @Test
    fun setMode_updatesControllerMode() {
        var modeObserved: MarkdownEditorMode? = null
        rule.setContent {
            PaletteMaterialTheme {
                val controller = useMarkdownEditorController(
                    initialMode = MarkdownEditorMode.Edit,
                )
                modeObserved = controller.mode
                PButton(onClick = { controller.setMode(MarkdownEditorMode.Preview) }) { Text("to-preview") }
            }
        }
        assertEquals(MarkdownEditorMode.Edit, modeObserved)
        rule.onNodeWithText("to-preview").performClick()
        rule.waitForIdle()
        assertEquals(MarkdownEditorMode.Preview, modeObserved)
    }
}
```

> 注意：若 `PButton` 的实际签名不同（参数名/类型），工程师需核对 `palette/src/commonMain/.../components/button/` 下的定义并调整测试中的调用。测试核心断言（`controller.value.text` 在 applyAction 后变化、undo 还原、setMode 生效）不变。

- [ ] **Step 2: 运行测试验证失败**

Run: `.\gradlew.bat :palette:desktopTest --tests "*MarkdownEditorControllerUiTest*"`
Expected: 编译失败，`useMarkdownEditorController` 未定义。

- [ ] **Step 3: 写实现**

新建 `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorController.kt`：

```kotlin
package xyz.junerver.compose.palette.components.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.hooks.useLatestState
import xyz.junerver.compose.hooks.useRef
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.markdown.MarkdownHistoryEntry
import xyz.junerver.compose.palette.markdown.continueOnEnter
import xyz.junerver.compose.palette.markdown.defaultCodeFence
import xyz.junerver.compose.palette.markdown.defaultTableSnippet
import xyz.junerver.compose.palette.markdown.indent
import xyz.junerver.compose.palette.markdown.insertText
import xyz.junerver.compose.palette.markdown.setHeadingLevel
import xyz.junerver.compose.palette.markdown.toggleLinePrefix
import xyz.junerver.compose.palette.markdown.toggleTaskItem
import xyz.junerver.compose.palette.markdown.wrapSelection

/**
 * 编辑器模式（保持原枚举，现 public）。
 */
public enum class MarkdownEditorMode {
    Edit,
    Preview,
    Split,
}

/**
 * 控制反转：编辑器的状态 + 操作函数对象（仿 compose-hooks useForm）。
 *
 * 由 [useMarkdownEditorController] 创建并注入。用户：
 * - 直接读 [value]/[mode]/[canUndo]/[canRedo] 绑定 UI；
 * - 调 [setValue]/[setText]/[setMode]/[applyAction]/[wrapSelection]/.../[undo]/[redo] 驱动状态；
 * - 默认 UI（PMarkdownEditor）与全定制 UI 都用它。
 *
 * 内部把 TextFieldValue 经桥接转为核心层 [MarkdownHistoryEntry]，调核心层纯函数，
 * 结果经桥接转回 TextFieldValue，并写入撤销栈。
 */
@Stable
public class MarkdownEditorController internal constructor(
    initial: TextFieldValue,
    initialMode: MarkdownEditorMode,
    historyLimit: Int,
) {
    private val history = xyz.junerver.compose.palette.markdown.MarkdownHistory(
        initial = initial.toCoreEntry(),
        capacity = historyLimit,
    )

    private val _value = useState(initial)
    internal val valueState get() = _value

    private val _mode = useState(initialMode)
    internal val modeState get() = _mode

    /** 当前文本 + 选区（唯一编辑状态源）。 */
    public val value: TextFieldValue get() = _value.value

    /** 当前模式。 */
    public val mode: MarkdownEditorMode get() = _mode.value

    public val canUndo: Boolean get() = history.canUndo
    public val canRedo: Boolean get() = history.canRedo

    /** 普通打字（来自 TextArea onValueChange）：走合并入栈。 */
    public fun setValue(next: TextFieldValue) {
        history.pushTyping(next.toCoreEntry())
        _value.setValue(history.current.toTextFieldValue())
    }

    /** 便捷：不关心选区时设置纯文本（光标置末尾）。 */
    public fun setText(text: String) {
        setValue(TextFieldValue(text, TextRange(text.length)))
    }

    public fun setMode(mode: MarkdownEditorMode) {
        _mode.setValue(mode)
    }

    /** 结构化提交（工具栏 / 快捷键内部用）：直接 commit 不走合并。 */
    internal fun commit(next: TextFieldValue) {
        history.commit(next.toCoreEntry())
        _value.setValue(history.current.toTextFieldValue())
    }

    /** 外部受控值同步：覆盖 present，不计入历史。 */
    internal fun sync(next: TextFieldValue) {
        history.sync(next.toCoreEntry())
        _value.setValue(next)
    }

    /** 粗粒度：按工具栏动作枚举执行（默认工具栏用）。 */
    public fun applyAction(action: MarkdownToolbarAction) {
        val tf = _value.value
        val result = when (action) {
            MarkdownToolbarAction.Bold -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "**")
            MarkdownToolbarAction.Italic -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "*")
            MarkdownToolbarAction.Strikethrough -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "~~")
            MarkdownToolbarAction.InlineCode -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "`")
            MarkdownToolbarAction.Heading -> setHeadingLevel(tf.text, tf.selection.toMarkdownSelection(), 1)
            MarkdownToolbarAction.UnorderedList -> toggleLinePrefix(tf.text, tf.selection.toMarkdownSelection(), "- ")
            MarkdownToolbarAction.OrderedList -> toggleLinePrefix(tf.text, tf.selection.toMarkdownSelection(), "1. ", ordered = true)
            MarkdownToolbarAction.TaskList -> toggleTaskItem(tf.text, tf.selection.toMarkdownSelection())
            MarkdownToolbarAction.Quote -> toggleLinePrefix(tf.text, tf.selection.toMarkdownSelection(), "> ")
            MarkdownToolbarAction.Link -> insertText(tf.text, tf.selection.toMarkdownSelection(), "[text](url)", selectInside = 7..9)
            MarkdownToolbarAction.Image -> insertText(tf.text, tf.selection.toMarkdownSelection(), "![alt](url)", selectInside = 8..10)
            MarkdownToolbarAction.CodeBlock -> insertText(tf.text, tf.selection.toMarkdownSelection(), defaultCodeFence())
            MarkdownToolbarAction.Table -> insertText(tf.text, tf.selection.toMarkdownSelection(), defaultTableSnippet)
            MarkdownToolbarAction.HorizontalRule -> insertText(tf.text, tf.selection.toMarkdownSelection(), "---\n")
            MarkdownToolbarAction.InlineLatex -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "\$")
            MarkdownToolbarAction.Subscript -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "~")
            MarkdownToolbarAction.Superscript -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "^")
            MarkdownToolbarAction.Highlight -> wrapSelection(tf.text, tf.selection.toMarkdownSelection(), "==")
        }
        commit(TextFieldValue(result.text, result.selection.toTextRange()))
    }

    /** 设置标题层级（细粒度）。 */
    public fun setHeadingLevel(level: MarkdownHeadingLevel) {
        val tf = _value.value
        val result = setHeadingLevel(tf.text, tf.selection.toMarkdownSelection(), level.level)
        commit(TextFieldValue(result.text, result.selection.toTextRange()))
    }

    // —— 细粒度操作（自定义 UI 按需调用，与 applyAction 共用底层纯函数）——
    public fun wrapSelection(prefix: String, suffix: String = prefix) {
        val tf = _value.value
        val r = wrapSelection(tf.text, tf.selection.toMarkdownSelection(), prefix, suffix)
        commit(TextFieldValue(r.text, r.selection.toTextRange()))
    }

    public fun toggleLinePrefix(prefix: String, ordered: Boolean = false) {
        val tf = _value.value
        val r = toggleLinePrefix(tf.text, tf.selection.toMarkdownSelection(), prefix, ordered)
        commit(TextFieldValue(r.text, r.selection.toTextRange()))
    }

    public fun toggleTaskItem() {
        val tf = _value.value
        val r = toggleTaskItem(tf.text, tf.selection.toMarkdownSelection())
        commit(TextFieldValue(r.text, r.selection.toTextRange()))
    }

    public fun insertText(snippet: String, selectInside: IntRange? = null) {
        val tf = _value.value
        val r = insertText(tf.text, tf.selection.toMarkdownSelection(), snippet, selectInside)
        commit(TextFieldValue(r.text, r.selection.toTextRange()))
    }

    public fun indent(forward: Boolean = true) {
        val tf = _value.value
        val r = indent(tf.text, tf.selection.toMarkdownSelection(), forward)
        commit(TextFieldValue(r.text, r.selection.toTextRange()))
    }

    public fun undo() {
        history.undo()
        _value.setValue(history.current.toTextFieldValue())
    }

    public fun redo() {
        history.redo()
        _value.setValue(history.current.toTextFieldValue())
    }
}

/**
 * 控制反转 hook：返回 [MarkdownEditorController]（状态 + 操作函数）。
 * UI 编排权交给调用方。仿 compose-hooks useForm。
 *
 * @param initialValue 初始文本+选区（编辑器场景天然需选区）
 * @param initialMode 初始模式，默认 Split
 * @param historyLimit 撤销栈容量上限
 */
@Composable
public fun useMarkdownEditorController(
    initialValue: TextFieldValue = TextFieldValue(""),
    initialMode: MarkdownEditorMode = MarkdownEditorMode.Split,
    historyLimit: Int = xyz.junerver.compose.palette.markdown.MarkdownHistory.DEFAULT_CAPACITY,
): MarkdownEditorController {
    // 用 useRef 持有 controller 实例（跨重组保持），内部用 useState 管理可观察状态。
    val ref = useRef { MarkdownEditorController(initialValue, initialMode, historyLimit) }
    // 读取最新值以便 controller 内部 setValue 驱动重组（useState 已在 controller 内部生效）
    ref.current.valueState.value // 触发重组订阅
    ref.current.modeState.value
    return ref.current
}

/**
 * Markdown 编辑器键盘快捷键（Ctrl/Cmd+B 加粗、+Z 撤销、Tab 缩进、Enter 续行等）。
 * 抽成 Modifier 扩展供自定义 UI 复用。
 */
public fun Modifier.markdownEditorKeyBindings(controller: MarkdownEditorController): Modifier =
    this.onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val primaryMod = event.isCtrlPressed || event.isMetaPressed
        if (primaryMod) {
            val handled = when (event.key) {
                Key.B -> { controller.applyAction(MarkdownToolbarAction.Bold); true }
                Key.I -> { controller.applyAction(MarkdownToolbarAction.Italic); true }
                Key.K -> {
                    if (event.isShiftPressed) controller.applyAction(MarkdownToolbarAction.Strikethrough)
                    else controller.applyAction(MarkdownToolbarAction.Link)
                    true
                }
                Key.E -> {
                    if (event.isShiftPressed) controller.applyAction(MarkdownToolbarAction.CodeBlock)
                    else controller.applyAction(MarkdownToolbarAction.InlineCode)
                    true
                }
                Key.U -> { controller.applyAction(MarkdownToolbarAction.UnorderedList); true }
                Key.O -> {
                    if (event.isShiftPressed) controller.applyAction(MarkdownToolbarAction.Quote)
                    else controller.applyAction(MarkdownToolbarAction.OrderedList)
                    true
                }
                Key.Z -> {
                    if (event.isShiftPressed) controller.redo() else controller.undo()
                    true
                }
                Key.Y -> { controller.redo(); true }
                else -> false
            }
            return@onPreviewKeyEvent handled
        }
        when (event.key) {
            Key.Tab -> { controller.indent(forward = !event.isShiftPressed); true }
            Key.Enter -> {
                val tf = controller.value
                val continued = continueOnEnter(tf.text, tf.selection.toMarkdownSelection())
                if (continued != null) {
                    controller.commit(TextFieldValue(continued.text, continued.selection.toTextRange()))
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }
```

> 工程师注意：
> - `MarkdownEditorMode` 枚举从 `MarkdownEditor.kt` 移到本文件（原位置删除，避免重复定义）。Task 7 处理 editor 时会移除旧定义。
> - `MarkdownToolbarAction`/`MarkdownHeadingLevel` 仍在 `MarkdownFormatToolbar.kt`（已是 public），这里直接引用。
> - `useState` 返回的 setter 命名按 compose-hooks 实际 API（`_value.value` 读、`_value.setValue(...)` 写——若实际 API 是解构 `val (v, setV) = useState(...)`，工程师需调整为内部持 `mutableStateOf` 桥接，或重构 controller 用解构）。**实现时核对 `compose-hooks` 的 `useState` 返回类型并适配。**
> - `useMarkdownEditorController` 内 `useRef { ... }`：核对 hooks 库 `useRef` 是否接受 factory lambda；若不接受，改为 `useRef(MarkdownEditorController(...))`（首次求值，之后不变）。

- [ ] **Step 4: 运行测试验证通过**

Run: `.\gradlew.bat :palette:desktopTest --tests "*MarkdownEditorControllerUiTest*"`
Expected: 3 个测试全 PASS。若 `useState`/`useRef` API 适配有问题，先修正 controller 内部状态持有方式再跑。

- [ ] **Step 5: 提交**

```bash
git add palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorController.kt palette/src/desktopTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditorControllerUiTest.kt
git commit -m "feat(markdown): add useMarkdownEditorController hook + IoC controller + key bindings"
```

---

## Task 7: PMarkdownEditor 重写（控制器 + 三槽 + PSegmented）

**Files:**
- Modify: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditor.kt`（重写）
- Delete: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditActions.kt`（已迁核心层）
- Delete: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownHistory.kt`（已迁核心层）

**职责：** `PMarkdownEditor` 重写为「调 `useMarkdownEditorController` + 默认三槽实现」，默认 mode-switch 从 `PToggleGroup` 改为 `PSegmented`。删除已迁移的旧逻辑文件。

- [ ] **Step 1: 删除已迁移的旧文件**

```bash
rm palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditActions.kt
rm palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownHistory.kt
rm palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditActionsLogicTest.kt
rm palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownHistoryLogicTest.kt
```

> 同时删除 palette 模块里旧的 logic 测试（已迁到核心层，避免重复 + 引用已删的 `internal` 符号）。

- [ ] **Step 2: 重写 MarkdownEditor.kt**

完整替换 `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/MarkdownEditor.kt`：

```kotlin
package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.components.segmented.PSegmented
import xyz.junerver.compose.palette.components.segmented.SegmentedOption
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.core.i18n.LocalPaletteStrings
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.markdown.MarkdownParser
import xyz.junerver.compose.palette.markdown.MarkdownRenderer

/**
 * 编辑器槽位的 receiver scope：让自定义槽位拿到 [controller] 与 label，无需自己管状态。
 */
@Stable  // 注：Stable import 在 controller 文件已加，这里若重复 import 去重
public class MarkdownEditorScope(
    public val controller: MarkdownEditorController,
    public val editLabel: String,
    public val previewLabel: String,
    public val splitLabel: String,
    public val placeholder: String,
    public val enabled: Boolean,
)

/**
 * 字符串版编辑器（开箱即用，受控便捷封装）。
 *
 * 默认 UI = 控制器 + PSegmented mode-switch + MarkdownFormatToolbar + TextArea + PMarkdownViewer。
 * 传入任一槽位（[modeSwitch]/[toolbar]/[preview]）即接管该层，槽内通过 [MarkdownEditorScope.controller] 操作。
 *
 * @param modeSwitch 自定义模式切换；默认 null → 内置 PSegmented
 * @param toolbar 自定义工具栏；默认 null → 内置 MarkdownFormatToolbar（showFormatToolbar=true 时）
 * @param preview 自定义预览；默认 null → 内置 PMarkdownViewer
 */
@Composable
public fun PMarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    showPreview: Boolean = true,
    showFormatToolbar: Boolean = true,
    modeSwitch: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    toolbar: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    preview: (@Composable MarkdownEditorScope.() -> Unit)? = null,
) {
    PMarkdownEditorImpl(
        value = value,
        onValueChange = onValueChange,
        tfValue = null,
        onTfValueChange = null,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        showPreview = showPreview,
        showFormatToolbar = showFormatToolbar,
        modeSwitch = modeSwitch,
        toolbar = toolbar,
        preview = preview,
    )
}

/**
 * [TextFieldValue] 版：暴露完整光标/选区（如需精确光标控制）。
 * 注意：重载无法通过 Palette.kt 函数引用再导出，需直接 import 本包。
 */
@Composable
public fun PMarkdownEditorValue(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    showPreview: Boolean = true,
    showFormatToolbar: Boolean = true,
    modeSwitch: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    toolbar: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    preview: (@Composable MarkdownEditorScope.() -> Unit)? = null,
) {
    PMarkdownEditorImpl(
        value = value.text,
        onValueChange = null,
        tfValue = value,
        onTfValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        showPreview = showPreview,
        showFormatToolbar = showFormatToolbar,
        modeSwitch = modeSwitch,
        toolbar = toolbar,
        preview = preview,
    )
}

@Composable
private fun PMarkdownEditorImpl(
    value: String,
    onValueChange: ((String) -> Unit)?,
    tfValue: TextFieldValue?,
    onTfValueChange: ((TextFieldValue) -> Unit)?,
    modifier: Modifier,
    placeholder: String,
    enabled: Boolean,
    showPreview: Boolean,
    showFormatToolbar: Boolean,
    modeSwitch: (@Composable MarkdownEditorScope.() -> Unit)?,
    toolbar: (@Composable MarkdownEditorScope.() -> Unit)?,
    preview: (@Composable MarkdownEditorScope.() -> Unit)?,
) {
    val initial = tfValue ?: TextFieldValue(value, selection = TextRange(value.length))
    val controller = useMarkdownEditorController(
        initialValue = initial,
        initialMode = if (showPreview) MarkdownEditorMode.Split else MarkdownEditorMode.Edit,
    )

    // label 默认值来自 i18n（若 PaletteStrings 无对应 key，工程师补 key 或用硬编码兜底）
    val strings = LocalPaletteStrings.current
    val scope = remember(controller) {
        MarkdownEditorScope(
            controller = controller,
            editLabel = strings.markdownEditLabel,
            previewLabel = strings.markdownPreviewLabel,
            splitLabel = strings.markdownSplitLabel,
            placeholder = placeholder,
            enabled = enabled,
        )
    }

    // 受控同步：外部 value 变化时写入 controller（不计入历史）
    val externalText = tfValue?.text ?: value
    val externalSelection = tfValue?.selection
    LaunchedEffect(externalText, externalSelection) {
        val current = controller.value
        val textChanged = externalText != current.text
        val selChanged = externalSelection != null && externalSelection != current.selection
        if (textChanged || selChanged) {
            val cursor = (externalSelection ?: current.selection)
                .let { TextRange(it.start.coerceIn(0, externalText.length), it.end.coerceIn(0, externalText.length)) }
            controller.sync(TextFieldValue(externalText, cursor, composition = null))
        }
    }

    // 控制器变更回写外部
    LaunchedEffect(controller) {
        snapshotFlow { controller.value }
            .drop(1)
            .collect { tfv ->
                onTfValueChange?.invoke(tfv)
                onValueChange?.invoke(tfv.text)
            }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        if (showPreview) {
            (modeSwitch ?: { DefaultModeSwitch() }).invoke(scope)
        }

        when (controller.mode) {
            MarkdownEditorMode.Edit -> {
                if (showFormatToolbar && enabled) {
                    (toolbar ?: { DefaultToolbar() }).invoke(scope)
                }
                MarkdownEditorTextArea(
                    value = controller.value,
                    onValueChange = controller::setValue,
                    placeholder = placeholder,
                    enabled = enabled,
                    modifier = Modifier.markdownEditorKeyBindings(controller),
                )
            }

            MarkdownEditorMode.Preview ->
                (preview ?: { DefaultPreview() }).invoke(scope)

            MarkdownEditorMode.Split -> {
                if (showFormatToolbar && enabled) {
                    (toolbar ?: { DefaultToolbar() }).invoke(scope)
                }
                MarkdownEditorTextArea(
                    value = controller.value,
                    onValueChange = controller::setValue,
                    placeholder = placeholder,
                    enabled = enabled,
                    modifier = Modifier.markdownEditorKeyBindings(controller),
                )
                (preview ?: { DefaultPreview() }).invoke(scope)
            }
        }
    }
}

// —— 默认槽位实现（用户不传槽位时用）——

@Composable
private fun MarkdownEditorScope.DefaultModeSwitch() {
    PSegmented(
        options = listOf(
            SegmentedOption(value = MarkdownEditorMode.Edit.name, label = editLabel),
            SegmentedOption(value = MarkdownEditorMode.Preview.name, label = previewLabel),
            SegmentedOption(value = MarkdownEditorMode.Split.name, label = splitLabel),
        ),
        value = controller.mode.name,
        onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        size = ComponentSize.Small,
    )
}

@Composable
private fun MarkdownEditorScope.DefaultToolbar() {
    MarkdownFormatToolbar(
        onAction = controller::applyAction,
        enabled = enabled,
        onHeadingLevel = controller::setHeadingLevel,
    )
}

@Composable
private fun MarkdownEditorScope.DefaultPreview() {
    val model = useCreation(controller.value.text) {
        MarkdownRenderer.toRenderModel(MarkdownParser.parse(controller.value.text))
    }.current
    MarkdownBlocks(model.blocks)
}

// —— 私有辅助 —— 

@Composable
private fun MarkdownEditorTextArea(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    TextArea(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        size = ComponentSize.Medium,
        placeholder = placeholder,
        minLines = 8,
        maxLines = 18,
        modifier = modifier.defaultMinSize(minHeight = MarkdownDefaults.editorMinHeight()),
    )
}

/** 预览态下勾选任务复选框：仍保留为内部工具（viewer 任务勾选回写文本）。 */
internal fun toggleTaskCheckbox(markdown: String, taskIndex: Int): String {
    val taskRegex = Regex("""^(\s*[-*+])\s+\[([ xX])]\s+(.*)$""")
    val lines = markdown.lines()
    var currentTaskIndex = 0
    val newLines = lines.map { line ->
        val match = taskRegex.matchEntire(line)
        if (match != null && currentTaskIndex++ == taskIndex) {
            val marker = match.groupValues[1]
            val currentMark = match.groupValues[2]
            val text = match.groupValues[3]
            val newMark = if (currentMark.equals("x", ignoreCase = true)) " " else "x"
            "$marker [$newMark] $text"
        } else {
            line
        }
    }
    return newLines.joinToString("\n")
}
```

> 工程师注意（实现期需核对/补全的点）：
> 1. **`MarkdownEditorMode` 枚举**：Task 6 已在 `MarkdownEditorController.kt` 定义为 public。本文件**不要重复定义**——删除原 `MarkdownEditor.kt:27-31` 的 enum。确认 Task 6 文件已定义。
> 2. **i18n key**：`strings.markdownEditLabel`/`markdownPreviewLabel`/`markdownSplitLabel` —— 需在 `PaletteStrings` 补这 3 个 key（中英两套）。若 `LocalPaletteStrings` 的实际 API 名不同（可能是 `PaletteStrings` / `LocalPaletteStrings.current`），核对 `core/i18n/` 下定义并适配。**这是必须补的，否则编译失败。**
> 3. **`snapshotFlow` import**：需 `import androidx.compose.runtime.snapshotFlow` 和 `import kotlinx.coroutines.flow.drop`。
> 4. **`remember` import**：需 `import androidx.compose.runtime.remember`。
> 5. **`Stable` import**：`MarkdownEditorController.kt` 已 import `androidx.compose.runtime.Stable`；若 scope 类在同一文件或另文件定义，确保 import。
> 6. **`useCreation` 签名**：核对 `useCreation(markdown) { ... }` 的参数顺序——可能是 `useCreation(key1, key2) { ... }` 或 `useCreation { ... }`。从 `MarkdownViewer.kt:113` 现有用法 `useCreation(renderModel, markdown) { ... }.current` 推断 API，按实际适配。
> 7. **回写 `snapshotFlow` 死循环风险**：`controller.value` 变化 → 回写外部 → 外部 `value` 变化 → `LaunchedEffect(externalText)` 调 `controller.sync` → 又触发 snapshotFlow。`sync` 会 `_value.setValue`。**需用标志位或比较防环**：在 sync 前记录 lastSynced，回写前比较是否来自外部。若复杂，可简化为：回写只在 `controller` 主动操作（applyAction/undo）后触发，打字同步不回写（外部 value 已是源头）。**实现期务必验证无无限循环。**

- [ ] **Step 3: 编译验证（先确保编译通过，再跑测试）**

Run: `.\gradlew.bat :palette:compileDebugKotlin` （或 `:palette:compileKotlinJvm`）
Expected: 编译通过。逐个修正上述注意点（i18n key、imports、API 适配）。

- [ ] **Step 4: 跑既有 markdown UI 测试回归**

Run: `.\gradlew.bat :palette:desktopTest --tests "*MarkdownUiTest*"`
Expected: PASS。若 `MarkdownUiTest` 引用了被删的旧 API（如旧 `PMarkdownEditor` 的 `mode` 参数），同步更新该测试。

- [ ] **Step 5: 跑 controller UI 测试（Task 6）确保未回归**

Run: `.\gradlew.bat :palette:desktopTest --tests "*MarkdownEditorControllerUiTest*"`
Expected: PASS。

- [ ] **Step 6: 提交**

```bash
git add -A palette/src/commonMain/kotlin/xyz/junerver/compose/palette/components/markdown/ palette/src/commonTest/kotlin/xyz/junerver/compose/palette/components/markdown/ palette/src/desktopTest/ palette/src/commonMain/kotlin/xyz/junerver/compose/palette/core/i18n/
git commit -m "refactor(markdown): rewrite PMarkdownEditor with controller + 3 slots + PSegmented; remove migrated files"
```

---

## Task 8: 统一导出 + demo 迁移 + docs + 全量验证

**Files:**
- Modify: `palette/src/commonMain/kotlin/xyz/junerver/compose/palette/Palette.kt`
- Modify: `app/src/commonMain/kotlin/xyz/junerver/compose/palette/demo/MarkdownDemo.kt`
- Modify: `docs-site/docs/features/markdown.md`

### Step 1: 更新 Palette.kt 导出

- [ ] **在 import 区（约 line 218 后）新增：**

```kotlin
import xyz.junerver.compose.palette.components.markdown.MarkdownEditorController as MarkdownEditorControllerImpl
import xyz.junerver.compose.palette.components.markdown.MarkdownEditorScope as MarkdownEditorScopeImpl
import xyz.junerver.compose.palette.components.markdown.useMarkdownEditorController as useMarkdownEditorControllerImpl
import xyz.junerver.compose.palette.components.markdown.DefaultInlineImage as DefaultInlineImageImpl
import xyz.junerver.compose.palette.components.markdown.MarkdownBlock as MarkdownBlockImpl
import xyz.junerver.compose.palette.components.markdown.MarkdownBlocks as MarkdownBlocksImpl
import xyz.junerver.compose.palette.markdown.MarkdownEditResult as MarkdownEditResultImpl
import xyz.junerver.compose.palette.markdown.MarkdownSelection as MarkdownSelectionImpl
```

> 注意：现有 import 已有 `MarkdownEditorMode as MarkdownEditorModeImpl`（指向旧 `components.markdown.MarkdownEditorMode`）和 `MarkdownToolbarAction as MarkdownToolbarActionImpl`。`MarkdownEditorMode` 现从 `MarkdownEditorController.kt` 导出（同包 `components.markdown`），import 路径不变。`MarkdownToolbarAction` 仍在 `MarkdownFormatToolbar.kt`，路径不变。

- [ ] **在导出区（约 line 934 后，barcode 之前）新增：**

```kotlin
// 控制反转层
val useMarkdownEditorController = ::useMarkdownEditorControllerImpl
typealias MarkdownEditorController = MarkdownEditorControllerImpl
typealias MarkdownEditorScope = MarkdownEditorScopeImpl
// 渲染原子
val MarkdownBlock = ::MarkdownBlockImpl
val MarkdownBlocks = ::MarkdownBlocksImpl
val DefaultInlineImage = ::DefaultInlineImageImpl
// 核心层类型（已在 palette-markdown public）
typealias MarkdownSelection = MarkdownSelectionImpl
typealias MarkdownEditResult = MarkdownEditResultImpl
```

> 扩展函数（`toAnnotatedContent`、`markdownEditorKeyBindings`）无法用 `::` 导出，KDoc 注明「直接 import `xyz.junerver.compose.palette.components.markdown`」。

- [ ] **验证导出编译**

Run: `.\gradlew.bat :palette:compileKotlinJvm`
Expected: PASS。

### Step 2: 迁移 demo

- [ ] **修改 `app/src/commonMain/kotlin/xyz/junerver/compose/palette/demo/MarkdownDemo.kt`：**

1. **line 53-69 的 `PMarkdownEditor` 调用**：删除 `editLabel`/`previewLabel`/`splitLabel` 三个参数（已进 scope，默认 mode-switch 从 i18n 取）：

```kotlin
DemoSection(title = text.editorTitle) {
    PMarkdownEditor(
        value = editorValue,
        onValueChange = setEditorValue,
        placeholder = text.editorPlaceholder,
        showPreview = true,
        showFormatToolbar = true,
    )
}
```

2. **新增「全定制编辑器」demo 节**（在 editor demo 之后，line 70 的 `}` 之前 或 之后追加新 `DemoSection`）：

```kotlin
Spacer(modifier = Modifier.height(24.dp))

DemoSection(title = text.customEditorTitle) {
    // 全定制：调 useMarkdownEditorController + MarkdownBlocks 原子自拼 UI
    val (customValue, setCustomValue) = useState(text.editorMarkdown)
    CustomMarkdownEditorDemo(
        value = customValue,
        onValueChange = setCustomValue,
        editLabel = text.editLabel,
        previewLabel = text.previewLabel,
        splitLabel = text.splitLabel,
    )
}
```

并在文件末尾新增 `CustomMarkdownEditorDemo` composable（演示控制反转契约）：

```kotlin
@Composable
private fun CustomMarkdownEditorDemo(
    value: String,
    onValueChange: (String) -> Unit,
    editLabel: String,
    previewLabel: String,
    splitLabel: String,
) {
    val controller = useMarkdownEditorController(
        initialValue = androidx.compose.ui.text.input.TextFieldValue(value),
    )
    androidx.compose.foundation.layout.Column {
        // 自定义 mode-switch（用 PSegmented）
        PSegmented(
            options = listOf(
                SegmentedOption(MarkdownEditorMode.Edit.name, editLabel),
                SegmentedOption(MarkdownEditorMode.Preview.name, previewLabel),
                SegmentedOption(MarkdownEditorMode.Split.name, splitLabel),
            ),
            value = controller.mode.name,
            onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        )
        // 自定义工具栏（调控制器操作函数）
        androidx.compose.foundation.layout.Row {
            androidx.compose.material3.TextButton(onClick = { controller.applyAction(MarkdownToolbarAction.Bold) }) {
                androidx.compose.material3.Text("B")
            }
            androidx.compose.material3.TextButton(onClick = { controller.applyAction(MarkdownToolbarAction.Italic) }) {
                androidx.compose.material3.Text("I")
            }
        }
        when (controller.mode) {
            MarkdownEditorMode.Edit, MarkdownEditorMode.Split -> {
                androidx.compose.material3.OutlinedTextField(
                    value = controller.value,
                    onValueChange = controller::setValue,
                    modifier = Modifier.markdownEditorKeyBindings(controller),
                )
            }
            MarkdownEditorMode.Preview -> {}
        }
        if (controller.mode != MarkdownEditorMode.Edit) {
            val model = useCreation(controller.value.text) {
                MarkdownRenderer.toRenderModel(MarkdownParser.parse(controller.value.text))
            }.current
            MarkdownBlocks(model.blocks)
        }
    }
}
```

3. **补 demo i18n**：`MarkdownDemoText`（line 382-393）新增字段 `customEditorTitle: String`，两种语言分别赋值（"全定制编辑器" / "Custom Editor"）。保留 `editLabel`/`previewLabel`/`splitLabel`（全定制 demo 用）。

> 工程师注意：demo 需新增 import：`useMarkdownEditorController`、`MarkdownEditorMode`、`PSegmented`、`SegmentedOption`、`MarkdownToolbarAction`、`Modifier.markdownEditorKeyBindings`、`useCreation`、`MarkdownRenderer`、`MarkdownParser`、`MarkdownBlocks`。从 `xyz.junerver.compose.palette` 统一导出或直接 import 包路径。

- [ ] **验证 demo 编译**

Run: `.\gradlew.bat :app:compileKotlinJvm` （或 `:app:compileDebugKotlin`）
Expected: PASS。

### Step 3: 扩充 docs

- [ ] **修改 `docs-site/docs/features/markdown.md`**：扩充架构、三槽定制、渲染原子章节（按设计文档 §3-5 内容改写为用户向文档）。至少包含：
  - 「架构」小节：三层架构图 + 三种使用姿势（开箱即用 / 半定制 / 全定制）
  - 编辑器章节：`PMarkdownEditor` 三槽（modeSwitch/toolbar/preview）+ scope controller 用法
  - 「自定义渲染」小节：`MarkdownBlock`/`MarkdownBlocks`/`toAnnotatedContent`/`useMarkdownEditorController` 原子/hook 用法 + 全定制示例代码

### Step 4: 全量验证

- [ ] **核心层全测试**

Run: `.\gradlew.bat :palette-markdown:test`
Expected: 全 PASS。

- [ ] **palette 全测试（含 UI + 桥接）**

Run: `.\gradlew.bat :palette:desktopTest` 和 `.\gradlew.bat :palette:testDebugUnitTest`
Expected: 全 PASS（含 Task 1-7 新增测试 + 既有 viewer/toc/ui 测试）。

- [ ] **palette 全量构建（确保无编译遗漏）**

Run: `.\gradlew.bat :palette:build`
Expected: BUILD SUCCESSFUL。

- [ ] **demo 构建**

Run: `.\gradlew.bat :app:compileKotlinJvm`
Expected: PASS。

- [ ] **提交**

```bash
git add palette/src/commonMain/kotlin/xyz/junerver/compose/palette/Palette.kt app/src/commonMain/kotlin/xyz/junerver/compose/palette/demo/MarkdownDemo.kt docs-site/docs/features/markdown.md
git commit -m "feat(markdown): unify exports, migrate demo to IoC editor + custom demo, update docs"
```

---

## 自检（writing-plans 规定项）

**1. Spec coverage（对照设计文档各节）:**
- §5.1 核心层 MarkdownSelection/EditResult → Task 1 ✓
- §5.1.2 MarkdownEditActions 迁移 → Task 2 ✓
- §5.1.2 MarkdownHistory 迁移（MarkdownHistoryEntry）→ Task 3 ✓
- §5.2 桥接层 → Task 4 ✓
- §5.3 useMarkdownEditorController + controller + key bindings → Task 6 ✓
- §5.4 渲染原子 private→public → Task 5 ✓
- §5.5.1 PMarkdownViewer 重写 → **Task 5 含可见性改动；薄封装重写在 Task 7 DefaultPreview 用 MarkdownBlocks**（PMarkdownViewer 自身的内部重写作为既有逻辑保留，因 viewer 内部本就调 MarkdownBlocks，可见性改动即够）✓
- §5.5.2 PMarkdownEditor 三槽 + scope → Task 7 ✓
- §5.6 PToggleGroup→PSegmented → Task 7 DefaultModeSwitch ✓
- §6 统一导出 → Task 8 ✓
- §7 demo + docs 迁移 → Task 8 ✓
- §10 测试策略 → Task 1-7 各有测试 ✓

**2. Placeholder scan:**
- 无 "TBD/TODO/implement later"
- Task 7 Step 2 有「注意点」列出实现期需核对项（i18n key、API 适配），这是必要的实现期核对清单，非占位符——但已给出具体核对方法和兜底。
- demo 自定义示例代码完整，无占位。

**3. Type consistency:**
- `MarkdownSelection(start, end)` + `.min`/`.max`/`.length` + `.Empty`：Task 1 定义，Task 2/3/4/6 一致使用 ✓
- `MarkdownEditResult(text, selection)`：Task 1 定义，Task 2 一致 ✓
- `MarkdownHistoryEntry(text, selection)`：Task 3 定义，Task 4/6 一致 ✓
- `MarkdownEditorMode.{Edit,Preview,Split}`：Task 6 定义，Task 7/8/demo 一致 ✓
- `useMarkdownEditorController(initialValue, initialMode, historyLimit)`：Task 6 定义，Task 7 一致 ✓
- `MarkdownEditorScope(controller, editLabel, previewLabel, splitLabel, placeholder, enabled)`：Task 7 定义 ✓
- 桥接函数 `toMarkdownSelection`/`toTextRange`/`toCoreEntry`/`toTextFieldValue`：Task 4 定义，Task 6/7 一致 ✓

**实现期已知风险（已在对应 Task 标注）：**
- Task 6/7：`compose-hooks` 的 `useState`/`useRef` 返回类型需适配
- Task 7：受控同步的回写防环
- Task 7/8：i18n key 需补全
