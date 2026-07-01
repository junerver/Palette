# Markdown 编辑器解耦与控制反转设计

- **日期**: 2026-07-01
- **状态**: 设计已评审通过，待实现
- **范围**: `palette-markdown` 模块（纯逻辑下沉）+ `palette` 模块 `components/markdown/`（控制反转 + 渲染原子 + 薄默认 UI + PSegmented 迁移）
- **Breaking change**: 是（已获授权，允许破坏性变更，demo/docs 同步迁移）

---

## 1. 背景与动机

### 1.1 起因

Markdown 示例 demo 中编辑器的「编辑/预览/分屏」切换按钮当前使用 `PToggleGroup`（`MarkdownEditor.kt:302-317`），用 `listOf(currentMode.name)` + `selected.firstOrNull()` 的 hack 把多选 List 硬凑成单选——这是用错组件的信号。该场景语义上是「N 个互斥视图模式」，正确组件应是 `PSegmented`（分段控制器，天生单选 + 内置滑块动效）。

### 1.2 更深层问题

在核对组件用法时发现 `PMarkdownEditor` 把**全部 UI 写死在一个 `Column` 里**，零个 `@Composable` 槽位：

- mode-switch（`PToggleGroup`）、`MarkdownFormatToolbar`、`MarkdownEditorTextArea`、`PMarkdownViewer` 全部硬编码
- 编辑器纯逻辑（`MarkdownEditActions`、`MarkdownHistory`）已是纯函数，但标 `internal`，外部够不着
- Viewer 块渲染分发（`MarkdownBlock` 的 `when` + `toAnnotatedContent`）全是 `private`，无法定制

这剥夺了组件库使用者自定义编辑器/渲染器 UI 的能力。

### 1.3 目标

按使用者表述：

> 核心层是纯逻辑，UI 层是一个薄的封装，用户可以用核心层完全自定义 UI。

即**控制反转（Inversion of Control）**——核心层只管「状态 + 纯逻辑 + 可复用渲染原子」，UI 编排权完全交给调用方。参照 compose-hooks 的 `useForm` 模式：hook 返回状态对象 + 操作函数，UI 由调用方编排。

---

## 2. 现状分层

| 关注点 | 位置 | Compose-free? | Public? |
|---|---|---|---|
| AST 模型 | `palette-markdown/.../MarkdownModels.kt` | 是 | 是 |
| 块解析器 | `MarkdownParser.kt:12` (`parse`) | 是 | 是 |
| 行内解析器 | `MarkdownInlineParser.kt:3` (`parse`) | 是 | 是 |
| 模型变换 | `MarkdownRenderer.kt:11` (`toRenderModel`) | 是 | 是 |
| 编辑纯函数 | `palette/.../MarkdownEditActions.kt` | 是（仅 `TextRange`） | **`internal`** |
| 撤销栈 | `palette/.../MarkdownHistory.kt:16` | 是 | **`internal`** |
| 块渲染分发 | `palette/.../MarkdownViewer.kt:125` (`MarkdownBlock`) | 否 | **`private`** |
| 行内→AnnotatedString | `MarkdownViewer.kt` (`toAnnotatedContent`) | 否 | **`private`** |
| 编辑器布局 | `palette/.../MarkdownEditor.kt:111` (`Impl`) | 否 | `private`，零槽位 |

**结论**：核心解析层 `palette-markdown` 已是干净纯 Kotlin 模块——不动。要重构的全在 `palette` 模块 `components/markdown/`：把「逻辑 + UI 写死」拆成「纯逻辑 hook + 可复用渲染原子 + 薄默认 UI」三层。

---

## 3. 目标分层架构

