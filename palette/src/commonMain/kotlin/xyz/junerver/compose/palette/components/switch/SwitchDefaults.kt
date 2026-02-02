package xyz.junerver.compose.palette.components.switch

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SwitchDefaults {
    val Width: Dp = 50.dp
    val Height: Dp = 26.dp
    val ThumbSize: Dp = 22.dp
    val ThumbOffset: Dp = 2.dp
    val BorderRadius: Dp = 16.dp
    val ThumbBorderRadius: Dp = 50.dp
    val CheckedThumbOffset: Dp = 26.dp
    val DisabledAlpha: Float = 0.7f
    val AnimationDuration: Int = 100

    @Composable
    fun checkedTrackColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun uncheckedTrackColor(): Color = PaletteTheme.colors.border

    @Composable
    fun thumbColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.onSurface
    } else {
        Color.White
    }
}