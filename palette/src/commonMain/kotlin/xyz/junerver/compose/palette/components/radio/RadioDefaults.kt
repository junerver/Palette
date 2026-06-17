package xyz.junerver.compose.palette.components.radio

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.PaletteRadioSizeTokens

object RadioDefaults {
    val Padding: Dp = 16.dp
    val BorderRadius: Dp = 10.dp
    val IconSize: Dp = 24.dp
    val DescriptionSpacing: Dp = 4.dp
    val LabelFontSize: TextUnit = 17.sp
    val DescriptionFontSize: TextUnit = 14.sp
    val DisabledAlpha: Float = 0.4f

    @Composable
    fun labelColor(): Color = PaletteTheme.componentThemes.radio.labelColor

    @Composable
    fun descriptionColor(): Color = PaletteTheme.componentThemes.radio.descriptionColor

    @Composable
    fun checkedColor(): Color = PaletteTheme.componentThemes.radio.checkedColor

    @Composable
    fun uncheckedColor(): Color = PaletteTheme.componentThemes.radio.uncheckedColor

    @Composable
    fun disabledColor(): Color = PaletteTheme.componentThemes.radio.disabledColor

    @Composable
    fun focusColor(): Color = PaletteTheme.componentThemes.radio.focusColor

    @Composable
    fun hoverColor(): Color = PaletteTheme.componentThemes.radio.hoverColor

    @Composable
    fun padding(): Dp = PaletteTheme.componentThemes.radio.itemPadding

    @Composable
    fun borderRadius(): Dp = PaletteTheme.componentThemes.radio.itemRadius

    @Composable
    fun descriptionSpacing(): Dp = PaletteTheme.componentThemes.radio.descriptionSpacing

    @Composable
    fun labelTextStyle(): TextStyle = PaletteTheme.componentThemes.radio.labelTextStyle

    @Composable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.radio.descriptionTextStyle

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.radio.disabledAlpha

    @Composable
    fun focusRingAlpha(): Float = PaletteTheme.componentThemes.radio.focusRingAlpha

    @Composable
    fun hoverBackgroundAlpha(): Float = PaletteTheme.componentThemes.radio.hoverBackgroundAlpha

    @Composable
    fun motionDuration(): Int = PaletteTheme.componentThemes.radio.motionDuration

    @Composable
    fun sizeTokens(size: ComponentSize): PaletteRadioSizeTokens = when (size) {
        ComponentSize.Small -> PaletteTheme.componentThemes.radio.small
        ComponentSize.Medium -> PaletteTheme.componentThemes.radio.medium
        ComponentSize.Large -> PaletteTheme.componentThemes.radio.large
    }
}
