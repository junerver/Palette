package xyz.junerver.compose.palette.components.treeselect

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

@Immutable
data class TreeSelectColors(
    val textColor: Color,
    val placeholderColor: Color,
    val disabledTextColor: Color,
    val containerColor: Color,
    val dropdownContainerColor: Color,
    val nodeTextColor: Color,
    val selectedNodeTextColor: Color,
    val selectedNodeContainerColor: Color,
    val disabledNodeTextColor: Color,
    val hoverColor: Color,
    val iconColor: Color,
    val borderColor: Color,
    val disabledBorderColor: Color,
)

object TreeSelectDefaults {
    val BorderWidth: Dp = TextFieldDefaults.BorderWidth
    val DropdownWidth: Dp = 240.dp
    val DropdownMaxHeight: Dp = 300.dp
    val NodeHeight: Dp = 36.dp
    val Indent: Dp = 20.dp
    val IconSize: Dp = 16.dp
    val FontSize: TextUnit = 14.sp
    val ArrowSize: Dp = 16.dp
    val SearchPadding: Dp = 8.dp
    val TrailingIconAlpha: Float = 0.72f
    val Separator: String = " / "

    @Composable
    @ReadOnlyComposable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.select.borderWidth

    @Composable
    @ReadOnlyComposable
    fun dropdownWidth(): Dp = PaletteTheme.componentThemes.select.dropdownMinWidth

    @Composable
    @ReadOnlyComposable
    fun dropdownMaxHeight(): Dp = PaletteTheme.componentThemes.select.dropdownMaxHeight

    @Composable
    @ReadOnlyComposable
    fun nodeHeight(): Dp = PaletteTheme.componentThemes.select.optionHeight

    @Composable
    @ReadOnlyComposable
    fun indent(): Dp = PaletteTheme.componentThemes.select.treeIndent

    @Composable
    @ReadOnlyComposable
    fun fontSize(): TextUnit = PaletteTheme.componentThemes.select.optionTextStyle.fontSize

    @Composable
    @ReadOnlyComposable
    fun arrowSize(): Dp = PaletteTheme.componentThemes.select.arrowSize

    @Composable
    @ReadOnlyComposable
    fun searchPadding(): Dp = PaletteTheme.componentThemes.select.searchFieldPadding

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
        nodeTextColor: Color = PaletteTheme.componentThemes.select.optionTextColor,
        selectedNodeTextColor: Color = PaletteTheme.componentThemes.select.selectedOptionTextColor,
        selectedNodeContainerColor: Color = PaletteTheme.componentThemes.select.selectedOptionContainerColor,
        disabledNodeTextColor: Color = PaletteTheme.componentThemes.select.disabledOptionTextColor,
        hoverColor: Color = PaletteTheme.componentThemes.select.hoverOptionContainerColor,
        iconColor: Color = PaletteTheme.componentThemes.select.iconColor,
        borderColor: Color = PaletteTheme.componentThemes.select.borderColor,
        disabledBorderColor: Color = PaletteTheme.componentThemes.select.disabledBorderColor,
    ): TreeSelectColors = TreeSelectColors(
        textColor = textColor,
        placeholderColor = placeholderColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        dropdownContainerColor = dropdownContainerColor,
        nodeTextColor = nodeTextColor,
        selectedNodeTextColor = selectedNodeTextColor,
        selectedNodeContainerColor = selectedNodeContainerColor,
        disabledNodeTextColor = disabledNodeTextColor,
        hoverColor = hoverColor,
        iconColor = iconColor,
        borderColor = borderColor,
        disabledBorderColor = disabledBorderColor,
    )
}
