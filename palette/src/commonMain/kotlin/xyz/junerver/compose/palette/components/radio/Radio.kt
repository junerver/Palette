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
    val sizeTokens = RadioDefaults.sizeTokens(size)
    val radioSize = sizeTokens.outerSize
    val innerCircleSize = sizeTokens.innerSize
    val motionDuration = RadioDefaults.motionDuration()
    val focusRingAlpha = RadioDefaults.focusRingAlpha()
    val hoverBackgroundAlpha = RadioDefaults.hoverBackgroundAlpha()

    val borderColor by animateColorAsState(
        targetValue = when {
            !disabled && checked -> checkedColor
            !disabled && isFocused -> RadioDefaults.focusColor()
            !disabled && isHovered -> RadioDefaults.hoverColor()
            disabled -> RadioDefaults.disabledColor()
            else -> uncheckedColor
        },
        animationSpec = tween(motionDuration)
    )

    val innerCircleAlpha by animateDpAsState(
        targetValue = if (checked) innerCircleSize else 0.dp,
        animationSpec = tween(motionDuration)
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(RadioDefaults.borderRadius()))
            .selectable(
                selected = checked,
                enabled = !disabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(RadioDefaults.padding())
            .alpha(if (disabled) RadioDefaults.disabledAlpha() else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = labelColor,
                style = RadioDefaults.labelTextStyle()
            )
            description?.let {
                Spacer(modifier = Modifier.height(RadioDefaults.descriptionSpacing()))
                Text(
                    text = it,
                    color = descriptionColor,
                    style = RadioDefaults.descriptionTextStyle()
                )
            }
        }

        Box(
            modifier = Modifier.size(radioSize + sizeTokens.touchPadding),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(radioSize)) {
                val canvasSize = radioSize.toPx()
                val strokeWidth = sizeTokens.strokeWidth.toPx()
                val radius = canvasSize / 2f
                val center = Offset(radius, radius)

                // Focus ring
                if (isFocused) {
                    drawCircle(
                        color = borderColor.copy(alpha = focusRingAlpha),
                        radius = radius + sizeTokens.focusRingOffset.toPx(),
                        center = center
                    )
                }

                // Hover background
                if (isHovered && !checked) {
                    drawCircle(
                        color = borderColor.copy(alpha = hoverBackgroundAlpha),
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
