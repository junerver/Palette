package xyz.junerver.compose.palette.components.radio

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object RadioDefaults {
    val Padding: Dp = 16.dp
    val BorderRadius: Dp = 10.dp
    val IconSize: Dp = 24.dp
    val DescriptionSpacing: Dp = 4.dp
    val LabelFontSize: TextUnit = 17.sp
    val DescriptionFontSize: TextUnit = 14.sp
    val DisabledAlpha: Float = 0.4f

    @Composable
    fun labelColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun descriptionColor(): Color = PaletteTheme.colors.hint

    @Composable
    fun checkedColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun uncheckedColor(): Color = PaletteTheme.colors.border
}