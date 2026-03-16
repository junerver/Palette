package xyz.junerver.compose.palette.ui.benchmark.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import xyz.junerver.compose.palette.DataGridColumn
import xyz.junerver.compose.palette.PDataGrid
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkTags

private data class BenchmarkRow(
    val index: Int,
    val name: String,
    val status: String,
)

@Composable
fun BenchmarkDataGridScreen() {
    val rows =
        List(2000) { index ->
            BenchmarkRow(index, "Row $index", if (index % 2 == 0) "OK" else "WARN")
        }
    val columns =
        listOf(
            DataGridColumn<BenchmarkRow>(title = "ID", value = { it.index.toString() }, weight = 0.3f),
            DataGridColumn<BenchmarkRow>(title = "Name", value = { it.name }, weight = 0.5f),
            DataGridColumn<BenchmarkRow>(title = "Status", value = { it.status }, weight = 0.2f),
        )
    Box(modifier = Modifier.fillMaxSize().testTag(BenchmarkTags.DataGrid)) {
        PDataGrid(
            rows = rows,
            columns = columns,
        )
    }
}
