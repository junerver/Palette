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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.dialog.rememberDialogState
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun DialogDemo() {
    val text = dialogDemoText()
    val dialogState = rememberDialogState()

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
                PButton(text = text.showDialogButton) {
                    dialogState.show(
                        title = text.basicDialogTitle,
                        content = text.basicDialogContent,
                        onOk = { println("Clicked OK") },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.dangerSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.deleteConfirmButton) {
                    dialogState.show(
                        title = text.deleteDialogTitle,
                        okColor = Color.Red,
                        okText = text.deleteOkText,
                        onOk = { println("Deleted") },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.confirmOnlySectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.noticeDialogButton) {
                    dialogState.show(
                        title = text.noticeDialogTitle,
                        content = text.noticeDialogContent,
                        onCancel = null,
                        onOk = { println("OK") },
                    )
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
private fun dialogDemoText(): DialogDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            DialogDemoText(
                title = "Dialog",
                subtitle = "对话框组件",
                basicSectionTitle = "基础用法",
                showDialogButton = "显示对话框",
                basicDialogTitle = "基础对话框",
                basicDialogContent = "这是一个基础的对话框示例，包含标题和内容。",
                dangerSectionTitle = "危险操作",
                deleteConfirmButton = "删除确认",
                deleteDialogTitle = "确认删除这条记录吗？",
                deleteOkText = "删除",
                confirmOnlySectionTitle = "仅确认按钮",
                noticeDialogButton = "提示对话框",
                noticeDialogTitle = "提示",
                noticeDialogContent = "操作成功完成！",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val dialogState = rememberDialogState()

                    // 基础用法
                    dialogState.show(
                        title = "标题",
                        content = "对话框内容",
                        onOk = { /* 确认回调 */ }
                    )

                    // 危险操作
                    dialogState.show(
                        title = "确认删除？",
                        okColor = Color.Red,
                        okText = "删除",
                        onOk = { /* 删除操作 */ }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            DialogDemoText(
                title = "Dialog",
                subtitle = "Dialog component.",
                basicSectionTitle = "Basic Usage",
                showDialogButton = "Show Dialog",
                basicDialogTitle = "Basic Dialog",
                basicDialogContent = "This is a basic dialog example with title and content.",
                dangerSectionTitle = "Danger Action",
                deleteConfirmButton = "Delete Confirm",
                deleteDialogTitle = "Confirm deleting this record?",
                deleteOkText = "Delete",
                confirmOnlySectionTitle = "Confirm Button Only",
                noticeDialogButton = "Show Notice",
                noticeDialogTitle = "Notice",
                noticeDialogContent = "Operation completed successfully!",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val dialogState = rememberDialogState()

                    // Basic usage
                    dialogState.show(
                        title = "Title",
                        content = "Dialog content",
                        onOk = { /* Confirm callback */ }
                    )

                    // Danger action
                    dialogState.show(
                        title = "Confirm delete?",
                        okColor = Color.Red,
                        okText = "Delete",
                        onOk = { /* Delete action */ }
                    )
                    """.trimIndent(),
            )
    }

private data class DialogDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val showDialogButton: String,
    val basicDialogTitle: String,
    val basicDialogContent: String,
    val dangerSectionTitle: String,
    val deleteConfirmButton: String,
    val deleteDialogTitle: String,
    val deleteOkText: String,
    val confirmOnlySectionTitle: String,
    val noticeDialogButton: String,
    val noticeDialogTitle: String,
    val noticeDialogContent: String,
    val codeTitle: String,
    val codeBlock: String,
)
