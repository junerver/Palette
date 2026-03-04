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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.radio.PRadio
import xyz.junerver.compose.palette.components.radio.PRadioGroup
import xyz.junerver.compose.palette.components.radio.RadioOption

@Composable
fun RadioDemo() {
    val text = radioDemoText()

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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var selected by remember { mutableStateOf(false) }
                PRadio(
                    label = text.option1,
                    checked = selected,
                    onClick = { selected = !selected }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.groupSectionTitle) {
            var selectedValue by remember { mutableStateOf<String?>("option1") }
            PRadioGroup(
                options = listOf(
                    RadioOption(label = text.option1, value = "option1"),
                    RadioOption(label = text.option2, value = "option2"),
                    RadioOption(label = text.option3, value = "option3")
                ),
                value = selectedValue,
                onChange = { selectedValue = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.descriptionSectionTitle) {
            var selectedValue by remember { mutableStateOf<String?>("opt1") }
            PRadioGroup(
                options = listOf(
                    RadioOption(
                        label = text.option1,
                        value = "opt1",
                        description = text.option1Description
                    ),
                    RadioOption(
                        label = text.option2,
                        value = "opt2",
                        description = text.option2Description
                    )
                ),
                value = selectedValue,
                onChange = { selectedValue = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            var selectedValue by remember { mutableStateOf<String?>("opt1") }
            PRadioGroup(
                options = listOf(
                    RadioOption(label = text.normalOption, value = "opt1"),
                    RadioOption(label = text.disabledOption, value = "opt2", disabled = true)
                ),
                value = selectedValue,
                onChange = { selectedValue = it }
            )
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
private fun radioDemoText(): RadioDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> RadioDemoText(
        title = "Radio",
        subtitle = "单选框组件",
        basicSectionTitle = "基础用法",
        groupSectionTitle = "单选组",
        descriptionSectionTitle = "带描述",
        disabledSectionTitle = "禁用状态",
        option1 = "选项 1",
        option2 = "选项 2",
        option3 = "选项 3",
        option1Description = "这是选项 1 的描述",
        option2Description = "这是选项 2 的描述",
        normalOption = "正常选项",
        disabledOption = "禁用选项",
        codeTitle = "代码示例",
        codeBlock = """
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
        """.trimIndent(),
    )

    Language.EN_US -> RadioDemoText(
        title = "Radio",
        subtitle = "Radio component.",
        basicSectionTitle = "Basic Usage",
        groupSectionTitle = "Radio Group",
        descriptionSectionTitle = "With Description",
        disabledSectionTitle = "Disabled State",
        option1 = "Option 1",
        option2 = "Option 2",
        option3 = "Option 3",
        option1Description = "Description for option 1",
        option2Description = "Description for option 2",
        normalOption = "Normal Option",
        disabledOption = "Disabled Option",
        codeTitle = "Code Example",
        codeBlock = """
var selectedValue by remember { mutableStateOf<String?>("option1") }
PRadioGroup(
    options = listOf(
        RadioOption(label = "Option 1", value = "option1"),
        RadioOption(label = "Option 2", value = "option2"),
        RadioOption(label = "Option 3", value = "option3")
    ),
    value = selectedValue,
    onChange = { selectedValue = it }
)
        """.trimIndent(),
    )
}

private data class RadioDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val groupSectionTitle: String,
    val descriptionSectionTitle: String,
    val disabledSectionTitle: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option1Description: String,
    val option2Description: String,
    val normalOption: String,
    val disabledOption: String,
    val codeTitle: String,
    val codeBlock: String,
)
