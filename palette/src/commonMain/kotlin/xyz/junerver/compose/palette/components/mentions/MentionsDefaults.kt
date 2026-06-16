package xyz.junerver.compose.palette.components.mentions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class MentionsOption(
    val value: String,
    val label: String,
    val disabled: Boolean = false,
)

object MentionsDefaults {
    val DropdownMaxHeight: Dp = 200.dp
    val OptionHeight: Dp = 36.dp
    val OptionPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 14.sp
    val MinLines: Int = 3
    val CornerRadius: Dp = 8.dp

    @Composable
    fun optionTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun selectedOptionColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun mentionTagColor(): Color = PaletteTheme.colors.primary.copy(alpha = 0.1f)
}
