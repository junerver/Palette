package xyz.junerver.compose.palette.components.treeselect

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TreeSelectUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun treeSelect_displaysPlaceholder() {
        val nodes = listOf(
            TreeSelectNode(
                value = "1",
                label = "Node 1",
                children = listOf(
                    TreeSelectNode(value = "1-1", label = "Node 1-1"),
                ),
            ),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PTreeSelect(
                    nodes = nodes,
                    value = null,
                    onValueChange = {},
                    placeholder = "Select...",
                )
            }
        }

        rule.onNodeWithText("Select...").assertIsDisplayed()
    }

    @Test
    fun treeSelect_withValueShowsSelectedLabel() {
        val nodes = listOf(
            TreeSelectNode(
                value = "1",
                label = "Node 1",
                children = listOf(
                    TreeSelectNode(value = "1-1", label = "Node 1-1"),
                ),
            ),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PTreeSelect(
                    nodes = nodes,
                    value = "1",
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("Node 1").assertIsDisplayed()
    }
}
