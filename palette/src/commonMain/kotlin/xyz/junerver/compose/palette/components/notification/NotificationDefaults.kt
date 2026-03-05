package xyz.junerver.compose.palette.components.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.message.MessageType
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object NotificationDefaults {
    const val DefaultDuration: Long = 3500L
    const val AnimationDuration: Int = 220

    val TopPadding: Dp = 20.dp
    val HorizontalPadding: Dp = 16.dp
    val VerticalPadding: Dp = 12.dp
    val CornerRadius: Dp = 10.dp
    val BorderWidth: Dp = 1.dp
    val MinWidth: Dp = 260.dp
    val MaxWidth: Dp = 380.dp

    @Composable
    fun containerColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.surface.copy(alpha = 0.98f)
    } else {
        PaletteTheme.colors.surface
    }

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun contentColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.75f)

    @Composable
    fun accentColor(type: MessageType): Color = when (type) {
        MessageType.Info -> PaletteTheme.colors.primary
        MessageType.Success -> PaletteTheme.colors.success
        MessageType.Warning -> PaletteTheme.colors.warning
        MessageType.Error -> PaletteTheme.colors.error
    }
}
