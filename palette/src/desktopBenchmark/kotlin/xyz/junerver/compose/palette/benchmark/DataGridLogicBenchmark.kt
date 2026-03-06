package xyz.junerver.compose.palette.benchmark

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
import xyz.junerver.compose.palette.components.datagrid.filterRows

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
open class DataGridLogicBenchmark {
    private lateinit var rows: List<Row>

    @Setup
    fun setup() {
        rows = List(3_000) { index ->
            Row(
                id = index,
                name = "Row $index",
                status = if (index % 2 == 0) "OK" else "WARN"
            )
        }
    }

    @Benchmark
    fun filterRowsHit(bh: Blackhole) {
        bh.consume(filterRows(rows, "warn") { row -> listOf(row.name, row.status) })
    }

    @Benchmark
    fun filterRowsMiss(bh: Blackhole) {
        bh.consume(filterRows(rows, "no-match") { row -> listOf(row.name, row.status) })
    }

    data class Row(
        val id: Int,
        val name: String,
        val status: String,
    )
}
