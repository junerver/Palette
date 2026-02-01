package xyz.junerver.compose.palette.components.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.util.PaletteDefaults

object BadgeDefaults {
    val Size: Dp = 10.dp

    fun size(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> 8.dp
        ComponentSize.Medium -> 10.dp
        ComponentSize.Large -> 14.dp
    }

    @Composable
    fun color(): Color = PaletteDefaults.colors.error
}
