package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.transfer.PTransfer
import xyz.junerver.compose.palette.components.transfer.TransferItem

private val mockData = (1..10).map {
    TransferItem(
        key = "item$it",
        title = "Item $it",
        disabled = it == 3 || it == 7,
    )
}

@Composable
fun TransferDemo() {
    val text = transferDemoText()
    val (basicTargetKeys, setBasicTargetKeys) = useState<List<String>>(emptyList())
    val (searchTargetKeys, setSearchTargetKeys) = useState<List<String>>(listOf("item1", "item2"))

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            PTransfer(
                dataSource = mockData,
                targetKeys = basicTargetKeys,
                onTargetKeysChange = setBasicTargetKeys,
                titles = Pair(text.sourceTitle, text.targetTitle),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.searchableSectionTitle) {
            PTransfer(
                dataSource = mockData,
                targetKeys = searchTargetKeys,
                onTargetKeysChange = setSearchTargetKeys,
                titles = Pair(text.sourceTitle, text.targetTitle),
                searchable = true,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun transferDemoText(): TransferDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            TransferDemoText(
                title = "Transfer",
                subtitle = "穿梭框组件",
                basicSectionTitle = "基础用法",
                searchableSectionTitle = "可搜索",
                sourceTitle = "源列表",
                targetTitle = "目标列表",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val dataSource = listOf(
                        TransferItem(key = "1", title = "Item 1"),
                        TransferItem(key = "2", title = "Item 2"),
                        TransferItem(key = "3", title = "Item 3", disabled = true)
                    )
                    val (targetKeys, setTargetKeys) = useState<List<String>>(emptyList())

                    PTransfer(
                        dataSource = dataSource,
                        targetKeys = targetKeys,
                        onTargetKeysChange = setTargetKeys,
                        titles = Pair("源列表", "目标列表"),
                        searchable = true
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            TransferDemoText(
                title = "Transfer",
                subtitle = "Transfer component",
                basicSectionTitle = "Basic Usage",
                searchableSectionTitle = "Searchable",
                sourceTitle = "Source",
                targetTitle = "Target",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val dataSource = listOf(
                        TransferItem(key = "1", title = "Item 1"),
                        TransferItem(key = "2", title = "Item 2"),
                        TransferItem(key = "3", title = "Item 3", disabled = true)
                    )
                    val (targetKeys, setTargetKeys) = useState<List<String>>(emptyList())

                    PTransfer(
                        dataSource = dataSource,
                        targetKeys = targetKeys,
                        onTargetKeysChange = setTargetKeys,
                        titles = Pair("Source", "Target"),
                        searchable = true
                    )
                    """.trimIndent(),
            )
    }

private data class TransferDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val searchableSectionTitle: String,
    val sourceTitle: String,
    val targetTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
