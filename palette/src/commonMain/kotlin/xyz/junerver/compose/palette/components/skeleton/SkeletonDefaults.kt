package xyz.junerver.compose.palette.components.skeleton

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SkeletonDefaults {
    val CircleSize: Dp = 100.dp
    val SquareSize: Dp = 100.dp
    val SquareBorderRadius: Dp = 24.dp
    val RectangleHeight: Dp = 200.dp
    val RectangleBorderRadius: Dp = 24.dp
    val LineLongWidth: Dp = 200.dp
    val LineShortWidth: Dp = 100.dp
    val LineHeight: Dp = 30.dp
    val LineBorderRadius: Dp = 8.dp

    @Composable
    fun circleSize(): Dp = PaletteTheme.componentThemes.progress.skeletonCircleSize

    @Composable
    fun squareSize(): Dp = PaletteTheme.componentThemes.progress.skeletonSquareSize

    @Composable
    fun squareBorderRadius(): Dp = PaletteTheme.componentThemes.progress.skeletonSquareBorderRadius

    @Composable
    fun rectangleHeight(): Dp = PaletteTheme.componentThemes.progress.skeletonRectangleHeight

    @Composable
    fun rectangleBorderRadius(): Dp = PaletteTheme.componentThemes.progress.skeletonRectangleBorderRadius

    @Composable
    fun lineLongWidth(): Dp = PaletteTheme.componentThemes.progress.skeletonLineLongWidth

    @Composable
    fun lineShortWidth(): Dp = PaletteTheme.componentThemes.progress.skeletonLineShortWidth

    @Composable
    fun lineHeight(): Dp = PaletteTheme.componentThemes.progress.skeletonLineHeight

    @Composable
    fun lineBorderRadius(): Dp = PaletteTheme.componentThemes.progress.skeletonLineBorderRadius

    @Composable
    fun shimmerStartColor(): Color = PaletteTheme.componentThemes.progress.skeletonShimmerStartColor

    @Composable
    fun shimmerCenterColor(): Color = PaletteTheme.componentThemes.progress.skeletonShimmerCenterColor

    @Composable
    fun shimmerEndColor(): Color = PaletteTheme.componentThemes.progress.skeletonShimmerEndColor

    @Composable
    fun shimmerAnimationDurationMillis(): Int = PaletteTheme.componentThemes.progress.skeletonShimmerAnimationDurationMillis

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.progress.skeletonBackgroundColor
}
