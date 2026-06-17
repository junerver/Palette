package xyz.junerver.compose.palette.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ToastDefaults {
    val IconSize: Dp = 136.dp
    val NoIconWidth: Dp = 152.dp
    val NoIconMinHeight: Dp = 44.dp
    val IconBorderRadius: Dp = 12.dp
    val NoIconBorderRadius: Dp = 8.dp
    val LoadingSize: Dp = 43.dp
    val IconSpacing: Dp = 10.dp
    val TextPaddingHorizontal: Dp = 12.dp
    val TextPaddingVertical: Dp = 6.dp
    val IconFontSize: TextUnit = 17.sp
    val NoIconFontSize: TextUnit = 14.sp

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.toast.iconSize

    @Composable
    fun noIconWidth(): Dp = PaletteTheme.componentThemes.toast.noIconWidth

    @Composable
    fun noIconMinHeight(): Dp = PaletteTheme.componentThemes.toast.noIconMinHeight

    @Composable
    fun iconBorderRadius(): Dp = PaletteTheme.componentThemes.toast.iconBorderRadius

    @Composable
    fun noIconBorderRadius(): Dp = PaletteTheme.componentThemes.toast.noIconBorderRadius

    @Composable
    fun loadingSize(): Dp = PaletteTheme.componentThemes.toast.loadingSize

    @Composable
    fun iconSpacing(): Dp = PaletteTheme.componentThemes.toast.iconSpacing

    @Composable
    fun textPaddingHorizontal(): Dp = PaletteTheme.componentThemes.toast.textPaddingHorizontal

    @Composable
    fun textPaddingVertical(): Dp = PaletteTheme.componentThemes.toast.textPaddingVertical

    @Composable
    fun iconTextStyle(): TextStyle = PaletteTheme.componentThemes.toast.iconTextStyle

    @Composable
    fun noIconTextStyle(): TextStyle = PaletteTheme.componentThemes.toast.noIconTextStyle

    @Composable
    fun iconTextMaxWidthPx(): Int = PaletteTheme.componentThemes.toast.iconTextMaxWidthPx

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.toast.backgroundColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.toast.textColor

    val AnimationDuration: Int = 100
    val DefaultDuration: Long = 1500L

    @Composable
    fun animationDuration(): Int = PaletteTheme.componentThemes.toast.animationDurationMillis

    @Composable
    fun exitDelay(): Long = PaletteTheme.componentThemes.toast.exitDelayMillis

    @Composable
    fun defaultDuration(): Long = PaletteTheme.componentThemes.toast.defaultDurationMillis
}


