package xyz.junerver.compose.palette.components.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class ToggleVariant {
    Default, Outline
}

data class ToggleItem(
    val value: String,
    val label: String,
    val icon: (@Composable (() -> Unit))? = null,
    val disabled: Boolean = false,
)

object ToggleDefaults {
    val CornerRadius: Dp = 6.dp
    val PaddingHorizontal: Dp = 12.dp
    val PaddingVertical: Dp = 8.dp
    val GroupSpacing: Dp = 0.dp
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun containerColor(pressed: Boolean): Color =
        if (pressed) PaletteTheme.colors.primary.copy(alpha = 0.1f) else Color.Transparent

    @Composable
    fun borderColor(pressed: Boolean): Color =
        if (pressed) PaletteTheme.colors.primary else PaletteTheme.colors.border

    @Composable
    fun contentColor(pressed: Boolean): Color =
        if (pressed) PaletteTheme.colors.primary else PaletteTheme.colors.onSurface
}
