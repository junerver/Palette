package xyz.junerver.compose.palette.components.radio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

data class RadioOption<T>(
    val label: String,
    val value: T,
    val description: String? = null,
    val disabled: Boolean = false
)

@Composable
fun PRadio(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    disabled: Boolean = false,
    labelColor: Color = RadioDefaults.labelColor(),
    descriptionColor: Color = RadioDefaults.descriptionColor(),
    checkedColor: Color = RadioDefaults.checkedColor(),
    uncheckedColor: Color = RadioDefaults.uncheckedColor()
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(RadioDefaults.BorderRadius))
            .clickableWithoutRipple(!disabled) {
                onClick()
            }
            .padding(RadioDefaults.Padding)
            .alpha(if (disabled) RadioDefaults.DisabledAlpha else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = labelColor,
                fontSize = RadioDefaults.LabelFontSize
            )
            description?.let {
                Spacer(modifier = Modifier.height(RadioDefaults.DescriptionSpacing))
                Text(
                    text = it,
                    color = descriptionColor,
                    fontSize = RadioDefaults.DescriptionFontSize
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(RadioDefaults.IconSize),
            tint = if (checked) checkedColor else uncheckedColor
        )
    }
}

@Composable
fun <T> PRadioGroup(
    options: List<RadioOption<T>>,
    modifier: Modifier = Modifier,
    value: T? = null,
    disabled: Boolean = false,
    onChange: ((value: T) -> Unit)? = null
) {
    Column(modifier = modifier) {
        for (option in options) {
            PRadio(
                label = option.label,
                description = option.description,
                checked = option.value == value,
                disabled = disabled || option.disabled,
                onClick = {
                    onChange?.invoke(option.value)
                }
            )
        }
    }
}