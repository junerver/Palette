package xyz.junerver.compose.palette.components.datagrid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataGridLogicTest {
    private data class Row(
        val name: String,
        val age: Int,
        val city: String?,
    )

    private val rows =
        listOf(
            Row(name = "Alice", age = 30, city = "Shanghai"),
            Row(name = "Bob", age = 25, city = "Beijing"),
            Row(name = "Cindy", age = 25, city = "Shenzhen"),
            Row(name = "Daniel", age = 34, city = null),
        )

    @Test
    fun filterRows_whenKeywordMatched_shouldReturnFilteredRows() {
        val filtered =
            filterRows(rows, "bei") { row ->
                listOf(row.name, row.city.orEmpty())
            }

        assertEquals(listOf("Bob"), filtered.map { it.name })
    }

    @Test
    fun sortRows_whenMultipleSpecs_shouldSortByPriority() {
        val sorted =
            sortRows(
                rows = rows,
                specs =
                    listOf(
                        DataGridSortSpec<Row>(
                            selector = { it.age },
                            direction = DataGridSortDirection.Asc,
                        ),
                        DataGridSortSpec<Row>(
                            selector = { it.name },
                            direction = DataGridSortDirection.Desc,
                        ),
                    ),
            )

        assertEquals(listOf("Cindy", "Bob", "Alice", "Daniel"), sorted.map { it.name })
    }

    @Test
    fun paginateRows_whenValidPage_shouldReturnSlicedRows() {
        val page = paginateRows(rows = rows, pageIndex = 1, pageSize = 2)

        assertEquals(listOf("Cindy", "Daniel"), page.map { it.name })
    }

    @Test
    fun paginateRows_whenOutOfRange_shouldReturnEmptyList() {
        val page = paginateRows(rows = rows, pageIndex = 3, pageSize = 2)

        assertTrue(page.isEmpty())
    }

    @Test
    fun resolvePageCount_shouldComputeByPageSize() {
        assertEquals(0, resolvePageCount(totalRows = 0, pageSize = 10))
        assertEquals(3, resolvePageCount(totalRows = 21, pageSize = 10))
        assertEquals(1, resolvePageCount(totalRows = 21, pageSize = 0))
    }
}
