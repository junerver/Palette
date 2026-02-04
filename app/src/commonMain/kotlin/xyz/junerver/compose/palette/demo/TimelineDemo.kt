package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.timeline.PTimeline
import xyz.junerver.compose.palette.components.timeline.TimelineItemData

@Composable
fun TimelineDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "PTimeline 时间轴",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = "用于展示时间流信息的组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        val events = listOf(
            TimelineItemData(
                content = {
                    Column {
                        Text("创建项目", style = MaterialTheme.typography.titleSmall)
                        Text("2024-01-01 10:00", style = MaterialTheme.typography.bodySmall)
                    }
                }
            ),
            TimelineItemData(
                content = {
                    Column {
                        Text("开发阶段", style = MaterialTheme.typography.titleSmall)
                        Text("2024-01-05 14:30", style = MaterialTheme.typography.bodySmall)
                    }
                }
            ),
            TimelineItemData(
                content = {
                    Column {
                        Text("测试阶段", style = MaterialTheme.typography.titleSmall)
                        Text("2024-01-15 09:00", style = MaterialTheme.typography.bodySmall)
                    }
                }
            ),
            TimelineItemData(
                content = {
                    Column {
                        Text("发布上线", style = MaterialTheme.typography.titleSmall)
                        Text("2024-01-20 16:00", style = MaterialTheme.typography.bodySmall)
                    }
                }
            )
        )

        DemoSection(title = "基础时间轴") {
            PTimeline(items = events)
        }
    }
}
