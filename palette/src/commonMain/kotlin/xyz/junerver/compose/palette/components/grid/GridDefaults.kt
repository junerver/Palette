package xyz.junerver.compose.palette.components.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object GridDefaults {
    const val TotalColumns: Int = 24
    val DefaultGutter: Dp = 0.dp

    @Composable
    fun defaultGutter(): Dp = PaletteTheme.componentThemes.layout.gridDefaultGutter

    fun colWidth(span: Int): Float = span.toFloat() / TotalColumns
    fun colOffset(offset: Int): Float = offset.toFloat() / TotalColumns
}
