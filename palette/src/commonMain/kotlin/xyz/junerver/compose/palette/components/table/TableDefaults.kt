package xyz.junerver.compose.palette.components.table

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TableDefaults {
    val HeaderHeight = 56.dp
    val RowHeight = 52.dp
    val CellContentPadding = 16.dp
    val DividerThickness = 1.dp

    @Composable
    @ReadOnlyComposable
    fun headerHeight(): Dp = PaletteTheme.componentThemes.table.headerHeight

    @Composable
    @ReadOnlyComposable
    fun rowHeight(): Dp = PaletteTheme.componentThemes.table.rowHeight

    @Composable
    @ReadOnlyComposable
    fun paginationHeight(): Dp = PaletteTheme.componentThemes.table.paginationHeight

    @Composable
    @ReadOnlyComposable
    fun cellContentPadding(): Dp = PaletteTheme.componentThemes.table.cellContentPadding

    @Composable
    @ReadOnlyComposable
    fun dividerThickness(): Dp = PaletteTheme.componentThemes.table.dividerThickness

    @Composable
    @ReadOnlyComposable
    fun selectionColumnWidth(): Dp = PaletteTheme.componentThemes.table.selectionColumnWidth

    @Composable
    @ReadOnlyComposable
    fun sortIconHeight(): Dp = PaletteTheme.componentThemes.table.sortIconHeight

    @Composable
    @ReadOnlyComposable
    fun emptyContentPadding(): Dp = PaletteTheme.componentThemes.table.emptyContentPadding

    @Composable
    @ReadOnlyComposable
    fun headerTextStyle(): TextStyle = PaletteTheme.componentThemes.table.headerTextStyle

    @Composable
    @ReadOnlyComposable
    fun bodyTextStyle(): TextStyle = PaletteTheme.componentThemes.table.bodyTextStyle

    @Composable
    @ReadOnlyComposable
    fun colors(
        headerContainerColor: Color = PaletteTheme.componentThemes.table.headerContainerColor,
        headerContentColor: Color = PaletteTheme.componentThemes.table.headerContentColor,
        rowContainerColor: Color = PaletteTheme.componentThemes.table.rowContainerColor,
        rowContentColor: Color = PaletteTheme.componentThemes.table.rowContentColor,
        selectedRowContainerColor: Color = PaletteTheme.componentThemes.table.selectedRowContainerColor,
        selectedRowContentColor: Color = PaletteTheme.componentThemes.table.selectedRowContentColor,
        dividerColor: Color = PaletteTheme.componentThemes.table.dividerColor,
    ): TableColors = TableColors(
        headerContainerColor = headerContainerColor,
        headerContentColor = headerContentColor,
        rowContainerColor = rowContainerColor,
        rowContentColor = rowContentColor,
        selectedRowContainerColor = selectedRowContainerColor,
        selectedRowContentColor = selectedRowContentColor,
        dividerColor = dividerColor
    )
}

@Immutable
data class TableColors(
    val headerContainerColor: Color,
    val headerContentColor: Color,
    val rowContainerColor: Color,
    val rowContentColor: Color,
    val selectedRowContainerColor: Color,
    val selectedRowContentColor: Color,
    val dividerColor: Color
)
