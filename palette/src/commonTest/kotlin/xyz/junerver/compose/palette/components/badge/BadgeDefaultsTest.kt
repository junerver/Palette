package xyz.junerver.compose.palette.components.badge

import xyz.junerver.compose.palette.core.spec.ComponentSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BadgeDefaultsTest {
    @Test
    fun badgeDefaults_shouldMapComponentSizesToExpectedDotSizes() {
        assertEquals(BadgeDefaults.Size, BadgeDefaults.size(ComponentSize.Medium))
        assertTrue(BadgeDefaults.size(ComponentSize.Small) < BadgeDefaults.size(ComponentSize.Medium))
        assertTrue(BadgeDefaults.size(ComponentSize.Medium) < BadgeDefaults.size(ComponentSize.Large))
    }
}

