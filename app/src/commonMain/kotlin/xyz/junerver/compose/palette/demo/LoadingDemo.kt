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
import xyz.junerver.compose.palette.components.loading.PLoading
import xyz.junerver.compose.palette.components.loading.PLoadingBars
import xyz.junerver.compose.palette.components.loading.PLoadingBounce
import xyz.junerver.compose.palette.components.loading.PLoadingCircle
import xyz.junerver.compose.palette.components.loading.PLoadingDots
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun LoadingDemo() {
    val text = loadingDemoText()

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

        DemoSection(title = text.defaultSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoading()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.dotsSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingDots()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.barsSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingBars()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.circleSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingCircle()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.bounceSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingBounce()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.allSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PLoadingDots()
                PLoadingBars()
                PLoadingCircle()
                PLoadingBounce()
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
private fun loadingDemoText(): LoadingDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            LoadingDemoText(
                title = "Loading",
                subtitle = "加载指示器",
                defaultSectionTitle = "默认加载",
                dotsSectionTitle = "点状加载",
                barsSectionTitle = "条状加载",
                circleSectionTitle = "圆形加载",
                bounceSectionTitle = "跳动加载",
                allSectionTitle = "所有样式",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    // 默认加载
                    PLoading()

                    // 点状加载
                    PLoadingDots()

                    // 条状加载
                    PLoadingBars()

                    // 圆形加载
                    PLoadingCircle()

                    // 跳动加载
                    PLoadingBounce()
                    """.trimIndent(),
            )

        Language.EN_US ->
            LoadingDemoText(
                title = "Loading",
                subtitle = "Loading indicators.",
                defaultSectionTitle = "Default Loading",
                dotsSectionTitle = "Dots Loading",
                barsSectionTitle = "Bars Loading",
                circleSectionTitle = "Circle Loading",
                bounceSectionTitle = "Bounce Loading",
                allSectionTitle = "All Styles",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    // Default loading
                    PLoading()

                    // Dots loading
                    PLoadingDots()

                    // Bars loading
                    PLoadingBars()

                    // Circle loading
                    PLoadingCircle()

                    // Bounce loading
                    PLoadingBounce()
                    """.trimIndent(),
            )
    }

private data class LoadingDemoText(
    val title: String,
    val subtitle: String,
    val defaultSectionTitle: String,
    val dotsSectionTitle: String,
    val barsSectionTitle: String,
    val circleSectionTitle: String,
    val bounceSectionTitle: String,
    val allSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
