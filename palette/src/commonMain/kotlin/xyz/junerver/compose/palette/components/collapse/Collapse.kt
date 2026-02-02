package xyz.junerver.compose.palette.components.collapse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class CollapseItemData(
    val key: String,
    val title: String,
    val content: @Composable ColumnScope.() -> Unit
)

@Composable
fun PCollapseItem(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = CollapseDefaults.titleColor(),
    contentColor: Color = CollapseDefaults.contentColor(),
    content: @Composable ColumnScope.() -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CollapseDefaults.TitleHeight)
                .clickable { onExpandChange(!expanded) }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = titleColor,
                style = PaletteTheme.typography.title
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotation),
                tint = CollapseDefaults.iconColor()
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(CollapseDefaults.ContentPadding),
                content = content
            )
        }
    }
}

@Composable
fun PCollapse(
    items: List<CollapseItemData>,
    modifier: Modifier = Modifier,
    accordion: Boolean = false,
    expandedKeys: Set<String> = emptySet(),
    onExpandChange: (Set<String>) -> Unit = {}
) {
    Column(modifier = modifier) {
        items.forEach { item ->
            val isExpanded = item.key in expandedKeys
            
            PCollapseItem(
                title = item.title,
                expanded = isExpanded,
                onExpandChange = { shouldExpand ->
                    val newKeys = if (accordion) {
                        if (shouldExpand) setOf(item.key) else emptySet()
                    } else {
                        if (shouldExpand) {
                            expandedKeys + item.key
                        } else {
                            expandedKeys - item.key
                        }
                    }
                    onExpandChange(newKeys)
                },
                content = item.content
            )
        }
    }
}
