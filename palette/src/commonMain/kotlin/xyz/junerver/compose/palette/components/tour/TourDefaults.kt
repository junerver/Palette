package xyz.junerver.compose.palette.components.tour

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TourDefaults {
    val CornerRadius: Dp = 10.dp
    val ContentPadding: Dp = 14.dp

    @Composable
    @ReadOnlyComposable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.floatingLayer.tourCornerRadius

    @Composable
    @ReadOnlyComposable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.floatingLayer.tourContentPadding

    @Composable
    @ReadOnlyComposable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.floatingLayer.tourItemSpacing

    @Composable
    @ReadOnlyComposable
    fun buttonSpacing(): Dp = PaletteTheme.componentThemes.floatingLayer.tourButtonSpacing

    @Composable
    @ReadOnlyComposable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.floatingLayer.tourTitleTextStyle

    @Composable
    @ReadOnlyComposable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.floatingLayer.tourDescriptionTextStyle

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.componentThemes.floatingLayer.tourContainerColor

    @Composable
    @ReadOnlyComposable
    fun titleColor(): Color = PaletteTheme.componentThemes.floatingLayer.tourTitleColor

    @Composable
    @ReadOnlyComposable
    fun descriptionColor(): Color = PaletteTheme.componentThemes.floatingLayer.tourDescriptionColor
}
