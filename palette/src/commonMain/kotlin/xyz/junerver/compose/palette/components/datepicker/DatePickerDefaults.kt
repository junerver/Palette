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
    fun iconSpacing(): Dp = PaletteTheme.componentThemes.dateTime.inputIconSpacing

    @Composable
    @ReadOnlyComposable
    fun placeholderColor(): Color = PaletteTheme.componentThemes.dateTime.inputPlaceholderColor

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.componentThemes.dateTime.inputTextColor

    @Composable
    @ReadOnlyComposable
    fun invalidColor(): Color = PaletteTheme.componentThemes.dateTime.inputInvalidColor

    @Composable
    @ReadOnlyComposable
    fun iconColor(): Color = PaletteTheme.componentThemes.dateTime.inputIconColor

    fun format(date: LocalDate): String = date.toString()
}
