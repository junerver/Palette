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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.slider.PSlider
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun SliderDemo() {
    val text = sliderDemoText()

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
            var value1 by remember { mutableFloatStateOf(50f) }
            PSlider(
                value = value1,
                onChange = { value1 = it },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.rangeSectionTitle) {
            var value2 by remember { mutableFloatStateOf(25f) }
            PSlider(
                value = value2,
                onChange = { value2 = it },
                range = 0f..50f,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.stepSectionTitle) {
            var value3 by remember { mutableFloatStateOf(10f) }
            PSlider(
                value = value3,
                onChange = { value3 = it },
                step = 10,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.formatSectionTitle) {
            var value4 by remember { mutableFloatStateOf(50f) }
            PSlider(
                value = value4,
                onChange = { value4 = it },
                formatter = { "${it.toInt()}%" },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            PSlider(
                value = 30f,
                onChange = {},
                disabled = true,
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
private fun sliderDemoText(): SliderDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            SliderDemoText(
                title = "Slider",
                subtitle = "滑块组件",
                basicSectionTitle = "基础用法",
                rangeSectionTitle = "自定义范围",
                stepSectionTitle = "步进值",
                formatSectionTitle = "自定义格式化",
                disabledSectionTitle = "禁用状态",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var value by remember { mutableFloatStateOf(50f) }
                    PSlider(
                        value = value,
                        onChange = { value = it },
                        range = 0f..100f,
                        step = 1
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            SliderDemoText(
                title = "Slider",
                subtitle = "Slider component.",
                basicSectionTitle = "Basic Usage",
                rangeSectionTitle = "Custom Range",
                stepSectionTitle = "Step Value",
                formatSectionTitle = "Custom Formatter",
                disabledSectionTitle = "Disabled State",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var value by remember { mutableFloatStateOf(50f) }
                    PSlider(
                        value = value,
                        onChange = { value = it },
                        range = 0f..100f,
                        step = 1
                    )
                    """.trimIndent(),
            )
    }

private data class SliderDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val rangeSectionTitle: String,
    val stepSectionTitle: String,
    val formatSectionTitle: String,
    val disabledSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
