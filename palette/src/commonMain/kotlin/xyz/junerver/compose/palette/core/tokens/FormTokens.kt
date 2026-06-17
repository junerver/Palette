package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object FormTokens {
    val BorderWidthDefault = 1.dp
    val BorderWidthFocus = 2.dp
    val BorderWidthDisabled = 1.dp

    val CornerRadiusSmall = 4.dp
    val CornerRadiusMedium = 6.dp
    val CornerRadiusLarge = 8.dp

    val HeightSmall = 24.dp
    val HeightMedium = 32.dp
    val HeightLarge = 40.dp

    val PaddingHorizontalSmall = 12.dp
    val PaddingHorizontalMedium = 16.dp
    val PaddingHorizontalLarge = 20.dp

    val PaddingVerticalSmall = 4.dp
    val PaddingVerticalMedium = 8.dp
    val PaddingVerticalLarge = 12.dp

    const val DurationFast = 150
    const val DurationNormal = 250
    const val DurationSlow = 350

    val FocusRingOffset = 2.dp
    val ShadowBlur = 4.dp

    @Composable
    fun borderWidthDefault() = PaletteTheme.control.borderWidth

    @Composable
    fun borderWidthFocus() = PaletteTheme.control.focusBorderWidth

    @Composable
    fun borderWidthDisabled() = PaletteTheme.control.disabledBorderWidth

    @Composable
    fun durationFast() = PaletteTheme.motion.durationFast

    @Composable
    fun durationNormal() = PaletteTheme.motion.durationNormal

    @Composable
    fun durationSlow() = PaletteTheme.motion.durationSlow

    @Composable
    fun focusRingOffset() = PaletteTheme.control.focusRingOffset

    @Composable
    fun shadowBlur() = PaletteTheme.elevation.focusShadowBlur
}
