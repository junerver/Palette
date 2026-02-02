package xyz.junerver.compose.palette.components.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class ColumnConfig<T>(
    val title: String,
    val width: Dp? = null,
    val sortable: Boolean = false,
    val render: @Composable (T) -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> PTable(
    data: List<T>,
    columns: List<ColumnConfig<T>>,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    multiSelect: Boolean = false,
    selectedItems: Set<T> = emptySet(),
    onSelectionChange: (Set<T>) -> Unit = {},
    sortColumn: Int? = null,
    sortAscending: Boolean = true,
    onSortChange: (Int, Boolean) -> Unit = { _, _ -> },
    emptyContent: (@Composable () -> Unit)? = null,
    colors: TableColors = TableDefaults.colors()
) {
    val selectAll = remember(data, selectedItems) {
        data.isNotEmpty() && selectedItems.containsAll(data)
    }

    LazyColumn(modifier = modifier) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TableDefaults.HeaderHeight)
                    .background(colors.headerContainerColor),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectable) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (multiSelect) {
                            Checkbox(
                                checked = selectAll,
                                onCheckedChange = { checked ->
                                    if (checked) onSelectionChange(data.toSet())
                                    else onSelectionChange(emptySet())
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colors.headerContentColor,
                                    uncheckedColor = colors.headerContentColor,
                                    checkmarkColor = colors.headerContainerColor
                                )
                            )
                        }
                    }
                }

                columns.forEachIndexed { index, column ->
                    Box(
                        modifier = Modifier
                            .then(
                                if (column.width != null) Modifier.width(column.width!!)
                                else Modifier.weight(1f)
                            )
                            .fillMaxHeight()
                            .clickable(enabled = column.sortable) {
                                val newAscending = if (sortColumn == index) !sortAscending else true
                                onSortChange(index, newAscending)
                            }
                            .padding(horizontal = TableDefaults.CellContentPadding),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = column.title,
                                style = PaletteTheme.typography.title,
                                color = colors.headerContentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (sortColumn == index) {
                                Icon(
                                    imageVector = if (sortAscending) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    tint = colors.headerContentColor,
                                    modifier = Modifier.height(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(
                thickness = TableDefaults.DividerThickness,
                color = colors.dividerColor
            )
        }

        if (data.isEmpty()) {
            if (emptyContent != null) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        emptyContent()
                    }
                }
            }
        } else {
            items(data, key = { it.hashCode() }) { item ->
                val isSelected = selectedItems.contains(item)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TableDefaults.RowHeight)
                        .background(if (isSelected) colors.selectedRowContainerColor else colors.rowContainerColor)
                        .clickable(enabled = selectable) {
                            val newSelection = if (multiSelect) {
                                if (isSelected) selectedItems - item else selectedItems + item
                            } else {
                                if (isSelected) emptySet() else setOf(item)
                            }
                            onSelectionChange(newSelection)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectable) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    val newSelection = if (multiSelect) {
                                        if (checked) selectedItems + item else selectedItems - item
                                    } else {
                                        if (checked) setOf(item) else emptySet()
                                    }
                                    onSelectionChange(newSelection)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colors.selectedRowContentColor,
                                    uncheckedColor = colors.rowContentColor,
                                    checkmarkColor = colors.selectedRowContainerColor
                                )
                            )
                        }
                    }

                    columns.forEach { column ->
                        Box(
                            modifier = Modifier
                                .then(
                                    if (column.width != null) Modifier.width(column.width)
                                    else Modifier.weight(1f)
                                )
                                .fillMaxHeight()
                                .padding(horizontal = TableDefaults.CellContentPadding),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides if (isSelected) colors.selectedRowContentColor else colors.rowContentColor
                            ) {
                                column.render(item)
                            }
                        }
                    }
                }
                HorizontalDivider(
                    thickness = TableDefaults.DividerThickness,
                    color = colors.dividerColor
                )
            }
        }
    }
}
