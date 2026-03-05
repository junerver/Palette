package xyz.junerver.compose.palette.components.datetimerange

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class DateTimeRange(
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime,
)

fun parseDateTimeRangeOrNull(text: String): DateTimeRange? {
    val parts = text.split(" - ")
    if (parts.size != 2) return null
    val start = parseDateTimeOrNull(parts[0]) ?: return null
    val end = parseDateTimeOrNull(parts[1]) ?: return null
    if (!isValidRangeOrder(start.first, start.second, end.first, end.second)) return null
    return DateTimeRange(
        startDate = start.first,
        startTime = start.second,
        endDate = end.first,
        endTime = end.second,
    )
}

fun isValidRangeOrder(
    startDate: LocalDate,
    startTime: LocalTime,
    endDate: LocalDate,
    endTime: LocalTime,
): Boolean {
    if (startDate < endDate) return true
    if (startDate > endDate) return false
    return startTime <= endTime
}

fun formatDateTimeRange(range: DateTimeRange): String {
    return "${range.startDate} ${formatHm(range.startTime)} - ${range.endDate} ${formatHm(range.endTime)}"
}

private fun parseDateTimeOrNull(text: String): Pair<LocalDate, LocalTime>? {
    val values = text.trim().split(" ")
    if (values.size != 2) return null
    val date = runCatching { LocalDate.parse(values[0]) }.getOrNull() ?: return null
    val time = parseHmOrNull(values[1]) ?: return null
    return date to time
}

private fun parseHmOrNull(value: String): LocalTime? {
    val match = Regex("""^(\d{2}):(\d{2})$""").matchEntire(value) ?: return null
    val hour = match.groupValues[1].toIntOrNull() ?: return null
    val minute = match.groupValues[2].toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return LocalTime(hour, minute)
}

private fun formatHm(time: LocalTime): String {
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}
