package xyz.junerver.compose.palette.components.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens

@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    status: ComponentStatus = ComponentStatus.Default,
    placeholder: String = "",
    showCount: Boolean = false,
    maxLength: Int? = null,
    minLines: Int = 3,
    maxLines: Int = 6,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val borderColor by animateColorAsState(
        targetValue = TextFieldDefaults.borderColor(
            status = status,
            isFocused = isFocused,
            isHovered = isHovered,
            enabled = enabled
        ),
        animationSpec = androidx.compose.animation.core.tween(FormTokens.DurationNormal)
    )

    val shadowColor by animateColorAsState(
        targetValue = TextFieldDefaults.shadowColor(status, isFocused),
        animationSpec = androidx.compose.animation.core.tween(FormTokens.DurationNormal)
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) FormTokens.BorderWidthFocus else FormTokens.BorderWidthDefault,
        animationSpec = androidx.compose.animation.core.tween(FormTokens.DurationNormal)
    )

    val backgroundColor = TextFieldDefaults.backgroundColor(enabled)
    val shape = RoundedCornerShape(size.cornerRadius)
    val textColor = if (enabled) PaletteTheme.colors.onSurface else PaletteTheme.colors.onSurface.copy(alpha = 0.5f)

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isFocused) FormTokens.ShadowBlur else 0.dp,
                    shape = shape,
                    spotColor = shadowColor
                )
                .border(borderWidth, borderColor, shape)
                .clip(shape)
                .background(backgroundColor)
                .defaultMinSize(minHeight = size.height * minLines)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = size.horizontalPadding, vertical = size.verticalPadding)
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = size.fontSize,
                        color = PaletteTheme.colors.hint,
                        style = PaletteTheme.typography.body
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = PaletteTheme.typography.body.copy(
                        fontSize = size.fontSize,
                        color = textColor
                    ),
                    cursorBrush = SolidColor(PaletteTheme.colors.primary),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    interactionSource = interactionSource,
                    minLines = minLines,
                    maxLines = maxLines
                )
            }
        }

        if (showCount) {
            Text(
                text = "${value.length}${maxLength?.let { "/$it" } ?: ""}",
                fontSize = PaletteTheme.typography.label.fontSize,
                color = PaletteTheme.colors.onSurface.copy(alpha = 0.45f),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )
        }
    }
}
