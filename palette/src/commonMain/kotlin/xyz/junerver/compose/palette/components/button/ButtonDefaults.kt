package xyz.junerver.compose.palette.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class ButtonType {
    PRIMARY,
    DANGER,
    PLAIN
}

enum class ButtonSize(
    val padding: PaddingValues,
    val fontSize: TextUnit,
    val borderRadius: Dp = 8.dp
) {
    LARGE(PaddingValues(vertical = 12.dp, horizontal = 24.dp), 17.sp),
    MEDIUM(PaddingValues(vertical = 10.dp, horizontal = 24.dp), 14.sp),
    SMALL(PaddingValues(vertical = 6.dp, horizontal = 12.dp), 14.sp, 6.dp)
}

object ButtonDefaults {
    val DefaultWidth: Dp = 184.dp
    val LoadingSpacing: Dp = 8.dp
    val DisabledAlpha: Float = 0.7f

    @Composable
    fun primaryContainerColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun primaryContentColor(): Color = PaletteTheme.colors.onPrimary

    @Composable
    fun dangerContainerColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.error
    } else {
        Color.Black.copy(0.05f)
    }

    @Composable
    fun dangerContentColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.onError
    } else {
        PaletteTheme.colors.error
    }

    @Composable
    fun plainContainerColor(): Color = if (PaletteTheme.isDark) {
        PaletteTheme.colors.surface
    } else {
        Color.Black.copy(0.05f)
    }

    @Composable
    fun plainContentColor(): Color = PaletteTheme.colors.onSurface
}