```
┌─────────────────────────────────────────────────────────┐
│ palette-markdown（核心层，保持纯净）                      │
│  原有：MarkdownParser / MarkdownInlineParser /            │
│        MarkdownRenderer / AST 模型（全 public、纯 Kotlin） │
│  新增：MarkdownSelection / MarkdownEditResult（纯类型）    │
│  下沉：MarkdownEditActions / MarkdownHistory（→ public）   │
└─────────────────────────────────────────────────────────┘
                          │ (palette 依赖 palette-markdown)
                          ▼
┌─────────────────────────────────────────────────────────┐
│ palette/.../markdown（重构区）                            │
│                                                          │
│ ① 桥接层（new）                                          │
│    TextRange ↔ MarkdownSelection 互转（仅 UI 层用）       │
│                                                          │
│ ② 控制反转层（new）                                       │
│    useMarkdownEditorController hook（仿 useForm）         │
│    MarkdownEditorController（状态 + 操作函数对象）         │
│    MarkdownEditorScope（receiver，供槽位拿 controller）    │
│    Modifier.markdownEditorKeyBindings(controller)         │
│                                                          │
│ ③ 渲染原子（private → public）                           │
│    MarkdownBlock / MarkdownBlocks / DefaultInlineImage    │
│    List<MarkdownInlineNode>.toAnnotatedContent()          │
│                                                          │
│ ④ 薄默认 UI 封装（重写为调 ①②③）                         │
│    PMarkdownEditor（控制器 + 三槽默认实现）                │
│    PMarkdownViewer（解析 + MarkdownBlocks 原子）           │
│    MarkdownFormatToolbar（保持 public，wiring 改 controller）│
└─────────────────────────────────────────────────────────┘
```

### 三种使用姿势

1. **开箱即用**：直接用 `PMarkdownEditor` / `PMarkdownViewer`，内部已迁移到 PSegmented + 控制器
2. **半定制**：用默认组件，传入任一槽（`modeSwitch`/`toolbar`/`preview`）替换某一层
3. **全定制**：自己写 `@Composable`，调 `useMarkdownEditorController()` 拿状态/操作，UI 全自己拼，预览侧用 `MarkdownBlock` 原子

---

## 4. 设计决策

### 决策 1：纯逻辑归宿 → 下沉到 palette-markdown

**选择**：`MarkdownEditActions` / `MarkdownHistory` 从 `palette` 迁到 `palette-markdown`，`internal → public`。

**代价**：这些函数当前依赖 `androidx.compose.ui.text.TextRange` / `TextFieldValue`，而 `palette-markdown` 是零 Compose 依赖模块。下沉的同时必须做**选区抽象**——核心层用 Compose-free 的 `MarkdownSelection`，UI 层负责与 `TextRange` 互转。

**为何不留在 palette（方案 A）**：使用者明确选择下沉，使核心层成为「解析 + 编辑逻辑」的完整纯逻辑层，UI 层纯粹薄封装。选区抽象是必要且一次性代价。

**为何不新建中间模块（方案 C）**：体量（~400 行）撑不起独立模块，过度工程。

### 决策 2：块渲染开放机制 → 提为 public composable 原子

**选择**：把 `MarkdownBlock` / `MarkdownBlocks` / `toAnnotatedContent` / `DefaultInlineImage` 从 `private → public`，作为「可复用渲染原子」。不额外引入 component-mapper 接口（YAGNI）。

**理由**：用户可在自定义布局里自由组合这些原子；块内 `when` 分发保持不变（仅可见性变化），最稳的开放方式，无新抽象。

### 决策 3：控制反转契约 → 仿 compose-hooks Form 模式

**选择**：`useMarkdownEditorController()` hook 返回 `MarkdownEditorController` 对象（状态 + 操作函数），UI 编排权交给调用方。

### 决策 4：mode-switch 迁移 → PToggleGroup 改为 PSegmented

**选择**：默认 mode-switch 实现从 `PToggleGroup`（`ToggleVariant.Surface`）改为 `PSegmented`（`ComponentSize.Small`）。

**理由**：mode-switch 是 N 个互斥视图模式，`PSegmented` 天生单选 + 内置 `animateDpAsState` 滑块，语义和交互都更对；删除 list 适配 hack。

---

## 5. 详细设计

### 5.1 核心层改动（palette-markdown）

#### 5.1.1 新增 Compose-free 选区/编辑结果类型

`palette-markdown/.../MarkdownModels.kt`：

```kotlin
/** 选区，等价于 Compose 的 TextRange 但不依赖 Compose。start<=end。 */
data class MarkdownSelection(val start: Int, val end: Int) {
    val length: Int get() = end - start
    companion object { val Empty = MarkdownSelection(0, 0) }
}

/** 所有编辑变换的统一返回：新文本 + 新选区。 */
data class MarkdownEditResult(val text: String, val selection: MarkdownSelection)
```

#### 5.1.2 编辑逻辑迁移并改为 public

