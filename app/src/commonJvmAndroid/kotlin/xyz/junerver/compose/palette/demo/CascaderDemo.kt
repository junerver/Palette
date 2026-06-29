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
import xyz.junerver.compose.palette.components.cascader.CascaderOption
import xyz.junerver.compose.palette.components.cascader.PCascader
import xyz.junerver.compose.palette.components.text.PText

private val regionOptions = listOf(
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
    CascaderOption(
        value = "zhejiang",
        label = "浙江省",
        children = listOf(
            CascaderOption(
                value = "hangzhou",
                label = "杭州市",
                children = listOf(
                    CascaderOption(value = "xihu", label = "西湖区"),
                    CascaderOption(value = "binjiang", label = "滨江区"),
                ),
            ),
            CascaderOption(
                value = "ningbo",
                label = "宁波市",
                children = listOf(
                    CascaderOption(value = "haishu", label = "海曙区"),
                    CascaderOption(value = "yinzhou", label = "鄞州区"),
                ),
            ),
        ),
    ),
)

@Composable
fun CascaderDemo() {
    val text = cascaderDemoText()
    val (basicValue, setBasicValue) = useState<List<String>>(emptyList())
    val (defaultValue, setDefaultValue) = useState<List<String>>(listOf("guangdong", "shenzhen", "nanshan"))

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
            PCascader(
                options = regionOptions,
                value = basicValue,
                onValueChange = setBasicValue,
                placeholder = text.placeholder,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.defaultSectionTitle) {
            PCascader(
                options = regionOptions,
                value = defaultValue,
                onValueChange = setDefaultValue,
                placeholder = text.placeholder,
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
private fun cascaderDemoText(): CascaderDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            CascaderDemoText(
                title = "Cascader",
                subtitle = "级联选择器组件",
                basicSectionTitle = "基础用法",
                defaultSectionTitle = "默认值",
                placeholder = "请选择地区",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val options = listOf(
                        CascaderOption(
                            value = "guangdong",
                            label = "广东省",
                            children = listOf(
                                CascaderOption(
                                    value = "guangzhou",
                                    label = "广州市",
                                    children = listOf(
                                        CascaderOption(value = "tianhe", label = "天河区")
                                    )
                                )
                            )
                        )
                    )

                    PCascader(
                        options = options,
                        value = value,
                        onValueChange = setValue,
                        placeholder = "请选择"
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            CascaderDemoText(
                title = "Cascader",
                subtitle = "Cascader component",
                basicSectionTitle = "Basic Usage",
                defaultSectionTitle = "Default Value",
                placeholder = "Select region",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val options = listOf(
                        CascaderOption(
                            value = "guangdong",
                            label = "Guangdong",
                            children = listOf(
                                CascaderOption(
                                    value = "guangzhou",
                                    label = "Guangzhou",
                                    children = listOf(
                                        CascaderOption(value = "tianhe", label = "Tianhe")
                                    )
                                )
                            )
                        )
                    )

                    PCascader(
                        options = options,
                        value = value,
                        onValueChange = setValue,
                        placeholder = "Select"
                    )
                    """.trimIndent(),
            )
    }

private data class CascaderDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val defaultSectionTitle: String,
    val placeholder: String,
    val codeTitle: String,
    val codeBlock: String,
)
