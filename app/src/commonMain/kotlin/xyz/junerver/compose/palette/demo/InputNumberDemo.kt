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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.inputnumber.PInputNumber
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.spec.ComponentSize

@Composable
fun InputNumberDemo() {
    val text = inputNumberDemoText()
    val basicValueState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Double?>(0.0) }
    val basicValue = basicValueState.value
    val limitedValueState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Double?>(5.0) }
    val limitedValue = limitedValueState.value
    val stepValueState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Double?>(0.0) }
    val stepValue = stepValueState.value
    val smallValueState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Double?>(0.0) }
    val smallValue = smallValueState.value
    val largeValueState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Double?>(0.0) }
    val largeValue = largeValueState.value

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
            PInputNumber(
                value = basicValue,
                onValueChange = { basicValueState.value = it },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.minMaxSectionTitle) {
            PInputNumber(
                value = limitedValue,
                onValueChange = { limitedValueState.value = it },
                min = 0.0,
                max = 10.0,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.stepSectionTitle) {
            PInputNumber(
                value = stepValue,
                onValueChange = { stepValueState.value = it },
                step = 0.5,
                precision = 1,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            PInputNumber(
                value = basicValue,
                onValueChange = {},
                disabled = true,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sizeSectionTitle) {
            Column {
                PInputNumber(
                    value = smallValue,
                    onValueChange = { smallValueState.value = it },
                    size = ComponentSize.Small,
                )
                Spacer(modifier = Modifier.height(12.dp))
                PInputNumber(
                    value = basicValue,
                    onValueChange = {},
                    size = ComponentSize.Medium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                PInputNumber(
                    value = largeValue,
                    onValueChange = { largeValueState.value = it },
                    size = ComponentSize.Large,
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
private fun inputNumberDemoText(): InputNumberDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            InputNumberDemoText(
                title = "InputNumber",
                subtitle = "数字输入框组件",
                basicSectionTitle = "基础用法",
                minMaxSectionTitle = "最小值/最大值",
                stepSectionTitle = "步长与精度",
                disabledSectionTitle = "禁用状态",
                sizeSectionTitle = "不同尺寸",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val (value, setValue) = useState<Double?>(0.0)

                    PInputNumber(
                        value = value,
                        onValueChange = setValue,
                        min = 0.0,
                        max = 10.0,
                        step = 1.0,
                        disabled = false,
                        size = ComponentSize.Medium
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            InputNumberDemoText(
                title = "InputNumber",
                subtitle = "InputNumber component",
                basicSectionTitle = "Basic Usage",
                minMaxSectionTitle = "Min / Max",
                stepSectionTitle = "Step & Precision",
                disabledSectionTitle = "Disabled",
                sizeSectionTitle = "Different Sizes",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val (value, setValue) = useState<Double?>(0.0)

                    PInputNumber(
                        value = value,
                        onValueChange = setValue,
                        min = 0.0,
                        max = 10.0,
                        step = 1.0,
                        disabled = false,
                        size = ComponentSize.Medium
                    )
                    """.trimIndent(),
            )
    }

private data class InputNumberDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val minMaxSectionTitle: String,
    val stepSectionTitle: String,
    val disabledSectionTitle: String,
    val sizeSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
