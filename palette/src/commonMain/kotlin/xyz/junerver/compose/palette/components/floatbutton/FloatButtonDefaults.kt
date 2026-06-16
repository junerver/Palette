package xyz.junerver.compose.palette.components.floatbutton

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class FloatButtonShape {
    Circle, Square
}

object FloatButtonDefaults {
    val Size: Dp = 48.dp
    val IconSize: Dp = 20.dp
    val CornerRadius: Dp = 12.dp
    val Elevation: Dp = 6.dp
    val TextPadding: Dp = 12.dp
    val TextFontSize: TextUnit = 14.sp

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun iconColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface
}
