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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.tag.PTag
import xyz.junerver.compose.palette.components.tag.TagVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PTag",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
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

        DemoSection(title = "标签组") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(12) { index ->
                    PTag(text = "Tag $index")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
            PTag(text = "Default")
            
            PTag(
                text = "Outlined", 
                variant = TagVariant.Outlined
            )
            
            PTag(
                text = "Closable",
                closable = true,
                onClose = { /* handle close */ }
            )
            """.trimIndent()
        )
    }
}
