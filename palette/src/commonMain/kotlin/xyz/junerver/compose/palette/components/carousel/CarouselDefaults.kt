package xyz.junerver.compose.palette.components.carousel

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CarouselDefaults {
    val Height: Dp = 200.dp
    val IndicatorSize: Dp = 8.dp
    val IndicatorSpacing: Dp = 8.dp
    val ArrowContainerSize: Dp = 40.dp

    @Composable
    fun indicatorColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.38f)

    @Composable
    fun activeIndicatorColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun arrowContainerColor(): Color = PaletteTheme.colors.surface.copy(alpha = 0.6f)

    @Composable
    fun arrowContentColor(): Color = PaletteTheme.colors.onSurface
}
