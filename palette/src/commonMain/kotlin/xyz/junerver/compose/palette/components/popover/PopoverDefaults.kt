package xyz.junerver.compose.palette.components.popover

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PopoverDefaults {
    val CornerRadius: Dp = 10.dp
    val BorderWidth: Dp = 1.dp
    val Elevation: Dp = 6.dp
    val Padding: Dp = 12.dp

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.floatingLayer.popoverCornerRadius

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.floatingLayer.popoverBorderWidth

    @Composable
    fun elevation(): Dp = PaletteTheme.componentThemes.floatingLayer.popoverElevation

    @Composable
    fun padding(): Dp = PaletteTheme.componentThemes.floatingLayer.popoverPadding

    @Composable
    fun offset(): Dp = PaletteTheme.componentThemes.floatingLayer.popoverOffset

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.floatingLayer.popoverContainerColor

    @Composable
    fun borderColor(): Color = PaletteTheme.componentThemes.floatingLayer.popoverBorderColor
}
