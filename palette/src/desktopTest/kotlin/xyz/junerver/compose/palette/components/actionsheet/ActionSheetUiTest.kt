package xyz.junerver.compose.palette.components.actionsheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class ActionSheetUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun actionSheet_visibleShowsOptions() {
        val options = listOf(
            ActionSheetItem(label = "Option 1"),
            ActionSheetItem(label = "Option 2"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PActionSheet(
                    visible = true,
                    options = options,
                    onDismiss = {},
                    onItemClick = {},
                )
            }
        }

        rule.onNodeWithText("Option 1").assertIsDisplayed()
        rule.onNodeWithText("Option 2").assertIsDisplayed()
    }

    @Test
    fun actionSheet_visibleWithTitleShowsTitle() {
        val options = listOf(
            ActionSheetItem(label = "Option 1"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PActionSheet(
                    visible = true,
                    options = options,
                    onDismiss = {},
                    onItemClick = {},
                    title = "Choose an option",
                )
            }
        }

        rule.onNodeWithText("Choose an option").assertIsDisplayed()
    }

    @Test
    fun actionSheet_clickItemCallsOnItemClick() {
        val options = listOf(
            ActionSheetItem(label = "Option 1"),
            ActionSheetItem(label = "Option 2"),
        )
        var clickedIndex = -1

        rule.setContent {
            PaletteMaterialTheme {
                PActionSheet(
                    visible = true,
                    options = options,
                    onDismiss = {},
                    onItemClick = { clickedIndex = it },
                )
            }
        }

        rule.onNodeWithText("Option 2").performClick()
        assertEquals(1, clickedIndex)
    }
}
