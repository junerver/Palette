package xyz.junerver.compose.palette.components.autocomplete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
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
    fun optionTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun selectedOptionColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun hoverOptionColor(): Color = PaletteTheme.colors.border

    @Composable
    fun disabledOptionColor(): Color = PaletteTheme.colors.hint
}
