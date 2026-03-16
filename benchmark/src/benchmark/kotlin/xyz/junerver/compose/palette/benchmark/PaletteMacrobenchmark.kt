package xyz.junerver.compose.palette.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaletteMacrobenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollList() {
        runScrollScenario(route = "list")
    }

    @Test
    fun scrollDataGrid() {
        runScrollScenario(route = "datagrid")
    }

    @Test
    fun scrollVirtualList() {
        runScrollScenario(route = "virtual_list")
    }

    @Test
    fun carouselAutoPlay() {
        runSimpleScenario(route = "carousel")
    }

    @Test
    fun openSelectDropdown() {
        runTapScenario(route = "select", targetTag = "benchmark_select_trigger")
    }

    @Test
    fun commandPaletteFilter() {
        runSimpleScenario(route = "command_palette")
    }

    private fun runScrollScenario(route: String) {
        runBenchmark(route = route) { scope ->
            val device = scope.device
            device.waitForIdle()
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 3 / 4,
                device.displayWidth / 2,
                device.displayHeight / 4,
                20,
            )
            device.waitForIdle()
        }
    }

    private fun runTapScenario(
        route: String,
        targetTag: String,
    ) {
        runBenchmark(route = route) { scope ->
            val device = scope.device
            device.waitForIdle()
            device.findObjectByTag(targetTag)?.click()
            device.waitForIdle()
        }
    }

    private fun runSimpleScenario(route: String) {
        runBenchmark(route = route) { scope ->
            scope.device.waitForIdle()
        }
    }

    private fun runBenchmark(
        route: String,
        measureBlock: (MacrobenchmarkScope) -> Unit,
    ) {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM,
            iterations = 5,
            setupBlock = {
                pressHome()
                startBenchmarkActivity(route)
            },
        ) {
            measureBlock(this)
        }
    }

    private fun MacrobenchmarkScope.startBenchmarkActivity(route: String) {
        startActivityAndWait(
            Intent().apply {
                setClassName(TARGET_PACKAGE, BENCHMARK_ACTIVITY)
                putExtra(EXTRA_BENCHMARK_DESTINATION, route)
            },
        )
    }

    private fun androidx.test.uiautomator.UiDevice.findObjectByTag(tag: String): androidx.test.uiautomator.UiObject2? {
        return findObject(androidx.test.uiautomator.By.res(TARGET_PACKAGE, tag))
    }

    companion object {
        private const val TARGET_PACKAGE = "xyz.junerver.compose.palette"
        private const val BENCHMARK_ACTIVITY = "xyz.junerver.compose.palette.BenchmarkActivity"
        private const val EXTRA_BENCHMARK_DESTINATION = "benchmark_destination"
    }
}
