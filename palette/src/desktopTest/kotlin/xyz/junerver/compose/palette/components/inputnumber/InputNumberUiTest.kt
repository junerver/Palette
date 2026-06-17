package xyz.junerver.compose.palette.components.inputnumber

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class InputNumberUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun inputNumber_shouldIncrementAndDecrementWhenStepperButtonsAreClicked() {
        var value by mutableStateOf<Double?>(0.0)

        rule.setContent {
            PaletteMaterialTheme {
                PInputNumber(
                    value = value,
                    onValueChange = { value = it },
                )
            }
        }

        rule.onNodeWithContentDescription("Increase").performClick()
        rule.onNodeWithText("1").assertTextEquals("1")

        rule.onNodeWithContentDescription("Decrease").performClick()
        rule.onNodeWithText("0").assertTextEquals("0")
    }

    @Test
    fun inputNumber_shouldNotIncrementBeyondMaximum() {
        var value by mutableStateOf<Double?>(1.0)

        rule.setContent {
            PaletteMaterialTheme {
                PInputNumber(
                    value = value,
                    onValueChange = { value = it },
                    min = 0.0,
                    max = 1.0,
                )
            }
        }

        rule.onNodeWithContentDescription("Increase").performClick()
        rule.onNodeWithText("1").assertTextEquals("1")
    }
}
