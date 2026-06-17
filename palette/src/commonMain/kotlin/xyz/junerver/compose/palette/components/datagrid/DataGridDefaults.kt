package xyz.junerver.compose.palette.components.datagrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DataGridDefaults {
    val HeaderHeight: Dp = 48.dp
    val RowHeight: Dp = 44.dp
    val CellPadding: Dp = 12.dp
    val DefaultPageSize: Int = 10

    @Composable
    @ReadOnlyComposable
    fun headerHeight(): Dp = PaletteTheme.componentThemes.dataGrid.headerHeight

    @Composable
    @ReadOnlyComposable
    fun rowHeight(): Dp = PaletteTheme.componentThemes.dataGrid.rowHeight

    @Composable
    @ReadOnlyComposable
    fun cellPadding(): Dp = PaletteTheme.componentThemes.dataGrid.cellPadding

    @Composable
    @ReadOnlyComposable
    fun defaultPageSize(): Int = PaletteTheme.componentThemes.dataGrid.defaultPageSize

    @Composable
    @ReadOnlyComposable
    fun headerTextStyle(): TextStyle = PaletteTheme.componentThemes.dataGrid.headerTextStyle

    @Composable
    @ReadOnlyComposable
    fun rowTextStyle(): TextStyle = PaletteTheme.componentThemes.dataGrid.rowTextStyle

    @Composable
    @ReadOnlyComposable
    fun headerContainerColor(): Color = PaletteTheme.componentThemes.dataGrid.headerContainerColor

    @Composable
    @ReadOnlyComposable
    fun headerContentColor(): Color = PaletteTheme.componentThemes.dataGrid.headerContentColor

    @Composable
    @ReadOnlyComposable
    fun rowContainerColor(): Color = PaletteTheme.componentThemes.dataGrid.rowContainerColor

    @Composable
    @ReadOnlyComposable
    fun rowContentColor(): Color = PaletteTheme.componentThemes.dataGrid.rowContentColor

    @Composable
    @ReadOnlyComposable
    fun dividerColor(): Color = PaletteTheme.componentThemes.dataGrid.dividerColor
}
