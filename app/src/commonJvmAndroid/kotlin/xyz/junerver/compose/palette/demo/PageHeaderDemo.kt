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
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.pageheader.PPageHeader
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PageHeaderDemo() {
    val text = pageHeaderDemoText()

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
            PPageHeader(
                title = text.basicTitle,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.backSectionTitle) {
            var message by remember { mutableStateOf("") }
            Column {
                PPageHeader(
                    title = text.backTitle,
                    onBack = { message = text.backClicked },
                    backText = text.backText,
                )
                if (message.isNotEmpty()) {
                    PText(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.extraSectionTitle) {
            PPageHeader(
                title = text.extraTitle,
                subtitle = text.extraSubtitle,
                onBack = {},
                backText = text.backText,
                extra = {
                    PButton(text = text.actionText, onClick = {})
                },
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
private fun pageHeaderDemoText(): PageHeaderDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            PageHeaderDemoText(
                title = "PageHeader",
                subtitle = "页头组件",
                basicSectionTitle = "基础用法",
                basicTitle = "页面标题",
                backSectionTitle = "带返回按钮",
                backTitle = "详情页",
                backText = "返回",
                backClicked = "已点击返回",
                extraSectionTitle = "带副标题和操作",
                extraTitle = "页面标题",
                extraSubtitle = "页面描述信息",
                actionText = "操作",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PPageHeader(
                        title = "页面标题",
                        subtitle = "页面描述信息",
                        onBack = { },
                        backText = "返回",
                        extra = {
                            PButton(onClick = { }) {
                                PText(text = "操作")
                            }
                        }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            PageHeaderDemoText(
                title = "PageHeader",
                subtitle = "Page header component.",
                basicSectionTitle = "Basic Usage",
                basicTitle = "Page Title",
                backSectionTitle = "With Back Button",
                backTitle = "Detail Page",
                backText = "Back",
                backClicked = "Back clicked",
                extraSectionTitle = "With Subtitle and Action",
                extraTitle = "Page Title",
                extraSubtitle = "Page description",
                actionText = "Action",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PPageHeader(
                        title = "Page Title",
                        subtitle = "Page description",
                        onBack = { },
                        backText = "Back",
                        extra = {
                            PButton(onClick = { }) {
                                PText(text = "Action")
                            }
                        }
                    )
                    """.trimIndent(),
            )
    }

private data class PageHeaderDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicTitle: String,
    val backSectionTitle: String,
    val backTitle: String,
    val backText: String,
    val backClicked: String,
    val extraSectionTitle: String,
    val extraTitle: String,
    val extraSubtitle: String,
    val actionText: String,
    val codeTitle: String,
    val codeBlock: String,
)
