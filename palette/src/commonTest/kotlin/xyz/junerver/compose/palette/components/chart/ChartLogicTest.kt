package xyz.junerver.compose.palette.components.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
    // endregion ────────────────────────────────────────────────────────────────

    // region hitTestPoint (cartesian: bar / line) ─────────────────────────────
    private val plot = PlotRect(left = 50f, top = 10f, width = 400f, height = 200f)
    private val cats = listOf("Q1", "Q2", "Q3", "Q4")

    @Test
    fun hitTestPoint_verticalBar_returnsOwningCategory() {
        // 4 categories over 400px starting at x=50 → slots at x=100,200,300,400 centers.
        val s = ChartSeries("a", listOf(10f, 20f, 30f, 40f))
        val t = hitTestPoint(Offset(310f, 100f), listOf(s), 0f, 40f, cats, plot, listOf(Color.Red))
        assertNotNull(t)
        assertEquals(2, t.categoryIndex) // slot 2 (Q3)
        assertEquals("Q3", t.category)
        assertEquals(30f, t.entries.first().value)
    }

    @Test
    fun hitTestPoint_horizontalBar_swapsToYAxis() {
        // Horizontal: category axis is Y. slot 0 occupies y=[10,60), slot 3 y=[160,210).
        val s = ChartSeries("a", listOf(10f, 20f, 30f, 40f))
        val t = hitTestPoint(Offset(200f, 180f), listOf(s), 0f, 40f, cats, plot, listOf(Color.Red), horizontal = true)
        assertNotNull(t)
        assertEquals(3, t.categoryIndex)
        assertEquals(40f, t.entries.first().value)
    }

    @Test
    fun hitTestPoint_stackedBar_returnsCumulativeValue() {
        val series = listOf(
            ChartSeries("2024", listOf(100f, 200f)),
            ChartSeries("2025", listOf(50f, 60f)),
        )
        val t = hitTestPoint(Offset(125f, 100f), series, 0f, 260f, listOf("Q1", "Q2"), plot, listOf(Color.Red, Color.Blue), stacked = true)
        assertNotNull(t)
        assertEquals(0, t.categoryIndex)
        // Stacked: entries list both series; cumulative = 100 + 50 = 150.
        assertEquals(2, t.entries.size)
        assertEquals(150f, t.entries.sumOf { it.value.toDouble() }.toFloat())
        assertEquals(HitGeometry.Bar, t.geometryKind)
    }

    @Test
    fun hitTestPoint_multiSeries_collectsAllEntriesAtCategory() {
        // The core fix: hovering a category returns an entry PER visible series, not just the first.
        val series = listOf(
            ChartSeries("a", listOf(10f, 20f)),
            ChartSeries("b", listOf(5f, 15f)),
        )
        val t = hitTestPoint(Offset(300f, 100f), series, 0f, 20f, listOf("Q1", "Q2"), plot, listOf(Color.Red, Color.Blue))
        assertNotNull(t)
        assertEquals(1, t.categoryIndex) // Q2
        assertEquals(2, t.entries.size)
        assertEquals(20f, t.entries[0].value) // series "a" at Q2
        assertEquals(15f, t.entries[1].value) // series "b" at Q2
        assertEquals("a", t.entries[0].label)
        assertEquals("b", t.entries[1].label)
    }

    @Test
    fun hitTestPoint_outsidePlot_returnsNull() {
        val s = ChartSeries("a", listOf(1f, 2f, 3f, 4f))
        // Left gutter (axis margin area)
        assertNull(hitTestPoint(Offset(20f, 100f), listOf(s), 0f, 4f, cats, plot, listOf(Color.Red)))
        // Right gutter
        assertNull(hitTestPoint(Offset(480f, 100f), listOf(s), 0f, 4f, cats, plot, listOf(Color.Red)))
        // Above plot
        assertNull(hitTestPoint(Offset(200f, 5f), listOf(s), 0f, 4f, cats, plot, listOf(Color.Red)))
    }

    @Test
    fun hitTestPoint_lineChart_snapsToNearestPointWithinRadius() {
        val s = ChartSeries("v", listOf(0f, 40f, 0f, 40f))
        // With yMax=40 the point for Q2 sits at the top (y ≈ 10). Cursor near it should snap.
        val t = hitTestPoint(Offset(205f, 12f), listOf(s), 0f, 40f, cats, plot, listOf(Color.Red), pointRadiusPx = 15f)
        assertNotNull(t)
        assertEquals(1, t.primaryCategoryIndex) // snapped to Q2's marker
        assertEquals(40f, t.entries.first().value)
        assertEquals(HitGeometry.Point, t.geometryKind)
    }

    @Test
    fun hitTestPoint_emptySeries_returnsNull() {
        assertNull(hitTestPoint(Offset(200f, 100f), emptyList(), 0f, 1f, cats, plot, emptyList()))
    }

    @Test
    fun hitTestPoint_emptyCategories_returnsNull() {
        val s = ChartSeries("a", listOf(1f))
        assertNull(hitTestPoint(Offset(200f, 100f), listOf(s), 0f, 1f, emptyList(), plot, listOf(Color.Red)))
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region hitTestPie ────────────────────────────────────────────────────────
    private val pieColors = listOf(Color.Red, Color.Green)

    @Test
    fun hitTestPie_insideSlice_returnsThatSlice() {
        // Two equal slices: 0..180 and 180..360 (start at 3 o'clock, clockwise).
        val center = Offset(100f, 100f)
        val t = hitTestPie(Offset(150f, 100f), center, radius = 80f, holeRadius = 0f, 0f, listOf(50f, 50f), listOf("A", "B"), pieColors, "Share")
        assertNotNull(t)
        assertEquals(0, t.primaryCategoryIndex)
        assertEquals(50f, t.entries.first().value)
        assertEquals("A", t.category)
        assertEquals(HitGeometry.Slice, t.geometryKind)
    }

    @Test
    fun hitTestPie_secondSlice_whenInLowerHalf() {
        val center = Offset(100f, 100f)
        // Slice 1 spans 180°..360° (upper-left half in screen coords). Point dx=-60, dy=-10 → ~189°.
        val t = hitTestPie(Offset(40f, 90f), center, 80f, 0f, 0f, listOf(50f, 50f), listOf("A", "B"), pieColors, "Share")
        assertNotNull(t)
        assertEquals(1, t.primaryCategoryIndex)
    }

    @Test
    fun hitTestPie_outsideCircle_returnsNull() {
        val center = Offset(100f, 100f)
        assertNull(hitTestPie(Offset(200f, 100f), center, 80f, 0f, 0f, listOf(50f, 50f), listOf("A", "B"), pieColors, "Share"))
    }

    @Test
    fun hitTestPie_insideDonutHole_returnsNull() {
        val center = Offset(100f, 100f)
        assertNull(hitTestPie(Offset(105f, 105f), center, 80f, 40f, 0f, listOf(50f, 50f), listOf("A", "B"), pieColors, "Share"))
    }

    @Test
    fun hitTestPie_respectsStartAngle() {
        // Start at 90° (12 o'clock). First slice (50%) now spans 90..270.
        val center = Offset(100f, 100f)
        val t = hitTestPie(Offset(100f, 150f), center, 80f, 0f, 90f, listOf(50f, 50f), listOf("A", "B"), pieColors, "Share")
        assertNotNull(t)
        assertEquals(0, t.primaryCategoryIndex)
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Entrance animation progress math (P3-A) ──────────────────────────
    // These verify the SCALE MATH the renderers apply from `entrance.value`; the Animatable itself
    // is a Compose-Runtime concern (its 0→1 spring is exercised by the desktop UI tests).

    @Test
    fun entrance_barScalesValueByProgress() {
        // A bar of value 10 over [0,10] at progress 0.5 → extent 0.5 (half-grown).
        val g0 = barLayout(1, 0, 1, 0, value = 10f * 0.5f, accValue = 0f, yMin = 0f, yMax = 10f, stacked = false)
        assertTrue(g0.extent > 0.49f && g0.extent < 0.51f)
        // At progress 0 → fully collapsed (extent 0).
        val g1 = barLayout(1, 0, 1, 0, value = 10f * 0f, accValue = 0f, yMin = 0f, yMax = 10f, stacked = false)
        assertEquals(0f, g1.extent)
    }

    @Test
    fun entrance_stackedBarScalesBothAccAndValue() {
        // Stacked column 200+240 over [0,440]. At progress 0.5 both halves scale → total 220/440.
        val seg0 = barLayout(1, 0, 2, 0, value = 200f * 0.5f, accValue = 0f, yMin = 0f, yMax = 440f, stacked = true)
        val seg1 = barLayout(1, 0, 2, 1, value = 240f * 0.5f, accValue = 200f * 0.5f, yMin = 0f, yMax = 440f, stacked = true)
        // Top of the stack at half progress = (100 + 120)/440 = 0.5.
        val topFrac = seg1.start + seg1.extent
        assertTrue(topFrac > 0.49f && topFrac < 0.51f)
    }

    @Test
    fun entrance_lineLiftsFromBaseline() {
        // The renderer lifts each value as yMin + (v - yMin) * progress. At progress 0 every point
        // normalizes to 0 (sits at the baseline); at progress 1 it reaches its true fraction.
        val yMin = 0f
        val yMax = 40f
        val v = 40f
        val liftedAt0 = yMin + (v - yMin) * 0f
        val liftedAt1 = yMin + (v - yMin) * 1f
        assertEquals(0f, normalizeValue(liftedAt0, yMin, yMax))
        assertEquals(1f, normalizeValue(liftedAt1, yMin, yMax))
    }

    @Test
    fun entrance_pieSweepWindowDoesNotOvershoot() {
        // Sweep-in budget = progress * 360. With 2 equal slices (180° each) at progress 0.25 the
        // budget is 90° → only the first half of slice 0 is visible.
        val progress = 0.25f
        val budget = progress * 360f
        val sliceSweep = 180f
        val slice0Start = 0f
        val slice0End = minOf(slice0Start + sliceSweep, budget)
        val visibleSweep0 = (slice0End - slice0Start).coerceAtLeast(0f)
        assertEquals(90f, visibleSweep0, 0.001f)
        // Slice 1 not yet reached.
        val slice1Start = minOf(slice0Start + sliceSweep, budget)
        val slice1End = minOf(slice1Start + sliceSweep, budget)
        val visibleSweep1 = (slice1End - slice1Start).coerceAtLeast(0f)
        assertEquals(0f, visibleSweep1, 0.001f)
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Scatter (P4-A) ────────────────────────────────────────────────────
    @Test
    fun scatterPairs_parsesFlatValuesIntoPairs() {
        // [x0,y0,x1,y1,x2,y2] → 3 pairs.
        val pairs = scatterPairs(listOf(1f, 2f, 3f, 4f, 5f, 6f))
        assertEquals(listOf(1f to 2f, 3f to 4f, 5f to 6f), pairs)
    }

    @Test
    fun scatterPairs_dropsTrailingOddValue() {
        assertEquals(listOf(1f to 2f), scatterPairs(listOf(1f, 2f, 3f)))
    }

    @Test
    fun scatterPairs_emptyOrSingle_returnsEmpty() {
        assertTrue(scatterPairs(emptyList()).isEmpty())
        assertTrue(scatterPairs(listOf(1f)).isEmpty())
    }

    @Test
    fun scatterBounds_acrossMultipleSeries() {
        val bounds = scatterBounds(
            listOf(
                ChartSeries("a", listOf(0f, 10f, 5f, 20f)),
                ChartSeries("b", listOf(10f, 5f, 15f, 30f)),
            ),
        )
        assertNotNull(bounds)
        val (xRange, yRange) = bounds
        val (xMin, xMax) = xRange
        val (yMin, yMax) = yRange
        assertEquals(0f, xMin)
        assertEquals(15f, xMax)
        assertEquals(5f, yMin)
        assertEquals(30f, yMax)
    }

    @Test
    fun scatterBounds_emptyReturnsNull() {
        assertNull(scatterBounds(emptyList()))
    }

    @Test
    fun scatterBounds_zeroSpanHasSafeUnitRange() {
        // All points share one X → xMax coerced to xMin+1 so the plot isn't degenerate.
        val bounds = scatterBounds(listOf(ChartSeries("a", listOf(5f, 1f, 5f, 2f))))
        assertNotNull(bounds)
        val (xMin, xMax) = bounds!!.first
        assertEquals(5f, xMin)
        assertEquals(6f, xMax)
    }

    @Test
    fun hitTestScatter_findsNearestPointWithinRadius() {
        val plot = PlotRect(0f, 0f, 100f, 100f)
        // One point at (x=0,y=0) → bottom-left; another at (x=1,y=1) → top-right.
        val series = listOf(ChartSeries("a", listOf(0f, 0f, 1f, 1f)))
        // Cursor near top-right (≈ x=100,y=0) should hit the (1,1) point.
        val t = hitTestScatter(Offset(92f, 8f), series, 0f to 1f, 0f to 1f, plot, radiusPx = 15f, seriesColors = listOf(Color.Red))
        assertNotNull(t)
        assertEquals(1, t.primaryCategoryIndex) // second pair
        assertEquals(1f, t.entries.first().value) // y of the hit point
    }

    @Test
    fun hitTestScatter_outsideRadiusReturnsNull() {
        val plot = PlotRect(0f, 0f, 100f, 100f)
        val series = listOf(ChartSeries("a", listOf(0f, 0f)))
        assertNull(hitTestScatter(Offset(90f, 90f), series, 0f to 1f, 0f to 1f, plot, radiusPx = 5f, seriesColors = listOf(Color.Red)))
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region resolveTooltipEntries (multi-series tooltip collection) ───────────
    @Test
    fun resolveTooltipEntries_collectsAllSeriesAtCategory() {
        val series = listOf(
            ChartSeries("a", listOf(10f, 20f, 30f)),
            ChartSeries("b", listOf(5f, 15f, 25f)),
        )
        val entries = resolveTooltipEntries(series, catIndex = 1, seriesColors = listOf(Color.Red, Color.Blue))
        assertEquals(2, entries.size)
        assertEquals(20f, entries[0].value)
        assertEquals(15f, entries[1].value)
        assertEquals(Color.Red, entries[0].color)
        assertEquals("a", entries[0].label)
    }

    @Test
    fun resolveTooltipEntries_skipsSeriesMissingValueAtCategory() {
        // Series "b" has only 1 value; at catIndex 2 it's missing → skipped, only "a" appears.
        val series = listOf(
            ChartSeries("a", listOf(1f, 2f, 3f)),
            ChartSeries("b", listOf(10f)),
        )
        val entries = resolveTooltipEntries(series, catIndex = 2, seriesColors = listOf(Color.Red, Color.Blue))
        assertEquals(1, entries.size)
        assertEquals(3f, entries[0].value)
        assertEquals("a", entries[0].label)
    }

    @Test
    fun resolveTooltipEntries_assignsYAxisIndex() {
        val series = listOf(
            ChartSeries("left", listOf(1f), yAxisIndex = 0),
            ChartSeries("right", listOf(100f), yAxisIndex = 1),
        )
        val entries = resolveTooltipEntries(series, catIndex = 0, seriesColors = listOf(Color.Red, Color.Blue), yAxisBySeries = listOf(0, 1))
        assertEquals(2, entries.size)
        assertEquals(0, entries[0].yAxisIndex)
        assertEquals(1, entries[1].yAxisIndex)
    }

    @Test
    fun resolveTooltipEntries_clampsAxisIndexToZeroOne() {
        val series = listOf(ChartSeries("x", listOf(1f)))
        val entries = resolveTooltipEntries(series, 0, listOf(Color.Red), yAxisBySeries = listOf(5))
        assertEquals(1, entries[0].yAxisIndex.coerceAtMost(1))
    }

    @Test
    fun resolveTooltipEntries_negativeCatIndexReturnsEmpty() {
        val series = listOf(ChartSeries("a", listOf(1f)))
        assertTrue(resolveTooltipEntries(series, catIndex = -1, seriesColors = listOf(Color.Red)).isEmpty())
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Radar geometry (P4-B) ─────────────────────────────────────────────
    @Test
    fun radarAxisAngle_firstAxisPointsUp() {
        // Axis 0 of N sits at -90° (straight up). Vertex should be directly above the center.
        val v = radarVertex(Offset(100f, 100f), radius = 50f, axisIndex = 0, axisCount = 4, fraction = 1f)
        assertTrue(v.x > 99f && v.x < 101f)
        assertTrue(v.y > 49f && v.y < 51f)
    }

    @Test
    fun radarAxisAngle_fourAxesQuarterTurn() {
        // 4 axes → 90° apart. Axis 1 points right, axis 2 down.
        val right = radarVertex(Offset(100f, 100f), 50f, 1, 4, 1f)
        assertTrue(right.x > 140f)
        assertTrue(right.y > 99f && right.y < 101f)
        val down = radarVertex(Offset(100f, 100f), 50f, 2, 4, 1f)
        assertTrue(down.y > 140f)
    }

    @Test
    fun radarVertex_fractionScalesRadius() {
        val center = Offset(100f, 100f)
        assertEquals(center, radarVertex(center, 50f, 0, 4, 0f))
        val atHalf = radarVertex(center, 50f, 1, 4, 0.5f) // axis 1 = right
        assertTrue(atHalf.x > 124f && atHalf.x < 126f) // 100 + 25
    }

    @Test
    fun radarValueRange_clampsMinToZero() {
        val (min, max) = radarValueRange(listOf(ChartSeries("a", listOf(3f, 7f, 5f))))
        assertEquals(0f, min)
        assertEquals(7f, max)
    }

    @Test
    fun radarValueRange_overrideWins() {
        assertEquals(0f to 100f, radarValueRange(listOf(ChartSeries("a", listOf(1f, 2f))), override = 0f to 100f))
    }

    @Test
    fun radarValueRange_emptySeriesSafe() {
        assertEquals(0f to 1f, radarValueRange(emptyList()))
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Dual Y-axis (P5-B) ────────────────────────────────────────────────
    @Test
    fun deriveDualYRanges_noRightAxis_returnsNullRight() {
        val (left, right) = deriveDualYRanges(listOf(ChartSeries("a", listOf(1f, 5f, 3f))))
        assertEquals(0f to 5f, left)
        assertNull(right)
    }

    @Test
    fun deriveDualYRanges_splitsSeriesByAxisIndex() {
        // Series A (axis 0): 1..10. Series B (axis 1): 100..1000. Each axis derives its own range.
        val series = listOf(
            ChartSeries("a", listOf(1f, 10f), yAxisIndex = 0),
            ChartSeries("b", listOf(100f, 1000f), yAxisIndex = 1),
        )
        val (left, right) = deriveDualYRanges(series)
        assertEquals(0f to 10f, left)
        assertNotNull(right)
        assertEquals(0f to 1000f, right)
    }

    @Test
    fun deriveDualYRanges_clampsAxisIndexToZeroOne() {
        // yAxisIndex out of range clamps to 0/1 — a value of 5 → axis 1, -3 → axis 0.
        val series = listOf(
            ChartSeries("a", listOf(1f, 2f), yAxisIndex = -3),
            ChartSeries("b", listOf(50f, 80f), yAxisIndex = 5),
        )
        val (left, right) = deriveDualYRanges(series)
        assertEquals(0f to 2f, left)
        assertNotNull(right)
        assertEquals(0f to 80f, right)
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Data-zoom slice (P6-A) ────────────────────────────────────────────
    private fun zoomSlice(series: List<Float>, cats: List<String>, start: Float, end: Float): ChartData =
        applyZoomSlice(ChartData(series = listOf(ChartSeries("a", series)), categories = cats), start to end)

    @Test
    fun applyZoomSlice_fullRangeReturnsDataUnchanged() {
        val data = ChartData(listOf(ChartSeries("a", listOf(1f, 2f, 3f))), listOf("A", "B", "C"))
        assertEquals(data, applyZoomSlice(data, null))
        assertEquals(data, applyZoomSlice(data, 0f to 1f))
    }

    @Test
    fun applyZoomSlice_trimsToWindow() {
        // 4 categories, window 0.25..0.75 → indices 1..3 (categories B,C; values 2,3).
        val sliced = zoomSlice(listOf(1f, 2f, 3f, 4f), listOf("A", "B", "C", "D"), 0.25f, 0.75f)
        assertEquals(listOf("B", "C"), sliced.categories)
        assertEquals(listOf(2f, 3f), sliced.series.first().values)
    }

    @Test
    fun applyZoomSlice_singleCategoryReturnsUnchanged() {
        val data = ChartData(listOf(ChartSeries("a", listOf(1f))), listOf("A"))
        assertEquals(data, applyZoomSlice(data, 0.25f to 0.75f))
    }

    @Test
    fun applyZoomSlice_trimsMultipleSeriesAndCategories() {
        // Two series + four categories: a window 0.5..1.0 → indices 2..4 (C,D).
        val data = ChartData(
            series = listOf(
                ChartSeries("a", listOf(1f, 2f, 3f, 4f)),
                ChartSeries("b", listOf(10f, 20f, 30f, 40f)),
            ),
            categories = listOf("A", "B", "C", "D"),
        )
        val sliced = applyZoomSlice(data, 0.5f to 1.0f)
        assertEquals(listOf("C", "D"), sliced.categories)
        assertEquals(listOf(3f, 4f), sliced.series[0].values)
        assertEquals(listOf(30f, 40f), sliced.series[1].values)
    }

    @Test
    fun applyZoomSlice_handlesShorterSeriesSafely() {
        // One series shorter than the category count; slice must not throw on sublist bounds.
        val data = ChartData(
            series = listOf(ChartSeries("a", listOf(1f, 2f))), // only 2 values for 4 cats
            categories = listOf("A", "B", "C", "D"),
        )
        val sliced = applyZoomSlice(data, 0.5f to 1.0f)
        assertEquals(listOf("C", "D"), sliced.categories)
        // values list has 2 entries → startIdx clamped, endIdx clamped to size → empty slice.
        assertTrue(sliced.series[0].values.isEmpty())
    }

    @Test
    fun applyZoomSlice_nullRangeIsNoOp() {
        val data = ChartData(listOf(ChartSeries("a", listOf(1f, 2f, 3f))), listOf("A", "B", "C"))
        assertEquals(data, applyZoomSlice(data, null))
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Tooltip helpers (formatTooltipValue / highlightStrokeColor) ────────
    @Test
    fun formatTooltipValue_integer_noDecimal() {
        assertEquals("120", formatTooltipValue(120f))
        assertEquals("0", formatTooltipValue(0f))
    }

    @Test
    fun formatTooltipValue_decimalTrimsTrailingZero() {
        // 50.0 → "50"; 2.5 → "2.5"
        assertEquals("50", formatTooltipValue(50f))
        assertEquals("2.5", formatTooltipValue(2.5f))
    }

    @Test
    fun formatTooltipValue_roundsToOneDecimal() {
        // 3.46 → rounded to 3.5 (1 decimal).
        assertEquals("3.5", formatTooltipValue(3.46f))
    }

    @Test
    fun formatTooltipValue_nonFiniteReturnsZero() {
        assertEquals("0", formatTooltipValue(Float.NaN))
        assertEquals("0", formatTooltipValue(Float.POSITIVE_INFINITY))
    }

    @Test
    fun highlightStrokeColor_opaqueColorReturnedAsIs() {
        // An opaque series color (alpha ≥ 0.5) is returned unchanged so the highlight matches the fill.
        val opaque = Color(0xFF112233)
        assertEquals(opaque, highlightStrokeColor(opaque))
    }

    @Test
    fun highlightStrokeColor_translucentColorBecomesOpaque() {
        // A translucent fill (alpha < 0.5, common for the categorical palette's faded entries) is
        // boosted to full alpha so the hover stroke still reads against the plot.
        val translucent = Color(0x55112233)
        val result = highlightStrokeColor(translucent)
        assertEquals(1f, result.alpha, 0.001f)
        assertEquals(translucent.red, result.red)
        assertEquals(translucent.green, result.green)
        assertEquals(translucent.blue, result.blue)
    }

    @Test
    fun highlightStrokeColor_fullyTransparentBecomesOpaque() {
        // alpha 0 is < 0.5 → boosted to 1 so an invisible fill still surfaces a visible hover stroke.
        // (RGB stays 0 → resolves to opaque black, which reads on light/dark plots alike.)
        val result = highlightStrokeColor(Color.Transparent)
        assertEquals(1f, result.alpha, 0.001f)
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region radarValueRange negatives & deriveDualYRanges edge ─────────────────
    @Test
    fun radarValueRange_allNegative_keepsActualBounds() {
        // No positive values → min is the actual min (not clamped to 0), max the actual max.
        val (min, max) = radarValueRange(listOf(ChartSeries("a", listOf(-10f, -4f, -7f))))
        assertEquals(-10f, min)
        assertEquals(-4f, max)
    }

    @Test
    fun radarValueRange_mixedSigns_clampsMinToZero() {
        // Any positive value → radar reads from center (0); negatives are still drawn (clamped by plot).
        val (min, max) = radarValueRange(listOf(ChartSeries("a", listOf(-3f, 5f))))
        assertEquals(0f, min)
        assertEquals(5f, max)
    }

    @Test
    fun deriveDualYRanges_singleSeriesOnRight_onlyRightRange() {
        // All series on axis 1 → left range is the empty-series safe span (0..1), right has data.
        val series = listOf(ChartSeries("b", listOf(100f, 1000f), yAxisIndex = 1))
        val (left, right) = deriveDualYRanges(series)
        assertEquals(0f to 1f, left)
        assertNotNull(right)
        assertEquals(0f to 1000f, right)
    }

    @Test
    fun deriveDualYRanges_overrideAffectsLeftOnly() {
        // The explicit override binds to the LEFT axis (right still derives from its own data).
        val series = listOf(
            ChartSeries("a", listOf(1f, 5f), yAxisIndex = 0),
            ChartSeries("b", listOf(10f, 50f), yAxisIndex = 1),
        )
        val (left, right) = deriveDualYRanges(series, override = 0f to 100f)
        assertEquals(0f to 100f, left)
        assertNotNull(right)
        assertEquals(0f to 50f, right)
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region niceTicks / formatTickValue edge ───────────────────────────────────
    @Test
    fun niceTicks_tinySpan_returnsSingleTick() {
        // Span of exactly 0 → single 0 tick (division-safe path).
        assertEquals(listOf(0f), niceTicks(7f, 7f, count = 4))
    }

    @Test
    fun niceTicks_countOne_stillProducesGrid() {
        val ticks = niceTicks(0f, 100f, count = 1)
        assertTrue(ticks.isNotEmpty())
        assertEquals(0f, ticks.first())
    }

    @Test
    fun formatTickValue_largeInteger_noScientificNotation() {
        // A large integer must render in full, not as 1e6.
        assertEquals("1000000", formatTickValue(1000000f))
    }

    @Test
    fun formatTickValue_negativeWithUnit() {
        assertEquals("-40%", formatTickValue(-40f, unit = "%"))
    }

    @Test
    fun formatTickValue_nonFiniteFallsBackToZeroWithUnit() {
        assertEquals("0k", formatTickValue(Float.NaN, unit = "k"))
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Dual-axis even fractions (alignment fix) ───────────────────────────
    @Test
    fun evenTickFractions_count4_yieldsFiveEvenSteps() {
        // count=4 → 5 fractions: 0, 0.25, 0.5, 0.75, 1
        val fracs = evenTickFractions(4)
        assertEquals(listOf(0f, 0.25f, 0.5f, 0.75f, 1f), fracs)
    }

    @Test
    fun evenTickFractions_count1_yieldsEndpoints() {
        assertEquals(listOf(0f, 1f), evenTickFractions(1))
    }

    @Test
    fun evenTickFractions_zeroOrNegative_clampsToOne() {
        // count ≤ 0 must not divide by zero → coerced to 1 → endpoints [0, 1].
        assertEquals(listOf(0f, 1f), evenTickFractions(0))
        assertEquals(listOf(0f, 1f), evenTickFractions(-3))
    }

    @Test
    fun fractionToAxisValue_endpointsAreAxisBounds() {
        // f=0 → min, f=1 → max.
        assertEquals(0f, fractionToAxisValue(0f, 0f, 100f))
        assertEquals(100f, fractionToAxisValue(1f, 0f, 100f))
    }

    @Test
    fun fractionToAxisValue_midpointIsAverage() {
        assertEquals(50f, fractionToAxisValue(0.5f, 0f, 100f))
        assertEquals(0f, fractionToAxisValue(0.5f, -10f, 10f))
    }

    @Test
    fun dualAxis_leftAndRightShareSameFractionHeights() {
        // The contract this fixes: left tick f and right tick f must map to the SAME fraction of
        // the plot height → (val - min)/(max - min) must be identical for both axes at each f.
        val leftRange = 0f to 70f
        val rightRange = 0f to 900f
        val fracs = evenTickFractions(4)
        fracs.forEach { f ->
            val leftVal = fractionToAxisValue(f, leftRange.first, leftRange.second)
            val rightVal = fractionToAxisValue(f, rightRange.first, rightRange.second)
            val leftFrac = (leftVal - leftRange.first) / (leftRange.second - leftRange.first)
            val rightFrac = (rightVal - rightRange.first) / (rightRange.second - rightRange.first)
            // Both axes' ticks at fraction f sit at the same plot height (within float tolerance).
            assertEquals(leftFrac, rightFrac, 0.0001f)
        }
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region DataZoom slider math (computeZoom) ────────────────────────────────
    // A 400px-wide slider → 1px = 0.0025 of the range.
    private val zoomWidth = 400f

    @Test
    fun computeZoom_leftHandle_movesStartClampedToMinSpan() {
        // Start 0.2, end 0.8, minSpan 0.1. Drag left handle right by 80px (0.2 of range).
        val (ns, ne) = computeZoom(0.2f, 0.8f, mode = -1, deltaPx = 80f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0.4f, ns, 0.001f)
        assertEquals(0.8f, ne, 0.001f)
    }

    @Test
    fun computeZoom_leftHandle_cannotExceedEndMinusMinSpan() {
        // Dragging the left handle past (end - minSpan) clamps: end=0.8, minSpan=0.2 → start ≤ 0.6.
        val (ns, ne) = computeZoom(0.5f, 0.8f, mode = -1, deltaPx = 200f, widthPx = zoomWidth, minSpan = 0.2f)
        assertEquals(0.6f, ns, 0.001f)
        assertEquals(0.8f, ne, 0.001f)
    }

    @Test
    fun computeZoom_leftHandle_cannotGoBelowZero() {
        // Drag left by a huge negative amount → start clamped to 0.
        val (ns, _) = computeZoom(0.3f, 0.8f, mode = -1, deltaPx = -500f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0f, ns, 0.001f)
    }

    @Test
    fun computeZoom_rightHandle_movesEndClampedToMinSpan() {
        val (ns, ne) = computeZoom(0.2f, 0.6f, mode = 1, deltaPx = 80f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0.2f, ns, 0.001f)
        assertEquals(0.8f, ne, 0.001f)
    }

    @Test
    fun computeZoom_rightHandle_cannotExceedOne() {
        val (ns, ne) = computeZoom(0.2f, 0.7f, mode = 1, deltaPx = 500f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0.2f, ns, 0.001f)
        assertEquals(1f, ne, 0.001f)
    }

    @Test
    fun computeZoom_bodyPan_shiftsWindowKeepsSpan() {
        // Pan the whole window: span stays constant, both shift by the delta.
        val (ns, ne) = computeZoom(0.2f, 0.6f, mode = 2, deltaPx = 80f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0.4f, ns, 0.001f)
        assertEquals(0.8f, ne, 0.001f)
        // Span preserved.
        assertEquals((0.6f - 0.2f), (ne - ns), 0.0001f)
    }

    @Test
    fun computeZoom_bodyPan_clampsAtRightEdge() {
        // Window [0.7, 0.9] dragged right → cannot exceed 1.0; start clamps to (1 - span).
        val (ns, ne) = computeZoom(0.7f, 0.9f, mode = 2, deltaPx = 200f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0.8f, ns, 0.001f)
        assertEquals(1.0f, ne, 0.001f)
    }

    @Test
    fun computeZoom_idleMode_isNoOp() {
        val (ns, ne) = computeZoom(0.2f, 0.8f, mode = 0, deltaPx = 100f, widthPx = zoomWidth, minSpan = 0.1f)
        assertEquals(0.2f to 0.8f, ns to ne)
    }

    @Test
    fun computeZoom_zeroWidth_isNoOp() {
        // Defensive: a not-yet-measured slider (width 0) ignores drags.
        assertEquals(0.2f to 0.8f, computeZoom(0.2f, 0.8f, mode = -1, deltaPx = 80f, widthPx = 0f, minSpan = 0.1f))
    }
    // endregion ────────────────────────────────────────────────────────────────
}
