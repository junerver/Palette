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
                subtitle = "Markdown 查看器、编辑器、代码高亮和 Mermaid flowchart 渲染。",
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
                    - `mermaid` 代码块使用 Mermaid flowchart 解析与布局逻辑

                    > 引用块会保留强调、链接和 `inline code` 等行内样式。

                    行内语法支持 ~~删除线~~、_强调_、__加粗 `code`__、[**嵌套链接**](https://palette.example/docs)、自动链接 <https://example.com>、裸链接 https://palette.example/docs 和转义 \*literal\*。
                    引用式链接也会解析：[文档][palette-docs]。

                    [palette-docs]: https://palette.example/docs "Palette docs"

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
                    | Mermaid | ready | `PMermaidDiagram` |
                    | 图片 | placeholder | ![Palette icon](https://example.com/palette.png) |

                    ```kotlin title="Preview.kt" showLineNumbers {2-4}
                    @Composable
                    fun Preview() {
                        PMarkdownViewer(markdown = content)
                    }
                    ```

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

                    ```mermaid
                    flowchart LR
                        subgraph Foundation [基础能力]
                            Code[Kotlin code]
                            Mermaid[Mermaid]
                        end
                        Markdown[Markdown] --> Code[Kotlin code]
                        Markdown --> Mermaid[Mermaid]
                        Code --> Viewer[Viewer]
                        Mermaid --> Viewer
                        Legend[Standalone node]
                    ```

                    ```mermaid
                    sequenceDiagram
                        participant Editor
                        participant Parser
                        participant Viewer
                        Editor->>Parser: update markdown
                        Note right of Parser: Build AST and diagrams
                        Parser-->>Viewer: render model
                        Note over Editor,Viewer: Preview refresh
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
                subtitle = "Markdown viewer, editor, code highlighting, and Mermaid flowchart rendering.",
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
                    - `mermaid` code blocks use Mermaid flowchart parsing and layout

                    > Block quotes keep inline emphasis, links, and `inline code` styles.

                    Inline syntax supports ~~strikethrough~~, _emphasis_, __strong `code`__, [**nested links**](https://palette.example/docs), autolinks <https://example.com>, bare links https://palette.example/docs, and escaped \*literal\* text.
                    Reference-style links are parsed too: [docs][palette-docs].

                    [palette-docs]: https://palette.example/docs "Palette docs"

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

                    ```kotlin title="Preview.kt" showLineNumbers {2-4}
                    @Composable
                    fun Preview() {
                        PMarkdownViewer(markdown = content)
                    }
                    ```

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

                    ```mermaid
                    flowchart LR
                        subgraph Foundation [Foundation logic]
                            Code[Kotlin code]
                            Mermaid[Mermaid]
                        end
                        Markdown[Markdown] --> Code[Kotlin code]
                        Markdown --> Mermaid[Mermaid]
                        Code --> Viewer[Viewer]
                        Mermaid --> Viewer
                        Legend[Standalone node]
                    ```

                    ```mermaid
                    sequenceDiagram
                        participant Editor
                        participant Parser
                        participant Viewer
                        Editor->>Parser: update markdown
                        Note right of Parser: Build AST and diagrams
                        Parser-->>Viewer: render model
                        Note over Editor,Viewer: Preview refresh
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
