package xyz.junerver.compose.palette.components.transfer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TransferUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun transfer_displaysSourceAndTargetLists() {
        val dataSource = listOf(
            TransferItem(key = "1", title = "Item 1"),
            TransferItem(key = "2", title = "Item 2"),
            TransferItem(key = "3", title = "Item 3"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PTransfer(
                    dataSource = dataSource,
                    targetKeys = listOf("1"),
                    onTargetKeysChange = {},
                )
            }
        }

        rule.onNodeWithText("源列表").assertIsDisplayed()
        rule.onNodeWithText("目标列表").assertIsDisplayed()
    }

    @Test
    fun transfer_withCustomTitlesShowsTitles() {
        val dataSource = listOf(
            TransferItem(key = "1", title = "Item 1"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PTransfer(
                    dataSource = dataSource,
                    targetKeys = emptyList(),
                    onTargetKeysChange = {},
                    titles = Pair("Available", "Selected"),
                )
            }
        }

        rule.onNodeWithText("Available").assertIsDisplayed()
        rule.onNodeWithText("Selected").assertIsDisplayed()
    }
}
