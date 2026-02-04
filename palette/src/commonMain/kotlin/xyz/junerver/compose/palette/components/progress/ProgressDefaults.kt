package xyz.junerver.compose.palette.components.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ProgressDefaults {
    val LinearHeight: Dp = 3.dp
    val LinearContainerHeight: Dp = 66.dp
    val CircleSize: Dp = 100.dp
    val CircleStrokeWidth: Dp = 6.dp
    val TextSize = 14.sp
    val LabelWidth: Dp = 40.dp
    val LabelSpacing: Dp = 10.dp

    @Composable
    fun progressColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun trackColor(): Color = PaletteTheme.colors.border

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun circleTextColor(): Color = PaletteTheme.colors.onSurface
}
