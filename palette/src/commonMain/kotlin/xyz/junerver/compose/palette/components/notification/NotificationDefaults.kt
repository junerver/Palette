package xyz.junerver.compose.palette.components.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.message.MessageType
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object NotificationDefaults {
    const val DefaultDuration: Long = 3500L
    const val AnimationDuration: Int = 220

    val TopPadding: Dp = 20.dp
    val HorizontalPadding: Dp = 16.dp
    val VerticalPadding: Dp = 12.dp
    val CornerRadius: Dp = 10.dp
    val BorderWidth: Dp = 1.dp
    val MinWidth: Dp = 260.dp
    val MaxWidth: Dp = 380.dp

    @Composable
    fun defaultDuration(): Long = PaletteTheme.componentThemes.notification.defaultDurationMillis

    @Composable
    fun animationDuration(): Int = PaletteTheme.componentThemes.notification.animationDurationMillis

    @Composable
    fun topPadding(): Dp = PaletteTheme.componentThemes.notification.topPadding

    @Composable
    fun endPadding(): Dp = PaletteTheme.componentThemes.notification.endPadding

    @Composable
    fun horizontalPadding(): Dp = PaletteTheme.componentThemes.notification.horizontalPadding

    @Composable
    fun verticalPadding(): Dp = PaletteTheme.componentThemes.notification.verticalPadding

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.notification.cornerRadius

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.notification.borderWidth

    @Composable
    fun minWidth(): Dp = PaletteTheme.componentThemes.notification.minWidth

    @Composable
    fun maxWidth(): Dp = PaletteTheme.componentThemes.notification.maxWidth

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.notification.iconSize

    @Composable
    fun closeIconSize(): Dp = PaletteTheme.componentThemes.notification.closeIconSize

    @Composable
    fun titleSpacing(): Dp = PaletteTheme.componentThemes.notification.titleSpacing

    @Composable
    fun contentSpacing(): Dp = PaletteTheme.componentThemes.notification.contentSpacing

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.notification.titleTextStyle

    @Composable
    fun contentTextStyle(): TextStyle = PaletteTheme.componentThemes.notification.contentTextStyle

    @Composable
    fun borderAlpha(): Float = PaletteTheme.componentThemes.notification.borderAlpha

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.notification.containerColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.notification.titleColor

    @Composable
    fun contentColor(): Color = PaletteTheme.componentThemes.notification.contentColor

    @Composable
    fun accentColor(type: MessageType): Color = when (type) {
        MessageType.Info -> PaletteTheme.componentThemes.notification.infoColor
        MessageType.Success -> PaletteTheme.componentThemes.notification.successColor
        MessageType.Warning -> PaletteTheme.componentThemes.notification.warningColor
        MessageType.Error -> PaletteTheme.componentThemes.notification.errorColor
    }
}
