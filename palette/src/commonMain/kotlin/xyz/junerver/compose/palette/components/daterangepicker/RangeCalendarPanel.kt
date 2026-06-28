package xyz.junerver.compose.palette.components.daterangepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.theme.PaletteTheme

private val RANGE_DAY_OF_WEEK_LABELS = listOf("一", "二", "三", "四", "五", "六", "日")

// pill-cap 端帽圆角半径：占满单元格宽度形成胶囊左/右半圆（图形编码常量）
private val CapCornerRadius: Dp = 999.dp

private data class RangeCell(
    val day: Int,
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
)

/**
 * 区间日历面板：两个相邻月份并排。
 *
 * 交互模型（参考 Ant Design / Element Plus 共识）：
 * - 起点未选 → 点击任意日期成为起点
 * - 起点已选、终点未选 → hover 实时预览 band；点击日期成为终点
 * - 起终点都已选 → 再次点击重置为新起点
 *
 * 高亮分层：band（中间浅色带）> hoverBand（预览更浅带）> cap（起止端帽实心 pill）。
 */
@Composable
internal fun RangeCalendarPanel(
    value: DateRange,
    hoverDate: LocalDate?,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    maxSpanDays: Int?,
    onDateClick: (LocalDate) -> Unit,
    onDateHover: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 左面板月份由 value 起点驱动，缺省回退到 today，保证打开即对齐用户已选区间
    val anchor = value.start ?: value.end ?: today
    val (leftYear, setLeftYear) = useState(anchor.year)
    val (leftMonth, setLeftMonth) = useState(anchor.monthNumber)

    fun shiftMonths(delta: Int) {
        val base = LocalDate(leftYear, leftMonth, 1).plus(delta, DateTimeUnit.MONTH)
        setLeftYear(base.year)
        setLeftMonth(base.monthNumber)
    }

    val rightYear: Int
    val rightMonth: Int
    LocalDate(leftYear, leftMonth, 1).plus(1, DateTimeUnit.MONTH).let {
        rightYear = it.year
        rightMonth = it.monthNumber
    }

    val tokens = PaletteTheme.componentThemes.dateTime

    Surface(
        modifier = modifier,
        color = tokens.calendarContainerColor,
    ) {
        Row {
            SingleMonthPanel(
                year = leftYear,
                month = leftMonth,
                value = value,
                hoverDate = hoverDate,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDate = disabledDate,
                maxSpanDays = maxSpanDays,
                onPrev = { shiftMonths(-1) },
                onNext = { shiftMonths(1) },
                showNav = true,
                onDateClick = onDateClick,
                onDateHover = onDateHover,
            )
            Spacer(modifier = Modifier.width(tokens.calendarCellSize))
            SingleMonthPanel(
                year = rightYear,
                month = rightMonth,
                value = value,
                hoverDate = hoverDate,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDate = disabledDate,
                maxSpanDays = maxSpanDays,
                onPrev = { shiftMonths(-1) },
                onNext = { shiftMonths(1) },
                showNav = false,
                onDateClick = onDateClick,
                onDateHover = onDateHover,
            )
        }
    }
}

