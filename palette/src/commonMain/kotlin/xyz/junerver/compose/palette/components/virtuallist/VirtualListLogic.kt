package xyz.junerver.compose.palette.components.virtuallist

data class VisibleRange(
    val startIndex: Int,
    val endIndex: Int,
)

fun calculateVisibleRange(
    scrollOffsetPx: Int,
    viewportHeightPx: Int,
    itemHeightPx: Int,
    totalItems: Int,
    overscan: Int = 1,
): VisibleRange {
    if (totalItems <= 0 || itemHeightPx <= 0 || viewportHeightPx <= 0) {
        return VisibleRange(startIndex = 0, endIndex = -1)
    }

    val firstVisible = if (scrollOffsetPx > 0) scrollOffsetPx / itemHeightPx else 0
    val visibleCount = (viewportHeightPx / itemHeightPx) + 1
    var start = firstVisible - overscan
    if (start < 0) {
        start = 0
    }
    val maxIndex = totalItems - 1
    var end = firstVisible + visibleCount - 1 + overscan
    if (end > maxIndex) {
        end = maxIndex
    }
    return VisibleRange(startIndex = start, endIndex = end)
}

fun totalHeightPx(
    totalItems: Int,
    itemHeightPx: Int,
): Int {
    val nonNegativeItems = if (totalItems >= 0) totalItems else 0
    val nonNegativeItemHeight = if (itemHeightPx >= 0) itemHeightPx else 0
    return nonNegativeItems * nonNegativeItemHeight
}
