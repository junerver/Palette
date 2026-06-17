package xyz.junerver.compose.palette.components.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import xyz.junerver.compose.palette.components.text.PText
import kotlin.math.roundToInt

@Composable
fun PColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    showAlpha: Boolean = false,
    showHex: Boolean = true,
    presetColors: List<Color>? = null,
) {
    val hsv = remember(color) { colorToHsv(color) }
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }
    var alpha by remember { mutableFloatStateOf(color.alpha) }
    var hexInput by remember { mutableStateOf(colorToHex(color)) }

    LaunchedEffect(color) {
        val newHsv = colorToHsv(color)
        hue = newHsv[0]
        saturation = newHsv[1]
        value = newHsv[2]
        alpha = color.alpha.coerceIn(0f, 1f)
        hexInput = colorToHex(color)
    }

    fun updateColor() {
        onColorChange(hsvToColor(hue, saturation, value, alpha))
    }

    val shape = RoundedCornerShape(ColorPickerDefaults.cornerRadius())
    val thumbFillColor = ColorPickerDefaults.thumbFillColor()
    val thumbBorderColor = ColorPickerDefaults.thumbBorderColor()
    val thumbSize = ColorPickerDefaults.thumbSize()
    val thumbBorderWidth = ColorPickerDefaults.inputBorderWidth()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(ColorPickerDefaults.barSpacing())) {
        Canvas(
            modifier = Modifier
                .width(ColorPickerDefaults.panelWidth())
                .height(ColorPickerDefaults.panelHeight())
                .clip(shape)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        saturation = (offset.x / size.width).coerceIn(0f, 1f)
                        value = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                        updateColor()
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        saturation = (change.position.x / size.width).coerceIn(0f, 1f)
                        value = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                        updateColor()
                    }
                }
        ) {
            val safeHue = hue.coerceIn(0f, 360f)
            drawRect(Color.hsv(safeHue, 1f, 1f))
            drawRect(Brush.horizontalGradient(listOf(Color.White, Color.Transparent)))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))

            val thumbX = saturation * size.width
            val thumbY = (1f - value) * size.height
            drawCircle(thumbFillColor, thumbSize.toPx() / 2, Offset(thumbX, thumbY))
            drawCircle(thumbBorderColor, thumbSize.toPx() / 2 + thumbBorderWidth.toPx(), Offset(thumbX, thumbY))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(ColorPickerDefaults.hueBarHeight())
                .clip(shape)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        hue = hueFromPosition(offset.x, size.width.toFloat())
                        updateColor()
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        hue = hueFromPosition(change.position.x, size.width.toFloat())
                        updateColor()
                    }
                }
        ) {
            val hueColors = (0..360 step 30).map { Color.hsv(it.toFloat(), 1f, 1f) }
            drawRect(Brush.horizontalGradient(hueColors))

            val thumbX = (hue.coerceIn(0f, 360f) / 360f) * size.width
            drawCircle(thumbFillColor, thumbSize.toPx() / 2, Offset(thumbX, size.height / 2))
        }

        if (showAlpha) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ColorPickerDefaults.alphaBarHeight())
                    .clip(shape)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            alpha = (offset.x / size.width).coerceIn(0f, 1f)
                            updateColor()
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            alpha = (change.position.x / size.width).coerceIn(0f, 1f)
                            updateColor()
                        }
                    }
            ) {
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, hsvToColor(hue, saturation, value, 1f))))

                val thumbX = alpha * size.width
                drawCircle(thumbFillColor, thumbSize.toPx() / 2, Offset(thumbX, size.height / 2))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ColorPickerDefaults.presetColorSpacing()),
        ) {
            Box(
                modifier = Modifier
                    .size(ColorPickerDefaults.previewSize())
                    .clip(RoundedCornerShape(ColorPickerDefaults.inputCornerRadius()))
                    .background(hsvToColor(hue, saturation, value, alpha))
                    .border(
                        ColorPickerDefaults.inputBorderWidth(),
                        ColorPickerDefaults.inputBorderColor(),
                        RoundedCornerShape(ColorPickerDefaults.inputCornerRadius())
                    )
            )

            if (showHex) {
                BasicTextField(
                    value = hexInput,
                    onValueChange = { new ->
                        hexInput = new
                        parseHexColor(new)?.let { parsed ->
                            onColorChange(parsed)
                            val newHsv = colorToHsv(parsed)
                            hue = newHsv[0]
                            saturation = newHsv[1]
                            value = newHsv[2]
                            alpha = parsed.alpha
                        }
                    },
                    singleLine = true,
                    textStyle = ColorPickerDefaults.inputTextStyle().copy(color = ColorPickerDefaults.inputTextColor()),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    cursorBrush = SolidColor(ColorPickerDefaults.inputCursorColor()),
                    modifier = Modifier
                        .width(ColorPickerDefaults.hexInputWidth())
                        .height(ColorPickerDefaults.hexInputHeight())
                        .clip(RoundedCornerShape(ColorPickerDefaults.inputCornerRadius()))
                        .border(
                            ColorPickerDefaults.inputBorderWidth(),
                            ColorPickerDefaults.inputBorderColor(),
                            RoundedCornerShape(ColorPickerDefaults.inputCornerRadius())
                        )
                        .padding(
                            horizontal = ColorPickerDefaults.inputPaddingHorizontal(),
                            vertical = ColorPickerDefaults.inputPaddingVertical()
                        ),
                )
            }
        }

        if (presetColors != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ColorPickerDefaults.presetColorSpacing()),
            ) {
                presetColors.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .size(ColorPickerDefaults.presetColorSize())
                            .clip(CircleShape)
                            .background(preset)
                            .border(
                                width = if (preset == color) {
                                    ColorPickerDefaults.inputBorderWidth() * 2
                                } else {
                                    ColorPickerDefaults.inputBorderWidth()
                                },
                                color = if (preset == color) {
                                    ColorPickerDefaults.selectedBorderColor()
                                } else {
                                    ColorPickerDefaults.inputBorderColor()
                                },
                                shape = CircleShape,
                            )
                            .let { mod ->
                                mod.pointerInput(Unit) {
                                    detectTapGestures {
                                        onColorChange(preset)
                                        val newHsv = colorToHsv(preset)
                                        hue = newHsv[0]
                                        saturation = newHsv[1]
                                        value = newHsv[2]
                                        alpha = preset.alpha
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

private fun colorToHsv(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val hue = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * ((b - r) / delta + 2f)
        else -> 60f * ((r - g) / delta + 4f)
    }.let { if (it < 0) it + 360f else it }

    val saturation = if (max == 0f) 0f else delta / max
    return floatArrayOf(hue, saturation, max)
}

internal fun hueFromPosition(
    positionX: Float,
    width: Float,
): Float {
    if (width <= 0f) return 0f
    return ((positionX / width) * 360f).coerceIn(0f, 360f)
}

internal fun hsvToColor(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Color {
    val safeHue = hue.coerceIn(0f, 360f)
    val safeSaturation = saturation.coerceIn(0f, 1f)
    val safeValue = value.coerceIn(0f, 1f)
    val safeAlpha = alpha.coerceIn(0f, 1f)
    val c = safeValue * safeSaturation
    val x = c * (1 - kotlin.math.abs((safeHue / 60f) % 2 - 1))
    val m = safeValue - c

    val (r, g, b) = when {
        safeHue < 60f -> Triple(c, x, 0f)
        safeHue < 120f -> Triple(x, c, 0f)
        safeHue < 180f -> Triple(0f, c, x)
        safeHue < 240f -> Triple(0f, x, c)
        safeHue < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color((r + m).coerceIn(0f, 1f), (g + m).coerceIn(0f, 1f), (b + m).coerceIn(0f, 1f), safeAlpha)
}

private fun colorToHex(color: Color): String {
    val argb = color.toArgb()
    return "#%08X".format(argb).uppercase()
}

private fun parseHexColor(hex: String): Color? {
    val clean = hex.removePrefix("#")
    return try {
        when (clean.length) {
            6 -> {
                val rgb = clean.toLong(16)
                Color(((0xFF.toLong() shl 24) or rgb).toUInt().toInt())
            }
            8 -> {
                val argb = clean.toLong(16)
                Color(argb.toUInt().toInt())
            }
            3 -> {
                val r = clean[0].toString().repeat(2)
                val g = clean[1].toString().repeat(2)
                val b = clean[2].toString().repeat(2)
                val rgb = "$r$g$b".toLong(16)
                Color(((0xFF.toLong() shl 24) or rgb).toUInt().toInt())
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}
