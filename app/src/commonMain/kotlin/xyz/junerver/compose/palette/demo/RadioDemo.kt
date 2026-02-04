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
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.radio.PRadio
import xyz.junerver.compose.palette.components.radio.PRadioGroup
import xyz.junerver.compose.palette.components.radio.RadioOption

@Composable
fun RadioDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "Radio",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "单选框组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var selected by remember { mutableStateOf(false) }
                PRadio(
                    label = "选项 1",
                    checked = selected,
                    onClick = { selected = !selected }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "单选组") {
            var selectedValue by remember { mutableStateOf<String?>("option1") }
            PRadioGroup(
                options = listOf(
                    RadioOption(label = "选项 1", value = "option1"),
                    RadioOption(label = "选项 2", value = "option2"),
                    RadioOption(label = "选项 3", value = "option3")
                ),
                value = selectedValue,
                onChange = { selectedValue = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "带描述") {
            var selectedValue by remember { mutableStateOf<String?>("opt1") }
            PRadioGroup(
                options = listOf(
                    RadioOption(
                        label = "选项 1",
                        value = "opt1",
                        description = "这是选项 1 的描述"
                    ),
                    RadioOption(
                        label = "选项 2",
                        value = "opt2",
                        description = "这是选项 2 的描述"
                    )
                ),
                value = selectedValue,
                onChange = { selectedValue = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "禁用状态") {
            var selectedValue by remember { mutableStateOf<String?>("opt1") }
            PRadioGroup(
                options = listOf(
                    RadioOption(label = "正常选项", value = "opt1"),
                    RadioOption(label = "禁用选项", value = "opt2", disabled = true)
                ),
                value = selectedValue,
                onChange = { selectedValue = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
var selectedValue by remember { mutableStateOf<String?>("option1") }
PRadioGroup(
    options = listOf(
        RadioOption(label = "选项 1", value = "option1"),
        RadioOption(label = "选项 2", value = "option2"),
        RadioOption(label = "选项 3", value = "option3")
    ),
    value = selectedValue,
    onChange = { selectedValue = it }
)
            """.trimIndent()
        )
    }
}
