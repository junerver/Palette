package xyz.junerver.compose.palette.components.collapse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.hooks.useState

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
    val animationDurationMillis = CollapseDefaults.animationDurationMillis()
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(animationDurationMillis)
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CollapseDefaults.titleHeight())
                .clickable { onExpandChange(!expanded) }
                .padding(horizontal = CollapseDefaults.titleHorizontalPadding()),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = titleColor,
                style = CollapseDefaults.titleTextStyle()
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
            enter = expandVertically(animationSpec = tween(animationDurationMillis)),
            exit = shrinkVertically(animationSpec = tween(animationDurationMillis))
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Column(
                    modifier = Modifier.padding(CollapseDefaults.contentPadding()),
                    content = content
                )
            }
        }
    }
}

@Composable
fun PCollapse(
    items: List<CollapseItemData>,
    modifier: Modifier = Modifier,
    accordion: Boolean = false,
    expandedKeys: Set<String>? = null,
    defaultExpandedKeys: Set<String> = emptySet(),
    onExpandChange: (Set<String>) -> Unit = {}
) {
    val (uncontrolledExpandedKeys, setUncontrolledExpandedKeys) = useState(defaultExpandedKeys)
    val activeExpandedKeys = expandedKeys ?: uncontrolledExpandedKeys
    val displayExpandedKeys = activeExpandedKeys.normalizeExpandedKeys(accordion)

    Column(modifier = modifier) {
        items.forEach { item ->
            val isExpanded = item.key in displayExpandedKeys
            
            PCollapseItem(
                title = item.title,
                expanded = isExpanded,
                onExpandChange = { shouldExpand ->
                    val newKeys = if (accordion) {
                        if (shouldExpand) setOf(item.key) else emptySet()
                    } else {
                        if (shouldExpand) {
                            displayExpandedKeys + item.key
                        } else {
                            displayExpandedKeys - item.key
                        }
                    }.normalizeExpandedKeys(accordion)
                    if (expandedKeys == null) {
                        setUncontrolledExpandedKeys(newKeys)
                    }
                    onExpandChange(newKeys)
                },
                content = item.content
            )
        }
    }
}

private fun Set<String>.normalizeExpandedKeys(accordion: Boolean): Set<String> =
    if (accordion) take(1).toSet() else this
