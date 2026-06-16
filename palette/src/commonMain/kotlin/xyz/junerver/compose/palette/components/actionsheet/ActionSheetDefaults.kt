package xyz.junerver.compose.palette.components.actionsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ActionSheetDefaults {
    val ItemHeight: Dp = 56.dp
    val TitleHeight: Dp = 56.dp
    val TitleFontSize: TextUnit = 12.sp
    val ItemFontSize: TextUnit = 17.sp
    val DescriptionFontSize: TextUnit = 12.sp
    val CancelFontSize: TextUnit = 17.sp
    val IconSpacing: Dp = 6.dp
    val ItemPadding: Dp = 12.dp
    val GapHeight: Dp = 8.dp
    val CancelHeight: Dp = 56.dp
    val DisabledAlpha: Float = 0.4f

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.hint

    @Composable
    fun itemTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun descriptionColor(): Color = PaletteTheme.colors.hint

    @Composable
    fun cancelTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun dividerColor(): Color = PaletteTheme.colors.border

    @Composable
    fun dangerColor(): Color = PaletteTheme.colors.error
}
