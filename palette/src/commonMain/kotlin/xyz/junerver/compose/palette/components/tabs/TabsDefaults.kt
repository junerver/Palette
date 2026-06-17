package xyz.junerver.compose.palette.components.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TabsDefaults {
    val HorizontalPadding: Dp = 14.dp
    val VerticalPadding: Dp = 10.dp
    val IndicatorHeight: Dp = 2.dp
    val ItemSpacing: Dp = 4.dp

    @Composable
    fun horizontalPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.tabsHorizontalPadding

    @Composable
    fun verticalPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.tabsVerticalPadding

    @Composable
    fun indicatorHeight(): Dp = PaletteTheme.componentThemes.navigationMenu.tabsIndicatorHeight

    @Composable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.tabsItemSpacing

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.tabsTextStyle

    @Composable
    fun activeColor(): Color = PaletteTheme.componentThemes.navigationMenu.tabsActiveColor

    @Composable
    fun inactiveColor(): Color = PaletteTheme.componentThemes.navigationMenu.tabsInactiveColor

    @Composable
    fun disabledColor(): Color = PaletteTheme.componentThemes.navigationMenu.tabsDisabledColor
}
