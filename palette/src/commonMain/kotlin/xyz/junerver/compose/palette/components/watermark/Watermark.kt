package xyz.junerver.compose.palette.components.watermark

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun PWatermark(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = WatermarkDefaults.fontSize(),
    color: Color = WatermarkDefaults.color(),
    rotate: Float = WatermarkDefaults.rotate(),
    gapX: Dp = WatermarkDefaults.gapX(),
    gapY: Dp = WatermarkDefaults.gapY(),
    content: @Composable () -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = WatermarkDefaults.textStyle().copy(fontSize = fontSize, color = color)

    Box(modifier = modifier) {
        content()
        Canvas(modifier = Modifier.fillMaxSize()) {
            val textLayoutResult = textMeasurer.measure(text, textStyle)
            val textWidth = textLayoutResult.size.width
            val textHeight = textLayoutResult.size.height
            val gapXPx = gapX.toPx()
            val gapYPx = gapY.toPx()
            val stepX = textWidth + gapXPx
            val stepY = textHeight + gapYPx
            val areaWidth = size.width
            val areaHeight = size.height
            val cols = ((areaWidth / stepX).toInt() + 2)
            val rows = ((areaHeight / stepY).toInt() + 2)

            for (row in -1..rows) {
                for (col in -1..cols) {
                    val offsetX = col * stepX
                    val offsetY = row * stepY
                    rotate(rotate, pivot = Offset(offsetX + textWidth / 2f, offsetY + textHeight / 2f)) {
                        drawText(textLayoutResult, topLeft = Offset(offsetX, offsetY))
                    }
                }
            }
        }
    }
}
