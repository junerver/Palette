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

    @Test
    fun deriveYRange_stacked_usesPerCategorySums() {
        // Q2 sums to 440 — the stacked bar's real max. A non-stacked range would cap at 240 and clip.
        val range = deriveYRange(
            listOf(
                ChartSeries("2024", listOf(120f, 200f, 150f, 80f)),
                ChartSeries("2025", listOf(90f, 240f, 180f, 110f)),
            ),
            stacked = true,
        )
        assertEquals(0f, range.first)
        assertEquals(440f, range.second)
    }

    @Test
    fun deriveYRange_stacked_emptySeries_returnsSafeUnitSpan() {
        assertEquals(0f to 1f, deriveYRange(emptyList(), stacked = true))
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

    // region pieSliceGeometry ──────────────────────────────────────────
    // The donut hole is cleared by a center circle, so a donut slice must STILL be a full wedge —
    // otherwise drawArc renders an arc+chord segment whose chord cuts through slices >180°.
    @Test
    fun pieSliceGeometry_alwaysUsesCenterForFullWedge() {
        // Even the largest slice (>180°) must useCenter = true.
        val bigSlice = pieSliceGeometry(index = 0, value = 55f, startAngle = 0f, total = 100f)
        assertTrue(bigSlice.useCenter)
        // A donut slice is no different — geometry does not depend on donut/pie.
        val smallSlice = pieSliceGeometry(index = 0, value = 5f, startAngle = 0f, total = 100f)
        assertTrue(smallSlice.useCenter)
    }

    @Test
    fun pieSliceGeometry_sweepIsProportionalShareOf360() {
        // 55 / 100 → 198°
        val g = pieSliceGeometry(index = 0, value = 55f, startAngle = 0f, total = 100f)
        assertEquals(0f, g.startAngle)
        assertTrue(g.sweepAngle > 197.9f && g.sweepAngle < 198.1f)
    }

    @Test
    fun pieSliceGeometry_startAngleIsPassedThrough() {
        val g = pieSliceGeometry(index = 2, value = 15f, startAngle = 198f, total = 100f)
        assertEquals(198f, g.startAngle)
    }
    // endregion ────────────────────────────────────────────────────────

    // region barLayout ─────────────────────────────────────────────────
    @Test
    fun barLayout_grouped_splitsSlotAcrossSeries() {
        // 1 category, 2 series: each bar fills half of the 70% group fraction.
        val g0 = barLayout(catCount = 1, catIndex = 0, seriesCount = 2, sIndex = 0, value = 3f, accValue = 0f, yMin = 0f, yMax = 10f, stacked = false)
        val g1 = barLayout(catCount = 1, catIndex = 0, seriesCount = 2, sIndex = 1, value = 7f, accValue = 0f, yMin = 0f, yMax = 10f, stacked = false)
        // value 3/10 → 0.3 extent; 7/10 → 0.7
        assertTrue(g0.extent > 0.29f && g0.extent < 0.31f)
        assertTrue(g1.extent > 0.69f && g1.extent < 0.71f)
        // bars sit side by side, both fully inside the slot
        assertTrue(g0.crossCenter < 0.5f)
        assertTrue(g1.crossCenter > 0.5f)
    }

    @Test
    fun barLayout_stacked_segmentsStackAlongValueAxis() {
        // Q2 of demo: 200 then 240 on top, yMax = 440.
        val seg0 = barLayout(catCount = 4, catIndex = 1, seriesCount = 2, sIndex = 0, value = 200f, accValue = 0f, yMin = 0f, yMax = 440f, stacked = true)
        val seg1 = barLayout(catCount = 4, catIndex = 1, seriesCount = 2, sIndex = 1, value = 240f, accValue = 200f, yMin = 0f, yMax = 440f, stacked = true)
        // seg0 occupies [0, 200/440], seg1 occupies [200/440, 1] → adjacent, no overlap/gap.
        assertEquals(seg0.start + seg0.extent, seg1.start, 0.001f)
        assertEquals(1f, seg1.start + seg1.extent, 0.001f)
        // both segments share the same slot center (full group width).
        assertEquals(seg0.crossCenter, seg1.crossCenter, 0.001f)
    }

    @Test
    fun barLayout_horizontalAndVerticalShareSameLayout() {
        // The layout function is orientation-agnostic, so horizontal bars must NOT be positioned
        // along the width axis (the old bug). The same call must produce identical geometry for both
        // orientations — the renderer just maps fractions to different canvas axes.
        val vertical = barLayout(catCount = 5, catIndex = 2, seriesCount = 1, sIndex = 0, value = 180f, accValue = 0f, yMin = 0f, yMax = 300f, stacked = false)
        // crossCenter for 5 categories, index 2 → slot 2 center = (2 + 0.5)/5 = 0.5
        assertTrue(vertical.crossCenter > 0.49f && vertical.crossCenter < 0.51f)
    }
    // endregion ────────────────────────────────────────────────────────

    // region niceTicks ─────────────────────────────────────────────────
    @Test
    fun niceTicks_normalRange_snapTo125() {
        // 0..100, count 4 → step 20 → ticks 0,20,40,60,80,100
        val ticks = niceTicks(0f, 100f, count = 4)
        assertTrue(ticks.isNotEmpty())
        assertEquals(0f, ticks.first())
        assertEquals(100f, ticks.last())
        // Every tick is a multiple of the (20) step — no ugly 17/34 values.
        ticks.forEach { assertTrue(it % 20f < 0.001f || (20f - it % 20f) % 20f < 0.001f, "tick $it not on 20-grid") }
    }

    @Test
    fun niceTicks_zeroSpan_returnsSingleZeroTick() {
        assertEquals(listOf(0f), niceTicks(5f, 5f))
    }

    @Test
    fun niceTicks_negativeRange_includesNegatives() {
        val ticks = niceTicks(-40f, 60f, count = 4)
        assertTrue(ticks.first() <= -40f)
        assertTrue(ticks.last() >= 60f)
        // 0 should appear as a tick on a symmetric-ish range.
        assertTrue(ticks.any { it > -0.001f && it < 0.001f })
    }

    @Test
    fun niceTicks_emptyFloat_returnsEmpty() {
        assertTrue(niceTicks(Float.NaN, 100f).isEmpty())
    }
    // endregion ────────────────────────────────────────────────────────

    // region formatTickValue ───────────────────────────────────────────
    @Test
    fun formatTickValue_integer_noDecimalNoUnit() {
        assertEquals("120", formatTickValue(120f))
    }

    @Test
    fun formatTickValue_integerWithUnit() {
        assertEquals("120%", formatTickValue(120f, unit = "%"))
        assertEquals("1.5k", formatTickValue(1.5f, unit = "k"))
    }

    @Test
    fun formatTickValue_decimalTrimsTrailingZero() {
        // 50.0 → "50" (not "50.0")
        assertEquals("50", formatTickValue(50f))
        // 2.5 → "2.5"
        assertEquals("2.5", formatTickValue(2.5f))
    }

    @Test
    fun formatTickValue_zero() {
        assertEquals("0", formatTickValue(0f))
    }

    @Test
    fun formatTickValue_negative() {
        assertEquals("-40", formatTickValue(-40f))
    }
    // endregion ────────────────────────────────────────────────────────
}
