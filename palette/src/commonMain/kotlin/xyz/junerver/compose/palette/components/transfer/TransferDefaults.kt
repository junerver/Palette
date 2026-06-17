package xyz.junerver.compose.palette.components.transfer

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    fun width(): Dp = PaletteTheme.componentThemes.dataEntry.transferWidth

    @Composable
    fun height(): Dp = PaletteTheme.componentThemes.dataEntry.transferHeight

    @Composable
    fun headerHeight(): Dp = PaletteTheme.componentThemes.dataEntry.transferHeaderHeight

    @Composable
    fun itemHeight(): Dp = PaletteTheme.componentThemes.dataEntry.transferItemHeight

    @Composable
    fun buttonWidth(): Dp = PaletteTheme.componentThemes.dataEntry.transferButtonWidth

    @Composable
    fun buttonHeight(): Dp = PaletteTheme.componentThemes.dataEntry.transferButtonHeight

    @Composable
    fun buttonSpacing(): Dp = PaletteTheme.componentThemes.dataEntry.transferButtonSpacing

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.dataEntry.transferPanelCornerRadius

    @Composable
    fun panelBorderWidth(): Dp = PaletteTheme.componentThemes.dataEntry.transferPanelBorderWidth

    @Composable
    fun headerPaddingHorizontal(): Dp = PaletteTheme.componentThemes.dataEntry.transferHeaderPaddingHorizontal

    @Composable
    fun rowPaddingHorizontal(): Dp = PaletteTheme.componentThemes.dataEntry.transferRowPaddingHorizontal

    @Composable
    fun searchHeight(): Dp = PaletteTheme.componentThemes.dataEntry.transferSearchHeight

    @Composable
    fun searchPaddingHorizontal(): Dp = PaletteTheme.componentThemes.dataEntry.transferSearchPaddingHorizontal

    @Composable
    fun searchPaddingVertical(): Dp = PaletteTheme.componentThemes.dataEntry.transferSearchPaddingVertical

    @Composable
    fun searchCornerRadius(): Dp = PaletteTheme.componentThemes.dataEntry.transferSearchCornerRadius

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataEntry.transferTextStyle

    @Composable
    fun searchTextStyle(): TextStyle = PaletteTheme.componentThemes.dataEntry.transferSearchTextStyle

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.dataEntry.transferIconSize

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.dataEntry.transferContainerColor

    @Composable
    fun headerColor(): Color = PaletteTheme.componentThemes.dataEntry.transferHeaderColor

    @Composable
    fun headerTextColor(): Color = PaletteTheme.componentThemes.dataEntry.transferHeaderTextColor

    @Composable
    fun itemTextColor(): Color = PaletteTheme.componentThemes.dataEntry.transferItemTextColor

    @Composable
    fun disabledItemTextColor(): Color = PaletteTheme.componentThemes.dataEntry.transferDisabledItemTextColor

    @Composable
    fun selectedItemColor(): Color = PaletteTheme.componentThemes.dataEntry.transferSelectedItemColor

    @Composable
    fun buttonColor(): Color = PaletteTheme.componentThemes.dataEntry.transferButtonColor

    @Composable
    fun buttonContentColor(): Color = PaletteTheme.componentThemes.dataEntry.transferButtonContentColor

    @Composable
    fun disabledButtonColor(): Color = PaletteTheme.componentThemes.dataEntry.transferDisabledButtonColor

    @Composable
    fun searchContainerColor(): Color = PaletteTheme.componentThemes.dataEntry.transferSearchContainerColor

    @Composable
    fun searchTextColor(): Color = PaletteTheme.componentThemes.dataEntry.transferSearchTextColor

    @Composable
    fun searchPlaceholderColor(): Color = PaletteTheme.componentThemes.dataEntry.transferSearchPlaceholderColor

    @Composable
    fun searchCursorColor(): Color = PaletteTheme.componentThemes.dataEntry.transferSearchCursorColor
}
