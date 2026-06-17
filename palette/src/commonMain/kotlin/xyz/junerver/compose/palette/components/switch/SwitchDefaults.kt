package xyz.junerver.compose.palette.components.switch

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens

object SwitchDefaults {
    val Width: Dp = 50.dp
    val Height: Dp = 26.dp
    val ThumbSize: Dp = 22.dp
    val ThumbOffset: Dp = 2.dp
    val BorderRadius: Dp = 16.dp
    val ThumbBorderRadius: Dp = 50.dp
    val CheckedThumbOffset: Dp = 26.dp
    val DisabledAlpha: Float = 0.5f
    val AnimationDuration: Int = FormTokens.DurationNormal

    @Composable
    fun checkedTrackColor(): Color = PaletteTheme.componentThemes.switch.checkedTrackColor

    @Composable
    fun uncheckedTrackColor(): Color = PaletteTheme.componentThemes.switch.uncheckedTrackColor

    @Composable
    fun thumbColor(): Color = PaletteTheme.componentThemes.switch.thumbColor
    
    @Composable
    fun disabledTrackColor(): Color = PaletteTheme.componentThemes.switch.disabledTrackColor

    @Composable
    fun width(): Dp = PaletteTheme.componentThemes.switch.width

    @Composable
    fun height(): Dp = PaletteTheme.componentThemes.switch.height

    @Composable
    fun thumbSize(): Dp = PaletteTheme.componentThemes.switch.thumbSize

    @Composable
    fun thumbOffset(): Dp = PaletteTheme.componentThemes.switch.thumbOffset

    @Composable
    fun checkedThumbOffset(): Dp = PaletteTheme.componentThemes.switch.checkedThumbOffset

    @Composable
    fun borderRadius(): Dp = PaletteTheme.componentThemes.switch.borderRadius

    @Composable
    fun thumbBorderRadius(): Dp = PaletteTheme.componentThemes.switch.thumbBorderRadius

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.switch.disabledAlpha

    @Composable
    fun animationDuration(): Int = PaletteTheme.componentThemes.switch.motionDuration
}
