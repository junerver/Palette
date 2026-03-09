package xyz.junerver.compose.palette.components.virtuallist

import kotlin.test.Test
import kotlin.test.assertEquals

class VirtualListBusinessFlowTest {
    @Test
    fun longListFlow_shouldClampVisibleRangeNearBottom() {
        val range =
            calculateVisibleRange(
                scrollOffsetPx = 3_960,
                viewportHeightPx = 200,
                itemHeightPx = 40,
                totalItems = 100,
                overscan = 2,
            )

        assertEquals(97, range.startIndex)
        assertEquals(99, range.endIndex)
    }

    @Test
    fun longListFlow_shouldNeverProduceNegativeHeight() {
        assertEquals(0, totalHeightPx(totalItems = -100, itemHeightPx = 40))
        assertEquals(0, totalHeightPx(totalItems = 100, itemHeightPx = -40))
    }
}
