package xyz.junerver.compose.palette.components.toolbar

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ToolbarUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun toolbar_shouldRenderTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                Toolbar(title = "Page Title")
            }
        }

        rule.onNodeWithText("Page Title").assertTextEquals("Page Title")
    }
}
