package xyz.junerver.compose.palette.components.sortable

import kotlin.test.Test
import kotlin.test.assertEquals

class SortableLogicTest {
    @Test
    fun moveItem_whenValidIndices_shouldReorder() {
        val result =
            moveItem(
                items = listOf("a", "b", "c", "d"),
                fromIndex = 1,
                toIndex = 3,
            )

        assertEquals(listOf("a", "c", "d", "b"), result)
    }

    @Test
    fun moveItem_whenInvalidIndices_shouldKeepOriginal() {
        val items = listOf("a", "b", "c")
        assertEquals(items, moveItem(items, -1, 2))
        assertEquals(items, moveItem(items, 1, 99))
    }

    @Test
    fun moveItemById_shouldFindAndMove() {
        val items =
            listOf(
                SortableItem(id = "a", payload = 1),
                SortableItem(id = "b", payload = 2),
                SortableItem(id = "c", payload = 3),
            )

        val result = moveItemById(items, draggingId = "c", targetId = "a")

        assertEquals(listOf("c", "a", "b"), result.map { it.id })
    }
}
