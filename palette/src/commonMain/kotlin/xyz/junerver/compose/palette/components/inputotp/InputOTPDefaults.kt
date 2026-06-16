package xyz.junerver.compose.palette.components.inputotp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.focusBorder

object InputOTPDefaults {
    val CellWidth: Dp = 40.dp
    val CellHeight: Dp = 48.dp
    val CellCornerRadius: Dp = 8.dp
    val CellSpacing: Dp = 8.dp
    val CellBorderWidth: Dp = 1.dp
    val FontSize: TextUnit = 20.sp
    val MaskChar: String = "•"
    val SeparatorWidth: Dp = 16.dp
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun cellBorderColor(isFocused: Boolean): Color =
        if (isFocused) PaletteTheme.colors.focusBorder else PaletteTheme.colors.border

    @Composable
    fun cellBackgroundColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun cursorColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun separatorColor(): Color = PaletteTheme.colors.border
}
