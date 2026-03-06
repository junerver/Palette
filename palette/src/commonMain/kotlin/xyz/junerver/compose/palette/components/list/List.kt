package xyz.junerver.compose.palette.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import xyz.junerver.compose.hooks.useState
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
    val (loadMoreArmed, setLoadMoreArmed) = useState(true)

    LaunchedEffect(data.size, loadMoreThreshold) {
        setLoadMoreArmed(true)
    }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.isScrollInProgress, data.size, loadMoreArmed) {
        if (onLoadMore != null && !listState.isScrollInProgress) {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val shouldLoadMore = lastVisibleIndex >= data.size - loadMoreThreshold
            if (!shouldLoadMore && !loadMoreArmed) {
                setLoadMoreArmed(true)
            }
            if (shouldLoadMore && loadMoreArmed) {
                setLoadMoreArmed(false)
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
