package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.pagination.PPagination

@Composable
fun PaginationDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PPagination 分页器",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "用于数据分页的组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        var currentPage by remember { mutableStateOf(1) }

        DemoSection(title = "基础分页器") {
            PPagination(
                currentPage = currentPage,
                totalPages = 10,
                onPageChange = { currentPage = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        var simplePage by remember { mutableStateOf(1) }

        DemoSection(title = "简洁模式") {
            PPagination(
                currentPage = simplePage,
                totalPages = 10,
                onPageChange = { simplePage = it },
                simple = true
            )
        }
    }
}
