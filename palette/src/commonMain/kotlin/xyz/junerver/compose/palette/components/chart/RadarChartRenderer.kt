package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a radar / spider chart. Each [ChartSeries] is a polygon whose vertices sit on the axes
 * defined by [ChartData.categories] (one axis per category, radiating from the center, starting at
 * the top and going clockwise). All series share the same value range so polygons are comparable.
 *
 * The concentric reference rings + axis spokes ([ChartSpec.Radar.showGrid]) and the axis labels are
 * drawn inline (no cartesian axes). Geometry math is delegated to the tested [radarAxisAngle] /
 * [radarVertex] / [radarValueRange] pure functions.
 */
@Composable
internal fun RadarChartRenderer(
    spec: ChartSpec.Radar,
    data: ChartData,
    modifier: Modifier,
    @Suppress("UNUSED_PARAMETER") options: ChartOptions,
    colors: ChartColors,
    @Suppress("UNUSED_PARAMETER") hoverState: ChartHoverState,
    @Suppress("UNUSED_PARAMETER") canvasSize: IntSize,
    density: Density,
    entrance: ChartEntranceAnimation,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val categories = resolveCategories(data)
    val series = data.series
    val axisCount = categories.size.coerceAtLeast(1)
    val (vMin, vMax) = radarValueRange(series, options.yRange)
    val accentFallback = PaletteTheme.colors.textPrimary
    val strokePx = with(density) { 2.dp.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val vertexRadiusPx = with(density) { 3.dp.toPx() }
    val labelStyle = ChartDefaults.axisTextStyle()
    val measurer = rememberTextMeasurer()
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }
    val ringCount = 4

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (series.isEmpty()) return@Canvas
            val side = minOf(size.width, size.height)
            // Leave room for the axis labels around the polygon.
            val labelRoom = side * 0.12f
            val radius = ((side / 2f) - labelRoom).coerceAtLeast(1f)
            val center = Offset(size.width / 2f, size.height / 2f)
            val progress = entrance.value

            // Concentric reference rings + axis spokes.
            if (spec.showGrid) {
                for (ring in 1..ringCount) {
                    val ringFrac = ring.toFloat() / ringCount
                    val ringPath = Path()
                    for (axis in 0 until axisCount) {
                        val p = radarVertex(center, radius, axis, axisCount, ringFrac)
                        if (axis == 0) ringPath.moveTo(p.x, p.y) else ringPath.lineTo(p.x, p.y)
                    }
                    ringPath.close()
                    drawPath(ringPath, color = colors.gridColor, style = Stroke(width = gridStrokePx))
                }
                for (axis in 0 until axisCount) {
                    val edge = radarVertex(center, radius, axis, axisCount, 1f)
                    drawLine(colors.axisColor, center, edge, axisStrokePx)
                }
            }

            // Axis (category) labels just outside each spoke's tip.
            categories.forEachIndexed { axis, label ->
                val tip = radarVertex(center, radius, axis, axisCount, 1f)
                val angle = radarAxisAngle(axis, axisCount)
                // Nudge the label outward along the spoke direction.
                val nudge = labelRoom * 0.5f
                val labelPos = Offset(
                    x = tip.x + (nudge * kotlin.math.cos(angle)),
                    y = tip.y + (nudge * kotlin.math.sin(angle)),
                )
                val tl = measurer.measure(
                    AnnotatedString(label),
                    style = labelStyle,
                    maxLines = 1,
                )
                drawText(
                    textLayoutResult = tl,
                    color = colors.tickLabelColor,
                    topLeft = Offset(labelPos.x - tl.size.width / 2f, labelPos.y - tl.size.height / 2f),
                )
            }

            // Each series → one polygon. Vertices scaled by the entrance progress (grow from center).
            series.forEachIndexed { sIndex, s ->
                val color = seriesColors[sIndex]
                val poly = Path()
                s.values.forEachIndexed { axis, v ->
                    if (axis >= axisCount) return@forEachIndexed
                    val frac = normalizeValue(v, vMin, vMax) * progress
                    val p = radarVertex(center, radius, axis, axisCount, frac)
                    if (axis == 0) poly.moveTo(p.x, p.y) else poly.lineTo(p.x, p.y)
                }
                poly.close()
                if (spec.fillAlpha > 0f) {
                    drawPath(poly, color = color.copy(alpha = spec.fillAlpha))
                }
                drawPath(poly, color = color, style = Stroke(width = strokePx))
                // Vertex dots.
                s.values.forEachIndexed { axis, v ->
                    if (axis >= axisCount) return@forEachIndexed
                    val frac = normalizeValue(v, vMin, vMax) * progress
                    val p = radarVertex(center, radius, axis, axisCount, frac)
                    drawCircle(color = color, radius = vertexRadiusPx, center = p)
                }
            }
        }
    }
}
