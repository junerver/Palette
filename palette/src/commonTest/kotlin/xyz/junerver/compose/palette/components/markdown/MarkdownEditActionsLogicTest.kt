package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkdownEditActionsLogicTest {

    // region wrapSelection
    @Test
    fun wrapSelection_bold_noSelection_insertsEmptyMarkerAndCaretInside() {
        val r = wrapSelection("hello", TextRange(0, 0), "**")
        assertEquals("****hello", r.text)
        assertEquals(TextRange(2, 2), r.selection)
    }

    @Test
    fun wrapSelection_bold_withSelection_wrapsAndCaretAfterContent() {
        val r = wrapSelection("hello world", TextRange(0, 5), "**")
        assertEquals("**hello** world", r.text)
        assertEquals(TextRange(0, 7), r.selection) // 包裹后选区覆盖 **hello
    }

    @Test
    fun wrapSelection_bold_alreadyWrapped_unwraps() {
        val r = wrapSelection("**hi**", TextRange(0, 6), "**")
        assertEquals("hi", r.text)
        assertEquals(TextRange(0, 2), r.selection)
    }

    @Test
    fun wrapSelection_inlineCode_usesBacktick() {
        val r = wrapSelection("a b c", TextRange(2, 3), "`")
        assertEquals("a `b` c", r.text)
    }

    @Test
    fun wrapSelection_italic_distinctFromBold() {
        val r = wrapSelection("x", TextRange(0, 1), "*")
        assertEquals("*x*", r.text)
    }
    // endregion

    // region toggleLinePrefix
    @Test
    fun toggleLinePrefix_unordered_singleLine_adds() {
        val r = toggleLinePrefix("item", TextRange(0, 0), "- ")
        assertEquals("- item", r.text)
    }

    @Test
    fun toggleLinePrefix_unordered_alreadyHas_removes() {
        val r = toggleLinePrefix("- item", TextRange(0, 6), "- ")
        assertEquals("item", r.text)
    }

    @Test
    fun toggleLinePrefix_multiLine_allAdded() {
        val r = toggleLinePrefix("a\nb\nc", TextRange(0, 5), "- ")
        assertEquals("- a\n- b\n- c", r.text)
    }

    @Test
    fun toggleLinePrefix_ordered_increments() {
        val r = toggleLinePrefix("a\nb", TextRange(0, 3), "1. ", ordered = true)
        assertEquals("1. a\n2. b", r.text)
    }

    @Test
    fun toggleLinePrefix_quote_prefix() {
        val r = toggleLinePrefix("note", TextRange(0, 0), "> ")
        assertEquals("> note", r.text)
    }

    @Test
    fun toggleLinePrefix_convertsExistingListToOrdered() {
        val r = toggleLinePrefix("- a\n- b", TextRange(0, 7), "1. ", ordered = true)
        assertEquals("1. a\n2. b", r.text)
    }
    // endregion

    // region setHeadingLevel
    @Test
    fun setHeadingLevel_setsH2() {
        val r = setHeadingLevel("title", TextRange(0, 0), 2)
        assertEquals("## title", r.text)
    }

    @Test
    fun setHeadingLevel_replacesExisting() {
        val r = setHeadingLevel("# title", TextRange(0, 7), 3)
        assertEquals("### title", r.text)
    }

    @Test
    fun setHeadingLevel_zeroClears() {
        val r = setHeadingLevel("## title", TextRange(0, 8), 0)
        assertEquals("title", r.text)
    }
    // endregion

    // region toggleTaskItem
    @Test
    fun toggleTaskItem_plainLine_addsUnchecked() {
        val r = toggleTaskItem("buy milk", TextRange(0, 0))
        assertEquals("- [ ] buy milk", r.text)
    }

    @Test
    fun toggleTaskItem_uncheckedBecomesChecked() {
        val r = toggleTaskItem("- [ ] buy milk", TextRange(0, 0))
        assertEquals("- [x] buy milk", r.text)
    }

    @Test
    fun toggleTaskItem_checkedBecomesPlainList() {
        val r = toggleTaskItem("- [x] done", TextRange(0, 0))
        assertEquals("- done", r.text)
    }
    // endregion

    // region insertText
    @Test
    fun insertText_link_selectsUrlPlaceholder() {
        val r = insertText("abc", TextRange(1, 1), "[text](url)", selectInside = 7..9)
        assertEquals("a[text](url)bc", r.text)
        // 占位 url 在 snippet 索引 7..9
        assertEquals(TextRange(1 + 7, 1 + 10), r.selection)
    }

    @Test
    fun insertText_codeFence_padsWithNewlines_midLine() {
        // 在 "abcd" 中间(光标=2)插入块级代码围栏，前后应补换行
        val r = insertText("abcd", TextRange(2, 2), "```\n\n```\n")
        assertEquals("ab\n```\n\n```\n\ncd", r.text)
    }

    @Test
    fun insertText_codeFence_atLineStart_noLeadingPad() {
        val r = insertText("cd", TextRange(0, 0), "```\n\n```\n")
        assertEquals("```\n\n```\n\ncd", r.text)
    }

    @Test
    fun insertText_table_snippetHasHeaderRow() {
        assertTrue(defaultTableSnippet.contains("| Column A |"))
        assertTrue(defaultTableSnippet.contains("| --- |"))
    }

    @Test
    fun defaultCodeFence_includesLanguage() {
        assertEquals("```kotlin\n\n```\n", defaultCodeFence("kotlin"))
    }
    // endregion

    // region indent
    @Test
    fun indent_forward_addsFourSpaces() {
        val r = indent("line", TextRange(0, 0), forward = true)
        assertEquals("    line", r.text)
    }

    @Test
    fun indent_backward_removesFourSpaces() {
        val r = indent("    line", TextRange(0, 0), forward = false)
        assertEquals("line", r.text)
    }

    @Test
    fun indent_backward_removesTwoSpacesIfNoFour() {
        val r = indent("  line", TextRange(0, 0), forward = false)
        assertEquals("line", r.text)
    }

    @Test
    fun indent_multiLine_indentsAll() {
        val r = indent("a\nb", TextRange(0, 3), forward = true)
        assertEquals("    a\n    b", r.text)
    }

    @Test
    fun indent_backward_noLeadingSpace_unchanged() {
        val r = indent("line", TextRange(0, 0), forward = false)
        assertEquals("line", r.text)
    }
    // endregion

    // region continueOnEnter
    @Test
    fun continueOnEnter_unorderedList_continuesPrefix() {
        val r = continueOnEnter("- item", TextRange(6, 6))
        assertNotNull(r)
        assertEquals("- item\n- ", r.text)
        assertEquals(TextRange(9, 9), r.selection)
    }

    @Test
    fun continueOnEnter_orderedList_increments() {
        val r = continueOnEnter("1. first", TextRange(8, 8))
        assertNotNull(r)
        assertEquals("1. first\n2. ", r.text)
    }

    @Test
    fun continueOnEnter_quote_continues() {
        val r = continueOnEnter("> note", TextRange(6, 6))
        assertNotNull(r)
        assertEquals("> note\n> ", r.text)
    }

    @Test
    fun continueOnEnter_emptyListItem_clearsPrefix() {
        val r = continueOnEnter("- ", TextRange(2, 2))
        assertNotNull(r)
        assertEquals("", r.text)
    }

    @Test
    fun continueOnEnter_plainLine_returnsNull() {
        val r = continueOnEnter("just text", TextRange(9, 9))
        assertNull(r)
    }

    @Test
    fun continueOnEnter_preservesIndent() {
        val r = continueOnEnter("  - nested", TextRange(10, 10))
        assertNotNull(r)
        assertEquals("  - nested\n  - ", r.text)
    }
    // endregion

    // region currentLineRange / selectedLineRanges
    @Test
    fun currentLineRange_singleLine() {
        val (s, e) = currentLineRange("hello", TextRange(2, 2))
        assertEquals(0, s)
        assertEquals(5, e)
    }

    @Test
    fun currentLineRange_middleLine() {
        val text = "a\nbb\nccc"
        val (s, e) = currentLineRange(text, TextRange(3, 3)) // 在 "bb" 内
        assertEquals(2, s)
        assertEquals(4, e)
    }

    @Test
    fun selectedLineRanges_coversMultipleLines() {
        val text = "a\nb\nc"
        val ranges = selectedLineRanges(text, TextRange(0, 5))
        assertEquals(3, ranges.size)
    }
    // endregion
}

