package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.toolbar.Toolbar
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults
import xyz.junerver.compose.palette.ui.theme.Primary
import xyz.junerver.compose.palette.ui.theme.Success

@Composable
fun ToolbarDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "Toolbar",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "带返回按钮的顶部工具栏",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Toolbar(
                title = "页面标题",
                onNavigationClick = { }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义颜色") {
            Column {
                Toolbar(
                    title = "主色调",
                    colors = ToolbarDefaults.colors(backgroundColor = Primary),
                    onNavigationClick = { }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Toolbar(
                    title = "成功色",
                    colors = ToolbarDefaults.colors(backgroundColor = Success),
                    onNavigationClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义高度") {
            Toolbar(
                title = "较高的工具栏",
                height = 72.dp,
                onNavigationClick = { }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
Toolbar(
    title = "页面标题",
    backgroundColor = Color(0xFF0F71F2),
    height = 58.dp,
    onIconClick = {
        // 返回操作
    }
)
            """.trimIndent()
        )
    }
}
