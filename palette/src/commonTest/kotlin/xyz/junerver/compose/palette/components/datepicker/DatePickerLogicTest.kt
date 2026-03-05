package xyz.junerver.compose.palette.components.datepicker

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DatePickerLogicTest {
    @Test
    fun parseDateOrNull_whenValidIso_shouldReturnDate() {
        val date = parseDateOrNull("2026-03-05")
        assertEquals(LocalDate(2026, 3, 5), date)
    }

    @Test
    fun parseDateOrNull_whenInvalid_shouldReturnNull() {
        assertNull(parseDateOrNull("2026/03/05"))
        assertNull(parseDateOrNull("bad-date"))
    }

    @Test
    fun isDateWithinRange_shouldValidateMinAndMax() {
        val date = LocalDate(2026, 3, 5)
        val min = LocalDate(2026, 3, 1)
        val max = LocalDate(2026, 3, 31)

        assertTrue(isDateWithinRange(date, min, max))
        assertFalse(isDateWithinRange(LocalDate(2026, 2, 28), min, max))
        assertFalse(isDateWithinRange(LocalDate(2026, 4, 1), min, max))
    }
}
