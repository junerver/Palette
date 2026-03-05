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

    val firstVisible = (scrollOffsetPx / itemHeightPx).coerceAtLeast(0)
    val visibleCount = (viewportHeightPx / itemHeightPx) + 1
    val start = (firstVisible - overscan).coerceAtLeast(0)
    val end = (firstVisible + visibleCount - 1 + overscan).coerceAtMost(totalItems - 1)
    return VisibleRange(startIndex = start, endIndex = end)
}

fun totalHeightPx(
    totalItems: Int,
    itemHeightPx: Int,
): Int = (totalItems.coerceAtLeast(0)) * (itemHeightPx.coerceAtLeast(0))
