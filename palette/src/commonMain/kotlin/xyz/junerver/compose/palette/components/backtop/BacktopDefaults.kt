package xyz.junerver.compose.palette.components.backtop

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BacktopDefaults {
    val Size: Dp = 40.dp
    val IconSize: Dp = 20.dp
    val CornerRadius: Dp = 50.dp
    val Elevation: Dp = 4.dp
    val VisibilityHeight: Int = 200
    val AnimationDuration: Int = 300

    @Composable
    fun size(): Dp = PaletteTheme.componentThemes.floatingAction.backtopSize

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.floatingAction.backtopIconSize

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.floatingAction.backtopCornerRadius

    @Composable
    fun elevation(): Dp = PaletteTheme.componentThemes.floatingAction.backtopElevation

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.floatingAction.backtopAnimationDurationMillis

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.floatingAction.backtopContainerColor

    @Composable
    fun iconColor(): Color = PaletteTheme.componentThemes.floatingAction.backtopIconColor
}
