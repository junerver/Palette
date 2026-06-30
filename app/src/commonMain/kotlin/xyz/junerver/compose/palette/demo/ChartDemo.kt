package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.chart.ChartData
import xyz.junerver.compose.palette.components.chart.ChartLegendPosition
import xyz.junerver.compose.palette.components.chart.ChartOptions
import xyz.junerver.compose.palette.components.chart.ChartSeries
import xyz.junerver.compose.palette.components.chart.ChartSpec
import xyz.junerver.compose.palette.components.chart.PChart
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ChartDemo() {
    val text = chartDemoText()
    val data = rememberChartData()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---- Pie ----
        DemoSection(title = text.pieSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PChart(
                    spec = ChartSpec.Pie(),
                    data = data.pie,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    options = ChartOptions(title = text.pieBasicTitle),
                )
                PChart(
                    spec = ChartSpec.Pie(donut = true),
                    data = data.pie,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    options = ChartOptions(title = text.pieDonutTitle),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Bar ----
        DemoSection(title = text.barSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = data.bar,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    options =
                        ChartOptions(
                            title = text.barGroupedTitle,
                            legendPosition = ChartLegendPosition.Top,
                            xAxisTitle = text.barXAxisTitle,
                            yAxisTitle = text.barYAxisTitle,
                        ),
                )
                PChart(
                    spec = ChartSpec.Bar(stacked = true),
                    data = data.bar,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    options =
                        ChartOptions(
                            title = text.barStackedTitle,
                            legendPosition = ChartLegendPosition.Top,
                            xAxisTitle = text.barXAxisTitle,
                            yAxisTitle = text.barYAxisTitle,
                        ),
                )
                PChart(
                    spec = ChartSpec.Bar(horizontal = true),
                    data = data.singleBar,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    options =
                        ChartOptions(
                            title = text.barHorizontalTitle,
                            // Single-series chart → hide the redundant legend.
                            showLegend = false,
                            xAxisTitle = text.barHorizontalXAxisTitle,
                            yAxisTitle = text.barHorizontalYAxisTitle,
                            valueUnit = text.barHorizontalUnit,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Line ----
        DemoSection(title = text.lineSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PChart(
                    spec = ChartSpec.Line(),
                    data = data.line,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    options =
                        ChartOptions(
                            title = text.lineBasicTitle,
                            legendPosition = ChartLegendPosition.Top,
                            xAxisTitle = text.lineXAxisTitle,
                            yAxisTitle = text.lineYAxisTitle,
                        ),
                )
                PChart(
                    spec = ChartSpec.Line(smooth = true, areaFill = true),
                    data = data.line,
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    options =
                        ChartOptions(
                            title = text.lineSmoothAreaTitle,
                            legendPosition = ChartLegendPosition.Top,
                            xAxisTitle = text.lineXAxisTitle,
                            yAxisTitle = text.lineYAxisTitle,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Empty state ----
        DemoSection(title = text.emptySectionTitle) {
            PChart(
                spec = ChartSpec.Bar(),
                data = ChartData(series = emptyList()),
                modifier = Modifier.fillMaxWidth().height(200.dp),
                options = ChartOptions(title = text.emptyStateTitle),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(code = text.codeBlock)
    }
}

// Sample datasets, localized by language ------------------------------------

private class ChartDemoData(
    val pie: ChartData,
    val bar: ChartData,
    val singleBar: ChartData,
    val line: ChartData,
)

@Composable
private fun rememberChartData(): ChartDemoData {
    val labels = chartDemoLabels()
    return remember(labels) {
        ChartDemoData(
            // Pie/donut renders ONE series: each value is a slice, categories name the slices.
            pie =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.share, listOf(55f, 30f, 15f)),
                        ),
                    categories = listOf(labels.desktop, labels.mobile, labels.tablet),
                ),
            bar =
                ChartData(
                    series =
                        listOf(
                            ChartSeries("2024", listOf(120f, 200f, 150f, 80f)),
                            ChartSeries("2025", listOf(90f, 240f, 180f, 110f)),
                        ),
                    categories = listOf("Q1", "Q2", "Q3", "Q4"),
                ),
            singleBar =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.revenue, listOf(120f, 240f, 180f, 300f, 220f)),
                        ),
                    categories = listOf("Jan", "Feb", "Mar", "Apr", "May"),
                ),
            line =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.visitors, listOf(30f, 45f, 40f, 60f, 55f, 75f, 70f)),
                            ChartSeries(labels.buyers, listOf(10f, 20f, 18f, 35f, 30f, 50f, 48f)),
                        ),
                    categories = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                ),
        )
    }
}

// I18n ----------------------------------------------------------------------

