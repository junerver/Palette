package xyz.junerver.compose.palette.components.toggle

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToggleUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun toggle_pressedStateShowsContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PToggle(
                    pressed = true,
                    onPressedChange = {},
                    content = { Text("Toggle") },
                )
            }
        }

        rule.onNodeWithText("Toggle").assertIsDisplayed()
    }

    @Test
    fun toggle_unpressedStateShowsContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PToggle(
                    pressed = false,
                    onPressedChange = {},
                    content = { Text("Toggle") },
                )
            }
        }

        rule.onNodeWithText("Toggle").assertIsDisplayed()
    }

    @Test
    fun toggle_clickTogglesState() {
        var pressed = false

        rule.setContent {
            PaletteMaterialTheme {
                PToggle(
                    pressed = pressed,
                    onPressedChange = { pressed = it },
                    content = { Text("Toggle") },
                )
            }
        }

        rule.onNodeWithText("Toggle").performClick()
        assertTrue(pressed)
    }

    @Test
    fun toggle_disabledNotClickable() {
        var pressed = false

        rule.setContent {
            PaletteMaterialTheme {
                PToggle(
                    pressed = pressed,
                    onPressedChange = { pressed = it },
                    disabled = true,
                    content = { Text("Toggle") },
                )
            }
        }

        rule.onNodeWithText("Toggle").performClick()
        assertFalse(pressed)
    }
}
