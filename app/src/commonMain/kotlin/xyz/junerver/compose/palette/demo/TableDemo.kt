package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.usetable.core.column
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.table.PTable
import xyz.junerver.compose.palette.components.table.TableScrollBehavior

data class Person(
    val id: String,
    val name: String,
    val age: Int,
    val email: String,
    val department: String
)

@Composable
fun TableDemo() {
    val text = tableDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        // Example 1: Basic Table
        BasicTableExample(text)

        // Example 2: Sortable Table
        SortableTableExample(text)

        // Example 3: Table with Row Selection
        SelectableTableExample(text)

        // Example 4: Table with Pagination
        PaginatedTableExample(text)

        // Example 5: Full-Featured Table
        FullFeaturedTableExample(text)

        // Example 6: Embedded Table (No Fixed Height Required)
        EmbeddedTableExample(text)
    }
}

/**
 * Example 1: Basic Table
 * Demonstrates the simplest usage with minimal configuration.
 */
@Composable
private fun BasicTableExample(text: TableDemoText) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PText(
            text = text.basicTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        PText(
            text = text.basicDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        val data = listOf(
            Person("1", "Alice", 28, "alice@example.com", text.departmentEngineering),
            Person("2", "Bob", 32, "bob@example.com", text.departmentMarketing),
            Person("3", "Charlie", 25, "charlie@example.com", text.departmentDesign),
        )

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = text.columnName,
                accessorFn = { it.name }
            ),
            column<Person, Int>(
                id = "age",
                header = text.columnAge,
                accessorFn = { it.age }
            ),
            column<Person, String>(
                id = "department",
                header = text.columnDepartment,
                accessorFn = { it.department }
            )
        )

        PTable(
            data = data,
            columns = columns,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

/**
 * Example 2: Sortable Table
 * Demonstrates automatic sorting with useTable.
 * Click column headers to sort - no manual state management needed!
 */
@Composable
private fun SortableTableExample(text: TableDemoText) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PText(
            text = text.sortableTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        PText(
            text = text.sortableDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        val data = listOf(
            Person("1", "Alice", 28, "alice@example.com", text.departmentEngineering),
            Person("2", "Bob", 32, "bob@example.com", text.departmentMarketing),
            Person("3", "Charlie", 25, "charlie@example.com", text.departmentDesign),
            Person("4", "David", 35, "david@example.com", text.departmentEngineering),
            Person("5", "Eve", 29, "eve@example.com", text.departmentMarketing),
        )

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = text.columnName,
                accessorFn = { it.name },
                enableSorting = true
            ),
            column<Person, Int>(
                id = "age",
                header = text.columnAge,
                accessorFn = { it.age },
                enableSorting = true
            ),
            column<Person, String>(
                id = "email",
                header = text.columnEmail,
                accessorFn = { it.email },
                enableSorting = true
            ),
            column<Person, String>(
                id = "department",
                header = text.columnDepartment,
                accessorFn = { it.department },
                enableSorting = true
            )
        )

        PTable(
            data = data,
            columns = columns,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            optionsOf = {
                enableSorting = true
            }
        )
    }
}

/**
 * Example 3: Table with Row Selection
 * Demonstrates row selection with checkboxes.
 * useTable automatically manages selection state!
 */
@Composable
private fun SelectableTableExample(text: TableDemoText) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PText(
            text = text.selectableTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        PText(
            text = text.selectableDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        val data = listOf(
            Person("1", "Alice", 28, "alice@example.com", text.departmentEngineering),
            Person("2", "Bob", 32, "bob@example.com", text.departmentMarketing),
            Person("3", "Charlie", 25, "charlie@example.com", text.departmentDesign),
            Person("4", "David", 35, "david@example.com", text.departmentEngineering),
        )

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = text.columnName,
                accessorFn = { it.name }
            ),
            column<Person, Int>(
                id = "age",
                header = text.columnAge,
                accessorFn = { it.age }
            ),
            column<Person, String>(
                id = "department",
                header = text.columnDepartment,
                accessorFn = { it.department }
            )
        )

        PTable(
            data = data,
            columns = columns,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            optionsOf = {
                enableRowSelection = true
            }
        )
    }
}

