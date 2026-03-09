package xyz.junerver.compose.palette.core.spec

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComponentSpecTest {
    @Test
    fun componentSize_shouldKeepUsableVisualHierarchy() {
        assertTrue(ComponentSize.Small.height < ComponentSize.Medium.height)
        assertTrue(ComponentSize.Medium.fontSize <= ComponentSize.Large.fontSize)
        assertTrue(ComponentSize.Small.cornerRadius < ComponentSize.Large.cornerRadius)
    }

    @Test
    fun componentState_shouldExposeStableEnumOrder() {
        assertEquals(
            listOf("Enabled", "Disabled", "Readonly", "Loading"),
            ComponentState.entries.map { it.name },
        )
    }

    @Test
    fun componentStatus_shouldCoverExpectedBusinessStates() {
        assertEquals(
            listOf("Default", "Success", "Warning", "Error"),
            ComponentStatus.entries.map { it.name },
        )
    }
}
