package xyz.junerver.compose.palette.components.sortable

data class SortableItem<T>(
    val id: String,
    val payload: T,
)

fun <T> moveItem(
    items: List<T>,
    fromIndex: Int,
    toIndex: Int,
): List<T> {
    if (fromIndex !in items.indices || toIndex !in items.indices || fromIndex == toIndex) return items
    val mutable = items.toMutableList()
    val moving = mutable.removeAt(fromIndex)
    mutable.add(toIndex, moving)
    return mutable
}

fun <T> moveItemById(
    items: List<SortableItem<T>>,
    draggingId: String,
    targetId: String,
): List<SortableItem<T>> {
    val from = items.indexOfFirst { it.id == draggingId }
    val to = items.indexOfFirst { it.id == targetId }
    if (from < 0 || to < 0) return items
    return moveItem(items, from, to)
}
