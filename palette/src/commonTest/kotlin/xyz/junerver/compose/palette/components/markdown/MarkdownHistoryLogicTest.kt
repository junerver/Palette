package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 可控时钟：每次调用 [tick] 推进，[invoke] 返回当前值。 */
private class FakeClock(var t: Long = 0L, val step: Long = 0L) : () -> Long {
    override fun invoke(): Long {
        val current = t
        if (step > 0L) t += step
        return current
    }
    /** 手动设置下次返回的时间。 */
    fun set(time: Long) { t = time }
}

class MarkdownHistoryLogicTest {

    private fun tf(text: String, caret: Int = text.length) =
        TextFieldValue(text, TextRange(caret, caret))

    private fun history(initial: String = "", clock: () -> Long = { 0L }) =
        MarkdownHistory(initial = tf(initial), coalesceMs = 500L, now = clock)

    @Test
    fun initial_state_cannotUndoOrRedo() {
        val h = history("hi")
        assertEquals("hi", h.current.text)
        assertFalse(h.canUndo)
        assertFalse(h.canRedo)
    }

    // region commit (结构化操作)
    @Test
    fun commit_createsNewEntry_enablesUndo() {
        val h = history("a")
        h.commit(tf("b"))
        assertEquals("b", h.current.text)
        assertTrue(h.canUndo)
        assertFalse(h.canRedo)
    }

    @Test
    fun commit_identicalValue_noOp() {
        val h = history("a")
        h.commit(tf("a"))
        assertFalse(h.canUndo)
    }

    @Test
    fun commit_clearsRedoBranch() {
        val h = history("a")
        h.commit(tf("b"))
        h.undo() // present=a, future=[b]
        assertTrue(h.canRedo)
        h.commit(tf("c"))
        assertFalse(h.canRedo)
    }
    // endregion

    // region pushTyping (coalescing)
    @Test
    fun pushTyping_consecutiveCharsWithinWindow_coalesceIntoOneEntry() {
        val clock = FakeClock(step = 100L) // 每次调用 +100ms，均在 500ms 窗口内
        val h = history("", clock)
        h.pushTyping(tf("h"))
        h.pushTyping(tf("he"))
        h.pushTyping(tf("hel"))
        h.pushTyping(tf("hell"))
        h.undo()
        assertEquals("", h.current.text)
    }

    @Test
    fun pushTyping_afterCoalesceWindow_startsNewEntry() {
        val clock = FakeClock()
        val h = history("", clock)
        clock.set(0); h.pushTyping(tf("h"))
        clock.set(800); h.pushTyping(tf("he")) // >500ms → 不合并
        h.undo()
        assertEquals("h", h.current.text)
    }

    @Test
    fun pushTyping_backspaceCoalescesWithTyping() {
        val clock = FakeClock(step = 100L)
        val h = history("", clock)
        h.pushTyping(tf("ab"))
        h.pushTyping(tf("abc"))
        h.pushTyping(tf("ab")) // 删除 c，相邻删除 → 合并
        h.undo()
        assertEquals("", h.current.text)
    }

    @Test
    fun pushTyping_discontinuousEdit_startsNewEntry() {
        val clock = FakeClock(step = 100L)
        val h = history("ab", clock)
        // 在开头插入 X：非光标连续扩展
        h.pushTyping(tf("Xab", caret = 1))
        h.undo()
        assertEquals("ab", h.current.text)
    }

    @Test
    fun commit_thenTyping_commitsNewEntry() {
        val clock = FakeClock(step = 100L)
        val h = history("", clock)
        h.commit(tf("**")) // 工具栏
        h.pushTyping(tf("**x"))
        h.undo()
        assertEquals("**", h.current.text)
    }
    // endregion

    // region undo / redo
    @Test
    fun undo_redo_roundTrip() {
        val h = history("a")
        h.commit(tf("b"))
        h.commit(tf("c"))
        h.undo()
        assertEquals("b", h.current.text)
        h.undo()
        assertEquals("a", h.current.text)
        assertFalse(h.canUndo)
        h.undo() // 栈底，不变
        assertEquals("a", h.current.text)
        h.redo()
        assertEquals("b", h.current.text)
        h.redo()
        assertEquals("c", h.current.text)
        assertFalse(h.canRedo)
    }

    @Test
    fun undo_afterTypingCoalesce_restoresSelection() {
        val clock = FakeClock(step = 100L)
        val h = history("", clock)
        h.pushTyping(tf("hi", caret = 2))
        val restored = h.undo()
        assertEquals("", restored.text)
        assertEquals(0, restored.selection.start)
    }
    // endregion

    // region sync / reset
    @Test
    fun sync_overwritesPresent_withoutHistory() {
        val h = history("a")
        h.commit(tf("b"))
        h.sync(tf("EXTERNAL"))
        assertEquals("EXTERNAL", h.current.text)
        assertTrue(h.canUndo)
        h.undo()
        assertEquals("a", h.current.text)
    }

    @Test
    fun reset_clearsEverything() {
        val h = history("a")
        h.commit(tf("b"))
        h.reset(tf("fresh"))
        assertEquals("fresh", h.current.text)
        assertFalse(h.canUndo)
        assertFalse(h.canRedo)
    }
    // endregion

    // region capacity
    @Test
    fun capacity_evictsOldestBeyondLimit() {
        val h = MarkdownHistory(initial = tf("0"), capacity = 3, coalesceMs = 0L, now = { 0L })
        h.commit(tf("1"))
        h.commit(tf("2"))
        h.commit(tf("3"))
        h.commit(tf("4")) // 超容量，最早项 "0"/"1" 被淘汰
        // 撤销最多 3 步
        repeat(3) { h.undo() }
        val afterThreeUndo = h.current.text
        // 再撤销应无效（已到栈底）
        h.undo()
        assertEquals(afterThreeUndo, h.current.text)
    }
    // endregion
}
