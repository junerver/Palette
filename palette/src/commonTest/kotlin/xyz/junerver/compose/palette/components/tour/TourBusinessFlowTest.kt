package xyz.junerver.compose.palette.components.tour

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TourBusinessFlowTest {
    private val steps =
        listOf(
            TourStep(id = "welcome", title = "Welcome"),
            TourStep(id = "filters", title = "Filters"),
            TourStep(id = "done", title = "Done"),
        )

    @Test
    fun guidedTourFlow_shouldStartFromRequestedStepWhenItExists() {
        val index = resolveInitialStepIndex(steps = steps, startStepId = "filters")

        assertEquals(1, index)
    }

    @Test
    fun guidedTourFlow_shouldFallbackToFirstStepWhenRequestedStepMissing() {
        val index = resolveInitialStepIndex(steps = steps, startStepId = "missing")

        assertEquals(0, index)
    }

    @Test
    fun guidedTourFlow_shouldStopAdvancingAtLastStep() {
        val index = nextTourIndex(current = 2, total = steps.size)

        assertEquals(2, index)
    }

    @Test
    fun guidedTourFlow_shouldOnlyCompleteAfterLastStepAndFinishSignal() {
        assertFalse(isTourCompleted(current = 1, total = steps.size, finished = true))
        assertTrue(isTourCompleted(current = 2, total = steps.size, finished = true))
        assertFalse(isTourCompleted(current = 2, total = steps.size, finished = false))
    }
}