- `MarkdownEditActions.kt` 从 `palette/.../components/markdown/` 迁到 `palette-markdown/.../`，所有函数 `internal → public`
- 签名 `TextRange` → `MarkdownSelection`，`MarkdownEditResult` 用核心层版本
- `MarkdownHistory.kt` 同步迁移：撤销栈存值从 `TextFieldValue` 改为 `Pair<String, MarkdownSelection>`（纯数据），`internal → public`

迁移后公共函数签名（示例）：

```kotlin
public fun wrapSelection(text: String, selection: MarkdownSelection, prefix: String, suffix: String = prefix): MarkdownEditResult
public fun toggleLinePrefix(text: String, selection: MarkdownSelection, prefix: String, ordered: Boolean = false): MarkdownEditResult
public fun toggleTaskItem(text: String, selection: MarkdownSelection): MarkdownEditResult
public fun setHeadingLevel(text: String, selection: MarkdownSelection, level: MarkdownHeadingLevel): MarkdownEditResult
public fun insertText(text: String, selection: MarkdownSelection, snippet: String, selectInside: IntRange? = null): MarkdownEditResult
public fun indent(text: String, selection: MarkdownSelection, forward: Boolean = true): MarkdownEditResult
public fun continueOnEnter(text: String, selection: MarkdownSelection): MarkdownEditResult?
```

#### 5.1.3 不动的部分

`MarkdownParser` / `MarkdownInlineParser` / `MarkdownRenderer` / 全套 AST 模型——**完全不动**。

### 5.2 桥接层（palette 模块新增）

`TextFieldValue` ↔ `MarkdownSelection` 互转，约 10 行，仅 UI 层使用：

```kotlin
internal fun TextRange.toMarkdownSelection() = MarkdownSelection(start, end)
internal fun MarkdownSelection.toTextRange() = TextRange(start, end)
internal fun TextFieldValue.toCore(): Pair<String, MarkdownSelection> = text to selection.toMarkdownSelection()
internal fun Pair<String, MarkdownSelection>.toTextFieldValue() = TextFieldValue(first, second.toTextRange())
```

`palette-markdown` 保持纯 Kotlin、零 Compose；所有 Compose 类型仅在 `palette` 桥接层出现。

### 5.3 控制反转层（palette 模块新增）

#### 5.3.1 MarkdownEditorController

```kotlin
@Stable
class MarkdownEditorController internal constructor(/* hook 注入 */) {
    // —— 状态（可观察，驱动重组）——
    val value: TextFieldValue
    val mode: MarkdownEditorMode
    val canUndo: Boolean
    val canRedo: Boolean

    // —— 编辑操作（委托 MarkdownEditActions 纯函数 + MarkdownHistory）——
    fun setValue(value: TextFieldValue)
    fun setText(text: String)
    fun setMode(mode: MarkdownEditorMode)
    fun applyAction(action: MarkdownToolbarAction)
    fun wrapSelection(prefix: String, suffix: String = prefix)
    fun toggleLinePrefix(prefix: String, ordered: Boolean = false)
    fun toggleTaskItem()
    fun setHeadingLevel(level: MarkdownHeadingLevel)
    fun insertText(snippet: String, selectInside: IntRange? = null)
    fun indent(forward: Boolean = true)
    fun undo()
    fun redo()
}
```

设计要点：
- 粗粒度 `applyAction`（对接 `MarkdownToolbarAction` 枚举，默认工具栏用）+ 细粒度方法（自定义 UI 按需调用）并存
- `@Stable`，字段全是值类型或 stable 引用，重组安全

#### 5.3.2 hook 签名

```kotlin
@Composable
fun useMarkdownEditorController(
    initialValue: TextFieldValue = TextFieldValue(""),
    initialMode: MarkdownEditorMode = MarkdownEditorMode.Split,
    historyLimit: Int = 100,
): MarkdownEditorController
```

- 内部 `useState` 持 `value` / `mode`，`useRef` 持 `MarkdownHistory`（跨重组保持撤销栈），遵循项目 `compose-hooks` 优先规范
- 操作内部走桥接（§5.2）调核心层纯函数，结果 `setValue` + `history.push`

#### 5.3.3 受控/非受控

