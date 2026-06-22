package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class MarkdownUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun markdownViewerRendersHeadingCodeAndMermaidNodes() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown =
                        """
                        # Markdown Viewer

                        ```kotlin
                        val answer = 42
                        ```

                        ```mermaid
                        flowchart LR
                            A[Markdown] -- renders --> B[Viewer]
                        ```
                        """.trimIndent(),
                )
            }
        }

        rule.onNodeWithText("Markdown Viewer").assertTextEquals("Markdown Viewer")
        rule.onNodeWithText("val answer = 42").assertTextEquals("val answer = 42")
        rule.onNodeWithText("Markdown").assertTextEquals("Markdown")
        rule.onNodeWithText("renders").assertTextEquals("renders")
        rule.onNodeWithText("Viewer").assertTextEquals("Viewer")
    }
}
