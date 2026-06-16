package xyz.junerver.compose.palette.components.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object GridDefaults {
    const val TotalColumns: Int = 24
    val DefaultGutter: Dp = 0.dp

    fun colWidth(span: Int): Float = span.toFloat() / TotalColumns
    fun colOffset(offset: Int): Float = offset.toFloat() / TotalColumns
}
