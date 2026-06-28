package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Rule
import kotlin.test.Test
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

class ChartUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val sampleData = ChartData(
        series = listOf(
            ChartSeries("Apples", listOf(3f, 2f, 5f)),
            ChartSeries("Oranges", listOf(2f, 4f, 1f)),
        ),
        categories = listOf("Q1", "Q2", "Q3"),
    )

    @Test
    fun barChartRendersWithoutCrashAndShowsTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Fruit Sales"),
                )
            }
        }
        rule.onNodeWithText("Fruit Sales").assertExists()
    }

    @Test
    fun lineChartRendersLegendEntries() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                )
            }
        }
        // Legend renders each series label.
        rule.onNodeWithText("Apples").assertExists()
        rule.onNodeWithText("Oranges").assertExists()
    }

    @Test
    fun pieChartRendersWithoutCrashAndShowsTitle() {
        // Slice labels are drawn on the Canvas (drawText) and therefore aren't semantics-discoverable;
        // this test guards that the pie renderer composes without throwing and surfaces the title.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Pie(showLabels = true),
                    data = ChartData(
                        series = listOf(ChartSeries("Share", listOf(30f, 70f))),
                        categories = listOf("A", "B"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                    options = ChartOptions(title = "Distribution"),
                )
            }
        }
        rule.onNodeWithText("Distribution").assertExists()
    }

    @Test
    fun donutChartRendersWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Pie(donut = true),
                    data = ChartData(
                        series = listOf(ChartSeries("Share", listOf(40f, 60f))),
                        categories = listOf("X", "Y"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                )
            }
        }
        // No assertion target on the Canvas; composing without exception is the contract. Legend label
        // proves the composition tree built.
        rule.onNodeWithText("Share").assertExists()
    }

    @Test
    fun emptyDataShowsPlaceholder() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = ChartData(emptyList()),
                    modifier = Modifier.size(300.dp, 200.dp),
                )
            }
        }
        rule.onNodeWithText("No data").assertExists()
    }

    @Test
    fun hiddenLegendDoesNotRenderLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(showLegend = false),
                )
            }
        }
        // No legend → series labels absent. The data still renders but without legend text.
        // Asserting absence guards against legend rendering when disabled.
        rule.onNodeWithText("Apples").assertDoesNotExist()
    }
}
