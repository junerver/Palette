package xyz.junerver.compose.palette.components.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TimePickerDefaults {
    val DefaultMinuteStep: Int = 5

    @Composable
    @ReadOnlyComposable
    fun placeholderColor(): Color = PaletteTheme.colors.hint

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.colors.onSurface
}
