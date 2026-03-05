package xyz.junerver.compose.palette.components.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class SelectColors(
    val textColor: Color,
    val placeholderColor: Color,
    val disabledTextColor: Color,
    val containerColor: Color,
    val dropdownContainerColor: Color,
    val optionTextColor: Color,
    val selectedOptionTextColor: Color,
    val selectedOptionContainerColor: Color,
    val disabledOptionTextColor: Color,
)

object SelectDefaults {
    val BorderWidth: Dp = TextFieldDefaults.BorderWidth
    val DropdownMaxHeight: Dp = 280.dp
    val SearchFieldPadding: Dp = 8.dp
    val OptionCornerRadius: Dp = 6.dp
    val TrailingIconAlpha: Float = 0.72f

    @Composable
    fun borderColor(
        status: ComponentStatus = ComponentStatus.Default,
        isFocused: Boolean = false,
        isHovered: Boolean = false,
        enabled: Boolean = true,
    ): Color = TextFieldDefaults.borderColor(
        status = status,
        isFocused = isFocused,
        isHovered = isHovered,
        enabled = enabled
    )

    @Composable
    @ReadOnlyComposable
    fun colors(
        textColor: Color = PaletteTheme.colors.onSurface,
        placeholderColor: Color = PaletteTheme.colors.hint,
        disabledTextColor: Color = PaletteTheme.colors.onSurface.copy(alpha = 0.5f),
        containerColor: Color = PaletteTheme.colors.surface,
        dropdownContainerColor: Color = PaletteTheme.colors.surface,
        optionTextColor: Color = PaletteTheme.colors.onSurface,
        selectedOptionTextColor: Color = PaletteTheme.colors.primary,
        selectedOptionContainerColor: Color = PaletteTheme.colors.primary.copy(alpha = 0.12f),
        disabledOptionTextColor: Color = PaletteTheme.colors.hint,
    ): SelectColors = SelectColors(
        textColor = textColor,
        placeholderColor = placeholderColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        dropdownContainerColor = dropdownContainerColor,
        optionTextColor = optionTextColor,
        selectedOptionTextColor = selectedOptionTextColor,
        selectedOptionContainerColor = selectedOptionContainerColor,
        disabledOptionTextColor = disabledOptionTextColor,
    )

    @Composable
    @ReadOnlyComposable
    fun noResultText(): String = PaletteTheme.strings.selectNoResult
}
