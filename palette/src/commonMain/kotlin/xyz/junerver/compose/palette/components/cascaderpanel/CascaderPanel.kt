package xyz.junerver.compose.palette.components.cascaderpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.cascader.CascaderOption
import xyz.junerver.compose.palette.components.text.PText

@Immutable
internal data class PanelColumnData(
    val pathValues: List<String>,
    val options: List<CascaderOption>,
    val selectedValue: String?,
)

@Composable
fun PCascaderPanel(
    options: List<CascaderOption>,
    value: List<String> = emptyList(),
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (activePath, setActivePath) = useState(value)
    // Nullable transient hover state is kept with Compose state because useState requires a non-null initial value.
    val hoveredItemState = remember { mutableStateOf<String?>(null) }
    val hoveredItem = hoveredItemState.value

    LaunchedEffect(value) {
        if (value != activePath) {
            setActivePath(value)
        }
    }

    val containerColor = CascaderPanelDefaults.containerColor()
    val itemTextColor = CascaderPanelDefaults.itemTextColor()
    val selectedItemColor = CascaderPanelDefaults.selectedItemColor()
    val selectedItemContainerColor = CascaderPanelDefaults.selectedItemContainerColor()
    val hoverColor = CascaderPanelDefaults.hoverColor()
    val disabledItemTextColor = CascaderPanelDefaults.disabledItemTextColor()
    val dividerColor = CascaderPanelDefaults.dividerColor()
    val dividerWidth = CascaderPanelDefaults.dividerWidth()
    val iconColor = CascaderPanelDefaults.iconColor()
    val trailingIconAlpha = CascaderPanelDefaults.trailingIconAlpha()
    val columnWidth = CascaderPanelDefaults.columnWidth()
    val columnHeight = CascaderPanelDefaults.columnHeight()
    val itemHeight = CascaderPanelDefaults.itemHeight()
    val itemPaddingHorizontal = CascaderPanelDefaults.itemPaddingHorizontal()
    val fontSize = CascaderPanelDefaults.fontSize()
    val arrowSize = CascaderPanelDefaults.arrowSize()

    val columns = remember(options, activePath) {
        buildPanelColumns(options, activePath)
    }

    Row(
        modifier = modifier
            .background(containerColor)
    ) {
        columns.forEachIndexed { index, column ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(dividerWidth)
                        .fillMaxHeight()
                        .background(dividerColor)
                )
            }
            PanelColumn(
                items = column,
                itemTextColor = itemTextColor,
                selectedItemColor = selectedItemColor,
                selectedItemContainerColor = selectedItemContainerColor,
                hoverColor = hoverColor,
                disabledItemTextColor = disabledItemTextColor,
                iconColor = iconColor,
                trailingIconAlpha = trailingIconAlpha,
                columnWidth = columnWidth,
                columnHeight = columnHeight,
                itemHeight = itemHeight,
                itemPaddingHorizontal = itemPaddingHorizontal,
                fontSize = fontSize,
                arrowSize = arrowSize,
                hoveredItem = hoveredItem,
                onHoverItem = { hoveredItemState.value = it },
                onItemClick = { option ->
                    if (option.disabled) return@PanelColumn
                    val newValue = column.pathValues + option.value
                    setActivePath(newValue)
                    onValueChange(newValue)
                }
            )
        }
    }
}

@Composable
private fun PanelColumn(
    items: PanelColumnData,
    itemTextColor: Color,
    selectedItemColor: Color,
    selectedItemContainerColor: Color,
    hoverColor: Color,
    disabledItemTextColor: Color,
    iconColor: Color,
    trailingIconAlpha: Float,
    columnWidth: Dp,
    columnHeight: Dp,
    itemHeight: Dp,
    itemPaddingHorizontal: Dp,
    fontSize: TextUnit,
    arrowSize: Dp,
    hoveredItem: String?,
    onHoverItem: (String?) -> Unit,
    onItemClick: (CascaderOption) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .width(columnWidth)
            .height(columnHeight)
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
                isSelected -> selectedItemContainerColor
                isHovered -> hoverColor
                else -> Color.Transparent
            }
            val textColor = when {
                option.disabled -> disabledItemTextColor
                isSelected -> selectedItemColor
                else -> itemTextColor
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .hoverable(interactionSource, enabled = !option.disabled)
                    .background(backgroundColor)
                    .clickable(enabled = !option.disabled) { onItemClick(option) }
                    .padding(horizontal = itemPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PText(
                    text = option.label,
                    modifier = Modifier.weight(1f),
                    color = textColor,
                    fontSize = fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (hasChildren) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (option.disabled) {
                            disabledItemTextColor
                        } else {
                            iconColor.copy(alpha = trailingIconAlpha)
                        },
                        modifier = Modifier.size(arrowSize)
                    )
                }
            }
        }
    }
}

private fun buildPanelColumns(
    options: List<CascaderOption>,
    value: List<String>,
): List<PanelColumnData> {
    val columns = mutableListOf<PanelColumnData>()
    columns.add(
        PanelColumnData(
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
                PanelColumnData(
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
