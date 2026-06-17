package xyz.junerver.compose.palette.components.drawer

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class DrawerPlacement {
    Start,
    End
}

object DrawerDefaults {
    val Width: Dp = 320.dp
    val Elevation: Dp = 8.dp
    val OverlayAlpha: Float = 0.45f
    val ContentPadding: Dp = 16.dp

    @Composable
    fun width(): Dp = PaletteTheme.componentThemes.drawer.width

    @Composable
    fun elevation(): Dp = PaletteTheme.componentThemes.drawer.elevation

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.drawer.contentPadding

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.drawer.animationDurationMillis

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.drawer.containerColor

    @Composable
    fun overlayColor(): Color = PaletteTheme.componentThemes.drawer.overlayColor
}
