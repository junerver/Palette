package xyz.junerver.compose.palette.components.popup

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PopupDefaults {
    val CornerRadius: Dp = 12.dp
    val ContentPadding: Dp = 12.dp
    val DraggableLineLength: Dp = 40.dp
    val DraggableLineThickness: Dp = 4.dp
    val DraggableLineOffset: Dp = -(12).dp
    val TitleHeight: Dp = 50.dp
    val TitleFontSize: TextUnit = 17.sp
    val AnimationDuration: Int = 150
    // Drag distance is interaction behavior rather than a visual style token.
    val DragDismissThreshold: Float = 0.5f

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.popup.cornerRadius

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.popup.contentPadding

    @Composable
    fun draggableLineLength(): Dp = PaletteTheme.componentThemes.popup.draggableLineLength

    @Composable
    fun draggableLineThickness(): Dp = PaletteTheme.componentThemes.popup.draggableLineThickness

    @Composable
    fun draggableLineOffset(): Dp = PaletteTheme.componentThemes.popup.draggableLineOffset

    @Composable
    fun titleHeight(): Dp = PaletteTheme.componentThemes.popup.titleHeight

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.popup.titleTextStyle

    @Composable
    fun titleFontSize(): TextUnit = PaletteTheme.componentThemes.popup.titleTextStyle.fontSize

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.popup.animationDurationMillis

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.popup.containerColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.popup.titleColor

    @Composable
    fun draggableLineColor(): Color = PaletteTheme.componentThemes.popup.draggableLineColor
}
