package xyz.junerver.compose.palette.components.inputnumber

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

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

internal fun nextInputNumberValue(
    value: Double?,
    direction: Int,
    min: Double,
    max: Double,
    step: Double,
    precision: Int,
): Double? {
    if (direction == 0) return value
    val current = value ?: when {
        direction > 0 -> if (min.isFinite()) min else 0.0
        direction < 0 -> if (max.isFinite()) max else 0.0
        else -> return value
    }
    val raw = current + step * direction
    val clamped = raw.coerceIn(min, max)
    val safePrecision = precision.coerceAtLeast(0)
    val factor = Math.pow(10.0, safePrecision.toDouble())
    return Math.round(clamped * factor) / factor
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
        onValueChange(nextInputNumberValue(value, direction, min, max, step, precision))
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
    val shape = RoundedCornerShape(size.cornerRadius)
    val borderColor = status.borderColor()

    Row(
        modifier = modifier
            .defaultMinSize(minWidth = InputNumberDefaults.Width)
            .height(size.height)
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(PaletteTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InputNumberStepButton(
            modifier = Modifier
                .width(InputNumberDefaults.ButtonWidth)
                .fillMaxHeight(),
            disabled = minusDisabled,
            color = minusAnimColor,
            icon = { Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = minusIconColor, modifier = Modifier.size(size.iconSize)) },
            onClick = { stepValue(-1) },
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(borderColor)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = size.horizontalPadding),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (inputText.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = size.fontSize,
                    color = PaletteTheme.colors.hint,
                    style = PaletteTheme.typography.body,
                )
            }
            BasicTextField(
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
                enabled = !disabled,
                singleLine = true,
                textStyle = PaletteTheme.typography.body.copy(
                    fontSize = size.fontSize,
                    color = if (disabled) PaletteTheme.colors.onSurface.copy(alpha = 0.5f) else PaletteTheme.colors.onSurface,
                ),
                cursorBrush = SolidColor(PaletteTheme.colors.primary),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(borderColor)
        )

        InputNumberStepButton(
            modifier = Modifier
                .width(InputNumberDefaults.ButtonWidth)
                .fillMaxHeight(),
            disabled = plusDisabled,
            color = plusAnimColor,
            icon = { Icon(Icons.Default.Add, contentDescription = "Increase", tint = plusIconColor, modifier = Modifier.size(size.iconSize)) },
            onClick = { stepValue(1) },
        )
    }
}

@Composable
private fun InputNumberStepButton(
    modifier: Modifier,
    disabled: Boolean,
    color: androidx.compose.ui.graphics.Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = modifier
            .background(if (!disabled && isHovered) InputNumberDefaults.hoverButtonColor() else color)
            .clickable(
                enabled = !disabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}
