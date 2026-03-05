package xyz.junerver.compose.palette.components.timepicker

import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimePickerLogicTest {
    @Test
    fun parseTimeOrNull_whenValidHm_shouldReturnTime() {
        val time = parseTimeOrNull("09:30")
        assertEquals(LocalTime(9, 30), time)
    }

    @Test
    fun parseTimeOrNull_whenInvalid_shouldReturnNull() {
        assertNull(parseTimeOrNull("9:30"))
        assertNull(parseTimeOrNull("24:00"))
        assertNull(parseTimeOrNull("bad"))
    }

    @Test
    fun formatTime_shouldAlwaysPadTwoDigits() {
        assertEquals("09:03", formatTime(LocalTime(9, 3)))
        assertEquals("23:59", formatTime(LocalTime(23, 59)))
    }

    @Test
    fun isTimeStepAligned_shouldValidateMinuteStep() {
        assertTrue(isTimeStepAligned(LocalTime(10, 30), minuteStep = 15))
        assertTrue(isTimeStepAligned(LocalTime(10, 0), minuteStep = 30))
        kotlin.test.assertFalse(isTimeStepAligned(LocalTime(10, 10), minuteStep = 15))
    }
}
