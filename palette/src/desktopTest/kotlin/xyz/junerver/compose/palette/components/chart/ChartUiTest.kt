package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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

    @Test
    fun animationDisabled_composesWithoutCrash() {
        // animationEnabled=false must pin the entrance at 1f and compose cleanly (the disabled path
        // is exercised here; the enabled path runs in every other test above).
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(animationEnabled = false, title = "No Animation"),
                )
            }
        }
        rule.onNodeWithText("No Animation").assertExists()
    }

    @Test
    fun tooltipDisabled_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(showTooltip = false, title = "No Tooltip"),
                )
            }
        }
        rule.onNodeWithText("No Tooltip").assertExists()
    }

    @Test
    fun pieAnimation_sweepIn_composesWithoutCrash() {
        // Pie sweep-in animation must not throw during composition (label gating, budget math).
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Pie(donut = true),
                    data = ChartData(
                        series = listOf(ChartSeries("Share", listOf(40f, 35f, 25f))),
                        categories = listOf("A", "B", "C"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                    options = ChartOptions(title = "Animated Donut"),
                )
            }
        }
        rule.onNodeWithText("Animated Donut").assertExists()
    }

    @Test
    fun scatterChart_rendersLegendEntries() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Scatter(),
                    data = ChartData(
                        series = listOf(
                            ChartSeries("Set A", listOf(1f, 2f, 3f, 5f, 5f, 1f)),
                            ChartSeries("Set B", listOf(2f, 4f, 4f, 3f)),
                        ),
                    ),
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Scatter"),
                )
            }
        }
        rule.onNodeWithText("Scatter").assertExists()
        rule.onNodeWithText("Set A").assertExists()
        rule.onNodeWithText("Set B").assertExists()
    }

    @Test
    fun radarChart_rendersLegendEntries() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Radar(),
                    data = ChartData(
                        series = listOf(
                            ChartSeries("Skill A", listOf(80f, 60f, 90f, 70f)),
                            ChartSeries("Skill B", listOf(50f, 85f, 65f, 95f)),
                        ),
                        categories = listOf("Speed", "Power", "Range", "Skill"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                    options = ChartOptions(title = "Radar"),
                )
            }
        }
        rule.onNodeWithText("Radar").assertExists()
        rule.onNodeWithText("Skill A").assertExists()
        rule.onNodeWithText("Skill B").assertExists()
    }

    @Test
    fun markLines_composeWithoutCrash() {
        // Reference annotation lines (average + category marker) must render over the plot without
        // throwing. The mark-line label is drawn on the Canvas (drawText), so we assert the title.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(
                        title = "With MarkLines",
                        markLines = listOf(
                            MarkLine(MarkLineAxis.Value, position = 3f, label = "Avg"),
                            MarkLine(MarkLineAxis.Category, position = 1f),
                        ),
                    ),
                )
            }
        }
        rule.onNodeWithText("With MarkLines").assertExists()
    }

    @Test
    fun dualAxis_lineChart_composesWithoutCrash() {
        // Two series on different Y axes (0..10 left, 0..1000 right) — both axes + ticks render.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = ChartData(
                        series = listOf(
                            ChartSeries("Left", listOf(1f, 5f, 8f), yAxisIndex = 0),
                            ChartSeries("Right", listOf(100f, 500f, 900f), yAxisIndex = 1),
                        ),
                        categories = listOf("A", "B", "C"),
                    ),
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Dual Axis"),
                )
            }
        }
        rule.onNodeWithText("Dual Axis").assertExists()
        rule.onNodeWithText("Left").assertExists()
        rule.onNodeWithText("Right").assertExists()
    }

    @Test
    fun dataZoom_rendersSliderAndSlicesData() {
        // With dataZoom enabled the slider renders and the initial window slices the categories.
        // Title asserts composition; the slice math is covered by the ChartLogic tests.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = ChartData(
                        series = listOf(ChartSeries("Trend", listOf(1f, 2f, 3f, 4f, 5f, 6f))),
                        categories = listOf("A", "B", "C", "D", "E", "F"),
                    ),
                    modifier = Modifier.size(300.dp, 220.dp),
                    options = ChartOptions(
                        title = "Zoomed",
                        dataZoom = DataZoom(start = 0.25f, end = 0.75f),
                    ),
                )
            }
        }
        rule.onNodeWithText("Zoomed").assertExists()
    }

    @Test
    fun linkedCharts_shareControlledZoomRange() {
        // Two PCharts sharing a lifted zoom range (controlledZoomRange + onZoomChange) must both
        // compose without crashing and apply the shared window. The lift wiring is the contract.
        val sharedRange = androidx.compose.runtime.mutableStateOf(0.25f to 0.75f)
        rule.setContent {
            PaletteMaterialTheme {
                androidx.compose.foundation.layout.Column {
                    PChart(
                        spec = ChartSpec.Line(),
                        data = ChartData(
                            series = listOf(ChartSeries("A", listOf(1f, 2f, 3f, 4f))),
                            categories = listOf("Q1", "Q2", "Q3", "Q4"),
                        ),
                        modifier = Modifier.size(300.dp, 120.dp),
                        options = ChartOptions(title = "Linked A", dataZoom = DataZoom()),
                        controlledZoomRange = sharedRange.value,
                        onZoomChange = { sharedRange.value = it },
                    )
                    PChart(
                        spec = ChartSpec.Bar(),
                        data = ChartData(
                            series = listOf(ChartSeries("B", listOf(5f, 6f, 7f, 8f))),
                            categories = listOf("Q1", "Q2", "Q3", "Q4"),
                        ),
                        modifier = Modifier.size(300.dp, 120.dp),
                        options = ChartOptions(title = "Linked B", dataZoom = DataZoom()),
                        controlledZoomRange = sharedRange.value,
                        onZoomChange = { sharedRange.value = it },
                    )
                }
            }
        }
        rule.onNodeWithText("Linked A").assertExists()
        rule.onNodeWithText("Linked B").assertExists()
    }

    // region Theme & instance-level customization ──────────────────────────────
    @Test
    fun customChartColors_overridesAreApplied() {
        // A caller-supplied ChartColors must flow through without crashing; the title proves the
        // composition built and the legend renders the (custom-colored) series labels.
        val custom = ChartColors(
            axisColor = androidx.compose.ui.graphics.Color.Magenta,
            gridColor = androidx.compose.ui.graphics.Color.LightGray,
            tickLabelColor = androidx.compose.ui.graphics.Color.Blue,
            axisTitleColor = androidx.compose.ui.graphics.Color.Red,
            seriesLabelColor = androidx.compose.ui.graphics.Color.Green,
            legendTextColor = androidx.compose.ui.graphics.Color.Cyan,
            emptyStateColor = androidx.compose.ui.graphics.Color.Gray,
            categoricalColors = listOf(androidx.compose.ui.graphics.Color(0xFF123456)),
        )
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Custom Colors", showLegend = true),
                    colors = custom,
                )
            }
        }
        rule.onNodeWithText("Custom Colors").assertExists()
        rule.onNodeWithText("Apples").assertExists()
    }

    @Test
    fun horizontalBarChart_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(horizontal = true),
                    data = ChartData(
                        series = listOf(ChartSeries("Revenue", listOf(120f, 240f, 180f))),
                        categories = listOf("Jan", "Feb", "Mar"),
                    ),
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Horizontal Bar", valueUnit = "k"),
                )
            }
        }
        rule.onNodeWithText("Horizontal Bar").assertExists()
    }

    @Test
    fun stackedBarChart_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(stacked = true),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Stacked Bar"),
                )
            }
        }
        rule.onNodeWithText("Stacked Bar").assertExists()
    }

    @Test
    fun scatterChart_customPointSize_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Scatter(pointSize = 6f),
                    data = ChartData(
                        series = listOf(ChartSeries("Pts", listOf(1f, 1f, 9f, 9f, 5f, 5f))),
                    ),
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Big Scatter"),
                )
            }
        }
        rule.onNodeWithText("Big Scatter").assertExists()
    }

    @Test
    fun radarChart_outlineOnly_fillAlphaZero() {
        // fillAlpha = 0 → outline-only polygons; must compose without throwing.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Radar(fillAlpha = 0f),
                    data = ChartData(
                        series = listOf(ChartSeries("A", listOf(3f, 5f, 2f, 4f))),
                        categories = listOf("N", "E", "S", "W"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                    options = ChartOptions(title = "Outline Radar"),
                )
            }
        }
        rule.onNodeWithText("Outline Radar").assertExists()
    }

    @Test
    fun radarChart_noGrid_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Radar(showGrid = false),
                    data = ChartData(
                        series = listOf(ChartSeries("A", listOf(3f, 5f, 2f, 4f, 6f))),
                        categories = listOf("A", "B", "C", "D", "E"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                    options = ChartOptions(title = "No Grid Radar"),
                )
            }
        }
        rule.onNodeWithText("No Grid Radar").assertExists()
    }

    @Test
    fun lineChart_areaFillSmooth_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(smooth = true, areaFill = true, showPoints = false),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Area Fill"),
                )
            }
        }
        rule.onNodeWithText("Area Fill").assertExists()
    }

    @Test
    fun pieChart_customStartAngle_composesWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Pie(startAngleDeg = 90f),
                    data = ChartData(
                        series = listOf(ChartSeries("S", listOf(40f, 35f, 25f))),
                        categories = listOf("X", "Y", "Z"),
                    ),
                    modifier = Modifier.size(300.dp, 300.dp),
                    options = ChartOptions(title = "Rotated Pie"),
                )
            }
        }
        rule.onNodeWithText("Rotated Pie").assertExists()
    }

    @Test
    fun axesDisabled_chartStillComposes() {
        // showAxes=false + showGrid=false + showTickLabels=false → bare data marks, no frame.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(
                        title = "No Axes",
                        showAxes = false,
                        showGrid = false,
                        showTickLabels = false,
                    ),
                )
            }
        }
        rule.onNodeWithText("No Axes").assertExists()
    }

    @Test
    fun yRangeOverride_isRespected() {
        // An explicit yRange outside the data must compose (data is clamped into the overridden span).
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = sampleData,
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Y Override", yRange = 0f to 100f),
                )
            }
        }
        rule.onNodeWithText("Y Override").assertExists()
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region Combined / regression ──────────────────────────────────────────────
    @Test
    fun allFeaturesCombined_barChart() {
        // animation + tooltip + legend + axes + titles + unit + markLine + dataZoom together.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Bar(),
                    data = ChartData(
                        series = listOf(
                            ChartSeries("A", listOf(10f, 40f, 25f, 60f, 35f, 50f)),
                            ChartSeries("B", listOf(5f, 20f, 15f, 30f, 25f, 10f)),
                        ),
                        categories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
                    ),
                    modifier = Modifier.size(320.dp, 240.dp),
                    options = ChartOptions(
                        title = "Everything",
                        legendPosition = ChartLegendPosition.Top,
                        xAxisTitle = "Month",
                        yAxisTitle = "Sales",
                        valueUnit = "k",
                        animationEnabled = true,
                        showTooltip = true,
                        markLines = listOf(MarkLine(MarkLineAxis.Value, position = 30f, label = "Avg")),
                        dataZoom = DataZoom(),
                    ),
                )
            }
        }
        rule.onNodeWithText("Everything").assertExists()
        rule.onNodeWithText("A").assertExists()
    }

    @Test
    fun singleValueSeries_doesNotCrash() {
        // A degenerate dataset (one category, one value) must render without divide-by-zero issues.
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = ChartData(
                        series = listOf(ChartSeries("Lonely", listOf(42f))),
                        categories = listOf("Only"),
                    ),
                    modifier = Modifier.size(200.dp, 150.dp),
                    options = ChartOptions(title = "Single Point"),
                )
            }
        }
        rule.onNodeWithText("Single Point").assertExists()
    }

    @Test
    fun negativeValues_lineChart_composesWithoutCrash() {
        // Values spanning negative → positive must compose (axis spans below 0).
        rule.setContent {
            PaletteMaterialTheme {
                PChart(
                    spec = ChartSpec.Line(),
                    data = ChartData(
                        series = listOf(ChartSeries("Delta", listOf(-5f, 0f, 3f, -2f, 4f))),
                        categories = listOf("1", "2", "3", "4", "5"),
                    ),
                    modifier = Modifier.size(300.dp, 200.dp),
                    options = ChartOptions(title = "Negatives"),
                )
            }
        }
        rule.onNodeWithText("Negatives").assertExists()
    }
    // endregion ────────────────────────────────────────────────────────────────

    // region DataZoomSlider rendering & wiring ─────────────────────────────────
    // NOTE: this Compose test runtime does not dispatch synthetic drag events from
    // `performTouchInput { down(); updatePointerTo(); up() }` into `detectDragGestures` (verified
    // with a bare-Box probe). The slider's drag MATH is therefore covered exhaustively by the
    // `computeZoom` logic tests in ChartLogicTest (9 cases). These UI tests guard the slider's
    // composition, theme wiring, and initial-window rendering — the parts the logic tests can't see.
    @Test
    fun dataZoomSlider_rendersInitialWindowWithoutCrash() {
        // The slider composes and lays out its track + selection + handles from the initial range.
        var fired = 0
        rule.setContent {
            PaletteMaterialTheme {
                DataZoomSlider(
                    startFraction = 0.25f,
                    endFraction = 0.75f,
                    minSpan = 0.1f,
                    onChange = { _, _ -> fired++ },
                    modifier = Modifier.size(400.dp, 40.dp),
                )
            }
        }
        rule.waitForIdle()
        // onChange is not called on composition (only on user drag). The contract here is "renders".
        // If the slider threw, setContent would fail; reaching waitForIdle proves it composed.
        assertEquals(0, fired)
    }

    @Test
    fun dataZoomSlider_fullRangeWindow_renders() {
        rule.setContent {
            PaletteMaterialTheme {
                DataZoomSlider(
                    startFraction = 0f,
                    endFraction = 1f,
                    minSpan = 0.05f,
                    onChange = { _, _ -> },
                    modifier = Modifier.size(400.dp, 40.dp),
                )
            }
        }
        rule.waitForIdle()
        // Full-range window: both handles at the extremes. Composition success is the contract.
    }

    @Test
    fun dataZoomSlider_narrowWindow_rendersAtMinSpan() {
        // A window clamped to the minimum span must still compose (computeZoom clamps to minSpan).
        rule.setContent {
            PaletteMaterialTheme {
                DataZoomSlider(
                    startFraction = 0.45f,
                    endFraction = 0.55f,
                    minSpan = 0.1f,
                    onChange = { _, _ -> },
                    modifier = Modifier.size(400.dp, 40.dp),
                )
            }
        }
        rule.waitForIdle()
    }
    // endregion ────────────────────────────────────────────────────────────────
}
