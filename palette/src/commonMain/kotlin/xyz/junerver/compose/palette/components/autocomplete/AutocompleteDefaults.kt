package xyz.junerver.compose.palette.components.autocomplete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class AutocompleteOption(
    val value: String,
    val label: String,
    val disabled: Boolean = false,
)

object AutocompleteDefaults {
    val DropdownMaxHeight: Dp = 280.dp
    val OptionHeight: Dp = 36.dp
    val OptionPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 14.sp
    val CornerRadius: Dp = 8.dp

    @Composable
    fun dropdownContainerColor(): Color = PaletteTheme.componentThemes.select.dropdownContainerColor

    @Composable
    fun dropdownMaxHeight(): Dp = PaletteTheme.componentThemes.select.dropdownMaxHeight

    @Composable
    fun optionHeight(): Dp = PaletteTheme.componentThemes.select.optionHeight

    @Composable
    fun optionPaddingHorizontal(): Dp = PaletteTheme.componentThemes.select.optionPaddingHorizontal

    @Composable
    fun optionTextStyle(): TextStyle = PaletteTheme.componentThemes.select.optionTextStyle

    @Composable
    fun fontSize(): TextUnit = PaletteTheme.componentThemes.select.optionTextStyle.fontSize

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.select.optionCornerRadius

    @Composable
    fun optionTextColor(): Color = PaletteTheme.componentThemes.select.optionTextColor

    @Composable
    fun selectedOptionColor(): Color = PaletteTheme.componentThemes.select.selectedOptionTextColor

    @Composable
    fun selectedOptionContainerColor(): Color = PaletteTheme.componentThemes.select.selectedOptionContainerColor

    @Composable
    fun hoverOptionColor(): Color = PaletteTheme.componentThemes.select.hoverOptionContainerColor

    @Composable
    fun disabledOptionColor(): Color = PaletteTheme.componentThemes.select.disabledOptionTextColor
}
