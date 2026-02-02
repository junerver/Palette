package xyz.junerver.compose.palette.components.statistic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class TrendType {
    Up, Down, None
}

@Composable
fun PStatistic(
    value: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    prefix: String? = null,
    suffix: String? = null,
    trend: TrendType = TrendType.None,
    valueColor: Color = StatisticDefaults.valueColor(),
    titleColor: Color = StatisticDefaults.titleColor()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(StatisticDefaults.Spacing)
    ) {
        title?.let {
            Text(
                text = it,
                color = titleColor,
                style = PaletteTheme.typography.body
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            prefix?.let {
                Text(
                    text = it,
                    color = valueColor,
                    style = PaletteTheme.typography.title
                )
            }

            Text(
                text = value,
                color = valueColor,
                style = PaletteTheme.typography.title
            )

            suffix?.let {
                Text(
                    text = it,
                    color = valueColor,
                    style = PaletteTheme.typography.title
                )
            }

            when (trend) {
                TrendType.Up -> Text(
                    text = "▲",
                    color = StatisticDefaults.trendUpColor(),
                    style = PaletteTheme.typography.body
                )
                TrendType.Down -> Text(
                    text = "▼",
                    color = StatisticDefaults.trendDownColor(),
                    style = PaletteTheme.typography.body
                )
                TrendType.None -> {}
            }
        }
    }
}
