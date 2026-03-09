package xyz.junerver.compose.palette.components.empty

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class EmptyUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun empty_shouldRenderLocalizedDefaults() {
        rule.setContent {
            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PEmpty()
            }
        }

        rule.onNodeWithText("No Data").assertTextEquals("No Data")
        rule.onNodeWithText("The current list is empty").assertTextEquals("The current list is empty")
    }
}
