package xyz.junerver.compose.palette.components.datepicker

import kotlinx.datetime.LocalDate

fun parseDateOrNull(text: String): LocalDate? = runCatching {
    LocalDate.parse(text)
}.getOrNull()

fun isDateWithinRange(
    date: LocalDate,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
): Boolean {
    if (minDate != null && date < minDate) return false
    if (maxDate != null && date > maxDate) return false
    return true
}
