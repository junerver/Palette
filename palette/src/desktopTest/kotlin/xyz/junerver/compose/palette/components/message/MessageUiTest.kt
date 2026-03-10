package xyz.junerver.compose.palette.components.message

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

class MessageUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun message_shouldRenderVisibleWarningText() {
        rule.setContent {
            PaletteMaterialTheme {
                PMessage(
                    visible = true,
                    text = "Storage is almost full",
                    type = MessageType.Warning,
                    duration = 0,
                    onClose = {},
                )
            }
        }

        rule.onNodeWithText("Storage is almost full").assertTextEquals("Storage is almost full")
    }

    @Test
    fun messageState_shouldShowMessageAfterTriggerButtonClick() {
        rule.setContent {
            PaletteMaterialTheme {
                val state = rememberMessageState()

                PButton(text = "Show Message") {
                    state.show(
                        text = "Profile updated",
                        type = MessageType.Success,
                        duration = 0,
                    )
                }
            }
        }

        rule.onNodeWithText("Show Message").performClick()

        rule.onNodeWithText("Profile updated").assertTextEquals("Profile updated")
    }

    @Test
    fun messageState_shouldHideMessageAfterHideCall() {
        lateinit var state: MessageState

        rule.setContent {
            PaletteMaterialTheme {
                state = rememberMessageState()

                PButton(text = "Show Persistent Message") {
                    state.show(
                        text = "Connection restored",
                        type = MessageType.Success,
                        duration = 0,
                    )
                }
            }
        }

        rule.onNodeWithText("Show Persistent Message").performClick()
        rule.onNodeWithText("Connection restored").assertTextEquals("Connection restored")

        rule.runOnIdle {
            state.hide()
        }

        rule.waitForIdle()
        rule.onAllNodesWithText("Connection restored").assertCountEquals(0)
    }
}
