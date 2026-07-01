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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a line / area chart. Each series is a polyline through its values, optionally smoothed
 * (Catmull-Rom→cubic Bézier), with optional data points and area fill to the baseline.
 *
 * When [hoverState] is active, the nearest data point to the pointer is resolved via
 * [hitTestPoint] (with a snap radius) and drawn larger + ringed for tooltip feedback.
 */
@Composable
internal fun LineChartRenderer(
    spec: ChartSpec.Line,
    data: ChartData,
    modifier: Modifier,
    options: ChartOptions,
    colors: ChartColors,
    hoverState: ChartHoverState,
    canvasSize: IntSize,
    density: Density,
    entrance: ChartEntranceAnimation,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val categories = resolveCategories(data)
    val series = data.series
    // Dual-axis support: rightRange non-null only when some series binds to yAxisIndex=1.
    val (leftRange, rightRange) = deriveDualYRanges(series, options.yRange)
    val (yMin, yMax) = leftRange
    val (yMinRight, yMaxRight) = rightRange ?: (0f to 1f)
    val yAxisTitleRight: String? = null
    val accentFallback = PaletteTheme.colors.textPrimary
    // Pre-resolve in the @Composable scope so the DrawScope stays pure
    // (PaletteTheme.componentThemes is a @Composable getter — cannot be read inside DrawScope).
    val lineStrokePx = with(density) { 2.dp.toPx() }
    val pointRadiusPx = with(density) { 3.dp.toPx() }
    val hoveredRadiusPx = with(density) { 6.dp.toPx() }
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val labelPadPx = with(density) { tokens.axisLabelPadding.toPx() }
    // Axis margins are now label-driven (see ChartAxisRenderer); ticks/titles add room as needed.
    val axisLayout = rememberAxisLayout(
        options = options,
        yMin = yMin,
        yMax = yMax,
        xLabels = categories,
        yRangeRight = rightRange,
        yAxisTitleRight = yAxisTitleRight,
    )
    val tickStyle = ChartDefaults.axisTextStyle()
    val titleStyle = ChartDefaults.axisTitleTextStyle()
    val measurer = rememberTextMeasurer()
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }
    val ringColor = PaletteTheme.colors.onSurface

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
                yMinRight = yMinRight,
                yMaxRight = yMaxRight,
                yAxisTitleRight = yAxisTitleRight,
            )

            // Entrance animation progress: scales each point's lift from the baseline (yMin) so the
            // line "grows" up from the axis. Pinned at 1 when disabled → no effect.
            val progress = entrance.value

            // Per-series point resolver: right-axis series map against the right range. The axis is
            // selected once per series (constant within a series) so the polyline stays coherent.
            fun pointOf(v: Float, i: Int, useRightAxis: Boolean): Offset {
                val x = leftPx + slotW * (i + 0.5f)
                val (lo, hi) = if (useRightAxis) yMinRight to yMaxRight else yMin to yMax
                val lifted = lo + (v - lo) * progress
                val y = baselineY - normalizeValue(lifted, lo, hi) * plotH
                return Offset(x, y)
            }

            // Resolve the hovered point (snaps to nearest marker within a radius).
            val plot = PlotRect(leftPx, topPx, plotW, plotH)
            val hovered = if (hoverState.active) {
                hitTestPoint(
                    mouse = hoverState.anchor,
                    series = series,
                    yMin = yMin,
                    yMax = yMax,
                    categories = categories,
                    plot = plot,
                    seriesColors = seriesColors,
                    yAxisBySeries = series.map { it.yAxisIndex },
                    pointRadiusPx = with(density) { 12.dp.toPx() },
                )
            } else null
            hoverState.target = hovered

            series.forEachIndexed { sIndex, s ->
                val color = seriesColors[sIndex]
                val useRightAxis = rightRange != null && s.yAxisIndex.coerceIn(0, 1) == 1
                val pts = s.values.mapIndexed { i, v -> pointOf(v, i, useRightAxis) }
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
                // Whole-column highlight: when hovering category C, EVERY series' point at C is enlarged
                // + ringed (the fix for "multi-series only highlighted one point"). hovered.categoryIndex
                // is the cursor's slot; points hidden → still surface the column's markers.
                val hoveredCat = hovered?.categoryIndex ?: -1
                if (spec.showPoints) {
                    pts.forEachIndexed { i, p ->
                        val isHovered = i == hoveredCat
                        if (isHovered) {
                            drawCircle(color = color, radius = hoveredRadiusPx, center = p)
                            drawCircle(
                                color = ringColor,
                                radius = hoveredRadiusPx,
                                center = p,
                                style = Stroke(width = with(density) { 1.5.dp.toPx() }),
                            )
                        } else {
                            drawCircle(color = color, radius = pointRadiusPx, center = p)
                        }
                    }
                } else if (hoveredCat >= 0) {
                    // Even with points hidden, surface the hovered column's markers so the tooltip has
                    // a visible anchor on every series.
                    val p = pts.getOrNull(hoveredCat) ?: return@forEachIndexed
                    drawCircle(color = color, radius = hoveredRadiusPx, center = p)
                    drawCircle(
                        color = ringColor,
                        radius = hoveredRadiusPx,
                        center = p,
                        style = Stroke(width = with(density) { 1.5.dp.toPx() }),
                    )
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
