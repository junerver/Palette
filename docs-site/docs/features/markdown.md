# Markdown

`palette-markdown` 负责纯解析与渲染模型，`palette` 中的 markdown 组件负责默认 UI、编辑器控制器和可复用渲染原子。

## 架构

当前 markdown 能力分成三层：

- 核心层：`palette-markdown` 暴露 `MarkdownParser`、`MarkdownRenderer`、`MarkdownSelection`、`MarkdownHistory`、编辑纯函数，全部是 Compose-free 逻辑。
- 控制层：`useMarkdownEditorController()` 返回 `MarkdownEditorController`，提供当前 `value` / `mode` / `undo` / `redo` / `applyAction()` 等能力。
- 渲染层：`PMarkdownViewer` 是开箱即用封装，`MarkdownBlock` / `MarkdownBlocks` / `DefaultInlineImage` 是 public 渲染原子，便于自定义布局。

对应三种使用姿势：

- 开箱即用：直接使用 `PMarkdownEditor` / `PMarkdownViewer`
- 半定制：为 `PMarkdownEditor` 传 `modeSwitch` / `toolbar` / `preview` 槽位
- 全定制：自己调用 `useMarkdownEditorController` + `MarkdownBlocks`

## Viewer

```kotlin
import xyz.junerver.compose.palette.components.markdown.PMarkdownViewer

val md = """
    # Title
    Some **bold** text and a `code span`.
"""

PMarkdownViewer(markdown = md, showCopyAction = true)
```

- `onLinkClick` / `onAnchorClick` 处理链接与标题锚点
- `inlineImageContent` 自定义行内图片渲染
- `verticalScroll` 控制内置纵向滚动
- fenced code block 会分派到 `palette-code`，```` ```mermaid ```` 会分派到 `palette-mermaid`

## Editor

```kotlin
import xyz.junerver.compose.palette.components.markdown.PMarkdownEditor

var text by remember { mutableStateOf("") }

PMarkdownEditor(
    value = text,
    onValueChange = { text = it },
)
```

默认编辑器内置：

- `PSegmented` 视图模式切换
- 格式工具栏（bold / italic / heading / list / quote / link / code / table）
- `Tab` 缩进、`Enter` 自动续行
- `MarkdownHistory` 撤销 / 重做和 `Ctrl/Cmd` 快捷键

### 槽位定制

`PMarkdownEditor` 提供三处槽位：

```kotlin
PMarkdownEditor(
    value = text,
    onValueChange = { text = it },
    modeSwitch = {
        PSegmented(
            options = listOf(
                SegmentedOption(MarkdownEditorMode.Edit.name, editLabel),
                SegmentedOption(MarkdownEditorMode.Preview.name, previewLabel),
                SegmentedOption(MarkdownEditorMode.Split.name, splitLabel),
            ),
            value = controller.mode.name,
            onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        )
    },
    toolbar = {
        MarkdownFormatToolbar(
            onAction = controller::applyAction,
            onHeadingLevel = controller::setHeadingLevel,
        )
    },
)
```

槽位 receiver 是 `MarkdownEditorScope`，可直接访问：

- `controller`
- `editLabel` / `previewLabel` / `splitLabel`
- `placeholder`
- `enabled`

## 全定制编辑器

```kotlin
val controller = useMarkdownEditorController(initialValue = TextFieldValue(initialText))

PSegmented(
    options = listOf(
        SegmentedOption(MarkdownEditorMode.Edit.name, "Edit"),
        SegmentedOption(MarkdownEditorMode.Preview.name, "Preview"),
        SegmentedOption(MarkdownEditorMode.Split.name, "Split"),
    ),
    value = controller.mode.name,
    onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
)

OutlinedTextField(
    value = controller.value,
    onValueChange = controller::setValue,
    modifier = Modifier.markdownEditorKeyBindings(controller),
)
```

`MarkdownEditorController` 提供：

- `value`
- `mode`
- `canUndo` / `canRedo`
- `applyAction()`
- `wrapSelection()` / `toggleLinePrefix()` / `insertText()` / `setHeadingLevel()`
- `undo()` / `redo()`

## 自定义渲染

如果不想使用 `PMarkdownViewer`，可以直接组合 render model 与渲染原子：

```kotlin
val model = MarkdownRenderer.toRenderModel(MarkdownParser.parse(markdown))

MarkdownBlocks(
    blocks = model.blocks,
    onLinkClick = { url -> open(url) },
    onAnchorClick = { slug -> scrollTo(slug) },
    inlineImageContent = { image ->
        DefaultInlineImage(image)
    },
)
```

也可以按块级别单独渲染：

```kotlin
model.blocks.forEach { block ->
    MarkdownBlock(
        block = block,
        onLinkClick = { url -> open(url) },
        inlineImageContent = { image -> DefaultInlineImage(image) },
    )
}
```

## TOC 与 Frontmatter

`MarkdownRenderModel` 暴露：

- `toc: List<MarkdownTocEntry>`：标题目录，`id` 与 viewer 锚点一致
- `frontmatter: Map<String, String>`：解析后的 YAML frontmatter

目录渲染可配合 `PMarkdownToc` 与 viewer 的 `onAnchorClick` 使用。
