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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.mentions.MentionsOption
import xyz.junerver.compose.palette.components.mentions.PMentions
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun MentionsDemo() {
    val text = mentionsDemoText()

    val mockUsers = listOf(
        MentionsOption(value = "user1", label = "张三"),
        MentionsOption(value = "user2", label = "李四"),
        MentionsOption(value = "user3", label = "王五"),
        MentionsOption(value = "user4", label = "赵六", disabled = true),
    )

    val mockTopics = listOf(
        MentionsOption(value = "topic1", label = "设计规范"),
        MentionsOption(value = "topic2", label = "组件库"),
        MentionsOption(value = "topic3", label = "Compose"),
    )

    val remoteUsers = listOf(
        MentionsOption(value = "remote1", label = "Alice"),
        MentionsOption(value = "remote2", label = "Bob"),
        MentionsOption(value = "remote3", label = "Charlie"),
        MentionsOption(value = "remote4", label = "Diana"),
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
            val (value, setValue) = useState("")
            PMentions(
                value = value,
                onValueChange = setValue,
                options = mockUsers,
                placeholder = text.basicPlaceholder,
                onSelect = {},
                highlight = true,
                highlightColor = Color(0xFFE8F3FF),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.prefixSectionTitle) {
            val (value, setValue) = useState("")
            PMentions(
                value = value,
                onValueChange = setValue,
                options = mockTopics,
                placeholder = text.prefixPlaceholder,
                prefix = "#",
                onSelect = {},
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.asyncSectionTitle) {
            val (value, setValue) = useState("")
            val (query, setQuery) = useState("")
            val (searchVersion, setSearchVersion) = useState(0)
            val (loading, setLoading) = useState(false)
            val (options, setOptions) = useState<List<MentionsOption>>(emptyList())

            LaunchedEffect(query, searchVersion) {
                if (query.isEmpty() && !value.endsWith("@")) {
                    setOptions(emptyList())
                    setLoading(false)
                    return@LaunchedEffect
                }
                setLoading(true)
                delay(500)
                setOptions(
                    remoteUsers.filter {
                        query.isBlank() ||
                            it.label.contains(query, ignoreCase = true) ||
                            it.value.contains(query, ignoreCase = true)
                    }
                )
                setLoading(false)
            }

            PMentions(
                value = value,
                onValueChange = setValue,
                options = options,
                placeholder = text.asyncPlaceholder,
                onSearch = {
                    setQuery(it)
                    setSearchVersion(searchVersion + 1)
                },
                loading = loading,
                highlight = true,
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
private fun mentionsDemoText(): MentionsDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            MentionsDemoText(
                title = "Mentions",
                subtitle = "提及组件，用于在输入中@某人或某话题",
                basicSectionTitle = "基础用法",
                basicPlaceholder = "输入 @ 提及用户",
                prefixSectionTitle = "自定义前缀",
                prefixPlaceholder = "输入 # 提及话题",
                asyncSectionTitle = "延迟加载",
                asyncPlaceholder = "输入 @ 后加载远程用户",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val options = listOf(
                        MentionsOption(value = "user1", label = "张三"),
                        MentionsOption(value = "user2", label = "李四")
                    )
                    val (value, setValue) = useState("")
                    PMentions(
                        value = value,
                        onValueChange = setValue,
                        options = options,
                        placeholder = "输入 @ 提及用户",
                        prefix = "@",
                        onSearch = { query ->
                            // 输入 @ 后可以按 query 请求网络并更新 options
                        },
                        loading = loading,
                        highlight = true,
                        highlightColor = Color(0xFFE8F3FF)
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            MentionsDemoText(
                title = "Mentions",
                subtitle = "Mentions component for @mentioning users or topics.",
                basicSectionTitle = "Basic Usage",
                basicPlaceholder = "Type @ to mention a user",
                prefixSectionTitle = "Custom Prefix",
                prefixPlaceholder = "Type # to mention a topic",
                asyncSectionTitle = "Lazy Loading",
                asyncPlaceholder = "Type @ to load remote users",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val options = listOf(
                        MentionsOption(value = "user1", label = "Alice"),
                        MentionsOption(value = "user2", label = "Bob")
                    )
                    val (value, setValue) = useState("")
                    PMentions(
                        value = value,
                        onValueChange = setValue,
                        options = options,
                        placeholder = "Type @ to mention a user",
                        prefix = "@",
                        onSearch = { query ->
                            // Request remote options with query and update options.
                        },
                        loading = loading,
                        highlight = true,
                        highlightColor = Color(0xFFE8F3FF)
                    )
                    """.trimIndent(),
            )
    }

private data class MentionsDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicPlaceholder: String,
    val prefixSectionTitle: String,
    val prefixPlaceholder: String,
    val asyncSectionTitle: String,
    val asyncPlaceholder: String,
    val codeTitle: String,
    val codeBlock: String,
)
