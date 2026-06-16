package xyz.junerver.compose.palette.components.watermark

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object WatermarkDefaults {
    val FontSize: TextUnit = 14.sp
    val Rotate: Float = -22f
    val GapX: Dp = 100.dp
    val GapY: Dp = 100.dp
    val Alpha: Float = 0.15f

    @Composable
    fun color(): Color = PaletteTheme.colors.onSurface.copy(alpha = Alpha)
}
