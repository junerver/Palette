package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.progress.PDashboardProgress
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun DashboardProgressDemo() {
    val text = dashboardProgressDemoText()

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PDashboardProgress(percent = 75f)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.percentSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PDashboardProgress(percent = 25f)
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(text = "25%", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PDashboardProgress(percent = 50f)
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(text = "50%", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PDashboardProgress(percent = 75f)
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(text = "75%", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PDashboardProgress(percent = 100f)
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(text = "100%", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.formatSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PDashboardProgress(
                    percent = 60f,
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
private fun dashboardProgressDemoText(): DashboardProgressDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            DashboardProgressDemoText(
                title = "DashboardProgress",
                subtitle = "仪表盘进度组件",
                basicSectionTitle = "基础用法",
                percentSectionTitle = "不同百分比",
                formatSectionTitle = "自定义格式化",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PDashboardProgress(percent = 75f)

                    PDashboardProgress(
                        percent = 60f,
                        formatter = { "${'$'}{it.toInt()}/100" }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            DashboardProgressDemoText(
                title = "DashboardProgress",
                subtitle = "Dashboard progress component",
                basicSectionTitle = "Basic Usage",
                percentSectionTitle = "Different Percentages",
                formatSectionTitle = "Custom Formatter",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PDashboardProgress(percent = 75f)

                    PDashboardProgress(
                        percent = 60f,
                        formatter = { "${'$'}{it.toInt()}/100" }
                    )
                    """.trimIndent(),
            )
    }

private data class DashboardProgressDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val percentSectionTitle: String,
    val formatSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
