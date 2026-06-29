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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.colorpicker.PColorPicker
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ColorPickerDemo() {
    val text = colorPickerDemoText()

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
            var color by remember { mutableStateOf(Color.Red) }
            PColorPicker(
                color = color,
                onColorChange = { color = it },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.presetSectionTitle) {
            var color by remember { mutableStateOf(Color(0xFF6200EE)) }
            PColorPicker(
                color = color,
                onColorChange = { color = it },
                presetColors =
                    listOf(
                        Color.Red,
                        Color(0xFFFF9800),
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color(0xFF6200EE),
                    ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.hexSectionTitle) {
            var color by remember { mutableStateOf(Color(0xFF03A9F4)) }
            PColorPicker(
                color = color,
                onColorChange = { color = it },
                showHex = true,
                showAlpha = true,
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
private fun colorPickerDemoText(): ColorPickerDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ColorPickerDemoText(
                title = "PColorPicker",
                subtitle = "颜色选择器组件",
                basicSectionTitle = "基础用法",
                presetSectionTitle = "预设颜色",
                hexSectionTitle = "十六进制输入",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var color by remember { mutableStateOf(Color.Red) }
                    PColorPicker(
                        color = color,
                        onColorChange = { color = it },
                        presetColors = listOf(Color.Red, Color.Blue),
                        showHex = true,
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            ColorPickerDemoText(
                title = "PColorPicker",
                subtitle = "Color picker component.",
                basicSectionTitle = "Basic Usage",
                presetSectionTitle = "Preset Colors",
                hexSectionTitle = "Hex Input",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var color by remember { mutableStateOf(Color.Red) }
                    PColorPicker(
                        color = color,
                        onColorChange = { color = it },
                        presetColors = listOf(Color.Red, Color.Blue),
                        showHex = true,
                    )
                    """.trimIndent(),
            )
    }

private data class ColorPickerDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val presetSectionTitle: String,
    val hexSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