控制器是唯一状态源（仿 Form）。`PMarkdownEditor` 内部调 hook 绑 UI；全定制用户自己调 hook。若需受控于外部状态，用户调 hook 后在 `LaunchedEffect` 里把外部 `value` 同步进 controller（与 `useForm` 一致），不额外做「外部传入 controller」重载。

**注意**：`PMarkdownEditor` 的 `value`/`onValueChange` 顶层参数保留（与 `useForm` 的便捷封装同形）。内部实现用 `LaunchedEffect(value)` 将外部 String 同步进控制器，控制器变更后通过 `onValueChange` 回报——即 `PMarkdownEditor` 是控制器的**受控便捷封装**，控制器本身才是唯一状态源。

#### 5.3.4 键盘快捷键独立化

抽出 `MarkdownEditor.kt:229-288` 的 `keyModifier` 为 public 扩展：

```kotlin
fun Modifier.markdownEditorKeyBindings(controller: MarkdownEditorController): Modifier
```

底层调 `controller.applyAction` / `undo` / `redo`，逻辑零重复。

### 5.4 渲染原子层（palette 模块，private → public）

`MarkdownViewer.kt` 的核心 composable 提为 public 并稳定签名：

```kotlin
@Composable
fun MarkdownBlock(
    block: MarkdownRenderBlock,
    modifier: Modifier = Modifier,
    onLinkClick: ((String) -> Unit)? = null,
    onAnchorClick: ((String) -> Unit)? = null,
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit = { DefaultInlineImage(it) },
)

@Composable
fun MarkdownBlocks(
    blocks: List<MarkdownRenderBlock>,
    modifier: Modifier = Modifier,
    onLinkClick: ((String) -> Unit)? = null,
    onAnchorClick: ((String) -> Unit)? = null,
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit = { DefaultInlineImage(it) },
)

fun List<MarkdownInlineNode>.toAnnotatedContent(): AnnotatedString

@Composable
fun DefaultInlineImage(image: MarkdownInlineImage)
```

`MarkdownBlock` 内部 `when` 分发保持不变（仅可见性变化），不引入新抽象。

### 5.5 薄默认 UI 封装

#### 5.5.1 PMarkdownViewer

签名基本不变，实现退化为薄封装：

```kotlin
@Composable
fun PMarkdownViewer(
    markdown: String,
    modifier: Modifier = Modifier,
    renderModel: MarkdownRenderModel? = null,
    onLinkClick: ((String) -> Unit)? = null,
    onAnchorClick: ((String) -> Unit)? = null,
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit = { DefaultInlineImage(it) },
    verticalScroll: Boolean = true,
    showCopyAction: Boolean = true,
)
```

实现：解析（`useCreation`，已有）→ 可选复制按钮（已有）→ 调 `MarkdownBlocks(renderModel.blocks, ...)`。

#### 5.5.2 PMarkdownEditor

移除硬参数（`mode`/`onModeChange`/`editLabel`/`previewLabel`/`splitLabel`），新增三槽 + scope：

```kotlin
@Composable
fun PMarkdownEditor(
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
)

@Stable
class MarkdownEditorScope(
    val controller: MarkdownEditorController,
    val editLabel: String,
    val previewLabel: String,
    val splitLabel: String,
    val placeholder: String,
    val enabled: Boolean,
)
```

label 默认值来自 `PaletteStrings`（i18n），用户自定义 mode-switch 时可读 `scope.editLabel` 也可忽略。

#### 5.5.3 默认实现

- **默认 modeSwitch**：`PSegmented`（替换 `PToggleGroup`），见 §5.6
- **默认 toolbar**：复用 `MarkdownFormatToolbar`（保持 public），绑 `scope.controller::applyAction`
- **默认 preview**：`PMarkdownViewer(controller.value.text)`
- **默认 textarea**：保持 `MarkdownEditorTextArea`（private），绑 `controller.value` / `controller::setValue` + `Modifier.markdownEditorKeyBindings(controller)`，不做成槽

### 5.6 PToggleGroup → PSegmented 迁移

默认 mode-switch 实现改为：

