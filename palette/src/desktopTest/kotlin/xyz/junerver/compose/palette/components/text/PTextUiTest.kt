package xyz.junerver.compose.palette.components.text

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PTextUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pText_shouldRenderProvidedText() {
        rule.setContent {
            PaletteMaterialTheme {
                PText(text = "Palette Typography")
            }
        }

        rule.onNodeWithText("Palette Typography").assertTextEquals("Palette Typography")
    }

    @Test
    fun pText_shouldRenderSingleLineText() {
        rule.setContent {
            PaletteMaterialTheme {
                PText(
                    text = "Compact content",
                    maxLines = 1,
                )
            }
        }

        rule.onNodeWithText("Compact content").assertTextEquals("Compact content")
    }
}
