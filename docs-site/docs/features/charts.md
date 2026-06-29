# Charts

`PChart` renders pie / bar / line charts with Compose-native Canvas — no WebView, no third-party charting library. Colors, dimensions and the series palette all derive from `PaletteTheme`.

## Architecture

```
PChart(spec, data, modifier, options, colors)
├─ ChartSpec       sealed: Pie | Bar | Line
├─ ChartData       series: List<ChartSeries>, categories: List<String>
├─ ChartOptions    title / axes / grid / legend / animation / yRange
├─ ChartColors     token-backed (axis/grid/legend/categoricalColors)
└─ ChartRenderer   dispatch → Pie / Bar / Line renderers (Compose Canvas)
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

`Bar(horizontal = true)` for horizontal bars; `stacked = true` to stack series.

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

## Theming

The series palette comes from `PaletteTheme.componentThemes.chart.categoricalColors` (derived from the semantic accents). Override a single series color via `ChartSeries.color`. See [Theming](../theming.md).
