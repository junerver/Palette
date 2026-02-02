package xyz.junerver.compose.palette.components.empty

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object EmptyDefaults {
    val IconSize: Dp = 64.dp
    val IconToTitle: Dp = 16.dp
    val TitleToDescription: Dp = 8.dp
    val DescriptionToAction: Dp = 24.dp
    
    @Composable
    fun iconColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.38f)
    
    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface
    
    @Composable
    fun descriptionColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.6f)
}
