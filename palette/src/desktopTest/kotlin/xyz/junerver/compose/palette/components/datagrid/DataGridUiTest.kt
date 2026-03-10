package xyz.junerver.compose.palette.components.datagrid

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class DataGridUiTest {
    @get:Rule
    val rule = createComposeRule()

    private data class RowData(
        val name: String,
        val status: String,
    )

    @Test
    fun dataGrid_shouldRenderHeadersAndRows() {
        val rows =
            listOf(
                RowData(name = "Palette", status = "Stable"),
                RowData(name = "Hooks", status = "Beta"),
            )

        val columns =
            listOf(
                DataGridColumn<RowData>(title = "Project", value = { it.name }),
                DataGridColumn<RowData>(title = "Status", value = { it.status }),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PDataGrid(
                    rows = rows,
                    columns = columns,
                )
            }
        }

        rule.onNodeWithText("Project").assertTextEquals("Project")
        rule.onNodeWithText("Status").assertTextEquals("Status")
        rule.onNodeWithText("Palette").assertTextEquals("Palette")
        rule.onNodeWithText("Stable").assertTextEquals("Stable")
        rule.onNodeWithText("Hooks").assertTextEquals("Hooks")
        rule.onNodeWithText("Beta").assertTextEquals("Beta")
    }

    @Test
    fun dataGrid_shouldRenderOnlyHeadersWhenRowsEmpty() {
        val columns =
            listOf(
                DataGridColumn<RowData>(title = "Project", value = { it.name }, weight = 2f),
                DataGridColumn<RowData>(title = "Status", value = { it.status }, weight = 1f),
            )

        rule.setContent {
            PaletteMaterialTheme {
                PDataGrid(
                    rows = emptyList(),
                    columns = columns,
                )
            }
        }

        rule.onNodeWithText("Project").assertTextEquals("Project")
        rule.onNodeWithText("Status").assertTextEquals("Status")
        rule.onAllNodesWithText("Palette").assertCountEquals(0)
        rule.onAllNodesWithText("Hooks").assertCountEquals(0)
    }
}
