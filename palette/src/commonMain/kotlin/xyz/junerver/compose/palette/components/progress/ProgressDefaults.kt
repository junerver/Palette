package xyz.junerver.compose.palette.components.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
    fun linearHeight(): Dp = PaletteTheme.componentThemes.progress.linearHeight

    @Composable
    fun linearContainerHeight(): Dp = PaletteTheme.componentThemes.progress.linearContainerHeight

    @Composable
    fun circleSize(): Dp = PaletteTheme.componentThemes.progress.circleSize

    @Composable
    fun circleStrokeWidth(): Dp = PaletteTheme.componentThemes.progress.circleStrokeWidth

    @Composable
    fun circleVerticalPadding(): Dp = PaletteTheme.componentThemes.progress.circleVerticalPadding

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.progress.textStyle

    @Composable
    fun textSize(): TextUnit = PaletteTheme.componentThemes.progress.textStyle.fontSize

    @Composable
    fun labelWidth(): Dp = PaletteTheme.componentThemes.progress.labelWidth

    @Composable
    fun labelSpacing(): Dp = PaletteTheme.componentThemes.progress.labelSpacing

    @Composable
    fun animationDurationMillis(): Int = PaletteTheme.componentThemes.progress.progressAnimationDurationMillis

    @Composable
    fun progressColor(): Color = PaletteTheme.componentThemes.progress.progressColor

    @Composable
    fun trackColor(): Color = PaletteTheme.componentThemes.progress.trackColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.progress.textColor

    @Composable
    fun circleTextColor(): Color = PaletteTheme.componentThemes.progress.textColor
}
