package xyz.junerver.compose.palette.components.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Resolved geometry + label data for a chart's cartesian axes. Shared by [BarChartRenderer] and
 * [LineChartRenderer] so both compute margins identically and never clip tick labels.
 *
 * - [leftPx]/[topPx]/[rightPx]/[bottomPx] are the canvas insets; the plot area is
 *   `[leftPx, leftPx+plotW] × [topPx, topPx+plotH]` derived from the canvas size.
 * - [yTicks] are the numeric tick values (already snapped to a 1/2/5 grid by [niceTicks]); each is
 *   paired with its rendered label text.
 * - [xLabels] are the category labels drawn along the X axis.
 */
internal data class AxisLayout(
    val leftPx: Float,
    val topPx: Float,
    val rightPx: Float,
    val bottomPx: Float,
    val yTicks: List<Pair<Float, String>>,
    val xLabels: List<String>,
)

/**
 * Computes the axis layout (incl. label-driven margins) in the @Composable scope, since text must be
 * measured here. Margins grow only for the annotations actually enabled — a bare chart with no tick
 * labels / titles keeps the original tight `48dp`/`32dp` insets so existing visuals are preserved.
 */
@Composable
internal fun rememberAxisLayout(
    options: ChartOptions,
    yMin: Float,
    yMax: Float,
    xLabels: List<String>,
    horizontal: Boolean = false,
): AxisLayout {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val tickStyle = ChartDefaults.axisTextStyle()
    val titleStyle = ChartDefaults.axisTitleTextStyle()
    val labelPadPx = with(density) { PaletteTheme.componentThemes.chart.axisLabelPadding.toPx() }

    return remember(options, yMin, yMax, xLabels, horizontal, tickStyle, titleStyle) {
        computeAxisLayout(
            options = options,
            yMin = yMin,
            yMax = yMax,
            xLabels = xLabels,
            horizontal = horizontal,
            measurer = measurer,
            tickStyle = tickStyle,
            titleStyle = titleStyle,
            labelPadPx = labelPadPx,
        )
    }
}

/**
 * Pure layout math (separated from the @Composable so the inputs are explicit and stable).
 *
 * [horizontal] swaps which canvas edge the value axis vs category axis lives on:
 * - vertical (default): value ticks on the LEFT, category labels on the BOTTOM.
 * - horizontal: category labels on the LEFT, value ticks on the BOTTOM.
 * The margins are driven by whichever labels actually occupy that edge.
 */
private fun computeAxisLayout(
    options: ChartOptions,
    yMin: Float,
    yMax: Float,
    xLabels: List<String>,
    horizontal: Boolean,
    measurer: TextMeasurer,
    tickStyle: TextStyle,
    titleStyle: TextStyle,
    labelPadPx: Float,
): AxisLayout {
    val baseLeft = 48f
    val baseTop = 12f
    val baseRight = 12f
    val baseBottom = 32f

    val yTicks = if (options.showAxes && options.showTickLabels) {
        niceTicks(yMin, yMax, count = options.tickCount).map { it to formatTickValue(it, options.valueUnit) }
    } else {
        emptyList()
    }

    var leftPx = baseLeft
    var bottomPx = baseBottom

    if (options.showAxes && options.showTickLabels) {
        if (horizontal) {
            // Horizontal: category labels on the LEFT (need width), value ticks on the BOTTOM (need height).
            val maxCatLabelWidth = xLabels.maxOfOrNull { label ->
                measurer.measure(AnnotatedString(label), tickStyle).size.width.toFloat()
            } ?: 0f
            leftPx = max(baseLeft, maxCatLabelWidth + labelPadPx * 2f)

            val maxYTickHeight = yTicks.maxOfOrNull { (_, label) ->
                measurer.measure(AnnotatedString(label), tickStyle).size.height.toFloat()
            } ?: 0f
            bottomPx = max(baseBottom, maxYTickHeight + labelPadPx * 2f)
        } else {
            // Vertical: value ticks on the LEFT (need width), category labels on the BOTTOM (need height).
            val maxYTickWidth = yTicks.maxOfOrNull { (_, label) ->
                measurer.measure(AnnotatedString(label), tickStyle).size.width.toFloat()
            } ?: 0f
            leftPx = max(baseLeft, maxYTickWidth + labelPadPx * 2f)

            val maxXLabelHeight = xLabels.maxOfOrNull { label ->
                measurer.measure(AnnotatedString(label), tickStyle).size.height.toFloat()
            } ?: 0f
            bottomPx = max(baseBottom, maxXLabelHeight + labelPadPx * 2f)
        }
    }
    // Axis titles occupy the same canvas edge regardless of orientation: xAxisTitle (value axis) is
    // always rendered along the value axis — bottom in BOTH orientations below — and yAxisTitle
    // (category axis) always gets rotated room on the left. See drawAxes for the per-orientation
    // placement of the labels themselves.
    if (options.xAxisTitle != null) {
        val titleHeight = measurer.measure(AnnotatedString(options.xAxisTitle), titleStyle).size.height.toFloat()
        bottomPx += titleHeight + labelPadPx
    }
    if (options.yAxisTitle != null) {
        val titleWidth = measurer.measure(AnnotatedString(options.yAxisTitle), titleStyle).size.height.toFloat()
        leftPx += titleWidth + labelPadPx
    }

    return AxisLayout(
        leftPx = leftPx,
        topPx = baseTop,
        rightPx = baseRight,
        bottomPx = bottomPx,
        yTicks = yTicks,
        xLabels = xLabels,
    )
}

