package xyz.junerver.compose.palette.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
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
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun headerColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun dayOfWeekColor(): Color = PaletteTheme.colors.hint

    @Composable
    @ReadOnlyComposable
    fun dayColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun selectedColor(): Color = PaletteTheme.colors.primary

    @Composable
    @ReadOnlyComposable
    fun selectedTextColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun disabledColor(): Color = PaletteTheme.colors.hint

    @Composable
    @ReadOnlyComposable
    fun todayBorderColor(): Color = PaletteTheme.colors.primary
}
