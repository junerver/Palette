package xyz.junerver.compose.palette.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object MenuDefaults {
    val ItemPaddingHorizontal: Dp = 12.dp
    val ItemPaddingVertical: Dp = 10.dp
    val ItemCornerRadius: Dp = 8.dp

    @Composable
    fun itemPaddingHorizontal(): Dp = PaletteTheme.componentThemes.navigationMenu.itemPaddingHorizontal

    @Composable
    fun itemPaddingVertical(): Dp = PaletteTheme.componentThemes.navigationMenu.itemPaddingVertical

    @Composable
    fun itemCornerRadius(): Dp = PaletteTheme.componentThemes.navigationMenu.itemCornerRadius

    @Composable
    fun itemOuterSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.itemOuterSpacing

    @Composable
    fun itemTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.itemTextStyle

    @Composable
    fun selectedContainerColor(): Color = PaletteTheme.componentThemes.navigationMenu.selectedContainerColor

    @Composable
    fun selectedTextColor(): Color = PaletteTheme.componentThemes.navigationMenu.selectedTextColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.navigationMenu.textColor

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.componentThemes.navigationMenu.disabledTextColor
}
