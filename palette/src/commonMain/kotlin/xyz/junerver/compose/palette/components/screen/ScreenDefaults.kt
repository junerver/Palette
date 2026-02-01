package xyz.junerver.compose.palette.components.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.components.toolbar.ToolbarColors
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults
import xyz.junerver.compose.palette.core.util.PaletteDefaults

@Immutable
data class ScreenColors(
    val backgroundColor: Color,
    val contentColor: Color,
)

object ScreenDefaults {
    @Composable
    fun colors(
        backgroundColor: Color = PaletteDefaults.colors.surface,
        contentColor: Color = PaletteDefaults.colors.onSurface,
    ): ScreenColors = ScreenColors(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
    )

    @Composable
    fun toolbarColors(): ToolbarColors = ToolbarDefaults.colors()
}
