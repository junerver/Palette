package xyz.junerver.compose.palette.components.pagination

import xyz.junerver.compose.palette.components.datagrid.DataGridSortDirection
import xyz.junerver.compose.palette.components.datagrid.DataGridSortSpec
import xyz.junerver.compose.palette.components.datagrid.filterRows
import xyz.junerver.compose.palette.components.datagrid.paginateRows
import xyz.junerver.compose.palette.components.datagrid.resolvePageCount
import xyz.junerver.compose.palette.components.datagrid.sortRows
import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationFlowIntegrationTest {
    private data class Ticket(
        val id: String,
        val title: String,
        val severity: Int,
        val updatedAt: Long,
    )

    @Test
    fun incidentListFlow_shouldProduceExpectedFirstPage() {
        val tickets =
            listOf(
                Ticket(id = "t1", title = "Billing failure", severity = 3, updatedAt = 170),
                Ticket(id = "t2", title = "Login timeout", severity = 1, updatedAt = 300),
                Ticket(id = "t3", title = "Billing address mismatch", severity = 2, updatedAt = 250),
                Ticket(id = "t4", title = "Billing tax check", severity = 1, updatedAt = 100),
                Ticket(id = "t5", title = "Search bug", severity = 2, updatedAt = 400),
            )

        val filtered = filterRows(tickets, keyword = "billing") { listOf(it.title) }
        val sorted =
            sortRows(
                rows = filtered,
                specs =
                    listOf(
                        DataGridSortSpec<Ticket>(selector = { it.severity }, direction = DataGridSortDirection.Asc),
                        DataGridSortSpec<Ticket>(selector = { it.updatedAt }, direction = DataGridSortDirection.Desc),
                    ),
            )
        val pageSize = 2
        val firstPage = paginateRows(sorted, pageIndex = 0, pageSize = pageSize)
        val totalPages = resolvePageCount(totalRows = sorted.size, pageSize = pageSize)
        val pageNumbers = calculatePageNumbers(current = 1, total = totalPages)

        assertEquals(listOf("t4", "t3"), firstPage.map { it.id })
        assertEquals(listOf(1, 2), pageNumbers)
    }

    @Test
    fun calculatePageNumbers_whenTotalIsZero_shouldReturnEmpty() {
        assertEquals(emptyList(), calculatePageNumbers(current = 1, total = 0))
    }
}
