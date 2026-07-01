# Charts

`PChart` renders **pie / bar / line / scatter / radar** charts with Compose-native Canvas — no WebView, no third-party charting library. Colors, dimensions and the series palette all derive from `PaletteTheme`, and every chart supports a themeable hover tooltip, click-to-toggle legend, and entrance animation out of the box.

## Architecture

```
PChart(spec, data, modifier, options, colors, controlledZoomRange?, onZoomChange?)
├─ ChartSpec       sealed: Pie | Bar | Line | Scatter | Radar
├─ ChartData       series: List<ChartSeries>, categories: List<String>
├─ ChartOptions    title / axes / grid / legend / tooltip / animation / yRange
│                  / xAxisTitle / yAxisTitle / valueUnit / tickCount
│                  / markLines / dataZoom
├─ ChartSeries     label / values / color / yAxisIndex  (yAxisIndex → dual axis)
├─ ChartColors     token-backed (axis/grid/legend/categoricalColors + tooltip)
└─ ChartRenderer   dispatch → Pie / Bar / Line / Scatter / Radar renderers (Compose Canvas)
```

Adding a new chart type = a new `ChartSpec` subclass + renderer branch — existing charts are untouched.

## Pie / Donut

```kotlin
import xyz.junerver.compose.palette.components.chart.*

PChart(
    spec = ChartSpec.Pie(donut = true, showLabels = true),
    data = ChartData(
        series = listOf(ChartSeries("Share", listOf(30f, 45f, 25f))),
        categories = listOf("Mobile", "Desktop", "Other"),
    ),
    options = ChartOptions(title = "Traffic Source"),
)
```

Hover a slice to pop it out and surface its value in the tooltip.

## Bar

```kotlin
PChart(
    spec = ChartSpec.Bar(horizontal = false, stacked = false),
    data = ChartData(
        series = listOf(
            ChartSeries("A", listOf(3f, 5f, 2f)),
            ChartSeries("B", listOf(4f, 2f, 6f)),
        ),
        categories = listOf("Q1", "Q2", "Q3"),
    ),
)
```

- `Bar(horizontal = true)` — horizontal bars (axes swap, category labels move to the left).
- `Bar(stacked = true)` — stack series into one column per category.

## Line

```kotlin
PChart(
    spec = ChartSpec.Line(smooth = true, showPoints = true, areaFill = false),
    data = ChartData(
        series = listOf(ChartSeries("Trend", listOf(1f, 3f, 2f, 5f))),
        categories = listOf("1", "2", "3", "4"),
    ),
)
```

`smooth` renders a Catmull-Rom→Bézier curve; `areaFill` fills below the line.

## Scatter

```kotlin
PChart(
    spec = ChartSpec.Scatter(pointSize = 4f),
    data = ChartData(
        series = listOf(
            ChartSeries("Group A", listOf(1f, 2f, 3f, 5f, 5f, 1f)), // (x0,y0),(x1,y1),…
            ChartSeries("Group B", listOf(2f, 4f, 4f, 3f)),
        ),
    ),
)
```

The `values` list is read as `(x, y)` pairs (an odd length drops the trailing unpaired value). **Both axes are numeric** — unlike bar/line, the X axis shows value ticks, not category labels. Hover snaps to the nearest marker.

## Radar / Spider

```kotlin
PChart(
    spec = ChartSpec.Radar(showGrid = true, fillAlpha = 0.22f),
    data = ChartData(
        series = listOf(
            ChartSeries("Product A", listOf(80f, 60f, 90f, 70f)),
            ChartSeries("Product B", listOf(50f, 85f, 65f, 95f)),
        ),
        categories = listOf("Speed", "Power", "Range", "Quality"), // one axis per category
    ),
)
```

Each series is a polygon; categories define the axes radiating from the center. `showGrid` draws concentric reference rings + spokes; `fillAlpha` controls the polygon fill (0 = outline only).

## Tooltip & highlight

