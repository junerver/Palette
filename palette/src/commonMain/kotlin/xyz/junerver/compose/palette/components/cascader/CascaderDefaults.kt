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
    fun colors(
        textColor: Color = PaletteTheme.colors.onSurface,
        placeholderColor: Color = PaletteTheme.colors.hint,
        disabledTextColor: Color = PaletteTheme.colors.onSurface.copy(alpha = 0.5f),
        containerColor: Color = PaletteTheme.colors.surface,
        dropdownContainerColor: Color = PaletteTheme.colors.surface,
        itemTextColor: Color = PaletteTheme.colors.onSurface,
        selectedItemTextColor: Color = PaletteTheme.colors.primary,
        disabledItemTextColor: Color = PaletteTheme.colors.hint,
        hoverColor: Color = PaletteTheme.colors.border.copy(alpha = 0.5f),
        borderColor: Color = PaletteTheme.colors.border,
        disabledBorderColor: Color = PaletteTheme.colors.border.copy(alpha = 0.5f),
    ): CascaderColors = CascaderColors(
        textColor = textColor,
        placeholderColor = placeholderColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        dropdownContainerColor = dropdownContainerColor,
        itemTextColor = itemTextColor,
        selectedItemTextColor = selectedItemTextColor,
        disabledItemTextColor = disabledItemTextColor,
        hoverColor = hoverColor,
        borderColor = borderColor,
        disabledBorderColor = disabledBorderColor,
    )
}
