package xyz.junerver.compose.palette.components.toolbar

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun toolbar_shouldRenderActionSlot() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PToolbar(
                    title = "Page Title",
                    actions = {
                        PButton(text = "Save") {
                            clicks++
                        }
                    }
                )
            }
        }

        rule.onNodeWithText("Save").performClick()

        assertEquals(1, clicks)
    }
}
