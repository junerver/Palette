package xyz.junerver.compose.palette.ui.benchmark.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import xyz.junerver.compose.palette.PVirtualList
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkTags

@Composable
fun BenchmarkVirtualListScreen() {
    val items = List(2000) { "Virtual $it" }
    Box(modifier = Modifier.fillMaxSize().testTag(BenchmarkTags.VirtualList)) {
        PVirtualList(
            items = items,
            itemText = { it },
            key = { it }
        )
    }
}
