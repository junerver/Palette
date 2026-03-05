package xyz.junerver.compose.palette.components.commandpalette

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandPaletteLogicTest {
    private val commands = listOf(
        CommandAction(
            id = "open-settings",
            title = "Open Settings",
            keywords = listOf("preferences"),
        ),
        CommandAction(
            id = "new-file",
            title = "New File",
            keywords = listOf("create", "document"),
        ),
        CommandAction(
            id = "search-project",
            title = "Search Project",
        ),
    )

    @Test
    fun filterCommands_whenQueryBlank_shouldReturnAll() {
        val result = filterCommands(commands, "  ")

        assertEquals(3, result.size)
    }

    @Test
    fun filterCommands_whenQueryMatchesKeyword_shouldIncludeCommand() {
        val result = filterCommands(commands, "pref")

        assertEquals(listOf("open-settings"), result.map { it.id })
    }

    @Test
    fun moveHighlight_whenOffsetOverflow_shouldWrap() {
        val next = moveHighlight(currentIndex = 2, offset = 1, size = 3)

        assertEquals(0, next)
    }

    @Test
    fun moveHighlight_whenEmpty_shouldReturnMinusOne() {
        val next = moveHighlight(currentIndex = 0, offset = 1, size = 0)

        assertEquals(-1, next)
    }

    @Test
    fun pickHighlightedCommand_whenInvalid_shouldReturnNull() {
        assertTrue(pickHighlightedCommand(commands, -1) == null)
        assertTrue(pickHighlightedCommand(commands, 99) == null)
    }
}
