package xyz.junerver.compose.palette.components.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DashboardProgressDefaults {
    val Size: Dp = 100.dp
    val StrokeWidth: Dp = 6.dp
    val FontSize: TextUnit = 16.sp
    val StartAngle: Float = 135f
    val SweepAngle: Float = 270f

    fun defaultFormatter(percent: Float): String = "${percent.toInt()}%"

    @Composable
    fun progressColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun trackColor(): Color = PaletteTheme.colors.border

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface
}
