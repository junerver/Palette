package xyz.junerver.compose.palette.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object MenuDefaults {
    val ItemPaddingHorizontal: Dp = 12.dp
    val ItemPaddingVertical: Dp = 10.dp
    val ItemCornerRadius: Dp = 8.dp

    @Composable
    fun selectedContainerColor(): Color = PaletteTheme.colors.primary.copy(alpha = 0.12f)

    @Composable
    fun selectedTextColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.colors.hint
}
