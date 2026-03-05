package xyz.junerver.compose.palette.components.timepicker

import kotlinx.datetime.LocalTime

fun parseTimeOrNull(text: String): LocalTime? {
    val match = Regex("""^(\d{2}):(\d{2})$""").matchEntire(text) ?: return null
    val hour = match.groupValues[1].toIntOrNull() ?: return null
    val minute = match.groupValues[2].toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return LocalTime(hour, minute)
}

fun formatTime(time: LocalTime): String {
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

fun isTimeStepAligned(time: LocalTime, minuteStep: Int): Boolean {
    if (minuteStep <= 0) return true
    return time.minute % minuteStep == 0
}
