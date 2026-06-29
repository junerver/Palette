package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
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
import xyz.junerver.compose.palette.components.autocomplete.AutocompleteOption
import xyz.junerver.compose.palette.components.autocomplete.PAutocomplete
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun AutocompleteDemo() {
    val text = autocompleteDemoText()

    val options =
        listOf(
            AutocompleteOption(value = "react", label = "React"),
            AutocompleteOption(value = "vue", label = "Vue"),
            AutocompleteOption(value = "angular", label = "Angular"),
            AutocompleteOption(value = "svelte", label = "Svelte"),
            AutocompleteOption(value = "solid", label = "Solid"),
            AutocompleteOption(value = "compose", label = "Compose Multiplatform"),
        )

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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value1 by remember { mutableStateOf("") }
                PAutocomplete(
                    value = value1,
                    onValueChange = { value1 = it },
                    options = options,
                    placeholder = text.placeholder,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customFilterSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value2 by remember { mutableStateOf("") }
                PAutocomplete(
                    value = value2,
                    onValueChange = { value2 = it },
                    options = options,
                    placeholder = text.customFilterPlaceholder,
                    filterOption = { query, option ->
                        option.label.startsWith(query, ignoreCase = true)
                    },
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
private fun autocompleteDemoText(): AutocompleteDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            AutocompleteDemoText(
                title = "PAutocomplete",
                subtitle = "自动完成输入框组件",
                basicSectionTitle = "基础用法",
                placeholder = "请输入搜索内容",
                customFilterSectionTitle = "自定义过滤",
                customFilterPlaceholder = "仅匹配前缀",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val options = listOf(
                        AutocompleteOption(value = "react", label = "React"),
                        AutocompleteOption(value = "vue", label = "Vue"),
                    )
                    PAutocomplete(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        placeholder = "请输入搜索内容",
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            AutocompleteDemoText(
                title = "PAutocomplete",
                subtitle = "Autocomplete input component.",
                basicSectionTitle = "Basic Usage",
                placeholder = "Enter search content",
                customFilterSectionTitle = "Custom Filter",
                customFilterPlaceholder = "Prefix match only",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val options = listOf(
                        AutocompleteOption(value = "react", label = "React"),
                        AutocompleteOption(value = "vue", label = "Vue"),
                    )
                    PAutocomplete(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        placeholder = "Enter search content",
                    )
                    """.trimIndent(),
            )
    }

private data class AutocompleteDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val placeholder: String,
    val customFilterSectionTitle: String,
    val customFilterPlaceholder: String,
    val codeTitle: String,
    val codeBlock: String,
)
