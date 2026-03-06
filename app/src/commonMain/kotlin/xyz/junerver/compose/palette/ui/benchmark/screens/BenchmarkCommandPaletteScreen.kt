package xyz.junerver.compose.palette.ui.benchmark.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import xyz.junerver.compose.palette.CommandAction
import xyz.junerver.compose.palette.PCommandPalette
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkTags

@Composable
fun BenchmarkCommandPaletteScreen() {
    val commands = List(2000) { index ->
        CommandAction(
            id = "cmd-$index",
            title = "Command $index",
            subtitle = if (index % 3 == 0) "Subtitle $index" else null,
            keywords = listOf("keyword", "cmd", "action$index")
        )
    }
    Box(modifier = Modifier.fillMaxSize().testTag(BenchmarkTags.CommandPalette)) {
        PCommandPalette(commands, "Command", 10, Modifier, { })
    }
}
