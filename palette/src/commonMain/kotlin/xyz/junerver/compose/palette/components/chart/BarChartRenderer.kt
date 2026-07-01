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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Renders a bar / column chart. Series are drawn side-by-side (grouped) by default, or stacked when
 * [ChartSpec.Bar.stacked] is set. [ChartSpec.Bar.horizontal] swaps the axis orientation.
 *
 * Axes, gridlines and y-range derive from the mermaid XY-chart pattern (drawLine grid + drawRect
 * bars) but route every color/dimension through the chart theme tokens.
 *
 * When [hoverState] is active, the bar under the pointer is resolved via [hitTestPoint] and stroked
 * with a theme-driven highlight for tooltip feedback.
 */
@Composable
internal fun BarChartRenderer(
    spec: ChartSpec.Bar,
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
    // Dual-axis: split series by yAxisIndex. rightRange is non-null only when some series binds
    // to axis 1 — otherwise this collapses to plain single-axis behavior (rightRange = null).
    val (leftRange, rightRange) = deriveDualYRanges(series, options.yRange, stacked = spec.stacked)
    val (yMin, yMax) = leftRange
    val (yMinRight, yMaxRight) = rightRange ?: (0f to 1f)
    val yAxisTitleRight: String? = null // surfaced via a dedicated option when needed
    val accentFallback = PaletteTheme.colors.textPrimary
    // Pre-resolve Dp→Px and the per-series colors in the @Composable scope so the DrawScope is pure
    // (PaletteTheme.componentThemes is a @Composable getter — cannot be read inside DrawScope).
    val barRadiusPx = with(density) { tokens.barCornerRadius.toPx() }
    val highlightStrokePx = with(density) { tokens.highlightStrokeWidth.toPx() }
    val axisStrokePx = with(density) { tokens.axisStrokeWidth.toPx() }
    val gridStrokePx = with(density) { tokens.gridStrokeWidth.toPx() }
    val labelPadPx = with(density) { tokens.axisLabelPadding.toPx() }
    // Axis margins are now label-driven (see ChartAxisRenderer); ticks/titles add room as needed.
    val axisLayout = rememberAxisLayout(
        options = options,
        yMin = yMin,
        yMax = yMax,
        xLabels = categories,
        horizontal = spec.horizontal,
        yRangeRight = rightRange,
        yAxisTitleRight = yAxisTitleRight,
    )
    val tickStyle = ChartDefaults.axisTextStyle()
    val titleStyle = ChartDefaults.axisTitleTextStyle()
    val measurer = rememberTextMeasurer()
    val seriesColors = series.mapIndexed { i, s ->
        resolveSeriesColor(s, i, colors.categoricalColors, accentFallback)
    }
    val highlightColor = PaletteTheme.colors.onSurface

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
                yMinRight = yMinRight,
                yMaxRight = yMaxRight,
                yAxisTitleRight = yAxisTitleRight,
            )

            val seriesCount = series.size.coerceAtLeast(1)

            // Resolve the hovered category once per frame so we can highlight every bar in that slot.
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
                    horizontal = spec.horizontal,
                    stacked = spec.stacked,
                )
            } else null
            hoverState.target = hovered

            // Entrance animation: scale every value by the progress so bars grow from the baseline.
            // When the animation is disabled the progress is pinned at 1 → no visual/math change.
            val progress = entrance.value

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
                        // Scale both the running stack and the segment value so the whole column
                        // grows uniformly from 0 → its final stacked height.
                        val g = barLayout(
                            catCount = catCount,
                            catIndex = catIndex,
                            seriesCount = seriesCount,
                            sIndex = sIndex,
                            value = v * progress,
                            accValue = acc * progress,
                            yMin = yMin,
                            yMax = yMax,
                            stacked = true,
                        )
                        val isHovered = hovered != null && hovered.categoryIndex == catIndex
                        drawBar(g, spec.horizontal, leftPx, topPx, plotW, plotH, color, barRadiusPx, isHovered, highlightColor, highlightStrokePx)
                        acc += v
                    }
                } else {
                    series.forEachIndexed { sIndex, s ->
                        val v = s.values.getOrNull(catIndex) ?: return@forEachIndexed
                        val color = seriesColors[sIndex]
                        // Pick the value range for THIS series' axis (dual-axis support). Right-axis
                        // series (yAxisIndex=1) map against the right range; everything else the left.
                        val (sMin, sMax) = if (rightRange != null && s.yAxisIndex.coerceIn(0, 1) == 1) {
                            yMinRight to yMaxRight
                        } else {
                            yMin to yMax
                        }
                        val g = barLayout(
                            catCount = catCount,
                            catIndex = catIndex,
                            seriesCount = seriesCount,
                            sIndex = sIndex,
                            value = v * progress,
                            accValue = 0f,
                            yMin = sMin,
                            yMax = sMax,
                            stacked = false,
                        )
                        val isHovered = hovered != null && hovered.categoryIndex == catIndex
                        drawBar(g, spec.horizontal, leftPx, topPx, plotW, plotH, color, barRadiusPx, isHovered, highlightColor, highlightStrokePx)
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
    isHovered: Boolean,
    highlightColor: Color,
    highlightStrokePx: Float,
) {
    val (topLeft, sz) = if (horizontal) {
        // value axis → X, category axis → Y.
        val x = leftPx + g.start * plotW
        val w = g.extent * plotW
        val h = g.crossSize * plotH
        val y = topPx + g.crossCenter * plotH - h / 2f
        Offset(x, y) to Size(w, h)
    } else {
        // value axis → Y (grows upward), category axis → X.
        val w = g.crossSize * plotW
        val h = g.extent * plotH
        val x = leftPx + g.crossCenter * plotW - w / 2f
        val y = topPx + plotH - (g.start + g.extent) * plotH
        Offset(x, y) to Size(w, h)
    }
    drawRoundBar(color = color, topLeft = topLeft, size = sz, radiusPx = radiusPx)
    if (isHovered) {
        // A bright outline + slight lift marks the hovered bar(s).
        val r = radiusPx.coerceAtMost(sz.width / 2f).coerceAtMost(sz.height / 2f).coerceAtLeast(0f)
        drawRoundRect(
            color = highlightColor,
            topLeft = topLeft,
            size = sz,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
            style = Stroke(width = highlightStrokePx),
        )
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
