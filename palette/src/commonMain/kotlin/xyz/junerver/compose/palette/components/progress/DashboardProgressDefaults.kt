package xyz.junerver.compose.palette.components.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    fun size(): Dp = PaletteTheme.componentThemes.progress.dashboardSize

    @Composable
    fun strokeWidth(): Dp = PaletteTheme.componentThemes.progress.dashboardStrokeWidth

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.progress.dashboardTextStyle

    @Composable
    fun fontSize(): TextUnit = PaletteTheme.componentThemes.progress.dashboardTextStyle.fontSize

    @Composable
    fun startAngle(): Float = PaletteTheme.componentThemes.progress.dashboardStartAngle

    @Composable
    fun sweepAngle(): Float = PaletteTheme.componentThemes.progress.dashboardSweepAngle

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.progress.progressAnimationDurationMillis

    @Composable
    fun progressColor(): Color = PaletteTheme.componentThemes.progress.progressColor

    @Composable
    fun trackColor(): Color = PaletteTheme.componentThemes.progress.trackColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.progress.textColor
}