/**
 * Draws the cartesian frame: gridlines, axes, tick labels, category labels, and (optionally) axis
 * titles. Plots values against `[yMin, yMax]`. Does NOT draw the data — callers draw their bars/
 * lines into the same plot rectangle afterwards.
 *
 * Orientation ([horizontal]):
 * - vertical (default): value ticks on the LEFT, category labels on the BOTTOM.
 * - horizontal: value ticks on the BOTTOM, category labels on the LEFT (the two axes swap).
 * Axis titles are named by screen position (xAxisTitle = bottom, yAxisTitle = left) regardless of
 * orientation — so a horizontal bar with `xAxisTitle="销售额", yAxisTitle="月份"` correctly labels the
 * bottom value axis and the left category axis.
 *
 * All theme values ([axisStrokePx], [gridStrokePx], [labelPadPx]) MUST be pre-resolved in the
 * @Composable scope — `PaletteTheme.componentThemes` is a @Composable getter and cannot be read from
 * a DrawScope extension.
 */
internal fun DrawScope.drawAxes(
    layout: AxisLayout,
    plotW: Float,
    plotH: Float,
    yMin: Float,
    yMax: Float,
    options: ChartOptions,
    colors: ChartColors,
    measurer: TextMeasurer,
    tickStyle: TextStyle,
    titleStyle: TextStyle,
    axisStrokePx: Float,
    gridStrokePx: Float,
    labelPadPx: Float,
    horizontal: Boolean = false,
) {
    if (!options.showAxes) return
    val left = layout.leftPx
    val top = layout.topPx

    // Gridlines sit on the VALUE axis ticks (aligned with the value labels). Vertical → horizontal
    // gridlines; horizontal → vertical gridlines.
    if (options.showGrid) {
        if (layout.yTicks.isNotEmpty()) {
            for ((tickValue, _) in layout.yTicks) {
                val frac = normalizeValue(tickValue, yMin, yMax)
                if (horizontal) {
                    val x = left + frac * plotW
                    drawLine(colors.gridColor, Offset(x, top), Offset(x, top + plotH), gridStrokePx)
                } else {
                    val y = top + plotH - frac * plotH
                    drawLine(colors.gridColor, Offset(left, y), Offset(left + plotW, y), gridStrokePx)
                }
            }
        } else {
            val ticks = 4
            for (i in 0..ticks) {
                val frac = i / ticks.toFloat()
                if (horizontal) {
                    val x = left + frac * plotW
                    drawLine(colors.gridColor, Offset(x, top), Offset(x, top + plotH), gridStrokePx)
                } else {
                    val y = top + plotH - frac * plotH
                    drawLine(colors.gridColor, Offset(left, y), Offset(left + plotW, y), gridStrokePx)
                }
            }
        }
    }

    // Axis lines (left + bottom) — identical for both orientations.
    drawLine(colors.axisColor, Offset(left, top), Offset(left, top + plotH), axisStrokePx)
    drawLine(colors.axisColor, Offset(left, top + plotH), Offset(left + plotW, top + plotH), axisStrokePx)

    if (!options.showTickLabels) return

    if (horizontal) {
        // Value ticks → BOTTOM (centered under each gridline, horizontally centered).
        for ((tickValue, label) in layout.yTicks) {
            val tl = measurer.measure(AnnotatedString(label), tickStyle)
            val x = left + normalizeValue(tickValue, yMin, yMax) * plotW
            val baseline = top + plotH + labelPadPx
            drawText(
                textLayoutResult = tl,
                color = colors.tickLabelColor,
                topLeft = Offset(x - tl.size.width / 2f, baseline),
            )
        }
        // Category labels → LEFT (right-aligned to the axis, vertically centered in each slot).
        val catCount = layout.xLabels.size.coerceAtLeast(1)
        val slotH = plotH / catCount
        layout.xLabels.forEachIndexed { index, label ->
            val tl = measurer.measure(AnnotatedString(label), tickStyle)
            val cy = top + slotH * (index + 0.5f)
            drawText(
                textLayoutResult = tl,
                color = colors.tickLabelColor,
                topLeft = Offset(left - labelPadPx - tl.size.width, cy - tl.size.height / 2f),
            )
        }
    } else {
        // Value ticks → LEFT (right-aligned to the axis, vertically centered on the gridline).
        for ((tickValue, label) in layout.yTicks) {
            val tl = measurer.measure(AnnotatedString(label), tickStyle)
            val y = top + plotH - normalizeValue(tickValue, yMin, yMax) * plotH
            drawText(
                textLayoutResult = tl,
                color = colors.tickLabelColor,
                topLeft = Offset(left - labelPadPx - tl.size.width, y - tl.size.height / 2f),
            )
        }
        // Category labels → BOTTOM (centered under each slot, just below the baseline).
        val catCount = layout.xLabels.size.coerceAtLeast(1)
        val slotW = plotW / catCount
        layout.xLabels.forEachIndexed { index, label ->
            val tl = measurer.measure(AnnotatedString(label), tickStyle)
            val cx = left + slotW * (index + 0.5f)
            val baseline = top + plotH + labelPadPx
            drawText(
                textLayoutResult = tl,
                color = colors.tickLabelColor,
                topLeft = Offset(cx - tl.size.width / 2f, baseline),
            )
        }
    }

    // X axis title: centered below the BOTTOM labels (value labels in horizontal, category in vertical).
    if (options.xAxisTitle != null) {
        val titleLayout = measurer.measure(AnnotatedString(options.xAxisTitle), titleStyle)
        val bottomLabels = if (horizontal) {
            layout.yTicks.map { it.second }
        } else {
            layout.xLabels
        }
        val bottomLabelHeight = (bottomLabels.maxOfOrNull { label ->
            measurer.measure(AnnotatedString(label), tickStyle).size.height
        } ?: 0).toFloat()
        val titleY = top + plotH + labelPadPx + bottomLabelHeight + labelPadPx
        drawText(
            textLayoutResult = titleLayout,
            color = colors.axisTitleColor,
            topLeft = Offset(left + plotW / 2f - titleLayout.size.width / 2f, titleY),
        )
    }

    // Y axis title: rotated -90° on the far LEFT, vertically centered with the plot (category labels
    // in horizontal, value labels in vertical).
    if (options.yAxisTitle != null) {
        val titleLayout = measurer.measure(AnnotatedString(options.yAxisTitle), titleStyle)
        val cy = top + plotH / 2f
        val cx = labelPadPx // hugging the left edge
        rotate(degrees = -90f, pivot = Offset(cx, cy)) {
            drawText(
                textLayoutResult = titleLayout,
                color = colors.axisTitleColor,
                topLeft = Offset(cx - titleLayout.size.width / 2f, cy - titleLayout.size.height / 2f),
            )
        }
    }
}
