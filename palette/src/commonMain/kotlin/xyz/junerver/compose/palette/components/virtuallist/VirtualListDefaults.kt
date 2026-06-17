package xyz.junerver.compose.palette.components.virtuallist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object VirtualListDefaults {
    val ItemHeight: Dp = 44.dp
    val ItemPadding: Dp = 12.dp
    val Overscan: Int = 1

    @Composable
    @ReadOnlyComposable
    fun itemHeight(): Dp = PaletteTheme.componentThemes.dataEntry.virtualListItemHeight

    @Composable
    @ReadOnlyComposable
    fun itemPadding(): Dp = PaletteTheme.componentThemes.dataEntry.virtualListItemPadding

    @Composable
    @ReadOnlyComposable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataEntry.virtualListTextStyle

    @Composable
    @ReadOnlyComposable
    fun itemColor(): Color = PaletteTheme.componentThemes.dataEntry.virtualListItemColor

    @Composable
    @ReadOnlyComposable
    fun itemContentColor(): Color = PaletteTheme.componentThemes.dataEntry.virtualListItemContentColor
}
