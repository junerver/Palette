package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens
import xyz.junerver.compose.palette.core.tokens.focusBorder
import xyz.junerver.compose.palette.core.tokens.hoverBorder

@Composable
fun ColoredCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ComponentSize = ComponentSize.Medium,
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val checkboxSize = when (size) {
        ComponentSize.Small -> 14.dp
        ComponentSize.Medium -> 16.dp
        ComponentSize.Large -> 20.dp
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledColor
            checked -> colors.checkedColor
            isFocused -> colors.focusColor
            isHovered -> colors.hoverColor
            else -> colors.uncheckedColor
        },
        animationSpec = tween(FormTokens.DurationNormal)
    )

    val fillColor by animateColorAsState(
        targetValue = if (checked && enabled) colors.checkedColor else Color.Transparent,
        animationSpec = tween(FormTokens.DurationNormal)
    )

    val checkmarkAlpha by animateColorAsState(
        targetValue = if (checked) colors.checkmarkColor else Color.Transparent,
        animationSpec = tween(FormTokens.DurationNormal)
    )

    val focusRingAlpha = if (isFocused) 0.3f else 0f

    Box(
        modifier = modifier
            .size(checkboxSize + 8.dp)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = { onCheckedChange?.invoke(it) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(checkboxSize)) {
            val canvasSize = checkboxSize.toPx()
            val strokeWidth = 2.dp.toPx()
            val cornerRadius = 2.dp.toPx()

            // Focus ring
            if (isFocused) {
                drawRoundRect(
                    color = borderColor.copy(alpha = focusRingAlpha),
                    topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = Size(canvasSize + 4.dp.toPx(), canvasSize + 4.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius + 2.dp.toPx())
                )
            }

            // Hover background
            if (isHovered && !checked) {
                drawRoundRect(
                    color = borderColor.copy(alpha = 0.1f),
                    size = Size(canvasSize, canvasSize),
                    cornerRadius = CornerRadius(cornerRadius)
                )
            }

            // Checkbox fill
            drawRoundRect(
                color = fillColor,
                size = Size(canvasSize, canvasSize),
                cornerRadius = CornerRadius(cornerRadius)
            )

            // Checkbox border
            drawRoundRect(
                color = borderColor.copy(alpha = if (enabled) 1f else 0.5f),
                size = Size(canvasSize, canvasSize),
                cornerRadius = CornerRadius(cornerRadius),
                style = Stroke(width = strokeWidth)
            )

            // Checkmark
            if (checked) {
                drawCheckmark(checkmarkAlpha, canvasSize)
            }
        }
    }
}

private fun DrawScope.drawCheckmark(color: Color, canvasSize: Float) {
    val checkPath = Path().apply {
        val checkWidth = canvasSize * 0.7f
        val checkHeight = canvasSize * 0.5f
        val startX = canvasSize * 0.15f
        val startY = canvasSize * 0.5f

        moveTo(startX, startY)
        lineTo(startX + checkWidth * 0.35f, startY + checkHeight * 0.5f)
        lineTo(startX + checkWidth, startY - checkHeight * 0.3f)
    }

    drawPath(
        path = checkPath,
        color = color,
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}
