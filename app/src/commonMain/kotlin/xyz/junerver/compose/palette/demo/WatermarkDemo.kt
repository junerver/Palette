package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.watermark.PWatermark

@Composable
fun WatermarkDemo() {
    val text = watermarkDemoText()

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
            PWatermark(text = "Palette") {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PText(
                        text = text.contentText,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customSectionTitle) {
            PWatermark(
                text = "CONFIDENTIAL",
                fontSize = 20.sp,
                color = Color.Red.copy(alpha = 0.1f),
                rotate = -30f,
                gapX = 80.dp,
                gapY = 80.dp,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PText(
                        text = text.contentText,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
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
private fun watermarkDemoText(): WatermarkDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            WatermarkDemoText(
                title = "PWatermark",
                subtitle = "在内容上添加水印的组件",
                basicSectionTitle = "基础用法",
                customSectionTitle = "自定义水印",
                contentText = "页面内容区域",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PWatermark(text = "Palette") {
                        // 被水印覆盖的内容
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            PText(text = "页面内容区域")
                        }
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            WatermarkDemoText(
                title = "PWatermark",
                subtitle = "A component that adds watermark overlay on content.",
                basicSectionTitle = "Basic Usage",
                customSectionTitle = "Custom Watermark",
                contentText = "Page Content Area",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PWatermark(text = "Palette") {
                        // Content covered by watermark
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            PText(text = "Page Content Area")
                        }
                    }
                    """.trimIndent(),
            )
    }

private data class WatermarkDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val customSectionTitle: String,
    val contentText: String,
    val codeTitle: String,
    val codeBlock: String,
)
