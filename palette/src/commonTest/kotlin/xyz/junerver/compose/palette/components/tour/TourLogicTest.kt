package xyz.junerver.compose.palette.components.tour

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TourLogicTest {
    private val steps =
        listOf(
            TourStep(id = "intro", title = "Intro"),
            TourStep(id = "action", title = "Action"),
            TourStep(id = "done", title = "Done"),
        )

    @Test
    fun resolveInitialStepIndex_whenMissing_shouldReturnZero() {
        assertEquals(0, resolveInitialStepIndex(steps, null))
    }

    @Test
    fun resolveInitialStepIndex_whenFound_shouldReturnMatchedIndex() {
        assertEquals(1, resolveInitialStepIndex(steps, "action"))
    }

    @Test
    fun nextTourIndex_whenReachedLast_shouldKeepLast() {
        assertEquals(2, nextTourIndex(current = 2, total = 3))
    }

    @Test
    fun previousTourIndex_whenReachedFirst_shouldKeepFirst() {
        assertEquals(0, previousTourIndex(current = 0))
    }

    @Test
    fun isTourCompleted_whenAtLastAndFinished_shouldReturnTrue() {
        assertTrue(isTourCompleted(current = 2, total = 3, finished = true))
        assertFalse(isTourCompleted(current = 1, total = 3, finished = true))
    }
}
