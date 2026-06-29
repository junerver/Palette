package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.backtop.PBacktop
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun BacktopDemo() {
    val text = backtopDemoText()

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
            val scrollState = rememberScrollState()
            Box {
                Column(
                    modifier =
                        Modifier
                            .height(300.dp)
                            .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    (1..30).forEach { index ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            PText(
                                text = "${text.itemPrefix} $index",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                PBacktop(
                    scrollState = scrollState,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customSectionTitle) {
            val scrollState = rememberScrollState()
            Box {
                Column(
                    modifier =
                        Modifier
                            .height(300.dp)
                            .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    (1..30).forEach { index ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            PText(
                                text = "${text.itemPrefix} $index",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                PBacktop(
                    scrollState = scrollState,
                    visibilityHeight = 100,
                    right = 16.dp,
                    bottom = 16.dp,
                    modifier = Modifier.align(Alignment.BottomEnd),
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
private fun backtopDemoText(): BacktopDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            BacktopDemoText(
                title = "PBacktop",
                subtitle = "点击回到页面顶部的浮动按钮",
                basicSectionTitle = "基础用法",
                customSectionTitle = "自定义配置",
                itemPrefix = "列表项",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val scrollState = rememberScrollState()

                    Box {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                        ) {
                            // 长内容
                        }
                        PBacktop(
                            scrollState = scrollState,
                            visibilityHeight = 200,
                        )
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            BacktopDemoText(
                title = "PBacktop",
                subtitle = "A floating button that scrolls back to the top of the page.",
                basicSectionTitle = "Basic Usage",
                customSectionTitle = "Custom Configuration",
                itemPrefix = "Item",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val scrollState = rememberScrollState()

                    Box {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                        ) {
                            // Long content
                        }
                        PBacktop(
                            scrollState = scrollState,
                            visibilityHeight = 200,
                        )
                    }
                    """.trimIndent(),
            )
    }

private data class BacktopDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val customSectionTitle: String,
    val itemPrefix: String,
    val codeTitle: String,
    val codeBlock: String,
)
