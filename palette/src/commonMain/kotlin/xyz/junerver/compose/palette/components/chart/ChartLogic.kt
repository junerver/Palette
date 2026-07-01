package xyz.junerver.compose.palette.components.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pure chart computations, kept free of Compose so they are unit-testable in `commonTest`.
 */

/**
 * Derives the inclusive y-axis range from a list of series.
 *
 * - Empty → `(0f, 1f)` (a safe 1-unit span so division never divides by zero).
 * - All non-negative → min clamped to `0` (charts read better from zero).
 * - Otherwise → actual min/max.
 *
 * With [stacked] = true (stacked bar) the max is the largest per-category **sum** of all series
 * (not the largest single value), so a stacked bar never exceeds its plot area. Negative minima use
 * the per-category sum as well; when series have differing lengths the shorter ones contribute 0.
 *
 * An explicit [override] (from [ChartOptions.yRange]) always wins when non-null.
 */
internal fun deriveYRange(
    series: List<ChartSeries>,
    override: Pair<Float, Float>? = null,
    stacked: Boolean = false,
): Pair<Float, Float> {
    override?.let { return it }
    if (stacked) {
        // Per-category totals across all series.
        val catCount = series.maxOfOrNull { it.values.size } ?: 0
        if (catCount == 0) return 0f to 1f
        val sums = FloatArray(catCount)
        series.forEach { s ->
            s.values.forEachIndexed { i, v -> sums[i] += v }
        }
        val min = sums.min()
        val max = sums.max()
        return if (min >= 0f) 0f to max.coerceAtLeast(min + 1f) else min to max.coerceAtLeast(min + 1f)
    }
    val values = series.flatMap { it.values }
    if (values.isEmpty()) return 0f to 1f
    val min = values.min()
    val max = values.max()
    return if (min >= 0f) 0f to max.coerceAtLeast(min + 1f) else min to max.coerceAtLeast(min + 1f)
}

/**
 * Splits a series list by [ChartSeries.yAxisIndex] (clamped to 0/1) and derives each axis's range.
 * Returns `(leftRange, rightRange)` where `rightRange` is `null` when NO series binds to axis 1
 * (single-axis mode). Used by the dual-axis bar/line renderers.
 *
 * [stacked] only affects the LEFT axis (a stacked bar's right axis is unusual; left dominates).
 */
internal fun deriveDualYRanges(
    series: List<ChartSeries>,
    override: Pair<Float, Float>? = null,
    stacked: Boolean = false,
): Pair<Pair<Float, Float>, Pair<Float, Float>?> {
    val left = series.filter { (it.yAxisIndex.coerceIn(0, 1)) == 0 }
    val right = series.filter { it.yAxisIndex.coerceIn(0, 1) == 1 }
    val leftRange = deriveYRange(left, override, stacked)
    val rightRange = if (right.isNotEmpty()) deriveYRange(right) else null
    return leftRange to rightRange
}

/**
 * Resolves the color for a series: an explicit [ChartSeries.color] wins; otherwise the [palette] is
 * cycled by [index]. Falls back to [fallback] when the palette is empty.
 */
internal fun resolveSeriesColor(
    series: ChartSeries,
    index: Int,
    palette: List<Color>,
    fallback: Color,
): Color = when {
    series.color != Color.Unspecified -> series.color
    palette.isNotEmpty() -> palette[index % palette.size]
    else -> fallback
}

/**
 * Resolves category labels: explicit [ChartData.categories] win; otherwise sequential `1..n` strings
 * sized to the longest series (min 1).
 */
internal fun resolveCategories(data: ChartData): List<String> {
    if (data.categories.isNotEmpty()) return data.categories
    val n = (data.series.maxOfOrNull { it.values.size } ?: 0).coerceAtLeast(1)
    return (1..n).map { it.toString() }
}

/**
 * Normalizes a value into the `[0, 1]` fraction along the y range. Out-of-range values are clamped.
 * Safe against a zero-width range (returns 0).
 */
internal fun normalizeValue(value: Float, yMin: Float, yMax: Float): Float {
    val span = yMax - yMin
    if (span <= 0f) return 0f
    return ((value - yMin) / span).coerceIn(0f, 1f)
}

