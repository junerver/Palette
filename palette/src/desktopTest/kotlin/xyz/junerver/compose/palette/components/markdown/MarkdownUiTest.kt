package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun viewerRendersCheckboxesDisabled() {
        // Verify viewer renders task list as checkboxes (text visible), not raw markdown
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown =
                        """
                        - [x] done
                        - [ ] open
                        """.trimIndent(),
                )
            }
        }

        // Task item text should be rendered as separate text (not as raw "- [x] done")
        rule.onNodeWithText("done").assertExists()
        rule.onNodeWithText("open").assertExists()
        // Should NOT find the raw "- [x] done" string as a single text node
        // (it should be split into checkbox + text)
    }

    @Test
    fun viewerRendersTaskListAsCheckboxes() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown = "- [x] done\n- [ ] open",
                )
            }
        }

        // Task items should render as separate text (not raw "- [x] done")
        rule.onNodeWithText("done").assertExists()
        rule.onNodeWithText("open").assertExists()
    }

    @Test
    fun viewerRendersUnorderedListItems() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown = "- first\n- second",
                )
            }
        }

        // List items should be rendered (not raw "- first")
        rule.onNodeWithText("first").assertExists()
        rule.onNodeWithText("second").assertExists()
    }

    @Test
    fun viewerRendersMixedListTypes() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown = "- item one\n- [x] done\n- [ ] open",
                )
            }
        }

        // Mixed regular + task list items should all render as text
        rule.onNodeWithText("item one").assertExists()
        rule.onNodeWithText("done").assertExists()
        rule.onNodeWithText("open").assertExists()
    }

    @Test
    fun toggleTaskCheckboxUpdatesCorrectLine() {
        val markdown = "- [x] first\n- [ ] second\n- [x] third"

        // Toggle second item on
        val result1 = toggleTaskCheckbox(markdown, 1, true)
        assertTrue(result1.contains("- [x] second"), "Expected second checked: $result1")
        assertTrue(result1.contains("- [x] first"), "Expected first unchanged: $result1")
        assertTrue(result1.contains("- [x] third"), "Expected third unchanged: $result1")

        // Toggle first item off
        val result2 = toggleTaskCheckbox(markdown, 0, false)
        assertTrue(result2.contains("- [ ] first"), "Expected first unchecked: $result2")
        assertTrue(result2.contains("- [ ] second"), "Expected second unchanged: $result2")
    }

    @Test
    fun viewerTaskCheckboxesNotInteractive() {
        // Viewer (no onTaskCheckedChange) should NOT expose interactive test tags
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownViewer(
                    markdown = "- [x] done\n- [ ] open",
                )
            }
        }

        // "task-checkbox:0" should not exist (checkboxes are disabled, no testTag)
        rule.onNodeWithTag("task-checkbox:0").assertDoesNotExist()
    }

    @Test
    fun editorPreviewCheckboxClickUpdatesMarkdown() {
        var textValue = "- [x] first\n- [ ] second"
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownEditor(
                    value = textValue,
                    onValueChange = { textValue = it },
                    mode = MarkdownEditorMode.Preview,
                    editLabel = "Edit",
                    previewLabel = "Preview",
                    splitLabel = "Split",
                )
            }
        }

        // The second checkbox (unchecked) should have testTag "task-checkbox:1"
        rule.onNodeWithTag("task-checkbox:1").performClick()

        rule.runOnIdle {
            assertTrue(textValue.contains("- [x] second"), "Expected second checked after click: $textValue")
            assertTrue(textValue.contains("- [x] first"), "Expected first unchanged: $textValue")
        }
    }
}
