package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.tag.PTag
import xyz.junerver.compose.palette.components.tag.PEditableTagGroup
import xyz.junerver.compose.palette.components.tag.TagVariant
import xyz.junerver.compose.palette.components.tag.TagSize
import xyz.junerver.compose.palette.components.tag.TagDefaults

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "PTag",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "用于标记和选择的标签组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PTag(text = "Default")
                PTag(text = "Outlined", variant = TagVariant.Outlined)
                PTag(text = "Soft", variant = TagVariant.Soft)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "尺寸") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PTag(text = "Small", size = TagSize.Small)
                PTag(text = "Medium", size = TagSize.Medium)
                PTag(text = "Large", size = TagSize.Large)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "语义化颜色") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PTag(text = "Success", colors = TagDefaults.successColors())
                PTag(text = "Warning", colors = TagDefaults.warningColors())
                PTag(text = "Error", colors = TagDefaults.errorColors())
                PTag(text = "Info", colors = TagDefaults.infoColors())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "Pastel 调色板") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew").forEach { fruit ->
                    PTag(
                        text = fruit,
                        colors = TagDefaults.pastelColors(fruit)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "可关闭标签") {
            val (tags, setTags) = useState(listOf("Tag 1", "Tag 2", "Tag 3"))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEach { tag ->
                    PTag(
                        text = tag,
                        closable = true,
                        onClose = {
                            setTags(tags - tag)
                        }
                    )
                }

                if (tags.isEmpty()) {
                    PTag(
                        text = "Reset",
                        onClick = { setTags(listOf("Tag 1", "Tag 2", "Tag 3")) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "可编辑标签组") {
            val (editableTags, setEditableTags) = useState(listOf("React", "Vue", "Angular"))

            PEditableTagGroup(
                tags = editableTags,
                onTagsChange = setEditableTags,
                placeholder = "Add framework...",
                maxTags = 8
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "可编辑标签组 (Pastel 风格)") {
            val (pastelTags, setPastelTags) = useState(listOf("Design", "Development", "Marketing"))

            PEditableTagGroup(
                tags = pastelTags,
                onTagsChange = setPastelTags,
                placeholder = "Add tag...",
                size = TagSize.Large,
                tagColors = { TagDefaults.pastelColors(it) }
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
            // 基础用法
            PTag(text = "Default")
            PTag(text = "Outlined", variant = TagVariant.Outlined)
            PTag(text = "Soft", variant = TagVariant.Soft)
            
            // 尺寸
            PTag(text = "Small", size = TagSize.Small)
            PTag(text = "Medium", size = TagSize.Medium)
            PTag(text = "Large", size = TagSize.Large)
            
            // 语义化颜色
            PTag(text = "Success", colors = TagDefaults.successColors())
            PTag(text = "Warning", colors = TagDefaults.warningColors())
            
            // Pastel 调色板
            PTag(text = "Apple", colors = TagDefaults.pastelColors("Apple"))
            
            // 可关闭标签
            PTag(
                text = "Closable",
                closable = true,
                onClose = { /* handle close */ }
            )
            
            // 可编辑标签组
            val (tags, setTags) = useState(listOf("Tag 1", "Tag 2"))
            PEditableTagGroup(
                tags = tags,
                onTagsChange = setTags,
                placeholder = "Add tag...",
                maxTags = 10
            )
            """.trimIndent()
        )
    }
}
