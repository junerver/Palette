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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.toggle.PToggle
import xyz.junerver.compose.palette.components.toggle.PToggleGroup

@Composable
fun ToggleDemo() {
    val text = toggleDemoText()

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
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                var pressed by remember { mutableStateOf(false) }
                PToggle(
                    pressed = pressed,
                    onPressedChange = { pressed = it },
                ) {
                    PText(
                        text = if (pressed) text.toggleOn else text.toggleOff,
                    )
                }
                PText(
                    text = "${text.statusPrefix}${if (pressed) text.pressedText else text.unpressedText}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.singleSectionTitle) {
            var selected by remember { mutableStateOf(listOf("left")) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PToggleGroup(
                    value = selected,
                    onValueChange = { selected = it },
                ) {
                    PToggleItem(value = "left", label = text.leftText)
                    PToggleItem(value = "center", label = text.centerText)
                    PToggleItem(value = "right", label = text.rightText)
                }
                PText(
                    text = "${text.selectedPrefix}${selected.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.multipleSectionTitle) {
            var selected by remember { mutableStateOf(listOf("bold")) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PToggleGroup(
                    value = selected,
                    onValueChange = { selected = it },
                    multiple = true,
                ) {
                    PToggleItem(value = "bold", label = text.boldText)
                    PToggleItem(value = "italic", label = text.italicText)
                    PToggleItem(value = "underline", label = text.underlineText)
                }
                PText(
                    text = "${text.selectedPrefix}${selected.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
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
private fun toggleDemoText(): ToggleDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ToggleDemoText(
                title = "Toggle",
                subtitle = "开关按钮组件",
                basicSectionTitle = "基础用法",
                toggleOn = "已开启",
                toggleOff = "已关闭",
                statusPrefix = "状态: ",
                pressedText = "按下",
                unpressedText = "未按下",
                singleSectionTitle = "单选",
                leftText = "左",
                centerText = "中",
                rightText = "右",
                selectedPrefix = "已选: ",
                multipleSectionTitle = "多选",
                boldText = "粗体",
                italicText = "斜体",
                underlineText = "下划线",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var selected by remember { mutableStateOf(listOf("left")) }
                    PToggleGroup(
                        value = selected,
                        onValueChange = { selected = it }
                    ) {
                        PToggleItem(value = "left", label = "左")
                        PToggleItem(value = "center", label = "中")
                        PToggleItem(value = "right", label = "右")
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            ToggleDemoText(
                title = "Toggle",
                subtitle = "Toggle button component.",
                basicSectionTitle = "Basic Usage",
                toggleOn = "On",
                toggleOff = "Off",
                statusPrefix = "Status: ",
                pressedText = "Pressed",
                unpressedText = "Unpressed",
                singleSectionTitle = "Single Select",
                leftText = "Left",
                centerText = "Center",
                rightText = "Right",
                selectedPrefix = "Selected: ",
                multipleSectionTitle = "Multiple Select",
                boldText = "Bold",
                italicText = "Italic",
                underlineText = "Underline",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var selected by remember { mutableStateOf(listOf("left")) }
                    PToggleGroup(
                        value = selected,
                        onValueChange = { selected = it }
                    ) {
                        PToggleItem(value = "left", label = "Left")
                        PToggleItem(value = "center", label = "Center")
                        PToggleItem(value = "right", label = "Right")
                    }
                    """.trimIndent(),
            )
    }

private data class ToggleDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val toggleOn: String,
    val toggleOff: String,
    val statusPrefix: String,
    val pressedText: String,
    val unpressedText: String,
    val singleSectionTitle: String,
    val leftText: String,
    val centerText: String,
    val rightText: String,
    val selectedPrefix: String,
    val multipleSectionTitle: String,
    val boldText: String,
    val italicText: String,
    val underlineText: String,
    val codeTitle: String,
    val codeBlock: String,
)
