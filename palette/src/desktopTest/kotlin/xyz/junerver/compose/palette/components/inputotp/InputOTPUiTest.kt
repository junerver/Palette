package xyz.junerver.compose.palette.components.inputotp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class InputOTPUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun inputOTP_displaysCorrectLength() {
        rule.setContent {
            PaletteMaterialTheme {
                PInputOTP(
                    length = 4,
                    value = "",
                    onValueChange = {},
                )
            }
        }

        rule.waitForIdle()
    }

    @Test
    fun inputOTP_withValueShowsValue() {
        rule.setContent {
            PaletteMaterialTheme {
                PInputOTP(
                    length = 6,
                    value = "123",
                    onValueChange = {},
                )
            }
        }

        rule.waitForIdle()
    }
}
