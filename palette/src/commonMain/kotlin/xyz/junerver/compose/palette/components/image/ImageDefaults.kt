package xyz.junerver.compose.palette.components.image

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ImageDefaults {
    val Shape: Shape = RoundedCornerShape(8.dp)

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface
}
