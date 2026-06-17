package xyz.junerver.compose.palette.components.barcode

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BarcodeDefaults {
    val Width: Dp = 180.dp
    val Height: Dp = 80.dp
    val Type: PaletteBarcodeType = PaletteBarcodeType.Code128

    @Composable
    fun width(): Dp = PaletteTheme.componentThemes.utility.barcodeWidth

    @Composable
    fun height(): Dp = PaletteTheme.componentThemes.utility.barcodeHeight

    @Composable
    fun color(): Color = PaletteTheme.componentThemes.utility.barcodeColor

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.utility.barcodeBackgroundColor
}
