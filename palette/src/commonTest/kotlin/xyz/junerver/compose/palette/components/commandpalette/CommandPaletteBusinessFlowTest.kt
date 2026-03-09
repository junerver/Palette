package xyz.junerver.compose.palette.components.commandpalette

import kotlin.test.Test
import kotlin.test.assertEquals

class CommandPaletteBusinessFlowTest {
    private val actions =
        listOf(
            CommandAction(
                id = "open-billing",
                title = "Open Billing",
                subtitle = "Finance workspace",
                keywords = listOf("invoice", "payment"),
            ),
            CommandAction(
                id = "open-logs",
                title = "Open Logs",
                subtitle = "Diagnostics",
            ),
        )

    @Test
    fun commandSearchFlow_shouldMatchSubtitleAndKeyword() {
        assertEquals(listOf("open-billing"), filterCommands(actions, "finance").map { it.id })
        assertEquals(listOf("open-billing"), filterCommands(actions, "invoice").map { it.id })
    }

    @Test
    fun commandSearchFlow_shouldClampOutOfRangeHighlightBeforeMoving() {
        val next = moveHighlight(currentIndex = 99, offset = 1, size = 2)

        assertEquals(0, next)
    }

    @Test
    fun commandSearchFlow_shouldReturnHighlightedActionWhenIndexValid() {
        val picked = pickHighlightedCommand(actions, 1)

        assertEquals("open-logs", picked?.id)
    }
}
