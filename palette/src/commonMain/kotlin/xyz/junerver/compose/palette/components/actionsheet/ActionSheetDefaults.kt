package xyz.junerver.compose.palette.components.actionsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    fun itemHeight(): Dp = PaletteTheme.componentThemes.actionSheet.itemHeight

    @Composable
    fun titleHeight(): Dp = PaletteTheme.componentThemes.actionSheet.titleHeight

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.actionSheet.titleTextStyle

    @Composable
    fun titleFontSize(): TextUnit = PaletteTheme.componentThemes.actionSheet.titleTextStyle.fontSize

    @Composable
    fun itemTextStyle(): TextStyle = PaletteTheme.componentThemes.actionSheet.itemTextStyle

    @Composable
    fun itemFontSize(): TextUnit = PaletteTheme.componentThemes.actionSheet.itemTextStyle.fontSize

    @Composable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.actionSheet.descriptionTextStyle

    @Composable
    fun descriptionFontSize(): TextUnit = PaletteTheme.componentThemes.actionSheet.descriptionTextStyle.fontSize

    @Composable
    fun cancelTextStyle(): TextStyle = PaletteTheme.componentThemes.actionSheet.cancelTextStyle

    @Composable
    fun cancelFontSize(): TextUnit = PaletteTheme.componentThemes.actionSheet.cancelTextStyle.fontSize

    @Composable
    fun iconSpacing(): Dp = PaletteTheme.componentThemes.actionSheet.iconSpacing

    @Composable
    fun itemPadding(): Dp = PaletteTheme.componentThemes.actionSheet.itemPadding

    @Composable
    fun gapHeight(): Dp = PaletteTheme.componentThemes.actionSheet.gapHeight

    @Composable
    fun cancelHeight(): Dp = PaletteTheme.componentThemes.actionSheet.cancelHeight

    @Composable
    fun dividerThickness(): Dp = PaletteTheme.componentThemes.actionSheet.dividerThickness

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.actionSheet.disabledAlpha

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.actionSheet.containerColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.actionSheet.titleColor

    @Composable
    fun itemTextColor(): Color = PaletteTheme.componentThemes.actionSheet.itemTextColor

    @Composable
    fun descriptionColor(): Color = PaletteTheme.componentThemes.actionSheet.descriptionColor

    @Composable
    fun cancelTextColor(): Color = PaletteTheme.componentThemes.actionSheet.cancelTextColor

    @Composable
    fun dividerColor(): Color = PaletteTheme.componentThemes.actionSheet.dividerColor

    @Composable
    fun dangerColor(): Color = PaletteTheme.componentThemes.actionSheet.dangerColor
}
