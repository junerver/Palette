package xyz.junerver.compose.palette.components.avatar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.util.PaletteDefaults

enum class AvatarSize(val size: Dp) {
    Small(24.dp),
    Medium(40.dp),
    Large(56.dp),
    XLarge(96.dp)
}

object AvatarDefaults {
    @Composable
    fun backgroundColor(): Color = PaletteDefaults.colors.primary

    @Composable
    fun textColor(): Color = PaletteDefaults.colors.onPrimary
}
