package xyz.junerver.compose.palette.components.descriptions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DescriptionsDefaults {
    val RowHeight: Dp = 40.dp
    val LabelWidth: Dp = 120.dp
    val Padding: Dp = 12.dp

    @Composable
    fun labelColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.6f)

    @Composable
    fun contentColor(): Color = PaletteTheme.colors.onSurface
}
