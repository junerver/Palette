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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.actionsheet.ActionSheetItem
import xyz.junerver.compose.palette.components.actionsheet.PActionSheet
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ActionSheetDemo() {
    val text = actionSheetDemoText()
    var basicVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descVisible by remember { mutableStateOf(false) }

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.basicButtonText) {
                    basicVisible = true
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.titleSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.titleButtonText) {
                    titleVisible = true
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.descSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.descButtonText) {
                    descVisible = true
                }
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

    PActionSheet(
        visible = basicVisible,
        options = listOf(
            ActionSheetItem(label = text.option1),
            ActionSheetItem(label = text.option2),
            ActionSheetItem(label = text.option3),
        ),
        onDismiss = { basicVisible = false },
        onItemClick = {},
    )

    PActionSheet(
        visible = titleVisible,
        options = listOf(
            ActionSheetItem(label = text.option1),
            ActionSheetItem(label = text.option2),
            ActionSheetItem(label = text.option3),
        ),
        onDismiss = { titleVisible = false },
        onItemClick = {},
        title = text.sheetTitle,
    )

    PActionSheet(
        visible = descVisible,
        options = listOf(
            ActionSheetItem(label = text.option1, description = text.desc1),
            ActionSheetItem(label = text.option2, description = text.desc2),
            ActionSheetItem(label = text.option3, disabled = true),
        ),
        onDismiss = { descVisible = false },
        onItemClick = {},
        title = text.sheetTitle,
    )
}

@Composable
@ReadOnlyComposable
private fun actionSheetDemoText(): ActionSheetDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ActionSheetDemoText(
                title = "ActionSheet",
                subtitle = "操作菜单组件",
                basicSectionTitle = "基础用法",
                basicButtonText = "显示操作菜单",
                titleSectionTitle = "带标题",
                titleButtonText = "带标题的操作菜单",
                descSectionTitle = "带描述和禁用项",
                descButtonText = "带描述的操作菜单",
                sheetTitle = "请选择",
                option1 = "选项一",
                option2 = "选项二",
                option3 = "选项三",
                desc1 = "这是选项一的描述",
                desc2 = "这是选项二的描述",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var visible by remember { mutableStateOf(false) }

                    PActionSheet(
                        visible = visible,
                        options = listOf(
                            ActionSheetItem(label = "选项一"),
                            ActionSheetItem(label = "选项二"),
                            ActionSheetItem(label = "选项三", disabled = true)
                        ),
                        onDismiss = { visible = false },
                        onItemClick = { index -> },
                        title = "请选择"
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            ActionSheetDemoText(
                title = "ActionSheet",
                subtitle = "Action sheet component",
                basicSectionTitle = "Basic Usage",
                basicButtonText = "Show Action Sheet",
                titleSectionTitle = "With Title",
                titleButtonText = "Action Sheet with Title",
                descSectionTitle = "With Description & Disabled",
                descButtonText = "Action Sheet with Description",
                sheetTitle = "Please select",
                option1 = "Option 1",
                option2 = "Option 2",
                option3 = "Option 3",
                desc1 = "Description for option 1",
                desc2 = "Description for option 2",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var visible by remember { mutableStateOf(false) }

                    PActionSheet(
                        visible = visible,
                        options = listOf(
                            ActionSheetItem(label = "Option 1"),
                            ActionSheetItem(label = "Option 2"),
                            ActionSheetItem(label = "Option 3", disabled = true)
                        ),
                        onDismiss = { visible = false },
                        onItemClick = { index -> },
                        title = "Please select"
                    )
                    """.trimIndent(),
            )
    }

private data class ActionSheetDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicButtonText: String,
    val titleSectionTitle: String,
    val titleButtonText: String,
    val descSectionTitle: String,
    val descButtonText: String,
    val sheetTitle: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val desc1: String,
    val desc2: String,
    val codeTitle: String,
    val codeBlock: String,
)
