package xyz.junerver.compose.palette.components.virtuallist

import kotlin.test.Test
import kotlin.test.assertEquals

class VirtualListLogicTest {
    @Test
    fun calculateVisibleRange_shouldIncludeOverscan() {
        val range =
            calculateVisibleRange(
                scrollOffsetPx = 120,
                viewportHeightPx = 200,
                itemHeightPx = 40,
                totalItems = 100,
                overscan = 1,
            )

        assertEquals(2, range.startIndex)
        assertEquals(9, range.endIndex)
    }

    @Test
    fun calculateVisibleRange_whenNoItems_shouldReturnEmptyRange() {
        val range =
            calculateVisibleRange(
                scrollOffsetPx = 0,
                viewportHeightPx = 200,
                itemHeightPx = 40,
                totalItems = 0,
                overscan = 1,
            )

        assertEquals(0, range.startIndex)
        assertEquals(-1, range.endIndex)
    }

    @Test
    fun totalHeightPx_shouldMultiplyItemHeight() {
        assertEquals(4000, totalHeightPx(totalItems = 100, itemHeightPx = 40))
    }
}
