package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownSelectionTest {

    @Test
    fun length_isDifferenceBetweenEndAndStart() {
        assertEquals(5, MarkdownSelection(2, 7).length)
        assertEquals(0, MarkdownSelection(4, 4).length)
    }

    @Test
    fun empty_isZeroZero() {
        assertEquals(0, MarkdownSelection.Empty.start)
        assertEquals(0, MarkdownSelection.Empty.end)
    }

    @Test
    fun markdownEditResult_holdsTextAndSelection() {
        val r = MarkdownEditResult("new text", MarkdownSelection(0, 3))
        assertEquals("new text", r.text)
        assertEquals(MarkdownSelection(0, 3), r.selection)
    }

    @Test
    fun minMax_areReorderedWhenStartExceedsEnd() {
        val reversed = MarkdownSelection(7, 2)
        assertEquals(2, reversed.min)
        assertEquals(7, reversed.max)
        assertEquals(5, reversed.length)
    }
}
