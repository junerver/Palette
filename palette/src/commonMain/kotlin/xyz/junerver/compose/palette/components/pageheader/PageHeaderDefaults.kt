package xyz.junerver.compose.palette.components.pageheader

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PageHeaderDefaults {
    val Height: Dp = 56.dp
    val TitleFontSize: TextUnit = 18.sp
    val SubtitleFontSize: TextUnit = 14.sp
    val BackIconSize: Dp = 20.dp
    val Padding: Dp = 16.dp
    val BackSpacing: Dp = 4.dp

    @Composable
    fun height(): Dp = PaletteTheme.componentThemes.appBar.pageHeaderHeight

    @Composable
    fun padding(): Dp = PaletteTheme.componentThemes.appBar.pageHeaderPadding

    @Composable
    fun backIconSize(): Dp = PaletteTheme.componentThemes.appBar.pageHeaderBackIconSize

    @Composable
    fun backSpacing(): Dp = PaletteTheme.componentThemes.appBar.pageHeaderBackSpacing

    @Composable
    fun backSectionSpacing(): Dp = PaletteTheme.componentThemes.appBar.pageHeaderBackSectionSpacing

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.appBar.pageHeaderTitleTextStyle

    @Composable
    fun subtitleTextStyle(): TextStyle = PaletteTheme.componentThemes.appBar.pageHeaderSubtitleTextStyle

    @Composable
    fun backTextStyle(): TextStyle = PaletteTheme.componentThemes.appBar.pageHeaderBackTextStyle

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.appBar.pageHeaderBackgroundColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.appBar.pageHeaderTitleColor

    @Composable
    fun subtitleColor(): Color = PaletteTheme.componentThemes.appBar.pageHeaderSubtitleColor

    @Composable
    fun backColor(): Color = PaletteTheme.componentThemes.appBar.pageHeaderBackColor
}
