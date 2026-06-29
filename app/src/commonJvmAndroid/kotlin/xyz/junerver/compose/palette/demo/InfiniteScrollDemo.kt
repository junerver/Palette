package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.infinitescroll.PInfiniteScroll
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun InfiniteScrollDemo() {
    val text = infiniteScrollDemoText()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            var items by remember { mutableStateOf((1..10).toList()) }
            var loading by remember { mutableStateOf(false) }
            var hasMore by remember { mutableStateOf(true) }

            PInfiniteScroll(
                modifier = Modifier.height(300.dp),
                loading = loading,
                hasMore = hasMore,
                onLoadMore = {
                    if (!loading && hasMore) {
                        loading = true
                        items = items + ((items.size + 1)..(items.size + 5)).toList()
                        if (items.size >= 30) hasMore = false
                        loading = false
                    }
                },
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items.forEach { index ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            PText(
                                text = "${text.itemPrefix} $index",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.loadingSectionTitle) {
            PInfiniteScroll(
                modifier = Modifier.height(150.dp),
                loading = true,
                hasMore = true,
                onLoadMore = {},
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    (1..3).forEach { index ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            PText(
                                text = "${text.itemPrefix} $index",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.noMoreSectionTitle) {
            PInfiniteScroll(
                modifier = Modifier.height(150.dp),
                loading = false,
                hasMore = false,
                onLoadMore = {},
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    (1..3).forEach { index ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            PText(
                                text = "${text.itemPrefix} $index",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun infiniteScrollDemoText(): InfiniteScrollDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            InfiniteScrollDemoText(
                title = "PInfiniteScroll",
                subtitle = "滚动到底部自动加载更多的容器组件",
                basicSectionTitle = "基础用法",
                loadingSectionTitle = "加载中状态",
                noMoreSectionTitle = "没有更多",
                itemPrefix = "列表项",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var items by remember { mutableStateOf((1..10).toList()) }
                    var loading by remember { mutableStateOf(false) }
                    var hasMore by remember { mutableStateOf(true) }

                    PInfiniteScroll(
                        loading = loading,
                        hasMore = hasMore,
                        onLoadMore = {
                            loading = true
                            // 加载更多数据
                            loading = false
                        },
                    ) {
                        // 列表内容
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            InfiniteScrollDemoText(
                title = "PInfiniteScroll",
                subtitle = "A container that auto-loads more content when scrolled to the bottom.",
                basicSectionTitle = "Basic Usage",
                loadingSectionTitle = "Loading State",
                noMoreSectionTitle = "No More Data",
                itemPrefix = "Item",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var items by remember { mutableStateOf((1..10).toList()) }
                    var loading by remember { mutableStateOf(false) }
                    var hasMore by remember { mutableStateOf(true) }

                    PInfiniteScroll(
                        loading = loading,
                        hasMore = hasMore,
                        onLoadMore = {
                            loading = true
                            // Load more data
                            loading = false
                        },
                    ) {
                        // List content
                    }
                    """.trimIndent(),
            )
    }

private data class InfiniteScrollDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val loadingSectionTitle: String,
    val noMoreSectionTitle: String,
    val itemPrefix: String,
    val codeTitle: String,
    val codeBlock: String,
)