```kotlin
@Composable
internal fun DefaultModeSwitch(scope: MarkdownEditorScope) {
    PSegmented(
        options = listOf(
            SegmentedOption(value = MarkdownEditorMode.Edit.name, label = scope.editLabel),
            SegmentedOption(value = MarkdownEditorMode.Preview.name, label = scope.previewLabel),
            SegmentedOption(value = MarkdownEditorMode.Split.name, label = scope.splitLabel),
        ),
        value = scope.controller.mode.name,
        onValueChange = { scope.controller.setMode(MarkdownEditorMode.valueOf(it)) },
        size = ComponentSize.Small,
    )
}
```

删除 `listOf(currentMode.name)` / `selected.firstOrNull() ?: return` 的 list 适配 hack；删除 `ToggleVariant.Surface`（PSegmented 自带容器）；视觉从「toggle 组」升级为「分段器」（预期效果）。

### 5.7 全定制姿势示例（设计验证）

```kotlin
@Composable
fun MyCustomMarkdownEditor(initial: String) {
    val controller = useMarkdownEditorController(initialValue = TextFieldValue(initial))
    LaunchedEffect(controller.value.text) { /* 同步外部 */ }

    Column {
        PSegmented(
            options = listOf(
                SegmentedOption("Edit", "编辑"),
                SegmentedOption("Preview", "预览"),
                SegmentedOption("Split", "分屏"),
            ),
            value = controller.mode.name,
            onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        )
        Row {
            PButton(onClick = { controller.applyAction(MarkdownToolbarAction.Bold) }) { Text("B") }
        }
        when (controller.mode) {
            MarkdownEditorMode.Edit -> TextField(
                value = controller.value,
                onValueChange = controller::setValue,
                modifier = Modifier.markdownEditorKeyBindings(controller),
            )
            MarkdownEditorMode.Preview -> {
                val model = useCreation { MarkdownRenderer.toRenderModel(MarkdownParser.parse(controller.value.text)) }
                MarkdownBlocks(model.blocks)
            }
            MarkdownEditorMode.Split -> { /* ... */ }
        }
    }
}
```

### 5.8 半定制姿势示例（槽位 API 验证）

```kotlin
PMarkdownEditor(
    value = text,
    onValueChange = ::setText,
    modeSwitch = {
        PSegmented(
            options = listOf(
                SegmentedOption("Edit", "编辑", icon = { PIcon(EditIcon) }),
                SegmentedOption("Preview", "预览", icon = { PIcon(EyeIcon) }),
                SegmentedOption("Split", "分屏", icon = { PIcon(SplitIcon) }),
            ),
            value = controller.mode.name,
            onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        )
    },
)
```

---

## 6. 统一导出（Palette.kt）

参照现有 `Palette.kt:925-934`：

```kotlin
// 保持
val PMarkdownViewer = ::PMarkdownViewerImpl
val PMarkdownEditor = ::PMarkdownEditorImpl
val PMarkdownToc = ::PMarkdownTocImpl
val MarkdownDefaults = MarkdownDefaultsImpl
val MarkdownTocDefaults = MarkdownTocDefaultsImpl
val MarkdownFormatToolbar = ::MarkdownFormatToolbarImpl
typealias MarkdownHeadingLevel = MarkdownHeadingLevelImpl

// 新增：核心层类型
typealias MarkdownSelection = MarkdownSelectionImpl
typealias MarkdownEditResult = MarkdownEditResultImpl

// 新增：控制反转层
val useMarkdownEditorController = ::useMarkdownEditorControllerImpl
typealias MarkdownEditorController = MarkdownEditorControllerImpl
typealias MarkdownEditorMode = MarkdownEditorModeImpl
typealias MarkdownEditorScope = MarkdownEditorScopeImpl
typealias MarkdownToolbarAction = MarkdownToolbarActionImpl   // internal → public

// 新增：渲染原子
val MarkdownBlock = ::MarkdownBlockImpl
val MarkdownBlocks = ::MarkdownBlocksImpl
val DefaultInlineImage = ::DefaultInlineImageImpl
```

> Kotlin 顶层扩展函数无法用 `::` 引用再导出。`toAnnotatedContent`、`markdownEditorKeyBindings`、`List<MarkdownInlineNode>` 扩展走「直接 import 包路径」+ KDoc 注明（与现有 `PMarkdownEditorValue` 同策略）。`PMarkdownEditor` 的 `TextFieldValue` 重载延续现有不导出策略。

---

## 7. 迁移

### 7.1 demo（`app/.../demo/MarkdownDemo.kt:52-70`）

