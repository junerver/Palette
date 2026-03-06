package xyz.junerver.compose.palette.ui.benchmark.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.PSelect
import xyz.junerver.compose.palette.SelectOption
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkTags

@Composable
fun BenchmarkSelectScreen() {
    val options = List(2000) { index ->
        SelectOption(label = "Option $index", value = index)
    }
    Box(modifier = Modifier.fillMaxSize().testTag(BenchmarkTags.Select)) {
        PSelect(
            options = options,
            value = null,
            onValueChange = {},
            searchable = true,
            placeholder = "Select",
            modifier = Modifier
                .width(280.dp)
                .testTag(BenchmarkTags.SelectTrigger)
        )
    }
}
