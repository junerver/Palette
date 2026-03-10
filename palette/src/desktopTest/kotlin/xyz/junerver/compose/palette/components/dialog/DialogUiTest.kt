package xyz.junerver.compose.palette.components.dialog

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class DialogUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun dialog_shouldRenderTitleContentAndInvokeOk() {
        var okClicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PDialog(
                    title = "Delete Record",
                    content = "This action cannot be undone.",
                    okText = "Delete",
                    onOk = { okClicks++ },
                    onDismiss = {},
                )
            }
        }

        rule.onNodeWithText("Delete Record").assertTextEquals("Delete Record")
        rule.onNodeWithText("This action cannot be undone.").assertTextEquals("This action cannot be undone.")
        rule.onNodeWithText("Delete").performClick()

        assertEquals(1, okClicks)
    }

    @Test
    fun dialog_shouldInvokeCancelWhenCancelActionIsProvided() {
        var cancelClicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PDialog(
                    title = "Close Session",
                    cancelText = "Keep Editing",
                    onOk = {},
                    onCancel = { cancelClicks++ },
                    onDismiss = {},
                )
            }
        }

        rule.onNodeWithText("Keep Editing").performClick()

        assertEquals(1, cancelClicks)
    }

    @Test
    fun dialogState_shouldShowDialogAfterTriggerButtonClick() {
        rule.setContent {
            PaletteMaterialTheme {
                val dialogState = rememberDialogState()

                PButton(text = "Open Dialog") {
                    dialogState.show(
                        title = "Publish Changes",
                        content = "Ready to release this version.",
                        okText = "Publish",
                    )
                }
            }
        }

        rule.onNodeWithText("Open Dialog").performClick()

        rule.onNodeWithText("Publish Changes").assertTextEquals("Publish Changes")
        rule.onNodeWithText("Ready to release this version.").assertTextEquals("Ready to release this version.")
        rule.onNodeWithText("Publish").assertTextEquals("Publish")
    }

    @Test
    fun dialogState_shouldRemainVisibleWhenCloseOnActionIsFalse() {
        var okClicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                val dialogState = rememberDialogState()

                PButton(text = "Open Sticky Dialog") {
                    dialogState.show(
                        title = "Keep Open",
                        content = "Dialog should stay visible after ok",
                        okText = "Acknowledge",
                        closeOnAction = false,
                        onOk = { okClicks++ },
                    )
                }
            }
        }

        rule.onNodeWithText("Open Sticky Dialog").performClick()
        rule.onNodeWithText("Acknowledge").performClick()

        assertEquals(1, okClicks)
        rule.onNodeWithText("Keep Open").assertTextEquals("Keep Open")
        rule.onNodeWithText("Dialog should stay visible after ok").assertTextEquals("Dialog should stay visible after ok")
        rule.onAllNodesWithText("Acknowledge").assertCountEquals(1)
    }
}
