package xyz.junerver.compose.palette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkApp
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkDestination

class BenchmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val destination = intent?.getStringExtra(EXTRA_BENCHMARK_DESTINATION)
        setContent {
            BenchmarkApp(
                destination = BenchmarkDestination.fromRoute(destination)
            )
        }
    }

    companion object {
        const val EXTRA_BENCHMARK_DESTINATION = "benchmark_destination"
    }
}
