package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.horizontalScroll
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
import xyz.junerver.compose.palette.components.cascader.CascaderOption
import xyz.junerver.compose.palette.components.cascaderpanel.PCascaderPanel
import xyz.junerver.compose.palette.components.text.PText

private val panelRegionOptions = listOf(
    CascaderOption(
        value = "zhejiang",
        label = "浙江省",
        children = listOf(
            CascaderOption(
                value = "hangzhou",
                label = "杭州市",
                children = listOf(
                    CascaderOption(
                        value = "xihu",
                        label = "西湖区",
                        children = listOf(
                            CascaderOption(value = "longxiangqiao", label = "龙翔桥"),
                            CascaderOption(value = "wenyiwestroad", label = "文一西路"),
                        ),
                    ),
                    CascaderOption(
                        value = "binjiang",
                        label = "滨江区",
                        children = listOf(
                            CascaderOption(value = "xingguang", label = "星光大道"),
                            CascaderOption(value = "changhe", label = "长河"),
                        ),
                    ),
                    CascaderOption(value = "gongshu", label = "拱墅区"),
                ),
            ),
            CascaderOption(
                value = "ningbo",
                label = "宁波市",
                children = listOf(
                    CascaderOption(value = "haishu", label = "海曙区"),
                    CascaderOption(value = "jiangbei", label = "江北区"),
                    CascaderOption(value = "yinzhou", label = "鄞州区"),
                ),
            ),
            CascaderOption(
                value = "wenzhou",
                label = "温州市",
                children = listOf(
                    CascaderOption(value = "lucheng", label = "鹿城区"),
                    CascaderOption(value = "ouhai", label = "瓯海区"),
                ),
            ),
        ),
    ),
    CascaderOption(
        value = "jiangsu",
        label = "江苏省",
        children = listOf(
            CascaderOption(
                value = "nanjing",
                label = "南京市",
                children = listOf(
                    CascaderOption(value = "xuanwu", label = "玄武区"),
                    CascaderOption(value = "gulou", label = "鼓楼区"),
                    CascaderOption(value = "jianye", label = "建邺区"),
                ),
            ),
            CascaderOption(
                value = "suzhou",
                label = "苏州市",
                children = listOf(
                    CascaderOption(
                        value = "gusu",
                        label = "姑苏区",
                        children = listOf(
                            CascaderOption(value = "guanqian", label = "观前街"),
                            CascaderOption(value = "pingjiang", label = "平江路"),
                        ),
                    ),
                    CascaderOption(value = "wuzhong", label = "吴中区"),
                    CascaderOption(value = "kunshan", label = "昆山市"),
                ),
            ),
        ),
    ),
    CascaderOption(
        value = "guangdong",
        label = "广东省",
        children = listOf(
            CascaderOption(
                value = "guangzhou",
                label = "广州市",
                children = listOf(
                    CascaderOption(value = "tianhe", label = "天河区"),
                    CascaderOption(value = "yuexiu", label = "越秀区"),
                    CascaderOption(value = "haizhu", label = "海珠区"),
                ),
            ),
            CascaderOption(
                value = "shenzhen",
                label = "深圳市",
                children = listOf(
                    CascaderOption(value = "nanshan", label = "南山区"),
                    CascaderOption(value = "futian", label = "福田区"),
                    CascaderOption(value = "luohu", label = "罗湖区"),
                ),
            ),
        ),
    ),
)

private val defaultPanelValue = listOf("zhejiang", "hangzhou", "xihu", "longxiangqiao")

@Composable
fun CascaderPanelDemo() {
    val text = cascaderPanelDemoText()
    val (selected, setSelected) = useState<List<String>>(defaultPanelValue)
    val selectedLabels = selectedLabelPath(panelRegionOptions, selected)

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
            Column {
                PCascaderPanel(
                    options = panelRegionOptions,
                    value = selected,
                    onValueChange = setSelected,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                )
                PText(
                    text = "${text.selectedPrefix}${selectedLabels.joinToString(" / ")}",
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
                            label = "浙江省",
                            children = listOf(
                                CascaderOption(
                                    value = "hangzhou",
                                    label = "杭州市",
                                    children = listOf(
                                        CascaderOption(
                                            value = "xihu",
                                            label = "西湖区",
                                            children = listOf(
                                                CascaderOption(value = "longxiangqiao", label = "龙翔桥"),
                                                CascaderOption(value = "wenyiwestroad", label = "文一西路")
                                            )
                                        ),
                                        CascaderOption(
                                            value = "binjiang",
                                            label = "滨江区",
                                            children = listOf(
                                                CascaderOption(value = "xingguang", label = "星光大道"),
                                                CascaderOption(value = "changhe", label = "长河")
                                            )
                                        )
                                    )
                                ),
                                CascaderOption(
                                    value = "ningbo",
                                    label = "宁波市",
                                    children = listOf(
                                        CascaderOption(value = "haishu", label = "海曙区"),
                                        CascaderOption(value = "jiangbei", label = "江北区")
                                    )
                                )
                            )
                        )
                    )
                    val (selected, setSelected) =
                        useState<List<String>>(listOf("zhejiang", "hangzhou", "xihu", "longxiangqiao"))
                    PCascaderPanel(
                        options = options,
                        value = selected,
                        onValueChange = setSelected
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
                                        CascaderOption(
                                            value = "xihu",
                                            label = "Xihu",
                                            children = listOf(
                                                CascaderOption(value = "longxiangqiao", label = "Longxiangqiao"),
                                                CascaderOption(value = "wenyiwestroad", label = "Wenyi West Road")
                                            )
                                        ),
                                        CascaderOption(
                                            value = "binjiang",
                                            label = "Binjiang",
                                            children = listOf(
                                                CascaderOption(value = "xingguang", label = "Xingguang Avenue"),
                                                CascaderOption(value = "changhe", label = "Changhe")
                                            )
                                        )
                                    )
                                ),
                                CascaderOption(
                                    value = "ningbo",
                                    label = "Ningbo",
                                    children = listOf(
                                        CascaderOption(value = "haishu", label = "Haishu"),
                                        CascaderOption(value = "jiangbei", label = "Jiangbei")
                                    )
                                )
                            )
                        )
                    )
                    val (selected, setSelected) =
                        useState<List<String>>(listOf("zhejiang", "hangzhou", "xihu", "longxiangqiao"))
                    PCascaderPanel(
                        options = options,
                        value = selected,
                        onValueChange = setSelected
                    )
                    """.trimIndent(),
            )
    }

private fun selectedLabelPath(
    options: List<CascaderOption>,
    value: List<String>,
): List<String> {
    var current = options
    val labels = mutableListOf<String>()
    for (selectedValue in value) {
        val option = current.firstOrNull { it.value == selectedValue } ?: break
        labels += option.label
        current = option.children
    }
    return labels
}

private data class CascaderPanelDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val selectedPrefix: String,
    val codeTitle: String,
    val codeBlock: String,
)
