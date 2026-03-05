package xyz.junerver.compose.palette.components.select

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SelectLogicTest {
    @Test
    fun resolveSelectedLabel_whenValueIsNull_shouldReturnPlaceholder() {
        val options = listOf(
            SelectOption(label = "Alpha", value = "a"),
            SelectOption(label = "Beta", value = "b"),
        )

        val label = resolveSelectedLabel(
            options = options,
            value = null,
            placeholder = "请选择"
        )

        assertEquals("请选择", label)
    }

    @Test
    fun resolveSelectedLabel_whenValueExists_shouldReturnMatchedLabel() {
        val options = listOf(
            SelectOption(label = "Alpha", value = "a"),
            SelectOption(label = "Beta", value = "b"),
        )

        val label = resolveSelectedLabel(
            options = options,
            value = "b",
            placeholder = "请选择"
        )

        assertEquals("Beta", label)
    }

    @Test
    fun filterSelectOptions_whenQueryBlank_shouldReturnAllOptions() {
        val options = listOf(
            SelectOption(label = "Alpha", value = "a"),
            SelectOption(label = "Beta", value = "b"),
        )

        val filtered = filterSelectOptions(options, "   ")

        assertEquals(options, filtered)
    }

    @Test
    fun filterSelectOptions_whenQueryPresent_shouldMatchIgnoreCase() {
        val options = listOf(
            SelectOption(label = "Apple", value = 1),
            SelectOption(label = "Banana", value = 2),
            SelectOption(label = "Grape", value = 3),
        )

        val filtered = filterSelectOptions(options, "AP")

        assertEquals(listOf("Apple", "Grape"), filtered.map { it.label })
    }

    @Test
    fun shouldToggleExpanded_whenDisabled_shouldAlwaysReturnFalse() {
        assertFalse(shouldToggleExpanded(currentExpanded = false, enabled = false))
        assertFalse(shouldToggleExpanded(currentExpanded = true, enabled = false))
    }

    @Test
    fun shouldToggleExpanded_whenEnabled_shouldToggleState() {
        assertTrue(shouldToggleExpanded(currentExpanded = false, enabled = true))
        assertFalse(shouldToggleExpanded(currentExpanded = true, enabled = true))
    }

    @Test
    fun isOptionSelectable_whenOptionDisabled_shouldReturnFalse() {
        val option = SelectOption(label = "Disabled", value = 1, disabled = true)

        assertFalse(isOptionSelectable(option, enabled = true))
    }
}
