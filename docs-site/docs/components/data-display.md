# Data Display Components

## Chart

`PChart` renders pie / bar / line charts with Compose-native Canvas (no WebView, no third-party libs). See [Charts](../features/charts.md) for the full guide.

```kotlin
import xyz.junerver.compose.palette.components.chart.PChart
import xyz.junerver.compose.palette.components.chart.ChartData
import xyz.junerver.compose.palette.components.chart.ChartSeries
import xyz.junerver.compose.palette.components.chart.ChartSpec
import xyz.junerver.compose.palette.components.chart.ChartOptions

PChart(
    spec = ChartSpec.Bar(),
    data = ChartData(
        series = listOf(ChartSeries("Sales", listOf(3f, 5f, 2f, 7f))),
        categories = listOf("Q1", "Q2", "Q3", "Q4"),
    ),
    options = ChartOptions(title = "Quarterly Sales"),
)
```

## Table & DataGrid

```kotlin
import xyz.junerver.compose.palette.components.datagrid.PDataGrid
import xyz.junerver.compose.palette.components.datagrid.DataGridColumn

PDataGrid(
    rows = users,
    columns = listOf(
        DataGridColumn(title = "Name") { it.name },
        DataGridColumn(title = "Email") { it.email },
    ),
)
```

`PTable` offers a richer API (sorting, selection, custom cell renderers).

## Tree & TreeSelect

```kotlin
import xyz.junerver.compose.palette.components.tree.PTree
import xyz.junerver.compose.palette.components.tree.TreeNode

PTree(
    nodes = listOf(TreeNode("1", "Root", children = listOf(
        TreeNode("1-1", "Child"),
    ))),
    expandedKeys = setOf("1"),
    onExpandChange = {},
    nodeContent = { Text(it.data) },
)
```

## Descriptions & Statistic

`PDescriptions` renders a definition list; `PStatistic` shows a labeled number.

## List, Collapse, Timeline, Transfer

- **List** — generic list with slots
- **Collapse** — accordion panels
- **Timeline** — vertical activity timeline
- **Transfer** — dual-panel list shuttle

## Feature modules

Three feature modules power richer data display:

- **[Code highlighting](../features/code.md)** — `PCodeBlock`, 16 languages
- **[Markdown](../features/markdown.md)** — viewer + editor, TOC, frontmatter
- **[Mermaid](../features/mermaid.md)** — 19 diagram types

See each feature page and component source for full APIs.
