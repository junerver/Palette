package xyz.junerver.compose.palette.components.rate

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.disabledBorder

object RateDefaults {
    val StarSize: Dp = 26.dp
    val DisabledAlpha: Float = 0.5f

    fun starSize(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> 20.dp
        ComponentSize.Medium -> 26.dp
        ComponentSize.Large -> 32.dp
    }

    @Composable
    fun activeColor(): Color = PaletteTheme.colors.warning

    @Composable
    fun inactiveColor(): Color = PaletteTheme.colors.border
    
    @Composable
    fun disabledColor(): Color = PaletteTheme.colors.disabledBorder
}
