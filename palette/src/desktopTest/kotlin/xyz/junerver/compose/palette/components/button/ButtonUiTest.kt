package xyz.junerver.compose.palette.components.button

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class ButtonUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun button_shouldInvokeClickWhenEnabled() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PButton(
                    text = "Submit",
                    onClick = { clicks++ },
                )
            }
        }

        rule.onNodeWithText("Submit").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun button_shouldIgnoreClickWhenLoading() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PButton(
                    text = "Saving",
                    loading = true,
                    onClick = { clicks++ },
                )
            }
        }

        rule.onNodeWithText("Saving").assertTextEquals("Saving")
        rule.onNodeWithText("Saving").performClick()

        assertEquals(0, clicks)
    }
}
