package xyz.junerver.compose.palette.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DialogDefaults {
    val BorderRadius: Dp = 12.dp
    val TitlePaddingTop: Dp = 32.dp
    val TitlePaddingBottom: Dp = 16.dp
    val ContentPaddingBottom: Dp = 32.dp
    val HorizontalPadding: Dp = 24.dp
    val ButtonHeight: Dp = 56.dp
    val DividerWidth: Dp = 0.5.dp
    val TitleFontSize: TextUnit = 17.sp
    val ContentFontSize: TextUnit = 17.sp
    val ButtonFontSize: TextUnit = 17.sp

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.dialog.containerColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.dialog.titleColor

    @Composable
    fun contentColor(): Color = PaletteTheme.componentThemes.dialog.contentColor

    @Composable
    fun cancelColor(): Color = PaletteTheme.componentThemes.dialog.cancelColor

    @Composable
    fun okColor(): Color = PaletteTheme.componentThemes.dialog.okColor

    @Composable
    fun dividerColor(): Color = PaletteTheme.componentThemes.dialog.dividerColor

    @Composable
    fun borderRadius(): Dp = PaletteTheme.componentThemes.dialog.borderRadius

    @Composable
    fun widthFraction(): Float = PaletteTheme.componentThemes.dialog.widthFraction

    @Composable
    fun titlePaddingTop(): Dp = PaletteTheme.componentThemes.dialog.titlePaddingTop

    @Composable
    fun titlePaddingBottom(): Dp = PaletteTheme.componentThemes.dialog.titlePaddingBottom

    @Composable
    fun contentPaddingBottom(): Dp = PaletteTheme.componentThemes.dialog.contentPaddingBottom

    @Composable
    fun horizontalPadding(): Dp = PaletteTheme.componentThemes.dialog.horizontalPadding

    @Composable
    fun buttonHeight(): Dp = PaletteTheme.componentThemes.dialog.buttonHeight

    @Composable
    fun dividerWidth(): Dp = PaletteTheme.componentThemes.dialog.dividerWidth

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.dialog.titleTextStyle

    @Composable
    fun contentTextStyle(): TextStyle = PaletteTheme.componentThemes.dialog.contentTextStyle

    @Composable
    fun buttonTextStyle(): TextStyle = PaletteTheme.componentThemes.dialog.buttonTextStyle
}
