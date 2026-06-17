package xyz.junerver.compose.palette.components.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
    @ReadOnlyComposable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.select.borderWidth

    @Composable
    @ReadOnlyComposable
    fun dropdownMaxHeight(): Dp = PaletteTheme.componentThemes.select.dropdownMaxHeight

    @Composable
    @ReadOnlyComposable
    fun searchFieldPadding(): Dp = PaletteTheme.componentThemes.select.searchFieldPadding

    @Composable
    @ReadOnlyComposable
    fun optionHeight(): Dp = PaletteTheme.componentThemes.select.optionHeight

    @Composable
    @ReadOnlyComposable
    fun optionPaddingHorizontal(): Dp = PaletteTheme.componentThemes.select.optionPaddingHorizontal

    @Composable
    @ReadOnlyComposable
    fun optionTextStyle(): TextStyle = PaletteTheme.componentThemes.select.optionTextStyle

    @Composable
    @ReadOnlyComposable
    fun optionFontSize(): TextUnit = PaletteTheme.componentThemes.select.optionTextStyle.fontSize

    @Composable
    @ReadOnlyComposable
    fun optionCornerRadius(): Dp = PaletteTheme.componentThemes.select.optionCornerRadius

    @Composable
    @ReadOnlyComposable
    fun arrowSize(): Dp = PaletteTheme.componentThemes.select.arrowSize

    @Composable
    @ReadOnlyComposable
    fun trailingIconAlpha(): Float = PaletteTheme.componentThemes.select.trailingIconAlpha

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
        textColor: Color = PaletteTheme.componentThemes.select.textColor,
        placeholderColor: Color = PaletteTheme.componentThemes.select.placeholderColor,
        disabledTextColor: Color = PaletteTheme.componentThemes.select.disabledTextColor,
        containerColor: Color = PaletteTheme.componentThemes.select.containerColor,
        dropdownContainerColor: Color = PaletteTheme.componentThemes.select.dropdownContainerColor,
        optionTextColor: Color = PaletteTheme.componentThemes.select.optionTextColor,
        selectedOptionTextColor: Color = PaletteTheme.componentThemes.select.selectedOptionTextColor,
        selectedOptionContainerColor: Color = PaletteTheme.componentThemes.select.selectedOptionContainerColor,
        disabledOptionTextColor: Color = PaletteTheme.componentThemes.select.disabledOptionTextColor,
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
