package xyz.junerver.compose.palette.components.mentions

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MentionsLogicTest {
    @Test
    fun replaceMentionQueryWithOption_placesCursorAfterWholeMention() {
        val editorValue = TextFieldValue(
            text = "hello @Al world",
            selection = TextRange(9),
        )

        val result = replaceMentionQueryWithOption(
            editorValue = editorValue,
            mentionTriggerIndex = 6,
            mentionQuery = "@Al",
            prefix = "@",
            option = MentionsOption(value = "alice", label = "Alice"),
        )

        assertEquals("hello @Alice  world", result.text)
        assertEquals(TextRange(13), result.selection)
    }

    @Test
    fun findMentionRanges_detectsPrefixTokensUntilWhitespace() {
        val ranges = findMentionRanges("hello @Alice and @Bob", "@")

        assertEquals(
            listOf(
                MentionRange(start = 6, end = 12),
                MentionRange(start = 17, end = 21),
            ),
            ranges,
        )
    }

    @Test
    fun replaceEditedMentionWithWholeDeletion_deletesMentionAndTrailingSpaceWhenBackspacingAfterMention() {
        val oldValue = TextFieldValue(
            text = "hello @Alice world",
            selection = TextRange(13),
        )
        val requestedValue = TextFieldValue(
            text = "hello @Aliceworld",
            selection = TextRange(12),
        )

        val result = replaceEditedMentionWithWholeDeletion(oldValue, requestedValue, "@")

        assertEquals("hello world", result?.text)
        assertEquals(TextRange(6), result?.selection)
    }

    @Test
    fun replaceEditedMentionWithWholeDeletion_deletesWholeMentionWhenBackspacingInsideMention() {
        val oldValue = TextFieldValue(
            text = "hello @Alice world",
            selection = TextRange(10),
        )
        val requestedValue = TextFieldValue(
            text = "hello @Alce world",
            selection = TextRange(9),
        )

        val result = replaceEditedMentionWithWholeDeletion(oldValue, requestedValue, "@")

        assertEquals("hello  world", result?.text)
        assertEquals(TextRange(6), result?.selection)
    }

    @Test
    fun replaceEditedMentionWithWholeDeletion_deletesWholeMentionWhenForwardDeletingInsideMention() {
        val oldValue = TextFieldValue(
            text = "hello @Alice world",
            selection = TextRange(6),
        )
        val requestedValue = TextFieldValue(
            text = "hello Alice world",
            selection = TextRange(6),
        )

        val result = replaceEditedMentionWithWholeDeletion(oldValue, requestedValue, "@")

        assertEquals("hello  world", result?.text)
        assertEquals(TextRange(6), result?.selection)
    }

    @Test
    fun replaceEditedMentionWithWholeDeletion_keepsOrdinaryBackspaceUntouched() {
        val oldValue = TextFieldValue(
            text = "hello @Alice world",
            selection = TextRange(4),
        )
        val requestedValue = TextFieldValue(
            text = "helo @Alice world",
            selection = TextRange(3),
        )

        val result = replaceEditedMentionWithWholeDeletion(oldValue, requestedValue, "@")

        assertNull(result)
    }

    @Test
    fun mentionsHighlightTransformation_addsBackgroundSpanWhenEnabled() {
        val transformation = MentionsHighlightTransformation(
            prefix = "@",
            enabled = true,
            highlightColor = Color.Red,
        )

        val result = transformation.filter(AnnotatedString("hello @Alice")).text

        assertEquals(1, result.spanStyles.size)
        assertEquals(6, result.spanStyles.single().start)
        assertEquals(12, result.spanStyles.single().end)
        assertEquals(Color.Red, result.spanStyles.single().item.background)
    }

    @Test
    fun mentionsHighlightTransformation_keepsTextUnstyledWhenDisabled() {
        val transformation = MentionsHighlightTransformation(
            prefix = "@",
            enabled = false,
            highlightColor = Color.Red,
        )

        val result = transformation.filter(AnnotatedString("hello @Alice")).text

        assertEquals(0, result.spanStyles.size)
    }
}