@Composable
@ReadOnlyComposable
private fun chartDemoText(): ChartDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ChartDemoText(
                title = "PChart 图表",
                subtitle = "Compose 原生 Canvas 绘制的饼图 / 柱状图 / 折线图，支持主题化。",
                pieSectionTitle = "饼图",
                pieBasicTitle = "基础饼图",
                pieDonutTitle = "环形图（donut）",
                barSectionTitle = "柱状图",
                barGroupedTitle = "分组柱状图",
                barStackedTitle = "堆叠柱状图",
                barHorizontalTitle = "横向柱状图",
                barXAxisTitle = "季度",
                barYAxisTitle = "销售额",
                barHorizontalXAxisTitle = "销售额",
                barHorizontalYAxisTitle = "月份",
                barHorizontalUnit = "k",
                lineSectionTitle = "折线图",
                lineBasicTitle = "基础折线图",
                lineSmoothAreaTitle = "平滑曲线 + 面积填充",
                lineXAxisTitle = "星期",
                lineYAxisTitle = "人数",
                emptySectionTitle = "空状态",
                emptyStateTitle = "无数据",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    // 饼图（一个 series，每个 value 是一个切片）
                    PChart(
                        spec = ChartSpec.Pie(donut = true),
                        data = ChartData(
                            series = listOf(
                                ChartSeries("Share", listOf(55f, 30f, 15f)),
                            ),
                            categories = listOf("Desktop", "Mobile", "Tablet"),
                        ),
                    )

                    // 柱状图（带坐标轴标题 + 单位）
                    PChart(
                        spec = ChartSpec.Bar(),
                        data = ChartData(
                            series = listOf(ChartSeries("2024", listOf(120f, 200f))),
                            categories = listOf("Q1", "Q2"),
                        ),
                        options = ChartOptions(
                            xAxisTitle = "季度",
                            yAxisTitle = "销售额",
                            valueUnit = "k",   // Y 轴刻度后缀
                        ),
                    )

                    // 折线图（平滑 + 面积）
                    PChart(
                        spec = ChartSpec.Line(smooth = true, areaFill = true),
                        data = ChartData(
                            series = listOf(ChartSeries("Visitors", listOf(30f, 45f, 60f))),
                        ),
                        options = ChartOptions(xAxisTitle = "日期", yAxisTitle = "人数"),
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            ChartDemoText(
                title = "PChart",
                subtitle = "Pie / bar / line charts drawn with Compose-native Canvas, themeable.",
                pieSectionTitle = "Pie",
                pieBasicTitle = "Basic Pie",
                pieDonutTitle = "Donut",
                barSectionTitle = "Bar",
                barGroupedTitle = "Grouped Bar",
                barStackedTitle = "Stacked Bar",
                barHorizontalTitle = "Horizontal Bar",
                barXAxisTitle = "Quarter",
                barYAxisTitle = "Sales",
                barHorizontalXAxisTitle = "Sales",
                barHorizontalYAxisTitle = "Month",
                barHorizontalUnit = "k",
                lineSectionTitle = "Line",
                lineBasicTitle = "Basic Line",
                lineSmoothAreaTitle = "Smooth + Area Fill",
                lineXAxisTitle = "Weekday",
                lineYAxisTitle = "Count",
                emptySectionTitle = "Empty State",
                emptyStateTitle = "No data",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    // Pie (one series; each value is a slice)
                    PChart(
                        spec = ChartSpec.Pie(donut = true),
                        data = ChartData(
                            series = listOf(
                                ChartSeries("Share", listOf(55f, 30f, 15f)),
                            ),
                            categories = listOf("Desktop", "Mobile", "Tablet"),
                        ),
                    )

                    // Bar (with axis titles + unit)
                    PChart(
                        spec = ChartSpec.Bar(),
                        data = ChartData(
                            series = listOf(ChartSeries("2024", listOf(120f, 200f))),
                            categories = listOf("Q1", "Q2"),
                        ),
                        options = ChartOptions(
                            xAxisTitle = "Quarter",
                            yAxisTitle = "Sales",
                            valueUnit = "k",   // Y-axis tick suffix
                        ),
                    )

                    // Line (smooth + area)
                    PChart(
                        spec = ChartSpec.Line(smooth = true, areaFill = true),
                        data = ChartData(
                            series = listOf(ChartSeries("Visitors", listOf(30f, 45f, 60f))),
                        ),
                        options = ChartOptions(xAxisTitle = "Date", yAxisTitle = "Count"),
                    )
                    """.trimIndent(),
            )
    }

@Composable
@ReadOnlyComposable
private fun chartDemoLabels(): ChartDemoLabels =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ChartDemoLabels(
                desktop = "桌面端",
                mobile = "移动端",
                tablet = "平板",
                share = "占比",
                revenue = "收入",
                visitors = "访客",
                buyers = "买家",
            )

        Language.EN_US ->
            ChartDemoLabels(
                desktop = "Desktop",
                mobile = "Mobile",
                tablet = "Tablet",
                share = "Share",
                revenue = "Revenue",
                visitors = "Visitors",
                buyers = "Buyers",
            )
    }

private data class ChartDemoText(
    val title: String,
    val subtitle: String,
    val pieSectionTitle: String,
    val pieBasicTitle: String,
    val pieDonutTitle: String,
    val barSectionTitle: String,
    val barGroupedTitle: String,
    val barStackedTitle: String,
    val barHorizontalTitle: String,
    val barXAxisTitle: String,
    val barYAxisTitle: String,
    val barHorizontalXAxisTitle: String,
    val barHorizontalYAxisTitle: String,
    val barHorizontalUnit: String,
    val lineSectionTitle: String,
    val lineBasicTitle: String,
    val lineSmoothAreaTitle: String,
    val lineXAxisTitle: String,
    val lineYAxisTitle: String,
    val emptySectionTitle: String,
    val emptyStateTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)

private data class ChartDemoLabels(
    val desktop: String,
    val mobile: String,
    val tablet: String,
    val share: String,
    val revenue: String,
    val visitors: String,
    val buyers: String,
)
