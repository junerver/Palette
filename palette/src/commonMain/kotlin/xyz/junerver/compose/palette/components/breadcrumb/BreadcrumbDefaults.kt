package xyz.junerver.compose.palette.components.breadcrumb

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BreadcrumbDefaults {
    @Composable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.breadcrumbItemSpacing

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.breadcrumbTextStyle

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.navigationMenu.breadcrumbTextColor

    @Composable
    fun currentColor(): Color = PaletteTheme.componentThemes.navigationMenu.breadcrumbCurrentColor

    @Composable
    fun separatorColor(): Color = PaletteTheme.componentThemes.navigationMenu.breadcrumbSeparatorColor
}
