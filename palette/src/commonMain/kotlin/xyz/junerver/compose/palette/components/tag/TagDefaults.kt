package xyz.junerver.compose.palette.components.tag

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TagDefaults {
    val Height: Dp = 32.dp
    val HorizontalPadding: Dp = 12.dp
    val CornerRadius: Dp = 16.dp
    val CloseButtonSize: Dp = 18.dp
    val BorderWidth: Dp = 1.dp

    @Composable
    fun defaultColors(): TagColors = TagColors(
        containerColor = PaletteTheme.colors.primary.copy(alpha = 0.12f),
        contentColor = PaletteTheme.colors.primary,
        borderColor = Color.Transparent
    )

    @Composable
    fun outlinedColors(): TagColors = TagColors(
        containerColor = Color.Transparent,
        contentColor = PaletteTheme.colors.onSurface,
        borderColor = PaletteTheme.colors.border
    )
}

data class TagColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color
)
