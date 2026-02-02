package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.statistic.PStatistic
import xyz.junerver.compose.palette.components.statistic.TrendType

@Composable
fun StatisticDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PStatistic 统计数值",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "用于展示统计数据的组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "基础用法") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                PStatistic(
                    value = "93,123",
                    title = "总用户数"
                )
                PStatistic(
                    value = "1,234",
                    title = "活跃用户",
                    trend = TrendType.Up
                )
                PStatistic(
                    value = "567",
                    title = "新增用户",
                    trend = TrendType.Down
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = "带前缀后缀") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                PStatistic(
                    value = "9,876",
                    title = "销售额",
                    prefix = "¥",
                    trend = TrendType.Up
                )
                PStatistic(
                    value = "98.5",
                    title = "完成率",
                    suffix = "%"
                )
            }
        }
    }
}
