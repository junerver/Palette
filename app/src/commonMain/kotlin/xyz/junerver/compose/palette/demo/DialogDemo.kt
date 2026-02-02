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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.dialog.rememberDialogState

@Composable
fun DialogDemo() {
    val dialogState = rememberDialogState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Dialog",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "对话框组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "显示对话框") {
                    dialogState.show(
                        title = "基础对话框",
                        content = "这是一个基础的对话框示例，包含标题和内容。",
                        onOk = { println("Clicked OK") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "危险操作") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "删除确认") {
                    dialogState.show(
                        title = "确认删除这条记录吗？",
                        okColor = Color.Red,
                        okText = "删除",
                        onOk = { println("Deleted") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "仅确认按钮") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "提示对话框") {
                    dialogState.show(
                        title = "提示",
                        content = "操作成功完成！",
                        onCancel = null,
                        onOk = { println("OK") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
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
            """.trimIndent()
        )
    }
}