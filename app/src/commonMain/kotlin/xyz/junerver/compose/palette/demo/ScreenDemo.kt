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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.screen.Screen
import xyz.junerver.compose.palette.components.screen.ScreenDefaults

@Composable
fun ScreenDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "页面容器组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Screen(title = "页面标题") {
                Text(text = "内容区域")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义颜色") {
            Screen(
                title = "带自定义背景",
                colors = ScreenDefaults.colors(
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            ) {
                Text(text = "自定义背景颜色")
            }
        }
    }
}
