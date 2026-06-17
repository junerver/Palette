package xyz.junerver.compose.palette.components.infinitescroll

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.components.loading.PLoading
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PInfiniteScroll(
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hasMore: Boolean = true,
    threshold: Dp = InfiniteScrollDefaults.Threshold,
    loadingContent: (@Composable () -> Unit)? = null,
    noMoreContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val thresholdPx = with(density) { threshold.toPx() }

    LaunchedEffect(listState, loading, hasMore) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                if (!loading && hasMore) {
                    val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@collect
                    val bottomOffset = layoutInfo.viewportEndOffset - lastVisible.offset - lastVisible.size
                    if (bottomOffset <= thresholdPx) {
                        onLoadMore()
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        item(key = "_infinite_scroll_content_") {
            content()
        }
        if (loading) {
            item(key = "_infinite_scroll_loading_") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(InfiniteScrollDefaults.loadingPadding()),
                    contentAlignment = Alignment.Center,
                ) {
                    loadingContent?.invoke() ?: run {
                        PLoading()
                        PText(
                            text = "加载中...",
                            color = InfiniteScrollDefaults.textColor(),
                            style = InfiniteScrollDefaults.textStyle(),
                            modifier = Modifier.padding(top = InfiniteScrollDefaults.loadingPadding()),
                        )
                    }
                }
            }
        }
        if (!hasMore) {
            item(key = "_infinite_scroll_no_more_") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(InfiniteScrollDefaults.loadingPadding()),
                    contentAlignment = Alignment.Center,
                ) {
                    noMoreContent?.invoke() ?: PText(
                        text = "没有更多了",
                        color = InfiniteScrollDefaults.textColor(),
                        style = InfiniteScrollDefaults.textStyle(),
                    )
                }
            }
        }
    }
}
