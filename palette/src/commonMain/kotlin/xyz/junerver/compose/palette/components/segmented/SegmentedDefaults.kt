package xyz.junerver.compose.palette.components.segmented

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SegmentedDefaults {
    val CornerRadius: Dp = 8.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val ItemPaddingVertical: Dp = 6.dp
    val IndicatorAnimationDuration: Int = 200
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.border

    @Composable
    fun selectedItemColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun selectedTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.colors.hint
}
