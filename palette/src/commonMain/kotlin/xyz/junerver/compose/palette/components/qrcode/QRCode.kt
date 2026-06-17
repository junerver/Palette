package xyz.junerver.compose.palette.components.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlin.math.roundToInt

@Composable
fun PQRCode(
    value: String,
    modifier: Modifier = Modifier,
    size: Dp = QRCodeDefaults.size(),
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    errorCorrectionLevel: Int = QRCodeDefaults.ErrorCorrectionLevel,
) {
    val resolvedColor = if (color == Color.Unspecified) QRCodeDefaults.color() else color
    val resolvedBg = if (backgroundColor == Color.Unspecified) QRCodeDefaults.backgroundColor() else backgroundColor
    val options =
        remember(resolvedColor, errorCorrectionLevel) {
            QrOptions(
                colors =
                    QrColors(
                        dark = QrBrush.solid(resolvedColor),
                        light = QrBrush.solid(Color.Transparent),
                    ),
                errorCorrectionLevel = errorCorrectionLevel.toQrErrorCorrectionLevel(),
            )
        }
    val painter = rememberQrCodePainter(data = value, options = options)
    val moduleCount = (painter.intrinsicSize.width / QRCodeDefaults.IntrinsicModuleScale)
        .roundToInt()
        .coerceAtLeast(1)
    val quietZone =
        size * (QRCodeDefaults.QuietZone.toFloat() / (moduleCount + QRCodeDefaults.QuietZone * 2))

    Box(
        modifier =
            modifier
                .size(size)
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(quietZone),
        )
    }
}

internal fun Int.toQrErrorCorrectionLevel(): QrErrorCorrectionLevel =
    when (coerceIn(0, 3)) {
        0 -> QrErrorCorrectionLevel.Low
        1 -> QrErrorCorrectionLevel.Medium
        2 -> QrErrorCorrectionLevel.MediumHigh
        else -> QrErrorCorrectionLevel.High
    }
