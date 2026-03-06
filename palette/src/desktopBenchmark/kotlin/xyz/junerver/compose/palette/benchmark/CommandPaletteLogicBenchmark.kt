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
import xyz.junerver.compose.palette.components.commandpalette.CommandAction
import xyz.junerver.compose.palette.components.commandpalette.filterCommands
import xyz.junerver.compose.palette.components.commandpalette.moveHighlight
import xyz.junerver.compose.palette.components.commandpalette.pickHighlightedCommand

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
open class CommandPaletteLogicBenchmark {
    private lateinit var commands: List<CommandAction>

    @Setup
    fun setup() {
        commands = List(3_000) { index ->
            CommandAction(
                id = "cmd-$index",
                title = "Command $index",
                subtitle = if (index % 3 == 0) "Subtitle $index" else null,
                keywords = listOf("keyword", "cmd", "action$index")
            )
        }
    }

    @Benchmark
    fun filterCommandsHit(bh: Blackhole) {
        bh.consume(filterCommands(commands, "Command 12"))
    }

    @Benchmark
    fun filterCommandsMiss(bh: Blackhole) {
        bh.consume(filterCommands(commands, "no-match"))
    }

    @Benchmark
    fun moveHighlightLoop(bh: Blackhole) {
        var index = 0
        repeat(2_000) {
            index = moveHighlight(index, 1, commands.size)
        }
        bh.consume(pickHighlightedCommand(commands, index))
    }
}
