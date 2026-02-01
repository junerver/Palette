package xyz.junerver.compose.palette.components.textfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.util.PaletteDefaults

object TextFieldDefaults {
    val Height: Dp = 28.dp
    val Width: Dp = 300.dp
    val BorderWidth: Dp = 0.5.dp
    val CornerSize: Dp = 5.dp
    val FontSize: TextUnit = 17.sp

    @Composable
    fun colors(
        textColor: Color = PaletteDefaults.colors.onSurface,
        hintColor: Color = PaletteDefaults.colors.hint,
        borderColor: Color = PaletteDefaults.colors.border,
        backgroundColor: Color = PaletteDefaults.colors.surface,
    ): BorderTextFieldColors = BorderTextFieldColors(
        textColor = textColor,
        hintColor = hintColor,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
    )
}
