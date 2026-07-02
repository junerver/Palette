package xyz.junerver.compose.palette.markdown

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

class MarkdownHistoryTest {

    private fun entry(text: String, caret: Int = text.length) =
        MarkdownHistoryEntry(text, MarkdownSelection(caret, caret))

    private fun history(initial: String = "", clock: () -> Long = { 0L }) =
        MarkdownHistory(initial = entry(initial), coalesceMs = 500L, now = clock)

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
        h.commit(entry("b"))
        assertEquals("b", h.current.text)
        assertTrue(h.canUndo)
        assertFalse(h.canRedo)
    }

    @Test
    fun commit_identicalValue_noOp() {
        val h = history("a")
        h.commit(entry("a"))
        assertFalse(h.canUndo)
    }

    @Test
    fun commit_clearsRedoBranch() {
        val h = history("a")
        h.commit(entry("b"))
        h.undo() // present=a, future=[b]
        assertTrue(h.canRedo)
        h.commit(entry("c"))
        assertFalse(h.canRedo)
    }
    // endregion

    // region pushTyping (coalescing)
    @Test
    fun pushTyping_consecutiveCharsWithinWindow_coalesceIntoOneEntry() {
        val clock = FakeClock(step = 100L) // 每次调用 +100ms，均在 500ms 窗口内
        val h = history("", clock)
        h.pushTyping(entry("h"))
        h.pushTyping(entry("he"))
        h.pushTyping(entry("hel"))
        h.pushTyping(entry("hell"))
        h.undo()
        assertEquals("", h.current.text)
    }

    @Test
    fun pushTyping_afterCoalesceWindow_startsNewEntry() {
        val clock = FakeClock()
        val h = history("", clock)
        clock.set(0); h.pushTyping(entry("h"))
        clock.set(800); h.pushTyping(entry("he")) // >500ms → 不合并
        h.undo()
        assertEquals("h", h.current.text)
    }

    @Test
    fun pushTyping_backspaceCoalescesWithTyping() {
        val clock = FakeClock(step = 100L)
        val h = history("", clock)
        h.pushTyping(entry("ab"))
        h.pushTyping(entry("abc"))
        h.pushTyping(entry("ab")) // 删除 c，相邻删除 → 合并
        h.undo()
        assertEquals("", h.current.text)
    }

    @Test
    fun pushTyping_discontinuousEdit_startsNewEntry() {
        val clock = FakeClock(step = 100L)
        val h = history("ab", clock)
        // 在开头插入 X：非光标连续扩展
        h.pushTyping(entry("Xab", caret = 1))
        h.undo()
        assertEquals("ab", h.current.text)
    }

    @Test
    fun commit_thenTyping_commitsNewEntry() {
        val clock = FakeClock(step = 100L)
        val h = history("", clock)
        h.commit(entry("**")) // 工具栏
        h.pushTyping(entry("**x"))
        h.undo()
        assertEquals("**", h.current.text)
    }
    // endregion

    // region undo / redo
    @Test
    fun undo_redo_roundTrip() {
        val h = history("a")
        h.commit(entry("b"))
        h.commit(entry("c"))
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
        h.pushTyping(entry("hi", caret = 2))
        val restored = h.undo()
        assertEquals("", restored.text)
        assertEquals(0, restored.selection.start)
    }
    // endregion

    // region sync / reset
    @Test
    fun sync_overwritesPresent_withoutHistory() {
        val h = history("a")
        h.commit(entry("b"))
        h.sync(entry("EXTERNAL"))
        assertEquals("EXTERNAL", h.current.text)
        assertTrue(h.canUndo)
        h.undo()
        assertEquals("a", h.current.text)
    }

    @Test
    fun reset_clearsEverything() {
        val h = history("a")
        h.commit(entry("b"))
        h.reset(entry("fresh"))
        assertEquals("fresh", h.current.text)
        assertFalse(h.canUndo)
        assertFalse(h.canRedo)
    }
    // endregion

    // region capacity
    @Test
    fun capacity_evictsOldestBeyondLimit() {
        val h = MarkdownHistory(initial = entry("0"), capacity = 3, coalesceMs = 0L, now = { 0L })
        h.commit(entry("1"))
        h.commit(entry("2"))
        h.commit(entry("3"))
        h.commit(entry("4")) // 超容量，最早项 "0"/"1" 被淘汰
        // 撤销最多 3 步
        repeat(3) { h.undo() }
        val afterThreeUndo = h.current.text
        // 再撤销应无效（已到栈底）
        h.undo()
        assertEquals(afterThreeUndo, h.current.text)
    }
    // endregion
}
