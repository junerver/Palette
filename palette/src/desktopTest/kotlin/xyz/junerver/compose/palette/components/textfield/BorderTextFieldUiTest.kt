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

class BorderTextFieldUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun borderTextField_shouldRenderPlaceholderPrefixSuffixAndCount() {
        rule.setContent {
            PaletteMaterialTheme {
                BorderTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Enter email",
                    prefix = "@",
                    suffix = ".com",
                    showCount = true,
                    maxLength = 20,
                )
            }
        }

        rule.onNodeWithText("Enter email").assertTextEquals("Enter email")
        rule.onNodeWithText("@").assertTextEquals("@")
        rule.onNodeWithText(".com").assertTextEquals(".com")
        rule.onNodeWithText("0/20").assertTextEquals("0/20")
    }

    @Test
    fun borderTextField_shouldClearValueWhenClearButtonClicked() {
        var value by mutableStateOf("hello")

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    BorderTextField(
                        value = value,
                        onValueChange = { value = it },
                        clearable = true,
                    )
                    Text("Value: $value")
                }
            }
        }

        rule.onNodeWithText("Value: hello").assertTextEquals("Value: hello")
        rule.onNodeWithContentDescription("Clear").performClick()
        rule.onNodeWithText("Value: ").assertTextEquals("Value: ")
    }
}
