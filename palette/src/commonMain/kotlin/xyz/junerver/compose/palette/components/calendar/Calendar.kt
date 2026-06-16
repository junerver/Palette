package xyz.junerver.compose.palette.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

private val DAY_OF_WEEK_LABELS = listOf("一", "二", "三", "四", "五", "六", "日")

private data class CalendarCell(
    val day: Int,
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
)

@Composable
fun PCalendar(
    selectedDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    showHeader: Boolean = true,
    today: LocalDate,
) {
    val todayDate = today
    val initYear = selectedDate?.let { it.year } ?: todayDate.let { it.year }
    val initMonth = selectedDate?.let { it.monthNumber } ?: todayDate.let { it.monthNumber }

    val (currentYear, setCurrentYear) = xyz.junerver.compose.hooks.useState(initYear)
    val (currentMonth, setCurrentMonth) = xyz.junerver.compose.hooks.useState(initMonth)

    val gridData = computeCalendarGridData(currentYear, currentMonth)

    Surface(
        modifier = modifier.clip(RoundedCornerShape(CalendarDefaults.CornerRadius)),
        color = CalendarDefaults.containerColor(),
    ) {
        Column {
            if (showHeader) {
                CalendarHeader(
                    year = currentYear,
                    month = currentMonth,
                    onPrev = {
                        val prev = LocalDate(currentYear, currentMonth, 1).minus(1, DateTimeUnit.MONTH)
                        setCurrentYear(prev.let { it.year })
                        setCurrentMonth(prev.let { it.monthNumber })
                    },
                    onNext = {
                        val next = LocalDate(currentYear, currentMonth, 1).plus(1, DateTimeUnit.MONTH)
                        setCurrentYear(next.let { it.year })
                        setCurrentMonth(next.let { it.monthNumber })
                    },
                )
            }
            DayOfWeekRow()
            CalendarGrid(
                gridData = gridData,
                selectedDate = selectedDate,
                today = todayDate,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelect = onDateSelect,
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    year: Int,
    month: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(CalendarDefaults.HeaderHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrev) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = CalendarDefaults.headerColor(),
            )
        }
        Text(
            text = "${year}年${month.toString().padStart(2, '0')}月",
            color = CalendarDefaults.headerColor(),
            fontSize = CalendarDefaults.HeaderFontSize,
            fontWeight = FontWeight.Medium,
        )
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = CalendarDefaults.headerColor(),
            )
        }
    }
}

@Composable
private fun DayOfWeekRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(CalendarDefaults.DayOfWeekHeight),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DAY_OF_WEEK_LABELS.forEach { label ->
            Box(
                modifier = Modifier.size(CalendarDefaults.CellSize),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = CalendarDefaults.dayOfWeekColor(),
                    fontSize = CalendarDefaults.DayOfWeekFontSize,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    gridData: List<List<CalendarCell>>,
    selectedDate: LocalDate?,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
) {
    val selectedColor = CalendarDefaults.selectedColor()
    val selectedTextColor = CalendarDefaults.selectedTextColor()
    val todayBorderColor = CalendarDefaults.todayBorderColor()
    val dayColor = CalendarDefaults.dayColor()
    val disabledColor = CalendarDefaults.disabledColor()

    Column(modifier = Modifier.fillMaxWidth()) {
        gridData.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                week.forEach { cell ->
                    CalendarDayCell(
                        cell = cell,
                        isSelected = cell.date != null && cell.date == selectedDate,
                        isToday = cell.date != null && cell.date == today,
                        isEnabled = cell.isCurrentMonth && isDateEnabled(cell.date, minDate, maxDate),
                        selectedColor = selectedColor,
                        selectedTextColor = selectedTextColor,
                        todayBorderColor = todayBorderColor,
                        dayColor = dayColor,
                        disabledColor = disabledColor,
                        onDateSelect = onDateSelect,
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    cell: CalendarCell,
    isSelected: Boolean,
    isToday: Boolean,
    isEnabled: Boolean,
    selectedColor: Color,
    selectedTextColor: Color,
    todayBorderColor: Color,
    dayColor: Color,
    disabledColor: Color,
    onDateSelect: (LocalDate) -> Unit,
) {
    val textColor = when {
        isSelected -> selectedTextColor
        !isEnabled -> disabledColor
        else -> dayColor
    }

    val circleModifier = when {
        isSelected -> Modifier
            .size(CalendarDefaults.SelectedCircleSize)
            .background(selectedColor, CircleShape)
        isToday -> Modifier
            .size(CalendarDefaults.SelectedCircleSize)
            .border(1.dp, todayBorderColor, CircleShape)
        else -> Modifier.size(CalendarDefaults.SelectedCircleSize)
    }

    val clickableModifier = if (cell.date != null && isEnabled) {
        Modifier.clickable { onDateSelect(cell.date) }
    } else {
        Modifier
    }

    Box(
        modifier = Modifier.size(CalendarDefaults.CellSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = circleModifier.then(clickableModifier),
            contentAlignment = Alignment.Center,
        ) {
            if (cell.day > 0) {
                Text(
                    text = cell.day.toString(),
                    color = textColor,
                    fontSize = CalendarDefaults.FontSize,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun isDateEnabled(date: LocalDate?, minDate: LocalDate?, maxDate: LocalDate?): Boolean {
    if (date == null) return false
    if (minDate != null && date < minDate) return false
    if (maxDate != null && date > maxDate) return false
    return true
}

private fun computeCalendarGridData(year: Int, month: Int): List<List<CalendarCell>> {
    val firstDay = LocalDate(year, month, 1)
    val daysInMonth = LocalDate(year, month, 1).let { first ->
        val nextMonth = first.plus(1, DateTimeUnit.MONTH)
        nextMonth.minus(1, DateTimeUnit.DAY).let { it.dayOfMonth }
    }
    val firstDayOfWeekOffset = (dayOfWeekIso(firstDay.dayOfWeek) - 1 + 7) % 7

    val totalCells = firstDayOfWeekOffset + daysInMonth
    val totalRows = (totalCells + 6) / 7

    val cells = mutableListOf<List<CalendarCell>>()
    var dayCounter = 1

    for (week in 0 until totalRows) {
        val row = mutableListOf<CalendarCell>()
        for (dayOfWeek in 0 until 7) {
            val cellIndex = week * 7 + dayOfWeek
            if (cellIndex < firstDayOfWeekOffset || dayCounter > daysInMonth) {
                row.add(CalendarCell(day = 0, date = null, isCurrentMonth = false))
            } else {
                val date = LocalDate(year, month, dayCounter)
                row.add(CalendarCell(day = dayCounter, date = date, isCurrentMonth = true))
                dayCounter++
            }
        }
        cells.add(row)
    }

    return cells
}

private fun dayOfWeekIso(dayOfWeek: DayOfWeek): Int = when (dayOfWeek) {
    DayOfWeek.MONDAY -> 1
    DayOfWeek.TUESDAY -> 2
    DayOfWeek.WEDNESDAY -> 3
    DayOfWeek.THURSDAY -> 4
    DayOfWeek.FRIDAY -> 5
    DayOfWeek.SATURDAY -> 6
    DayOfWeek.SUNDAY -> 7
}
