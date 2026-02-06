package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.list.PList
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ListDemo() {
    val text = listDemoText()

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

        val items = remember(text.itemPrefix) { (1..20).map { "${text.itemPrefix} $it" } }

        DemoSection(title = text.basicSectionTitle) {
            Box(modifier = Modifier.height(300.dp)) {
                PList(
                    data = items,
                    showDivider = true
                ) { item ->
                    PText(
                        text = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun listDemoText(): ListDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> ListDemoText(
        title = "PList 列表",
        subtitle = "虚拟滚动列表组件",
        basicSectionTitle = "基础列表",
        itemPrefix = "列表项",
    )

    Language.EN_US -> ListDemoText(
        title = "PList",
        subtitle = "Virtual scrolling list component",
        basicSectionTitle = "Basic List",
        itemPrefix = "List Item",
    )
}

private data class ListDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val itemPrefix: String,
)
