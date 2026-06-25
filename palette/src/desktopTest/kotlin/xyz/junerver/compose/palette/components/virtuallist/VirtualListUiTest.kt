package xyz.junerver.compose.palette.components.virtuallist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class VirtualListUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun virtualList_displaysItems() {
        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

        rule.setContent {
            PaletteMaterialTheme {
                PVirtualList(
                    items = items,
                    itemText = { it },
                )
            }
        }

        rule.onNodeWithText("Item 1").assertIsDisplayed()
        rule.onNodeWithText("Item 2").assertIsDisplayed()
        rule.onNodeWithText("Item 3").assertIsDisplayed()
    }

    @Test
    fun virtualList_withCustomKeyDisplaysItems() {
        data class User(val id: Int, val name: String)

        val items = listOf(
            User(1, "Alice"),
            User(2, "Bob"),
            User(3, "Charlie"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PVirtualList(
                    items = items,
                    itemText = { it.name },
                    key = { it.id },
                )
            }
        }

        rule.onNodeWithText("Alice").assertIsDisplayed()
        rule.onNodeWithText("Bob").assertIsDisplayed()
        rule.onNodeWithText("Charlie").assertIsDisplayed()
    }
}
