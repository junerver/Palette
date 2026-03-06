package xyz.junerver.compose.palette.ui.benchmark.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.list.PList
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkTags

@Composable
fun BenchmarkListScreen() {
    val data = List(2000) { "Item $it" }
    Box(modifier = Modifier.fillMaxSize().testTag(BenchmarkTags.List)) {
        PList(
            data = data,
            showDivider = true
        ) { item ->
            // Keep content minimal for list scroll measurement.
            Text(text = item)
        }
    }
}
