package xyz.junerver.compose.palette.components.datetimerange

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
import kotlinx.datetime.LocalTime
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class DateTimeRangeUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun dateTimeRange_shouldRenderPlaceholderWhenValueIsNull() {
        rule.setContent {
            PaletteMaterialTheme {
                PDateTimeRange(
                    value = null,
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("YYYY-MM-DD HH:mm - YYYY-MM-DD HH:mm")
            .assertTextEquals("YYYY-MM-DD HH:mm - YYYY-MM-DD HH:mm")
    }

    @Test
    fun dateTimeRange_shouldAcceptValidInputAndUpdateHostState() {
        rule.setContent {
            var value by mutableStateOf<DateTimeRange?>(null)

            PaletteMaterialTheme {
                Column {
                    PDateTimeRange(
                        value = value,
                        onValueChange = { value = it },
                    )
                    Text(
                        text = "Host: ${value?.let(::formatDateTimeRange) ?: "null"}"
                    )
                }
            }
        }

        rule.onNode(hasSetTextAction()).performTextInput("2026-03-10 10:00 - 2026-03-10 11:00")

        rule.onNodeWithText("Host: 2026-03-10 10:00 - 2026-03-10 11:00")
            .assertTextEquals("Host: 2026-03-10 10:00 - 2026-03-10 11:00")
    }

    @Test
    fun dateTimeRange_shouldRejectInvalidRangeOrder() {
        rule.setContent {
            var value by mutableStateOf<DateTimeRange?>(
                DateTimeRange(
                    startDate = LocalDate(2026, 3, 10),
                    startTime = LocalTime(10, 0),
                    endDate = LocalDate(2026, 3, 10),
                    endTime = LocalTime(11, 0),
                )
            )

            PaletteMaterialTheme {
                Column {
                    PDateTimeRange(
                        value = value,
                        onValueChange = { value = it },
                    )
                    Text(text = "Host: ${value?.let(::formatDateTimeRange) ?: "null"}")
                }
            }
        }

        // end earlier than start -> parse returns null
        rule.onNode(hasSetTextAction()).performTextInput("2026-03-10 11:00 - 2026-03-10 10:00")

        rule.onNodeWithText("Host: null").assertTextEquals("Host: null")
    }
}
