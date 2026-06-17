package xyz.junerver.compose.palette.components.carousel

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CarouselDefaults {
    val Height: Dp = 200.dp
    val IndicatorSize: Dp = 8.dp
    val ActiveIndicatorSize: Dp = 10.dp
    val InactiveIndicatorSize: Dp = 6.dp
    val IndicatorSpacing: Dp = 8.dp
    val ArrowContainerSize: Dp = 40.dp

    @Composable
    fun height(): Dp = PaletteTheme.componentThemes.media.carouselHeight

    @Composable
    fun indicatorSize(): Dp = PaletteTheme.componentThemes.media.carouselIndicatorSize

    @Composable
    fun activeIndicatorSize(): Dp = PaletteTheme.componentThemes.media.carouselActiveIndicatorSize

    @Composable
    fun inactiveIndicatorSize(): Dp = PaletteTheme.componentThemes.media.carouselInactiveIndicatorSize

    @Composable
    fun indicatorSpacing(): Dp = PaletteTheme.componentThemes.media.carouselIndicatorSpacing

    @Composable
    fun arrowContainerSize(): Dp = PaletteTheme.componentThemes.media.carouselArrowContainerSize

    @Composable
    fun arrowInset(): Dp = PaletteTheme.componentThemes.media.carouselArrowInset

    @Composable
    fun indicatorBottomPadding(): Dp = PaletteTheme.componentThemes.media.carouselIndicatorBottomPadding

    @Composable
    fun indicatorColor(): Color = PaletteTheme.componentThemes.media.carouselIndicatorColor

    @Composable
    fun activeIndicatorColor(): Color = PaletteTheme.componentThemes.media.carouselActiveIndicatorColor

    @Composable
    fun arrowContainerColor(): Color = PaletteTheme.componentThemes.media.carouselArrowContainerColor

    @Composable
    fun arrowContentColor(): Color = PaletteTheme.componentThemes.media.carouselArrowContentColor
}
