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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
 * - [yTicks] are the LEFT-axis numeric tick values (already snapped to a 1/2/5 grid by [niceTicks]);
 *   each is paired with its rendered label text.
 * - [yTicksRight] are the RIGHT-axis ticks (non-empty only in dual-axis mode, i.e. when some series
 *   binds to yAxisIndex=1). Empty for single-axis charts → no right margin, no right labels.
 * - [xLabels] are the category labels drawn along the X axis.
 */
internal data class AxisLayout(
    val leftPx: Float,
    val topPx: Float,
    val rightPx: Float,
    val bottomPx: Float,
    val yTicks: List<Pair<Float, String>>,
    val xLabels: List<String>,
    val yTicksRight: List<Pair<Float, String>> = emptyList(),
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
    yRangeRight: Pair<Float, Float>? = null,
    yAxisTitleRight: String? = null,
): AxisLayout {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val tickStyle = ChartDefaults.axisTextStyle()
    val titleStyle = ChartDefaults.axisTitleTextStyle()
    val labelPadPx = with(density) { PaletteTheme.componentThemes.chart.axisLabelPadding.toPx() }

    return remember(options, yMin, yMax, xLabels, horizontal, yRangeRight, yAxisTitleRight, tickStyle, titleStyle) {
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
            yRangeRight = yRangeRight,
            yAxisTitleRight = yAxisTitleRight,
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
 *
 * Dual-axis: when [yRangeRight] is non-null a second value axis is laid out on the RIGHT edge
 * (vertical) / TOP edge (horizontal), and [yAxisTitleRight] reserves extra room like the left title.
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
    yRangeRight: Pair<Float, Float>? = null,
    yAxisTitleRight: String? = null,
): AxisLayout {
    val baseLeft = 48f
    val baseTop = 12f
    val baseRight = 12f
    val baseBottom = 32f

    val yTicks = if (options.showAxes && options.showTickLabels) {
        if (yRangeRight != null) {
            // DUAL-AXIS: both axes share EVEN fractions so their grid lines align pixel-for-pixel.
            // (Independent niceTicks per axis would produce mismatched step counts → misaligned ticks.)
            evenTickFractions(options.tickCount).map { f ->
                fractionToAxisValue(f, yMin, yMax) to formatTickValue(fractionToAxisValue(f, yMin, yMax), options.valueUnit)
            }
        } else {
            niceTicks(yMin, yMax, count = options.tickCount).map { it to formatTickValue(it, options.valueUnit) }
        }
    } else {
        emptyList()
    }
    // Right-axis ticks (dual mode). Shares the SAME even fractions as the left axis so each left tick
    // at fraction f sits at the same height as the right tick at fraction f.
    val yTicksRight: List<Pair<Float, String>> = if (yRangeRight != null && options.showAxes && options.showTickLabels) {
        val (rMin, rMax) = yRangeRight
        evenTickFractions(options.tickCount).map { f ->
            fractionToAxisValue(f, rMin, rMax) to formatTickValue(fractionToAxisValue(f, rMin, rMax), options.valueUnit)
        }
    } else {
        emptyList()
    }

    var leftPx = baseLeft
    var bottomPx = baseBottom
    var rightPx = baseRight
    var topPx = baseTop

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
            // Dual-axis in horizontal mode: the right axis moves to the TOP edge.
            if (yTicksRight.isNotEmpty()) {
                val rightTickHeight = yTicksRight.maxOfOrNull { (_, label) ->
                    measurer.measure(AnnotatedString(label), tickStyle).size.height.toFloat()
                } ?: 0f
                topPx = max(baseTop, rightTickHeight + labelPadPx * 2f)
            }
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
            // Dual-axis in vertical mode: the right axis occupies the RIGHT edge.
            if (yTicksRight.isNotEmpty()) {
                val rightTickWidth = yTicksRight.maxOfOrNull { (_, label) ->
                    measurer.measure(AnnotatedString(label), tickStyle).size.width.toFloat()
                } ?: 0f
                rightPx = max(baseRight, rightTickWidth + labelPadPx * 2f)
            }
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
    // Right-axis title (dual mode): reserves room on the right (vertical) / top (horizontal).
    if (yAxisTitleRight != null) {
        val titleSize = measurer.measure(AnnotatedString(yAxisTitleRight), titleStyle).size.height.toFloat()
        if (horizontal) topPx += titleSize + labelPadPx else rightPx += titleSize + labelPadPx
    }

    return AxisLayout(
        leftPx = leftPx,
        topPx = topPx,
        rightPx = rightPx,
        bottomPx = bottomPx,
        yTicks = yTicks,
        xLabels = xLabels,
        yTicksRight = yTicksRight,
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
    yMinRight: Float = 0f,
    yMaxRight: Float = 0f,
    yAxisTitleRight: String? = null,
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

    // Dual-axis: the RIGHT value axis (its ticks live in layout.yTicksRight). Only drawn when present.
    if (layout.yTicksRight.isNotEmpty()) {
        val rightEdge = left + plotW
        if (horizontal) {
            // Right axis on the TOP edge (horizontal mode): ticks centered above each gridline.
            for ((tickValue, label) in layout.yTicksRight) {
                val tl = measurer.measure(AnnotatedString(label), tickStyle)
                val x = left + normalizeValue(tickValue, yMinRight, yMaxRight) * plotW
                drawText(
                    textLayoutResult = tl,
                    color = colors.tickLabelColor,
                    topLeft = Offset(x - tl.size.width / 2f, top - labelPadPx - tl.size.height),
                )
            }
        } else {
            // Right axis on the RIGHT edge (vertical mode): ticks left-aligned just outside the plot.
            for ((tickValue, label) in layout.yTicksRight) {
                val tl = measurer.measure(AnnotatedString(label), tickStyle)
                val y = top + plotH - normalizeValue(tickValue, yMinRight, yMaxRight) * plotH
                drawText(
                    textLayoutResult = tl,
                    color = colors.tickLabelColor,
                    topLeft = Offset(rightEdge + labelPadPx, y - tl.size.height / 2f),
                )
            }
            // Right axis line.
            drawLine(colors.axisColor, Offset(rightEdge, top), Offset(rightEdge, top + plotH), axisStrokePx)
        }
        if (yAxisTitleRight != null) {
            val titleLayout = measurer.measure(AnnotatedString(yAxisTitleRight), titleStyle)
            if (horizontal) {
                // Top title, centered horizontally.
                drawText(
                    textLayoutResult = titleLayout,
                    color = colors.axisTitleColor,
                    topLeft = Offset(left + plotW / 2f - titleLayout.size.width / 2f, 0f),
                )
            } else {
                // Right title, rotated +90° hugging the right edge.
                val cy = top + plotH / 2f
                val cx = rightEdge + layout.rightPx - labelPadPx
                rotate(degrees = 90f, pivot = Offset(cx, cy)) {
                    drawText(
                        textLayoutResult = titleLayout,
                        color = colors.axisTitleColor,
                        topLeft = Offset(cx - titleLayout.size.width / 2f, cy - titleLayout.size.height / 2f),
                    )
                }
            }
        }
    }

    // Reference annotation lines (averages / targets / thresholds). Drawn AFTER the axes+grid so
    // they sit on top of the data-area guides but below the data marks (callers draw data after).
    drawMarkLines(
        layout = layout,
        plotW = plotW,
        plotH = plotH,
        yMin = yMin,
        yMax = yMax,
        options = options,
        colors = colors,
        measurer = measurer,
        tickStyle = tickStyle,
        labelPadPx = labelPadPx,
        horizontal = horizontal,
    )
}

/**
 * Draws [ChartOptions.markLines] over the plot rectangle. A [MarkLineAxis.Value] line spans the
 * category axis at the given Y position (horizontal in a vertical chart, vertical in a horizontal
 * one); a [MarkLineAxis.Category] line spans the value axis at the given category slot.
 *
 * Dashed strokes use a [PathEffect] so the convention "dashed = reference/target" reads clearly.
 * The optional [MarkLine.label] is drawn at the line's end inside the plot so it doesn't collide
 * with the axis tick labels. Lines outside the data range are skipped (no clipping artifact).
 */
internal fun DrawScope.drawMarkLines(
    layout: AxisLayout,
    plotW: Float,
    plotH: Float,
    yMin: Float,
    yMax: Float,
    options: ChartOptions,
    colors: ChartColors,
    measurer: TextMeasurer,
    tickStyle: TextStyle,
    labelPadPx: Float,
    horizontal: Boolean = false,
) {
    if (options.markLines.isEmpty()) return
    val left = layout.leftPx
    val top = layout.topPx
    val catCount = layout.xLabels.size.coerceAtLeast(1)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
    val lineColor = colors.seriesLabelColor

    for (mark in options.markLines) {
        val color = if (mark.color == androidx.compose.ui.graphics.Color.Unspecified) lineColor else mark.color
        when (mark.axis) {
            MarkLineAxis.Value -> {
                val frac = normalizeValue(mark.position, yMin, yMax)
                if (frac !in 0f..1f) continue
                if (horizontal) {
                    // Value axis is X in a horizontal chart → vertical line at x = left + frac*plotW.
                    val x = left + frac * plotW
                    drawLine(color, Offset(x, top), Offset(x, top + plotH), strokeWidth = 1.5f, pathEffect = if (mark.dashed) dashEffect else null)
                } else {
                    val y = top + plotH - frac * plotH
                    drawLine(color, Offset(left, y), Offset(left + plotW, y), strokeWidth = 1.5f, pathEffect = if (mark.dashed) dashEffect else null)
                }
                if (mark.label != null) {
                    val tl = measurer.measure(AnnotatedString(mark.label), tickStyle)
                    // Place at the right edge (vertical) / top edge (horizontal), just inside the plot.
                    if (horizontal) {
                        drawText(tl, color = color, topLeft = Offset(left + plotW - tl.size.width - labelPadPx, top + labelPadPx))
                    } else {
                        drawText(tl, color = color, topLeft = Offset(left + plotW - tl.size.width - labelPadPx, top + labelPadPx))
                    }
                }
            }
            MarkLineAxis.Category -> {
                val frac = (mark.position + 0.5f) / catCount
                if (frac !in 0f..1f) continue
                if (horizontal) {
                    val y = top + frac * plotH
                    drawLine(color, Offset(left, y), Offset(left + plotW, y), strokeWidth = 1.5f, pathEffect = if (mark.dashed) dashEffect else null)
                } else {
                    val x = left + frac * plotW
                    drawLine(color, Offset(x, top), Offset(x, top + plotH), strokeWidth = 1.5f, pathEffect = if (mark.dashed) dashEffect else null)
                }
            }
        }
    }
}
