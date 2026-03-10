package xyz.junerver.compose.palette.components.timepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import kotlinx.datetime.LocalTime
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TimePickerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun timePicker_shouldRenderPlaceholderWhenValueIsNull() {
        rule.setContent {
            PaletteMaterialTheme {
                PTimePicker(
                    value = null,
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("HH:mm").assertTextEquals("HH:mm")
    }

    @Test
    fun timePicker_shouldAcceptInputAlignedToMinuteStep() {
        rule.setContent {
            var value by mutableStateOf<LocalTime?>(null)

            PaletteMaterialTheme {
                Column {
                    PTimePicker(
                        value = value,
                        onValueChange = { value = it },
                        minuteStep = 15,
                    )
                    Text("Selected: ${value?.let(::formatTime) ?: "null"}")
                }
            }
        }

        rule.onNode(hasSetTextAction()).performTextInput("10:30")

        rule.onNodeWithText("Selected: 10:30").assertTextEquals("Selected: 10:30")
    }

    @Test
    fun timePicker_shouldIgnoreInputThatBreaksMinuteStepRule() {
        rule.setContent {
            var value by mutableStateOf<LocalTime?>(null)

            PaletteMaterialTheme {
                Column {
                    PTimePicker(
                        value = value,
                        onValueChange = { value = it },
                        minuteStep = 15,
                    )
                    Text("Selected: ${value?.let(::formatTime) ?: "null"}")
                }
            }
        }

        rule.onNode(hasSetTextAction()).performTextInput("10:22")

        rule.onNodeWithText("Selected: null").assertTextEquals("Selected: null")
    }
}
