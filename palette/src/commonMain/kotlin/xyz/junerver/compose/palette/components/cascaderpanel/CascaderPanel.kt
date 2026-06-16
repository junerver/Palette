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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.cascader.CascaderOption
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteTheme

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
    val hoveredItemState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val hoveredItem = hoveredItemState.value

    val containerColor = CascaderPanelDefaults.containerColor()
    val itemTextColor = CascaderPanelDefaults.itemTextColor()
    val selectedItemColor = CascaderPanelDefaults.selectedItemColor()
    val hoverColor = CascaderPanelDefaults.hoverColor()

    val columns = remember(options, value) {
        buildPanelColumns(options, value)
    }

    Row(
        modifier = modifier
            .background(containerColor)
    ) {
        columns.forEachIndexed { index, column ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(PaletteTheme.colors.border)
                )
            }
            PanelColumn(
                items = column,
                itemTextColor = itemTextColor,
                selectedItemColor = selectedItemColor,
                hoverColor = hoverColor,
                hoveredItem = hoveredItem,
                onHoverItem = { hoveredItemState.value = it },
                onItemClick = { option ->
                    if (option.disabled) return@PanelColumn
                    val newValue = column.pathValues + option.value
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
    hoverColor: Color,
    hoveredItem: String?,
    onHoverItem: (String?) -> Unit,
    onItemClick: (CascaderOption) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .width(CascaderPanelDefaults.ColumnWidth)
            .height(CascaderPanelDefaults.ColumnHeight)
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
                isSelected -> selectedItemColor.copy(alpha = 0.08f)
                isHovered -> hoverColor.copy(alpha = 0.5f)
                else -> Color.Transparent
            }
            val textColor = when {
                option.disabled -> itemTextColor.copy(alpha = 0.5f)
                isSelected -> selectedItemColor
                else -> itemTextColor
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CascaderPanelDefaults.ItemHeight)
                    .hoverable(interactionSource, enabled = !option.disabled)
                    .background(backgroundColor)
                    .clickable(enabled = !option.disabled) { onItemClick(option) }
                    .padding(horizontal = CascaderPanelDefaults.ItemPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PText(
                    text = option.label,
                    modifier = Modifier.weight(1f),
                    color = textColor,
                    fontSize = CascaderPanelDefaults.FontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (hasChildren) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (option.disabled) {
                            itemTextColor.copy(alpha = 0.5f)
                        } else {
                            itemTextColor.copy(alpha = 0.72f)
                        },
                        modifier = Modifier.size(CascaderPanelDefaults.ArrowSize)
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
