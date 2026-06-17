package xyz.junerver.compose.palette.components.popconfirm

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PopconfirmDefaults {
    val CornerRadius: Dp = 8.dp
    val Padding: Dp = 12.dp
    val TitleFontSize: TextUnit = 14.sp
    val DescriptionFontSize: TextUnit = 12.sp
    val ButtonSpacing: Dp = 8.dp
    val Elevation: Dp = 4.dp
    val MaxWidth: Dp = 280.dp

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.floatingLayer.popconfirmCornerRadius

    @Composable
    fun padding(): Dp = PaletteTheme.componentThemes.floatingLayer.popconfirmPadding

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.floatingLayer.popconfirmTitleTextStyle

    @Composable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.floatingLayer.popconfirmDescriptionTextStyle

    @Composable
    fun descriptionSpacing(): Dp = PaletteTheme.componentThemes.floatingLayer.popconfirmDescriptionSpacing

    @Composable
    fun buttonSpacing(): Dp = PaletteTheme.componentThemes.floatingLayer.popconfirmButtonSpacing

    @Composable
    fun elevation(): Dp = PaletteTheme.componentThemes.floatingLayer.popconfirmElevation

    @Composable
    fun maxWidth(): Dp = PaletteTheme.componentThemes.floatingLayer.popconfirmMaxWidth

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.floatingLayer.popconfirmContainerColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.floatingLayer.popconfirmTitleColor

    @Composable
    fun descriptionColor(): Color = PaletteTheme.componentThemes.floatingLayer.popconfirmDescriptionColor
}
