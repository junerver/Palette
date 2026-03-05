package xyz.junerver.compose.palette.components.datetimerange

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DateTimeRangeDefaults {
    @Composable
    @ReadOnlyComposable
    fun placeholderColor(): Color = PaletteTheme.colors.hint

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun invalidColor(): Color = PaletteTheme.colors.error
}
