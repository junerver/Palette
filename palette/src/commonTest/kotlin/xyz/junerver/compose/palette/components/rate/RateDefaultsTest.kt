package xyz.junerver.compose.palette.components.rate

import xyz.junerver.compose.palette.core.spec.ComponentSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RateDefaultsTest {
    @Test
    fun rateDefaults_shouldMapComponentSizesToExpectedStarSizes() {
        assertEquals(RateDefaults.StarSize, RateDefaults.starSize(ComponentSize.Medium))
        assertTrue(RateDefaults.starSize(ComponentSize.Small) < RateDefaults.starSize(ComponentSize.Medium))
        assertTrue(RateDefaults.starSize(ComponentSize.Medium) < RateDefaults.starSize(ComponentSize.Large))
    }

    @Test
    fun rateDefaults_shouldExposeExpectedDisabledAlpha() {
        assertEquals(0.5f, RateDefaults.DisabledAlpha)
    }
}
