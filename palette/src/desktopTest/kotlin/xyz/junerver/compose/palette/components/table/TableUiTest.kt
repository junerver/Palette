package xyz.junerver.compose.palette.components.table

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import xyz.junerver.compose.hooks.usetable.core.column
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TableUiTest {
    @get:Rule
    val rule = createComposeRule()

    private data class RowData(
        val id: String,
        val name: String,
        val age: Int,
    )

    @Test
    fun table_shouldRenderHeadersAndRows() {
        val rows =
            listOf(
                RowData(id = "1", name = "Alice", age = 28),
                RowData(id = "2", name = "Bob", age = 32),
            )

        val columns =
            listOf(
                column<RowData, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name },
                ),
                column<RowData, Int>(
                    id = "age",
                    header = "Age",
                    accessorFn = { it.age },
                ),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PTable(
                    data = rows,
                    columns = columns,
                    modifier = Modifier.height(220.dp),
                    showPagination = false,
                )
            }
        }

        rule.onNodeWithText("Name").assertTextEquals("Name")
        rule.onNodeWithText("Age").assertTextEquals("Age")
        rule.onNodeWithText("Alice").assertTextEquals("Alice", "28")
        rule.onNodeWithText("Bob").assertTextEquals("Bob", "32")
    }

    @Test
    fun table_shouldRenderCustomEmptyContentWhenNoRows() {
        val columns =
            listOf(
                column<RowData, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name },
                ),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PTable(
                    data = emptyList<RowData>(),
                    columns = columns,
                    modifier = Modifier.height(220.dp),
                    showPagination = false,
                    emptyContent = {
                        Text("No rows available")
                    },
                )
            }
        }

        rule.onNodeWithText("No rows available").assertTextEquals("No rows available")
    }

    @Test
    fun table_shouldSortRowsWhenSortableHeaderClicked() {
        val rows =
            listOf(
                RowData(id = "1", name = "Bob", age = 32),
                RowData(id = "2", name = "Alice", age = 28),
            )

        val columns =
            listOf(
                column<RowData, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name },
                    enableSorting = true,
                ),
                column<RowData, Int>(
                    id = "age",
                    header = "Age",
                    accessorFn = { it.age },
                ),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PTable(
                    data = rows,
                    columns = columns,
                    modifier = Modifier.height(220.dp),
                    showPagination = false,
                    optionsOf = {
                        enableSorting = true
                    },
                )
            }
        }

        rule.onNodeWithText("Bob").assertTextEquals("Bob", "32")
        rule.onNodeWithText("Name").performClick()

        rule.onNodeWithText("Alice").assertTextEquals("Alice", "28")
        rule.onAllNodesWithText("Bob").assertCountEquals(1)
    }

    @Test
    fun table_shouldRenderPaginationSummaryWhenEnabled() {
        val rows =
            listOf(
                RowData(id = "1", name = "Alice", age = 28),
                RowData(id = "2", name = "Bob", age = 32),
                RowData(id = "3", name = "Cindy", age = 26),
            )

        val columns =
            listOf(
                column<RowData, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name },
                ),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PTable(
                    data = rows,
                    columns = columns,
                    modifier = Modifier.height(220.dp),
                    showPagination = true,
                    optionsOf = {
                        enablePagination = true
                        pageSize = 2
                    },
                )
            }
        }

        rule.onNodeWithText("第 1 / 2 页").assertTextEquals("第 1 / 2 页")
    }

    @Test
    fun table_shouldSortRowsDescendingWhenHeaderClickedTwice() {
        val rows =
            listOf(
                RowData(id = "1", name = "Alice", age = 28),
                RowData(id = "2", name = "Bob", age = 32),
            )

        val columns =
            listOf(
                column<RowData, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name },
                    enableSorting = true,
                ),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PTable(
                    data = rows,
                    columns = columns,
                    modifier = Modifier.height(220.dp),
                    showPagination = false,
                    optionsOf = {
                        enableSorting = true
                    },
                )
            }
        }

        rule.onNodeWithText("Name").performClick()
        rule.onNodeWithText("Name").performClick()
        rule.onNodeWithText("Bob").assertTextEquals("Bob")
    }

    @Test
    fun table_shouldRenderRowsInEmbeddedMode() {
        val rows =
            listOf(
                RowData(id = "1", name = "Palette", age = 1),
                RowData(id = "2", name = "Hooks", age = 2),
            )

        val columns =
            listOf(
                column<RowData, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name },
                ),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PTable(
                    data = rows,
                    columns = columns,
                    modifier = Modifier.height(220.dp),
                    scrollBehavior = TableScrollBehavior.Embedded,
                    showPagination = false,
                )
            }
        }

        rule.onNodeWithText("Name").assertTextEquals("Name")
        rule.onNodeWithText("Palette").assertTextEquals("Palette")
        rule.onNodeWithText("Hooks").assertTextEquals("Hooks")
    }
}
