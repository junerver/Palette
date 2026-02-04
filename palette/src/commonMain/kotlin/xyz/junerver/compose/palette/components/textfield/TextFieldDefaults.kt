package xyz.junerver.compose.palette.components.textfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.*

@Immutable
data class BorderTextFieldColors(
    val textColor: Color,
    val hintColor: Color,
    val borderColor: Color,
    val backgroundColor: Color,
)

object TextFieldDefaults {
    @Deprecated("Use ComponentSize.Medium.height instead")
    val Height: Dp = 28.dp
    
    @Deprecated("Use Modifier.fillMaxWidth() instead of fixed width")
    val Width: Dp = 300.dp
    
    val BorderWidth: Dp = FormTokens.BorderWidthDefault
    val CornerSize: Dp = FormTokens.CornerRadiusMedium
    val FontSize: TextUnit = 17.sp

    fun height(size: ComponentSize): Dp = size.height

    fun fontSize(size: ComponentSize): TextUnit = size.fontSize

    @Composable
    fun borderColor(
        status: ComponentStatus = ComponentStatus.Default,
        isFocused: Boolean = false,
        isHovered: Boolean = false,
        enabled: Boolean = true
    ): Color = when {
        !enabled -> PaletteTheme.colors.disabledBorder
        isFocused -> when (status) {
            ComponentStatus.Default -> PaletteTheme.colors.focusBorder
            ComponentStatus.Success -> PaletteTheme.colors.successBorder
            ComponentStatus.Warning -> PaletteTheme.colors.warningBorder
            ComponentStatus.Error -> PaletteTheme.colors.errorBorder
        }
        isHovered -> PaletteTheme.colors.hoverBorder
        else -> status.borderColor()
    }

    @Composable
    fun shadowColor(
        status: ComponentStatus = ComponentStatus.Default,
        isFocused: Boolean = false
    ): Color = if (isFocused) {
        status.shadowColor()
    } else {
        Color.Transparent
    }

    @Composable
    fun backgroundColor(enabled: Boolean = true): Color = 
        if (enabled) PaletteTheme.colors.surface 
        else PaletteTheme.colors.disabledBackground

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
