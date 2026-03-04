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
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.switch.PSwitch

@Composable
fun SwitchDemo() {
    val text = switchDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var checked1 by remember { mutableStateOf(false) }
                PSwitch(
                    checked = checked1,
                    onChange = { checked1 = it }
                )
                PText(
                    text = "${text.statusPrefix}${if (checked1) text.onText else text.offText}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.defaultOnSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var checked2 by remember { mutableStateOf(true) }
                PSwitch(
                    checked = checked2,
                    onChange = { checked2 = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PSwitch(checked = false, onChange = null, disabled = true)
                PSwitch(checked = true, onChange = null, disabled = true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock
        )
    }
}

@Composable
@ReadOnlyComposable
private fun switchDemoText(): SwitchDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> SwitchDemoText(
        title = "Switch",
        subtitle = "开关组件",
        basicSectionTitle = "基础用法",
        statusPrefix = "状态: ",
        onText = "开启",
        offText = "关闭",
        defaultOnSectionTitle = "默认开启",
        disabledSectionTitle = "禁用状态",
        codeTitle = "代码示例",
        codeBlock = """
var checked by remember { mutableStateOf(false) }
PSwitch(
    checked = checked,
    onChange = { checked = it }
)
        """.trimIndent(),
    )

    Language.EN_US -> SwitchDemoText(
        title = "Switch",
        subtitle = "Switch component.",
        basicSectionTitle = "Basic Usage",
        statusPrefix = "Status: ",
        onText = "On",
        offText = "Off",
        defaultOnSectionTitle = "Default On",
        disabledSectionTitle = "Disabled State",
        codeTitle = "Code Example",
        codeBlock = """
var checked by remember { mutableStateOf(false) }
PSwitch(
    checked = checked,
    onChange = { checked = it }
)
        """.trimIndent(),
    )
}

private data class SwitchDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val statusPrefix: String,
    val onText: String,
    val offText: String,
    val defaultOnSectionTitle: String,
    val disabledSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
