package xyz.junerver.compose.palette.components.table

import kotlin.test.Test
import kotlin.test.assertEquals

class TableScrollBehaviorTest {
    @Test
    fun tableScrollBehavior_shouldExposeSupportedModes() {
        assertEquals(
            listOf("Scrollable", "Embedded", "FixedHeight"),
            TableScrollBehavior.entries.map { it.name },
        )
    }
}
