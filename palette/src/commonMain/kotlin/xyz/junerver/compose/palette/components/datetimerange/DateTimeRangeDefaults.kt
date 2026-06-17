package xyz.junerver.compose.palette.components.datetimerange

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DateTimeRangeDefaults {
    @Composable
    @ReadOnlyComposable
    fun placeholderColor(): Color = PaletteTheme.componentThemes.dateTime.inputPlaceholderColor

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.componentThemes.dateTime.inputTextColor

    @Composable
    @ReadOnlyComposable
    fun invalidColor(): Color = PaletteTheme.componentThemes.dateTime.inputInvalidColor
}
