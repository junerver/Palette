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
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun overlayColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = OverlayAlpha)
}
