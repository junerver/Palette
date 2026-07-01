package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a pie / donut chart from the FIRST series of [data] (each value → one slice). Slices are
 * colored by the categorical palette (cycling by index) unless [ChartSeries.color] is set.
 *
 * When [hoverState] is active, the slice under the pointer is resolved via [hitTestPie] and drawn
 * slightly "popped" (a thicker bright outline) for tooltip feedback.
 */
@Composable
internal fun PieChartRenderer(
    spec: ChartSpec.Pie,
    data: ChartData,
    modifier: Modifier,
    @Suppress("UNUSED_PARAMETER") options: ChartOptions,
    colors: ChartColors,
    hoverState: ChartHoverState,
    canvasSize: IntSize,
    entrance: ChartEntranceAnimation,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val density = LocalDensity.current
    val series = data.series.firstOrNull()
    val categories = resolveCategories(data)
    val slices = series?.values ?: emptyList()
    val total = slices.sum().coerceAtLeast(Float.MIN_VALUE)
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = colors.seriesLabelColor, fontSize = 11.sp)
    val separatorPx = with(density) { 2.dp.toPx() }
    val highlightStrokePx = with(density) { (tokens.highlightStrokeWidth * 1.5f).toPx() }
    val holeFraction = tokens.donutHoleRadiusFraction
    val surfaceColor = PaletteTheme.colors.surface
    val ringColor = PaletteTheme.colors.onSurface
    val accentFallback = PaletteTheme.colors.textPrimary
    // Pre-resolve per-slice colors in the @Composable scope.
    val sliceColors = slices.mapIndexed { i, _ ->
        series?.let { resolveSeriesColor(it, i, colors.categoricalColors, accentFallback) } ?: accentFallback
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val side = minOf(size.width, size.height)
            val radius = side / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val labelRoom = if (spec.showLabels) radius * 0.18f else 0f
            val drawRadius = (radius - labelRoom).coerceAtLeast(1f)
            val holeRadius = if (spec.donut) drawRadius * holeFraction else 0f
            val sweepBase = spec.startAngleDeg

            if (slices.isEmpty()) return@Canvas

            // Resolve the hovered slice (angular hit-test).
            val hovered = if (hoverState.active) {
                hitTestPie(
                    mouse = hoverState.anchor,
                    center = center,
                    radius = drawRadius,
                    holeRadius = holeRadius,
                    startAngleDeg = sweepBase,
                    values = slices,
                    categories = categories,
                    sliceColors = sliceColors,
                    sliceLabel = series?.label ?: "",
                )
            } else null
            hoverState.target = hovered

            var startAngle = sweepBase
            // Entrance animation: scale each slice's sweep so the pie sweeps in from the start angle.
            // The "remaining" sweep budget shrinks so partial slices don't overshoot mid-animation.
            val progress = entrance.value
            val fullSweep = 360f
            val sweptSoFar = FloatArray(1) // mutable running total for the sweep-in effect

            slices.forEachIndexed { index, value ->
                // Geometry (incl. useCenter) is derived from one tested source — donut slices must be
                // full wedges too, since the hole is cleared afterwards by a center circle.
                val geom = pieSliceGeometry(index, value, startAngle, total)
                val color = sliceColors[index]
                val isHovered = hovered != null && hovered.primaryCategoryIndex == index
                // Hovered slices draw slightly enlarged (a "pop") for tactile feedback.
                val r = if (isHovered) drawRadius * 1.03f else drawRadius
                // Sweep-in: this slice's visible sweep is how much of its target sweep falls within
                // the [0, progress*360°] window that has "drawn in" so far.
                val drawnBudget = progress * fullSweep
                val sliceStart = sweptSoFar[0]
                val sliceEnd = minOf(sliceStart + geom.sweepAngle, drawnBudget)
                val visibleSweep = (sliceEnd - sliceStart).coerceAtLeast(0f)
                sweptSoFar[0] = minOf(sliceStart + geom.sweepAngle, drawnBudget)
                drawArc(
                    color = color,
                    startAngle = geom.startAngle,
                    sweepAngle = visibleSweep,
                    useCenter = geom.useCenter,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2, r * 2),
                )
                // Slice separator stroke (only once the slice has actually drawn).
                if (visibleSweep > 0f) {
                    drawArc(
                        color = surfaceColor,
                        startAngle = geom.startAngle,
                        sweepAngle = visibleSweep,
                        useCenter = false,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = separatorPx),
                    )
                }
                // Hovered slice outline.
                if (isHovered && visibleSweep > 0f) {
                    drawArc(
                        color = ringColor,
                        startAngle = geom.startAngle,
                        sweepAngle = visibleSweep,
                        useCenter = false,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = highlightStrokePx),
                    )
                }

                if (spec.showLabels && geom.sweepAngle >= 8f && progress >= 0.98f) {
                    val midAngle = geom.startAngle + geom.sweepAngle / 2f
                    val labelRad = r * 1.12f
                    val rad = midAngle * (PI / 180f)
                    val lx = center.x + (labelRad * cos(rad)).toFloat()
                    val ly = center.y + (labelRad * sin(rad)).toFloat()
                    val label = categories.getOrNull(index) ?: value.toString()
                    val pct = formatPercentLabel(value, total)
                    val text = AnnotatedString("$label\n$pct")
                    val layout = measurer.measure(text, labelStyle)
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(lx - layout.size.width / 2f, ly - layout.size.height / 2f),
                    )
                }
                startAngle += geom.sweepAngle
            }

            // Donut hole: clear the center by drawing the surface color (keeps it theme-correct).
            if (spec.donut && holeRadius > 0f) {
                drawCircle(
                    color = surfaceColor,
                    radius = holeRadius,
                    center = center,
                )
            }
        }
    }
}

/** Formats a slice percentage with one decimal, platform-agnostic (no String.format on Native). */
private fun formatPercentLabel(value: Float, total: Float): String {
    val pct = (value / total) * 100f
    // Round to 1 decimal.
    val rounded = (kotlin.math.round(pct * 10.0) / 10.0)
    val intPart = rounded.toInt()
    val dec = ((rounded - intPart) * 10.0).roundToIntSafe() % 10
    return "$intPart.$dec%"
}

private fun Double.roundToIntSafe(): Int =
    if (this >= 0) kotlin.math.round(this).toInt() else -kotlin.math.round(-this).toInt()
