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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
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
    val (yMin, yMax) = deriveYRange(series, options.yRange, stacked = spec.stacked)
    val accentFallback = PaletteTheme.colors.textPrimary
    // Pre-resolve Dp→Px and the per-series colors in the @Composable scope so the DrawScope is pure
    // (PaletteTheme.componentThemes is a @Composable getter — cannot be read inside DrawScope).
    val barRadiusPx = with(density) { tokens.barCornerRadius.toPx() }
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val labelPadPx = with(density) { tokens.axisLabelPadding.toPx() }
    // Axis margins are now label-driven (see ChartAxisRenderer); ticks/titles add room as needed.
    val axisLayout = rememberAxisLayout(options, yMin, yMax, categories, horizontal = spec.horizontal)
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
            val rightPx = axisLayout.rightPx
            val bottomPx = axisLayout.bottomPx
            val plotW = (size.width - leftPx - rightPx).coerceAtLeast(1f)
            val plotH = (size.height - topPx - bottomPx).coerceAtLeast(1f)

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
                horizontal = spec.horizontal,
            )

            val seriesCount = series.size.coerceAtLeast(1)

            // Geometry is computed once by the tested, orientation-agnostic barLayout(); here we only
            // map its fractions onto the canvas axes. For vertical bars the value axis is Y and the
            // category axis is X; horizontal swaps them. plotW/plotH are used for the CORRECT axis in
            // each direction (horizontal previously used width for category positions — a bug).
            categories.forEachIndexed { catIndex, _ ->
                if (spec.stacked) {
                    var acc = 0f
                    series.forEachIndexed { sIndex, s ->
                        val v = s.values.getOrNull(catIndex) ?: return@forEachIndexed
                        val color = seriesColors[sIndex]
                        val g = barLayout(
                            catCount = catCount,
                            catIndex = catIndex,
                            seriesCount = seriesCount,
                            sIndex = sIndex,
                            value = v,
                            accValue = acc,
                            yMin = yMin,
                            yMax = yMax,
                            stacked = true,
                        )
                        drawBar(g, spec.horizontal, leftPx, topPx, plotW, plotH, color, barRadiusPx)
                        acc += v
                    }
                } else {
                    series.forEachIndexed { sIndex, s ->
                        val v = s.values.getOrNull(catIndex) ?: return@forEachIndexed
                        val color = seriesColors[sIndex]
                        val g = barLayout(
                            catCount = catCount,
                            catIndex = catIndex,
                            seriesCount = seriesCount,
                            sIndex = sIndex,
                            value = v,
                            accValue = 0f,
                            yMin = yMin,
                            yMax = yMax,
                            stacked = false,
                        )
                        drawBar(g, spec.horizontal, leftPx, topPx, plotW, plotH, color, barRadiusPx)
                    }
                }
            }
        }
    }
}

/**
 * Maps a [BarLayout] (fractions) onto the canvas. Vertical: value on Y, category on X. Horizontal:
 * value on X, category on Y — the two axes are swapped, but the SAME layout fractions are used.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBar(
    g: BarLayout,
    horizontal: Boolean,
    leftPx: Float,
    topPx: Float,
    plotW: Float,
    plotH: Float,
    color: Color,
    radiusPx: Float,
) {
    if (horizontal) {
        // value axis → X, category axis → Y.
        val x = leftPx + g.start * plotW
        val w = g.extent * plotW
        val h = g.crossSize * plotH
        val y = topPx + g.crossCenter * plotH - h / 2f
        drawRoundBar(color = color, topLeft = Offset(x, y), size = Size(w, h), radiusPx = radiusPx)
    } else {
        // value axis → Y (grows upward), category axis → X.
        val w = g.crossSize * plotW
        val h = g.extent * plotH
        val x = leftPx + g.crossCenter * plotW - w / 2f
        val y = topPx + plotH - (g.start + g.extent) * plotH
        drawRoundBar(color = color, topLeft = Offset(x, y), size = Size(w, h), radiusPx = radiusPx)
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
