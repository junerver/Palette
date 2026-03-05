package xyz.junerver.compose.palette.components.commandpalette

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CommandPaletteDefaults {
    val Width: Dp = 560.dp
    val MaxHeight: Dp = 420.dp
    val ItemPadding: Dp = 12.dp

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun subtitleColor(): Color = PaletteTheme.colors.hint

    @Composable
    @ReadOnlyComposable
    fun highlightedContainerColor(): Color = PaletteTheme.colors.primary.copy(alpha = 0.12f)
}