@Composable
private fun SingleMonthPanel(
    year: Int,
    month: Int,
    value: DateRange,
    hoverDate: LocalDate?,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    maxSpanDays: Int?,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    showNav: Boolean,
    onDateClick: (LocalDate) -> Unit,
    onDateHover: (LocalDate?) -> Unit,
) {
    val tokens = PaletteTheme.componentThemes.dateTime
    val grid = remember(year, month) { computeRangeGrid(year, month) }

    Column {
        // 月份头
        Row(
            modifier = Modifier.fillMaxWidth().height(tokens.calendarHeaderHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showNav) {
                IconButton(onClick = onPrev) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = tokens.calendarHeaderColor,
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(tokens.calendarHeaderHeight / 2))
            }
            Text(
                text = "$year-${month.toString().padStart(2, '0')}",
                color = tokens.calendarHeaderColor,
                style = tokens.calendarHeaderTextStyle,
            )
            if (showNav) {
                IconButton(onClick = onNext) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = tokens.calendarHeaderColor,
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(tokens.calendarHeaderHeight / 2))
            }
        }

        // 星期行
        Row(
            modifier = Modifier.fillMaxWidth().height(tokens.calendarDayOfWeekHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RANGE_DAY_OF_WEEK_LABELS.forEach { label ->
                Box(
                    modifier = Modifier.size(tokens.calendarCellSize),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = tokens.calendarDayOfWeekColor,
                        style = tokens.calendarDayOfWeekTextStyle,
                    )
                }
            }
        }

        // 日期网格
        Column(modifier = Modifier.fillMaxWidth()) {
            grid.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    week.forEach { cell ->
                        RangeDayCell(
                            cell = cell,
                            value = value,
                            hoverDate = hoverDate,
                            today = today,
                            minDate = minDate,
                            maxDate = maxDate,
                            disabledDate = disabledDate,
                            maxSpanDays = maxSpanDays,
                            onClick = onDateClick,
                            onHover = onDateHover,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RangeDayCell(
    cell: RangeCell,
    value: DateRange,
    hoverDate: LocalDate?,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    maxSpanDays: Int?,
    onClick: (LocalDate) -> Unit,
    onHover: (LocalDate?) -> Unit,
) {
    val tokens = PaletteTheme.componentThemes.dateTime
    val date = cell.date
    val enabled = date != null &&
        !isDateDisabled(date, minDate, maxDate, disabledDate)

    if (date == null) {
        // 月外占位：仍占位以保持网格对齐
        Box(modifier = Modifier.size(tokens.calendarCellSize))
        return
    }

    // 判定本单元格在区间中的角色
    val isStart = value.start != null && date == value.start
    val isEnd = value.end != null && date == value.end
    val isToday = date == today

    // 计算 hover 预览下的有效区间（用于 band 渲染）
    val effectiveRange = remember(value, hoverDate, maxSpanDays) {
        computeEffectiveRange(value, hoverDate, maxSpanDays)
    }
    val isInBand = !isStart && !isEnd &&
        effectiveRange.start != null && effectiveRange.end != null &&
        date > effectiveRange.start && date < effectiveRange.end
    val isInHoverBand = isInBand && value.isPartial && hoverDate != null

    // 文字色：cap 反白；禁用灰显；其余正常
    val textColor = when {
        isStart || isEnd -> tokens.calendarRangeCapTextColor
        !enabled || !cell.isCurrentMonth -> tokens.calendarDisabledColor
        else -> tokens.calendarDayColor
    }

    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    if (hovered && enabled) {
        // 鼠标进入时上报 hover；离开由父级清空
        onHover(date)
    }

    Box(
        modifier = Modifier
            .size(tokens.calendarCellSize)
            .hoverable(interactionSource),
        contentAlignment = Alignment.Center,
    ) {
        // 层 1：band 背景（覆盖整个单元格宽度，相邻 band 拼接成连续带）
        val bandModifier = when {
            isStart || isEnd -> Modifier
                .fillMaxWidth()
                .background(tokens.calendarRangeBandColor)
            isInHoverBand -> Modifier
                .fillMaxWidth()
                .background(tokens.calendarRangeHoverColor)
            isInBand -> Modifier
                .fillMaxWidth()
                .background(tokens.calendarRangeBandColor)
            else -> Modifier
        }

        // 层 2：cap（起止端帽实心 pill）
        val capModifier = when {
            isStart -> Modifier
                .size(tokens.calendarCellSize)
                .clip(RoundedCornerShape(topStart = CapCornerRadius, bottomStart = CapCornerRadius))
                .background(tokens.calendarRangeStartCapColor)
            isEnd -> Modifier
                .size(tokens.calendarCellSize)
                .clip(RoundedCornerShape(topEnd = CapCornerRadius, bottomEnd = CapCornerRadius))
                .background(tokens.calendarRangeEndCapColor)
            isToday && enabled -> Modifier
                .size(tokens.calendarSelectedCircleSize)
                .border(
                    tokens.calendarTodayBorderWidth,
                    tokens.calendarTodayBorderColor,
                    androidx.compose.foundation.shape.CircleShape,
                )
            else -> Modifier.size(tokens.calendarSelectedCircleSize)
        }

        // 层 3：文字（最上层）
        Box(
            modifier = Modifier.then(bandModifier).then(capModifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = cell.day.toString(),
                color = textColor,
                style = tokens.calendarDayTextStyle,
            )
        }

        // 点击层
        if (enabled && cell.isCurrentMonth) {
            Box(
                modifier = Modifier
                    .size(tokens.calendarCellSize)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        onClick(date)
                    },
            )
        }
    }
}

/**
 * 计算用于 band 渲染的有效区间：
 * - 完整区间：直接用 value
 * - 部分区间（仅起点）+ hover：用 [clampHoverEnd] 钳制后的 hover 作为临时终点
 * - 其他：返回原 value（band 不渲染）
 */
private fun computeEffectiveRange(value: DateRange, hoverDate: LocalDate?, maxSpanDays: Int?): DateRange {
    if (value.isComplete) return value
    if (value.isPartial && hoverDate != null) {
        val clamped = clampHoverEnd(value.start, hoverDate, maxSpanDays)
        return DateRange(start = value.start, end = clamped)
    }
    return value
}

private fun computeRangeGrid(year: Int, month: Int): List<List<RangeCell>> {
    val firstDay = LocalDate(year, month, 1)
    val daysInMonth = firstDay.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
    val firstDayOfWeekOffset = (dayOfWeekIso(firstDay.dayOfWeek) - 1 + 7) % 7

    val totalCells = firstDayOfWeekOffset + daysInMonth
    val totalRows = (totalCells + 6) / 7

    val cells = mutableListOf<List<RangeCell>>()
    var dayCounter = 1

    for (week in 0 until totalRows) {
        val row = mutableListOf<RangeCell>()
        for (dayOfWeek in 0 until 7) {
            val cellIndex = week * 7 + dayOfWeek
            if (cellIndex < firstDayOfWeekOffset || dayCounter > daysInMonth) {
                row.add(RangeCell(day = 0, date = null, isCurrentMonth = false))
            } else {
                val date = LocalDate(year, month, dayCounter)
                row.add(RangeCell(day = dayCounter, date = date, isCurrentMonth = true))
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