Hover (desktop/web mouse) or press-drag (touch) the canvas to surface a floating value readout. The hit point is highlighted — bars get an outline, line/scatter markers enlarge + ring, pie slices pop out. Toggle the whole feature with `ChartOptions(showTooltip = false)`. All tooltip visuals (background, border, elevation, text) derive from the chart theme tokens.

## Legend interaction

Click a legend dot to toggle that series' visibility. Hidden series drop out of the plot AND the axis range recomputes against the remaining data; the legend entry dims to reflect the hidden state.

## Entrance animation

`ChartOptions(animationEnabled = true)` (the default) plays a spring entrance on first composition and whenever the dataset identity changes: bars grow from the baseline, lines lift from the axis, pie slices sweep in from the start angle, scatter markers expand, radar polygons scale out from the center. Set `animationEnabled = false` to pin everything at full geometry (zero extra per-frame work).

## Axes, titles & units

```kotlin
ChartOptions(
    xAxisTitle = "Quarter",
    yAxisTitle = "Revenue (¥10k)",
    valueUnit = "k",      // suffix appended to every Y tick label, e.g. "120k"
    tickCount = 4,        // target Y intervals (snaps to a 1/2/5 grid)
    showTickLabels = true,
)
```

For horizontal bars the titles follow screen position: `xAxisTitle` labels the bottom (value) axis, `yAxisTitle` the left (category) axis.

## Reference lines (markLine)

Overlay average / target / threshold lines:

```kotlin
ChartOptions(
    markLines = listOf(
        MarkLine(MarkLineAxis.Value, position = 53.6f, label = "Average", dashed = true),
        MarkLine(MarkLineAxis.Category, position = 1f), // vertical line at category index 1
    ),
)
```

`MarkLineAxis.Value` lines span perpendicular to the value axis (horizontal in a vertical chart); `MarkLineAxis.Category` lines mark a category slot. Set `dashed = false` for a solid target line, or override `color` per line.

## Dual Y-axis

Bind a series to the right axis with `yAxisIndex`:

```kotlin
ChartData(
    series = listOf(
        ChartSeries("Visitors", listOf(1f, 5f, 8f), yAxisIndex = 0), // left axis 0..10
        ChartSeries("Revenue",  listOf(100f, 500f, 900f), yAxisIndex = 1), // right axis 0..1000
    ),
)
```

The renderer draws a second value axis (ticks + labels) on the right; each series maps against its own range. Index is clamped to `0`/`1`.

## Data zoom & linked charts

Add a zoom slider below a cartesian chart to inspect a slice of the data:

```kotlin
PChart(
    spec = ChartSpec.Line(),
    data = longSeries,
    options = ChartOptions(dataZoom = DataZoom(start = 0.25f, end = 0.75f, minSpan = 0.05f)),
)
```

Drag the slider's handles (or its body) to filter the visible category range — the plot, ticks and grid all recompute against the window. Only `Bar` and `Line` honor `dataZoom`.

**Link two charts** by lifting the zoom range into parent state and feeding it back via `controlledZoomRange` + `onZoomChange`:

```kotlin
var zoom by remember { mutableStateOf(0f to 1f) }

PChart(spec = ChartSpec.Line(), data = seriesA, options = ChartOptions(dataZoom = DataZoom()),
       controlledZoomRange = zoom, onZoomChange = { zoom = it })
PChart(spec = ChartSpec.Bar(),  data = seriesB, options = ChartOptions(dataZoom = DataZoom()),
       controlledZoomRange = zoom, onZoomChange = { zoom = it })
```

Dragging either chart's slider now drives both — ideal for comparing the same time window across two metrics. When `controlledZoomRange` is `null` (the default) the chart manages its own zoom state internally.

## Theming

The series palette comes from `PaletteTheme.componentThemes.chart.categoricalColors` (derived from the semantic accents). Override a single series color via `ChartSeries.color`. The tooltip, legend-hidden, and highlight styles are also token-backed (`tooltipBackgroundColor`, `legendHiddenAlpha`, `highlightStrokeWidth`, …) so the whole family restyles from `PaletteTheme`. See [Theming](../theming.md).
