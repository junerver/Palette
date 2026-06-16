package xyz.junerver.compose.palette.components.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class TransferItem(
    val key: String,
    val title: String,
    val disabled: Boolean = false,
)

@Composable
fun PTransfer(
    dataSource: List<TransferItem>,
    targetKeys: List<String>,
    onTargetKeysChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    titles: Pair<String, String> = Pair("源列表", "目标列表"),
    searchable: Boolean = false,
    showSelectAll: Boolean = true,
) {
    val (searchQuery, setSearchQuery) = useState("")
    val sourceSelectedKeys = remember { mutableStateListOf<String>() }
    val targetSelectedKeys = remember { mutableStateListOf<String>() }

    val sourceItems = remember(dataSource, targetKeys, searchQuery) {
        dataSource.filter { it.key !in targetKeys && (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true)) }
    }
    val targetItems = remember(dataSource, targetKeys, searchQuery) {
        dataSource.filter { it.key in targetKeys && (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true)) }
    }

    val shape = RoundedCornerShape(TransferDefaults.CornerRadius)

    Row(
        modifier = modifier.height(TransferDefaults.Height),
        horizontalArrangement = Arrangement.spacedBy(TransferDefaults.ButtonSpacing),
    ) {
        TransferPanel(
            title = titles.first,
            items = sourceItems,
            selectedKeys = sourceSelectedKeys,
            searchable = searchable,
            showSelectAll = showSelectAll,
            searchQuery = searchQuery,
            onSearchQueryChange = setSearchQuery,
            shape = shape,
            modifier = Modifier.width(TransferDefaults.Width).fillMaxHeight(),
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val canMoveRight = sourceSelectedKeys.isNotEmpty()
            val canMoveLeft = targetSelectedKeys.isNotEmpty()

            Box(
                modifier = Modifier
                    .size(TransferDefaults.ButtonWidth, 32.dp)
                    .clip(RoundedCornerShape(TransferDefaults.CornerRadius))
                    .background(if (canMoveRight) TransferDefaults.buttonColor() else TransferDefaults.disabledButtonColor())
                    .clickable(enabled = canMoveRight) {
                        onTargetKeysChange(targetKeys + sourceSelectedKeys.toList())
                        sourceSelectedKeys.clear()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = PaletteTheme.colors.onPrimary,
                    modifier = Modifier.size(TransferDefaults.IconSize),
                )
            }

            Spacer(modifier = Modifier.height(TransferDefaults.ButtonSpacing))

            Box(
                modifier = Modifier
                    .size(TransferDefaults.ButtonWidth, 32.dp)
                    .clip(RoundedCornerShape(TransferDefaults.CornerRadius))
                    .background(if (canMoveLeft) TransferDefaults.buttonColor() else TransferDefaults.disabledButtonColor())
                    .clickable(enabled = canMoveLeft) {
                        onTargetKeysChange(targetKeys - targetSelectedKeys.toSet())
                        targetSelectedKeys.clear()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = PaletteTheme.colors.onPrimary,
                    modifier = Modifier.size(TransferDefaults.IconSize),
                )
            }
        }

        TransferPanel(
            title = titles.second,
            items = targetItems,
            selectedKeys = targetSelectedKeys,
            searchable = searchable,
            showSelectAll = showSelectAll,
            searchQuery = searchQuery,
            onSearchQueryChange = setSearchQuery,
            shape = shape,
            modifier = Modifier.width(TransferDefaults.Width).fillMaxHeight(),
        )
    }
}

@Composable
private fun TransferPanel(
    title: String,
    items: List<TransferItem>,
    selectedKeys: MutableList<String>,
    searchable: Boolean,
    showSelectAll: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(TransferDefaults.containerColor())
            .padding(1.dp)
            .background(TransferDefaults.containerColor(), shape),
    ) {
        TransferHeader(
            title = title,
            selectedCount = selectedKeys.size,
            totalCount = items.size,
        )

        if (searchable) {
            TransferSearchField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
            )
        }

        if (showSelectAll) {
            SelectAllRow(
                items = items,
                selectedKeys = selectedKeys,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            items.forEach { item ->
                TransferItemRow(
                    item = item,
                    checked = item.key in selectedKeys,
                    onCheckedChange = { checked ->
                        if (checked) selectedKeys.add(item.key) else selectedKeys.remove(item.key)
                    },
                )
            }
        }
    }
}

@Composable
private fun TransferHeader(
    title: String,
    selectedCount: Int,
    totalCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TransferDefaults.HeaderHeight)
            .background(TransferDefaults.headerColor())
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PText(
            text = title,
            color = TransferDefaults.headerTextColor(),
            fontSize = TransferDefaults.FontSize,
        )
        PText(
            text = "$selectedCount/$totalCount",
            color = TransferDefaults.headerTextColor(),
            fontSize = TransferDefaults.FontSize,
        )
    }
}

@Composable
private fun TransferSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(TransferDefaults.SearchHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(PaletteTheme.colors.border.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = TransferDefaults.SearchFontSize,
                color = PaletteTheme.colors.onSurface,
            ),
            cursorBrush = SolidColor(PaletteTheme.colors.primary),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box {
                    innerTextField()
                    if (query.isEmpty()) {
                        PText(
                            text = "搜索",
                            fontSize = TransferDefaults.SearchFontSize,
                            color = PaletteTheme.colors.hint,
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun SelectAllRow(
    items: List<TransferItem>,
    selectedKeys: MutableList<String>,
) {
    val selectableItems = items.filter { !it.disabled }
    val allSelected = selectableItems.isNotEmpty() && selectableItems.all { it.key in selectedKeys }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TransferDefaults.ItemHeight)
            .clickable {
                if (allSelected) {
                    selectedKeys.clear()
                } else {
                    selectedKeys.clear()
                    selectedKeys.addAll(selectableItems.map { it.key })
                }
            }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColoredCheckBox(
            checked = allSelected,
            onCheckedChange = { checked ->
                if (checked) {
                    selectedKeys.clear()
                    selectedKeys.addAll(selectableItems.map { it.key })
                } else {
                    selectedKeys.clear()
                }
            },
            size = xyz.junerver.compose.palette.core.spec.ComponentSize.Small,
        )
        PText(
            text = "全选",
            fontSize = TransferDefaults.FontSize,
            color = TransferDefaults.itemTextColor(),
        )
    }
}

@Composable
private fun TransferItemRow(
    item: TransferItem,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TransferDefaults.ItemHeight)
            .background(if (checked) TransferDefaults.selectedItemColor() else Color.Transparent)
            .clickable(enabled = !item.disabled) { onCheckedChange(!checked) }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColoredCheckBox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = !item.disabled,
            size = xyz.junerver.compose.palette.core.spec.ComponentSize.Small,
        )
        PText(
            text = item.title,
            fontSize = TransferDefaults.FontSize,
            color = if (item.disabled) PaletteTheme.colors.hint else TransferDefaults.itemTextColor(),
        )
    }
}
