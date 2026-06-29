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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.tag.PEditableTagGroup
import xyz.junerver.compose.palette.components.tag.PTag
import xyz.junerver.compose.palette.components.tag.TagDefaults
import xyz.junerver.compose.palette.components.tag.TagSize
import xyz.junerver.compose.palette.components.tag.TagVariant
import xyz.junerver.compose.palette.components.text.PText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagDemo() {
    val text = tagDemoText()

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
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PTag(text = "Default")
                PTag(text = "Outlined", variant = TagVariant.Outlined)
                PTag(text = "Soft", variant = TagVariant.Soft)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sizeSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PTag(text = "Small", size = TagSize.Small)
                PTag(text = "Medium", size = TagSize.Medium)
                PTag(text = "Large", size = TagSize.Large)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.semanticSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PTag(text = "Success", colors = TagDefaults.successColors())
                PTag(text = "Warning", colors = TagDefaults.warningColors())
                PTag(text = "Error", colors = TagDefaults.errorColors())
                PTag(text = "Info", colors = TagDefaults.infoColors())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.pastelSectionTitle) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew").forEach { fruit ->
                    PTag(
                        text = fruit,
                        colors = TagDefaults.pastelColors(fruit),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.closableSectionTitle) {
            val (tags, setTags) = useState(listOf("Tag 1", "Tag 2", "Tag 3"))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tags.forEach { tag ->
                    PTag(
                        text = tag,
                        closable = true,
                        onClose = {
                            setTags(tags - tag)
                        },
                    )
                }

                if (tags.isEmpty()) {
                    PTag(
                        text = text.resetText,
                        onClick = { setTags(listOf("Tag 1", "Tag 2", "Tag 3")) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.editableSectionTitle) {
            val (editableTags, setEditableTags) = useState(listOf("React", "Vue", "Angular"))

            PEditableTagGroup(
                tags = editableTags,
                onTagsChange = setEditableTags,
                placeholder = text.addFrameworkPlaceholder,
                maxTags = 8,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.editablePastelSectionTitle) {
            val (pastelTags, setPastelTags) = useState(listOf("Design", "Development", "Marketing"))

            PEditableTagGroup(
                tags = pastelTags,
                onTagsChange = setPastelTags,
                placeholder = text.addTagPlaceholder,
                size = TagSize.Large,
                tagColors = { TagDefaults.pastelColors(it) },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun tagDemoText(): TagDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            TagDemoText(
                title = "PTag",
                subtitle = "用于标记和选择的标签组件",
                basicSectionTitle = "基础用法",
                sizeSectionTitle = "尺寸",
                semanticSectionTitle = "语义化颜色",
                pastelSectionTitle = "Pastel 调色板",
                closableSectionTitle = "可关闭标签",
                resetText = "Reset",
                editableSectionTitle = "可编辑标签组",
                addFrameworkPlaceholder = "Add framework...",
                editablePastelSectionTitle = "可编辑标签组 (Pastel 风格)",
                addTagPlaceholder = "Add tag...",
                codeTitle = "代码示例",
                codeBlock =
                    """
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
                    """.trimIndent(),
            )

        Language.EN_US ->
            TagDemoText(
                title = "PTag",
                subtitle = "Tag component for marking and selection.",
                basicSectionTitle = "Basic Usage",
                sizeSectionTitle = "Sizes",
                semanticSectionTitle = "Semantic Colors",
                pastelSectionTitle = "Pastel Palette",
                closableSectionTitle = "Closable Tags",
                resetText = "Reset",
                editableSectionTitle = "Editable Tag Group",
                addFrameworkPlaceholder = "Add framework...",
                editablePastelSectionTitle = "Editable Tag Group (Pastel Style)",
                addTagPlaceholder = "Add tag...",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    // Basic usage
                    PTag(text = "Default")
                    PTag(text = "Outlined", variant = TagVariant.Outlined)
                    PTag(text = "Soft", variant = TagVariant.Soft)

                    // Sizes
                    PTag(text = "Small", size = TagSize.Small)
                    PTag(text = "Medium", size = TagSize.Medium)
                    PTag(text = "Large", size = TagSize.Large)

                    // Semantic colors
                    PTag(text = "Success", colors = TagDefaults.successColors())
                    PTag(text = "Warning", colors = TagDefaults.warningColors())

                    // Pastel palette
                    PTag(text = "Apple", colors = TagDefaults.pastelColors("Apple"))

                    // Closable tag
                    PTag(
                        text = "Closable",
                        closable = true,
                        onClose = { /* handle close */ }
                    )

                    // Editable tag group
                    val (tags, setTags) = useState(listOf("Tag 1", "Tag 2"))
                    PEditableTagGroup(
                        tags = tags,
                        onTagsChange = setTags,
                        placeholder = "Add tag...",
                        maxTags = 10
                    )
                    """.trimIndent(),
            )
    }

private data class TagDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val sizeSectionTitle: String,
    val semanticSectionTitle: String,
    val pastelSectionTitle: String,
    val closableSectionTitle: String,
    val resetText: String,
    val editableSectionTitle: String,
    val addFrameworkPlaceholder: String,
    val editablePastelSectionTitle: String,
    val addTagPlaceholder: String,
    val codeTitle: String,
    val codeBlock: String,
)