/**
 * Resolves a pie/donut slice's sweep angles and fill geometry for one slice.
 *
 * The donut hole is produced by clearing a circle from the center afterwards (see [PieChartRenderer]),
 * so every slice — pie **and** donut — must be a full wedge (`useCenter = true`). Letting `useCenter` be
 * false for donut slices makes `drawArc` render an arc+chord segment whose chord cuts through large
 * slices (>180°) and leaves gaps near the center, producing a distorted, uneven ring.
 *
 * [startAngle]/[sweepAngle] are in degrees; sweep is the slice's share of a full 360° turn.
 */
internal data class PieSliceGeometry(
    val startAngle: Float,
    val sweepAngle: Float,
    val useCenter: Boolean,
)

/**
 * Computes the geometry for pie/donut slice [index] given its [value] and the running [startAngle].
 * [total] is the sum of all slice values (must be > 0).
 */
internal fun pieSliceGeometry(
    index: Int,
    value: Float,
    startAngle: Float,
    total: Float,
): PieSliceGeometry = PieSliceGeometry(
    startAngle = startAngle,
    sweepAngle = (value / total) * 360f,
    useCenter = true,
)

/**
 * Orientation-agnostic bar geometry, expressed as fractions of the plotting area. The renderer maps
 * [start] / [size] onto either the x-axis (vertical bars) or the y-axis (horizontal bars).
 *
 * - [start] is the leading edge of the bar (or stack segment), in `[0,1]` of the value axis.
 * - [extent] is the bar's length along the value axis, in `[0,1]` of the value axis.
 * - [crossCenter] is the bar's slot center along the category axis, in `[0,1]`.
 * - [crossSize] is the bar's thickness along the category axis, in `[0,1]` of the category axis.
 */
internal data class BarLayout(
    val start: Float,
    val extent: Float,
    val crossCenter: Float,
    val crossSize: Float,
)

/**
 * Layouts a single bar in a vertical or horizontal bar chart.
 *
 * @param catCount number of categories (slots along the category axis).
 * @param catIndex index of the current category slot.
 * @param seriesCount number of series in this group.
 * @param sIndex index of the series within its group.
 * @param value the bar's value (or stacked segment value).
 * @param accValue for stacked charts, the running sum of values below this segment (else 0).
 * @param yMin/yMax the inclusive value-axis range; value/accValue are normalized against it.
 */
internal fun barLayout(
    catCount: Int,
    catIndex: Int,
    seriesCount: Int,
    sIndex: Int,
    value: Float,
    accValue: Float,
    yMin: Float,
    yMax: Float,
    stacked: Boolean,
): BarLayout {
    val catCountSafe = catCount.coerceAtLeast(1)
    val slotFraction = 1f / catCountSafe
    // The category axis is identical for horizontal & vertical — only which canvas axis it maps to.
    val crossCenter = slotFraction * catIndex + slotFraction / 2f
    val groupFraction = slotFraction * 0.7f
    val crossSize = if (stacked) groupFraction else (groupFraction / seriesCount.coerceAtLeast(1))
    val crossOffset = if (stacked) 0f else crossSize * sIndex
    val crossStart = crossCenter - groupFraction / 2f + crossOffset
    // Value axis: normalized position. extent is always ≥ 0 (a bar grows from its base).
    val startFrac = normalizeValue(accValue, yMin, yMax)
    val endFrac = normalizeValue(accValue + value, yMin, yMax)
    val extent = (endFrac - startFrac).coerceAtLeast(0f)
    return BarLayout(
        start = startFrac,
        extent = extent,
        crossCenter = crossStart + crossSize / 2f,
        crossSize = crossSize,
    )
}

// region Axis tick computation ───────────────────────────────────────────────

/**
 * Produces "nice" tick values for a numeric axis spanning [min]..[max], aiming for roughly [count]
 * intervals. Uses the classic 1/2/5×10ⁿ step so labels read cleanly (e.g. 0,20,40,60,80,100 rather
 * than 0,17,34,…). Ticks are clamped to the data range and padded one step beyond, so the data never
 * touches the canvas edge.
 *
 * Edge cases: zero/negative span → a single 0 tick (division-safe); non-finite input → empty.
 */
