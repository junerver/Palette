package xyz.junerver.compose.palette.components.datagrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
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
    fun headerContainerColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun headerContentColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun rowContainerColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun rowContentColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun dividerColor(): Color = PaletteTheme.colors.border
}
