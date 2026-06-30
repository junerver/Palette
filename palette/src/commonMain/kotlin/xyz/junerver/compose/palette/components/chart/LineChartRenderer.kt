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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
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
    val accentFallback = PaletteTheme.colors.textPrimary
    // Pre-resolve in the @Composable scope so the DrawScope stays pure
    // (PaletteTheme.componentThemes is a @Composable getter — cannot be read inside DrawScope).
    val lineStrokePx = with(density) { 2.dp.toPx() }
    val pointRadiusPx = with(density) { 3.dp.toPx() }
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val labelPadPx = with(density) { tokens.axisLabelPadding.toPx() }
    // Axis margins are now label-driven (see ChartAxisRenderer); ticks/titles add room as needed.
    val axisLayout = rememberAxisLayout(options, yMin, yMax, categories)
    val tickStyle = ChartDefaults.axisTextStyle()
    val titleStyle = ChartDefaults.axisTitleTextStyle()
    val measurer = rememberTextMeasurer()
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (series.isEmpty()) return@Canvas
            val catCount = categories.size.coerceAtLeast(1)
            val leftPx = axisLayout.leftPx
            val topPx = axisLayout.topPx
            val plotW = (size.width - leftPx - axisLayout.rightPx).coerceAtLeast(1f)
            val plotH = (size.height - topPx - axisLayout.bottomPx).coerceAtLeast(1f)
            val baselineY = topPx + plotH
            val slotW = plotW / catCount

            // Shared cartesian frame (grid + axes + Y ticks + X labels + titles).
            drawAxes(
                layout = axisLayout,
                plotW = plotW,
                plotH = plotH,
                yMin = yMin,
                yMax = yMax,
                options = options,
                colors = colors,
                measurer = measurer,
                tickStyle = tickStyle,
                titleStyle = titleStyle,
                axisStrokePx = axisStrokePx,
                gridStrokePx = gridStrokePx,
                labelPadPx = labelPadPx,
            )

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
