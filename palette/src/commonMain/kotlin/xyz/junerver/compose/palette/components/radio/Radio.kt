package xyz.junerver.compose.palette.components.radio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens
import xyz.junerver.compose.palette.core.tokens.focusBorder
import xyz.junerver.compose.palette.core.tokens.hoverBorder
import xyz.junerver.compose.palette.core.tokens.disabledBorder

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
    size: ComponentSize = ComponentSize.Medium,
    labelColor: Color = RadioDefaults.labelColor(),
    descriptionColor: Color = RadioDefaults.descriptionColor(),
    checkedColor: Color = RadioDefaults.checkedColor(),
    uncheckedColor: Color = RadioDefaults.uncheckedColor()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val radioSize = when (size) {
        ComponentSize.Small -> 14.dp
        ComponentSize.Medium -> 16.dp
        ComponentSize.Large -> 20.dp
    }

    val innerCircleSize = when (size) {
        ComponentSize.Small -> 6.dp
        ComponentSize.Medium -> 8.dp
        ComponentSize.Large -> 10.dp
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            !disabled && checked -> checkedColor
            !disabled && isFocused -> PaletteTheme.colors.focusBorder
            !disabled && isHovered -> PaletteTheme.colors.hoverBorder
            disabled -> PaletteTheme.colors.disabledBorder
            else -> uncheckedColor
        },
        animationSpec = tween(FormTokens.DurationNormal)
    )

    val innerCircleAlpha by animateDpAsState(
        targetValue = if (checked) innerCircleSize else 0.dp,
        animationSpec = tween(FormTokens.DurationNormal)
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(RadioDefaults.BorderRadius))
            .selectable(
                selected = checked,
                enabled = !disabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
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

        Box(
            modifier = Modifier.size(radioSize + 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(radioSize)) {
                val canvasSize = radioSize.toPx()
                val strokeWidth = 2.dp.toPx()
                val radius = canvasSize / 2f
                val center = Offset(radius, radius)

                // Focus ring
                if (isFocused) {
                    drawCircle(
                        color = borderColor.copy(alpha = 0.3f),
                        radius = radius + 2.dp.toPx(),
                        center = center
                    )
                }

                // Hover background
                if (isHovered && !checked) {
                    drawCircle(
                        color = borderColor.copy(alpha = 0.1f),
                        radius = radius,
                        center = center
                    )
                }

                // Outer circle border
                drawCircle(
                    color = borderColor,
                    radius = radius - strokeWidth / 2,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Inner filled circle (when selected)
                if (checked) {
                    drawCircle(
                        color = checkedColor,
                        radius = innerCircleAlpha.toPx() / 2f,
                        center = center
                    )
                }
            }
        }
    }
}

@Composable
fun <T> PRadioGroup(
    options: List<RadioOption<T>>,
    modifier: Modifier = Modifier,
    value: T? = null,
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    onChange: ((value: T) -> Unit)? = null
) {
    Column(modifier = modifier) {
        for (option in options) {
            PRadio(
                label = option.label,
                description = option.description,
                checked = option.value == value,
                disabled = disabled || option.disabled,
                size = size,
                onClick = {
                    onChange?.invoke(option.value)
                }
            )
        }
    }
}
