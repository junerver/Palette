package xyz.junerver.compose.palette.components.textfield

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TextAreaUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun textArea_shouldRenderPlaceholderAndCount() {
        rule.setContent {
            PaletteMaterialTheme {
                TextArea(
                    value = "",
                    onValueChange = {},
                    placeholder = "Write summary",
                    showCount = true,
                    maxLength = 50,
                )
            }
        }

        rule.onNodeWithText("Write summary").assertTextEquals("Write summary")
        rule.onNodeWithText("0/50").assertTextEquals("0/50")
    }
}
