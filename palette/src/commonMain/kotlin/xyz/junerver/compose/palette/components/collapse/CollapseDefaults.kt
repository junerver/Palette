package xyz.junerver.compose.palette.components.collapse

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CollapseDefaults {
    val TitleHeight: Dp = 48.dp
    val ContentPadding: Dp = 16.dp
    val AnimationDuration: Int = 300

    @Composable
    fun titleHeight(): Dp = PaletteTheme.componentThemes.navigationMenu.collapseTitleHeight

    @Composable
    fun titleHorizontalPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.collapseTitleHorizontalPadding

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.collapseContentPadding

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.collapseTitleTextStyle

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.navigationMenu.collapseAnimationDurationMillis

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.navigationMenu.collapseTitleColor

    @Composable
    fun contentColor(): Color = PaletteTheme.componentThemes.navigationMenu.collapseContentColor

    @Composable
    fun iconColor(): Color = PaletteTheme.componentThemes.navigationMenu.collapseIconColor
}
