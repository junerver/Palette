package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * The interactive state shared between [PChart] and its tooltip overlay.
 *
 * The renderers write the hovered point here during `pointerInput` (the canvas cannot host a
 * composable tooltip), and [ChartTooltipOverlay] reads it back to position + render the floating
 * readout.
 *
 * Native `remember { … }` + `mutableStateOf` is used here (rather than `useState`) because this is a
 * *stable state holder* object whose individual fields are observed by Compose — `useState` models a
 * single replaceable value, not a bag of observed properties. This is the documented exception in
 * AGENTS.md for "stable state holding in interface/class implementations".
 */
@Stable
internal class ChartHoverState {
    /** The currently hovered data point, or `null` when the pointer is over empty space / outside. */
    var target: HitTarget? by mutableStateOf(null)
        internal set
    /** Pointer position in canvas pixels, used to anchor the tooltip near the cursor. */
    var anchor: Offset by mutableStateOf(Offset.Zero)
        internal set
    /** Whether the pointer is currently inside the chart canvas. */
    var active: Boolean by mutableStateOf(false)
        internal set

    /** Convenience: a tooltip should render only when active AND a target was hit. */
    val isVisible: Boolean get() = active && target != null
}

/**
 * Creates a [ChartHoverState] that survives recomposition, shared across the chart + overlay pair.
 */
@Composable
internal fun rememberChartHoverState(): ChartHoverState = remember { ChartHoverState() }

/**
 * The floating value readout shown when the pointer hovers a data point.
 *
 * Rendered as an overlay ABOVE the chart canvas (not inside `DrawScope`, which cannot host
 * composables). All visuals — background, text color, border, elevation, corner radius, padding,
 * type style — derive from [PaletteChartTokens] so the tooltip restyles with the theme and accepts
 * no hardcoded values. The overlay does not depend on any third-party library.
 *
 * Positioning: the tooltip follows [state.anchor] (the cursor), offset so it sits above-right of
 * the pointer and stays inside the chart box via [offsetFor]. A small leader gap keeps it from
 * covering the highlighted element.
 *
 * @param state the hover state shared with the chart's pointer input.
 * @param data the chart data (used to resolve the series label).
 * @param modifier outer modifier (fills the chart canvas so anchors share its coordinate space).
 * @param colors token-backed color bundle (drives tooltip visuals via its derived token fields).
 */
