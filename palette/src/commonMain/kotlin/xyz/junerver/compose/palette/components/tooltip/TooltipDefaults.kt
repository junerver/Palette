package xyz.junerver.compose.palette.components.tooltip

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TooltipDefaults {
    val CornerRadius: Dp = 6.dp
    val HorizontalPadding: Dp = 10.dp
    val VerticalPadding: Dp = 6.dp
    val OffsetY: Dp = 8.dp

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.floatingLayer.tooltipCornerRadius

    @Composable
    fun horizontalPadding(): Dp = PaletteTheme.componentThemes.floatingLayer.tooltipHorizontalPadding

    @Composable
    fun verticalPadding(): Dp = PaletteTheme.componentThemes.floatingLayer.tooltipVerticalPadding

    @Composable
    fun offsetY(): Dp = PaletteTheme.componentThemes.floatingLayer.tooltipOffsetY

    @Composable
    fun maxWidth(): Dp = PaletteTheme.componentThemes.floatingLayer.tooltipMaxWidth

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.floatingLayer.tooltipTextStyle

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.floatingLayer.tooltipBackgroundColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.floatingLayer.tooltipTextColor
}
