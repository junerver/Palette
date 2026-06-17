package xyz.junerver.compose.palette.components.barcode

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object BarcodeDefaults {
    val Width: Dp = 180.dp
    val Height: Dp = 80.dp
    val Type: PaletteBarcodeType = PaletteBarcodeType.Code128

    @Composable
    fun color(): Color = Color.Black

    @Composable
    fun backgroundColor(): Color = Color.White
}
