package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.tree.PTree
import xyz.junerver.compose.palette.components.tree.TreeNode

@Composable
fun TreeDemo() {
    val text = treeDemoText()

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

        val treeData = listOf(
            TreeNode(
                key = "1",
                data = text.root,
                children = listOf(
                    TreeNode(key = "1-1", data = text.folder1, children = listOf(
                        TreeNode(key = "1-1-1", data = text.file1),
                        TreeNode(key = "1-1-2", data = text.file2)
                    )),
                    TreeNode(key = "1-2", data = text.folder2, children = listOf(
                        TreeNode(key = "1-2-1", data = text.file3)
                    ))
                )
            )
        )

        var expandedKeys by remember { mutableStateOf(setOf("1")) }
        var selectedKey by remember { mutableStateOf<String?>(null) }

        DemoSection(title = text.basicSectionTitle) {
            PTree(
                nodes = treeData,
                expandedKeys = expandedKeys,
                selectedKey = selectedKey,
                onExpandChange = { expandedKeys = it },
                onSelect = { selectedKey = it }
            ) { node ->
                Text(node.data)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun treeDemoText(): TreeDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> TreeDemoText(
        title = "PTree 树形控件",
        subtitle = "用于展示树形结构数据的组件",
        basicSectionTitle = "基础树形控件",
        root = "根目录",
        folder1 = "文件夹 1",
        folder2 = "文件夹 2",
        file1 = "文件 1.txt",
        file2 = "文件 2.txt",
        file3 = "文件 3.txt",
    )

    Language.EN_US -> TreeDemoText(
        title = "PTree",
        subtitle = "A component for displaying tree-structured data",
        basicSectionTitle = "Basic Tree",
        root = "Root",
        folder1 = "Folder 1",
        folder2 = "Folder 2",
        file1 = "File 1.txt",
        file2 = "File 2.txt",
        file3 = "File 3.txt",
    )
}

private data class TreeDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val root: String,
    val folder1: String,
    val folder2: String,
    val file1: String,
    val file2: String,
    val file3: String,
)
