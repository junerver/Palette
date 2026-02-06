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
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.toast.ToastIcon
import xyz.junerver.compose.palette.components.toast.rememberToastState

@Composable
fun ToastDemo() {
    val toastState = rememberToastState()
    val text = toastDemoText()
    
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.basicButtonText) {
                    toastState.show(text.basicToastText)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.successSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.successButtonText) {
                    toastState.show(
                        title = text.successToastText,
                        icon = ToastIcon.SUCCESS,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.failSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.failButtonText) {
                    toastState.show(
                        title = text.failToastText,
                        icon = ToastIcon.FAIL,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.loadingSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.loadingButtonText) {
                    toastState.show(
                        title = text.loadingToastText,
                        icon = ToastIcon.LOADING,
                        duration = 3000L,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.durationSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.durationButtonText) {
                    toastState.show(
                        title = text.durationToastText,
                        duration = 5000L,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun toastDemoText(): ToastDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> ToastDemoText(
        title = "Toast",
        subtitle = "轻提示组件",
        basicSectionTitle = "基础用法",
        basicButtonText = "显示提示",
        basicToastText = "这是一条提示消息",
        successSectionTitle = "成功提示",
        successButtonText = "成功",
        successToastText = "操作成功！",
        failSectionTitle = "错误提示",
        failButtonText = "错误",
        failToastText = "操作失败，请重试",
        loadingSectionTitle = "加载提示",
        loadingButtonText = "加载中",
        loadingToastText = "正在处理...",
        durationSectionTitle = "自定义时长",
        durationButtonText = "长时间显示",
        durationToastText = "这条消息会显示 5 秒",
        codeTitle = "代码示例",
        codeBlock = """
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
        """.trimIndent(),
    )

    Language.EN_US -> ToastDemoText(
        title = "Toast",
        subtitle = "Toast component",
        basicSectionTitle = "Basic Usage",
        basicButtonText = "Show Toast",
        basicToastText = "This is a toast message",
        successSectionTitle = "Success Toast",
        successButtonText = "Success",
        successToastText = "Operation succeeded!",
        failSectionTitle = "Error Toast",
        failButtonText = "Error",
        failToastText = "Operation failed, please retry",
        loadingSectionTitle = "Loading Toast",
        loadingButtonText = "Loading",
        loadingToastText = "Processing...",
        durationSectionTitle = "Custom Duration",
        durationButtonText = "Long Display",
        durationToastText = "This toast will be shown for 5 seconds",
        codeTitle = "Code Example",
        codeBlock = """
val toastState = rememberToastState()

// Basic usage
toastState.show("Message")

// Different types
toastState.show(
    title = "Success",
    icon = ToastIcon.SUCCESS
)
toastState.show(
    title = "Error",
    icon = ToastIcon.FAIL
)
toastState.show(
    title = "Loading",
    icon = ToastIcon.LOADING
)

// Custom duration
toastState.show(
    title = "Message",
    duration = 5000L
)
        """.trimIndent(),
    )
}

private data class ToastDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicButtonText: String,
    val basicToastText: String,
    val successSectionTitle: String,
    val successButtonText: String,
    val successToastText: String,
    val failSectionTitle: String,
    val failButtonText: String,
    val failToastText: String,
    val loadingSectionTitle: String,
    val loadingButtonText: String,
    val loadingToastText: String,
    val durationSectionTitle: String,
    val durationButtonText: String,
    val durationToastText: String,
    val codeTitle: String,
    val codeBlock: String,
)
