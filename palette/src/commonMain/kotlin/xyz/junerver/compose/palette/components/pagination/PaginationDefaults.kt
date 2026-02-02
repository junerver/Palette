package xyz.junerver.compose.palette.components.pagination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PaginationDefaults {
    val MinTouchSize: Dp = 48.dp
    val Spacing: Dp = 4.dp
    
    @Composable
    fun colors(
        activeColor: Color = PaletteTheme.colors.primary,
        textColor: Color = PaletteTheme.colors.onSurface,
        disabledColor: Color = PaletteTheme.colors.onSurface.copy(alpha = 0.38f),
        activeBackgroundColor: Color = PaletteTheme.colors.primary.copy(alpha = 0.12f),
        backgroundColor: Color = Color.Transparent,
        disabledBackgroundColor: Color = Color.Transparent
    ): PaginationColors = PaginationColors(
        activeColor = activeColor,
        textColor = textColor,
        disabledColor = disabledColor,
        activeBackgroundColor = activeBackgroundColor,
        backgroundColor = backgroundColor,
        disabledBackgroundColor = disabledBackgroundColor
    )
}

@Immutable
data class PaginationColors(
    val activeColor: Color,
    val textColor: Color,
    val disabledColor: Color,
    val activeBackgroundColor: Color,
    val backgroundColor: Color,
    val disabledBackgroundColor: Color
)
