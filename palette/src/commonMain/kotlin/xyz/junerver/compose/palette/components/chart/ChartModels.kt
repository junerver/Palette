package xyz.junerver.compose.palette.components.chart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * A single data series for a chart (e.g. one line, one bar group, one pie slice set).
 *
 * [color] defaults to [Color.Unspecified]; when left unspecified the renderer falls back to the
 * theme categorical palette (cycling by series index).
 *
 * [yAxisIndex] binds the series to the left (0) or right (1) Y axis for dual-axis charts (bar/line).
 * Index 0 is the default and the only axis when no series uses index 1. Values are clamped to 0/1.
 */
@Immutable
data class ChartSeries(
    val label: String,
    val values: List<Float>,
    val color: Color = Color.Unspecified,
    val yAxisIndex: Int = 0,
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

    /**
     * Scatter chart. Plots each (x, y) pair in a [ChartSeries.values] as an independent point — the
     * values list is read in pairs `(v0,v1), (v2,v3), …` where even indices are X and odd are Y. This
     * keeps the existing [ChartSeries] shape (a flat `List<Float>`) so scatter reuses the same data
     * model, legend, and color resolution as the other chart types. An odd-length list drops the
     * trailing unpaired value.
     *
     * [pointSize] scales the marker radius (in dp); [showAxes] is honored via [ChartOptions].
     */
    @Immutable
    data class Scatter(
        val pointSize: Float = 4f,
    ) : ChartSpec

    /**
     * Radar / spider chart. Each [ChartSeries] renders as one polygon whose vertices sit on the
     * axes defined by [ChartData.categories] (one axis per category, radiating from the center).
     * [showGrid] draws concentric reference rings + axis spokes; [fillAlpha] controls the polygon
     * fill translucency (set to 0 for outline-only).
     */
    @Immutable
    data class Radar(
        val showGrid: Boolean = true,
        val fillAlpha: Float = 0.22f,
    ) : ChartSpec
}

/** Where the legend is placed relative to the chart canvas. */
enum class ChartLegendPosition { Top, Bottom, Start, End }

/**
 * The axis a [MarkLine] is anchored to. [Value] lines span the full plot perpendicular to the
 * value axis (a horizontal line at a Y value, like an average); [Category] lines span it
 * perpendicular to the category axis (a vertical line at a category index, like marking a period).
 */
enum class MarkLineAxis { Value, Category }

/**
 * A reference annotation line drawn over the plot — e.g. an average, a target, or a threshold.
 *
 * - [axis] = [MarkLineAxis.Value]: a horizontal (vertical bar) / vertical (horizontal bar) line at
 *   Y = [position], labeled with [label].
 * - [axis] = [MarkLineAxis.Category]: a line at the category slot whose index = [position]. For a
 *   non-integer [position] the line lands between slots.
 *
 * [color] defaults to [Color.Unspecified] → the renderer uses the theme's series-label color.
 * [dashed] renders a dashed stroke (the convention for reference/target lines).
 */
@Immutable
data class MarkLine(
    val axis: MarkLineAxis,
    val position: Float,
    val label: String? = null,
    val color: Color = Color.Unspecified,
    val dashed: Boolean = true,
)

/**
 * Configuration for the optional data-zoom slider rendered below a cartesian chart. The user drags
 * the slider's two handles to choose a sub-range of categories; the chart redraws only that slice
 * and its axes/ticks recompute against the filtered data.
 *
 * - [start]/[end] are the initial visible category indices (inclusive), fractions in `[0, 1]`.
 *   The chart clamps + rounds them to real category indices.
 * - [minSpan] limits how narrow the window can get (e.g. 0.1 = at least 10% of categories visible).
 *
 * The slider is theme-colored (track = grid, selection = primary accent, handles = series-label).
 */
@Immutable
data class DataZoom(
    val start: Float = 0f,
    val end: Float = 1f,
    val minSpan: Float = 0.05f,
)

/**
 * Render options independent of data and chart type. All flags default to sensible values so a bare
 * `PChart(spec, data)` is valid.
 *
 * Axis annotation: [showTickLabels] draws Y value ticks + X category labels (defaults to true — this
 * is the "previously missing" capability, now on by default). [xAxisTitle]/[yAxisTitle] add axis
 * titles; [valueUnit] appends a unit suffix (e.g. "%", "k") to every Y tick label.
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
    /** X-axis title drawn below the category labels, e.g. "Quarter" / "Weekday". */
    val xAxisTitle: String? = null,
    /** Y-axis title drawn rotated on the left of the value axis, e.g. "Revenue (¥10k)". */
    val yAxisTitle: String? = null,
    /** Unit suffix appended to each Y tick label, e.g. "%" or "k". Empty by default. */
    val valueUnit: String = "",
    /** Draws Y value ticks + X category labels. On by default (the previously missing capability). */
    val showTickLabels: Boolean = true,
    /** Target number of Y-axis tick intervals (the actual count snaps to a 1/2/5 grid). */
    val tickCount: Int = 4,
    /** Whether the hover/press tooltip overlay is enabled. On by default. */
    val showTooltip: Boolean = true,
    /**
     * Reference annotation lines drawn over the plot (averages, targets, thresholds). Empty by
     * default. See [MarkLine]. Only cartesian charts (bar / line / scatter) render these.
     */
    val markLines: List<MarkLine> = emptyList(),
    /**
     * When non-null, renders a data-zoom slider below the chart. Drag its handles to filter the
     * visible category range (e.g. inspect a slice of a long time series). Only cartesian charts
     * (bar / line) honor this; pie/scatter/radar ignore it. See [DataZoom].
     */
    val dataZoom: DataZoom? = null,
)
