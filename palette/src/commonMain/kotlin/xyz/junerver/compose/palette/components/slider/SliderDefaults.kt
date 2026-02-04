package xyz.junerver.compose.palette.components.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens
import xyz.junerver.compose.palette.core.tokens.disabledBorder

object SliderDefaults {
    val Height: Dp = 48.dp
    val TrackHeight: Dp = FormTokens.BorderWidthFocus
    val ThumbSize: Dp = 28.dp
    val ThumbShadowElevation: Dp = FormTokens.ShadowBlur
    val LabelSpacing: Dp = 12.dp
    val LabelWidth: Dp = 40.dp
    val LabelFontSize: TextUnit = 14.sp
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun activeTrackColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun inactiveTrackColor(): Color = PaletteTheme.colors.border

    @Composable
    fun thumbColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun labelColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun thumbShadowColor(): Color = PaletteTheme.colors.border
    
    @Composable
    fun disabledTrackColor(): Color = PaletteTheme.colors.disabledBorder
}