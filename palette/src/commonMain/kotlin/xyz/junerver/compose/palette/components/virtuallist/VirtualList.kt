package xyz.junerver.compose.palette.components.virtuallist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> PVirtualList(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemText: (T) -> String = { it.toString() },
) {
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            Text(
                text = itemText(item),
                color = VirtualListDefaults.itemContentColor(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(VirtualListDefaults.ItemHeight)
                    .background(VirtualListDefaults.itemColor())
                    .padding(VirtualListDefaults.ItemPadding)
            )
        }
    }
}
