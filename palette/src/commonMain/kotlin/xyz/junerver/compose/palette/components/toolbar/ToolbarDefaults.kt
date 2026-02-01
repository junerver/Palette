package xyz.junerver.compose.palette.components.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.util.PaletteDefaults

@Immutable
data class ToolbarColors(
    val backgroundColor: Color,
    val contentColor: Color,
)

object ToolbarDefaults {
    val Height: Dp = 58.dp

    @Composable
    fun colors(
        backgroundColor: Color = PaletteDefaults.colors.primary,
        contentColor: Color = PaletteDefaults.colors.onPrimary,
    ): ToolbarColors = ToolbarColors(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
    )
}
