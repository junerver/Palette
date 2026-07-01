package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.max
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a scatter chart. Unlike bar/line, BOTH axes are numeric: X spans the scatter bounds'
 * horizontal range, Y spans the vertical range. Each series is a set of independent (x, y) markers
 * (parsed from the flat values list by [scatterPairs]).
 *
 * The cartesian frame (grid + axes + numeric ticks on both axes) is drawn inline rather than via
 * [drawAxes], because [drawAxes] assumes a categorical X axis. The numeric-tick math reuses the
 * tested [niceTicks] / [formatTickValue] so the labels stay consistent with the rest of the family.
 *
 * Hover resolves the nearest marker via [hitTestScatter] and renders it larger + ringed.
 */
@Composable
internal fun ScatterChartRenderer(
    spec: ChartSpec.Scatter,
    data: ChartData,
    modifier: Modifier,
    options: ChartOptions,
    colors: ChartColors,
    hoverState: ChartHoverState,
    @Suppress("UNUSED_PARAMETER") canvasSize: IntSize,
    density: Density,
    entrance: ChartEntranceAnimation,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val series = data.series
    val bounds = scatterBounds(series)
    val accentFallback = PaletteTheme.colors.textPrimary
    val pointRadiusPx = with(density) { spec.pointSize.dp.toPx() }
    val hoveredRadiusPx = with(density) { (spec.pointSize * 2f).dp.toPx() }
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val labelPadPx = with(density) { tokens.axisLabelPadding.toPx() }
    val tickStyle = ChartDefaults.axisTextStyle()
    val titleStyle = ChartDefaults.axisTitleTextStyle()
    val measurer = rememberTextMeasurer()
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }
    val ringColor = PaletteTheme.colors.onSurface

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (series.isEmpty() || bounds == null) return@Canvas
            val (xRange, yRange) = bounds
            val (xMin, xMax) = xRange
            val (yMin, yMax) = yRange

            // Margin: measure widest tick label on each axis so nothing clips.
            val xTicks = if (options.showAxes && options.showTickLabels) niceTicks(xMin, xMax, count = options.tickCount) else emptyList()
            val yTicks = if (options.showAxes && options.showTickLabels) niceTicks(yMin, yMax, count = options.tickCount) else emptyList()
            val xTickLabels = xTicks.map { formatTickValue(it) }
            val yTickLabels = yTicks.map { formatTickValue(it, options.valueUnit) }
            val maxYLabelWidth = yTickLabels.maxOfOrNull { measurer.measure(AnnotatedString(it), tickStyle).size.width.toFloat() } ?: 0f
            val maxXLabelHeight = xTickLabels.maxOfOrNull { measurer.measure(AnnotatedString(it), tickStyle).size.height.toFloat() } ?: 0f

            var leftPx = 48f
            val topPx = 12f
            val rightPx = 12f
            var bottomPx = 32f
            if (options.showAxes && options.showTickLabels) {
                leftPx = max(leftPx, maxYLabelWidth + labelPadPx * 2f)
                bottomPx = max(bottomPx, maxXLabelHeight + labelPadPx * 2f)
            }
            if (options.xAxisTitle != null) {
                bottomPx += measurer.measure(AnnotatedString(options.xAxisTitle), titleStyle).size.height + labelPadPx
            }
            if (options.yAxisTitle != null) {
                leftPx += measurer.measure(AnnotatedString(options.yAxisTitle), titleStyle).size.height + labelPadPx
            }

            val plotW = (size.width - leftPx - rightPx).coerceAtLeast(1f)
            val plotH = (size.height - topPx - bottomPx).coerceAtLeast(1f)
            val plot = PlotRect(leftPx, topPx, plotW, plotH)

            // Grid + axis lines.
            if (options.showGrid) {
                for (tv in yTicks) {
                    val y = topPx + plotH - normalizeValue(tv, yMin, yMax) * plotH
                    drawLine(colors.gridColor, Offset(leftPx, y), Offset(leftPx + plotW, y), gridStrokePx)
                }
                for (tv in xTicks) {
                    val x = leftPx + normalizeValue(tv, xMin, xMax) * plotW
                    drawLine(colors.gridColor, Offset(x, topPx), Offset(x, topPx + plotH), gridStrokePx)
                }
            }
            if (options.showAxes) {
                drawLine(colors.axisColor, Offset(leftPx, topPx), Offset(leftPx, topPx + plotH), axisStrokePx)
                drawLine(colors.axisColor, Offset(leftPx, topPx + plotH), Offset(leftPx + plotW, topPx + plotH), axisStrokePx)
            }

            // Numeric tick labels: Y on the LEFT, X on the BOTTOM.
            if (options.showTickLabels) {
                for ((i, tv) in yTicks.withIndex()) {
                    val tl = measurer.measure(AnnotatedString(yTickLabels[i]), tickStyle)
                    val y = topPx + plotH - normalizeValue(tv, yMin, yMax) * plotH
                    drawText(tl, color = colors.tickLabelColor, topLeft = Offset(leftPx - labelPadPx - tl.size.width, y - tl.size.height / 2f))
                }
                for ((i, tv) in xTicks.withIndex()) {
                    val tl = measurer.measure(AnnotatedString(xTickLabels[i]), tickStyle)
                    val x = leftPx + normalizeValue(tv, xMin, xMax) * plotW
                    drawText(tl, color = colors.tickLabelColor, topLeft = Offset(x - tl.size.width / 2f, topPx + plotH + labelPadPx))
                }
            }
            // Axis titles (mirror drawAxes placement).
            if (options.xAxisTitle != null) {
                val tl = measurer.measure(AnnotatedString(options.xAxisTitle), titleStyle)
                val titleY = topPx + plotH + labelPadPx + (maxXLabelHeight) + labelPadPx
                drawText(tl, color = colors.axisTitleColor, topLeft = Offset(leftPx + plotW / 2f - tl.size.width / 2f, titleY))
            }
            if (options.yAxisTitle != null) {
                val tl = measurer.measure(AnnotatedString(options.yAxisTitle), titleStyle)
                val cx = labelPadPx
                val cy = topPx + plotH / 2f
                rotate(degrees = -90f, pivot = Offset(cx, cy)) {
                    drawText(tl, color = colors.axisTitleColor, topLeft = Offset(cx - tl.size.width / 2f, cy - tl.size.height / 2f))
                }
            }

            // Entrance: markers fade/scale in by expanding their radius with progress.
            val progress = entrance.value

            // Resolve hover (nearest marker within a snap radius).
            val hovered = if (hoverState.active) {
                hitTestScatter(
                    mouse = hoverState.anchor,
                    series = series,
                    xRange = xMin to xMax,
                    yRange = yMin to yMax,
                    plot = plot,
                    radiusPx = with(density) { 16.dp.toPx() },
                )
            } else null
            hoverState.target = hovered

            // Draw markers.
            series.forEachIndexed { sIndex, s ->
                val color = seriesColors[sIndex]
                val r = pointRadiusPx * progress
                if (r <= 0f) return@forEachIndexed
                scatterPairs(s.values).forEachIndexed { pairIndex, (x, y) ->
                    val px = leftPx + normalizeValue(x, xMin, xMax) * plotW
                    val py = topPx + plotH - normalizeValue(y, yMin, yMax) * plotH
                    val isHovered = hovered != null && hovered.seriesIndex == sIndex && hovered.categoryIndex == pairIndex
                    if (isHovered) {
                        drawCircle(color = color, radius = hoveredRadiusPx, center = Offset(px, py))
                        drawCircle(
                            color = ringColor,
                            radius = hoveredRadiusPx,
                            center = Offset(px, py),
                            style = Stroke(width = with(density) { 1.5.dp.toPx() }),
                        )
                    } else {
                        drawCircle(color = color, radius = r, center = Offset(px, py))
                    }
                }
            }
        }
    }
}
