package xyz.junerver.compose.palette.components.inputotp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PInputOTP(
    length: Int = 6,
    value: String = "",
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    separator: (@Composable ((index: Int) -> Unit))? = null,
    masked: Boolean = false,
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!disabled) {
            focusRequester.requestFocus()
        }
    }

    val disabledAlpha by animateFloatAsState(
        targetValue = if (disabled) InputOTPDefaults.DisabledAlpha else 1f,
        animationSpec = tween(100)
    )

    val shape = RoundedCornerShape(InputOTPDefaults.CellCornerRadius)

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InputOTPDefaults.CellSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until length) {
                val char = value.getOrNull(i)?.toString() ?: ""
                val displayChar = if (masked && char.isNotEmpty()) InputOTPDefaults.MaskChar else char
                val cellFocused = isFocused && i == value.length.coerceAtMost(length - 1)

                val borderColor = InputOTPDefaults.cellBorderColor(isFocused = cellFocused)

                Box(
                    modifier = Modifier
                        .width(InputOTPDefaults.CellWidth)
                        .height(InputOTPDefaults.CellHeight)
                        .alpha(disabledAlpha)
                        .border(InputOTPDefaults.CellBorderWidth, borderColor, shape)
                        .clip(shape)
                        .background(InputOTPDefaults.cellBackgroundColor())
                        .then(
                            if (!disabled) Modifier.clickable {
                                focusRequester.requestFocus()
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayChar.isNotEmpty()) {
                        PText(
                            text = displayChar,
                            color = InputOTPDefaults.textColor(),
                            fontSize = InputOTPDefaults.FontSize,
                            textAlign = TextAlign.Center,
                            style = TextStyle(textAlign = TextAlign.Center)
                        )
                    } else if (cellFocused) {
                        Box(
                            modifier = Modifier
                                .size(2.dp, InputOTPDefaults.FontSize.value.dp)
                                .background(InputOTPDefaults.cursorColor())
                        )
                    }
                }

                if (separator != null && i < length - 1) {
                    separator(i)
                }
            }
        }

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }
                onValueChange(filtered.take(length))
            },
            enabled = !disabled,
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                        if (value.isNotEmpty()) {
                            onValueChange(value.dropLast(1))
                        }
                        true
                    } else {
                        false
                    }
                }
                .focusable()
        )
    }
}
