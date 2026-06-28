package xyz.junerver.compose.palette.components.daterangepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DateRangePickerDefaults {
    /** 默认分钟步长（与 PTimePicker 对齐）。 */
    const val DefaultMinuteStep: Int = 5

    @Composable
    @ReadOnlyComposable
    fun placeholderColor(): Color = PaletteTheme.componentThemes.dateTime.inputPlaceholderColor

    @Composable
    @ReadOnlyComposable
    fun textColor(): Color = PaletteTheme.componentThemes.dateTime.inputTextColor

    @Composable
    @ReadOnlyComposable
    fun iconColor(): Color = PaletteTheme.componentThemes.dateTime.inputIconColor

    /**
     * 默认快捷项：今天 / 最近 7 天 / 本月 / 本季度。
     * 全部使用工厂函数，保证"最近 7 天"等相对区间在点击瞬间即时计算。
     */
    fun presets(today: LocalDate = defaultToday()): List<DateRangePreset> = listOf(
        DateRangePreset(label = "今天") { DateRange(start = today, end = today) },
        DateRangePreset(label = "最近 7 天") {
            DateRange(start = today.minus(6, DateTimeUnit.DAY), end = today)
        },
        DateRangePreset(label = "本月") {
            val monthStart = LocalDate(today.year, today.monthNumber, 1)
            val monthEnd = monthStart.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
            DateRange(start = monthStart, end = monthEnd)
        },
        DateRangePreset(label = "本季度") {
            val quarterStartMonth = ((today.monthNumber - 1) / 3) * 3 + 1
            val qStart = LocalDate(today.year, quarterStartMonth, 1)
            val qEnd = qStart.plus(3, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
            DateRange(start = qStart, end = qEnd)
        },
    )

    /**
     * 跨平台"今天"默认实现（基于 kotlin.time 系统时钟，kotlinx-datetime 0.8+ API）。
     */
    fun defaultToday(): LocalDate = kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
}
