package xyz.junerver.compose.palette.components.toast

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

class ToastUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun toast_shouldRenderVisibleSuccessTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PToast(
                    visible = true,
                    title = "Saved successfully",
                    icon = ToastIcon.SUCCESS,
                    duration = 0,
                    onClose = {},
                )
            }
        }

        rule.onNodeWithText("Saved successfully").assertTextEquals("Saved successfully")
    }

    @Test
    fun toastState_shouldShowToastAfterTriggerButtonClick() {
        rule.setContent {
            PaletteMaterialTheme {
                val state = rememberToastState()

                PButton(text = "Show Toast") {
                    state.show(
                        title = "Uploading",
                        icon = ToastIcon.LOADING,
                        duration = 0,
                    )
                }
            }
        }

        rule.onNodeWithText("Show Toast").performClick()

        rule.onNodeWithText("Uploading").assertTextEquals("Uploading")
    }

    @Test
    fun toast_shouldRenderVisibleTitleWithoutIcon() {
        rule.setContent {
            PaletteMaterialTheme {
                PToast(
                    visible = true,
                    title = "Plain text toast",
                    icon = ToastIcon.NONE,
                    duration = 0,
                    mask = true,
                    onClose = {},
                )
            }
        }

        rule.onNodeWithText("Plain text toast").assertTextEquals("Plain text toast")
    }

    @Test
    fun toastState_shouldHideToastAfterHideCall() {
        lateinit var state: ToastState

        rule.setContent {
            PaletteMaterialTheme {
                state = rememberToastState()

                PButton(text = "Show Persistent Toast") {
                    state.show(
                        title = "Upload queued",
                        icon = ToastIcon.FAIL,
                        duration = 0,
                    )
                }
            }
        }

        rule.onNodeWithText("Show Persistent Toast").performClick()
        rule.onNodeWithText("Upload queued").assertTextEquals("Upload queued")

        rule.runOnIdle {
            state.hide()
        }

        rule.waitForIdle()
        rule.onAllNodesWithText("Upload queued").assertCountEquals(0)
    }
}
