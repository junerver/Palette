package xyz.junerver.compose.palette.components.sortable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SortableDefaults {
    val ItemPadding: Dp = 12.dp
    val ItemSpacing: Dp = 8.dp

    @Composable
    @ReadOnlyComposable
    fun itemColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun dragHintColor(): Color = PaletteTheme.colors.primary.copy(alpha = 0.12f)
}
