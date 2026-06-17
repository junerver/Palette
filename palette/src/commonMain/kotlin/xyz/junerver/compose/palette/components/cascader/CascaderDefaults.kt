package xyz.junerver.compose.palette.components.cascader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class CascaderExpandTrigger {
    Click, Hover
}

@Immutable
data class CascaderColors(
    val textColor: Color,
    val placeholderColor: Color,
    val disabledTextColor: Color,
    val containerColor: Color,
    val dropdownContainerColor: Color,
    val itemTextColor: Color,
    val selectedItemTextColor: Color,
    val selectedItemContainerColor: Color,
    val disabledItemTextColor: Color,
    val hoverColor: Color,
    val borderColor: Color,
    val disabledBorderColor: Color,
)

object CascaderDefaults {
    val BorderWidth: Dp = TextFieldDefaults.BorderWidth
    val ColumnWidth: Dp = 200.dp
    val ColumnMaxHeight: Dp = 220.dp
    val ItemHeight: Dp = 36.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 14.sp
    val ArrowSize: Dp = 16.dp
    val Separator: String = " / "
    val TrailingIconAlpha: Float = 0.72f

    @Composable
    @ReadOnlyComposable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.select.borderWidth

    @Composable
    @ReadOnlyComposable
    fun columnWidth(): Dp = PaletteTheme.componentThemes.select.cascaderColumnWidth

    @Composable
    @ReadOnlyComposable
    fun columnMaxHeight(): Dp = PaletteTheme.componentThemes.select.cascaderColumnMaxHeight

    @Composable
    @ReadOnlyComposable
    fun itemHeight(): Dp = PaletteTheme.componentThemes.select.optionHeight

    @Composable
    @ReadOnlyComposable
    fun itemPaddingHorizontal(): Dp = PaletteTheme.componentThemes.select.optionPaddingHorizontal

    @Composable
    @ReadOnlyComposable
    fun fontSize(): TextUnit = PaletteTheme.componentThemes.select.optionTextStyle.fontSize

    @Composable
    @ReadOnlyComposable
    fun arrowSize(): Dp = PaletteTheme.componentThemes.select.arrowSize

    @Composable
    @ReadOnlyComposable
    fun trailingIconAlpha(): Float = PaletteTheme.componentThemes.select.trailingIconAlpha

    @Composable
    @ReadOnlyComposable
    fun colors(
        textColor: Color = PaletteTheme.componentThemes.select.textColor,
        placeholderColor: Color = PaletteTheme.componentThemes.select.placeholderColor,
        disabledTextColor: Color = PaletteTheme.componentThemes.select.disabledTextColor,
        containerColor: Color = PaletteTheme.componentThemes.select.containerColor,
        dropdownContainerColor: Color = PaletteTheme.componentThemes.select.dropdownContainerColor,
        itemTextColor: Color = PaletteTheme.componentThemes.select.optionTextColor,
        selectedItemTextColor: Color = PaletteTheme.componentThemes.select.selectedOptionTextColor,
        selectedItemContainerColor: Color = PaletteTheme.componentThemes.select.selectedOptionContainerColor,
        disabledItemTextColor: Color = PaletteTheme.componentThemes.select.disabledOptionTextColor,
        hoverColor: Color = PaletteTheme.componentThemes.select.hoverOptionContainerColor,
        borderColor: Color = PaletteTheme.componentThemes.select.borderColor,
        disabledBorderColor: Color = PaletteTheme.componentThemes.select.disabledBorderColor,
    ): CascaderColors = CascaderColors(
        textColor = textColor,
        placeholderColor = placeholderColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        dropdownContainerColor = dropdownContainerColor,
        itemTextColor = itemTextColor,
        selectedItemTextColor = selectedItemTextColor,
        selectedItemContainerColor = selectedItemContainerColor,
        disabledItemTextColor = disabledItemTextColor,
        hoverColor = hoverColor,
        borderColor = borderColor,
        disabledBorderColor = disabledBorderColor,
    )
}
