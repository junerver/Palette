package xyz.junerver.compose.palette.components.searchbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SearchBarDefaults {
    val Height: Dp = 38.dp
    val CornerRadius: Dp = 6.dp
    val IconSize: Dp = 20.dp
    val IconPadding: Dp = 8.dp
    val ContentPadding: Dp = 8.dp
    val FontSize: TextUnit = 16.sp
    val CancelFontSize: TextUnit = 16.sp

    @Composable
    fun backgroundColor(): Color = PaletteTheme.colors.border

    @Composable
    fun placeholderColor(): Color = PaletteTheme.colors.hint

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun iconColor(): Color = PaletteTheme.colors.hint

    @Composable
    fun cancelColor(): Color = PaletteTheme.colors.primary
}
