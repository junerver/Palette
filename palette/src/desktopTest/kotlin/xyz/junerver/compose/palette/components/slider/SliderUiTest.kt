package xyz.junerver.compose.palette.components.slider

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SliderUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun slider_shouldRenderFormattedValueLabel() {
        rule.setContent {
            PaletteMaterialTheme {
                PSlider(
                    value = 75f,
                    onChange = {},
                    formatter = { "${it.toInt()}%" },
                )
            }
        }

        rule.onNodeWithText("75%").assertTextEquals("75%")
    }

    @Test
    fun slider_shouldRenderCustomRangeValueLabel() {
        rule.setContent {
            PaletteMaterialTheme {
                PSlider(
                    value = 25f,
                    onChange = {},
                    range = 0f..50f,
                    formatter = { "${it.toInt()} / 50" },
                )
            }
        }

        rule.onNodeWithText("25 / 50").assertTextEquals("25 / 50")
    }
}
