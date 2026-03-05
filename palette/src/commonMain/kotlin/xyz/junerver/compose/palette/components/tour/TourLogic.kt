package xyz.junerver.compose.palette.components.tour

data class TourStep(
    val id: String,
    val title: String,
    val description: String? = null,
)

fun resolveInitialStepIndex(
    steps: List<TourStep>,
    startStepId: String?,
): Int {
    if (steps.isEmpty()) return 0
    if (startStepId.isNullOrBlank()) return 0
    return steps.indexOfFirst { it.id == startStepId }.takeIf { it >= 0 } ?: 0
}

fun nextTourIndex(
    current: Int,
    total: Int,
): Int {
    if (total <= 0) return 0
    if (current >= total - 1) return total - 1
    return current + 1
}

fun previousTourIndex(current: Int): Int = if (current <= 0) 0 else current - 1

fun isTourCompleted(
    current: Int,
    total: Int,
    finished: Boolean,
): Boolean {
    if (!finished || total <= 0) return false
    return current >= total - 1
}
