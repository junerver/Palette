package xyz.junerver.compose.palette.components.chart

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChartLogicTest {
    // region deriveYRange ──────────────────────────────────────────────
    @Test
    fun deriveYRange_emptySeries_returnsSafeUnitSpan() {
        assertEquals(0f to 1f, deriveYRange(emptyList()))
    }

    @Test
    fun deriveYRange_allNonNegative_clampsMinToZero() {
        val range = deriveYRange(listOf(ChartSeries("a", listOf(3f, 7f, 5f))))
        assertEquals(0f, range.first)
        assertEquals(7f, range.second)
    }

    @Test
    fun deriveYRange_negativeValues_keepsActualMin() {
        val range = deriveYRange(listOf(ChartSeries("a", listOf(-4f, 2f))))
        assertEquals(-4f, range.first)
        assertEquals(2f, range.second)
    }

    @Test
    fun deriveYRange_singleValue_hasNonZeroSpan() {
        val range = deriveYRange(listOf(ChartSeries("a", listOf(5f))))
        assertEquals(0f, range.first)
        // Max coerced to min+1 so the span is never zero (division-safe).
        assertEquals(6f, range.second)
    }

    @Test
    fun deriveYRange_overrideAlwaysWins() {
        val range = deriveYRange(listOf(ChartSeries("a", listOf(1f, 2f, 3f))), override = 10f to 100f)
        assertEquals(10f to 100f, range)
    }

    @Test
    fun deriveYRange_spansMultipleSeries() {
        val range = deriveYRange(
            listOf(
                ChartSeries("a", listOf(1f, 5f)),
                ChartSeries("b", listOf(-2f, 9f)),
            ),
        )
        assertEquals(-2f, range.first)
        assertEquals(9f, range.second)
    }
    // endregion ────────────────────────────────────────────────────────

    // region resolveSeriesColor ────────────────────────────────────────
    @Test
    fun resolveSeriesColor_explicitColorWins() {
        val palette = listOf(Color.Red, Color.Green)
        val s = ChartSeries("a", emptyList(), color = Color.Blue)
        assertEquals(Color.Blue, resolveSeriesColor(s, 0, palette, Color.Gray))
    }

    @Test
    fun resolveSeriesColor_unspecifiedCyclesPaletteByIndex() {
        val palette = listOf(Color.Red, Color.Green, Color.Blue)
        val s0 = ChartSeries("a", emptyList())
        val s3 = ChartSeries("d", emptyList())
        assertEquals(Color.Red, resolveSeriesColor(s0, 0, palette, Color.Gray))
        // index 3 wraps to palette[0]
        assertEquals(Color.Red, resolveSeriesColor(s3, 3, palette, Color.Gray))
    }

    @Test
    fun resolveSeriesColor_emptyPaletteFallsBack() {
        val s = ChartSeries("a", emptyList())
        assertEquals(Color.Gray, resolveSeriesColor(s, 0, emptyList(), Color.Gray))
    }
    // endregion ────────────────────────────────────────────────────────

    // region resolveCategories ─────────────────────────────────────────
    @Test
    fun resolveCategories_explicitCategoriesWin() {
        val data = ChartData(listOf(ChartSeries("a", listOf(1f, 2f))), categories = listOf("Q1", "Q2"))
        assertEquals(listOf("Q1", "Q2"), resolveCategories(data))
    }

    @Test
    fun resolveCategories_derivedFromLongestSeries() {
        val data = ChartData(
            listOf(
                ChartSeries("a", listOf(1f, 2f, 3f)),
                ChartSeries("b", listOf(4f)),
            ),
        )
        assertEquals(listOf("1", "2", "3"), resolveCategories(data))
    }

    @Test
    fun resolveCategories_emptyDataReturnsSinglePlaceholder() {
        val data = ChartData(emptyList())
        assertEquals(listOf("1"), resolveCategories(data))
    }
    // endregion ────────────────────────────────────────────────────────

    // region normalizeValue ────────────────────────────────────────────
    @Test
    fun normalizeValue_withinRangeIsLinear() {
        assertEquals(0f, normalizeValue(0f, 0f, 10f))
        assertEquals(1f, normalizeValue(10f, 0f, 10f))
        assertEquals(0.5f, normalizeValue(5f, 0f, 10f))
    }

    @Test
    fun normalizeValue_clampsOutOfRange() {
        assertEquals(0f, normalizeValue(-5f, 0f, 10f))
        assertEquals(1f, normalizeValue(20f, 0f, 10f))
    }

    @Test
    fun normalizeValue_zeroSpanReturnsZero() {
        assertEquals(0f, normalizeValue(5f, 5f, 5f))
    }

    @Test
    fun normalizeValue_negativeRangeWorks() {
        val frac = normalizeValue(0f, -10f, 10f)
        assertTrue(frac > 0.49f && frac < 0.51f)
    }
    // endregion ────────────────────────────────────────────────────────
}
