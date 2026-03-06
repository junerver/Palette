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
import xyz.junerver.compose.palette.components.virtuallist.calculateVisibleRange
import xyz.junerver.compose.palette.components.virtuallist.totalHeightPx

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
open class VirtualListLogicBenchmark {
    private val totalItems = 2_000
    private val itemHeightPx = 44
    private val viewportHeightPx = 800
    private lateinit var offsets: IntArray

    @Setup
    fun setup() {
        offsets = IntArray(2_000) { it * 12 }
    }

    @Benchmark
    fun calculateVisibleRangeBulk(bh: Blackhole) {
        for (offset in offsets) {
            bh.consume(
                calculateVisibleRange(
                    scrollOffsetPx = offset,
                    viewportHeightPx = viewportHeightPx,
                    itemHeightPx = itemHeightPx,
                    totalItems = totalItems,
                    overscan = 2
                )
            )
        }
    }

    @Benchmark
    fun totalHeight(bh: Blackhole) {
        bh.consume(totalHeightPx(totalItems = totalItems, itemHeightPx = itemHeightPx))
    }
}
