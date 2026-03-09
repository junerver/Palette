package xyz.junerver.compose.palette.components.menu

import kotlin.test.Test
import kotlin.test.assertEquals

class MenuLogicTest {
    @Test
    fun resolveMenuSelection_whenRequestedExists_shouldReturnRequested() {
        val items =
            listOf(
                MenuItem(key = "home", label = "Home"),
                MenuItem(key = "docs", label = "Docs"),
            )

        val selected = resolveMenuSelection(items, selectedKey = "docs")

        assertEquals("docs", selected)
    }

    @Test
    fun resolveMenuSelection_whenRequestedMissing_shouldFallbackFirstEnabled() {
        val items =
            listOf(
                MenuItem(key = "home", label = "Home", disabled = true),
                MenuItem(key = "docs", label = "Docs"),
                MenuItem(key = "about", label = "About"),
            )

        val selected = resolveMenuSelection(items, selectedKey = "missing")

        assertEquals("docs", selected)
    }

    @Test
    fun resolveMenuSelection_whenNoEnabled_shouldReturnNull() {
        val items =
            listOf(
                MenuItem(key = "home", label = "Home", disabled = true),
                MenuItem(key = "docs", label = "Docs", disabled = true),
            )

        val selected = resolveMenuSelection(items, selectedKey = "docs")

        assertEquals(null, selected)
    }
}
