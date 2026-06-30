package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.markdown.PMarkdownEditor
import xyz.junerver.compose.palette.components.markdown.PMarkdownViewer
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun MarkdownDemo() {
    val text = markdownDemoText()
    val (editorValue, setEditorValue) = useState(text.editorMarkdown)

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
                editLabel = text.editLabel,
                previewLabel = text.previewLabel,
                splitLabel = text.splitLabel,
                showPreview = true,
                // 阶段 A：内置格式化工具栏（加粗/斜体/删除线/代码/标题/列表/引用/链接/图片/代码块/表格/分隔线）。
                // Tab 缩进、回车续行也已内置；设为 false 可仅保留纯文本框。
                // 阶段 B：撤销/重做 + 键盘快捷键。
                //   Ctrl/Cmd+B 加粗 | +I 斜体 | +K 链接 | +Shift+K 删除线
                //   Ctrl/Cmd+E 行内代码 | +Shift+E 代码块
                //   Ctrl/Cmd+U 无序列表 | +O 有序列表 | +Shift+O 引用
                //   Ctrl/Cmd+Z 撤销 | +Shift+Z 或 +Y 重做（连续输入按词合并回退）
                showFormatToolbar = true,
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

                    行内数学与标记：勾股定理 ${'$'}a^2 + b^2 = c^2${'$'}、分数 ${'$'}\frac{1}{2}${'$'}、根号 ${'$'}\sqrt{x+1}${'$'}、求和 ${'$'}\sum_{i=0}^{n} i${'$'}、向量 ${'$'}\vec{a} \cdot \vec{b}${'$'}、不等式 ${'$'}a \neq b \leq c \geq d${'$'}、下标 H~2~O、上标 E^2^、高亮 ==关键==。

                    ${'$'}${'$'}\int_0^1 x^2 \, dx = \frac{1}{3}${'$'}${'$'}

                    ## ATX 结尾井号 ##

                    Setext 标题
                    -----------

                    3. 保留有序列表的起始编号
                    4. 继续使用原始 Markdown 编号显示

                    - [x] 解析 Markdown 文档
                    - [x] 高亮 fenced code
                    - [ ] 接入远程图片加载

                    | 能力 | 状态 | 入口 |
                    | :--- | :---: | ---: |
                    | 代码高亮 | ready | `PCodeBlock` |
                    | 图片 | placeholder | ![Palette icon](https://example.com/palette.png) |
                    | 表格竖线 | a\|b | `x|y` |
                    | 空单元格 |  | 保留列 |

                    ```kotlin title="Preview.kt" showLineNumbers {2-4}
                    @Composable
                    fun Preview() {
                        PMarkdownViewer(markdown = content)
                    }
                    ```

                    ```markdown
                    ## 嵌套 Markdown 代码

                    - [x] 显示 `inline code`
                    - [ ] 打开 [文档](https://palette.example/docs)
                    ```

                    缩进代码块会作为普通代码块渲染：

                        val mode = "indented"
                            println(mode)

                    ```json
                    {
                      "component": "PMarkdownViewer",
                      "preview": true
                    }
                    ```

                    ```yaml
                    name: Palette
                    enabled: true
                    retries: 3
                    defaults: &defaults
                      theme: "light"
                    items:
                      - *defaults # shared config
                    ```

                    ```toml
                    # package metadata
                    [project]
                    name = "Palette"
                    enabled = true
                    targets = ["android", "desktop", "ios"]
                    ```

                    ```sql
                    SELECT id, COUNT(*) AS total
                    FROM components
                    WHERE enabled = TRUE AND kind = 'markdown'
                    ORDER BY created_at DESC;
                    ```

                    ```css
                    .markdown-viewer {
                      color: #fff;
                      margin: 8px;
                    }
                    ```

                    ```html
                    <section class="markdown-viewer">
                      <h1>Palette</h1>
                    </section>
                    ```

                    ```python title="preview.py" showLineNumbers {2}
                    def render(name: str) -> str:
                        return f"Hello, {name}"
                    ```

                    ```bash
                    export APP_NAME="Palette"
                    ./gradlew :palette:desktopTest --info
                    ```

                    ```diff
                    @@ -1,2 +1,2 @@
                    -OldButton()
                    +PButton(text = "Save") {}
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

                    ~~~kotlin
                    val fence = "tilde"
                    ~~~
                    """.trimIndent(),
            )

        Language.EN_US ->
            MarkdownDemoText(
                title = "Markdown",
                subtitle = "Markdown viewer, editor, and code highlighting.",
                viewerTitle = "Viewer",
                editorTitle = "Editor",
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

                    Inline syntax supports ~~strikethrough~~, _emphasis_, __strong `code`__, `` `code span` `` with backticks, [**nested links**](https://palette.example/docs), autolinks <https://example.com>, bare links https://palette.example/docs/(guide), and escaped \*literal\* text.
                    Reference-style links are parsed too: [docs][palette-docs].

                    [palette-docs]: https://palette.example/docs "Palette docs"

                    Inline math and marks: Pythagorean theorem ${'$'}a^2 + b^2 = c^2${'$'}, fraction ${'$'}\frac{1}{2}${'$'}, radical ${'$'}\sqrt{x+1}${'$'}, summation ${'$'}\sum_{i=0}^{n} i${'$'}, vectors ${'$'}\vec{a} \cdot \vec{b}${'$'}, inequality ${'$'}a \neq b \leq c \geq d${'$'}, subscript H~2~O, superscript E^2^, highlight ==key==.

                    ${'$'}${'$'}\int_0^1 x^2 \, dx = \frac{1}{3}${'$'}${'$'}

                    ## ATX Closing Hashes ##

                    Setext Heading
                    --------------

                    3. Preserve ordered list start numbers
                    4. Continue rendering from the Markdown marker

                    - [x] Parse Markdown documents
                    - [x] Highlight fenced code
                    - [ ] Load remote images

                    | Capability | Status | Entry |
                    | :--- | :---: | ---: |
                    | Code highlighting | ready | `PCodeBlock` |
                    | Mermaid | ready | `PMermaidDiagram` |
                    | Images | placeholder | ![Palette icon](https://example.com/palette.png) |
                    | Table pipes | a\|b | `x|y` |
                    | Empty cells |  | Keep columns |

                    ```kotlin title="Preview.kt" showLineNumbers {2-4}
                    @Composable
                    fun Preview() {
                        PMarkdownViewer(markdown = content)
                    }
                    ```

                    ```markdown
                    ## Nested Markdown Code

                    - [x] Render `inline code`
                    - [ ] Open [docs](https://palette.example/docs)
                    ```

                    Indented code blocks render as plain code:

                        val mode = "indented"
                            println(mode)

                    ```json
                    {
                      "component": "PMarkdownViewer",
                      "preview": true
                    }
                    ```

                    ```yaml
                    name: Palette
                    enabled: true
                    retries: 3
                    defaults: &defaults
                      theme: "light"
                    items:
                      - *defaults # shared config
                    ```

                    ```toml
                    # package metadata
                    [project]
                    name = "Palette"
                    enabled = true
                    targets = ["android", "desktop", "ios"]
                    ```

                    ```sql
                    SELECT id, COUNT(*) AS total
                    FROM components
                    WHERE enabled = TRUE AND kind = 'markdown'
                    ORDER BY created_at DESC;
                    ```

                    ```css
                    .markdown-viewer {
                      color: #fff;
                      margin: 8px;
                    }
                    ```

                    ```html
                    <section class="markdown-viewer">
                      <h1>Palette</h1>
                    </section>
                    ```

                    ```python title="preview.py" showLineNumbers {2}
                    def render(name: str) -> str:
                        return f"Hello, {name}"
                    ```

                    ```bash
                    export APP_NAME="Palette"
                    ./gradlew :palette:desktopTest --info
                    ```

                    ```diff
                    @@ -1,2 +1,2 @@
                    -OldButton()
                    +PButton(text = "Save") {}
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

                    ~~~kotlin
                    val fence = "tilde"
                    ~~~
                    """.trimIndent(),
            )
    }

private data class MarkdownDemoText(
    val title: String,
    val subtitle: String,
    val viewerTitle: String,
    val editorTitle: String,
    val editorPlaceholder: String,
    val editLabel: String,
    val previewLabel: String,
    val splitLabel: String,
    val viewerMarkdown: String,
    val editorMarkdown: String,
)
