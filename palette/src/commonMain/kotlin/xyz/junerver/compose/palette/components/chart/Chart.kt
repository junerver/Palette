package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * A themeable, dependency-free chart. Renders the chart described by [spec] from [data] using
 * Compose-native Canvas drawing (no WebView, no third-party libs) — consistent with the mermaid
 * module's self-rendering philosophy.
 *
 * To add a new chart type: declare a new [ChartSpec] subclass + add a `when` branch in
 * [ChartRenderer] + ship a renderer. Existing charts are untouched.
 *
 * @param spec which chart + its config (pie / bar / line).
 * @param data series + optional categories.
 * @param modifier outer modifier.
 * @param options title, axes, grid, legend, animation.
 * @param colors token-backed color bundle; defaults to [ChartDefaults.colors].
 */
@Composable
fun PChart(
    spec: ChartSpec,
    data: ChartData,
    modifier: Modifier = Modifier,
    options: ChartOptions = ChartOptions(),
    colors: ChartColors = ChartDefaults.colors(),
) {
    val titleStyle = ChartDefaults.titleTextStyle()
    val emptyColor = colors.emptyStateColor
    val hasData = data.series.isNotEmpty() && data.series.any { it.values.isNotEmpty() }

    Column(modifier = modifier) {
        if (options.title != null) {
            Text(
                text = options.title,
                color = colors.legendTextColor,
                style = titleStyle.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        if (options.showLegend && options.legendPosition == ChartLegendPosition.Top && hasData) {
            ChartLegend(data, colors, Modifier.padding(bottom = 4.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            contentAlignment = Alignment.Center,
        ) {
            if (!hasData) {
                Text(text = "No data", color = emptyColor, style = PaletteTheme.typography.body)
            } else {
                ChartRenderer.render(
                    spec = spec,
                    data = data,
                    modifier = Modifier.fillMaxSize(),
                    options = options,
                    colors = colors,
                )
            }
        }

        if (options.showLegend && options.legendPosition == ChartLegendPosition.Bottom && hasData) {
            ChartLegend(data, colors, Modifier.padding(top = 4.dp))
        }
    }
}

/** Renders the categorical legend: a colored dot + series label per series. */
@Composable
private fun ChartLegend(
    data: ChartData,
    colors: ChartColors,
    modifier: Modifier = Modifier,
) {
    val symbolSize = ChartDefaults.legendSymbolSize()
    val legendStyle = ChartDefaults.legendTextStyle()
    val accentFallback = PaletteTheme.colors.textPrimary

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        data.series.forEachIndexed { index, series ->
            if (index > 0) Box(modifier = Modifier.size(12.dp))
            val color = resolveSeriesColor(series, index, colors.categoricalColors, accentFallback)
            Box(
                modifier = Modifier
                    .size(symbolSize)
                    .clip(CircleShape)
                    .background(color),
            )
            Text(
                text = series.label,
                color = colors.legendTextColor,
                style = legendStyle,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp),
            )
        }
    }
}
