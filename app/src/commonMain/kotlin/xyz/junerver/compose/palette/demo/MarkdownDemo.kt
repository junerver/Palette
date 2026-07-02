package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.markdown.MarkdownBlocks
import xyz.junerver.compose.palette.components.markdown.MarkdownEditorMode
import xyz.junerver.compose.palette.components.markdown.MarkdownToolbarAction
import xyz.junerver.compose.palette.components.markdown.PMarkdownEditor
import xyz.junerver.compose.palette.components.markdown.PMarkdownViewer
import xyz.junerver.compose.palette.components.markdown.markdownEditorKeyBindings
import xyz.junerver.compose.palette.components.markdown.useMarkdownEditorController
import xyz.junerver.compose.palette.components.segmented.PSegmented
import xyz.junerver.compose.palette.components.segmented.SegmentedOption
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.markdown.MarkdownParser
import xyz.junerver.compose.palette.markdown.MarkdownRenderer

@Composable
fun MarkdownDemo() {
    val text = markdownDemoText()
    val (editorValue, setEditorValue) = useState(text.editorMarkdown)
    val (customValue, setCustomValue) = useState(text.editorMarkdown)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.viewerTitle) {
            PMarkdownViewer(markdown = text.viewerMarkdown)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.editorTitle) {
            PMarkdownEditor(
                value = editorValue,
                onValueChange = setEditorValue,
                placeholder = text.editorPlaceholder,
                showPreview = true,
                showFormatToolbar = true,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customEditorTitle) {
            CustomMarkdownEditorDemo(
                value = customValue,
                onValueChange = setCustomValue,
                editLabel = text.editLabel,
                previewLabel = text.previewLabel,
                splitLabel = text.splitLabel,
            )
        }
    }
}

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

    androidx.compose.runtime.LaunchedEffect(value) {
        if (value != controller.value.text) {
            controller.sync(androidx.compose.ui.text.input.TextFieldValue(value))
        }
    }
    androidx.compose.runtime.LaunchedEffect(controller.value.text) {
        if (controller.value.text != value) {
            onValueChange(controller.value.text)
        }
    }

    Column {
        PSegmented(
            options = listOf(
                SegmentedOption(MarkdownEditorMode.Edit.name, editLabel),
                SegmentedOption(MarkdownEditorMode.Preview.name, previewLabel),
                SegmentedOption(MarkdownEditorMode.Split.name, splitLabel),
            ),
            value = controller.mode.name,
            onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            TextButton(onClick = { controller.applyAction(MarkdownToolbarAction.Bold) }) {
                androidx.compose.material3.Text("B")
            }
            TextButton(onClick = { controller.applyAction(MarkdownToolbarAction.Italic) }) {
                androidx.compose.material3.Text("I")
            }
            TextButton(onClick = { controller.applyAction(MarkdownToolbarAction.Link) }) {
                androidx.compose.material3.Text("Link")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (controller.mode != MarkdownEditorMode.Preview) {
            OutlinedTextField(
                value = controller.value,
                onValueChange = controller::setValue,
                modifier = Modifier.markdownEditorKeyBindings(controller),
            )
        }

        if (controller.mode != MarkdownEditorMode.Edit) {
            Spacer(modifier = Modifier.height(12.dp))
            val model = useCreation(controller.value.text) {
                MarkdownRenderer.toRenderModel(MarkdownParser.parse(controller.value.text))
            }.current
            MarkdownBlocks(
                blocks = model.blocks,
                onLinkClick = null,
                onAnchorClick = null,
                inlineImageContent = { image ->
                    xyz.junerver.compose.palette.components.markdown.DefaultInlineImage(image)
                },
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun markdownDemoText(): MarkdownDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            MarkdownDemoText(
                title = "Markdown",
                subtitle = "Markdown 查看器、编辑器、代码高亮。",
                viewerTitle = "查看器",
                editorTitle = "编辑器",
                customEditorTitle = "全定制编辑器",
                editorPlaceholder = "输入 Markdown",
                editLabel = "编辑",
                previewLabel = "预览",
                splitLabel = "分屏",
                viewerMarkdown =
                    """
                    # Palette Markdown

                    Markdown 渲染器会把 fenced code 分派到对应基础能力：

                    - `kotlin` / `yaml` / `toml` / `sql` / `diff` 等代码块使用 Palette 代码高亮逻辑

                    > 引用块会保留强调、链接和 `inline code` 等行内样式。

                    行内语法支持 ~~删除线~~、_强调_、__加粗 `code`__、包含反引号的 `` `code span` ``、[**嵌套链接**](https://palette.example/docs)、自动链接 <https://example.com>、裸链接 https://palette.example/docs/(guide) 和转义 \*literal\*。
                    引用式链接也会解析：[文档][palette-docs]。

                    [palette-docs]: https://palette.example/docs "Palette docs"

                    ## ATX 结尾井号 ##

                    Setext 标题
                    -----------

                    - [x] 解析 Markdown 文档
                    - [x] 高亮 fenced code
                    - [ ] 接入远程图片加载

                    ```kotlin title="Preview.kt" showLineNumbers {2-4}
                    @Composable
                    fun Preview() {
                        PMarkdownViewer(markdown = content)
                    }
                    ```
                    """.trimIndent(),
                editorMarkdown =
                    """
                    ## Live Preview

                    - edit markdown
                    - preview code and diagram
                    - [x] task list

                    | Syntax | Result |
                    | :--- | ---: |
                    | `**bold**` | strong text |
                    | `![alt](url)` | image placeholder |

                    ```kotlin
                    val component = "PMarkdownEditor"
                    ```
                    """.trimIndent(),
            )

        Language.EN_US ->
            MarkdownDemoText(
                title = "Markdown",
                subtitle = "Markdown viewer, editor, and code highlighting.",
                viewerTitle = "Viewer",
                editorTitle = "Editor",
                customEditorTitle = "Custom Editor",
                editorPlaceholder = "Enter Markdown",
                editLabel = "Edit",
                previewLabel = "Preview",
                splitLabel = "Split",
                viewerMarkdown =
                    """
                    # Palette Markdown

                    The Markdown renderer dispatches fenced blocks to foundation logic:

                    - `kotlin` / `yaml` / `toml` / `sql` / `diff` code blocks use Palette code highlighting

                    > Block quotes keep inline emphasis, links, and `inline code` styles.

                    ## ATX Closing Hashes ##

                    Setext Heading
                    --------------

                    - [x] Parse Markdown documents
                    - [x] Highlight fenced code
                    - [ ] Load remote images

                    ```kotlin title="Preview.kt" showLineNumbers {2-4}
                    @Composable
                    fun Preview() {
                        PMarkdownViewer(markdown = content)
                    }
                    ```
                    """.trimIndent(),
                editorMarkdown =
                    """
                    ## Live Preview

                    - edit markdown
                    - preview code and diagram
                    - [x] task list

                    | Syntax | Result |
                    | :--- | ---: |
                    | `**bold**` | strong text |
                    | `![alt](url)` | image placeholder |

                    ```kotlin
                    val component = "PMarkdownEditor"
                    ```
                    """.trimIndent(),
            )
    }

private data class MarkdownDemoText(
    val title: String,
    val subtitle: String,
    val viewerTitle: String,
    val editorTitle: String,
    val customEditorTitle: String,
    val editorPlaceholder: String,
    val editLabel: String,
    val previewLabel: String,
    val splitLabel: String,
    val viewerMarkdown: String,
    val editorMarkdown: String,
)
