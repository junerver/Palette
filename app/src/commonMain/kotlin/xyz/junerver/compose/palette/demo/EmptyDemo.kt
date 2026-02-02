package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.empty.PEmpty

@Composable
fun EmptyDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PEmpty 空状态",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "用于展示空状态的组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "基础用法") {
            PEmpty(
                title = "暂无数据",
                description = "当前列表为空"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = "带操作按钮") {
            PEmpty(
                title = "暂无内容",
                description = "点击下方按钮添加内容",
                action = {
                    Text("添加内容")
                }
            )
        }
    }
}
