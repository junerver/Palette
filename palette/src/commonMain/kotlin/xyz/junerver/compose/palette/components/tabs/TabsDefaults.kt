package xyz.junerver.compose.palette.components.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TabsDefaults {
    val HorizontalPadding: Dp = 14.dp
    val VerticalPadding: Dp = 10.dp
    val IndicatorHeight: Dp = 2.dp
    val ItemSpacing: Dp = 4.dp

    @Composable
    fun activeColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun inactiveColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.7f)
}
