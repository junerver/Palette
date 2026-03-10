package xyz.junerver.compose.palette.components.datepicker

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
import kotlinx.datetime.LocalDate
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class DatePickerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun datePicker_shouldRenderPlaceholderWhenValueIsNull() {
        rule.setContent {
            PaletteMaterialTheme {
                PDatePicker(
                    value = null,
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("YYYY-MM-DD").assertTextEquals("YYYY-MM-DD")
    }

    @Test
    fun datePicker_shouldAcceptValidInputWithinRange() {
        rule.setContent {
            var value by mutableStateOf<LocalDate?>(null)

            PaletteMaterialTheme {
                Column {
                    PDatePicker(
                        value = value,
                        onValueChange = { value = it },
                        minDate = LocalDate(2026, 1, 1),
                        maxDate = LocalDate(2026, 12, 31),
                    )
                    Text("Selected: ${value?.toString() ?: "null"}")
                }
            }
        }

        rule.onNode(hasSetTextAction()).performTextInput("2026-03-10")

        rule.onNodeWithText("Selected: 2026-03-10").assertTextEquals("Selected: 2026-03-10")
    }

    @Test
    fun datePicker_shouldIgnoreInputOutsideAllowedRange() {
        rule.setContent {
            var value by mutableStateOf<LocalDate?>(null)

            PaletteMaterialTheme {
                Column {
                    PDatePicker(
                        value = value,
                        onValueChange = { value = it },
                        minDate = LocalDate(2026, 1, 1),
                        maxDate = LocalDate(2026, 12, 31),
                    )
                    Text("Selected: ${value?.toString() ?: "null"}")
                }
            }
        }

        rule.onNode(hasSetTextAction()).performTextInput("2025-12-31")

        rule.onNodeWithText("Selected: null").assertTextEquals("Selected: null")
    }
}
