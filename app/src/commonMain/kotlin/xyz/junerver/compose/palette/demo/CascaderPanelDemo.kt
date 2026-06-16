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
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.cascader.CascaderOption
import xyz.junerver.compose.palette.components.cascaderpanel.PCascaderPanel
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun CascaderPanelDemo() {
    val text = cascaderPanelDemoText()

    val mockOptions = listOf(
        CascaderOption(
            value = "zhejiang",
            label = "浙江",
            children = listOf(
                CascaderOption(
                    value = "hangzhou",
                    label = "杭州",
                    children = listOf(
                        CascaderOption(value = "xihu", label = "西湖区"),
                        CascaderOption(value = "binjiang", label = "滨江区"),
                    ),
                ),
                CascaderOption(
                    value = "ningbo",
                    label = "宁波",
                    children = listOf(
                        CascaderOption(value = "haishu", label = "海曙区"),
                        CascaderOption(value = "jiangbei", label = "江北区"),
                    ),
                ),
            ),
        ),
        CascaderOption(
            value = "jiangsu",
            label = "江苏",
            children = listOf(
                CascaderOption(
                    value = "nanjing",
                    label = "南京",
                    children = listOf(
                        CascaderOption(value = "xuanwu", label = "玄武区"),
                        CascaderOption(value = "gulou", label = "鼓楼区"),
                    ),
                ),
                CascaderOption(
                    value = "suzhou",
                    label = "苏州",
                    children = listOf(
                        CascaderOption(value = "gusu", label = "姑苏区"),
                        CascaderOption(value = "wuzhong", label = "吴中区"),
                    ),
                ),
            ),
        ),
    )

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
            var selected by remember { mutableStateOf(emptyList<String>()) }
            Column {
                PCascaderPanel(
                    options = mockOptions,
                    value = selected,
                    onValueChange = { selected = it },
                )
                PText(
                    text = "${text.selectedPrefix}${selected.joinToString(" / ")}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
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
private fun cascaderPanelDemoText(): CascaderPanelDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            CascaderPanelDemoText(
                title = "CascaderPanel",
                subtitle = "级联面板组件，用于多层级数据选择",
                basicSectionTitle = "基础用法",
                selectedPrefix = "已选: ",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val options = listOf(
                        CascaderOption(
                            value = "zhejiang",
                            label = "浙江",
                            children = listOf(
                                CascaderOption(
                                    value = "hangzhou",
                                    label = "杭州",
                                    children = listOf(
                                        CascaderOption(value = "xihu", label = "西湖区")
                                    )
                                )
                            )
                        )
                    )
                    var selected by remember { mutableStateOf(emptyList<String>()) }
                    PCascaderPanel(
                        options = options,
                        value = selected,
                        onValueChange = { selected = it }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            CascaderPanelDemoText(
                title = "CascaderPanel",
                subtitle = "Cascader panel component for hierarchical data selection.",
                basicSectionTitle = "Basic Usage",
                selectedPrefix = "Selected: ",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val options = listOf(
                        CascaderOption(
                            value = "zhejiang",
                            label = "Zhejiang",
                            children = listOf(
                                CascaderOption(
                                    value = "hangzhou",
                                    label = "Hangzhou",
                                    children = listOf(
                                        CascaderOption(value = "xihu", label = "Xihu")
                                    )
                                )
                            )
                        )
                    )
                    var selected by remember { mutableStateOf(emptyList<String>()) }
                    PCascaderPanel(
                        options = options,
                        value = selected,
                        onValueChange = { selected = it }
                    )
                    """.trimIndent(),
            )
    }

private data class CascaderPanelDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val selectedPrefix: String,
    val codeTitle: String,
    val codeBlock: String,
)
