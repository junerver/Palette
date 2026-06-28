package xyz.junerver.compose.palette.components.daterangepicker

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateRangePickerLogicTest {

    // ---------------- normalizeRange ----------------

    @Test
    fun normalizeRange_whenEndBeforeStartAndAllowSwap_shouldSwap() {
        val range = DateRange(start = LocalDate(2026, 3, 10), end = LocalDate(2026, 3, 1))
        val normalized = normalizeRange(range, allowSwap = true)
        assertEquals(LocalDate(2026, 3, 1), normalized.start)
        assertEquals(LocalDate(2026, 3, 10), normalized.end)
    }

    @Test
    fun normalizeRange_whenEndBeforeStartAndDisallowSwap_shouldKeep() {
        val range = DateRange(start = LocalDate(2026, 3, 10), end = LocalDate(2026, 3, 1))
        val normalized = normalizeRange(range, allowSwap = false)
        assertEquals(LocalDate(2026, 3, 10), normalized.start)
        assertEquals(LocalDate(2026, 3, 1), normalized.end)
    }

    @Test
    fun normalizeRange_whenOrdered_shouldKeep() {
        val range = DateRange(start = LocalDate(2026, 3, 1), end = LocalDate(2026, 3, 10))
        val normalized = normalizeRange(range, allowSwap = true)
        assertEquals(range, normalized)
    }

    @Test
    fun normalizeRange_whenPartial_shouldReturnAsIs() {
        val range = DateRange(start = LocalDate(2026, 3, 1), end = null)
        assertEquals(range, normalizeRange(range))
    }

    @Test
    fun normalizeRange_whenEmpty_shouldReturnEmpty() {
        assertEquals(DateRange.Empty, normalizeRange(DateRange.Empty))
    }

    // ---------------- isDateDisabled ----------------

    @Test
    fun isDateDisabled_whenOutsideMax_shouldReturnTrue() {
        val min = LocalDate(2026, 3, 1)
        val max = LocalDate(2026, 3, 31)
        assertTrue(isDateDisabled(LocalDate(2026, 4, 1), min, max, null))
    }

    @Test
    fun isDateDisabled_whenOutsideMin_shouldReturnTrue() {
        val min = LocalDate(2026, 3, 1)
        assertTrue(isDateDisabled(LocalDate(2026, 2, 28), min, null, null))
    }

    @Test
    fun isDateDisabled_whenInsideRange_shouldReturnFalse() {
        val min = LocalDate(2026, 3, 1)
        val max = LocalDate(2026, 3, 31)
        assertFalse(isDateDisabled(LocalDate(2026, 3, 15), min, max, null))
    }

    @Test
    fun isDateDisabled_whenCallbackReturnsTrue_shouldReturnTrue() {
        val weekend = { d: LocalDate ->
            d.dayOfWeek == DayOfWeek.SATURDAY || d.dayOfWeek == DayOfWeek.SUNDAY
        }
        // 2026-03-07 是周六
        assertTrue(isDateDisabled(LocalDate(2026, 3, 7), null, null, weekend))
    }

    @Test
    fun isDateDisabled_whenNoConstraints_shouldReturnFalse() {
        assertFalse(isDateDisabled(LocalDate(2026, 3, 15), null, null, null))
    }

    @Test
    fun isDateDisabled_whenOnBoundary_shouldReturnFalse() {
        val min = LocalDate(2026, 3, 1)
        val max = LocalDate(2026, 3, 31)
        assertFalse(isDateDisabled(min, min, max, null))
        assertFalse(isDateDisabled(max, min, max, null))
    }

    // ---------------- isWithinMaxSpan ----------------

    @Test
    fun isWithinMaxSpan_whenWithin_shouldReturnTrue() {
        val start = LocalDate(2026, 3, 1)
        val end = LocalDate(2026, 3, 7)
        assertTrue(isWithinMaxSpan(start, end, maxSpanDays = 7))
    }

    @Test
    fun isWithinMaxSpan_whenExceeds_shouldReturnFalse() {
        val start = LocalDate(2026, 3, 1)
        val end = LocalDate(2026, 3, 10)
        assertFalse(isWithinMaxSpan(start, end, maxSpanDays = 7))
    }

    @Test
    fun isWithinMaxSpan_whenNull_shouldReturnTrue() {
        assertTrue(isWithinMaxSpan(LocalDate(2026, 3, 1), LocalDate(2026, 12, 31), maxSpanDays = null))
    }

    @Test
    fun isWithinMaxSpan_whenSameDay_shouldReturnTrue() {
        val day = LocalDate(2026, 3, 1)
        assertTrue(isWithinMaxSpan(day, day, maxSpanDays = 0))
    }

    // ---------------- clampHoverEnd / isHoverClickable ----------------

    @Test
    fun clampHoverEnd_whenExceedsMax_shouldClamp() {
        val start = LocalDate(2026, 3, 1)
        val hover = LocalDate(2026, 3, 20)
        assertEquals(LocalDate(2026, 3, 8), clampHoverEnd(start, hover, maxSpanDays = 7))
    }

    @Test
    fun clampHoverEnd_whenBeforeStart_shouldClampToStart() {
        val start = LocalDate(2026, 3, 10)
        val hover = LocalDate(2026, 3, 5)
        assertEquals(start, clampHoverEnd(start, hover, maxSpanDays = 7))
    }

    @Test
    fun clampHoverEnd_whenWithinMax_shouldReturnHover() {
        val start = LocalDate(2026, 3, 1)
        val hover = LocalDate(2026, 3, 5)
        assertEquals(hover, clampHoverEnd(start, hover, maxSpanDays = 7))
    }

    @Test
    fun isHoverClickable_whenExceedsMax_shouldReturnFalse() {
        val start = LocalDate(2026, 3, 1)
        assertFalse(isHoverClickable(start, LocalDate(2026, 3, 20), maxSpanDays = 7))
    }

    @Test
    fun isHoverClickable_whenNoStart_shouldReturnTrue() {
        assertTrue(isHoverClickable(null, LocalDate(2026, 3, 5), maxSpanDays = 7))
    }

    // ---------------- formatDateRange ----------------

    @Test
    fun formatDateRange_whenEmpty_shouldReturnEmpty() {
        assertEquals("", formatDateRange(DateRange.Empty, null, null))
    }

    @Test
    fun formatDateRange_whenPartial_shouldReturnStartOnly() {
        val range = DateRange(start = LocalDate(2026, 3, 1), end = null)
        assertEquals("2026-03-01", formatDateRange(range, null, null))
    }

    @Test
    fun formatDateRange_whenComplete_shouldFormatBothEnds() {
        val range = DateRange(start = LocalDate(2026, 3, 1), end = LocalDate(2026, 3, 5))
        assertEquals("2026-03-01 - 2026-03-05", formatDateRange(range, null, null))
    }

    @Test
    fun formatDateRange_whenWithTime_shouldAppendTime() {
        val range = DateRange(start = LocalDate(2026, 3, 1), end = LocalDate(2026, 3, 5))
        val text = formatDateRange(range, LocalTime(9, 0), LocalTime(18, 30))
        assertEquals("2026-03-01 09:00 - 2026-03-05 18:30", text)
    }

    @Test
    fun formatDateRange_whenCrossYear_shouldFormatCorrectly() {
        val range = DateRange(start = LocalDate(2025, 12, 30), end = LocalDate(2026, 1, 2))
        assertEquals("2025-12-30 - 2026-01-02", formatDateRange(range, null, null))
    }

    // ---------------- parseDateRangeOrNull ----------------

    @Test
    fun parseDateRangeOrNull_whenValid_shouldReturnRange() {
        val range = parseDateRangeOrNull("2026-03-01 - 2026-03-05", expectTime = false)
        assertEquals(DateRange(LocalDate(2026, 3, 1), LocalDate(2026, 3, 5)), range)
    }

    @Test
    fun parseDateRangeOrNull_whenEmpty_shouldReturnEmpty() {
        assertEquals(DateRange.Empty, parseDateRangeOrNull("", expectTime = false))
    }

    @Test
    fun parseDateRangeOrNull_whenInvalidFormat_shouldReturnNull() {
        assertNull(parseDateRangeOrNull("2026/03/01 - 2026-03-05", expectTime = false))
        assertNull(parseDateRangeOrNull("invalid", expectTime = false))
        assertNull(parseDateRangeOrNull("2026-03-01", expectTime = false))
    }

    @Test
    fun parseDateRangeOrNull_whenWithTime_shouldParseBoth() {
        val range = parseDateRangeOrNull("2026-03-01 09:00 - 2026-03-05 18:30", expectTime = true)
        assertEquals(DateRange(LocalDate(2026, 3, 1), LocalDate(2026, 3, 5)), range)
    }

    @Test
    fun parseDateRangeOrNull_whenExpectTimeButMissingTime_shouldReturnNull() {
        assertNull(parseDateRangeOrNull("2026-03-01 - 2026-03-05", expectTime = true))
    }

    @Test
    fun parseDateRangeOrNull_whenEndBeforeStart_shouldStillParse() {
        // 顺序由 normalizeRange 处理，parse 阶段不拦截
        val range = parseDateRangeOrNull("2026-03-10 - 2026-03-01", expectTime = false)
        assertEquals(DateRange(LocalDate(2026, 3, 10), LocalDate(2026, 3, 1)), range)
    }

    // ---------------- 边界：闰年 ----------------

    @Test
    fun parseDateRangeOrNull_whenLeapYearFeb29_shouldParse() {
        val range = parseDateRangeOrNull("2024-02-29 - 2024-03-01", expectTime = false)
        assertEquals(DateRange(LocalDate(2024, 2, 29), LocalDate(2024, 3, 1)), range)
    }

    @Test
    fun parseDateRangeOrNull_whenNonLeapYearFeb29_shouldReturnNull() {
        assertNull(parseDateRangeOrNull("2025-02-29 - 2025-03-01", expectTime = false))
    }

    // ---------------- DateRange 模型辅助属性 ----------------

    @Test
    fun dateRange_isComplete_isPartial_isEmpty_shouldBeConsistent() {
        assertTrue(DateRange.Empty.isEmpty)
        assertFalse(DateRange.Empty.isPartial)
        assertFalse(DateRange.Empty.isComplete)

        val partial = DateRange(start = LocalDate(2026, 3, 1), end = null)
        assertFalse(partial.isEmpty)
        assertTrue(partial.isPartial)
        assertFalse(partial.isComplete)

        val complete = DateRange(start = LocalDate(2026, 3, 1), end = LocalDate(2026, 3, 5))
        assertFalse(complete.isEmpty)
        assertFalse(complete.isPartial)
        assertTrue(complete.isComplete)
    }
}
