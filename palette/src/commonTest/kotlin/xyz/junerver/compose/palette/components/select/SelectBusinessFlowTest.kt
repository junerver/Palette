package xyz.junerver.compose.palette.components.select

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SelectBusinessFlowTest {
    private val options =
        listOf(
            SelectOption(label = "Pending", value = "pending"),
            SelectOption(label = "Approved", value = "approved"),
            SelectOption(label = "Archived", value = "archived", disabled = true),
        )

    @Test
    fun statusFilterFlow_shouldShowPlaceholderBeforeChoosingOption() {
        val label = resolveSelectedLabel(options = options, value = null, placeholder = "请选择状态")

        assertEquals("请选择状态", label)
    }

    @Test
    fun statusFilterFlow_shouldIgnoreDisabledOptionInSelectionCheck() {
        assertTrue(isOptionSelectable(options[0], enabled = true))
        assertFalse(isOptionSelectable(options[2], enabled = true))
    }

    @Test
    fun statusFilterFlow_shouldFilterByBusinessKeyword() {
        val filtered = filterSelectOptions(options, query = "app")

        assertEquals(listOf("Approved"), filtered.map { it.label })
    }

    @Test
    fun statusFilterFlow_shouldPreserveSelectionOrderInMultiMode() {
        val selected = filterSelectedOptions(options, selectedValues = listOf("approved", "pending"))

        assertEquals(listOf("Approved", "Pending"), selected.map { it.label })
    }

    @Test
    fun statusFilterFlow_shouldRespectSelectionLimit() {
        val current = listOf("pending", "approved")

        assertEquals(current, toggleMultiSelection(current = current, value = "archived", maxSelection = 2))
    }
}
