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
    fun lineColor(): Color = PaletteTheme.colors.border

    @Composable
    fun dotColor(): Color = PaletteTheme.colors.primary
}
