package xyz.junerver.compose.palette.components.qrcode

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class PQRCodeRenderDecodeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun renderedPQRCodeDecodesToOriginalContent() {
        rule.setContent {
            PQRCode(
                value = QrValue,
                size = 256.dp,
                modifier = Modifier.testTag(QrTag),
            )
        }

        val image = rule.onNodeWithTag(QrTag).captureToImage()
        val pixelMap = image.toPixelMap()
        val pixels = IntArray(image.width * image.height) { index ->
            pixelMap[index % image.width, index / image.width].toArgb()
        }
        val source = RGBLuminanceSource(image.width, image.height, pixels)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val hints =
            mapOf(
                DecodeHintType.CHARACTER_SET to "UTF-8",
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
            )

        assertEquals(QrValue, MultiFormatReader().decode(bitmap, hints).text)
    }

    private companion object {
        const val QrTag = "qr"
        const val QrValue = "https://github.com/junerver/Palette"
    }
}
