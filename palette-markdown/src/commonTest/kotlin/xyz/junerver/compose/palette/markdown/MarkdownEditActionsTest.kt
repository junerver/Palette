package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkdownEditActionsTest {

    // region wrapSelection
    @Test
    fun wrapSelection_bold_noSelection_insertsEmptyMarkerAndCaretInside() {
        val r = wrapSelection("hello", MarkdownSelection(0, 0), "**")
        assertEquals("****hello", r.text)
        assertEquals(MarkdownSelection(2, 2), r.selection)
    }

    @Test
    fun wrapSelection_bold_withSelection_wrapsAndCaretAfterContent() {
        val r = wrapSelection("hello world", MarkdownSelection(0, 5), "**")
        assertEquals("**hello** world", r.text)
        assertEquals(MarkdownSelection(0, 7), r.selection) // 包裹后选区覆盖 **hello
    }

    @Test
    fun wrapSelection_bold_alreadyWrapped_unwraps() {
        val r = wrapSelection("**hi**", MarkdownSelection(0, 6), "**")
        assertEquals("hi", r.text)
        assertEquals(MarkdownSelection(0, 2), r.selection)
    }

    @Test
    fun wrapSelection_inlineCode_usesBacktick() {
        val r = wrapSelection("a b c", MarkdownSelection(2, 3), "`")
        assertEquals("a `b` c", r.text)
    }

    @Test
    fun wrapSelection_italic_distinctFromBold() {
        val r = wrapSelection("x", MarkdownSelection(0, 1), "*")
        assertEquals("*x*", r.text)
    }
    // endregion

    // region toggleLinePrefix
    @Test
    fun toggleLinePrefix_unordered_singleLine_adds() {
        val r = toggleLinePrefix("item", MarkdownSelection(0, 0), "- ")
        assertEquals("- item", r.text)
    }

    @Test
    fun toggleLinePrefix_unordered_alreadyHas_removes() {
        val r = toggleLinePrefix("- item", MarkdownSelection(0, 6), "- ")
        assertEquals("item", r.text)
    }

    @Test
    fun toggleLinePrefix_multiLine_allAdded() {
        val r = toggleLinePrefix("a\nb\nc", MarkdownSelection(0, 5), "- ")
        assertEquals("- a\n- b\n- c", r.text)
    }

    @Test
    fun toggleLinePrefix_ordered_increments() {
        val r = toggleLinePrefix("a\nb", MarkdownSelection(0, 3), "1. ", ordered = true)
        assertEquals("1. a\n2. b", r.text)
    }

    @Test
    fun toggleLinePrefix_quote_prefix() {
        val r = toggleLinePrefix("note", MarkdownSelection(0, 0), "> ")
        assertEquals("> note", r.text)
    }

    @Test
    fun toggleLinePrefix_convertsExistingListToOrdered() {
        val r = toggleLinePrefix("- a\n- b", MarkdownSelection(0, 7), "1. ", ordered = true)
        assertEquals("1. a\n2. b", r.text)
    }
    // endregion

    // region setHeadingLevel
    @Test
    fun setHeadingLevel_setsH2() {
        val r = setHeadingLevel("title", MarkdownSelection(0, 0), 2)
        assertEquals("## title", r.text)
    }

    @Test
    fun setHeadingLevel_replacesExisting() {
        val r = setHeadingLevel("# title", MarkdownSelection(0, 7), 3)
        assertEquals("### title", r.text)
    }

    @Test
    fun setHeadingLevel_zeroClears() {
        val r = setHeadingLevel("## title", MarkdownSelection(0, 8), 0)
        assertEquals("title", r.text)
    }
    // endregion

    // region toggleTaskItem
    @Test
    fun toggleTaskItem_plainLine_addsUnchecked() {
        val r = toggleTaskItem("buy milk", MarkdownSelection(0, 0))
        assertEquals("- [ ] buy milk", r.text)
    }

    @Test
    fun toggleTaskItem_uncheckedBecomesChecked() {
        val r = toggleTaskItem("- [ ] buy milk", MarkdownSelection(0, 0))
        assertEquals("- [x] buy milk", r.text)
    }

    @Test
    fun toggleTaskItem_checkedBecomesPlainList() {
        val r = toggleTaskItem("- [x] done", MarkdownSelection(0, 0))
        assertEquals("- done", r.text)
    }
    // endregion

    // region insertText
    @Test
    fun insertText_link_selectsUrlPlaceholder() {
        val r = insertText("abc", MarkdownSelection(1, 1), "[text](url)", selectInside = 7..9)
        assertEquals("a[text](url)bc", r.text)
        // 占位 url 在 snippet 索引 7..9
        assertEquals(MarkdownSelection(1 + 7, 1 + 10), r.selection)
    }

    @Test
    fun insertText_codeFence_padsWithNewlines_midLine() {
        // 在 "abcd" 中间(光标=2)插入块级代码围栏，前后应补换行
        val r = insertText("abcd", MarkdownSelection(2, 2), "```\n\n```\n")
        assertEquals("ab\n```\n\n```\n\ncd", r.text)
    }

    @Test
    fun insertText_codeFence_atLineStart_noLeadingPad() {
        val r = insertText("cd", MarkdownSelection(0, 0), "```\n\n```\n")
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
        val r = indent("line", MarkdownSelection(0, 0), forward = true)
        assertEquals("    line", r.text)
    }

    @Test
    fun indent_backward_removesFourSpaces() {
        val r = indent("    line", MarkdownSelection(0, 0), forward = false)
        assertEquals("line", r.text)
    }

    @Test
    fun indent_backward_removesTwoSpacesIfNoFour() {
        val r = indent("  line", MarkdownSelection(0, 0), forward = false)
        assertEquals("line", r.text)
    }

    @Test
    fun indent_multiLine_indentsAll() {
        val r = indent("a\nb", MarkdownSelection(0, 3), forward = true)
        assertEquals("    a\n    b", r.text)
    }

    @Test
    fun indent_backward_noLeadingSpace_unchanged() {
        val r = indent("line", MarkdownSelection(0, 0), forward = false)
        assertEquals("line", r.text)
    }
    // endregion

    // region continueOnEnter
    @Test
    fun continueOnEnter_unorderedList_continuesPrefix() {
        val r = continueOnEnter("- item", MarkdownSelection(6, 6))
        assertNotNull(r)
        assertEquals("- item\n- ", r.text)
        assertEquals(MarkdownSelection(9, 9), r.selection)
    }

    @Test
    fun continueOnEnter_orderedList_increments() {
        val r = continueOnEnter("1. first", MarkdownSelection(8, 8))
        assertNotNull(r)
        assertEquals("1. first\n2. ", r.text)
    }

    @Test
    fun continueOnEnter_quote_continues() {
        val r = continueOnEnter("> note", MarkdownSelection(6, 6))
        assertNotNull(r)
        assertEquals("> note\n> ", r.text)
    }

    @Test
    fun continueOnEnter_emptyListItem_clearsPrefix() {
        val r = continueOnEnter("- ", MarkdownSelection(2, 2))
        assertNotNull(r)
        assertEquals("", r.text)
    }

    @Test
    fun continueOnEnter_plainLine_returnsNull() {
        val r = continueOnEnter("just text", MarkdownSelection(9, 9))
        assertNull(r)
    }

    @Test
    fun continueOnEnter_preservesIndent() {
        val r = continueOnEnter("  - nested", MarkdownSelection(10, 10))
        assertNotNull(r)
        assertEquals("  - nested\n  - ", r.text)
    }
    // endregion

    // region currentLineRange / selectedLineRanges
    @Test
    fun currentLineRange_singleLine() {
        val (s, e) = currentLineRange("hello", MarkdownSelection(2, 2))
        assertEquals(0, s)
        assertEquals(5, e)
    }

    @Test
    fun currentLineRange_middleLine() {
        val text = "a\nbb\nccc"
        val (s, e) = currentLineRange(text, MarkdownSelection(3, 3)) // 在 "bb" 内
        assertEquals(2, s)
        assertEquals(4, e)
    }

    @Test
    fun selectedLineRanges_coversMultipleLines() {
        val text = "a\nb\nc"
        val ranges = selectedLineRanges(text, MarkdownSelection(0, 5))
        assertEquals(3, ranges.size)
    }
    // endregion

    // region 工具栏动作映射（原 MarkdownToolbarIntegrationLogicTest）
    // 这些测试原本通过 MarkdownToolbarAction 枚举 + Compose TextFieldValue 路由，
    // 但该枚举属 UI 层（palette 模块），核心层必须 Compose-free。
    // 因此这里改为直接调用对应的纯函数，保持输入/输出断言完全一致。
    @Test
    fun boldAction_wrapsSelection() {
        val r = wrapSelection("hello", MarkdownSelection(0, 5), "**")
        assertEquals("**hello**", r.text)
    }

    @Test
    fun inlineLatexAction_wrapsSelection() {
        val r = wrapSelection("a^2+b^2", MarkdownSelection(0, 7), "\$")
        assertEquals("\$a^2+b^2\$", r.text)
    }

    @Test
    fun subscriptAction_wrapsSelection() {
        val r = wrapSelection("2", MarkdownSelection(0, 1), "~")
        assertEquals("~2~", r.text)
    }

    @Test
    fun superscriptAction_wrapsSelection() {
        val r = wrapSelection("2", MarkdownSelection(0, 1), "^")
        assertEquals("^2^", r.text)
    }

    @Test
    fun highlightAction_wrapsSelection() {
        val r = wrapSelection("KEY", MarkdownSelection(0, 3), "==")
        assertEquals("==KEY==", r.text)
    }

    @Test
    fun headingAction_setsH1() {
        val r = setHeadingLevel("title", MarkdownSelection(0, 0), 1)
        assertEquals("# title", r.text)
    }

    @Test
    fun unorderedListAction_addsBullet() {
        val r = toggleLinePrefix("item", MarkdownSelection(0, 0), "- ")
        assertEquals("- item", r.text)
    }

    @Test
    fun taskListAction_addsUnchecked() {
        val r = toggleTaskItem("buy milk", MarkdownSelection(0, 0))
        assertEquals("- [ ] buy milk", r.text)
    }

    @Test
    fun linkAction_insertsAtCursor() {
        val r = insertText("x", MarkdownSelection(1, 1), "[text](url)", selectInside = 7..9)
        assertEquals("x[text](url)", r.text)
        // 光标应落在 url 占位上
        assertEquals(MarkdownSelection(8, 11), r.selection)
    }
    // endregion
}
