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
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.segmented.PSegmented
import xyz.junerver.compose.palette.components.segmented.SegmentedOption
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun SegmentedDemo() {
    val text = segmentedDemoText()

    val (selectedBasic, setSelectedBasic) = useState("option1")
    val (selectedDefault, setSelectedDefault) = useState("tab2")

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
                PSegmented(
                    options = listOf(
                        SegmentedOption(value = "option1", label = text.option1Label),
                        SegmentedOption(value = "option2", label = text.option2Label),
                        SegmentedOption(value = "option3", label = text.option3Label),
                    ),
                    value = selectedBasic,
                    onValueChange = { setSelectedBasic(it) },
                )
                PText(text = "${text.selectedText}: $selectedBasic")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.defaultSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PSegmented(
                    options = listOf(
                        SegmentedOption(value = "tab1", label = text.tab1Label),
                        SegmentedOption(value = "tab2", label = text.tab2Label),
                        SegmentedOption(value = "tab3", label = text.tab3Label),
                    ),
                    value = selectedDefault,
                    onValueChange = { setSelectedDefault(it) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PSegmented(
                    options = listOf(
                        SegmentedOption(value = "a", label = text.enabledLabel),
                        SegmentedOption(value = "b", label = text.disabledLabel, disabled = true),
                        SegmentedOption(value = "c", label = text.enabledLabel2),
                    ),
                    value = "a",
                    onValueChange = {},
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
private fun segmentedDemoText(): SegmentedDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            SegmentedDemoText(
                title = "Segmented",
                subtitle = "分段控制器组件",
                basicSectionTitle = "基础用法",
                option1Label = "选项一",
                option2Label = "选项二",
                option3Label = "选项三",
                selectedText = "已选中",
                defaultSectionTitle = "默认值",
                tab1Label = "标签一",
                tab2Label = "标签二",
                tab3Label = "标签三",
                disabledSectionTitle = "禁用选项",
                enabledLabel = "启用",
                disabledLabel = "禁用",
                enabledLabel2 = "启用",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val (selected, setSelected) = useState("option1")

                    PSegmented(
                        options = listOf(
                            SegmentedOption(value = "option1", label = "选项一"),
                            SegmentedOption(value = "option2", label = "选项二"),
                            SegmentedOption(value = "option3", label = "选项三"),
                        ),
                        value = selected,
                        onValueChange = { setSelected(it) },
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            SegmentedDemoText(
                title = "Segmented",
                subtitle = "Segmented control component.",
                basicSectionTitle = "Basic Usage",
                option1Label = "Option 1",
                option2Label = "Option 2",
                option3Label = "Option 3",
                selectedText = "Selected",
                defaultSectionTitle = "Default Value",
                tab1Label = "Tab 1",
                tab2Label = "Tab 2",
                tab3Label = "Tab 3",
                disabledSectionTitle = "Disabled Option",
                enabledLabel = "Enabled",
                disabledLabel = "Disabled",
                enabledLabel2 = "Enabled",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val (selected, setSelected) = useState("option1")

                    PSegmented(
                        options = listOf(
                            SegmentedOption(value = "option1", label = "Option 1"),
                            SegmentedOption(value = "option2", label = "Option 2"),
                            SegmentedOption(value = "option3", label = "Option 3"),
                        ),
                        value = selected,
                        onValueChange = { setSelected(it) },
                    )
                    """.trimIndent(),
            )
    }

private data class SegmentedDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val option1Label: String,
    val option2Label: String,
    val option3Label: String,
    val selectedText: String,
    val defaultSectionTitle: String,
    val tab1Label: String,
    val tab2Label: String,
    val tab3Label: String,
    val disabledSectionTitle: String,
    val enabledLabel: String,
    val disabledLabel: String,
    val enabledLabel2: String,
    val codeTitle: String,
    val codeBlock: String,
)
