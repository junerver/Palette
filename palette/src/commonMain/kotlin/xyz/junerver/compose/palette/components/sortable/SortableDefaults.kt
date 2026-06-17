package xyz.junerver.compose.palette.components.sortable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SortableDefaults {
    val ItemPadding: Dp = 12.dp
    val ItemSpacing: Dp = 8.dp

    @Composable
    @ReadOnlyComposable
    fun itemPadding(): Dp = PaletteTheme.componentThemes.dataEntry.sortableItemPadding

    @Composable
    @ReadOnlyComposable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.dataEntry.sortableItemSpacing

    @Composable
    @ReadOnlyComposable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataEntry.sortableTextStyle

    @Composable
    @ReadOnlyComposable
    fun itemColor(): Color = PaletteTheme.componentThemes.dataEntry.sortableItemColor

    @Composable
    @ReadOnlyComposable
    fun itemTextColor(): Color = PaletteTheme.componentThemes.dataEntry.sortableItemTextColor

    @Composable
    @ReadOnlyComposable
    fun dragHintColor(): Color = PaletteTheme.componentThemes.dataEntry.sortableDragHintColor
}
