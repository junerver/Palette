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
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.toolbar.Toolbar
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults
import xyz.junerver.compose.palette.ui.theme.Primary
import xyz.junerver.compose.palette.ui.theme.Success

@Composable
fun ToolbarDemo() {
    val text = toolbarDemoText()

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
            Toolbar(
                title = text.pageTitle,
                onNavigationClick = { },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.colorSectionTitle) {
            Column {
                Toolbar(
                    title = text.primaryToneTitle,
                    colors = ToolbarDefaults.colors(backgroundColor = Primary),
                    onNavigationClick = { },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Toolbar(
                    title = text.successToneTitle,
                    colors = ToolbarDefaults.colors(backgroundColor = Success),
                    onNavigationClick = { },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.heightSectionTitle) {
            Toolbar(
                title = text.tallToolbarTitle,
                height = 72.dp,
                onNavigationClick = { },
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
private fun toolbarDemoText(): ToolbarDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ToolbarDemoText(
                title = "Toolbar",
                subtitle = "带返回按钮的顶部工具栏",
                basicSectionTitle = "基础用法",
                pageTitle = "页面标题",
                colorSectionTitle = "自定义颜色",
                primaryToneTitle = "主色调",
                successToneTitle = "成功色",
                heightSectionTitle = "自定义高度",
                tallToolbarTitle = "较高的工具栏",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    Toolbar(
                        title = "页面标题",
                        backgroundColor = Color(0xFF0F71F2),
                        height = 58.dp,
                        onIconClick = {
                            // 返回操作
                        }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            ToolbarDemoText(
                title = "Toolbar",
                subtitle = "Top toolbar with navigation button.",
                basicSectionTitle = "Basic Usage",
                pageTitle = "Page Title",
                colorSectionTitle = "Custom Colors",
                primaryToneTitle = "Primary Tone",
                successToneTitle = "Success Tone",
                heightSectionTitle = "Custom Height",
                tallToolbarTitle = "Taller Toolbar",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    Toolbar(
                        title = "Page Title",
                        backgroundColor = Color(0xFF0F71F2),
                        height = 58.dp,
                        onIconClick = {
                            // Back action
                        }
                    )
                    """.trimIndent(),
            )
    }

private data class ToolbarDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val pageTitle: String,
    val colorSectionTitle: String,
    val primaryToneTitle: String,
    val successToneTitle: String,
    val heightSectionTitle: String,
    val tallToolbarTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
