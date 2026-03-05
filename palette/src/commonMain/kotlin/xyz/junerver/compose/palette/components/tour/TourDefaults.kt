package xyz.junerver.compose.palette.components.tour

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TourDefaults {
    val CornerRadius: Dp = 10.dp
    val ContentPadding: Dp = 14.dp

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun descriptionColor(): Color = PaletteTheme.colors.hint
}
