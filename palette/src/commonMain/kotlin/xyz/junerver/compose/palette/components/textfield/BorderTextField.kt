package xyz.junerver.compose.palette.components.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun BorderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: BorderTextFieldColors = TextFieldDefaults.colors(),
    height: Dp = TextFieldDefaults.Height,
    width: Dp = TextFieldDefaults.Width,
    borderWidth: Dp = TextFieldDefaults.BorderWidth,
    cornerSize: Dp = TextFieldDefaults.CornerSize,
    fontSize: TextUnit = TextFieldDefaults.FontSize,
    hint: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(cornerSize)
    val showHint = value.isEmpty()
    val visualTransformation = if (isPassword && !passwordVisible) {
        PasswordVisualTransformation('*')
    } else {
        VisualTransformation.None
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .border(borderWidth, colors.borderColor, shape)
            .clip(shape)
            .background(colors.backgroundColor)
            .padding(8.dp)
            .width(width)
            .height(height)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.invoke()
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (showHint) {
                    Text(
                        text = hint,
                        fontSize = fontSize,
                        color = colors.hintColor,
                        style = PaletteTheme.typography.body,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = true,
                    textStyle = PaletteTheme.typography.body.copy(
                        fontSize = fontSize,
                        color = colors.textColor,
                    ),
                    cursorBrush = SolidColor(colors.textColor),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp, start = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    visualTransformation = visualTransformation
                )
            }
            trailingIcon?.invoke()
        }
    }
}

@Deprecated(
    message = "Use BorderTextField with leadingIcon/trailingIcon parameters",
    replaceWith = ReplaceWith(
        "BorderTextField(value, onValueChange, modifier, true, TextFieldDefaults.colors(textColor, hintColor, borderColor, backgroundColor), height, width, borderWidth, cornerSize, fontSize, hint, keyboardType, keyboardType == KeyboardType.Password, passTrans, icon, tailIcon)",
        "xyz.junerver.compose.palette.components.textfield.BorderTextField",
        "xyz.junerver.compose.palette.components.textfield.TextFieldDefaults"
    )
)
@Composable
fun BorderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable (() -> Unit)? = null,
    tailIcon: @Composable (() -> Unit)? = null,
    textColor: Color,
    fontSize: TextUnit = TextFieldDefaults.FontSize,
    height: Dp = TextFieldDefaults.Height,
    width: Dp = TextFieldDefaults.Width,
    borderWidth: Dp = TextFieldDefaults.BorderWidth,
    cornerSize: Dp = TextFieldDefaults.CornerSize,
    borderColor: Color,
    backgroundColor: Color,
    hint: String = "",
    hintColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    passTrans: Boolean = true,
) {
    BorderTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier,
        enabled = true,
        colors = TextFieldDefaults.colors(
            textColor = textColor,
            hintColor = hintColor,
            borderColor = borderColor,
            backgroundColor = backgroundColor,
        ),
        height = height,
        width = width,
        borderWidth = borderWidth,
        cornerSize = cornerSize,
        fontSize = fontSize,
        hint = hint,
        keyboardType = keyboardType,
        isPassword = keyboardType == KeyboardType.Password,
        passwordVisible = !passTrans,
        leadingIcon = icon,
        trailingIcon = tailIcon,
    )
}
