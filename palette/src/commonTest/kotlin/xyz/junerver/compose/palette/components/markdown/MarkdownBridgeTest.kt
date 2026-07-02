package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.palette.markdown.MarkdownHistoryEntry
import xyz.junerver.compose.palette.markdown.MarkdownSelection
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownBridgeTest {

    @Test
    fun textRange_toMarkdownSelection_preservesStartEnd() {
        assertEquals(MarkdownSelection(2, 7), TextRange(2, 7).toMarkdownSelection())
        assertEquals(MarkdownSelection(5, 5), TextRange(5, 5).toMarkdownSelection())
    }

    @Test
    fun markdownSelection_toTextRange_preservesStartEnd() {
        assertEquals(TextRange(3, 9), MarkdownSelection(3, 9).toTextRange())
    }

    @Test
    fun roundTrip_textRange_isStable() {
        val original = TextRange(4, 10)
        val roundTrip = original.toMarkdownSelection().toTextRange()
        assertEquals(original, roundTrip)
    }

    @Test
    fun textFieldValue_toCoreEntry_preservesTextAndSelection() {
        val tfv = TextFieldValue("hello", TextRange(1, 3))
        val entry = tfv.toCoreEntry()
        assertEquals("hello", entry.text)
        assertEquals(MarkdownSelection(1, 3), entry.selection)
    }

    @Test
    fun coreEntry_toTextFieldValue_preservesTextAndSelection() {
        val entry = MarkdownHistoryEntry("world", MarkdownSelection(0, 2))
        val tfv = entry.toTextFieldValue()
        assertEquals("world", tfv.text)
        assertEquals(TextRange(0, 2), tfv.selection)
    }
}
