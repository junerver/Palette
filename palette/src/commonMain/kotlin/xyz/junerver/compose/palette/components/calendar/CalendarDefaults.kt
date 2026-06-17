package xyz.junerver.compose.palette.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CalendarDefaults {
    val CellSize: Dp = 40.dp
    val HeaderHeight: Dp = 48.dp
    val DayOfWeekHeight: Dp = 32.dp
    val FontSize: TextUnit = 14.sp
    val HeaderFontSize: TextUnit = 16.sp
    val DayOfWeekFontSize: TextUnit = 12.sp
    val SelectedCircleSize: Dp = 32.dp
    val CornerRadius: Dp = 8.dp

    @Composable
    @ReadOnlyComposable
    fun cellSize(): Dp = PaletteTheme.componentThemes.dateTime.calendarCellSize

    @Composable
    @ReadOnlyComposable
    fun headerHeight(): Dp = PaletteTheme.componentThemes.dateTime.calendarHeaderHeight

    @Composable
    @ReadOnlyComposable
    fun dayOfWeekHeight(): Dp = PaletteTheme.componentThemes.dateTime.calendarDayOfWeekHeight

    @Composable
    @ReadOnlyComposable
    fun selectedCircleSize(): Dp = PaletteTheme.componentThemes.dateTime.calendarSelectedCircleSize

    @Composable
    @ReadOnlyComposable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.dateTime.calendarCornerRadius

    @Composable
    @ReadOnlyComposable
    fun todayBorderWidth(): Dp = PaletteTheme.componentThemes.dateTime.calendarTodayBorderWidth

    @Composable
    @ReadOnlyComposable
    fun dayTextStyle(): TextStyle = PaletteTheme.componentThemes.dateTime.calendarDayTextStyle

    @Composable
    @ReadOnlyComposable
    fun headerTextStyle(): TextStyle = PaletteTheme.componentThemes.dateTime.calendarHeaderTextStyle

    @Composable
    @ReadOnlyComposable
    fun dayOfWeekTextStyle(): TextStyle = PaletteTheme.componentThemes.dateTime.calendarDayOfWeekTextStyle

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.componentThemes.dateTime.calendarContainerColor

    @Composable
    @ReadOnlyComposable
    fun headerColor(): Color = PaletteTheme.componentThemes.dateTime.calendarHeaderColor

    @Composable
    @ReadOnlyComposable
    fun dayOfWeekColor(): Color = PaletteTheme.componentThemes.dateTime.calendarDayOfWeekColor

    @Composable
    @ReadOnlyComposable
    fun dayColor(): Color = PaletteTheme.componentThemes.dateTime.calendarDayColor

    @Composable
    @ReadOnlyComposable
    fun selectedColor(): Color = PaletteTheme.componentThemes.dateTime.calendarSelectedColor

    @Composable
    @ReadOnlyComposable
    fun selectedTextColor(): Color = PaletteTheme.componentThemes.dateTime.calendarSelectedTextColor

    @Composable
    @ReadOnlyComposable
    fun disabledColor(): Color = PaletteTheme.componentThemes.dateTime.calendarDisabledColor

    @Composable
    @ReadOnlyComposable
    fun todayBorderColor(): Color = PaletteTheme.componentThemes.dateTime.calendarTodayBorderColor
}
