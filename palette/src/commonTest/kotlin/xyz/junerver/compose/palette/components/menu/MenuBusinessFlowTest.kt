package xyz.junerver.compose.palette.components.menu

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MenuBusinessFlowTest {
    @Test
    fun navigationMenuFlow_shouldKeepExistingEnabledSelection() {
        val items =
            listOf(
                MenuItem(key = "overview", label = "Overview"),
                MenuItem(key = "settings", label = "Settings"),
                MenuItem(key = "audit", label = "Audit", disabled = true),
            )

        val selected = resolveMenuSelection(items, selectedKey = "settings")

        assertEquals("settings", selected)
    }

    @Test
    fun navigationMenuFlow_shouldSkipDisabledRequestedSelection() {
        val items =
            listOf(
                MenuItem(key = "overview", label = "Overview"),
                MenuItem(key = "settings", label = "Settings", disabled = true),
                MenuItem(key = "members", label = "Members"),
            )

        val selected = resolveMenuSelection(items, selectedKey = "settings")

        assertEquals("overview", selected)
    }

    @Test
    fun navigationMenuFlow_shouldReturnNullWhenNoAvailableEntry() {
        val items =
            listOf(
                MenuItem(key = "overview", label = "Overview", disabled = true),
                MenuItem(key = "settings", label = "Settings", disabled = true),
            )

        assertNull(resolveMenuSelection(items, selectedKey = null))
    }
}
