package xyz.junerver.compose.palette.components.loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object LoadingDefaults {
    val Size: Dp = 16.dp
    val DotSize: Dp = 4.dp
    val StrokeWidth: Dp = 2.dp
    val MobileSize: Dp = 34.dp
    val WebSize: Dp = 24.dp
    val MPWidth: Dp = 44.dp
    val MPHeight: Dp = 20.dp
    val MinDotsWidth: Dp = 32.dp
    val AnimationDuration: Int = 1000

    @Composable
    fun size(): Dp = PaletteTheme.componentThemes.progress.loadingSize

    @Composable
    fun dotSize(): Dp = PaletteTheme.componentThemes.progress.loadingDotSize

    @Composable
    fun strokeWidth(): Dp = PaletteTheme.componentThemes.progress.loadingStrokeWidth

    @Composable
    fun mobileSize(): Dp = PaletteTheme.componentThemes.progress.loadingMobileSize

    @Composable
    fun webSize(): Dp = PaletteTheme.componentThemes.progress.loadingWebSize

    @Composable
    fun multipointWidth(): Dp = PaletteTheme.componentThemes.progress.loadingMultipointWidth

    @Composable
    fun multipointHeight(): Dp = PaletteTheme.componentThemes.progress.loadingMultipointHeight

    @Composable
    fun minDotsWidth(): Dp = PaletteTheme.componentThemes.progress.loadingMinDotsWidth

    @Composable
    fun circleOrbitInset(): Dp = PaletteTheme.componentThemes.progress.loadingCircleOrbitInset

    @Composable
    fun bounceOffset(): Dp = PaletteTheme.componentThemes.progress.loadingBounceOffset

    @Composable
    fun activeDotAlpha(): Float = PaletteTheme.componentThemes.progress.loadingActiveDotAlpha

    @Composable
    fun inactiveDotAlpha(): Float = PaletteTheme.componentThemes.progress.loadingInactiveDotAlpha

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.progress.loadingDotsAnimationDurationMillis

    @Composable
    fun barsAnimationDurationMillis(): Int = PaletteTheme.componentThemes.progress.loadingBarsAnimationDurationMillis

    @Composable
    fun barsDelayMillis(): Int = PaletteTheme.componentThemes.progress.loadingBarsDelayMillis

    @Composable
    fun circleAnimationDurationMillis(): Int = PaletteTheme.componentThemes.progress.loadingCircleAnimationDurationMillis

    @Composable
    fun color(): Color = PaletteTheme.componentThemes.progress.loadingColor

    @Composable
    fun outlineColor(): Color = PaletteTheme.componentThemes.progress.loadingOutlineColor

    @Composable
    fun onPrimaryColor(): Color = PaletteTheme.componentThemes.progress.loadingOnPrimaryColor
}



