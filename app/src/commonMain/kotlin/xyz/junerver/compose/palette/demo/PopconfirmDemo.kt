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
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.alert.AlertType
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.popconfirm.PPopconfirm
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PopconfirmDemo() {
    val text = popconfirmDemoText()

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
                PPopconfirm(
                    title = text.deleteConfirmTitle,
                    onConfirm = {},
                ) {
                    PButton(text = text.deleteButtonText, type = ButtonType.DANGER) {}
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.descriptionSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PPopconfirm(
                    title = text.deleteConfirmTitle,
                    description = text.deleteDescription,
                    onConfirm = {},
                ) {
                    PButton(text = text.deleteButtonText, type = ButtonType.DANGER) {}
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PPopconfirm(
                    title = text.submitConfirmTitle,
                    okText = text.customOkText,
                    cancelText = text.customCancelText,
                    okType = AlertType.Info,
                    onConfirm = {},
                ) {
                    PButton(text = text.submitButtonText, type = ButtonType.PRIMARY) {}
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
}

@Composable
@ReadOnlyComposable
private fun popconfirmDemoText(): PopconfirmDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            PopconfirmDemoText(
                title = "Popconfirm",
                subtitle = "气泡确认框组件",
                basicSectionTitle = "基础用法",
                deleteConfirmTitle = "确定要删除吗？",
                deleteButtonText = "删除",
                descriptionSectionTitle = "带描述",
                deleteDescription = "删除后数据将无法恢复",
                customSectionTitle = "自定义按钮文字",
                submitConfirmTitle = "确定要提交吗？",
                customOkText = "确认提交",
                customCancelText = "再想想",
                submitButtonText = "提交",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PPopconfirm(
                        title = "确定要删除吗？",
                        description = "删除后数据将无法恢复",
                        onConfirm = { /* 处理确认 */ },
                    ) {
                        PButton(text = "删除", type = ButtonType.DANGER) {}
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            PopconfirmDemoText(
                title = "Popconfirm",
                subtitle = "Popconfirm component.",
                basicSectionTitle = "Basic Usage",
                deleteConfirmTitle = "Are you sure to delete?",
                deleteButtonText = "Delete",
                descriptionSectionTitle = "With Description",
                deleteDescription = "The data cannot be recovered after deletion",
                customSectionTitle = "Custom Button Text",
                submitConfirmTitle = "Are you sure to submit?",
                customOkText = "Confirm",
                customCancelText = "Think Again",
                submitButtonText = "Submit",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PPopconfirm(
                        title = "Are you sure to delete?",
                        description = "The data cannot be recovered",
                        onConfirm = { /* Handle confirm */ },
                    ) {
                        PButton(text = "Delete", type = ButtonType.DANGER) {}
                    }
                    """.trimIndent(),
            )
    }

private data class PopconfirmDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val deleteConfirmTitle: String,
    val deleteButtonText: String,
    val descriptionSectionTitle: String,
    val deleteDescription: String,
    val customSectionTitle: String,
    val submitConfirmTitle: String,
    val customOkText: String,
    val customCancelText: String,
    val submitButtonText: String,
    val codeTitle: String,
    val codeBlock: String,
)