移除 `editLabel/previewLabel/splitLabel` 顶层参数（已进 scope），其余不变：

```kotlin
PMarkdownEditor(
    value = editorValue,
    onValueChange = setEditorValue,
    placeholder = text.editorPlaceholder,
    showPreview = true,
    showFormatToolbar = true,
)
```

**新增 demo 节**：「全定制编辑器」，演示 `useMarkdownEditorController` + `MarkdownBlocks` 原子自拼 UI，落地验证控制反转契约。

### 7.2 docs-site（`docs-site/docs/features/markdown.md`）

扩充：
- 新增「架构」小节：三层架构图 + 三种使用姿势
- 编辑器章节：说明三槽定制 + scope controller
- 新增「自定义渲染」小节：`MarkdownBlock` / `MarkdownBlocks` / `toAnnotatedContent` 原子用法

---

## 8. 完整 Breaking Change 清单

| # | 变更 | 影响面 | 迁移动作 |
|---|---|---|---|
| 1 | `MarkdownEditActions.kt` 迁到 `palette-markdown`，`internal→public`，`TextRange→MarkdownSelection` | 模块内部 | 删原文件，新模块建文件 |
| 2 | `MarkdownHistory.kt` 同上迁移，存值改 `Pair<String,MarkdownSelection>` | 模块内部 | 同上 |
| 3 | `palette-markdown` 新增 `MarkdownSelection` / `MarkdownEditResult` | 核心模块 | 新增 |
| 4 | `palette` 新增桥接层 `TextRange↔MarkdownSelection` 互转 | 模块内部 | 新增小文件 |
| 5 | 新增 `MarkdownEditorController` + `useMarkdownEditorController` hook | 公共 API | 新增 + 导出 |
| 6 | `MarkdownToolbarAction` `internal→public` | 公共 API | 改可见性 + 导出 |
| 7 | `PMarkdownEditor` 移除参数：`mode`/`onModeChange`/`editLabel`/`previewLabel`/`splitLabel` | 公共 API + demo | 删参数，demo 同步 |
| 8 | `PMarkdownEditor` 新增参数：`modeSwitch`/`toolbar`/`preview` 三槽 + `MarkdownEditorScope` | 公共 API | 新增 |
| 9 | `PMarkdownEditor` 内部 mode-switch：`PToggleGroup`→`PSegmented` | 内部 | 重写默认实现 |
| 10 | `MarkdownBlock`/`MarkdownBlocks`/`toAnnotatedContent`/`DefaultInlineImage` `private→public` | 公共 API | 改可见性 + 导出 |
| 11 | `PMarkdownViewer` 内部重写为调 `MarkdownBlocks` 原子 | 内部 | 重写，签名不变 |
| 12 | 新增 `Modifier.markdownEditorKeyBindings(controller)` 扩展 | 公共 API | 抽出（import 导出） |
| 13 | `Palette.kt` 导出增删 | 公共 API | 同步 |

---

## 9. 不变的部分（防止 scope 蔓延）

- `palette-markdown` 的 `MarkdownParser` / `MarkdownInlineParser` / `MarkdownRenderer` / 全套 AST 模型——不动
- `PMarkdownToc` / `MarkdownToc` 相关——不动
- `MarkdownFormatToolbar` 组件本身——保持 public，仅内部 wiring 改为调 controller
- `MarkdownDefaults` / `MarkdownTocDefaults` 的 token 派生——不动
- 现有 i18n key（`editLabel` 等）——复用，不删

---

## 10. 测试策略

呼应项目 TDD 守则：

- **核心层纯逻辑迁移后**：现有 `MarkdownEditActions` 测试随迁到 `palette-markdown/commonTest`，断言改用 `MarkdownSelection`；若无测试，补正常路径 + 边界（空选区、全选、跨行）测试
- **桥接层**：`TextRange↔MarkdownSelection` 互转 round-trip 测试
- **controller hook**：Desktop UI 测试覆盖 `applyAction` 改变 `value`、`undo`/`redo` 栈正确性、`setMode` 切换
- **渲染原子**：Desktop UI 测试 `MarkdownBlocks(blocks)` 对各块类型的渲染（已有 viewer 测试可复用/迁移）
- 提交前 `:palette:desktopTest` / `:palette-markdown:test` 必过
