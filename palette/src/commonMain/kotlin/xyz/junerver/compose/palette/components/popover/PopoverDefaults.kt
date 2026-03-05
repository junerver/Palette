package xyz.junerver.compose.palette.components.popover

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PopoverDefaults {
    val CornerRadius: Dp = 10.dp
    val BorderWidth: Dp = 1.dp
    val Elevation: Dp = 6.dp
    val Padding: Dp = 12.dp

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun borderColor(): Color = PaletteTheme.colors.border
}
