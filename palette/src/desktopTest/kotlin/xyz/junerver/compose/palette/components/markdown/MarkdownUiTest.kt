package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun markdownViewerInvokesLinkClick() {
        var clickedDestination: String? = null

        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown = "[docs](https://example.com/docs)",
                    onLinkClick = { destination -> clickedDestination = destination },
                )
            }
        }

        rule.onNodeWithText("docs").performClick()

        rule.runOnIdle {
            assertEquals("https://example.com/docs", clickedDestination)
        }
    }

    @Test
    fun markdownViewerRendersNestedListAndBlockQuoteChildren() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown =
                        """
                        - Parent
                          - Child

                        > ## Note
                        > - first
                        > - second
                        """.trimIndent(),
                )
            }
        }

        rule.onNodeWithText("Parent").assertTextEquals("Parent")
        rule.onNodeWithText("Child").assertTextEquals("Child")
        rule.onNodeWithText("Note").assertTextEquals("Note")
        rule.onNodeWithText("first").assertTextEquals("first")
        rule.onNodeWithText("second").assertTextEquals("second")
    }
}
import androidx.compose.ui.test.onNodeWithTag
    @Test
    fun markdownViewerExposesHeadingAnchorsAndRoutesAnchorClicks() {
        var anchorSlug: String? = null

        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown =
                        """
                        # Title

                        [jump](#title)
                        """.trimIndent(),
                    onAnchorClick = { slug -> anchorSlug = slug },
                )
            }
        }

        rule.onNodeWithTag("heading:title").assertExists()
        rule.onNodeWithText("jump").performClick()

        rule.runOnIdle {
            assertEquals("title", anchorSlug)
        }
    }
