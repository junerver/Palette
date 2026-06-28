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
 */
@Composable
internal fun PieChartRenderer(
    spec: ChartSpec.Pie,
    data: ChartData,
    modifier: Modifier,
    @Suppress("UNUSED_PARAMETER") options: ChartOptions,
    colors: ChartColors,
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
    val holeFraction = tokens.donutHoleRadiusFraction
    val surfaceColor = PaletteTheme.colors.surface
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

            var startAngle = sweepBase
            slices.forEachIndexed { index, value ->
                val sweep = (value / total) * 360f
                val color = sliceColors[index]
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = !spec.donut,
                    topLeft = Offset(center.x - drawRadius, center.y - drawRadius),
                    size = Size(drawRadius * 2, drawRadius * 2),
                )
                // Slice separator stroke.
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(center.x - drawRadius, center.y - drawRadius),
                    size = Size(drawRadius * 2, drawRadius * 2),
                    style = Stroke(width = separatorPx),
                )

                if (spec.showLabels && sweep >= 8f) {
                    val midAngle = startAngle + sweep / 2f
                    val labelRad = drawRadius * 1.12f
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
                startAngle += sweep
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
