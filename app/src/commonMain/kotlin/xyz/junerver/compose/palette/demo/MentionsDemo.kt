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
            var value by remember { mutableStateOf("") }
            PMentions(
                value = value,
                onValueChange = { value = it },
                options = mockUsers,
                placeholder = text.basicPlaceholder,
                onSelect = {},
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.prefixSectionTitle) {
            var value by remember { mutableStateOf("") }
            PMentions(
                value = value,
                onValueChange = { value = it },
                options = mockTopics,
                placeholder = text.prefixPlaceholder,
                prefix = "#",
                onSelect = {},
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
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val options = listOf(
                        MentionsOption(value = "user1", label = "张三"),
                        MentionsOption(value = "user2", label = "李四")
                    )
                    var value by remember { mutableStateOf("") }
                    PMentions(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        placeholder = "输入 @ 提及用户",
                        prefix = "@"
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
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val options = listOf(
                        MentionsOption(value = "user1", label = "Alice"),
                        MentionsOption(value = "user2", label = "Bob")
                    )
                    var value by remember { mutableStateOf("") }
                    PMentions(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        placeholder = "Type @ to mention a user",
                        prefix = "@"
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
    val codeTitle: String,
    val codeBlock: String,
)
