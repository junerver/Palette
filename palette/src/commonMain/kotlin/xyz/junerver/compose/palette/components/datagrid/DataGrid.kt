package xyz.junerver.compose.palette.components.datagrid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> PDataGrid(
    rows: List<T>,
    columns: List<DataGridColumn<T>>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item(key = "header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DataGridDefaults.headerContainerColor())
                    .padding(DataGridDefaults.CellPadding),
                horizontalArrangement = Arrangement.spacedBy(DataGridDefaults.CellPadding)
            ) {
                columns.forEach { column ->
                    Text(
                        text = column.title,
                        color = DataGridDefaults.headerContentColor(),
                        modifier = Modifier.weight(column.weight)
                    )
                }
            }
        }
        itemsIndexed(rows, key = { index, _ -> index }) { _, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DataGridDefaults.rowContainerColor())
                    .padding(DataGridDefaults.CellPadding),
                horizontalArrangement = Arrangement.spacedBy(DataGridDefaults.CellPadding)
            ) {
                columns.forEach { column ->
                    Text(
                        text = column.value(row),
                        color = DataGridDefaults.rowContentColor(),
                        modifier = Modifier.weight(column.weight)
                    )
                }
            }
        }
    }
}

data class DataGridColumn<T>(
    val title: String,
    val value: (T) -> String,
    val weight: Float = 1f,
)
