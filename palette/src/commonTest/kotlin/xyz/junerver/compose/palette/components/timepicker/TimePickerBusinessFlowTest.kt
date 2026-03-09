package xyz.junerver.compose.palette.components.timepicker

import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimePickerBusinessFlowTest {
    @Test
    fun appointmentFlow_shouldParseValidTimeAndKeepFormattingStable() {
        val time = parseTimeOrNull("08:05")

        assertEquals(LocalTime(8, 5), time)
        assertEquals("08:05", formatTime(requireNotNull(time)))
    }

    @Test
    fun appointmentFlow_shouldRejectMinuteStepViolations() {
        assertTrue(isTimeStepAligned(LocalTime(9, 30), minuteStep = 15))
        assertFalse(isTimeStepAligned(LocalTime(9, 10), minuteStep = 15))
    }

    @Test
    fun appointmentFlow_shouldAllowAnyMinuteWhenStepDisabled() {
        assertTrue(isTimeStepAligned(LocalTime(9, 7), minuteStep = 0))
    }
}
