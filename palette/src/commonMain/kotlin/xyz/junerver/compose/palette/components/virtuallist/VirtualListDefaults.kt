package xyz.junerver.compose.palette.components.virtuallist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object VirtualListDefaults {
    val ItemHeight: Dp = 44.dp
    val ItemPadding: Dp = 12.dp
    val Overscan: Int = 1

    @Composable
    @ReadOnlyComposable
    fun itemColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun itemContentColor(): Color = PaletteTheme.colors.onSurface
}
