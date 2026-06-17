package xyz.junerver.compose.palette.components.barcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import io.github.alexzhirkevich.qrose.oned.BarcodeType
import io.github.alexzhirkevich.qrose.oned.rememberBarcodePainter

@Composable
fun PBarcode(
    value: String,
    modifier: Modifier = Modifier,
    type: PaletteBarcodeType = BarcodeDefaults.Type,
    width: Dp = BarcodeDefaults.width(),
    height: Dp = BarcodeDefaults.height(),
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
) {
    val resolvedColor = if (color == Color.Unspecified) BarcodeDefaults.color() else color
    val resolvedBg = if (backgroundColor == Color.Unspecified) BarcodeDefaults.backgroundColor() else backgroundColor
    val painter =
        rememberBarcodePainter(
            data = value,
            type = type.toQroseBarcodeType(),
            brush = SolidColor(resolvedColor),
        )

    Box(
        modifier =
            modifier
                .size(width = width, height = height)
                .then(
                    if (resolvedBg == Color.Transparent) {
                        Modifier
                    } else {
                        Modifier.background(resolvedBg)
                    },
                ),
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

enum class PaletteBarcodeType {
    Codabar,
    Code39,
    Code93,
    Code128,
    EAN8,
    EAN13,
    ITF,
    UPCA,
    UPCE,
}

internal fun PaletteBarcodeType.toQroseBarcodeType(): BarcodeType =
    when (this) {
        PaletteBarcodeType.Codabar -> BarcodeType.Codabar
        PaletteBarcodeType.Code39 -> BarcodeType.Code39
        PaletteBarcodeType.Code93 -> BarcodeType.Code93
        PaletteBarcodeType.Code128 -> BarcodeType.Code128
        PaletteBarcodeType.EAN8 -> BarcodeType.EAN8
        PaletteBarcodeType.EAN13 -> BarcodeType.EAN13
        PaletteBarcodeType.ITF -> BarcodeType.ITF
        PaletteBarcodeType.UPCA -> BarcodeType.UPCA
        PaletteBarcodeType.UPCE -> BarcodeType.UPCE
    }
