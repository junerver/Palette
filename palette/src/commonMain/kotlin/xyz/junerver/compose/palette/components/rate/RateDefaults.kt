package xyz.junerver.compose.palette.components.rate

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object RateDefaults {
    val StarSize: Dp = 26.dp
    val DisabledAlpha: Float = 0.5f

    fun starSize(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> 20.dp
        ComponentSize.Medium -> 26.dp
        ComponentSize.Large -> 32.dp
    }

    @Composable
    fun starSize(): Dp = PaletteTheme.componentThemes.selectionControl.rateMediumStarSize

    @Composable
    fun componentStarSize(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> PaletteTheme.componentThemes.selectionControl.rateSmallStarSize
        ComponentSize.Medium -> PaletteTheme.componentThemes.selectionControl.rateMediumStarSize
        ComponentSize.Large -> PaletteTheme.componentThemes.selectionControl.rateLargeStarSize
    }

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.selectionControl.sliderDisabledAlpha

    @Composable
    fun activeColor(): Color = PaletteTheme.componentThemes.selectionControl.rateActiveColor

    @Composable
    fun inactiveColor(): Color = PaletteTheme.componentThemes.selectionControl.rateInactiveColor
    
    @Composable
    fun disabledColor(): Color = PaletteTheme.componentThemes.selectionControl.rateDisabledColor
}
