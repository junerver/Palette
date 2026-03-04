package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.statistic.PStatistic
import xyz.junerver.compose.palette.components.statistic.TrendType

@Composable
fun StatisticDemo() {
    val text = statisticDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.basicSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                PStatistic(
                    value = "93,123",
                    title = text.totalUsersTitle
                )
                PStatistic(
                    value = "1,234",
                    title = text.activeUsersTitle,
                    trend = TrendType.Up
                )
                PStatistic(
                    value = "567",
                    title = text.newUsersTitle,
                    trend = TrendType.Down
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = text.prefixSuffixSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                PStatistic(
                    value = "9,876",
                    title = text.salesTitle,
                    prefix = "¥",
                    trend = TrendType.Up
                )
                PStatistic(
                    value = "98.5",
                    title = text.completionRateTitle,
                    suffix = "%"
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun statisticDemoText(): StatisticDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> StatisticDemoText(
        title = "PStatistic 统计数值",
        subtitle = "用于展示统计数据的组件",
        basicSectionTitle = "基础用法",
        totalUsersTitle = "总用户数",
        activeUsersTitle = "活跃用户",
        newUsersTitle = "新增用户",
        prefixSuffixSectionTitle = "带前缀后缀",
        salesTitle = "销售额",
        completionRateTitle = "完成率",
    )

    Language.EN_US -> StatisticDemoText(
        title = "PStatistic",
        subtitle = "A component for displaying statistics.",
        basicSectionTitle = "Basic Usage",
        totalUsersTitle = "Total Users",
        activeUsersTitle = "Active Users",
        newUsersTitle = "New Users",
        prefixSuffixSectionTitle = "With Prefix and Suffix",
        salesTitle = "Sales",
        completionRateTitle = "Completion Rate",
    )
}

private data class StatisticDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val totalUsersTitle: String,
    val activeUsersTitle: String,
    val newUsersTitle: String,
    val prefixSuffixSectionTitle: String,
    val salesTitle: String,
    val completionRateTitle: String,
)
