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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import xyz.junerver.compose.palette.components.chart.DataZoom
import xyz.junerver.compose.palette.components.chart.MarkLine
import xyz.junerver.compose.palette.components.chart.MarkLineAxis
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
                            // Average-visitor reference line drawn over the plot (P5-A demo).
                            markLines =
                                listOf(
                                    MarkLine(
                                        axis = MarkLineAxis.Value,
                                        position = 53.6f,
                                        label = text.lineAverageLabel,
                                    ),
                                ),
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

        // ---- Scatter ----
        DemoSection(title = text.scatterSectionTitle) {
            PChart(
                spec = ChartSpec.Scatter(),
                data = data.scatter,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                options =
                    ChartOptions(
                        title = text.scatterTitle,
                        legendPosition = ChartLegendPosition.Top,
                        xAxisTitle = text.scatterXAxisTitle,
                        yAxisTitle = text.scatterYAxisTitle,
                    ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Radar ----
        DemoSection(title = text.radarSectionTitle) {
            PChart(
                spec = ChartSpec.Radar(),
                data = data.radar,
                modifier = Modifier.fillMaxWidth().height(260.dp),
                options =
                    ChartOptions(
                        title = text.radarTitle,
                        legendPosition = ChartLegendPosition.Top,
                    ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Dual Y-axis (P5-B): two metrics with different scales on one chart ----
        DemoSection(title = text.dualAxisSectionTitle) {
            PChart(
                spec = ChartSpec.Line(),
                data = data.dualAxis,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                options =
                    ChartOptions(
                        title = text.dualAxisTitle,
                        legendPosition = ChartLegendPosition.Top,
                        xAxisTitle = text.dualAxisXTitle,
                        yAxisTitle = text.dualAxisLeftYTitle,
                    ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- DataZoom + Linked charts (P6-A/B): drag one slider to drive both views ----
        DemoSection(title = text.linkedSectionTitle) {
            // Lift the zoom range into parent state so the two charts share it.
            var linkedZoom by remember { mutableStateOf(0f to 1f) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PChart(
                    spec = ChartSpec.Line(),
                    data = data.zoomLine,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    options =
                        ChartOptions(
                            title = text.linkedTopTitle,
                            legendPosition = ChartLegendPosition.Top,
                            dataZoom = DataZoom(),
                        ),
                    controlledZoomRange = linkedZoom,
                    onZoomChange = { linkedZoom = it },
                )
                PChart(
                    spec = ChartSpec.Bar(),
                    data = data.zoomBar,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    options =
                        ChartOptions(
                            title = text.linkedBottomTitle,
                            legendPosition = ChartLegendPosition.Top,
                            dataZoom = DataZoom(),
                        ),
                    controlledZoomRange = linkedZoom,
                    onZoomChange = { linkedZoom = it },
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
    val scatter: ChartData,
    val radar: ChartData,
    val dualAxis: ChartData,
    val zoomLine: ChartData,
    val zoomBar: ChartData,
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
            // Scatter: flat values list read as (x,y) pairs. Two series for color grouping.
            scatter =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.groupA, listOf(1f, 2f, 2f, 5f, 3f, 8f, 5f, 3f, 7f, 9f)),
                            ChartSeries(labels.groupB, listOf(2f, 4f, 4f, 7f, 6f, 6f, 8f, 5f, 9f, 8f)),
                        ),
                ),
            // Radar: categories define the axes; each series is a polygon.
            radar =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.productA, listOf(80f, 60f, 90f, 70f, 85f)),
                            ChartSeries(labels.productB, listOf(50f, 85f, 65f, 95f, 60f)),
                        ),
                    categories = listOf(labels.speed, labels.power, labels.range, labels.quality, labels.cost),
                ),
            // Dual-axis: visitors (left, 0..100) vs. revenue (right, 0..1000). Same categories.
            dualAxis =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.visitors, listOf(20f, 35f, 50f, 45f, 70f), yAxisIndex = 0),
                            ChartSeries(labels.revenue, listOf(150f, 300f, 550f, 480f, 900f), yAxisIndex = 1),
                        ),
                    categories = listOf(labels.mon, labels.tue, labels.wed, labels.thu, labels.fri),
                ),
            // Long series for the data-zoom / linked demo (zoomLine on top, zoomBar on bottom —
            // same 12 months so the linked slider slices both in lockstep).
            zoomLine =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.visitors, listOf(30f, 45f, 40f, 60f, 55f, 75f, 70f, 65f, 80f, 50f, 35f, 60f)),
                        ),
                    categories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                ),
            zoomBar =
                ChartData(
                    series =
                        listOf(
                            ChartSeries(labels.revenue, listOf(120f, 180f, 150f, 210f, 190f, 260f, 240f, 220f, 280f, 200f, 160f, 230f)),
                        ),
                    categories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
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
                lineAverageLabel = "平均值",
                scatterSectionTitle = "散点图",
                scatterTitle = "散点分布",
                scatterXAxisTitle = "X",
                scatterYAxisTitle = "Y",
                radarSectionTitle = "雷达图",
                radarTitle = "多维对比",
                dualAxisSectionTitle = "双 Y 轴",
                dualAxisTitle = "访客 vs 收入（双轴）",
                dualAxisXTitle = "工作日",
                dualAxisLeftYTitle = "访客",
                linkedSectionTitle = "缩放与联动",
                linkedTopTitle = "访客趋势（拖动下方滑块）",
                linkedBottomTitle = "收入对比（联动）",
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
                lineAverageLabel = "Average",
                scatterSectionTitle = "Scatter",
                scatterTitle = "Scatter Distribution",
                scatterXAxisTitle = "X",
                scatterYAxisTitle = "Y",
                radarSectionTitle = "Radar",
                radarTitle = "Multi-dimension Compare",
                dualAxisSectionTitle = "Dual Y-axis",
                dualAxisTitle = "Visitors vs. Revenue (dual axis)",
                dualAxisXTitle = "Weekday",
                dualAxisLeftYTitle = "Visitors",
                linkedSectionTitle = "Zoom & Linking",
                linkedTopTitle = "Visitor Trend (drag the slider)",
                linkedBottomTitle = "Revenue (linked)",
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
                groupA = "A 组",
                groupB = "B 组",
                productA = "产品 A",
                productB = "产品 B",
                speed = "速度",
                power = "性能",
                range = "续航",
                quality = "质量",
                cost = "成本",
                mon = "周一",
                tue = "周二",
                wed = "周三",
                thu = "周四",
                fri = "周五",
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
                groupA = "Group A",
                groupB = "Group B",
                productA = "Product A",
                productB = "Product B",
                speed = "Speed",
                power = "Power",
                range = "Range",
                quality = "Quality",
                cost = "Cost",
                mon = "Mon",
                tue = "Tue",
                wed = "Wed",
                thu = "Thu",
                fri = "Fri",
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
    val lineAverageLabel: String,
    val scatterSectionTitle: String,
    val scatterTitle: String,
    val scatterXAxisTitle: String,
    val scatterYAxisTitle: String,
    val radarSectionTitle: String,
    val radarTitle: String,
    val dualAxisSectionTitle: String,
    val dualAxisTitle: String,
    val dualAxisXTitle: String,
    val dualAxisLeftYTitle: String,
    val linkedSectionTitle: String,
    val linkedTopTitle: String,
    val linkedBottomTitle: String,
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
    val groupA: String,
    val groupB: String,
    val productA: String,
    val productB: String,
    val speed: String,
    val power: String,
    val range: String,
    val quality: String,
    val cost: String,
    val mon: String,
    val tue: String,
    val wed: String,
    val thu: String,
    val fri: String,
)
