package xyz.junerver.compose.palette.benchmark

import androidx.compose.ui.geometry.Offset
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import xyz.junerver.compose.palette.components.chart.ChartSeries
import xyz.junerver.compose.palette.components.chart.PlotRect
import xyz.junerver.compose.palette.components.chart.barLayout
import xyz.junerver.compose.palette.components.chart.deriveYRange
import xyz.junerver.compose.palette.components.chart.hitTestPie
import xyz.junerver.compose.palette.components.chart.hitTestPoint
import xyz.junerver.compose.palette.components.chart.niceTicks
import xyz.junerver.compose.palette.components.chart.pieSliceGeometry

/**
 * Microbenchmarks for the pure chart-layout / hit-test hot paths. These run on both the baseline
 * (pre-animation) and the P3-A working tree to confirm the entrance-animation change — which only
 * touches the @Composable DrawScope, not these functions — introduces NO regression in the
 * reusable logic. Per AGENTS.md: ≥3 runs, watch median + std.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
open class ChartLogicBenchmark {
    private lateinit var series: List<ChartSeries>
    private lateinit var cats: List<String>
    private val plot = PlotRect(left = 50f, top = 10f, width = 400f, height = 200f)

    @Setup
    fun setup() {
        series = listOf(
            ChartSeries("2024", listOf(120f, 200f, 150f, 80f)),
            ChartSeries("2025", listOf(90f, 240f, 180f, 110f)),
        )
        cats = listOf("Q1", "Q2", "Q3", "Q4")
    }

    @Benchmark
    fun deriveYRangeHit(bh: Blackhole) {
        bh.consume(deriveYRange(series, stacked = true))
        bh.consume(deriveYRange(series, stacked = false))
    }

    @Benchmark
    fun niceTicksHit(bh: Blackhole) {
        bh.consume(niceTicks(0f, 440f, count = 4))
        bh.consume(niceTicks(-40f, 60f, count = 4))
    }

    @Benchmark
    fun barLayoutHit(bh: Blackhole) {
        // 4 categories × 2 series, grouped.
        for (cat in 0 until 4) {
            for (s in 0 until 2) {
                bh.consume(
                    barLayout(4, cat, 2, s, value = 100f, accValue = 0f, yMin = 0f, yMax = 440f, stacked = false),
                )
            }
        }
    }

    @Benchmark
    fun hitTestPointHit(bh: Blackhole) {
        // Sweep across the plot, mimicking a hover drag.
        for (x in 60..440 step 40) {
            bh.consume(hitTestPoint(Offset(x.toFloat(), 100f), series, 0f, 440f, cats, plot))
        }
    }

    @Benchmark
    fun pieGeometryHit(bh: Blackhole) {
        val values = listOf(55f, 30f, 15f)
        var acc = 0f
        values.forEachIndexed { i, v ->
            val g = pieSliceGeometry(i, v, acc, 100f)
            bh.consume(g)
            acc += g.sweepAngle
        }
        bh.consume(hitTestPie(Offset(150f, 100f), Offset(100f, 100f), 80f, 0f, 0f, values, listOf("A", "B", "C")))
    }
}
