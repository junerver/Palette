package xyz.junerver.compose.palette.components.treeselect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class TreeSelectNode(
    val value: String,
    val label: String,
    val children: List<TreeSelectNode> = emptyList(),
    val disabled: Boolean = false,
)

@Composable
fun PTreeSelect(
    value: String?,
    onValueChange: (String?) -> Unit,
    nodes: List<TreeSelectNode>,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    showSearch: Boolean = false,
    searchPlaceholder: String = "",
    colors: TreeSelectColors = TreeSelectDefaults.colors(),
) {
    val (expanded, setExpanded) = useState(false)
    val (anchorWidth, setAnchorWidth) = useState(0)
    val (expandedKeys, setExpandedKeys) = useState<Set<String>>(emptySet())
    val (searchQuery, setSearchQuery) = useState("")
    val hoveredKeyState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val hoveredKey = hoveredKeyState.value

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val selectedLabel = remember(nodes, value, placeholder) {
        if (value == null) placeholder
        else resolveSelectedLabel(nodes, value) ?: placeholder
    }
    val isPlaceholder = value == null || selectedLabel == placeholder

    val displayNodes = remember(nodes, showSearch, searchQuery) {
        if (showSearch && searchQuery.isNotEmpty()) {
            filterNodes(nodes, searchQuery)
        } else {
            nodes
        }
    }

    val shape = RoundedCornerShape(size.cornerRadius)
    val borderWidth = TreeSelectDefaults.borderWidth()
    val dropdownMinWidth = TreeSelectDefaults.dropdownWidth()
    val dropdownMaxHeight = TreeSelectDefaults.dropdownMaxHeight()
    val nodeHeight = TreeSelectDefaults.nodeHeight()
    val indent = TreeSelectDefaults.indent()
    val arrowSize = TreeSelectDefaults.arrowSize()
    val iconSpacing = TreeSelectDefaults.searchPadding() / 2
    val searchPadding = TreeSelectDefaults.searchPadding()
    val fontSize = TreeSelectDefaults.fontSize()
    val trailingIconAlpha = TreeSelectDefaults.trailingIconAlpha()
    val currentBorderColor = when {
        !disabled && isHovered -> colors.borderColor
        !disabled -> colors.borderColor
        else -> colors.disabledBorderColor
    }

    val density = LocalDensity.current
    val dropdownWidth = with(density) { anchorWidth.toDp() }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { setAnchorWidth(it.width) }
                .height(size.height)
                .border(
                    width = borderWidth,
                    color = currentBorderColor,
                    shape = shape
                )
                .clip(shape)
                .background(colors.containerColor)
                .clickable(
                    enabled = !disabled,
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    setExpanded(!expanded)
                    if (expanded) {
                        setSearchQuery("")
                    }
                }
                .padding(
                    horizontal = size.horizontalPadding,
                    vertical = size.verticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedLabel,
                modifier = Modifier.weight(1f),
                style = PaletteTheme.typography.body.copy(fontSize = size.fontSize),
                color = when {
                    disabled -> colors.disabledTextColor
                    isPlaceholder -> colors.placeholderColor
                    else -> colors.textColor
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (disabled) colors.disabledTextColor else colors.textColor,
                modifier = Modifier.alpha(trailingIconAlpha)
            )
        }

        if (expanded) {
            Popup(
                onDismissRequest = {
                    setExpanded(false)
                    setSearchQuery("")
                },
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize,
                    ): IntOffset {
                        val x = anchorBounds.left
                        val spaceBelow = windowSize.height - anchorBounds.bottom
                        val spaceAbove = anchorBounds.top
                        val y = if (spaceBelow >= popupContentSize.height || spaceBelow >= spaceAbove) {
                            anchorBounds.bottom
                        } else {
                            anchorBounds.top - popupContentSize.height
                        }
                        return IntOffset(x, y)
                    }
                },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .width(dropdownWidth.coerceAtLeast(dropdownMinWidth))
                        .background(
                            colors.dropdownContainerColor,
                            RoundedCornerShape(size.cornerRadius)
                        )
                        .border(
                            width = borderWidth,
                            color = colors.borderColor,
                            shape = RoundedCornerShape(size.cornerRadius)
                        )
                ) {
                    if (showSearch) {
                        Box(
                            modifier = Modifier.padding(searchPadding)
                        ) {
                            BorderTextField(
                                value = searchQuery,
                                onValueChange = { setSearchQuery(it) },
                                placeholder = searchPlaceholder,
                                size = ComponentSize.Small,
                            )
                        }
                    }

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .heightIn(max = dropdownMaxHeight)
                            .verticalScroll(scrollState)
                    ) {
                        if (displayNodes.isEmpty()) {
                            Text(
                                text = PaletteTheme.strings.selectNoResult,
                                modifier = Modifier.padding(searchPadding),
                                color = colors.placeholderColor,
                                style = PaletteTheme.typography.body.copy(fontSize = fontSize),
                            )
                        } else {
                            displayNodes.forEach { node ->
                                TreeNodeItem(
                                    node = node,
                                    level = 0,
                                    selectedValue = value,
                                    expandedKeys = expandedKeys,
                                    hoveredKey = hoveredKey,
                                    colors = colors,
                                    nodeHeight = nodeHeight,
                                    indent = indent,
                                    arrowSize = arrowSize,
                                    iconSpacing = iconSpacing,
                                    fontSize = fontSize,
                                    onExpandToggle = { key ->
                                        setExpandedKeys(
                                            if (key in expandedKeys) expandedKeys - key
                                            else expandedKeys + key
                                        )
                                    },
                                    onHover = { hoveredKeyState.value = it },
                                    onSelect = { selectedValue ->
                                        onValueChange(selectedValue)
                                        setExpanded(false)
                                        setSearchQuery("")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: TreeSelectNode,
    level: Int,
    selectedValue: String?,
    expandedKeys: Set<String>,
    hoveredKey: String?,
    colors: TreeSelectColors,
    nodeHeight: Dp,
    indent: Dp,
    arrowSize: Dp,
    iconSpacing: Dp,
    fontSize: TextUnit,
    onExpandToggle: (String) -> Unit,
    onHover: (String?) -> Unit,
    onSelect: (String) -> Unit,
) {
    val isExpanded = node.value in expandedKeys
    val isSelected = node.value == selectedValue
    val hasChildren = node.children.isNotEmpty()
    val isHovered = node.value == hoveredKey
    val interactionSource = remember { MutableInteractionSource() }
    val isItemHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isSelected -> colors.selectedNodeContainerColor
        isHovered || isItemHovered -> colors.hoverColor
        else -> Color.Transparent
    }
    val textColor = when {
        node.disabled -> colors.disabledNodeTextColor
        isSelected -> colors.selectedNodeTextColor
        else -> colors.nodeTextColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(nodeHeight)
            .hoverable(interactionSource, enabled = !node.disabled)
            .background(backgroundColor)
            .clickable(enabled = !node.disabled) {
                onSelect(node.value)
            }
            .padding(start = indent * level.toFloat()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasChildren) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .size(arrowSize)
                    .clickable(enabled = !node.disabled) {
                        onExpandToggle(node.value)
                    },
                tint = if (node.disabled) colors.disabledNodeTextColor else colors.iconColor
            )
            Spacer(modifier = Modifier.width(iconSpacing))
        } else {
            Spacer(modifier = Modifier.width(arrowSize + iconSpacing))
        }

        Text(
            text = node.label,
            modifier = Modifier.weight(1f),
            style = PaletteTheme.typography.body.copy(fontSize = fontSize),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (hasChildren && isExpanded) {
        node.children.forEach { child ->
            TreeNodeItem(
                node = child,
                level = level + 1,
                selectedValue = selectedValue,
                expandedKeys = expandedKeys,
                hoveredKey = hoveredKey,
                colors = colors,
                nodeHeight = nodeHeight,
                indent = indent,
                arrowSize = arrowSize,
                iconSpacing = iconSpacing,
                fontSize = fontSize,
                onExpandToggle = onExpandToggle,
                onHover = onHover,
                onSelect = onSelect
            )
        }
    }
}

private fun resolveSelectedLabel(
    nodes: List<TreeSelectNode>,
    value: String,
): String? {
    for (node in nodes) {
        if (node.value == value) return node.label
        if (node.children.isNotEmpty()) {
            val childLabel = resolveSelectedLabel(node.children, value)
            if (childLabel != null) {
                return "${node.label}${TreeSelectDefaults.Separator}$childLabel"
            }
        }
    }
    return null
}

private fun filterNodes(
    nodes: List<TreeSelectNode>,
    query: String,
): List<TreeSelectNode> {
    val result = mutableListOf<TreeSelectNode>()
    for (node in nodes) {
        val matchesSelf = node.label.contains(query, ignoreCase = true)
        val filteredChildren = if (node.children.isNotEmpty()) {
            filterNodes(node.children, query)
        } else {
            emptyList()
        }
        if (matchesSelf || filteredChildren.isNotEmpty()) {
            result.add(node.copy(children = filteredChildren))
        }
    }
    return result
}
