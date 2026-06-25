package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple
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

        // Toggle second item (unchecked → checked)
        val result1 = toggleTaskCheckbox(markdown, 1)
        assertTrue(result1.contains("- [x] second"), "Expected second checked: $result1")
        assertTrue(result1.contains("- [x] first"), "Expected first unchanged: $result1")
        assertTrue(result1.contains("- [x] third"), "Expected third unchanged: $result1")

        // Toggle second item back (checked → unchecked)
        val result1b = toggleTaskCheckbox(result1, 1)
        assertTrue(result1b.contains("- [ ] second"), "Expected second unchecked: $result1b")

        // Toggle first item (checked → unchecked)
        val result2 = toggleTaskCheckbox(markdown, 0)
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
        val textValue = androidx.compose.runtime.mutableStateOf("- [x] first\n- [ ] second")
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownEditor(
                    value = textValue.value,
                    onValueChange = { textValue.value = it },
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
            assertTrue(textValue.value.contains("- [x] second"), "Expected second checked: ${textValue.value}")
            assertTrue(textValue.value.contains("- [x] first"), "Expected first unchanged: ${textValue.value}")
        }
    }

    @Test
    fun editorPreviewCheckboxMultipleClicks() {
        val textValue = androidx.compose.runtime.mutableStateOf("- [x] first\n- [ ] second")
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownEditor(
                    value = textValue.value,
                    onValueChange = { textValue.value = it },
                    mode = MarkdownEditorMode.Preview,
                    editLabel = "Edit",
                    previewLabel = "Preview",
                    splitLabel = "Split",
                )
            }
        }

        // First click: check "second"
        rule.onNodeWithTag("task-checkbox:1").performClick()
        rule.waitForIdle()
        assertTrue(textValue.value.contains("- [x] second"), "After 1st click: ${textValue.value}")

        // Second click: uncheck "second"
        rule.onNodeWithTag("task-checkbox:1").performClick()
        rule.waitForIdle()
        assertTrue(textValue.value.contains("- [ ] second"), "After 2nd click: ${textValue.value}")
    }

    @Test
    fun clickableMultipleClicks() {
        var count = 0
        rule.setContent {
            Box(
                modifier = Modifier
                    .testTag("clickable")
                    .clickableWithoutRipple { count++ }
            ) {
                Text(text = "Count: $count")
            }
        }

        rule.onNodeWithTag("clickable").performClick()
        rule.waitForIdle()
        assertEquals(1, count, "After 1st click")

        rule.onNodeWithTag("clickable").performClick()
        rule.waitForIdle()
        assertEquals(2, count, "After 2nd click")
    }

    @Test
    fun clickableNestedMultipleClicks() {
        // Test clickable deep inside a composable tree (simulates editor/viewer)
        var checked = false
        rule.setContent {
            Column {
                Text("Header")
                Row {
                    Box(modifier = Modifier.testTag("spacer").size(10.dp))
                    Box(
                        modifier = Modifier
                            .testTag("deep-clickable")
                            .clickableWithoutRipple { checked = !checked }
                    ) {
                        Text(text = if (checked) "ON" else "OFF")
                    }
                }
                Text("Footer")
            }
        }

        rule.onNodeWithTag("deep-clickable").performClick()
        rule.waitForIdle()
        assertEquals(true, checked, "After 1st click")

        rule.onNodeWithTag("deep-clickable").performClick()
        rule.waitForIdle()
        assertEquals(false, checked, "After 2nd click")
    }

    @Test
    fun clickableWithDrawBehindChildMultipleClicks() {
        // Test: does Box(drawBehind) child break clickable multi-click?
        var checked = false
        rule.setContent {
            Box(
                modifier = Modifier
                    .testTag("cb")
                    .clickableWithoutRipple { checked = !checked }
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = if (checked) Color(0xFF6200EE) else Color.Gray,
                                cornerRadius = CornerRadius(4.dp.toPx()),
                                style = Stroke(2.dp.toPx())
                            )
                        }
                )
            }
        }

        rule.onNodeWithTag("cb").performClick()
        rule.waitForIdle()
        assertEquals(true, checked, "After 1st click")

        rule.onNodeWithTag("cb").performClick()
        rule.waitForIdle()
        assertEquals(false, checked, "After 2nd click")
    }

    @Test
    fun editorPreviewCheckboxMultipleClicksWithSimpleCheckbox() {
        // Test: does the issue reproduce with a simplified ColoredCheckBox (no animations)?
        var textValue = "- [x] first\n- [ ] second"
        rule.setContent {
            PaletteMaterialTheme {
                // Simulate editor preview with a simple clickable checkbox
                Column {
                    val items = textValue.lines().map { line ->
                        val checked = line.startsWith("- [x]")
                        val text = line.removePrefix("- [x] ").removePrefix("- [ ] ")
                        checked to text
                    }
                    items.forEachIndexed { index, (checked, text) ->
                        Row {
                            Box(
                                modifier = Modifier
                                    .testTag("task-checkbox:$index")
                                    .size(32.dp)
                                    .clickableWithoutRipple {
                                        textValue = textValue.lines().mapIndexed { i, line ->
                                            if (i == index) {
                                                if (line.startsWith("- [x]")) line.replace("- [x]", "- [ ]")
                                                else line.replace("- [ ]", "- [x]")
                                            } else line
                                        }.joinToString("\n")
                                    }
                            ) {
                                Text(text = if (checked) "☑" else "☐")
                            }
                            Text(text = text)
                        }
                    }
                }
            }
        }

        rule.onNodeWithTag("task-checkbox:1").performClick()
        rule.waitForIdle()
        assertTrue(textValue.contains("- [x] second"), "After 1st click: $textValue")

        rule.onNodeWithTag("task-checkbox:1").performClick()
        rule.waitForIdle()
        assertTrue(textValue.contains("- [ ] second"), "After 2nd click: $textValue")
    }

    @Test
    fun toggleableMultipleClicksWork() {
        // Minimal repro: a single toggleable that recomposes on click
        var count = 0
        rule.setContent {
            val checked = count % 2 == 1
            Box(
                modifier = Modifier
                    .testTag("toggle")
                    .toggleable(
                        value = checked,
                        onValueChange = { count++ },
                    )
            ) {
                Text(text = if (checked) "ON" else "OFF")
            }
        }

        rule.onNodeWithTag("toggle").performClick()
        rule.waitForIdle()
        assertEquals(1, count, "After 1st click")

        rule.onNodeWithTag("toggle").performClick()
        rule.waitForIdle()
        assertEquals(2, count, "After 2nd click")
    }

    @Test
    fun textToggleableMultipleClicks() {
        // Exact same as toggleableMultipleClicksWork but with state = Boolean
        var checked = false
        rule.setContent {
            Box(
                modifier = Modifier
                    .testTag("cb")
                    .toggleable(
                        value = checked,
                        onValueChange = { checked = !checked },
                    )
            ) {
                Text(text = if (checked) "ON" else "OFF")
            }
        }

        rule.onNodeWithTag("cb").performClick()
        rule.waitForIdle()
        assertEquals(true, checked, "After 1st click")

        rule.onNodeWithTag("cb").performClick()
        rule.waitForIdle()
        assertEquals(false, checked, "After 2nd click")
    }
}
