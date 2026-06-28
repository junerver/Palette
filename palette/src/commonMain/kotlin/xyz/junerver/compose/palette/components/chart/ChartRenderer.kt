package xyz.junerver.compose.palette.components.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Dispatches a [ChartSpec] to its matching renderer. Adding a new chart type is a localized change:
 * declare a new [ChartSpec] subclass + add a `when` branch here + ship a renderer. Existing
 * renderers are never edited — mirroring the mermaid parser-registration pattern.
 */
internal object ChartRenderer {
    @Composable
    fun render(
        spec: ChartSpec,
        data: ChartData,
        modifier: Modifier,
        options: ChartOptions,
        colors: ChartColors,
    ) {
        when (spec) {
            is ChartSpec.Pie -> PieChartRenderer(spec, data, modifier, options, colors)
            is ChartSpec.Bar -> BarChartRenderer(spec, data, modifier, options, colors)
            is ChartSpec.Line -> LineChartRenderer(spec, data, modifier, options, colors)
        }
    }
}
