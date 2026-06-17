package xyz.junerver.compose.palette.components.timeline

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TimelineDefaults {
    val DotSize: Dp = 8.dp
    val LineWidth: Dp = 2.dp
    val DotToContent: Dp = 16.dp
    val ItemSpacing: Dp = 24.dp

    @Composable
    fun dotSize(): Dp = PaletteTheme.componentThemes.dataEntry.timelineDotSize

    @Composable
    fun lineWidth(): Dp = PaletteTheme.componentThemes.dataEntry.timelineLineWidth

    @Composable
    fun dotToContent(): Dp = PaletteTheme.componentThemes.dataEntry.timelineDotToContent

    @Composable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.dataEntry.timelineItemSpacing

    @Composable
    fun lineColor(): Color = PaletteTheme.componentThemes.dataEntry.timelineLineColor

    @Composable
    fun dotColor(): Color = PaletteTheme.componentThemes.dataEntry.timelineDotColor
}
