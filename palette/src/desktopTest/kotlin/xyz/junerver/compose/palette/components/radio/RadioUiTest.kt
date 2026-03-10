package xyz.junerver.compose.palette.components.radio

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class RadioUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun radio_shouldRenderLabelDescriptionAndInvokeClick() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PRadio(
                    label = "Email",
                    description = "Notify me about product updates",
                    checked = false,
                    onClick = { clicks++ },
                )
            }
        }

        rule.onNodeWithText("Email").assertTextEquals("Email", "Notify me about product updates")
        rule.onNodeWithText("Email").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun radioGroup_shouldUpdateSelectedValueAfterClick() {
        val options = listOf(
            RadioOption(label = "Starter", value = "starter", description = "For personal projects"),
            RadioOption(label = "Team", value = "team", description = "For growing teams"),
        )

        rule.setContent {
            var selected by mutableStateOf("starter")

            PaletteMaterialTheme {
                PRadioGroup(
                    options = options,
                    value = selected,
                    onChange = { selected = it },
                )
                Text("Selected: $selected")
            }
        }

        rule.onNodeWithText("Selected: starter").assertTextEquals("Selected: starter")
        rule.onNodeWithText("Team").performClick()
        rule.onNodeWithText("Selected: team").assertTextEquals("Selected: team")
    }
}
