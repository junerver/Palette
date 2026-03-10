package xyz.junerver.compose.palette.components.sortable

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SortableUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun sortable_shouldRenderItemsInProvidedOrder() {
        val items = listOf(
            SortableItem(id = "a", payload = "Deploy"),
            SortableItem(id = "b", payload = "Verify"),
            SortableItem(id = "c", payload = "Notify"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PSortable(
                    items = items,
                    itemText = { it.payload },
                )
            }
        }

        rule.onNodeWithText("Deploy").assertTextEquals("Deploy")
        rule.onNodeWithText("Verify").assertTextEquals("Verify")
        rule.onNodeWithText("Notify").assertTextEquals("Notify")
    }
}
