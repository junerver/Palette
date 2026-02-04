package xyz.junerver.compose.palette.components.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BadgeDefaults {
    val Size: Dp = 16.dp

    fun size(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> 12.dp
        ComponentSize.Medium -> 16.dp
        ComponentSize.Large -> 20.dp
    }

    @Composable
    fun color(): Color = PaletteTheme.colors.error
}