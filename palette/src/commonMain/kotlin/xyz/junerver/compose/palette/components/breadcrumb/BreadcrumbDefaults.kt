package xyz.junerver.compose.palette.components.breadcrumb

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BreadcrumbDefaults {
    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.72f)

    @Composable
    fun currentColor(): Color = PaletteTheme.colors.onSurface
}
