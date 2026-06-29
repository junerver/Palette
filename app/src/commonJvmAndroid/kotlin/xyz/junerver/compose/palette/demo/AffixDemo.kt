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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.affix.PAffix
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun AffixDemo() {
    val text = affixDemoText()

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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PAffix {
                    PButton(text = text.affixButtonText, type = ButtonType.PRIMARY) {}
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.offsetSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PAffix(offset = 16.dp) {
                    PButton(text = text.offsetButtonText, type = ButtonType.PRIMARY) {}
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
private fun affixDemoText(): AffixDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            AffixDemoText(
                title = "Affix",
                subtitle = "固钉组件",
                basicSectionTitle = "基础用法",
                affixButtonText = "固定在顶部",
                offsetSectionTitle = "偏移距离",
                offsetButtonText = "偏移 16dp",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PAffix {
                        PButton(text = "固定在顶部") {}
                    }

                    PAffix(offset = 16.dp) {
                        PButton(text = "偏移 16dp") {}
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            AffixDemoText(
                title = "Affix",
                subtitle = "Affix component.",
                basicSectionTitle = "Basic Usage",
                affixButtonText = "Stick to Top",
                offsetSectionTitle = "With Offset",
                offsetButtonText = "Offset 16dp",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PAffix {
                        PButton(text = "Stick to Top") {}
                    }

                    PAffix(offset = 16.dp) {
                        PButton(text = "Offset 16dp") {}
                    }
                    """.trimIndent(),
            )
    }

private data class AffixDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val affixButtonText: String,
    val offsetSectionTitle: String,
    val offsetButtonText: String,
    val codeTitle: String,
    val codeBlock: String,
)