/**
 * Example 4: Table with Pagination
 * Demonstrates automatic pagination with useTable.
 */
@Composable
private fun PaginatedTableExample(text: TableDemoText) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PText(
            text = text.paginatedTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        PText(
            text = text.paginatedDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        val data = (1..20).map {
            Person(
                id = it.toString(),
                name = "${text.personPrefix} $it",
                age = 20 + (it % 30),
                email = "person$it@example.com",
                department = listOf(
                    text.departmentEngineering,
                    text.departmentMarketing,
                    text.departmentDesign,
                    text.departmentSales,
                )[it % 4]
            )
        }

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = text.columnName,
                accessorFn = { it.name }
            ),
            column<Person, Int>(
                id = "age",
                header = text.columnAge,
                accessorFn = { it.age }
            ),
            column<Person, String>(
                id = "email",
                header = text.columnEmail,
                accessorFn = { it.email }
            ),
            column<Person, String>(
                id = "department",
                header = text.columnDepartment,
                accessorFn = { it.department }
            )
        )

        PTable(
            data = data,
            columns = columns,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            optionsOf = {
                enablePagination = true
                pageSize = 5
            },
            showPagination = true
        )
    }
}

/**
 * Example 5: Full-Featured Table
 * Demonstrates all features combined: sorting + selection + pagination.
 * This shows the power of useTable - all features work together seamlessly!
 */
@Composable
private fun FullFeaturedTableExample(text: TableDemoText) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PText(
            text = text.fullFeaturedTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        PText(
            text = text.fullFeaturedDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        val data = (1..50).map {
            Person(
                id = it.toString(),
                name = "${text.employeePrefix} $it",
                age = 22 + (it % 40),
                email = "employee$it@company.com",
                department = listOf(
                    text.departmentEngineering,
                    text.departmentMarketing,
                    text.departmentDesign,
                    text.departmentSales,
                    text.departmentHr,
                )[it % 5]
            )
        }

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = text.columnName,
                accessorFn = { it.name },
                enableSorting = true
            ),
            column<Person, Int>(
                id = "age",
                header = text.columnAge,
                accessorFn = { it.age },
                enableSorting = true
            ),
            column<Person, String>(
                id = "email",
                header = text.columnEmail,
                accessorFn = { it.email },
                enableSorting = true
            ),
            column<Person, String>(
                id = "department",
                header = text.columnDepartment,
                accessorFn = { it.department },
                enableSorting = true
            )
        )

        PTable(
            data = data,
            columns = columns,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            optionsOf = {
                enableSorting = true
                enableRowSelection = true
                enablePagination = true
                pageSize = 10
            },
            showPagination = true
        )
    }
}

@Composable
private fun EmbeddedTableExample(text: TableDemoText) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        PText(
            text = text.embeddedTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PText(
            text = text.embeddedDescription,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val data = remember(
            text.userPrefix,
            text.departmentEngineering,
            text.departmentDesign,
            text.departmentMarketing,
            text.departmentSales,
        ) {
            List(100) { index ->
                Person(
                    id = "${index + 1}",
                    name = "${text.userPrefix} ${index + 1}",
                    age = 20 + (index % 50),
                    email = "user${index + 1}@example.com",
                    department = listOf(
                        text.departmentEngineering,
                        text.departmentDesign,
                        text.departmentMarketing,
                        text.departmentSales,
                    )[index % 4]
                )
            }
        }

        val columns = remember(
            text.columnId,
            text.columnName,
            text.columnAge,
            text.columnEmail,
            text.columnDepartment,
        ) {
            listOf(
                column<Person, String>(
                    id = "id",
                    header = text.columnId,
                    accessorFn = { it.id }
                ),
                column<Person, String>(
                    id = "name",
                    header = text.columnName,
                    accessorFn = { it.name }
                ),
                column<Person, Int>(
                    id = "age",
                    header = text.columnAge,
                    accessorFn = { it.age }
                ),
                column<Person, String>(
                    id = "email",
                    header = text.columnEmail,
                    accessorFn = { it.email }
                ),
                column<Person, String>(
                    id = "department",
                    header = text.columnDepartment,
                    accessorFn = { it.department }
                )
            )
        }

        PTable<Person>(
            data = data,
            columns = columns,
            scrollBehavior = TableScrollBehavior.Embedded,
            modifier = Modifier.fillMaxWidth(),
            optionsOf = {
                enablePagination = true
                pageSize = 20
            }
        )
    }
}

