package xyz.junerver.compose.palette.components.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SliderDefaults {
    val Height: Dp = 48.dp
    val TrackHeight: Dp = 2.dp
    val ThumbSize: Dp = 28.dp
    val ThumbShadowElevation: Dp = 14.dp
    val LabelSpacing: Dp = 12.dp
    val LabelWidth: Dp = 40.dp
    val LabelFontSize: TextUnit = 14.sp

    @Composable
    fun activeTrackColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun inactiveTrackColor(): Color = PaletteTheme.colors.border

    @Composable
    fun thumbColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.onSurface
    } else {
        Color.White
    }

    @Composable
    fun labelColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun thumbShadowColor(): Color = PaletteTheme.colors.border
}