package xyz.junerver.compose.palette.components.datepicker

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePickerBusinessFlowTest {
    @Test
    fun scheduleFilterFlow_shouldAcceptBoundaryDates() {
        val min = LocalDate(2026, 3, 1)
        val max = LocalDate(2026, 3, 31)

        assertTrue(isDateWithinRange(LocalDate(2026, 3, 1), min, max))
        assertTrue(isDateWithinRange(LocalDate(2026, 3, 31), min, max))
    }

    @Test
    fun scheduleFilterFlow_shouldRejectDateOutsideConfiguredWindow() {
        val min = LocalDate(2026, 3, 1)
        val max = LocalDate(2026, 3, 31)

        assertFalse(isDateWithinRange(LocalDate(2026, 2, 28), min, max))
        assertFalse(isDateWithinRange(LocalDate(2026, 4, 1), min, max))
    }

    @Test
    fun scheduleFilterFlow_shouldParseIsoDateForSubmission() {
        assertEquals(LocalDate(2026, 12, 5), parseDateOrNull("2026-12-05"))
    }
}
