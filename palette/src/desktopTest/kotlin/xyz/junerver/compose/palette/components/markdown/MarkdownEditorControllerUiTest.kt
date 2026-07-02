package xyz.junerver.compose.palette.components.markdown

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.segmented.PSegmented
import xyz.junerver.compose.palette.components.segmented.SegmentedOption
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

class MarkdownEditorControllerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun controllerApplyActionUpdatesValue() {
        var latestText = ""
        rule.setContent {
            PaletteMaterialTheme {
                val controller = useMarkdownEditorController(initialValue = TextFieldValue("hello"))
                LaunchedEffect(controller.value.text) {
                    latestText = controller.value.text
                }
                PButton(
                    text = "bold",
                    onClick = { controller.applyAction(MarkdownToolbarAction.Bold) },
                    modifier = Modifier.testTag("bold"),
                )
            }
        }

        rule.onNodeWithTag("bold").performClick()
        rule.runOnIdle {
            assertTrue(latestText.contains("****"), "Expected bold markers inserted: $latestText")
        }
    }

    @Test
    fun controllerUndoRedoRestoresText() {
        var latestText = ""
        var controller: MarkdownEditorController? = null
        rule.setContent {
            PaletteMaterialTheme {
                val currentController = useMarkdownEditorController(initialValue = TextFieldValue("hello"))
                controller = currentController
                LaunchedEffect(currentController.value.text) {
                    latestText = currentController.value.text
                }
                androidx.compose.material3.Text(currentController.value.text, modifier = Modifier.testTag("value"))
            }
        }

        rule.runOnIdle { controller!!.applyAction(MarkdownToolbarAction.Bold) }
        rule.waitUntil(timeoutMillis = 5_000) { latestText.contains("****") }
        rule.runOnIdle { controller!!.undo() }
        rule.waitUntil(timeoutMillis = 5_000) { latestText == "hello" }
        rule.runOnIdle { controller!!.redo() }
        rule.waitUntil(timeoutMillis = 5_000) { latestText.contains("****") }
    }

    @Test
    fun controllerModeSwitchChangesMode() {
        rule.setContent {
            PaletteMaterialTheme {
                val controller = useMarkdownEditorController(initialMode = MarkdownEditorMode.Edit)
                PSegmented(
                    options = listOf(
                        SegmentedOption(MarkdownEditorMode.Edit.name, "Edit"),
                        SegmentedOption(MarkdownEditorMode.Preview.name, "Preview"),
                        SegmentedOption(MarkdownEditorMode.Split.name, "Split"),
                    ),
                    value = controller.mode.name,
                    onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
                )
                androidx.compose.material3.Text(controller.mode.name, modifier = Modifier.testTag("mode"))
            }
        }

        rule.onNodeWithText("Preview").performClick()
        rule.onNodeWithTag("mode").assertTextContains(MarkdownEditorMode.Preview.name)
    }
}
