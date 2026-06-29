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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.progress.PCircleProgress
import xyz.junerver.compose.palette.components.progress.PProgress
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ProgressDemo() {
    val text = progressDemoText()

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

        DemoSection(title = text.linearSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                var progress1 by remember { mutableFloatStateOf(30f) }
                PProgress(percent = progress1)

                var progress2 by remember { mutableFloatStateOf(60f) }
                PProgress(percent = progress2)

                var progress3 by remember { mutableFloatStateOf(90f) }
                PProgress(percent = progress3)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.circleSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                var circleProgress by remember { mutableFloatStateOf(75f) }
                PCircleProgress(percent = circleProgress)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.formatSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PProgress(
                    percent = 50f,
                    formatter = { "${it.toInt()}/100" },
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
private fun progressDemoText(): ProgressDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ProgressDemoText(
                title = "Progress",
                subtitle = "进度条组件",
                linearSectionTitle = "线性进度条",
                circleSectionTitle = "圆形进度条",
                formatSectionTitle = "自定义格式化",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    // 线性进度条
                    PProgress(percent = 60f)

                    // 圆形进度条
                    PCircleProgress(percent = 75f)

                    // 自定义格式化
                    PProgress(
                        percent = 50f,
                        formatter = { "${'$'}{it.toInt()}/100" }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            ProgressDemoText(
                title = "Progress",
                subtitle = "Progress component.",
                linearSectionTitle = "Linear Progress",
                circleSectionTitle = "Circular Progress",
                formatSectionTitle = "Custom Formatter",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    // Linear progress
                    PProgress(percent = 60f)

                    // Circular progress
                    PCircleProgress(percent = 75f)

                    // Custom formatter
                    PProgress(
                        percent = 50f,
                        formatter = { "${'$'}{it.toInt()}/100" }
                    )
                    """.trimIndent(),
            )
    }

private data class ProgressDemoText(
    val title: String,
    val subtitle: String,
    val linearSectionTitle: String,
    val circleSectionTitle: String,
    val formatSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
