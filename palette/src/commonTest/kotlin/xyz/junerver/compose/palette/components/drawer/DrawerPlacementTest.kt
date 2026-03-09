package xyz.junerver.compose.palette.components.drawer

import kotlin.test.Test
import kotlin.test.assertEquals

class DrawerPlacementTest {
    @Test
    fun drawerPlacement_shouldExposeStartAndEndModes() {
        assertEquals(
            listOf("Start", "End"),
            DrawerPlacement.entries.map { it.name },
        )
    }
}
