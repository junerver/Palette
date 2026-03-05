package xyz.junerver.compose.palette.components.steps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object StepsDefaults {
    val DotSize: Dp = 20.dp
    val LineWidth: Dp = 2.dp

    @Composable
    fun doneColor(): Color = PaletteTheme.colors.success

    @Composable
    fun currentColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun pendingColor(): Color = PaletteTheme.colors.border
}
