package xyz.junerver.compose.palette.components.qrcode

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object QRCodeDefaults {
    val Size: Dp = 120.dp
    val QuietZone: Int = 4
    val ErrorCorrectionLevel: Int = 1
    internal const val IntrinsicModuleScale: Float = 10f

    @Composable
    fun size(): Dp = PaletteTheme.componentThemes.utility.qrCodeSize

    @Composable
    fun color(): Color = PaletteTheme.componentThemes.utility.qrCodeColor

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.utility.qrCodeBackgroundColor
}
