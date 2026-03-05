package xyz.junerver.compose.palette.components.tooltip

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TooltipDefaults {
    val CornerRadius: Dp = 6.dp
    val HorizontalPadding: Dp = 10.dp
    val VerticalPadding: Dp = 6.dp
    val OffsetY: Dp = 8.dp

    @Composable
    fun backgroundColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.onSurface.copy(alpha = 0.92f)
    } else {
        PaletteTheme.colors.onSurface.copy(alpha = 0.86f)
    }

    @Composable
    fun textColor(): Color = PaletteTheme.colors.surface
}