internal fun niceTicks(
    min: Float,
    max: Float,
    count: Int = 4,
): List<Float> {
    if (!min.isFinite() || !max.isFinite()) return emptyList()
    val span = max - min
    if (span <= 0f) return listOf(0f)
    val targetCount = count.coerceIn(1, 12)
    // Raw step → normalized mantissa in [1, 10), then snap to 1/2/5.
    val rawStep = span / targetCount
    val magnitude = floor(log10(rawStep.toDouble()))
    val mantissa = rawStep / 10f.pow(magnitude.toInt()).toFloat()
    val niceMantissa = when {
        mantissa < 1.5f -> 1f
        mantissa < 3f -> 2f
        mantissa < 7f -> 5f
        else -> 10f
    }
    val step = (niceMantissa * 10f.pow(magnitude.toInt()).toFloat()).coerceAtLeast(Float.MIN_VALUE)
    // Start at the first multiple of `step` that is ≤ min.
    val start = floor(min / step) * step
    val ticks = mutableListOf<Float>()
    var v = start
    // Guard against float drift: iterate while within one step of max.
    while (v <= max + step * 0.5f) {
        if (v >= min - step * 0.5f) ticks.add(v)
        // Advance and snap to the step grid to avoid accumulating error.
        v = (ticks.size) * step + start
        if (v.isInfinite() || v.isNaN()) break
    }
    return ticks
}

/**
 * Formats a tick value for display, platform-agnostic (Kotlin/Native restricts `String.format`).
 * Integers render without a decimal; non-integers render with up to 1 fractional digit (trailing
 * zeros trimmed). [unit] (e.g. "%", "k") is appended when non-empty.
 *
 * Examples: 120 → "120"; 120, unit="%" → "120%"; 1.5, unit="k" → "1.5k"; 0 → "0".
 */
internal fun formatTickValue(value: Float, unit: String = ""): String {
    if (!value.isFinite()) return "0$unit"
    // Round to 1 decimal, manual (no String.format on Native).
    val rounded = kotlin.math.round(value * 10.0) / 10.0
    val intPart = rounded.toLong()
    val decimal = ((rounded - intPart) * 10.0).let { kotlin.math.round(it).toInt() % 10 }
    val number = if (decimal == 0) intPart.toString() else "$intPart.$decimal"
    return if (unit.isEmpty()) number else "$number$unit"
}

// endregion ───────────────────────────────────────────────────────────────────

/**
 * Parses a flat values list into (x, y) pairs for scatter charts: indices 0,2,4,… are X and
 * 1,3,5,… are Y. An odd-length list drops the trailing unpaired value. Pure + testable.
 */
internal fun scatterPairs(values: List<Float>): List<Pair<Float, Float>> {
    val n = values.size / 2
    if (n == 0) return emptyList()
    return List(n) { i -> values[i * 2] to values[i * 2 + 1] }
}

/**
 * Derives the bounding box of all scatter points across every series. Returns `null` when there are
 * no points. Used to size the scatter chart's axes (its X axis is numeric, not categorical).
 */
internal fun scatterBounds(series: List<ChartSeries>): Pair<Pair<Float, Float>, Pair<Float, Float>>? {
    val pts = series.flatMap { scatterPairs(it.values) }
    if (pts.isEmpty()) return null
    val xs = pts.map { it.first }
    val ys = pts.map { it.second }
    val xMin = xs.min()
    val xMax = xs.max().coerceAtLeast(xMin + 1f)
    val yMin = ys.min()
    val yMax = ys.max().coerceAtLeast(yMin + 1f)
    return (xMin to xMax) to (yMin to yMax)
}

/**
 * Hit test for a scatter chart: returns the nearest point within [radiusPx] of [mouse], or `null`.
 * Pure + testable. [xRange]/[yRange] are the scatter bounds (so points map onto the plot rect).
 */
internal fun hitTestScatter(
    mouse: Offset,
    series: List<ChartSeries>,
    xRange: Pair<Float, Float>,
    yRange: Pair<Float, Float>,
    plot: PlotRect,
    radiusPx: Float,
): HitTarget? {
    if (series.isEmpty()) return null
    val (xMin, xMax) = xRange
    val (yMin, yMax) = yRange
    var best: HitTarget? = null
    var bestDist = radiusPx * radiusPx
    series.forEachIndexed { sIndex, s ->
        scatterPairs(s.values).forEachIndexed { pairIndex, (x, y) ->
            val fx = normalizeValue(x, xMin, xMax)
            val fy = normalizeValue(y, yMin, yMax)
            val px = plot.left + fx * plot.width
            // Y grows downward on screen → invert.
            val py = plot.top + plot.height - fy * plot.height
            val dx = mouse.x - px
            val dy = mouse.y - py
            val dist = dx * dx + dy * dy
            if (dist <= bestDist) {
                bestDist = dist
                best = HitTarget(sIndex, pairIndex, y, category = null, geometryKind = HitGeometry.Point)
            }
        }
    }
    return best
}

