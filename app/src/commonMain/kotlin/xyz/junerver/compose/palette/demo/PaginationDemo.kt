package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.pagination.PPagination
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PaginationDemo() {
    val text = paginationDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        var currentPage by remember { mutableStateOf(1) }

        DemoSection(title = text.basicSectionTitle) {
            PPagination(
                currentPage = currentPage,
                totalPages = 10,
                onPageChange = { currentPage = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        var simplePage by remember { mutableStateOf(1) }

        DemoSection(title = text.simpleSectionTitle) {
            PPagination(
                currentPage = simplePage,
                totalPages = 10,
                onPageChange = { simplePage = it },
                simple = true
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun paginationDemoText(): PaginationDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> PaginationDemoText(
        title = "PPagination 分页器",
        subtitle = "用于数据分页的组件",
        basicSectionTitle = "基础分页器",
        simpleSectionTitle = "简洁模式",
    )

    Language.EN_US -> PaginationDemoText(
        title = "PPagination",
        subtitle = "A component for data pagination",
        basicSectionTitle = "Basic Pagination",
        simpleSectionTitle = "Simple Mode",
    )
}

private data class PaginationDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val simpleSectionTitle: String,
)
