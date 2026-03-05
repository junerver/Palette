package xyz.junerver.compose.palette.components.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class MessageType {
    Info,
    Success,
    Warning,
    Error
}

object MessageDefaults {
    const val DefaultDuration: Long = 2500L
    const val AnimationDuration: Int = 180

    val TopPadding: Dp = 20.dp
    val HorizontalPadding: Dp = 14.dp
    val VerticalPadding: Dp = 10.dp
    val BorderRadius: Dp = 8.dp
    val BorderWidth: Dp = 1.dp
    val IconSize: Dp = 18.dp
    val IconSpacing: Dp = 8.dp

    @Composable
    fun containerColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.surface.copy(alpha = 0.95f)
    } else {
        PaletteTheme.colors.surface
    }

    @Composable
    fun borderColor(type: MessageType): Color = when (type) {
        MessageType.Info -> PaletteTheme.colors.primary.copy(alpha = 0.45f)
        MessageType.Success -> PaletteTheme.colors.success.copy(alpha = 0.5f)
        MessageType.Warning -> PaletteTheme.colors.warning.copy(alpha = 0.5f)
        MessageType.Error -> PaletteTheme.colors.error.copy(alpha = 0.5f)
    }

    @Composable
    fun textColor(type: MessageType): Color = when (type) {
        MessageType.Info -> PaletteTheme.colors.primary
        MessageType.Success -> PaletteTheme.colors.success
        MessageType.Warning -> PaletteTheme.colors.warning
        MessageType.Error -> PaletteTheme.colors.error
    }
}
