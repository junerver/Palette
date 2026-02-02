package xyz.junerver.compose.palette.components.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CardDefaults {
    val CornerRadius: Dp = 12.dp
    val ContentPadding: Dp = 16.dp
    val Elevation: Dp = 1.dp
    val BorderWidth: Dp = 1.dp

    @Composable
    fun elevatedColors(): CardColors = CardColors(
        containerColor = PaletteTheme.colors.surface,
        contentColor = PaletteTheme.colors.onSurface
    )

    @Composable
    fun filledColors(): CardColors = CardColors(
        containerColor = PaletteTheme.colors.surface,
        contentColor = PaletteTheme.colors.onSurface
    )

    @Composable
    fun outlinedColors(): CardColors = CardColors(
        containerColor = PaletteTheme.colors.surface,
        contentColor = PaletteTheme.colors.onSurface
    )
}

data class CardColors(
    val containerColor: Color,
    val contentColor: Color
)
