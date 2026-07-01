package xyz.junerver.compose.palette.components.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize

/**
 * Dispatches a [ChartSpec] to its matching renderer. Adding a new chart type is a localized change:
 * declare a new [ChartSpec] subclass + add a `when` branch here + ship a renderer. Existing
 * renderers are never edited — mirroring the mermaid parser-registration pattern.
 *
 * [hoverState]/[canvasSize]/[density] are threaded through so renderers can resolve the hovered data
 * point inside their [DrawScope] (where the exact plot rectangle is known) and paint a highlight.
 */
internal object ChartRenderer {
    @Composable
    fun render(
        spec: ChartSpec,
        data: ChartData,
        modifier: Modifier,
        options: ChartOptions,
        colors: ChartColors,
        hoverState: ChartHoverState,
        canvasSize: IntSize,
        density: Density,
        entrance: ChartEntranceAnimation,
    ) {
        when (spec) {
            is ChartSpec.Pie -> PieChartRenderer(spec, data, modifier, options, colors, hoverState, canvasSize, entrance)
            is ChartSpec.Bar -> BarChartRenderer(spec, data, modifier, options, colors, hoverState, canvasSize, density, entrance)
            is ChartSpec.Line -> LineChartRenderer(spec, data, modifier, options, colors, hoverState, canvasSize, density, entrance)
            is ChartSpec.Scatter -> ScatterChartRenderer(spec, data, modifier, options, colors, hoverState, canvasSize, density, entrance)
            is ChartSpec.Radar -> RadarChartRenderer(spec, data, modifier, options, colors, hoverState, canvasSize, density, entrance)
        }
    }
}
