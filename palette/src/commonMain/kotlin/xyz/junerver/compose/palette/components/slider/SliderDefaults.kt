package xyz.junerver.compose.palette.components.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens

object SliderDefaults {
    val Height: Dp = 48.dp
    val TrackHeight: Dp = FormTokens.BorderWidthFocus
    val ThumbSize: Dp = 28.dp
    val ThumbShadowElevation: Dp = FormTokens.ShadowBlur
    val LabelSpacing: Dp = 12.dp
    val LabelWidth: Dp = 40.dp
    val LabelFontSize: TextUnit = 14.sp
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun height(): Dp = PaletteTheme.componentThemes.selectionControl.sliderHeight

    @Composable
    fun trackHeight(): Dp = PaletteTheme.componentThemes.selectionControl.sliderTrackHeight

    @Composable
    fun thumbSize(): Dp = PaletteTheme.componentThemes.selectionControl.sliderThumbSize

    @Composable
    fun thumbShadowElevation(): Dp = PaletteTheme.componentThemes.selectionControl.sliderThumbShadowElevation

    @Composable
    fun labelSpacing(): Dp = PaletteTheme.componentThemes.selectionControl.sliderLabelSpacing

    @Composable
    fun labelWidth(): Dp = PaletteTheme.componentThemes.selectionControl.sliderLabelWidth

    @Composable
    fun labelTextStyle(): TextStyle = PaletteTheme.componentThemes.selectionControl.sliderLabelTextStyle

    @Composable
    fun labelFontSize(): TextUnit = PaletteTheme.componentThemes.selectionControl.sliderLabelTextStyle.fontSize

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.selectionControl.sliderDisabledAlpha

    @Composable
    fun activeTrackColor(): Color = PaletteTheme.componentThemes.selectionControl.sliderActiveTrackColor

    @Composable
    fun inactiveTrackColor(): Color = PaletteTheme.componentThemes.selectionControl.sliderInactiveTrackColor

    @Composable
    fun thumbColor(): Color = PaletteTheme.componentThemes.selectionControl.sliderThumbColor

    @Composable
    fun labelColor(): Color = PaletteTheme.componentThemes.selectionControl.sliderLabelColor

    @Composable
    fun thumbShadowColor(): Color = PaletteTheme.componentThemes.selectionControl.sliderThumbShadowColor
    
    @Composable
    fun disabledTrackColor(): Color = PaletteTheme.componentThemes.selectionControl.sliderDisabledTrackColor
}
