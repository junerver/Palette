package xyz.junerver.compose.palette.components.floatbutton

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class FloatButtonShape {
    Circle, Square
}

object FloatButtonDefaults {
    val Size: Dp = 48.dp
    val IconSize: Dp = 20.dp
    val CornerRadius: Dp = 12.dp
    val Elevation: Dp = 6.dp
    val TextPadding: Dp = 12.dp
    val TextFontSize: TextUnit = 14.sp

    @Composable
    fun size(): Dp = PaletteTheme.componentThemes.floatingAction.floatButtonSize

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.floatingAction.floatButtonIconSize

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.floatingAction.floatButtonCornerRadius

    @Composable
    fun elevation(): Dp = PaletteTheme.componentThemes.floatingAction.floatButtonElevation

    @Composable
    fun textPadding(): Dp = PaletteTheme.componentThemes.floatingAction.floatButtonTextPadding

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.floatingAction.floatButtonTextStyle

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.floatingAction.floatButtonContainerColor

    @Composable
    fun iconColor(): Color = PaletteTheme.componentThemes.floatingAction.floatButtonIconColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.floatingAction.floatButtonTextColor
}
