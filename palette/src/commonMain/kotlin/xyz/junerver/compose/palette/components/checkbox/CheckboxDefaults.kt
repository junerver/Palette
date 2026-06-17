package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.tokens.PaletteCheckboxSizeTokens

@Immutable
data class CheckboxColors(
    val checkedColor: Color,
    val uncheckedColor: Color,
    val checkmarkColor: Color,
    val disabledColor: Color,
    val focusColor: Color,
    val hoverColor: Color
)

object CheckboxDefaults {
    @Composable
    fun color(): Color = PaletteTheme.componentThemes.checkbox.checkedColor

    @Composable
    fun sizeTokens(size: ComponentSize): PaletteCheckboxSizeTokens = when (size) {
        ComponentSize.Small -> PaletteTheme.componentThemes.checkbox.small
        ComponentSize.Medium -> PaletteTheme.componentThemes.checkbox.medium
        ComponentSize.Large -> PaletteTheme.componentThemes.checkbox.large
    }

    @Composable
    fun motionDuration(): Int = PaletteTheme.componentThemes.checkbox.motionDuration

    @Composable
    fun disabledBorderAlpha(): Float = PaletteTheme.componentThemes.checkbox.disabledBorderAlpha

    @Composable
    fun focusRingAlpha(): Float = PaletteTheme.componentThemes.checkbox.focusRingAlpha

    @Composable
    fun hoverBackgroundAlpha(): Float = PaletteTheme.componentThemes.checkbox.hoverBackgroundAlpha

    @Composable
    fun touchPadding(size: ComponentSize): Dp = sizeTokens(size).touchPadding

    @Composable
    fun colors(
        checkedColor: Color = PaletteTheme.componentThemes.checkbox.checkedColor,
        uncheckedColor: Color = PaletteTheme.componentThemes.checkbox.uncheckedColor,
        checkmarkColor: Color = PaletteTheme.componentThemes.checkbox.checkmarkColor,
        disabledColor: Color = PaletteTheme.componentThemes.checkbox.disabledColor,
        focusColor: Color = PaletteTheme.componentThemes.checkbox.focusColor,
        hoverColor: Color = PaletteTheme.componentThemes.checkbox.hoverColor
    ): CheckboxColors = CheckboxColors(
        checkedColor = checkedColor,
        uncheckedColor = uncheckedColor,
        checkmarkColor = checkmarkColor,
        disabledColor = disabledColor,
        focusColor = focusColor,
        hoverColor = hoverColor
    )
}
