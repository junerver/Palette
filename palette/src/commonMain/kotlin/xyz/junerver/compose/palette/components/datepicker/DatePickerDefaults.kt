package xyz.junerver.compose.palette.components.datepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DatePickerDefaults {
    val CalendarIconSpacing: Dp = 8.dp

    @Composable
    @ReadOnlyComposable
    fun placeholderColor(): Color = PaletteTheme.colors.hint

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun invalidColor(): Color = PaletteTheme.colors.error

    fun format(date: LocalDate): String = date.toString()
}
