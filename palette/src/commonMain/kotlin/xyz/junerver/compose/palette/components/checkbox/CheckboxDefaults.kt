package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.disabledBorder
import xyz.junerver.compose.palette.core.tokens.focusBorder
import xyz.junerver.compose.palette.core.tokens.hoverBorder

@Immutable
data class CheckboxColors(
    val checkedColor: Color,
    val uncheckedColor: Color,
    val checkmarkColor: Color,
    val disabledColor: Color,
    val focusColor: Color,
    val hoverColor: Color
)

object CheckboxDefaults {
    @Composable
    fun color(): Color = PaletteTheme.colors.primary

    @Composable
    fun colors(
        checkedColor: Color = PaletteTheme.colors.primary,
        uncheckedColor: Color = PaletteTheme.colors.border,
        checkmarkColor: Color = Color.White,
        disabledColor: Color = PaletteTheme.colors.disabledBorder,
        focusColor: Color = PaletteTheme.colors.focusBorder,
        hoverColor: Color = PaletteTheme.colors.hoverBorder
    ): CheckboxColors = CheckboxColors(
        checkedColor = checkedColor,
        uncheckedColor = uncheckedColor,
        checkmarkColor = checkmarkColor,
        disabledColor = disabledColor,
        focusColor = focusColor,
        hoverColor = hoverColor
    )
}
