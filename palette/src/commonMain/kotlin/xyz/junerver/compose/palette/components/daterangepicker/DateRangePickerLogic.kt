package xyz.junerver.compose.palette.components.daterangepicker

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.until

/**
 * 规范化区间：当 end < start 且 [allowSwap] 时自动交换两端（Arco 风格，默认开启）。
 * 任意一端为空时原样返回。
 */
fun normalizeRange(range: DateRange, allowSwap: Boolean = true): DateRange {
    val start = range.start ?: return range
    val end = range.end ?: return range
    if (!allowSwap) return range
    return if (end < start) DateRange(start = end, end = start) else range
}

/**
 * 判断单个日期是否被禁用。规则取并集：min/max 边界 + 调用方回调 [disabledDate]。
 * 这是前端主流组件库（Ant/Element/Naive/Arco）事实标准的 disabledDate API。
 */
fun isDateDisabled(
    date: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
): Boolean {
    if (minDate != null && date < minDate) return true
    if (maxDate != null && date > maxDate) return true
    if (disabledDate != null && disabledDate(date)) return true
    return false
}

/**
 * 判断完整区间是否在允许的最大跨度（自然日）内。[maxSpanDays] 为 null 表示不限。
 */
fun isWithinMaxSpan(start: LocalDate, end: LocalDate, maxSpanDays: Int?): Boolean {
    if (maxSpanDays == null) return true
    if (maxSpanDays < 0) return false
    val days = start.until(end, DateTimeUnit.DAY)
    return days in 0..maxSpanDays
}

/**
 * hover 预览终点钳制：当 hover 超出最大跨度时，把预览终点钳制到 start + maxSpanDays，
 * 避免预览 band 视觉溢出可选范围。无限制或起点为空时原样返回。
 */
fun clampHoverEnd(start: LocalDate?, hover: LocalDate, maxSpanDays: Int?): LocalDate {
    if (start == null || maxSpanDays == null) return hover
    return if (hover < start) {
        // hover 在起点之前：预览仅到起点（避免反向 band 误导）
        start
    } else {
        val maxEnd = start.plus(maxSpanDays, DateTimeUnit.DAY)
        if (hover > maxEnd) maxEnd else hover
    }
}

/**
 * 判断 hover 落点是否可作为有效终点提交（受最大跨度约束）。
 */
fun isHoverClickable(start: LocalDate?, hover: LocalDate, maxSpanDays: Int?): Boolean {
    if (start == null) return true
    if (maxSpanDays == null) return true
    return isWithinMaxSpan(start, hover, maxSpanDays)
}

/**
 * 将区间两端钳制到 [minDate]/[maxDate] 边界内。
 * 用于 preset 应用：超出可选范围的 preset 值会被裁剪到合法边界。
 */
fun clampRangeToBounds(range: DateRange, minDate: LocalDate?, maxDate: LocalDate?): DateRange {
    val start = range.start?.let { s ->
        when {
            minDate != null && s < minDate -> minDate
            maxDate != null && s > maxDate -> maxDate
            else -> s
        }
    }
    val end = range.end?.let { e ->
        when {
            minDate != null && e < minDate -> minDate
            maxDate != null && e > maxDate -> maxDate
            else -> e
        }
    }
    return DateRange(start = start, end = end)
}

private const val RANGE_SEPARATOR = " - "

/**
 * 格式化区间为显示文本。
 * - 空：空串（占位符负责）
 * - 仅起点："<start>"
 * - 完整："<start> - <end>"，启用时间则附加 HH:mm
 */
fun formatDateRange(range: DateRange, startTime: LocalTime?, endTime: LocalTime?): String {
    val start = range.start
    val end = range.end
    return when {
        start == null -> ""
        end == null -> formatEndpoint(start, startTime)
        else -> "${formatEndpoint(start, startTime)}$RANGE_SEPARATOR${formatEndpoint(end, endTime)}"
    }
}

private fun formatEndpoint(date: LocalDate, time: LocalTime?): String {
    val datePart = date.toString()
    return if (time == null) datePart else "$datePart ${formatHm(time)}"
}

/**
 * 解析区间文本为 [DateRange]。容错：两端格式必须都合法，否则返回 null。
 * 不在此处校验顺序与跨度，留给 [normalizeRange] / [isWithinMaxSpan] 处理。
 */
fun parseDateRangeOrNull(text: String, expectTime: Boolean): DateRange? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return DateRange.Empty
    val parts = trimmed.split(RANGE_SEPARATOR)
    if (parts.size != 2) return null
    val start = parseEndpointOrNull(parts[0].trim(), expectTime) ?: return null
    val end = parseEndpointOrNull(parts[1].trim(), expectTime) ?: return null
    return DateRange(start = start.first, end = end.first)
}

private fun parseEndpointOrNull(text: String, expectTime: Boolean): Pair<LocalDate, LocalTime?>? {
    val tokens = text.split(" ")
    return if (expectTime) {
        if (tokens.size != 2) return null
        val date = runCatching { LocalDate.parse(tokens[0]) }.getOrNull() ?: return null
        val time = parseHmOrNull(tokens[1]) ?: return null
        date to time
    } else {
        if (tokens.size != 1) return null
        val date = runCatching { LocalDate.parse(tokens[0]) }.getOrNull() ?: return null
        date to null
    }
}

internal fun parseHmOrNull(value: String): LocalTime? {
    val match = Regex("""^(\d{2}):(\d{2})$""").matchEntire(value) ?: return null
    val hour = match.groupValues[1].toIntOrNull() ?: return null
    val minute = match.groupValues[2].toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return LocalTime(hour, minute)
}

internal fun formatHm(time: LocalTime): String {
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}
