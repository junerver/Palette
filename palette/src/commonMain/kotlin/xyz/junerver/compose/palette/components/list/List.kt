package xyz.junerver.compose.palette.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.empty.PEmpty

@Composable
fun <T> PList(
    data: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    showDivider: Boolean = true,
    emptyContent: (@Composable () -> Unit)? = null,
    onLoadMore: (() -> Unit)? = null,
    loadMoreThreshold: Int = 5,
    itemContent: @Composable (T) -> Unit
) {
    if (data.isEmpty()) {
        if (emptyContent != null) {
            emptyContent()
        } else {
            PEmpty(
                modifier = modifier,
                title = "暂无数据",
                description = "列表为空"
            )
        }
        return
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState.firstVisibleItemIndex, listState.isScrollInProgress) {
        if (onLoadMore != null && !listState.isScrollInProgress) {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            if (lastVisibleIndex >= data.size - loadMoreThreshold) {
                onLoadMore()
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(
            items = data,
            key = key
        ) { item ->
            Column {
                itemContent(item)
                if (showDivider) {
                    Divider(
                        thickness = ListDefaults.DividerHeight,
                        color = ListDefaults.dividerColor()
                    )
                }
            }
        }
    }
}
