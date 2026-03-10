package xyz.junerver.compose.palette.components.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PasswordFieldUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun passwordField_shouldRenderPlaceholderAndCount() {
        rule.setContent {
            PaletteMaterialTheme {
                PasswordField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Enter password",
                    showCount = true,
                    maxLength = 12,
                )
            }
        }

        rule.onNodeWithText("Enter password").assertTextEquals("Enter password")
        rule.onNodeWithText("0/12").assertTextEquals("0/12")
        rule.onNodeWithContentDescription("Show password")
    }

    @Test
    fun passwordField_shouldToggleVisibilityDescription() {
        var value by mutableStateOf("secret")

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PasswordField(
                        value = value,
                        onValueChange = { value = it },
                    )
                    Text("Password value: $value")
                }
            }
        }

        rule.onNodeWithContentDescription("Show password").performClick()
        rule.onNodeWithContentDescription("Hide password").performClick()
        rule.onNodeWithText("Password value: secret").assertTextEquals("Password value: secret")
    }
}
