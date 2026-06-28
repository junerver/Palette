package xyz.junerver.compose.palette.components.chart

import androidx.compose.ui.graphics.Color

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
 * An explicit [override] (from [ChartOptions.yRange]) always wins when non-null.
 */
internal fun deriveYRange(series: List<ChartSeries>, override: Pair<Float, Float>? = null): Pair<Float, Float> {
    override?.let { return it }
    val values = series.flatMap { it.values }
    if (values.isEmpty()) return 0f to 1f
    val min = values.min()
    val max = values.max()
    return if (min >= 0f) 0f to max.coerceAtLeast(min + 1f) else min to max.coerceAtLeast(min + 1f)
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
