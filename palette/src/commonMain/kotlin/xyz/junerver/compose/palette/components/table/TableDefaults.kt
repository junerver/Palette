package xyz.junerver.compose.palette.components.table

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TableDefaults {
    val HeaderHeight = 56.dp
    val RowHeight = 52.dp
    val CellContentPadding = 16.dp
    val DividerThickness = 1.dp

    @Composable
    @ReadOnlyComposable
    fun colors(
        headerContainerColor: Color = PaletteTheme.colors.surface,
        headerContentColor: Color = PaletteTheme.colors.onSurface,
        rowContainerColor: Color = PaletteTheme.colors.surface,
        rowContentColor: Color = PaletteTheme.colors.onSurface,
        selectedRowContainerColor: Color = PaletteTheme.colors.primary.copy(alpha = 0.08f),
        selectedRowContentColor: Color = PaletteTheme.colors.primary,
        dividerColor: Color = PaletteTheme.colors.border,
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