/**
 * Produces `count + 1` evenly-spaced tick fractions in `[0, 1]` (i.e. `0, 1/n, 2/n, …, 1`).
 *
 * Used for **dual-axis** charts: both Y axes map their own `[min, max]` ranges onto these SAME
 * fractions, so a left-axis tick at fraction `f` lands at exactly the same pixel height as the
 * right-axis tick at fraction `f`. This is what makes the two axes' grid lines align — calling
 * `niceTicks` independently per axis would produce different step counts/positions and the ticks
 * would visually disagree (the original dual-axis bug).
 *
 * [count] is the number of INTERVALS (so count=4 → 5 ticks at 0/0.25/0.5/0.75/1). Clamped to ≥1.
 */
internal fun evenTickFractions(count: Int): List<Float> {
    val n = count.coerceAtLeast(1)
    return List(n + 1) { i -> i.toFloat() / n }
}

/**
 * Maps an evenly-spaced tick fraction [frac] ∈ `[0,1]` to its value on the axis spanning
 * `[axisMin, axisMax]`. Pure; the dual-axis layout pairs this with [evenTickFractions].
 */
internal fun fractionToAxisValue(frac: Float, axisMin: Float, axisMax: Float): Float =
    axisMin + frac * (axisMax - axisMin)

/**
 * Applies a data-zoom window [range] = (startFrac, endFrac) to [data], trimming the categories and
 * each series' values to that sub-range. Returns [data] unchanged when [range] is null or covers the
 * full set, or when there's ≤1 category (nothing to slice). Pure + testable.
 */
internal fun applyZoomSlice(data: ChartData, range: Pair<Float, Float>?): ChartData {
    if (range == null) return data
    val cats = resolveCategories(data)
    if (cats.size <= 1) return data
    val (s, e) = range
    if (s <= 0f && e >= 1f) return data
    val startIdx = (s * cats.size).toInt().coerceIn(0, cats.size - 1)
    val endIdx = (e * cats.size).toInt().coerceIn(startIdx + 1, cats.size)
    val slicedCats = cats.subList(startIdx, endIdx)
    val slicedSeries = data.series.map { series ->
        series.copy(values = series.values.subList(startIdx.coerceAtMost(series.values.size), endIdx.coerceAtMost(series.values.size)))
    }
    return data.copy(series = slicedSeries, categories = slicedCats)
}

// region Radar geometry (P4-B) ───────────────────────────────────────────────

/**
 * The angle (radians) of radar axis [axisIndex] given there are [axisCount] axes radiating from the
 * center. Axes start at the top (−90°) and go clockwise, matching how a radar/spider chart is
 * conventionally drawn. Pure + testable.
 */
internal fun radarAxisAngle(axisIndex: Int, axisCount: Int): Float {
    val safeCount = axisCount.coerceAtLeast(1)
    val step = (2.0 * PI).toFloat() / safeCount
    return -PI.toFloat() / 2f + step * axisIndex
}

/**
 * Computes the (x, y) canvas position of a radar vertex: the point on axis [axisIndex] at the given
 * normalized [fraction] (0 = center, 1 = outer ring), around [center] with [radius]. Pure.
 */
internal fun radarVertex(
    center: Offset,
    radius: Float,
    axisIndex: Int,
    axisCount: Int,
    fraction: Float,
): Offset {
    val angle = radarAxisAngle(axisIndex, axisCount)
    val r = radius * fraction.coerceIn(0f, 1f)
    return Offset(
        x = center.x + (r * cos(angle)),
        y = center.y + (r * sin(angle)),
    )
}

/**
 * Derives the inclusive value range for a radar chart across every series+axis. Min is clamped to 0
 * (radar axes read from the center = 0) unless all data is negative. An explicit [override] wins.
 */
