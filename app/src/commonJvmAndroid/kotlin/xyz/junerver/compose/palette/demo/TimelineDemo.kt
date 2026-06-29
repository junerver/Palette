package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.timeline.PTimeline
import xyz.junerver.compose.palette.components.timeline.TimelineItemData

@Composable
fun TimelineDemo() {
    val text = timelineDemoText()

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
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))

        val events =
            listOf(
                TimelineItemData(
                    content = {
                        Column {
                            Text(text.event1Title, style = MaterialTheme.typography.titleSmall)
                            Text("2024-01-01 10:00", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                ),
                TimelineItemData(
                    content = {
                        Column {
                            Text(text.event2Title, style = MaterialTheme.typography.titleSmall)
                            Text("2024-01-05 14:30", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                ),
                TimelineItemData(
                    content = {
                        Column {
                            Text(text.event3Title, style = MaterialTheme.typography.titleSmall)
                            Text("2024-01-15 09:00", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                ),
                TimelineItemData(
                    content = {
                        Column {
                            Text(text.event4Title, style = MaterialTheme.typography.titleSmall)
                            Text("2024-01-20 16:00", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                ),
            )

        DemoSection(title = text.basicSectionTitle) {
            PTimeline(items = events)
        }
    }
}

@Composable
@ReadOnlyComposable
private fun timelineDemoText(): TimelineDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            TimelineDemoText(
                title = "PTimeline 时间轴",
                subtitle = "用于展示时间流信息的组件",
                basicSectionTitle = "基础时间轴",
                event1Title = "创建项目",
                event2Title = "开发阶段",
                event3Title = "测试阶段",
                event4Title = "发布上线",
            )

        Language.EN_US ->
            TimelineDemoText(
                title = "PTimeline",
                subtitle = "A component for displaying timeline information.",
                basicSectionTitle = "Basic Timeline",
                event1Title = "Project Created",
                event2Title = "Development Phase",
                event3Title = "Testing Phase",
                event4Title = "Release",
            )
    }

private data class TimelineDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val event1Title: String,
    val event2Title: String,
    val event3Title: String,
    val event4Title: String,
)
