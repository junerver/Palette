package xyz.junerver.compose.palette.components.transfer

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TransferDefaults {
    val Width: Dp = 200.dp
    val Height: Dp = 300.dp
    val HeaderHeight: Dp = 40.dp
    val ItemHeight: Dp = 36.dp
    val ButtonWidth: Dp = 48.dp
    val ButtonSpacing: Dp = 8.dp
    val FontSize: TextUnit = 14.sp
    val CornerRadius: Dp = 8.dp
    val SearchHeight: Dp = 32.dp
    val SearchFontSize: TextUnit = 12.sp
    val IconSize: Dp = 16.dp

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun headerColor(): Color = PaletteTheme.colors.border

    @Composable
    fun headerTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun itemTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun selectedItemColor(): Color = PaletteTheme.colors.primary.copy(alpha = 0.12f)

    @Composable
    fun buttonColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun disabledButtonColor(): Color = PaletteTheme.colors.border
}
