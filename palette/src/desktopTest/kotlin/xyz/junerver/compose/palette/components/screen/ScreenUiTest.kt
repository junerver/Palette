package xyz.junerver.compose.palette.components.screen

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ScreenUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun screen_shouldRenderToolbarTitleAndContent() {
        rule.setContent {
            PaletteMaterialTheme {
                Screen(title = "Settings") {
                    Text("Screen content")
                }
            }
        }

        rule.onNodeWithText("Settings").assertTextEquals("Settings")
        rule.onNodeWithText("Screen content").assertTextEquals("Screen content")
    }

    @Test
    fun screen_shouldRenderContentWithoutToolbarWhenDisabled() {
        rule.setContent {
            PaletteMaterialTheme {
                Screen(
                    title = "Hidden title",
                    showToolbar = false,
                ) {
                    Text("Toolbar hidden")
                }
            }
        }

        rule.onNodeWithText("Toolbar hidden").assertTextEquals("Toolbar hidden")
    }
}
