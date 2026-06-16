package xyz.junerver.compose.palette.components.pageheader

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PageHeaderDefaults {
    val Height: Dp = 56.dp
    val TitleFontSize: TextUnit = 18.sp
    val SubtitleFontSize: TextUnit = 14.sp
    val BackIconSize: Dp = 20.dp
    val Padding: Dp = 16.dp
    val BackSpacing: Dp = 4.dp

    @Composable
    fun backgroundColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun subtitleColor(): Color = PaletteTheme.colors.hint

    @Composable
    fun backColor(): Color = PaletteTheme.colors.primary
}
