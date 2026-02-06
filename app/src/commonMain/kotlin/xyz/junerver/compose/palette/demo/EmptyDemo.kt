package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.empty.PEmpty
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun EmptyDemo() {
    val text = emptyDemoText()

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
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.basicSectionTitle) {
            PEmpty(
                title = text.emptyTitle,
                description = text.emptyDescription,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = text.actionSectionTitle) {
            PEmpty(
                title = text.emptyActionTitle,
                description = text.emptyActionDescription,
                action = {
                    Text(text.actionText)
                },
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun emptyDemoText(): EmptyDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> EmptyDemoText(
        title = "PEmpty 空状态",
        subtitle = "用于展示空状态的组件",
        basicSectionTitle = "基础用法",
        actionSectionTitle = "带操作按钮",
        emptyTitle = "暂无数据",
        emptyDescription = "当前列表为空",
        emptyActionTitle = "暂无内容",
        emptyActionDescription = "点击下方按钮添加内容",
        actionText = "添加内容",
    )

    Language.EN_US -> EmptyDemoText(
        title = "PEmpty",
        subtitle = "A component for empty states",
        basicSectionTitle = "Basic Usage",
        actionSectionTitle = "With Action",
        emptyTitle = "No Data",
        emptyDescription = "The current list is empty",
        emptyActionTitle = "No Content",
        emptyActionDescription = "Click the button below to add content",
        actionText = "Add Content",
    )
}

private data class EmptyDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val actionSectionTitle: String,
    val emptyTitle: String,
    val emptyDescription: String,
    val emptyActionTitle: String,
    val emptyActionDescription: String,
    val actionText: String,
)
