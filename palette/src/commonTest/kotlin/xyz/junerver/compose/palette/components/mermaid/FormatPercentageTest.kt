package xyz.junerver.compose.palette.components.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Regression coverage for the pie-chart legend percentage formatting.
 *
 * The previous implementation used `"%.1f%%".format(...)` which relies on `java.lang.String.format`
 * and is unavailable on the Kotlin/Native (iOS) target, breaking `:palette:compileKotlinIosSimulatorArm64`.
 * `formatPercentage` replaces it with plain arithmetic so the code is platform-agnostic.
 */
class FormatPercentageTest {
    @Test
    fun formatsWholePercentWithoutSpuriousDecimals() {
        assertEquals("50.0%", formatPercentage(50.0))
        assertEquals("100.0%", formatPercentage(100.0))
    }

    @Test
    fun roundsToOneDecimalPlace() {
        assertEquals("33.3%", formatPercentage(33.33333))
        assertEquals("66.7%", formatPercentage(66.66666))
        assertEquals("12.5%", formatPercentage(12.5))
    }

    @Test
    fun handlesZeroAndSmallFractions() {
        assertEquals("0.0%", formatPercentage(0.0))
        assertEquals("0.1%", formatPercentage(0.1))
    }

    @Test
    fun roundsHalfUp() {
        // 0.05 → 0.1 (round half up)
        assertEquals("0.1%", formatPercentage(0.05))
        // 12.35 → 12.4
        assertEquals("12.4%", formatPercentage(12.35))
    }

    @Test
    fun outputAlwaysHasPercentSuffixAndSingleDecimalPoint() {
        // Sanity: output always ends with '%' and contains a single decimal point.
        listOf(0.0, 1.0, 33.3, 99.9, 100.0, 0.05).forEach { value ->
            val result = formatPercentage(value)
            assertTrue(result.endsWith("%"), "Expected '%' suffix for $value, got $result")
            assertTrue(result.count { it == '.' } == 1, "Expected single decimal point for $value, got $result")
        }
    }
}

