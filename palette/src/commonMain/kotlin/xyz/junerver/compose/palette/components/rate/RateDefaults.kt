package xyz.junerver.compose.palette.components.rate

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object RateDefaults {
    val StarSize: Dp = 26.dp

    @Composable
    fun activeColor(): Color = PaletteTheme.colors.warning

    @Composable
    fun inactiveColor(): Color = PaletteTheme.colors.border
}
