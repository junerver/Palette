package xyz.junerver.compose.palette.components.cascader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class CascaderOption(
    val value: String,
    val label: String,
    val children: List<CascaderOption> = emptyList(),
    val disabled: Boolean = false,
)

@Composable
fun PCascader(
    options: List<CascaderOption>,
    value: List<String> = emptyList(),
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    expandTrigger: CascaderExpandTrigger = CascaderExpandTrigger.Click,
    colors: CascaderColors = CascaderDefaults.colors(),
) {
    val (expanded, setExpanded) = useState(false)
    val hoveredItemState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val hoveredItem = hoveredItemState.value
    val (anchorWidth, setAnchorWidth) = useState(0)

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val selectedPath = remember(options, value) {
        resolveSelectedPath(options, value)
    }
    val selectedLabel = remember(selectedPath, placeholder) {
        if (selectedPath.isEmpty()) placeholder
        else selectedPath.joinToString(CascaderDefaults.Separator) { it.label }
    }
    val isPlaceholder = value.isEmpty() || selectedPath.isEmpty()

    val shape = RoundedCornerShape(size.cornerRadius)
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
                    width = CascaderDefaults.BorderWidth,
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
                modifier = Modifier.alpha(CascaderDefaults.TrailingIconAlpha)
            )
        }

        if (expanded) {
            val columns = remember(options, value) {
                buildColumns(options, value)
            }

            Popup(
                onDismissRequest = { setExpanded(false) },
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
                Row(
                    modifier = Modifier
                        .widthIn(min = dropdownWidth)
                        .heightIn(max = CascaderDefaults.ColumnMaxHeight)
                        .background(colors.dropdownContainerColor)
                        .border(
                            width = CascaderDefaults.BorderWidth,
                            color = colors.borderColor,
                            shape = RoundedCornerShape(size.cornerRadius)
                        )
                ) {
                    columns.forEachIndexed { index, column ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .width(CascaderDefaults.BorderWidth)
                                    .fillMaxHeight()
                                    .background(colors.borderColor)
                            )
                        }
                        CascaderColumn(
                            items = column,
                            colors = colors,
                            expandTrigger = expandTrigger,
                            hoveredItem = hoveredItem,
                            onHoverItem = { hoveredItemState.value = it },
                            onItemClick = { option ->
                                if (option.disabled) return@CascaderColumn
                                val newValue = column.pathValues + option.value
                                if (option.children.isEmpty()) {
                                    onValueChange(newValue)
                                    setExpanded(false)
                                } else {
                                    onValueChange(newValue)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CascaderColumn(
    items: CascaderColumnData,
    colors: CascaderColors,
    expandTrigger: CascaderExpandTrigger,
    hoveredItem: String?,
    onHoverItem: (String?) -> Unit,
    onItemClick: (CascaderOption) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .width(CascaderDefaults.ColumnWidth)
            .fillMaxHeight()
    ) {
        items(
            items = items.options,
            key = { it.value }
        ) { option ->
            val isSelected = items.selectedValue == option.value
            val isHovered = hoveredItem == option.value
            val hasChildren = option.children.isNotEmpty()
            val interactionSource = remember { MutableInteractionSource() }

            val backgroundColor = when {
                isSelected -> colors.selectedItemTextColor.copy(alpha = 0.08f)
                isHovered -> colors.hoverColor
                else -> Color.Transparent
            }
            val textColor = when {
                option.disabled -> colors.disabledItemTextColor
                isSelected -> colors.selectedItemTextColor
                else -> colors.itemTextColor
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CascaderDefaults.ItemHeight)
                    .hoverable(interactionSource, enabled = !option.disabled)
                    .background(backgroundColor)
                    .clickable(enabled = !option.disabled) { onItemClick(option) }
                    .padding(horizontal = CascaderDefaults.ItemPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.label,
                    modifier = Modifier.weight(1f),
                    style = PaletteTheme.typography.body.copy(fontSize = CascaderDefaults.FontSize),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (hasChildren) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (option.disabled) colors.disabledItemTextColor else colors.itemTextColor.copy(
                            alpha = CascaderDefaults.TrailingIconAlpha
                        ),
                        modifier = Modifier.size(CascaderDefaults.ArrowSize)
                    )
                }
            }
        }
    }
}

private data class CascaderColumnData(
    val pathValues: List<String>,
    val options: List<CascaderOption>,
    val selectedValue: String?,
)

private fun resolveSelectedPath(
    options: List<CascaderOption>,
    value: List<String>,
): List<CascaderOption> {
    if (value.isEmpty()) return emptyList()
    val result = mutableListOf<CascaderOption>()
    var current = options
    for (v in value) {
        val found = current.firstOrNull { it.value == v } ?: break
        result.add(found)
        current = found.children
    }
    return result
}

private fun buildColumns(
    options: List<CascaderOption>,
    value: List<String>,
): List<CascaderColumnData> {
    val columns = mutableListOf<CascaderColumnData>()
    columns.add(
        CascaderColumnData(
            pathValues = emptyList(),
            options = options,
            selectedValue = value.firstOrNull()
        )
    )
    var current = options
    for (i in value.indices) {
        val v = value[i]
        val found = current.firstOrNull { it.value == v } ?: break
        if (found.children.isNotEmpty()) {
            columns.add(
                CascaderColumnData(
                    pathValues = value.take(i + 1),
                    options = found.children,
                    selectedValue = value.getOrNull(i + 1)
                )
            )
            current = found.children
        } else {
            break
        }
    }
    return columns
}
