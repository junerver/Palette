package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.table.ColumnConfig
import xyz.junerver.compose.palette.components.table.PTable

data class Person(val name: String, val age: Int, val address: String)

private val demoData = listOf(
    Person("John Brown", 32, "New York No. 1 Lake Park"),
    Person("Jim Green", 42, "London No. 1 Bridge Street"),
    Person("Joe Black", 32, "Sidney No. 1 Lake Park"),
    Person("Jim Red", 32, "London No. 2 Lake Park")
)

@Composable
fun TableDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PTable",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "用于展示行列数据。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 1. 基础表格
        DemoSection(title = "基础用法") {
            val columns = remember {
                listOf(
                    ColumnConfig<Person>(title = "Name", width = 120.dp) { Text(it.name) },
                    ColumnConfig(title = "Age", width = 80.dp) { Text(it.age.toString()) },
                    ColumnConfig(title = "Address") { Text(it.address) }
                )
            }

            PTable(
                data = demoData,
                columns = columns,
                modifier = Modifier.height(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
            val columns = listOf(
                ColumnConfig<Person>(title = "Name", width = 120.dp) { Text(it.name) },
                ColumnConfig(title = "Age", width = 80.dp) { Text(it.age.toString()) },
                ColumnConfig(title = "Address") { Text(it.address) }
            )

            PTable(
                data = data,
                columns = columns
            )
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. 可排序表格
        DemoSection(title = "可排序") {
            var sortColumn by remember { mutableStateOf<Int?>(null) }
            var sortAscending by remember { mutableStateOf(true) }
            
            // 根据排序状态处理数据
            val sortedData = remember(sortColumn, sortAscending) {
                if (sortColumn == null) demoData else {
                    val comparator = when (sortColumn) {
                        0 -> compareBy<Person> { it.name }
                        1 -> compareBy { it.age }
                        else -> compareBy { it.address }
                    }
                    if (sortAscending) demoData.sortedWith(comparator)
                    else demoData.sortedWith(comparator).reversed()
                }
            }

            val columns = remember {
                listOf(
                    ColumnConfig<Person>(title = "Name", width = 120.dp, sortable = true) { Text(it.name) },
                    ColumnConfig(title = "Age", width = 80.dp, sortable = true) { Text(it.age.toString()) },
                    ColumnConfig(title = "Address", sortable = true) { Text(it.address) }
                )
            }

            PTable(
                data = sortedData,
                columns = columns,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                onSortChange = { col, asc ->
                    sortColumn = col
                    sortAscending = asc
                },
                modifier = Modifier.height(250.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
            var sortColumn by remember { mutableStateOf<Int?>(null) }
            var sortAscending by remember { mutableStateOf(true) }
            
            PTable(
                data = sortedData,
                columns = columns, // set sortable = true in ColumnConfig
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                onSortChange = { col, asc ->
                    sortColumn = col
                    sortAscending = asc
                }
            )
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. 可选择表格
        DemoSection(title = "可选择") {
            var selectedItems by remember { mutableStateOf(emptySet<Person>()) }
            
            val columns = remember {
                listOf(
                    ColumnConfig<Person>(title = "Name", width = 120.dp) { Text(it.name) },
                    ColumnConfig(title = "Age", width = 80.dp) { Text(it.age.toString()) },
                    ColumnConfig(title = "Address") { Text(it.address) }
                )
            }

            Column {
                Text(
                    text = "Selected: ${selectedItems.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                PTable(
                    data = demoData,
                    columns = columns,
                    selectable = true,
                    multiSelect = true,
                    selectedItems = selectedItems,
                    onSelectionChange = { selectedItems = it },
                    modifier = Modifier.height(250.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
            var selectedItems by remember { mutableStateOf(emptySet<Person>()) }
            
            PTable(
                data = data,
                columns = columns,
                selectable = true,
                multiSelect = true,
                selectedItems = selectedItems,
                onSelectionChange = { selectedItems = it }
            )
            """.trimIndent()
        )
    }
}
