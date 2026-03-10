package xyz.junerver.compose.palette.components.notification

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.message.MessageType
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class NotificationUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun notification_shouldRenderTitleAndContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PNotification(
                    visible = true,
                    title = "Deploy finished",
                    content = "Production cluster is healthy",
                    type = MessageType.Success,
                    duration = 0,
                    onClose = {},
                )
            }
        }

        rule.onNodeWithText("Deploy finished").assertTextEquals("Deploy finished")
        rule.onNodeWithText("Production cluster is healthy").assertTextEquals("Production cluster is healthy")
    }

    @Test
    fun notificationState_shouldShowNotificationAfterTriggerButtonClick() {
        rule.setContent {
            PaletteMaterialTheme {
                val state = rememberNotificationState()

                PButton(text = "Show Notification") {
                    state.show(
                        title = "Build warning",
                        content = "One module needs attention",
                        type = MessageType.Warning,
                        duration = 0,
                    )
                }
            }
        }

        rule.onNodeWithText("Show Notification").performClick()

        rule.onNodeWithText("Build warning").assertTextEquals("Build warning")
        rule.onNodeWithText("One module needs attention").assertTextEquals("One module needs attention")
    }

    @Test
    fun notification_shouldSupportTitleOnlyContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PNotification(
                    visible = true,
                    title = "Background task running",
                    content = null,
                    type = MessageType.Info,
                    duration = 0,
                    onClose = {},
                )
            }
        }

        rule.onNodeWithText("Background task running").assertTextEquals("Background task running")
        rule.onAllNodesWithText("Production cluster is healthy").assertCountEquals(0)
    }

    @Test
    fun notificationState_shouldHideNotificationAfterHideCall() {
        lateinit var state: NotificationState

        rule.setContent {
            PaletteMaterialTheme {
                state = rememberNotificationState()

                PButton(text = "Show Closable Notification") {
                    state.show(
                        title = "Sync completed",
                        content = "All files are up to date",
                        type = MessageType.Success,
                        duration = 0,
                    )
                }
            }
        }

        rule.onNodeWithText("Show Closable Notification").performClick()
        rule.onNodeWithText("Sync completed").assertTextEquals("Sync completed")
        rule.onNodeWithText("All files are up to date").assertTextEquals("All files are up to date")

        rule.runOnIdle {
            state.hide()
        }

        rule.waitForIdle()
        rule.onAllNodesWithText("Sync completed").assertCountEquals(0)
        rule.onAllNodesWithText("All files are up to date").assertCountEquals(0)
    }
}