@Composable
@ReadOnlyComposable
private fun tableDemoText(): TableDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> TableDemoText(
        title = "表格组件示例",
        basicTitle = "1. 基础表格",
        basicDescription = "简单表格，无交互功能",
        sortableTitle = "2. 可排序表格",
        sortableDescription = "点击表头排序（useTable 自动处理排序）",
        selectableTitle = "3. 可选择表格",
        selectableDescription = "点击行或复选框选择（useTable 自动处理选择）",
        paginatedTitle = "4. 分页表格",
        paginatedDescription = "自动分页（每页 5 条）",
        fullFeaturedTitle = "5. 全功能表格",
        fullFeaturedDescription = "排序 + 选择 + 分页（全部开启）",
        embeddedTitle = "嵌入式表格（无需固定高度）",
        embeddedDescription = "使用 scrollBehavior = Embedded，表格可在滚动容器内无固定高度显示；建议启用分页（pageSize = 20）以保证性能。",
        columnId = "编号",
        columnName = "姓名",
        columnAge = "年龄",
        columnEmail = "邮箱",
        columnDepartment = "部门",
        departmentEngineering = "工程",
        departmentMarketing = "市场",
        departmentDesign = "设计",
        departmentSales = "销售",
        departmentHr = "人事",
        personPrefix = "人员",
        employeePrefix = "员工",
        userPrefix = "用户",
    )

    Language.EN_US -> TableDemoText(
        title = "Table Component Examples",
        basicTitle = "1. Basic Table",
        basicDescription = "Simple table with no interactions",
        sortableTitle = "2. Sortable Table",
        sortableDescription = "Click column headers to sort (automatic sorting by useTable)",
        selectableTitle = "3. Selectable Table",
        selectableDescription = "Click rows or checkboxes to select (automatic selection by useTable)",
        paginatedTitle = "4. Paginated Table",
        paginatedDescription = "Automatic pagination (5 items per page)",
        fullFeaturedTitle = "5. Full-Featured Table",
        fullFeaturedDescription = "Sorting + Selection + Pagination (all features enabled)",
        embeddedTitle = "Embedded Table (No Fixed Height Required)",
        embeddedDescription = "Use scrollBehavior = Embedded, so the table can render in a scrollable container without fixed height. For performance, pagination is recommended (pageSize = 20).",
        columnId = "ID",
        columnName = "Name",
        columnAge = "Age",
        columnEmail = "Email",
        columnDepartment = "Department",
        departmentEngineering = "Engineering",
        departmentMarketing = "Marketing",
        departmentDesign = "Design",
        departmentSales = "Sales",
        departmentHr = "HR",
        personPrefix = "Person",
        employeePrefix = "Employee",
        userPrefix = "User",
    )
}

private data class TableDemoText(
    val title: String,
    val basicTitle: String,
    val basicDescription: String,
    val sortableTitle: String,
    val sortableDescription: String,
    val selectableTitle: String,
    val selectableDescription: String,
    val paginatedTitle: String,
    val paginatedDescription: String,
    val fullFeaturedTitle: String,
    val fullFeaturedDescription: String,
    val embeddedTitle: String,
    val embeddedDescription: String,
    val columnId: String,
    val columnName: String,
    val columnAge: String,
    val columnEmail: String,
    val columnDepartment: String,
    val departmentEngineering: String,
    val departmentMarketing: String,
    val departmentDesign: String,
    val departmentSales: String,
    val departmentHr: String,
    val personPrefix: String,
    val employeePrefix: String,
    val userPrefix: String,
)
