package xyz.junerver.compose.palette.components.popconfirm

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun descriptionColor(): Color = PaletteTheme.colors.hint
}
