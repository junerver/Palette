package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a bar / column chart. Series are drawn side-by-side (grouped) by default, or stacked when
 * [ChartSpec.Bar.stacked] is set. [ChartSpec.Bar.horizontal] swaps the axis orientation.
 *
 * Axes, gridlines and y-range derive from the mermaid XY-chart pattern (drawLine grid + drawRect
 * bars) but route every color/dimension through the chart theme tokens.
 */
@Composable
internal fun BarChartRenderer(
    spec: ChartSpec.Bar,
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
    // Pre-resolve Dp→Px and the per-series colors in the @Composable scope so the DrawScope is pure.
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val barRadiusPx = with(density) { tokens.barCornerRadius.toPx() }
    val leftPx = with(density) { 48.dp.toPx() }
    val topPx = with(density) { 12.dp.toPx() }
    val rightPx = with(density) { 12.dp.toPx() }
    val bottomPx = with(density) { 32.dp.toPx() }
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (series.isEmpty()) return@Canvas
            val catCount = categories.size.coerceAtLeast(1)
            val plotW = (size.width - leftPx - rightPx).coerceAtLeast(1f)
            val plotH = (size.height - topPx - bottomPx).coerceAtLeast(1f)

            // Grid + baseline.
            if (options.showGrid) {
                val ticks = 4
                for (i in 0..ticks) {
                    val frac = i / ticks.toFloat()
                    val y = topPx + plotH - frac * plotH
                    drawLine(gridColor, Offset(leftPx, y), Offset(leftPx + plotW, y), gridStrokePx)
                }
            }
            if (options.showAxes) {
                drawLine(axisColor, Offset(leftPx, topPx), Offset(leftPx, topPx + plotH), axisStrokePx)
                drawLine(axisColor, Offset(leftPx, topPx + plotH), Offset(leftPx + plotW, topPx + plotH), axisStrokePx)
            }

            val slotW = plotW / catCount
            val seriesCount = series.size.coerceAtLeast(1)

            categories.forEachIndexed { catIndex, _ ->
                if (spec.stacked) {
                    var acc = 0f
                    series.forEachIndexed { sIndex, s ->
                        val v = s.values.getOrNull(catIndex) ?: return@forEachIndexed
                        val color = seriesColors[sIndex]
                        val norm = normalizeValue(v, yMin, yMax)
                        val barH = norm * plotH
                        if (spec.horizontal) {
                            val x0 = leftPx + normalizeValue(acc, yMin, yMax) * plotW
                            drawRoundBar(
                                color = color,
                                topLeft = Offset(x0, topPx + slotW * catIndex + slotW * 0.15f),
                                size = Size(barH, slotW * 0.7f),
                                radiusPx = barRadiusPx,
                            )
                        } else {
                            val baseY = topPx + plotH - normalizeValue(acc, yMin, yMax) * plotH
                            drawRoundBar(
                                color = color,
                                topLeft = Offset(leftPx + slotW * catIndex + slotW * 0.15f, baseY - barH),
                                size = Size(slotW * 0.7f, barH),
                                radiusPx = barRadiusPx,
                            )
                        }
                        acc += v
                    }
                } else {
                    val groupW = slotW * 0.7f
                    val barW = (groupW / seriesCount).coerceAtLeast(1f)
                    series.forEachIndexed { sIndex, s ->
                        val v = s.values.getOrNull(catIndex) ?: return@forEachIndexed
                        val color = seriesColors[sIndex]
                        val norm = normalizeValue(v, yMin, yMax)
                        val len = norm * plotH
                        if (spec.horizontal) {
                            val y = topPx + slotW * catIndex + slotW * 0.15f + barW * sIndex
                            drawRoundBar(
                                color = color,
                                topLeft = Offset(leftPx, y),
                                size = Size(len, barW),
                                radiusPx = barRadiusPx,
                            )
                        } else {
                            val x = leftPx + slotW * catIndex + slotW * 0.15f + barW * sIndex
                            drawRoundBar(
                                color = color,
                                topLeft = Offset(x, topPx + plotH - len),
                                size = Size(barW, len),
                                radiusPx = barRadiusPx,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Draws a bar with rounded top corners (vertical) or leading edge (horizontal). */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRoundBar(
    color: Color,
    topLeft: Offset,
    size: Size,
    radiusPx: Float,
) {
    drawRect(color = color, topLeft = topLeft, size = size)
    // Subtle rounded cap via an overlaid rounded rect of the same color.
    val r = radiusPx.coerceAtMost(size.width / 2f).coerceAtMost(size.height / 2f).coerceAtLeast(0f)
    if (r > 0f) {
        drawRoundRect(
            color = color,
            topLeft = topLeft,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
        )
    }
}
