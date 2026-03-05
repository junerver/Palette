package xyz.junerver.compose.palette.components.sortable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> PSortable(
    items: List<SortableItem<T>>,
    modifier: Modifier = Modifier,
    itemText: (SortableItem<T>) -> String = { it.id },
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SortableDefaults.ItemSpacing)
    ) {
        items.forEach { item ->
            Text(
                text = itemText(item),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SortableDefaults.itemColor())
                    .padding(SortableDefaults.ItemPadding)
            )
        }
    }
}
