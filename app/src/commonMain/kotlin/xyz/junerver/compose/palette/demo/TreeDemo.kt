package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.tree.PTree
import xyz.junerver.compose.palette.components.tree.TreeNode

@Composable
fun TreeDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PTree 树形控件",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "用于展示树形结构数据的组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        val treeData = listOf(
            TreeNode(
                key = "1",
                data = "根目录",
                children = listOf(
                    TreeNode(key = "1-1", data = "文件夹 1", children = listOf(
                        TreeNode(key = "1-1-1", data = "文件 1.txt"),
                        TreeNode(key = "1-1-2", data = "文件 2.txt")
                    )),
                    TreeNode(key = "1-2", data = "文件夹 2", children = listOf(
                        TreeNode(key = "1-2-1", data = "文件 3.txt")
                    ))
                )
            )
        )

        var expandedKeys by remember { mutableStateOf(setOf("1")) }
        var selectedKey by remember { mutableStateOf<String?>(null) }

        DemoSection(title = "基础树形控件") {
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
