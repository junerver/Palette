package xyz.junerver.compose.palette.components.textfield

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun BorderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    status: ComponentStatus = ComponentStatus.Default,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: String? = null,
    suffix: String? = null,
    clearable: Boolean = false,
    showCount: Boolean = false,
    maxLength: Int? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val fieldTokens = PaletteTheme.componentThemes.textField
    val sizeTokens = TextFieldDefaults.sizeTokens(size)

    val borderColor by animateColorAsState(
        targetValue = TextFieldDefaults.borderColor(
            status = status,
            isFocused = isFocused,
            isHovered = isHovered,
            enabled = enabled
        ),
        animationSpec = androidx.compose.animation.core.tween(TextFieldDefaults.motionDuration())
    )

    val shadowColor by animateColorAsState(
        targetValue = TextFieldDefaults.shadowColor(status, isFocused),
        animationSpec = androidx.compose.animation.core.tween(TextFieldDefaults.motionDuration())
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) TextFieldDefaults.focusBorderWidth() else TextFieldDefaults.borderWidth(),
        animationSpec = androidx.compose.animation.core.tween(TextFieldDefaults.motionDuration())
    )

    val backgroundColor = TextFieldDefaults.backgroundColor(enabled)
    val shape = RoundedCornerShape(sizeTokens.cornerRadius)
    val textColor = if (enabled) fieldTokens.textColor else fieldTokens.disabledTextColor

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isFocused) TextFieldDefaults.shadowElevation() else 0.dp,
                    shape = shape,
                    spotColor = shadowColor
                )
                .border(borderWidth, borderColor, shape)
                .clip(shape)
                .background(backgroundColor)
                .height(sizeTokens.height)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = sizeTokens.horizontalPadding, vertical = sizeTokens.verticalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (prefix != null) {
                    Text(
                        text = prefix,
                        fontSize = sizeTokens.fontSize,
                        color = fieldTokens.prefixSuffixColor,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                leadingIcon?.let {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        it()
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = sizeTokens.fontSize,
                            color = fieldTokens.placeholderColor,
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
                        singleLine = singleLine,
                        textStyle = PaletteTheme.typography.body.copy(
                            fontSize = sizeTokens.fontSize,
                            color = textColor
                        ),
                        cursorBrush = SolidColor(fieldTokens.cursorColor),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource
                    )
                }

                AnimatedVisibility(visible = clearable && value.isNotEmpty() && enabled) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(sizeTokens.iconSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = fieldTokens.prefixSuffixColor,
                            modifier = Modifier.size(sizeTokens.iconSize - 4.dp)
                        )
                    }
                }

                trailingIcon?.let {
                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        it()
                    }
                }

                if (suffix != null) {
                    Text(
                        text = suffix,
                        fontSize = sizeTokens.fontSize,
                        color = fieldTokens.prefixSuffixColor,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        if (showCount) {
            Text(
                text = "${value.length}${maxLength?.let { "/$it" } ?: ""}",
                fontSize = PaletteTheme.typography.label.fontSize,
                color = fieldTokens.countColor,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )
        }
    }
}
