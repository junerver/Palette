package xyz.junerver.compose.palette.components.commandpalette

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CommandPaletteDefaults {
    val Width: Dp = 560.dp
    val MaxHeight: Dp = 420.dp
    val ItemPadding: Dp = 12.dp

    @Composable
    @ReadOnlyComposable
    fun width(): Dp = PaletteTheme.componentThemes.navigationMenu.commandPaletteWidth

    @Composable
    @ReadOnlyComposable
    fun maxHeight(): Dp = PaletteTheme.componentThemes.navigationMenu.commandPaletteMaxHeight

    @Composable
    @ReadOnlyComposable
    fun itemPadding(): Dp = PaletteTheme.componentThemes.navigationMenu.commandPaletteItemPadding

    @Composable
    @ReadOnlyComposable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.navigationMenu.commandPaletteItemSpacing

    @Composable
    @ReadOnlyComposable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.commandPaletteTitleTextStyle

    @Composable
    @ReadOnlyComposable
    fun subtitleTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.commandPaletteSubtitleTextStyle

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.componentThemes.navigationMenu.containerColor

    @Composable
    @ReadOnlyComposable
    fun titleColor(): Color = PaletteTheme.componentThemes.navigationMenu.textColor

    @Composable
    @ReadOnlyComposable
    fun subtitleColor(): Color = PaletteTheme.componentThemes.navigationMenu.subtitleColor

    @Composable
    @ReadOnlyComposable
    fun highlightedContainerColor(): Color =
        PaletteTheme.componentThemes.navigationMenu.commandPaletteHighlightedContainerColor
}
