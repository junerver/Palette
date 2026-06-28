package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a line / area chart. Each series is a polyline through its values, optionally smoothed
 * (Catmull-Rom→cubic Bézier), with optional data points and area fill to the baseline.
 */
@Composable
internal fun LineChartRenderer(
    spec: ChartSpec.Line,
    data: ChartData,
    modifier: Modifier,
    options: ChartOptions,
    colors: ChartColors,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val density = LocalDensity.current
    val categories = resolveCategories(data)
    val series = data.series
    val (yMin, yMax) = deriveYRange(series, options.yRange)
    val gridColor = colors.gridColor
    val axisColor = colors.axisColor
    val accentFallback = PaletteTheme.colors.textPrimary
    // Pre-resolve in the @Composable scope so the DrawScope stays pure.
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val leftPx = with(density) { 48.dp.toPx() }
    val topPx = with(density) { 12.dp.toPx() }
    val rightPx = with(density) { 12.dp.toPx() }
    val bottomPx = with(density) { 32.dp.toPx() }
    val lineStrokePx = with(density) { 2.dp.toPx() }
    val pointRadiusPx = with(density) { 3.dp.toPx() }
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (series.isEmpty()) return@Canvas
            val catCount = categories.size.coerceAtLeast(1)
            val plotW = (size.width - leftPx - rightPx).coerceAtLeast(1f)
            val plotH = (size.height - topPx - bottomPx).coerceAtLeast(1f)
            val baselineY = topPx + plotH
            val slotW = plotW / catCount

            // Grid + axes.
            if (options.showGrid) {
                val ticks = 4
                for (i in 0..ticks) {
                    val frac = i / ticks.toFloat()
                    val y = baselineY - frac * plotH
                    drawLine(gridColor, Offset(leftPx, y), Offset(leftPx + plotW, y), gridStrokePx)
                }
            }
            if (options.showAxes) {
                drawLine(axisColor, Offset(leftPx, topPx), Offset(leftPx, baselineY), axisStrokePx)
                drawLine(axisColor, Offset(leftPx, baselineY), Offset(leftPx + plotW, baselineY), axisStrokePx)
            }

            fun pointOf(v: Float, i: Int): Offset {
                val x = leftPx + slotW * (i + 0.5f)
                val y = baselineY - normalizeValue(v, yMin, yMax) * plotH
                return Offset(x, y)
            }

            series.forEachIndexed { sIndex, s ->
                val color = seriesColors[sIndex]
                val pts = s.values.mapIndexed { i, v -> pointOf(v, i) }
                if (pts.isEmpty()) return@forEachIndexed

                val linePath = if (spec.smooth) buildSmoothPath(pts) else buildStraightPath(pts)

                if (spec.areaFill) {
                    val area = Path().apply {
                        addPath(linePath)
                        lineTo(pts.last().x, baselineY)
                        lineTo(pts.first().x, baselineY)
                        close()
                    }
                    drawPath(path = area, color = color.copy(alpha = 0.22f), style = Fill)
                }
                drawPath(
                    path = linePath,
                    color = color,
                    style = Stroke(width = lineStrokePx),
                )
                if (spec.showPoints) {
                    pts.forEach { p -> drawCircle(color = color, radius = pointRadiusPx, center = p) }
                }
            }
        }
    }
}

/** Polyline through the points. */
private fun buildStraightPath(points: List<Offset>): Path = Path().apply {
    points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
}

/**
 * Smooth curve via Catmull-Rom→cubic Bézier (tension 0.5). Endpoints use a reflected control so the
 * curve starts/ends exactly at the first/last point. Matches the mermaid flowchart smoothing.
 */
private fun buildSmoothPath(points: List<Offset>): Path {
    if (points.size < 3) return buildStraightPath(points)
    val p = Path()
    p.moveTo(points[0].x, points[0].y)
    for (i in 0 until points.size - 1) {
        val p0 = points[if (i == 0) i else i - 1]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points[if (i + 2 < points.size) i + 2 else i + 1]
        val cp1x = p1.x + (p2.x - p0.x) / 6f
        val cp1y = p1.y + (p2.y - p0.y) / 6f
        val cp2x = p2.x - (p3.x - p1.x) / 6f
        val cp2y = p2.y - (p3.y - p1.y) / 6f
        p.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
    }
    return p
}
