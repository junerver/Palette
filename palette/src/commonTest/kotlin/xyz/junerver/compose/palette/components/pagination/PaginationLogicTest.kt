package xyz.junerver.compose.palette.components.pagination

import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationLogicTest {
    @Test
    fun calculatePageNumbers_whenTotalLessThanOrEqualSeven_shouldReturnAllPages() {
        val pages = calculatePageNumbers(current = 3, total = 7)

        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), pages)
    }

    @Test
    fun calculatePageNumbers_whenNearStart_shouldReturnLeadingPagesAndTailEllipsis() {
        val pages = calculatePageNumbers(current = 2, total = 12)

        assertEquals(listOf(1, 2, 3, 4, 5, -1, 12), pages)
    }

    @Test
    fun calculatePageNumbers_whenInMiddle_shouldReturnBothSideEllipsis() {
        val pages = calculatePageNumbers(current = 6, total = 12)

        assertEquals(listOf(1, -1, 5, 6, 7, -1, 12), pages)
    }

    @Test
    fun calculatePageNumbers_whenNearEnd_shouldReturnHeadEllipsisAndTrailingPages() {
        val pages = calculatePageNumbers(current = 10, total = 12)

        assertEquals(listOf(1, -1, 8, 9, 10, 11, 12), pages)
    }
}