internal fun radarValueRange(series: List<ChartSeries>, override: Pair<Float, Float>? = null): Pair<Float, Float> {
    override?.let { return it }
    val values = series.flatMap { it.values }
    if (values.isEmpty()) return 0f to 1f
    val max = values.max()
    return if (max > 0f) 0f to max else values.min() to max.coerceAtLeast(values.min() + 1f)
}

// endregion ───────────────────────────────────────────────────────────────────

/**
 * Describes the data point the pointer is hovering. Returned by [hitTestPoint].
 *
 * - [seriesIndex]/[categoryIndex] locate the point in the dataset.
 * - [value] is the underlying value at that position (the stacked sum for stacked bars — the
 *   visually-relevant "top" of the hovered segment column).
 * - [category] is the resolved category label (or `null` for pie/scatter which have no x-axis).
 * - [geometryKind] tells the renderer how the highlight should be drawn for this point.
 */
internal data class HitTarget(
    val seriesIndex: Int,
    val categoryIndex: Int,
    val value: Float,
    val category: String?,
    val geometryKind: HitGeometry,
)

/** How a [HitTarget] is shaped on the canvas — drives the highlight feedback in the renderer. */
internal enum class HitGeometry { Bar, Point, Slice }

/**
 * The plot rectangle in canvas pixels. [left]/[top] is the top-left corner of the data area (after
 * axis margins); [width]/[height] its size. Built by the renderers from their [AxisLayout] + canvas
 * size so [hitTestPoint] stays a pure function of geometry (no Compose dependency).
 */
internal data class PlotRect(val left: Float, val top: Float, val width: Float, val height: Float)

/**
 * Resolves which data point the pointer is hovering, or `null` when it is over empty space.
 *
 * The function is **chart-agnostic** for the cartesian types: given the same plot rectangle, value
 * range, category count and orientation it returns the same answer a bar/line renderer drew. Pie is
 * handled by a dedicated [hitTestPie] (angular geometry, not cartesian). Points outside the plot
 * rectangle (the axis margin gutters) return `null`.
 *
 * Selection rules:
 * - **Bar / line**: the hovered *category slot* is found from the pointer's category-axis position
 *   (the slot that contains the pointer in X for vertical, in Y for horizontal). A bar additionally
 *   requires the pointer to be within the bar's value span — but for tooltip purposes we relax this
 *   to "inside the slot" so dragging along the axis still surfaces a value (the convention used by
 *   mainstream chart libraries); `value` is then the series' value at that category.
 * - **Stacked bar**: returns the *top* of the stack (the running sum) so the tooltip shows the
 *   cumulative value at that category, matching what the eye sees.
 *
 * @param mouse pointer position in canvas pixels.
 * @param series the (already visibility-filtered) series list the renderer actually draws.
 * @param yMin/yMax the value-axis range the renderer plots against.
 * @param categories resolved category labels (length = slot count).
 * @param plot the data-area rectangle.
 * @param horizontal `true` for horizontal bars (axes swapped).
 * @param stacked `true` for stacked bars (returns cumulative value).
 * @param pointRadiusPx for line charts, snaps to the nearest point within this radius; outside it
 *   falls back to the slot-based hover (so the tooltip follows the cursor along the line).
 */
internal fun hitTestPoint(
    mouse: Offset,
    series: List<ChartSeries>,
    yMin: Float,
    yMax: Float,
    categories: List<String>,
    plot: PlotRect,
    horizontal: Boolean = false,
    stacked: Boolean = false,
    pointRadiusPx: Float = 0f,
): HitTarget? {
    if (series.isEmpty() || categories.isEmpty()) return null
    if (mouse.x < plot.left || mouse.x > plot.left + plot.width) return null
    if (mouse.y < plot.top || mouse.y > plot.top + plot.height) return null

    val catCount = categories.size
    // Category axis position ∈ [0, catCount): X for vertical, Y for horizontal.
    val catPos = if (horizontal) {
        (mouse.y - plot.top) / plot.height * catCount
    } else {
        (mouse.x - plot.left) / plot.width * catCount
    }
    val catIndex = catPos.toInt().coerceIn(0, catCount - 1)

    // Line chart: when a snap radius is given, prefer the nearest plotted point within it.
    if (pointRadiusPx > 0f && !stacked) {
        val slot = (if (horizontal) plot.width else plot.width) / catCount
        val nearest = nearestSeriesPoint(mouse, series, yMin, yMax, catIndex, plot, horizontal, pointRadiusPx, slot)
        if (nearest != null) return nearest
    }

    // Bar / line default: report the first visible series' value at this category. For a stacked bar
    // sum every series so the tooltip reflects the visible stack height.
    val value = if (stacked) {
        series.sumOf { (it.values.getOrNull(catIndex) ?: 0f).toDouble() }.toFloat()
    } else {
        series.firstOrNull()?.values?.getOrNull(catIndex) ?: return null
    }

    return HitTarget(
        seriesIndex = 0,
        categoryIndex = catIndex,
        value = value,
        category = categories.getOrNull(catIndex),
        geometryKind = if (stacked || series.size > 1) HitGeometry.Bar else HitGeometry.Point,
    )
}

