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
    fun colors(
        textColor: Color = PaletteTheme.colors.onSurface,
        placeholderColor: Color = PaletteTheme.colors.hint,
        disabledTextColor: Color = PaletteTheme.colors.onSurface.copy(alpha = 0.5f),
        containerColor: Color = PaletteTheme.colors.surface,
        dropdownContainerColor: Color = PaletteTheme.colors.surface,
        nodeTextColor: Color = PaletteTheme.colors.onSurface,
        selectedNodeTextColor: Color = PaletteTheme.colors.primary,
        disabledNodeTextColor: Color = PaletteTheme.colors.hint,
        hoverColor: Color = PaletteTheme.colors.border.copy(alpha = 0.5f),
        iconColor: Color = PaletteTheme.colors.hint,
        borderColor: Color = PaletteTheme.colors.border,
        disabledBorderColor: Color = PaletteTheme.colors.border.copy(alpha = 0.5f),
    ): TreeSelectColors = TreeSelectColors(
        textColor = textColor,
        placeholderColor = placeholderColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        dropdownContainerColor = dropdownContainerColor,
        nodeTextColor = nodeTextColor,
        selectedNodeTextColor = selectedNodeTextColor,
        disabledNodeTextColor = disabledNodeTextColor,
        hoverColor = hoverColor,
        iconColor = iconColor,
        borderColor = borderColor,
        disabledBorderColor = disabledBorderColor,
    )
}
