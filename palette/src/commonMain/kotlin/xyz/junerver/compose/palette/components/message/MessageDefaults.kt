package xyz.junerver.compose.palette.components.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class MessageType {
    Info,
    Success,
    Warning,
    Error
}

object MessageDefaults {
    const val DefaultDuration: Long = 2500L
    const val AnimationDuration: Int = 180

    val TopPadding: Dp = 20.dp
    val HorizontalPadding: Dp = 14.dp
    val VerticalPadding: Dp = 10.dp
    val BorderRadius: Dp = 8.dp
    val BorderWidth: Dp = 1.dp
    val IconSize: Dp = 18.dp
    val IconSpacing: Dp = 8.dp

    @Composable
    fun defaultDuration(): Long = PaletteTheme.componentThemes.message.defaultDurationMillis

    @Composable
    fun animationDuration(): Int = PaletteTheme.componentThemes.message.animationDurationMillis

    @Composable
    fun topPadding(): Dp = PaletteTheme.componentThemes.message.topPadding

    @Composable
    fun screenHorizontalPadding(): Dp = PaletteTheme.componentThemes.message.screenHorizontalPadding

    @Composable
    fun horizontalPadding(): Dp = PaletteTheme.componentThemes.message.horizontalPadding

    @Composable
    fun verticalPadding(): Dp = PaletteTheme.componentThemes.message.verticalPadding

    @Composable
    fun borderRadius(): Dp = PaletteTheme.componentThemes.message.borderRadius

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.message.borderWidth

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.message.iconSize

    @Composable
    fun iconSpacing(): Dp = PaletteTheme.componentThemes.message.iconSpacing

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.message.textStyle

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.message.containerColor

    @Composable
    fun borderColor(type: MessageType): Color = when (type) {
        MessageType.Info -> PaletteTheme.componentThemes.message.infoBorderColor
        MessageType.Success -> PaletteTheme.componentThemes.message.successBorderColor
        MessageType.Warning -> PaletteTheme.componentThemes.message.warningBorderColor
        MessageType.Error -> PaletteTheme.componentThemes.message.errorBorderColor
    }

    @Composable
    fun textColor(type: MessageType): Color = when (type) {
        MessageType.Info -> PaletteTheme.componentThemes.message.infoColor
        MessageType.Success -> PaletteTheme.componentThemes.message.successColor
        MessageType.Warning -> PaletteTheme.componentThemes.message.warningColor
        MessageType.Error -> PaletteTheme.componentThemes.message.errorColor
    }
}
