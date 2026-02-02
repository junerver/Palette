package xyz.junerver.compose.palette.components.skeleton

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SkeletonDefaults {
    val CircleSize: Dp = 100.dp
    val SquareSize: Dp = 100.dp
    val SquareBorderRadius: Dp = 24.dp
    val RectangleHeight: Dp = 200.dp
    val RectangleBorderRadius: Dp = 24.dp
    val LineLongWidth: Dp = 200.dp
    val LineShortWidth: Dp = 100.dp
    val LineHeight: Dp = 30.dp
    val LineBorderRadius: Dp = 8.dp

    @Composable
    fun backgroundColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.onSurface.copy(alpha = 0.12f)
    } else {
        PaletteTheme.colors.onSurface.copy(alpha = 0.08f)
    }
}