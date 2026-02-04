package xyz.junerver.compose.palette.demo

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.toast.ToastIcon
import xyz.junerver.compose.palette.components.toast.rememberToastState

@Composable
fun ToastDemo() {
    val toastState = rememberToastState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "Toast",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "轻提示组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "显示提示") {
                    toastState.show("这是一条提示消息")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "成功提示") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "成功") {
                    toastState.show(
                        title = "操作成功！",
                        icon = ToastIcon.SUCCESS
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "错误提示") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "错误") {
                    toastState.show(
                        title = "操作失败，请重试",
                        icon = ToastIcon.FAIL
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "加载提示") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "加载中") {
                    toastState.show(
                        title = "正在处理...",
                        icon = ToastIcon.LOADING,
                        duration = 3000L
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义时长") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = "长时间显示") {
                    toastState.show(
                        title = "这条消息会显示 5 秒",
                        duration = 5000L
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
val toastState = rememberToastState()

// 基础用法
toastState.show("提示消息")

// 不同类型
toastState.show(
    title = "成功",
    icon = ToastIcon.SUCCESS
)
toastState.show(
    title = "错误",
    icon = ToastIcon.FAIL
)
toastState.show(
    title = "加载中",
    icon = ToastIcon.LOADING
)

// 自定义时长
toastState.show(
    title = "消息",
    duration = 5000L
)
            """.trimIndent()
        )
    }
}
