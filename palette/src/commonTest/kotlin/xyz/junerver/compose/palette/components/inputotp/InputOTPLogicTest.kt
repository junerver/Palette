package xyz.junerver.compose.palette.components.inputotp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InputOTPLogicTest {
    @Test
    fun currentInputOtpIndexPointsToNextEmptyCell() {
        assertEquals(0, currentInputOtpIndex(valueLength = 0, length = 4))
        assertEquals(2, currentInputOtpIndex(valueLength = 2, length = 4))
    }

    @Test
    fun currentInputOtpIndexClampsToLastCellWhenValueIsFullOrLonger() {
        assertEquals(3, currentInputOtpIndex(valueLength = 4, length = 4))
        assertEquals(3, currentInputOtpIndex(valueLength = 8, length = 4))
    }

    @Test
    fun currentInputOtpIndexReturnsNullForEmptyOrInvalidLength() {
        assertNull(currentInputOtpIndex(valueLength = 0, length = 0))
        assertNull(currentInputOtpIndex(valueLength = 0, length = -1))
    }

    @Test
    fun filterInputOtpValueKeepsOnlyDigitsAndLimitsLength() {
        assertEquals("123", filterInputOtpValue(value = "1a2 3b4", length = 3))
        assertEquals("12", filterInputOtpValue(value = "12", length = 4))
    }

    @Test
    fun filterInputOtpValueHandlesInvalidLengthAsEmpty() {
        assertEquals("", filterInputOtpValue(value = "1234", length = 0))
        assertEquals("", filterInputOtpValue(value = "1234", length = -1))
    }

    @Test
    fun inputOtpDefaultsUseReadableCursorBlinkDuration() {
        assertTrue(InputOTPDefaults.CursorBlinkDurationMillis >= 1000)
    }
}
