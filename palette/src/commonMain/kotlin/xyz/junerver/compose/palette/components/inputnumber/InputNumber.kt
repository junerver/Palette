package xyz.junerver.compose.palette.components.inputnumber

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

private const val REPEAT_INITIAL_DELAY = 300L
private const val REPEAT_INTERVAL = 80L

private fun isValidNumberInput(text: String): Boolean {
    if (text.isEmpty()) return true
    if (text == "-" || text == ".") return true
    return text.matches(Regex("^-?\\d*\\.?\\d*$"))
}

private fun formatValue(value: Double, precision: Int): String {
    return if (precision == 0) {
        value.toLong().toString()
    } else {
        value.toBigDecimal().setScale(precision, java.math.RoundingMode.HALF_UP).toPlainString()
    }
}

@Composable
fun PInputNumber(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    min: Double = Double.NEGATIVE_INFINITY,
    max: Double = Double.POSITIVE_INFINITY,
    step: Double = 1.0,
    precision: Int = 0,
    placeholder: String = "",
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    status: ComponentStatus = ComponentStatus.Default,
) {
    val (inputText, setInputText) = useState("")
    val isAtMin = value != null && value <= min
    val isAtMax = value != null && value >= max
    val minusDisabled = disabled || isAtMin
    val plusDisabled = disabled || isAtMax

    LaunchedEffect(value, precision) {
        setInputText(if (value != null) formatValue(value, precision) else "")
    }

    fun stepValue(direction: Int) {
        if (disabled) return
        val current = value ?: when {
            direction > 0 -> if (min.isFinite()) min else 0.0
            direction < 0 -> if (max.isFinite()) max else 0.0
            else -> return
        }
        val raw = current + step * direction
        val clamped = raw.coerceIn(min, max)
        val factor = Math.pow(10.0, precision.toDouble())
        val rounded = Math.round(clamped * factor) / factor
        onValueChange(rounded)
    }

    val minusAnimColor by animateColorAsState(
        targetValue = if (minusDisabled) InputNumberDefaults.disabledButtonColor() else InputNumberDefaults.buttonColor(),
        animationSpec = tween(InputNumberDefaults.AnimationDuration)
    )
    val plusAnimColor by animateColorAsState(
        targetValue = if (plusDisabled) InputNumberDefaults.disabledButtonColor() else InputNumberDefaults.buttonColor(),
        animationSpec = tween(InputNumberDefaults.AnimationDuration)
    )
    val minusIconColor = if (minusDisabled) InputNumberDefaults.disabledButtonIconColor() else InputNumberDefaults.buttonIconColor()
    val plusIconColor = if (plusDisabled) InputNumberDefaults.disabledButtonIconColor() else InputNumberDefaults.buttonIconColor()
    val buttonShape = RoundedCornerShape(size.cornerRadius)

    Row(
        modifier = modifier.defaultMinSize(minWidth = InputNumberDefaults.Width),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-1).dp)
    ) {
        Row(
            modifier = Modifier
                .width(InputNumberDefaults.ButtonWidth)
                .height(size.height)
                .clip(buttonShape)
                .background(minusAnimColor)
                .border(1.dp, PaletteTheme.colors.border, buttonShape)
                .then(
                    if (minusDisabled) Modifier
                    else Modifier.pointerInput(Unit) {
                        coroutineScope {
                            launch {
                                while (true) {
                                    awaitPointerEventScope {
                                        val event = awaitPointerEvent()
                                        if (!event.changes.any { it.pressed }) return@awaitPointerEventScope
                                    }
                                    stepValue(-1)
                                    delay(REPEAT_INITIAL_DELAY)
                                    while (true) {
                                        awaitPointerEventScope {
                                            val event = awaitPointerEvent()
                                            val pressed = event.changes.any { it.pressed }
                                            if (!pressed) throw kotlinx.coroutines.CancellationException()
                                        }
                                        stepValue(-1)
                                        delay(REPEAT_INTERVAL)
                                    }
                                }
                            }
                        }
                    }
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = minusIconColor,
                modifier = Modifier.size(size.iconSize)
            )
        }

        BorderTextField(
            value = inputText,
            onValueChange = { new ->
                if (isValidNumberInput(new)) {
                    setInputText(new)
                    if (new.isEmpty()) {
                        onValueChange(null)
                    } else {
                        new.toDoubleOrNull()?.let { parsed ->
                            val clamped = parsed.coerceIn(min, max)
                            onValueChange(clamped)
                        }
                    }
                }
            },
            modifier = Modifier.weight(1f),
            enabled = !disabled,
            size = size,
            status = status,
            placeholder = placeholder,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Row(
            modifier = Modifier
                .width(InputNumberDefaults.ButtonWidth)
                .height(size.height)
                .clip(buttonShape)
                .background(plusAnimColor)
                .border(1.dp, PaletteTheme.colors.border, buttonShape)
                .then(
                    if (plusDisabled) Modifier
                    else Modifier.pointerInput(Unit) {
                        coroutineScope {
                            launch {
                                while (true) {
                                    awaitPointerEventScope {
                                        val event = awaitPointerEvent()
                                        if (!event.changes.any { it.pressed }) return@awaitPointerEventScope
                                    }
                                    stepValue(1)
                                    delay(REPEAT_INITIAL_DELAY)
                                    while (true) {
                                        awaitPointerEventScope {
                                            val event = awaitPointerEvent()
                                            val pressed = event.changes.any { it.pressed }
                                            if (!pressed) throw kotlinx.coroutines.CancellationException()
                                        }
                                        stepValue(1)
                                        delay(REPEAT_INTERVAL)
                                    }
                                }
                            }
                        }
                    }
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = plusIconColor,
                modifier = Modifier.size(size.iconSize)
            )
        }
    }
}
