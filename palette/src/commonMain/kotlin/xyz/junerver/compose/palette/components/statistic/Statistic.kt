package xyz.junerver.compose.palette.components.statistic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
        verticalArrangement = Arrangement.spacedBy(StatisticDefaults.spacing())
    ) {
        title?.let {
            Text(
                text = it,
                color = titleColor,
                style = StatisticDefaults.titleTextStyle()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(StatisticDefaults.rowItemSpacing())
        ) {
            prefix?.let {
                Text(
                    text = it,
                    color = valueColor,
                    style = StatisticDefaults.valueTextStyle()
                )
            }

            Text(
                text = value,
                color = valueColor,
                style = StatisticDefaults.valueTextStyle()
            )

            suffix?.let {
                Text(
                    text = it,
                    color = valueColor,
                    style = StatisticDefaults.valueTextStyle()
                )
            }

            when (trend) {
                TrendType.Up -> Text(
                    text = "▲",
                    color = StatisticDefaults.trendUpColor(),
                    style = StatisticDefaults.trendTextStyle()
                )
                TrendType.Down -> Text(
                    text = "▼",
                    color = StatisticDefaults.trendDownColor(),
                    style = StatisticDefaults.trendTextStyle()
                )
                TrendType.None -> {}
            }
        }
    }
}
