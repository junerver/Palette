package xyz.junerver.compose.palette.components.textfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class BorderTextFieldColors(
    val textColor: Color,
    val hintColor: Color,
    val borderColor: Color,
    val backgroundColor: Color,
)

object TextFieldDefaults {
    val Height: Dp = 28.dp
    val Width: Dp = 300.dp
    val BorderWidth: Dp = 0.5.dp
    val CornerSize: Dp = 5.dp
    val FontSize: TextUnit = 17.sp

    fun height(size: ComponentSize): Dp = when (size) {
        ComponentSize.Small -> 24.dp
        ComponentSize.Medium -> 28.dp
        ComponentSize.Large -> 36.dp
    }

    fun fontSize(size: ComponentSize): TextUnit = when (size) {
        ComponentSize.Small -> 14.sp
        ComponentSize.Medium -> 17.sp
        ComponentSize.Large -> 20.sp
    }

    @Composable
    fun colors(
        textColor: Color = PaletteTheme.colors.onSurface,
        hintColor: Color = PaletteTheme.colors.hint,
        borderColor: Color = PaletteTheme.colors.border,
        backgroundColor: Color = PaletteTheme.colors.surface,
    ): BorderTextFieldColors = BorderTextFieldColors(
        textColor = textColor,
        hintColor = hintColor,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
    )
}