class MarkdownToolbarIntegrationLogicTest {
    // 模拟工具栏按钮到编辑动作的映射（与 MarkdownEditor 内 when 分支一致），
    // 验证"动作枚举 -> 纯函数 -> 文本结果"链路，无需 Compose。

    private fun applyAction(action: MarkdownToolbarAction, text: String, sel: TextRange): MarkdownEditResult {
        val tf = androidx.compose.ui.text.input.TextFieldValue(text, sel)
        return when (action) {
            MarkdownToolbarAction.Bold -> wrapSelection(tf.text, tf.selection, "**")
            MarkdownToolbarAction.Italic -> wrapSelection(tf.text, tf.selection, "*")
            MarkdownToolbarAction.Strikethrough -> wrapSelection(tf.text, tf.selection, "~~")
            MarkdownToolbarAction.InlineCode -> wrapSelection(tf.text, tf.selection, "`")
            MarkdownToolbarAction.Heading -> setHeadingLevel(tf.text, tf.selection, 1)
            MarkdownToolbarAction.UnorderedList -> toggleLinePrefix(tf.text, tf.selection, "- ")
            MarkdownToolbarAction.OrderedList -> toggleLinePrefix(tf.text, tf.selection, "1. ", ordered = true)
            MarkdownToolbarAction.TaskList -> toggleTaskItem(tf.text, tf.selection)
            MarkdownToolbarAction.Quote -> toggleLinePrefix(tf.text, tf.selection, "> ")
            MarkdownToolbarAction.Link -> insertText(tf.text, tf.selection, "[text](url)", selectInside = 7..9)
            MarkdownToolbarAction.Image -> insertText(tf.text, tf.selection, "![alt](url)", selectInside = 8..10)
            MarkdownToolbarAction.CodeBlock -> insertText(tf.text, tf.selection, defaultCodeFence())
            MarkdownToolbarAction.Table -> insertText(tf.text, tf.selection, defaultTableSnippet)
            MarkdownToolbarAction.HorizontalRule -> insertText(tf.text, tf.selection, "---\n")
        }
    }

    @Test
    fun boldAction_wrapsSelection() {
        val r = applyAction(MarkdownToolbarAction.Bold, "hello", TextRange(0, 5))
        assertEquals("**hello**", r.text)
    }

    @Test
    fun headingAction_setsH1() {
        val r = applyAction(MarkdownToolbarAction.Heading, "title", TextRange(0, 0))
        assertEquals("# title", r.text)
    }

    @Test
    fun unorderedListAction_addsBullet() {
        val r = applyAction(MarkdownToolbarAction.UnorderedList, "item", TextRange(0, 0))
        assertEquals("- item", r.text)
    }

    @Test
    fun taskListAction_addsUnchecked() {
        val r = applyAction(MarkdownToolbarAction.TaskList, "buy milk", TextRange(0, 0))
        assertEquals("- [ ] buy milk", r.text)
    }

    @Test
    fun linkAction_insertsAtCursor() {
        val r = applyAction(MarkdownToolbarAction.Link, "x", TextRange(1, 1))
        assertEquals("x[text](url)", r.text)
        // 光标应落在 url 占位上
        assertEquals(TextRange(8, 11), r.selection)
    }
}
