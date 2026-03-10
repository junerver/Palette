package xyz.junerver.compose.palette.components.datetimerange

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateTimeRangeLogicTest {
    @Test
    fun parseDateTimeRangeOrNull_whenValid_shouldReturnRange() {
        val range = parseDateTimeRangeOrNull("2026-03-01 09:00 - 2026-03-05 18:30")

        assertEquals(
            DateTimeRange(
                startDate = LocalDate(2026, 3, 1),
                startTime = LocalTime(9, 0),
                endDate = LocalDate(2026, 3, 5),
                endTime = LocalTime(18, 30),
            ),
            range,
        )
    }

    @Test
    fun parseDateTimeRangeOrNull_whenInvalid_shouldReturnNull() {
        assertNull(parseDateTimeRangeOrNull("2026/03/01 09:00 - 2026-03-05 18:30"))
        assertNull(parseDateTimeRangeOrNull("invalid"))
    }

    @Test
    fun parseDateTimeRangeOrNull_whenEndBeforeStart_shouldReturnNull() {
        assertNull(parseDateTimeRangeOrNull("2026-03-10 11:00 - 2026-03-10 10:00"))
    }

    @Test
    fun isValidRangeOrder_shouldCheckStartBeforeEnd() {
        val startDate = LocalDate(2026, 3, 1)
        val endDate = LocalDate(2026, 3, 1)

        assertTrue(
            isValidRangeOrder(
                startDate = startDate,
                startTime = LocalTime(9, 0),
                endDate = endDate,
                endTime = LocalTime(9, 1),
            ),
        )
        assertFalse(
            isValidRangeOrder(
                startDate = startDate,
                startTime = LocalTime(10, 0),
                endDate = endDate,
                endTime = LocalTime(9, 59),
            ),
        )
    }

    @Test
    fun isValidRangeOrder_whenSameMoment_shouldReturnTrue() {
        assertTrue(
            isValidRangeOrder(
                startDate = LocalDate(2026, 3, 10),
                startTime = LocalTime(10, 0),
                endDate = LocalDate(2026, 3, 10),
                endTime = LocalTime(10, 0),
            )
        )
    }

    @Test
    fun formatDateTimeRange_shouldOutputStablePattern() {
        val text =
            formatDateTimeRange(
                DateTimeRange(
                    startDate = LocalDate(2026, 3, 1),
                    startTime = LocalTime(9, 0),
                    endDate = LocalDate(2026, 3, 5),
                    endTime = LocalTime(18, 30),
                ),
            )

        assertEquals("2026-03-01 09:00 - 2026-03-05 18:30", text)
    }
}
