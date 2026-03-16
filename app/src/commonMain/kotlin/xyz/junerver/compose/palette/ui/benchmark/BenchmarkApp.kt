package xyz.junerver.compose.palette.ui.benchmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.ui.benchmark.screens.BenchmarkCarouselScreen
import xyz.junerver.compose.palette.ui.benchmark.screens.BenchmarkCommandPaletteScreen
import xyz.junerver.compose.palette.ui.benchmark.screens.BenchmarkDataGridScreen
import xyz.junerver.compose.palette.ui.benchmark.screens.BenchmarkListScreen
import xyz.junerver.compose.palette.ui.benchmark.screens.BenchmarkSelectScreen
import xyz.junerver.compose.palette.ui.benchmark.screens.BenchmarkVirtualListScreen

@Composable
fun BenchmarkApp(destination: BenchmarkDestination) {
    PaletteMaterialTheme(
        colors = PaletteColors.light(),
        strings = PaletteStrings.enUS(),
        darkTheme = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTagsAsResourceId()
                .testTag(BenchmarkTags.Root)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (destination) {
                BenchmarkDestination.CAROUSEL -> BenchmarkCarouselScreen()
                BenchmarkDestination.LIST -> BenchmarkListScreen()
                BenchmarkDestination.SELECT -> BenchmarkSelectScreen()
                BenchmarkDestination.DATAGRID -> BenchmarkDataGridScreen()
                BenchmarkDestination.COMMAND_PALETTE -> BenchmarkCommandPaletteScreen()
                BenchmarkDestination.VIRTUAL_LIST -> BenchmarkVirtualListScreen()
            }
        }
    }
}
