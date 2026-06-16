package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.treeselect.PTreeSelect
import xyz.junerver.compose.palette.components.treeselect.TreeSelectNode

@Composable
fun TreeSelectDemo() {
    val text = treeSelectDemoText()

    val treeData =
        listOf(
            TreeSelectNode(
                value = "1",
                label = text.root,
                children =
                    listOf(
                        TreeSelectNode(
                            value = "1-1",
                            label = text.folder1,
                            children =
                                listOf(
                                    TreeSelectNode(value = "1-1-1", label = text.file1),
                                    TreeSelectNode(value = "1-1-2", label = text.file2),
                                ),
                        ),
                        TreeSelectNode(
                            value = "1-2",
                            label = text.folder2,
                            children =
                                listOf(
                                    TreeSelectNode(value = "1-2-1", label = text.file3),
                                ),
                        ),
                    ),
            ),
        )

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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value1 by remember { mutableStateOf<String?>(null) }
                PTreeSelect(
                    value = value1,
                    onValueChange = { value1 = it },
                    nodes = treeData,
                    placeholder = text.placeholder,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.withPlaceholderSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value2 by remember { mutableStateOf<String?>(null) }
                PTreeSelect(
                    value = value2,
                    onValueChange = { value2 = it },
                    nodes = treeData,
                    placeholder = text.customPlaceholder,
                )
            }
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
private fun treeSelectDemoText(): TreeSelectDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            TreeSelectDemoText(
                title = "PTreeSelect",
                subtitle = "树形选择组件",
                basicSectionTitle = "基础用法",
                placeholder = "请选择",
                withPlaceholderSectionTitle = "自定义占位文本",
                customPlaceholder = "请选择文件",
                root = "根目录",
                folder1 = "文件夹 1",
                folder2 = "文件夹 2",
                file1 = "文件 1.txt",
                file2 = "文件 2.txt",
                file3 = "文件 3.txt",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val nodes = listOf(
                        TreeSelectNode(
                            value = "1",
                            label = "根目录",
                            children = listOf(
                                TreeSelectNode(value = "1-1", label = "文件 1"),
                            ),
                        ),
                    )
                    PTreeSelect(
                        value = value,
                        onValueChange = { value = it },
                        nodes = nodes,
                        placeholder = "请选择",
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            TreeSelectDemoText(
                title = "PTreeSelect",
                subtitle = "Tree select component.",
                basicSectionTitle = "Basic Usage",
                placeholder = "Please select",
                withPlaceholderSectionTitle = "Custom Placeholder",
                customPlaceholder = "Select a file",
                root = "Root",
                folder1 = "Folder 1",
                folder2 = "Folder 2",
                file1 = "File 1.txt",
                file2 = "File 2.txt",
                file3 = "File 3.txt",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val nodes = listOf(
                        TreeSelectNode(
                            value = "1",
                            label = "Root",
                            children = listOf(
                                TreeSelectNode(value = "1-1", label = "File 1"),
                            ),
                        ),
                    )
                    PTreeSelect(
                        value = value,
                        onValueChange = { value = it },
                        nodes = nodes,
                        placeholder = "Please select",
                    )
                    """.trimIndent(),
            )
    }

private data class TreeSelectDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val placeholder: String,
    val withPlaceholderSectionTitle: String,
    val customPlaceholder: String,
    val root: String,
    val folder1: String,
    val folder2: String,
    val file1: String,
    val file2: String,
    val file3: String,
    val codeTitle: String,
    val codeBlock: String,
)
