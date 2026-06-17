package xyz.junerver.compose.palette.components.code

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.oned.BarcodePainter
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.toByteArray
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import xyz.junerver.compose.palette.components.barcode.PaletteBarcodeType
import xyz.junerver.compose.palette.components.barcode.toQroseBarcodeType
import xyz.junerver.compose.palette.components.qrcode.toQrErrorCorrectionLevel

class QroseDecodeTest {

    @Test
    fun qroseQrCodesDecodeToOriginalContent() {
        val values =
            listOf(
                "Hello",
                "https://github.com/junerver/Palette",
                "Palette QRCode 1234567890",
                "二维码测试",
            )

        for (value in values) {
            for (errorCorrectionLevel in 0..3) {
                val painter =
                    QrCodePainter(
                        data = value,
                        options =
                            QrOptions(
                                colors =
                                    QrColors(
                                        dark = QrBrush.solid(Color.Black),
                                        light = QrBrush.solid(Color.White),
                                    ),
                                errorCorrectionLevel = errorCorrectionLevel.toQrErrorCorrectionLevel(),
                            ),
                    )

                assertEquals(
                    value,
                    decodeQr(painter.toByteArray(QrRenderSize, QrRenderSize, ImageFormat.PNG)),
                    "Decoded QR content should match for value=$value, ecLevel=$errorCorrectionLevel",
                )
            }
        }
    }

    @Test
    fun qroseOneDimensionalBarcodesDecodeToOriginalContent() {
        val cases =
            listOf(
                BarcodeCase("PALETTE-2026", PaletteBarcodeType.Code128, BarcodeFormat.CODE_128, "PALETTE-2026"),
                BarcodeCase("978020137962", PaletteBarcodeType.EAN13, BarcodeFormat.EAN_13, "9780201379624"),
            )

        for (case in cases) {
            val painter =
                BarcodePainter(
                    data = case.value,
                    type = case.type.toQroseBarcodeType(),
                    brush = SolidColor(Color.Black),
                )

            assertEquals(
                case.expectedDecodedValue,
                decodeBarcode(
                    imageBytes = painter.toByteArray(BarcodeRenderWidth, BarcodeRenderHeight, ImageFormat.PNG),
                    format = case.format,
                ),
                "Decoded barcode content should match for type=${case.type}",
            )
        }
    }

    private fun decodeQr(imageBytes: ByteArray): String =
        decode(
            imageBytes = imageBytes,
            quietZone = QrQuietZone,
            hints =
                mapOf(
                    DecodeHintType.CHARACTER_SET to "UTF-8",
                    DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                ),
        )

    private fun decodeBarcode(
        imageBytes: ByteArray,
        format: BarcodeFormat,
    ): String =
        decode(
            imageBytes = imageBytes,
            quietZone = BarcodeQuietZone,
            hints =
                mapOf(
                    DecodeHintType.TRY_HARDER to true,
                    DecodeHintType.POSSIBLE_FORMATS to listOf(format),
                ),
        )

    private fun decode(
        imageBytes: ByteArray,
        quietZone: Int,
        hints: Map<DecodeHintType, Any>,
    ): String {
        val image = withQuietZone(ImageIO.read(ByteArrayInputStream(imageBytes)), quietZone)
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

        val source = RGBLuminanceSource(image.width, image.height, pixels)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        return MultiFormatReader().decode(bitmap, hints).text
    }

    private fun withQuietZone(
        source: BufferedImage,
        quietZone: Int,
    ): BufferedImage {
        val image =
            BufferedImage(
                source.width + quietZone * 2,
                source.height + quietZone * 2,
                BufferedImage.TYPE_INT_RGB,
            )
        val graphics = image.createGraphics()
        try {
            graphics.color = java.awt.Color.WHITE
            graphics.fillRect(0, 0, image.width, image.height)
            graphics.drawImage(source, quietZone, quietZone, null)
        } finally {
            graphics.dispose()
        }
        return image
    }

    private data class BarcodeCase(
        val value: String,
        val type: PaletteBarcodeType,
        val format: BarcodeFormat,
        val expectedDecodedValue: String,
    )

    private companion object {
        const val QrRenderSize = 256
        const val QrQuietZone = 32
        const val BarcodeRenderWidth = 420
        const val BarcodeRenderHeight = 120
        const val BarcodeQuietZone = 24
    }
}
