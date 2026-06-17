package xyz.junerver.compose.palette.components.space

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SpaceDefaults {
    val SmallSpacing: Dp = 8.dp
    val MediumSpacing: Dp = 12.dp
    val LargeSpacing: Dp = 16.dp

    @Composable
    fun smallSpacing(): Dp = PaletteTheme.componentThemes.layout.spaceSmallSpacing

    @Composable
    fun mediumSpacing(): Dp = PaletteTheme.componentThemes.layout.spaceMediumSpacing

    @Composable
    fun largeSpacing(): Dp = PaletteTheme.componentThemes.layout.spaceLargeSpacing
}
