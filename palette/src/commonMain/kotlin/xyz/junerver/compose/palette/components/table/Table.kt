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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.usetable.TableHolder
import xyz.junerver.compose.hooks.usetable.TableOptions
import xyz.junerver.compose.hooks.usetable.core.ColumnDef
import xyz.junerver.compose.hooks.usetable.Table as HeadlessTable
import xyz.junerver.compose.hooks.usetable.useTable
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Primary PTable component that accepts a TableHolder from useTable.
 *
 * This component provides a Material Design 3 styled table UI that integrates
 * with ComposeHooks' headless table implementation.
 *
 * @param table TableHolder instance from useTable hook
 * @param modifier Modifier to be applied to the table
 * @param colors Color scheme for the table
 * @param emptyContent Content to display when table has no data
 * @param showPagination Whether to show pagination controls
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> PTable(
    table: TableHolder<T>,
    modifier: Modifier = Modifier,
    colors: TableColors = TableDefaults.colors(),
    emptyContent: (@Composable () -> Unit)? = null,
    showPagination: Boolean = true
) {
    val columns = table.columns.value
    val state = table.state.value
    val density = LocalDensity.current

    LazyColumn(modifier = modifier) {
        // Header
        stickyHeader {
            HeadlessTable(table = table) {
                TableHeader { _, _ ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(TableDefaults.HeaderHeight)
                            .background(colors.headerContainerColor),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Selection checkbox column
                        if (state.rowSelection.selectedRowIds.isNotEmpty() || table.rowModel.value.totalRows > 0) {
                            val selectedCount = state.rowSelection.selectedRowIds.size
                            val totalCount = table.rowModel.value.totalRows
                            val isAllSelected = selectedCount == totalCount && totalCount > 0

                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Checkbox(
                                    checked = isAllSelected,
                                    onCheckedChange = { checked ->
                                        table.toggleAllRowsSelection(checked)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = colors.headerContentColor,
                                        uncheckedColor = colors.headerContentColor,
                                        checkmarkColor = colors.headerContainerColor
                                    )
                                )
                            }
                        }

                        // Column headers
                        columns.forEach { column ->
                            val sortingState = state.sorting.sorting.firstOrNull { it.columnId == column.id }
                            val isSorted = sortingState != null
                            val isSortable = column.enableSorting

                            val columnWidth = state.columnSizing.columnSizing[column.id]
                            val widthModifier = if (columnWidth != null) {
                                with(density) { Modifier.width(columnWidth.toDp()) }
                            } else {
                                Modifier.weight(1f)
                            }

                            Box(
                                modifier = Modifier
                                    .then(widthModifier)
                                    .fillMaxHeight()
                                    .clickable(enabled = isSortable) {
                                        table.toggleSorting(column.id, null)
                                    }
                                    .padding(horizontal = TableDefaults.CellContentPadding),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = column.header,
                                        style = PaletteTheme.typography.title,
                                        color = colors.headerContentColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (isSorted) {
                                        Icon(
                                            imageVector = if (!sortingState.desc)
                                                Icons.Filled.ArrowDropUp
                                            else
                                                Icons.Filled.ArrowDropDown,
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
            }
        }

        // Body
        val rows = table.rowModel.value.rows
        if (rows.isEmpty()) {
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
            items(rows, key = { it.id }) { row ->
                val isSelected = state.rowSelection.selectedRowIds.contains(row.id)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TableDefaults.RowHeight)
                        .background(
                            if (isSelected) colors.selectedRowContainerColor
                            else colors.rowContainerColor
                        )
                        .clickable {
                            table.toggleRowSelection(row.id)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection checkbox
                    if (state.rowSelection.selectedRowIds.isNotEmpty() || table.rowModel.value.totalRows > 0) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    table.toggleRowSelection(row.id)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colors.selectedRowContentColor,
                                    uncheckedColor = colors.rowContentColor,
                                    checkmarkColor = colors.selectedRowContainerColor
                                )
                            )
                        }
                    }

                    // Cells
                    columns.forEach { column ->
                        val columnWidth = state.columnSizing.columnSizing[column.id]
                        val widthModifier = if (columnWidth != null) {
                            with(density) { Modifier.width(columnWidth.toDp()) }
                        } else {
                            Modifier.weight(1f)
                        }

                        Box(
                            modifier = Modifier
                                .then(widthModifier)
                                .fillMaxHeight()
                                .padding(horizontal = TableDefaults.CellContentPadding),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides
                                    if (isSelected) colors.selectedRowContentColor
                                    else colors.rowContentColor
                            ) {
                                val cellValue = row.getValue(column)
                                Text(
                                    text = cellValue?.toString().orEmpty(),
                                    style = PaletteTheme.typography.body,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
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

        // Pagination
        if (showPagination && state.pagination.pageSize > 0) {
            item {
                HeadlessTable(table = table) {
                    TablePagination { paginationScope ->
                        PTablePagination(
                            paginationScope = paginationScope,
                            colors = colors,
                            onPreviousPage = { table.previousPage() },
                            onNextPage = { table.nextPage() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Convenience overload that creates TableHolder internally using useTable.
 *
 * @param data List of data items to display
 * @param columns Column definitions
 * @param modifier Modifier to be applied to the table
 * @param optionsOf Configuration block for table options
 * @param colors Color scheme for the table
 * @param emptyContent Content to display when table has no data
 * @param showPagination Whether to show pagination controls
 */
@Composable
fun <T> PTable(
    data: List<T>,
    columns: List<ColumnDef<T, *>>,
    modifier: Modifier = Modifier,
    optionsOf: TableOptions<T>.() -> Unit = {},
    colors: TableColors = TableDefaults.colors(),
    emptyContent: (@Composable () -> Unit)? = null,
    showPagination: Boolean = true
) {
    val table = useTable(
        data = data,
        columns = columns,
        optionsOf = optionsOf
    )

    PTable(
        table = table,
        modifier = modifier,
        colors = colors,
        emptyContent = emptyContent,
        showPagination = showPagination
    )
}

@Composable
private fun PTablePagination(
    paginationScope: xyz.junerver.compose.hooks.usetable.PaginationScope,
    colors: TableColors,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.headerContainerColor)
            .padding(horizontal = TableDefaults.CellContentPadding),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Page ${paginationScope.pageIndex + 1} of ${paginationScope.pageCount}",
            style = PaletteTheme.typography.body,
            color = colors.headerContentColor
        )
    }
}
