package xyz.junerver.compose.palette.components.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object LoadingDefaults {
    val Size: Dp = 16.dp
    val DotSize: Dp = 4.dp
    val StrokeWidth: Dp = 2.dp
    val MobileSize: Dp = 34.dp
    val WebSize: Dp = 24.dp
    val MPWidth: Dp = 44.dp
    val MPHeight: Dp = 20.dp
    val MinDotsWidth: Dp = 32.dp
    val AnimationDuration: Int = 1000

    @Composable
    fun color(): Color = PaletteTheme.colors.primary

    @Composable
    fun outlineColor(): Color = PaletteTheme.colors.border

    @Composable
    fun onPrimaryColor(): Color = PaletteTheme.colors.onPrimary
}



