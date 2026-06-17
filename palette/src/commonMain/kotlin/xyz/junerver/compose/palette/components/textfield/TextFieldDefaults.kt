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

    @Composable
    fun sizeTokens(size: ComponentSize): PaletteControlSizeTokens = when (size) {
        ComponentSize.Small -> PaletteTheme.componentThemes.textField.small
        ComponentSize.Medium -> PaletteTheme.componentThemes.textField.medium
        ComponentSize.Large -> PaletteTheme.componentThemes.textField.large
    }

    @Composable
    fun height(size: ComponentSize): Dp = sizeTokens(size).height

    @Composable
    fun fontSize(size: ComponentSize): TextUnit = sizeTokens(size).fontSize

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.textField.borderWidth

    @Composable
    fun focusBorderWidth(): Dp = PaletteTheme.componentThemes.textField.focusBorderWidth

    @Composable
    fun cornerSize(): Dp = PaletteTheme.componentThemes.textField.cornerRadius

    @Composable
    fun shadowElevation(): Dp = PaletteTheme.componentThemes.textField.shadowElevation

    @Composable
    fun motionDuration(): Int = PaletteTheme.componentThemes.textField.motionDuration

    @Composable
    fun borderColor(
        status: ComponentStatus = ComponentStatus.Default,
        isFocused: Boolean = false,
        isHovered: Boolean = false,
        enabled: Boolean = true
    ): Color = when {
        !enabled -> PaletteTheme.componentThemes.textField.disabledBorderColor
        isFocused -> when (status) {
            ComponentStatus.Default -> PaletteTheme.componentThemes.textField.focusBorderColor
            ComponentStatus.Success -> PaletteTheme.componentThemes.textField.successBorderColor
            ComponentStatus.Warning -> PaletteTheme.componentThemes.textField.warningBorderColor
            ComponentStatus.Error -> PaletteTheme.componentThemes.textField.errorBorderColor
        }
        isHovered -> PaletteTheme.componentThemes.textField.hoverBorderColor
        else -> status.borderColor()
    }

    @Composable
    fun shadowColor(
        status: ComponentStatus = ComponentStatus.Default,
        isFocused: Boolean = false
    ): Color = if (isFocused) {
        when (status) {
            ComponentStatus.Default -> PaletteTheme.componentThemes.textField.focusShadowColor
            ComponentStatus.Success -> PaletteTheme.componentThemes.textField.successShadowColor
            ComponentStatus.Warning -> PaletteTheme.componentThemes.textField.warningShadowColor
            ComponentStatus.Error -> PaletteTheme.componentThemes.textField.errorShadowColor
        }
    } else {
        Color.Transparent
    }

    @Composable
    fun backgroundColor(enabled: Boolean = true): Color = 
        if (enabled) PaletteTheme.componentThemes.textField.backgroundColor
        else PaletteTheme.componentThemes.textField.disabledBackgroundColor

    @Composable
    fun colors(
        textColor: Color = PaletteTheme.componentThemes.textField.textColor,
        hintColor: Color = PaletteTheme.componentThemes.textField.placeholderColor,
        borderColor: Color = PaletteTheme.componentThemes.textField.borderColor,
        backgroundColor: Color = PaletteTheme.componentThemes.textField.backgroundColor,
    ): BorderTextFieldColors = BorderTextFieldColors(
        textColor = textColor,
        hintColor = hintColor,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
    )
}
