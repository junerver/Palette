package xyz.junerver.compose.palette.components.inputnumber

import kotlin.test.Test
import kotlin.test.assertEquals

class InputNumberLogicTest {
    @Test
    fun nextInputNumberValue_incrementsFromCurrentValue() {
        assertEquals(
            expected = 2.0,
            actual = nextInputNumberValue(
                value = 1.0,
                direction = 1,
                min = Double.NEGATIVE_INFINITY,
                max = Double.POSITIVE_INFINITY,
                step = 1.0,
                precision = 0,
            ),
        )
    }

    @Test
    fun nextInputNumberValue_decrementsAndRespectsMinimum() {
        assertEquals(
            expected = 0.0,
            actual = nextInputNumberValue(
                value = 0.0,
                direction = -1,
                min = 0.0,
                max = 10.0,
                step = 1.0,
                precision = 0,
            ),
        )
    }

    @Test
    fun nextInputNumberValue_appliesStepAndPrecision() {
        assertEquals(
            expected = 1.5,
            actual = nextInputNumberValue(
                value = 1.0,
                direction = 1,
                min = 0.0,
                max = 10.0,
                step = 0.5,
                precision = 1,
            ),
        )
    }

    @Test
    fun nextInputNumberValue_startsFromMinimumWhenValueIsNullAndIncrementing() {
        assertEquals(
            expected = 1.0,
            actual = nextInputNumberValue(
                value = null,
                direction = 1,
                min = 0.0,
                max = 10.0,
                step = 1.0,
                precision = 0,
            ),
        )
    }
}
