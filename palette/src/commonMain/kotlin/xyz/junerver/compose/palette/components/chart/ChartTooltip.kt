package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
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
    data: ChartData,
    colors: ChartColors,
    modifier: Modifier = Modifier,
) {
    val target = state.target
    if (target == null || !state.active) return

    val tokens = PaletteTheme.componentThemes.chart
    val density = LocalDensity.current
    // Leader gap + cursor offset so the tooltip clears the highlighted element.
    val gapPx = with(density) { 12.dp.toPx() }
    val anchor = state.anchor
    val seriesLabel = data.series.getOrNull(target.seriesIndex)?.label
    val valueText = formatTooltipValue(target.value)

    Box(modifier = modifier) {
        // Anchor the tooltip at the cursor, nudged up-right by the leader gap.
        Column(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (anchor.x + gapPx).roundToInt(),
                        y = (anchor.y - gapPx).roundToInt(),
                    )
                }
                .shadow(elevation = tokens.tooltipElevation, shape = androidx.compose.foundation.shape.RoundedCornerShape(tokens.tooltipCornerRadius))
                .background(
                    color = tokens.tooltipBackgroundColor,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(tokens.tooltipCornerRadius),
                )
                .border(
                    width = 1.dp,
                    color = tokens.tooltipBorderColor,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(tokens.tooltipCornerRadius),
                )
                .padding(tokens.tooltipPadding),
        ) {
            if (seriesLabel != null) {
                Text(
                    text = seriesLabel,
                    color = tokens.tooltipTextColor,
                    style = tokens.tooltipTextStyle.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            if (target.category != null) {
                Text(
                    text = target.category,
                    color = tokens.tooltipTextColor.copy(alpha = 0.7f),
                    style = tokens.tooltipTextStyle,
                )
            }
            Text(
                text = valueText,
                color = tokens.tooltipTextColor,
                style = tokens.tooltipTextStyle,
            )
        }
    }
}

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