@Composable
internal fun ChartTooltipOverlay(
    state: ChartHoverState,
    @Suppress("UNUSED_PARAMETER") data: ChartData,
    @Suppress("UNUSED_PARAMETER") colors: ChartColors,
    modifier: Modifier = Modifier,
) {
    val target = state.target
    if (target == null || !state.active) return

    val tokens = PaletteTheme.componentThemes.chart
    val density = LocalDensity.current
    val gapPx = with(density) { 12.dp.toPx() }
    val edgePaddingPx = with(density) { 4.dp.toPx() }
    val anchor = state.anchor
    val dotSize = 8.dp
    val rowGap = 6.dp
    val hasRightAxis = target.entries.any { it.yAxisIndex == 1 }
    val cornerShape = androidx.compose.foundation.shape.RoundedCornerShape(tokens.tooltipCornerRadius)

    // Measure the tooltip + host once they're laid out, so we can flip it to the opposite side of
    // the cursor when it would overflow the chart canvas (the fix for "tooltip runs off-screen").
    var hostSize by remember { mutableStateOf(IntSize.Zero) }
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier.onGloballyPositioned { hostSize = it.size }) {
        Column(
            modifier = Modifier
                // width(IntrinsicSize.Min) → the Column shrinks to its widest child, so the inner
                // Rows have a BOUNDED width. Without this the Row's weight(1f) is unbounded and long
                // labels push the tooltip (and the value column) off the right edge of the screen.
                // widthIn(max) caps pathological labels so the tooltip never exceeds a sane size and
                // the value column stays visible (label ellipsizes instead).
                .width(IntrinsicSize.Min)
                .widthIn(max = 220.dp)
                .onGloballyPositioned { tooltipSize = it.size }
                .offset {
                    val aw = tooltipSize.width.toFloat()
                    val ah = tooltipSize.height.toFloat()
                    val hw = hostSize.width.toFloat().coerceAtLeast(1f)
                    val hh = hostSize.height.toFloat().coerceAtLeast(1f)
                    // Default: place to the right of + above the cursor. Flip when that overflows.
                    val placeRight = anchor.x + gapPx + aw <= hw - edgePaddingPx
                    val x = if (placeRight) {
                        anchor.x + gapPx
                    } else {
                        // No room on the right → place on the left of the cursor.
                        (anchor.x - gapPx - aw).coerceAtLeast(edgePaddingPx)
                    }
                    // Vertically: prefer above the cursor; flip below if it would clip the top.
                    val placeAbove = anchor.y - gapPx - ah >= edgePaddingPx
                    val y = if (placeAbove) {
                        anchor.y - gapPx - ah
                    } else {
                        (anchor.y + gapPx).coerceAtMost((hh - ah - edgePaddingPx).coerceAtLeast(edgePaddingPx))
                    }
                    IntOffset(x.roundToInt(), y.roundToInt())
                }
                .shadow(elevation = tokens.tooltipElevation, shape = cornerShape)
                .background(color = tokens.tooltipBackgroundColor, shape = cornerShape)
                .border(width = 1.dp, color = tokens.tooltipBorderColor, shape = cornerShape)
                .padding(tokens.tooltipPadding),
        ) {
            // Header: the hovered category (e.g. "Q2", "Mon"). Omitted for pie/scatter (null).
            if (target.category != null) {
                Text(
                    text = target.category,
                    color = tokens.tooltipTextColor,
                    style = tokens.tooltipTextStyle.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.size(rowGap))
            }
            if (hasRightAxis) {
                val left = target.entries.filter { it.yAxisIndex == 0 }
                val right = target.entries.filter { it.yAxisIndex == 1 }
                if (left.isNotEmpty()) {
                    AxisGroupHeader(label = LeftAxisLabel, style = tokens.tooltipTextStyle, color = tokens.tooltipTextColor)
                    left.forEach { EntryRow(it, dotSize, rowGap, tokens) }
                }
                if (right.isNotEmpty()) {
                    if (left.isNotEmpty()) Spacer(modifier = Modifier.size(rowGap))
                    AxisGroupHeader(label = RightAxisLabel, style = tokens.tooltipTextStyle, color = tokens.tooltipTextColor)
                    right.forEach { EntryRow(it, dotSize, rowGap, tokens) }
                }
            } else {
                target.entries.forEach { EntryRow(it, dotSize, rowGap, tokens) }
            }
        }
    }
}

/** The per-series row: a color dot + label (left, fill) + value (right). Stays inside the tooltip's
 *  bounded width (the parent Column uses width(IntrinsicSize.Min)), so the value is always visible. */
@Composable
private fun EntryRow(
    entry: TooltipEntry,
    dotSize: androidx.compose.ui.unit.Dp,
    rowGap: androidx.compose.ui.unit.Dp,
    tokens: xyz.junerver.compose.palette.core.tokens.PaletteChartTokens,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(rowGap),
    ) {
        Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(entry.color))
        // Label fills the remaining width; long labels ellipsize instead of pushing the value out.
        Text(
            text = entry.label,
            color = tokens.tooltipTextColor,
            style = tokens.tooltipTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        // Value is right-aligned and never truncated (the whole point of the tooltip).
        Text(
            text = formatTooltipValue(entry.value),
            color = tokens.tooltipTextColor,
            style = tokens.tooltipTextStyle.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
        )
    }
}

/** A small dimmed sub-header labeling an axis group in the dual-axis tooltip. */
@Composable
private fun AxisGroupHeader(
    label: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
) {
    Text(
        text = label,
        color = color.copy(alpha = 0.6f),
        style = style,
    )
}

/** i18n-neutral axis labels. The chart has no string-table; these are short fixed labels. */
private const val LeftAxisLabel = "Y1"
private const val RightAxisLabel = "Y2"

/**
 * Formats the hovered value for the tooltip, mirroring [formatTickValue]'s platform-agnostic
 * trimming (integers without decimals, otherwise one fractional digit). Pure + testable.
 */
internal fun formatTooltipValue(value: Float): String {
    if (!value.isFinite()) return "0"
    val rounded = kotlin.math.round(value * 10.0) / 10.0
    val intPart = rounded.toLong()
    val decimal = ((rounded - intPart) * 10.0).let { kotlin.math.round(it).toInt() % 10 }
    return if (decimal == 0) intPart.toString() else "$intPart.$decimal"
}

/**
 * The color used to stroke a hovered data element for highlight feedback. Brighter than the fill so
 * the highlight reads against the series color. Kept here so all renderers resolve it identically.
 */
internal fun highlightStrokeColor(seriesColor: Color): Color =
    if (seriesColor.alpha < 0.5f) seriesColor.copy(alpha = 1f) else seriesColor
