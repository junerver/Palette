package com.edusoa.android.palette

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.hooks.ext.toColor
import xyz.junerver.compose.hooks.useState

/**
 * Description:
 *
 * @author Junerver date: 2024/3/27-14:47 Email: junerver@gmail.com
 *     Version: v1.0
 */
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ContentBorder(
    height: Dp = 28.dp,
    width: Dp = 300.dp,
    borderWidth: Dp = 0.5.dp,
    cornerSize: Dp = 5.dp,
    borderColor: Color = "#D9D9D9".toColor(),
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(borderWidth, borderColor, shape = RoundedCornerShape(cornerSize))
            .clip(RoundedCornerShape(cornerSize))
            .background(color = backgroundColor)
            .padding(8.dp)
            .width(width)
            .height(height)
    ) {
        content()
    }
}

@Composable
fun BorderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable (() -> Unit)? = null,
    tailIcon: @Composable (() -> Unit)? = null,
    textColor: Color = Color.Black,
    fontSize: TextUnit = 17.sp,
    height: Dp = 28.dp,
    width: Dp = 300.dp,
    borderWidth: Dp = 0.5.dp,
    cornerSize: Dp = 5.dp,
    borderColor: Color = "#D9D9D9".toColor(),
    backgroundColor: Color = Color.White,
    hint: String = "",
    hintColor: Color = "#bfbfbf".toColor(),
    keyboardType: KeyboardType = KeyboardType.Text,
    passTrans: Boolean = true,
) {
    ContentBorder(
        height = height,
        width = width,
        borderWidth = borderWidth,
        cornerSize = cornerSize,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
    ) {
        Row {
            icon?.invoke()
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                val showHint by useState(value) {
                    value.isEmpty()
                }
                if (showHint) {
                    Text(
                        hint,
                        fontSize = fontSize,
                        color = hintColor,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                BasicTextField(
                    singleLine = true,
                    textStyle = TextStyle.Default.copy(
                        fontSize = fontSize,
                        color = textColor,

                    ),
                    cursorBrush = SolidColor(textColor),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp, start = 10.dp),
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = keyboardType
                    ),
                    visualTransformation = if (keyboardType == KeyboardType.Password && passTrans) PasswordVisualTransformation(
                        '*'
                    ) else VisualTransformation.None
                )
            }
            tailIcon?.invoke()
        }
    }
}
