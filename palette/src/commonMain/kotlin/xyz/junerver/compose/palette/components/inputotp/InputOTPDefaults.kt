package xyz.junerver.compose.palette.components.inputotp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object InputOTPDefaults {
    val CellWidth: Dp = 40.dp
    val CellHeight: Dp = 48.dp
    val CellCornerRadius: Dp = 8.dp
    val CellSpacing: Dp = 8.dp
    val CellBorderWidth: Dp = 1.dp
    val FontSize: TextUnit = 20.sp
    val CursorWidth: Dp = 2.dp
    val CursorHeight: Dp = 24.dp
    const val CursorBlinkDurationMillis: Int = 1000
    val MaskChar: String = "•"
    val SeparatorWidth: Dp = 16.dp
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun cellWidth(): Dp = PaletteTheme.componentThemes.input.otpCellWidth

    @Composable
    fun cellHeight(): Dp = PaletteTheme.componentThemes.input.otpCellHeight

    @Composable
    fun cellCornerRadius(): Dp = PaletteTheme.componentThemes.input.otpCellCornerRadius

    @Composable
    fun cellSpacing(): Dp = PaletteTheme.componentThemes.input.otpCellSpacing

    @Composable
    fun cellBorderWidth(): Dp = PaletteTheme.componentThemes.input.otpCellBorderWidth

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.input.otpTextStyle

    @Composable
    fun cursorWidth(): Dp = PaletteTheme.componentThemes.input.otpCursorWidth

    @Composable
    fun cursorHeight(): Dp = PaletteTheme.componentThemes.input.otpCursorHeight

    @Composable
    fun cursorBlinkDurationMillis(): Int = PaletteTheme.componentThemes.input.otpCursorBlinkDurationMillis

    @Composable
    fun separatorWidth(): Dp = PaletteTheme.componentThemes.input.otpSeparatorWidth

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.input.otpDisabledAlpha

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.input.otpAnimationDurationMillis

    @Composable
    fun cellBorderColor(isFocused: Boolean): Color =
        if (isFocused) PaletteTheme.componentThemes.input.otpFocusedBorderColor else PaletteTheme.componentThemes.input.otpBorderColor

    @Composable
    fun cellBackgroundColor(): Color = PaletteTheme.componentThemes.input.otpCellBackgroundColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.input.otpTextColor

    @Composable
    fun cursorColor(): Color = PaletteTheme.componentThemes.input.otpCursorColor

    @Composable
    fun separatorColor(): Color = PaletteTheme.componentThemes.input.otpSeparatorColor
}
