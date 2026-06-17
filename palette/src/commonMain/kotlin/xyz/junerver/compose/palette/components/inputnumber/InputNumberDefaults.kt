package xyz.junerver.compose.palette.components.inputnumber

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object InputNumberDefaults {
    val Width: Dp = 220.dp
    val ButtonWidth: Dp = 36.dp
    val MinButtonWidth: Dp = 32.dp
    val AnimationDuration: Int = 100
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun width(): Dp = PaletteTheme.componentThemes.input.inputNumberWidth

    @Composable
    fun buttonWidth(): Dp = PaletteTheme.componentThemes.input.inputNumberButtonWidth

    @Composable
    fun minButtonWidth(): Dp = PaletteTheme.componentThemes.input.inputNumberMinButtonWidth

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.input.inputNumberAnimationDurationMillis

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.input.inputNumberDisabledAlpha

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.input.inputNumberContainerColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.input.inputNumberTextColor

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.componentThemes.input.inputNumberDisabledTextColor

    @Composable
    fun placeholderColor(): Color = PaletteTheme.componentThemes.input.inputNumberPlaceholderColor

    @Composable
    fun cursorColor(): Color = PaletteTheme.componentThemes.input.inputNumberCursorColor

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.input.inputNumberBorderWidth

    @Composable
    fun dividerWidth(): Dp = PaletteTheme.componentThemes.input.inputNumberDividerWidth

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.input.inputNumberTextStyle

    @Composable
    fun buttonColor(): Color = PaletteTheme.componentThemes.input.inputNumberButtonColor

    @Composable
    fun buttonIconColor(): Color = PaletteTheme.componentThemes.input.inputNumberButtonIconColor

    @Composable
    fun disabledButtonColor(): Color = PaletteTheme.componentThemes.input.inputNumberDisabledButtonColor

    @Composable
    fun disabledButtonIconColor(): Color = PaletteTheme.componentThemes.input.inputNumberDisabledButtonIconColor

    @Composable
    fun hoverButtonColor(): Color = PaletteTheme.componentThemes.input.inputNumberHoverButtonColor
}