/**
 * Finds the plotted data point nearest to [mouse] within [pointRadiusPx], used by line charts so the
 * tooltip can snap to a marker rather than the cursor's raw slot. Returns `null` when no point is
 * close enough.
 */
private fun nearestSeriesPoint(
    mouse: Offset,
    series: List<ChartSeries>,
    yMin: Float,
    yMax: Float,
    catIndex: Int,
    plot: PlotRect,
    horizontal: Boolean,
    pointRadiusPx: Float,
    slot: Float,
): HitTarget? {
    // Search the hovered slot and its immediate neighbours — a point can sit near a slot edge.
    val range = (catIndex - 1).coerceAtLeast(0)..(catIndex + 1).let { it.coerceAtMost((series.firstOrNull()?.values?.size ?: 1) - 1) }
    var best: HitTarget? = null
    var bestDist = pointRadiusPx * pointRadiusPx
    series.forEachIndexed { sIndex, s ->
        for (i in range) {
            val v = s.values.getOrNull(i) ?: continue
            val catFrac = (i + 0.5f) / (s.values.size.coerceAtLeast(1))
            val valFrac = normalizeValue(v, yMin, yMax)
            val (px, py) = if (horizontal) {
                plot.left + valFrac * plot.width to plot.top + catFrac * plot.height
            } else {
                plot.left + catFrac * plot.width to plot.top + plot.height - valFrac * plot.height
            }
            val dx = mouse.x - px
            val dy = mouse.y - py
            val dist = dx * dx + dy * dy
            if (dist <= bestDist) {
                bestDist = dist
                best = HitTarget(sIndex, i, v, category = null, geometryKind = HitGeometry.Point)
            }
        }
    }
    // suppress unused-warning while keeping the parameter documented
    @Suppress("UNUSED_VARIABLE") val _slot = slot
    return best
}

/**
 * Hit test for a pie / donut. Returns the slice under [mouse], or `null` when the pointer is outside
 * the pie circle or inside the donut hole.
 *
 * @param center pie center in canvas pixels.
 * @param radius outer draw radius (slices are drawn within this circle).
 * @param holeRadius donut hole radius (0 for a solid pie).
 * @param startAngleDeg the base angle slices are laid out from (matches [ChartSpec.Pie.startAngleDeg]).
 * @param values the slice values (the first series' values).
 * @param categories slice labels.
 */
internal fun hitTestPie(
    mouse: Offset,
    center: Offset,
    radius: Float,
    holeRadius: Float,
    startAngleDeg: Float,
    values: List<Float>,
    categories: List<String>,
): HitTarget? {
    if (values.isEmpty()) return null
    val dx = mouse.x - center.x
    val dy = mouse.y - center.y
    val dist = sqrt(dx * dx + dy * dy)
    if (dist > radius || dist < holeRadius) return null
    // Compose drawArc measures angles clockwise from 3 o'clock; atan2(y,x) gives the same convention.
    val angle = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() - startAngleDeg) % 360f + 360f) % 360f
    val total = values.sum().coerceAtLeast(Float.MIN_VALUE)
    var acc = 0f
    values.forEachIndexed { i, v ->
        val sweep = v / total * 360f
        if (angle >= acc && angle < acc + sweep) {
            return HitTarget(i, i, v, categories.getOrNull(i), HitGeometry.Slice)
        }
        acc += sweep
    }
    return null
}

// endregion ───────────────────────────────────────────────────────────────────
