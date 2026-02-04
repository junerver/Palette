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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.usetable.core.column
import xyz.junerver.compose.hooks.usetable.state.PaginationState
import xyz.junerver.compose.palette.components.table.PTable
import xyz.junerver.compose.palette.components.table.TableScrollBehavior
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class Person(
    val id: String,
    val name: String,
    val age: Int,
    val email: String,
    val department: String
)

@Composable
fun TableDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "Table Component Examples",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        // Example 1: Basic Table
        BasicTableExample()

        // Example 2: Sortable Table
        SortableTableExample()

        // Example 3: Table with Row Selection
        SelectableTableExample()

        // Example 4: Table with Pagination
        PaginatedTableExample()

        // Example 5: Full-Featured Table
        FullFeaturedTableExample()

        // Example 6: Embedded Table (No Fixed Height Required)
        EmbeddedTableExample()
    }
}

/**
 * Example 1: Basic Table
 * Demonstrates the simplest usage with minimal configuration.
 */
@Composable
private fun BasicTableExample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "1. Basic Table",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Simple table with no interactions",
            style = MaterialTheme.typography.bodyMedium
        )

        val data = listOf(
            Person("1", "Alice", 28, "alice@example.com", "Engineering"),
            Person("2", "Bob", 32, "bob@example.com", "Marketing"),
            Person("3", "Charlie", 25, "charlie@example.com", "Design")
        )

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = "Name",
                accessorFn = { it.name }
            ),
            column<Person, Int>(
                id = "age",
                header = "Age",
                accessorFn = { it.age }
            ),
            column<Person, String>(
                id = "department",
                header = "Department",
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
private fun SortableTableExample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "2. Sortable Table",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Click column headers to sort (automatic sorting by useTable)",
            style = MaterialTheme.typography.bodyMedium
        )

        val data = listOf(
            Person("1", "Alice", 28, "alice@example.com", "Engineering"),
            Person("2", "Bob", 32, "bob@example.com", "Marketing"),
            Person("3", "Charlie", 25, "charlie@example.com", "Design"),
            Person("4", "David", 35, "david@example.com", "Engineering"),
            Person("5", "Eve", 29, "eve@example.com", "Marketing")
        )

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = "Name",
                accessorFn = { it.name },
                enableSorting = true
            ),
            column<Person, Int>(
                id = "age",
                header = "Age",
                accessorFn = { it.age },
                enableSorting = true
            ),
            column<Person, String>(
                id = "email",
                header = "Email",
                accessorFn = { it.email },
                enableSorting = true
            ),
            column<Person, String>(
                id = "department",
                header = "Department",
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
private fun SelectableTableExample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "3. Selectable Table",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Click rows or checkboxes to select (automatic selection by useTable)",
            style = MaterialTheme.typography.bodyMedium
        )

        val data = listOf(
            Person("1", "Alice", 28, "alice@example.com", "Engineering"),
            Person("2", "Bob", 32, "bob@example.com", "Marketing"),
            Person("3", "Charlie", 25, "charlie@example.com", "Design"),
            Person("4", "David", 35, "david@example.com", "Engineering")
        )

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = "Name",
                accessorFn = { it.name }
            ),
            column<Person, Int>(
                id = "age",
                header = "Age",
                accessorFn = { it.age }
            ),
            column<Person, String>(
                id = "department",
                header = "Department",
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
private fun PaginatedTableExample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "4. Paginated Table",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Automatic pagination (5 items per page)",
            style = MaterialTheme.typography.bodyMedium
        )

        val data = (1..20).map {
            Person(
                id = it.toString(),
                name = "Person $it",
                age = 20 + (it % 30),
                email = "person$it@example.com",
                department = listOf("Engineering", "Marketing", "Design", "Sales")[it % 4]
            )
        }

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = "Name",
                accessorFn = { it.name }
            ),
            column<Person, Int>(
                id = "age",
                header = "Age",
                accessorFn = { it.age }
            ),
            column<Person, String>(
                id = "email",
                header = "Email",
                accessorFn = { it.email }
            ),
            column<Person, String>(
                id = "department",
                header = "Department",
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
private fun FullFeaturedTableExample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "5. Full-Featured Table",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sorting + Selection + Pagination (all features enabled)",
            style = MaterialTheme.typography.bodyMedium
        )

        val data = (1..50).map {
            Person(
                id = it.toString(),
                name = "Employee $it",
                age = 22 + (it % 40),
                email = "employee$it@company.com",
                department = listOf("Engineering", "Marketing", "Design", "Sales", "HR")[it % 5]
            )
        }

        val columns = listOf(
            column<Person, String>(
                id = "name",
                header = "Name",
                accessorFn = { it.name },
                enableSorting = true
            ),
            column<Person, Int>(
                id = "age",
                header = "Age",
                accessorFn = { it.age },
                enableSorting = true
            ),
            column<Person, String>(
                id = "email",
                header = "Email",
                accessorFn = { it.email },
                enableSorting = true
            ),
            column<Person, String>(
                id = "department",
                header = "Department",
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
private fun EmbeddedTableExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Embedded Table (No Fixed Height Required)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "使用 scrollBehavior = Embedded 模式，表格可以在可滚动容器中无需固定高度。" +
                    "为保持性能，建议启用分页（pageSize = 20）。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val data = remember {
            List(100) { index ->
                Person(
                    id = "${index + 1}",
                    name = "User ${index + 1}",
                    age = 20 + (index % 50),
                    email = "user${index + 1}@example.com",
                    department = listOf("Engineering", "Design", "Marketing", "Sales")[index % 4]
                )
            }
        }

        val columns = remember {
            listOf(
                column<Person, String>(
                    id = "id",
                    header = "ID",
                    accessorFn = { it.id }
                ),
                column<Person, String>(
                    id = "name",
                    header = "Name",
                    accessorFn = { it.name }
                ),
                column<Person, Int>(
                    id = "age",
                    header = "Age",
                    accessorFn = { it.age }
                ),
                column<Person, String>(
                    id = "email",
                    header = "Email",
                    accessorFn = { it.email }
                ),
                column<Person, String>(
                    id = "department",
                    header = "Department",
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
