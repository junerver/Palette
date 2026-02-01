package xyz.junerver.compose.palette.components.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BadgeDefaults {
    val Size: Dp = 10.dp

    @Composable
    fun color(): Color = PaletteTheme.colors.error
}
