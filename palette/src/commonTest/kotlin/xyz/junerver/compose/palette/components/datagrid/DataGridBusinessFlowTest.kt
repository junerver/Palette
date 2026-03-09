package xyz.junerver.compose.palette.components.datagrid

import kotlin.test.Test
import kotlin.test.assertEquals

class DataGridBusinessFlowTest {
    private data class MixedValueRow(
        val id: String,
        val value: Comparable<*>?,
    )

    @Test
    fun sortRows_whenValueTypesMixed_shouldFallbackToStringAndStayStable() {
        val rows =
            listOf(
                MixedValueRow(id = "first", value = 2),
                MixedValueRow(id = "second", value = 11L),
                MixedValueRow(id = "third", value = 1),
            )

        val sorted =
            sortRows(
                rows = rows,
                specs = listOf(DataGridSortSpec<MixedValueRow>(selector = { it.value })),
            )

        assertEquals(listOf("third", "second", "first"), sorted.map { it.id })
    }

    @Test
    fun sortRows_whenNullsPresent_shouldKeepNullValuesAtEndForAscOrder() {
        val rows =
            listOf(
                MixedValueRow(id = "need-action", value = 1),
                MixedValueRow(id = "backlog", value = null),
                MixedValueRow(id = "blocked", value = 0),
            )

        val sorted =
            sortRows(
                rows = rows,
                specs = listOf(DataGridSortSpec<MixedValueRow>(selector = { it.value })),
            )

        assertEquals(listOf("blocked", "need-action", "backlog"), sorted.map { it.id })
    }
}
