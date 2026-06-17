package xyz.junerver.compose.palette.components.cascaderpanel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CascaderPanelDefaults {
    val ColumnWidth: Dp = 200.dp
    val ColumnHeight: Dp = 220.dp
    val ItemHeight: Dp = 36.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 14.sp
    val ArrowSize: Dp = 16.dp

    @Composable
    @ReadOnlyComposable
    fun columnWidth(): Dp = PaletteTheme.componentThemes.select.cascaderColumnWidth

    @Composable
    @ReadOnlyComposable
    fun columnHeight(): Dp = PaletteTheme.componentThemes.select.cascaderPanelColumnHeight

    @Composable
    @ReadOnlyComposable
    fun itemHeight(): Dp = PaletteTheme.componentThemes.select.optionHeight

    @Composable
    @ReadOnlyComposable
    fun itemPaddingHorizontal(): Dp = PaletteTheme.componentThemes.select.optionPaddingHorizontal

    @Composable
    @ReadOnlyComposable
    fun fontSize(): TextUnit = PaletteTheme.componentThemes.select.optionTextStyle.fontSize

    @Composable
    @ReadOnlyComposable
    fun arrowSize(): Dp = PaletteTheme.componentThemes.select.arrowSize

    @Composable
    @ReadOnlyComposable
    fun dividerWidth(): Dp = PaletteTheme.componentThemes.select.borderWidth

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.componentThemes.select.dropdownContainerColor

    @Composable
    @ReadOnlyComposable
    fun itemTextColor(): Color = PaletteTheme.componentThemes.select.optionTextColor

    @Composable
    @ReadOnlyComposable
    fun selectedItemColor(): Color = PaletteTheme.componentThemes.select.selectedOptionTextColor

    @Composable
    @ReadOnlyComposable
    fun selectedItemContainerColor(): Color = PaletteTheme.componentThemes.select.selectedOptionContainerColor

    @Composable
    @ReadOnlyComposable
    fun hoverColor(): Color = PaletteTheme.componentThemes.select.hoverOptionContainerColor

    @Composable
    @ReadOnlyComposable
    fun disabledItemTextColor(): Color = PaletteTheme.componentThemes.select.disabledOptionTextColor

    @Composable
    @ReadOnlyComposable
    fun dividerColor(): Color = PaletteTheme.componentThemes.select.dividerColor

    @Composable
    @ReadOnlyComposable
    fun iconColor(): Color = PaletteTheme.componentThemes.select.iconColor

    @Composable
    @ReadOnlyComposable
    fun trailingIconAlpha(): Float = PaletteTheme.componentThemes.select.trailingIconAlpha
}
