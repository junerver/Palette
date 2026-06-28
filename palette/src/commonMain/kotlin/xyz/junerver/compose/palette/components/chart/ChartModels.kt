package xyz.junerver.compose.palette.components.chart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * A single data series for a chart (e.g. one line, one bar group, one pie slice set).
 *
 * [color] defaults to [Color.Unspecified]; when left unspecified the renderer falls back to the
 * theme categorical palette (cycling by series index).
 */
@Immutable
data class ChartSeries(
    val label: String,
    val values: List<Float>,
    val color: Color = Color.Unspecified,
)

/**
 * Chart-agnostic dataset: a list of [ChartSeries] plus optional category labels shared across
 * series (x-axis ticks / slice names). When [categories] is empty, renderers derive sequential
 * numeric labels (1, 2, 3, …) sized to the longest series.
 */
@Immutable
data class ChartData(
    val series: List<ChartSeries>,
    val categories: List<String> = emptyList(),
)

/**
 * Describes which chart to render and its type-specific configuration. Sealed so adding a new chart
 * type is a localized change: declare a new subclass + a matching renderer branch (see
 * [ChartRenderer]). Existing renderers are untouched.
 */
@Immutable
sealed interface ChartSpec {
    /** Pie / donut chart. Each [ChartSeries] renders as one slice set; the first series is used. */
    @Immutable
    data class Pie(
        val donut: Boolean = false,
        val showLabels: Boolean = true,
        val startAngleDeg: Float = 0f,
    ) : ChartSpec

    /** Bar / column chart. Supports grouping (side-by-side), stacking, and horizontal orientation. */
    @Immutable
    data class Bar(
        val horizontal: Boolean = false,
        val stacked: Boolean = false,
    ) : ChartSpec

    /** Line / area chart. [smooth] renders a Catmull-Rom→Bézier curve through the points. */
    @Immutable
    data class Line(
        val smooth: Boolean = false,
        val showPoints: Boolean = true,
        val areaFill: Boolean = false,
    ) : ChartSpec
}

/** Where the legend is placed relative to the chart canvas. */
enum class ChartLegendPosition { Top, Bottom, Start, End }

/**
 * Render options independent of data and chart type. All flags default to sensible values so a bare
 * `PChart(spec, data)` is valid.
 */
@Immutable
data class ChartOptions(
    val title: String? = null,
    val showAxes: Boolean = true,
    val showGrid: Boolean = true,
    val showLegend: Boolean = true,
    val legendPosition: ChartLegendPosition = ChartLegendPosition.Bottom,
    val animationEnabled: Boolean = true,
    val yRange: Pair<Float, Float>? = null,
)
