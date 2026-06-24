package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun ColoredCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ComponentSize = ComponentSize.Medium,
    colors: CheckboxColors = CheckboxDefaults.colors(),
) {
    val interactionSource = remember(checked) { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val sizeTokens = CheckboxDefaults.sizeTokens(size)
    val checkboxSize = sizeTokens.size
    val motionDuration = CheckboxDefaults.motionDuration()
    val hoverBackgroundAlpha = CheckboxDefaults.hoverBackgroundAlpha()
    val disabledBorderAlpha = CheckboxDefaults.disabledBorderAlpha()

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.disabledColor
            checked -> colors.checkedColor
            isFocused -> colors.focusColor
            isHovered -> colors.hoverColor
            else -> colors.uncheckedColor
        },
        animationSpec = tween(motionDuration)
    )

    val fillColor by animateColorAsState(
        targetValue = if (checked && enabled) colors.checkedColor else Color.Transparent,
        animationSpec = tween(motionDuration)
    )

    val checkmarkAlpha by animateColorAsState(
        targetValue = if (checked) colors.checkmarkColor else Color.Transparent,
        animationSpec = tween(motionDuration)
    )

    val focusRingAlpha = if (isFocused) CheckboxDefaults.focusRingAlpha() else 0f

    Box(
        modifier = modifier
            .size(checkboxSize + CheckboxDefaults.touchPadding(size))
            .then(
                if (enabled && onCheckedChange != null) {
                    Modifier.clickableWithoutRipple { onCheckedChange(!checked) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val drawSize = checkboxSize
        val strokeWidth = sizeTokens.strokeWidth
        val cornerRadius = sizeTokens.cornerRadius
        val focusRingOffset = sizeTokens.focusRingOffset

        Box(
            modifier = Modifier
                .size(checkboxSize)
                .drawBehind {
                    val canvasSize = drawSize.toPx()
                    val strokeW = strokeWidth.toPx()
                    val cornerR = cornerRadius.toPx()
                    val focusOff = focusRingOffset.toPx()

                    // Focus ring
                    if (isFocused) {
                        drawRoundRect(
                            color = borderColor.copy(alpha = focusRingAlpha),
                            topLeft = Offset(-focusOff, -focusOff),
                            size = Size(canvasSize + focusOff * 2, canvasSize + focusOff * 2),
                            cornerRadius = CornerRadius(cornerR + focusOff)
                        )
                    }

                    // Hover background
                    if (isHovered && !checked) {
                        drawRoundRect(
                            color = borderColor.copy(alpha = hoverBackgroundAlpha),
                            size = Size(canvasSize, canvasSize),
                            cornerRadius = CornerRadius(cornerR)
                        )
                    }

                    // Checkbox fill
                    drawRoundRect(
                        color = fillColor,
                        size = Size(canvasSize, canvasSize),
                        cornerRadius = CornerRadius(cornerR)
                    )

                    // Checkbox border
                    drawRoundRect(
                        color = borderColor.copy(alpha = if (enabled) 1f else disabledBorderAlpha),
                        size = Size(canvasSize, canvasSize),
                        cornerRadius = CornerRadius(cornerR),
                        style = Stroke(width = strokeW)
                    )

                    // Checkmark
                    if (checked) {
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
                            color = checkmarkAlpha,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                }
        )
    }
}
