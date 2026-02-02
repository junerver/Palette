package xyz.junerver.compose.palette.components.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TreeNode<T>(
    val key: String,
    val data: T,
    val children: List<TreeNode<T>> = emptyList()
)

@Composable
fun <T> PTree(
    nodes: List<TreeNode<T>>,
    modifier: Modifier = Modifier,
    expandedKeys: Set<String> = emptySet(),
    selectedKey: String? = null,
    onExpandChange: (Set<String>) -> Unit = {},
    onSelect: (String) -> Unit = {},
    nodeContent: @Composable (TreeNode<T>) -> Unit
) {
    Column(modifier = modifier) {
        nodes.forEach { node ->
            TreeNodeItem(
                node = node,
                level = 0,
                expandedKeys = expandedKeys,
                selectedKey = selectedKey,
                onExpandChange = onExpandChange,
                onSelect = onSelect,
                nodeContent = nodeContent
            )
        }
    }
}

@Composable
private fun <T> TreeNodeItem(
    node: TreeNode<T>,
    level: Int,
    expandedKeys: Set<String>,
    selectedKey: String?,
    onExpandChange: (Set<String>) -> Unit,
    onSelect: (String) -> Unit,
    nodeContent: @Composable (TreeNode<T>) -> Unit
) {
    val isExpanded = node.key in expandedKeys
    val isSelected = node.key == selectedKey
    val hasChildren = node.children.isNotEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TreeDefaults.NodeHeight)
            .background(
                if (isSelected) TreeDefaults.selectedColor().copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable { onSelect(node.key) }
            .padding(start = (level * TreeDefaults.Indent.value).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasChildren) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(TreeDefaults.IconSize)
                    .clickable {
                        val newKeys = if (isExpanded) {
                            expandedKeys - node.key
                        } else {
                            expandedKeys + node.key
                        }
                        onExpandChange(newKeys)
                    },
                tint = TreeDefaults.iconColor()
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(TreeDefaults.IconSize + 8.dp))
        }

        nodeContent(node)
    }

    if (hasChildren && isExpanded) {
        node.children.forEach { child ->
            TreeNodeItem(
                node = child,
                level = level + 1,
                expandedKeys = expandedKeys,
                selectedKey = selectedKey,
                onExpandChange = onExpandChange,
                onSelect = onSelect,
                nodeContent = nodeContent
            )
        }
    }
}
