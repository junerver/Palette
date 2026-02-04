package xyz.junerver.compose.palette.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ToastDefaults {
    val IconSize: Dp = 136.dp
    val NoIconWidth: Dp = 152.dp
    val NoIconMinHeight: Dp = 44.dp
    val IconBorderRadius: Dp = 12.dp
    val NoIconBorderRadius: Dp = 8.dp
    val LoadingSize: Dp = 43.dp
    val IconSpacing: Dp = 10.dp
    val TextPaddingHorizontal: Dp = 12.dp
    val TextPaddingVertical: Dp = 6.dp
    val IconFontSize: TextUnit = 17.sp
    val NoIconFontSize: TextUnit = 14.sp
    @Composable
    fun backgroundColor(): Color = if (PaletteTheme.isDark) {
        Color.White.copy(alpha = 0.16f)
    } else {
        Color.Black.copy(alpha = 0.7f)
    }
    @Composable
    fun textColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.onSurface
    } else {
        Color.White
    }
    val AnimationDuration: Int = 100
    val DefaultDuration: Long = 1500L
}


