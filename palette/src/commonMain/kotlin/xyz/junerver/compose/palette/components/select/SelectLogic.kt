package xyz.junerver.compose.palette.components.select

data class SelectOption<T>(
    val label: String,
    val value: T,
    val disabled: Boolean = false,
)

fun <T> resolveSelectedLabel(
    options: List<SelectOption<T>>,
    value: T?,
    placeholder: String,
): String {
    if (value == null) return placeholder
    return options.firstOrNull { it.value == value }?.label ?: placeholder
}

fun <T> filterSelectOptions(
    options: List<SelectOption<T>>,
    query: String,
): List<SelectOption<T>> {
    val normalized = query.trim()
    if (normalized.isEmpty()) return options
    return options.filter { it.label.contains(normalized, ignoreCase = true) }
}

fun shouldToggleExpanded(
    currentExpanded: Boolean,
    enabled: Boolean,
): Boolean {
    if (!enabled) return false
    return !currentExpanded
}

fun <T> isOptionSelectable(
    option: SelectOption<T>,
    enabled: Boolean,
): Boolean = enabled && !option.disabled

fun <T> toggleMultiSelection(
    current: List<T>,
    value: T,
    maxSelection: Int = Int.MAX_VALUE,
): List<T> {
    if (current.contains(value)) return current.filterNot { it == value }
    if (maxSelection > 0 && current.size >= maxSelection) return current
    return current + value
}

fun <T> filterSelectedOptions(
    options: List<SelectOption<T>>,
    selectedValues: List<T>,
): List<SelectOption<T>> {
    if (selectedValues.isEmpty()) return emptyList()
    val lookup = options.associateBy { it.value }
    return selectedValues.mapNotNull { lookup[it] }
}
