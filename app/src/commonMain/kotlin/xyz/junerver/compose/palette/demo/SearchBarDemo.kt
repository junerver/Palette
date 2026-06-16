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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.searchbar.PSearchBar
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun SearchBarDemo() {
    val text = searchBarDemoText()
    var controlledValue by remember { mutableStateOf("") }

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
            PSearchBar(
                value = "",
                onValueChange = {},
                placeholder = text.basicPlaceholder,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.controlledSectionTitle) {
            Column {
                PSearchBar(
                    value = controlledValue,
                    onValueChange = { controlledValue = it },
                    placeholder = text.controlledPlaceholder,
                    onSearch = {},
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = "${text.currentValuePrefix}$controlledValue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
private fun searchBarDemoText(): SearchBarDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            SearchBarDemoText(
                title = "SearchBar",
                subtitle = "搜索栏组件",
                basicSectionTitle = "基础用法",
                basicPlaceholder = "搜索",
                controlledSectionTitle = "受控模式",
                controlledPlaceholder = "输入关键词搜索",
                currentValuePrefix = "当前输入：",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var value by remember { mutableStateOf("") }

                    PSearchBar(
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "搜索",
                        onSearch = { query -> }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            SearchBarDemoText(
                title = "SearchBar",
                subtitle = "Search bar component",
                basicSectionTitle = "Basic Usage",
                basicPlaceholder = "Search",
                controlledSectionTitle = "Controlled Mode",
                controlledPlaceholder = "Enter keywords to search",
                currentValuePrefix = "Current input: ",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var value by remember { mutableStateOf("") }

                    PSearchBar(
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Search",
                        onSearch = { query -> }
                    )
                    """.trimIndent(),
            )
    }

private data class SearchBarDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicPlaceholder: String,
    val controlledSectionTitle: String,
    val controlledPlaceholder: String,
    val currentValuePrefix: String,
    val codeTitle: String,
    val codeBlock: String,
)
