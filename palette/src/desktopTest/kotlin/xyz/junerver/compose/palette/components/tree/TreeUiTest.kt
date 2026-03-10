package xyz.junerver.compose.palette.components.tree

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TreeUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val nodes =
        listOf(
            TreeNode(
                key = "docs",
                data = "Docs",
                children =
                    listOf(
                        TreeNode(key = "api", data = "API"),
                        TreeNode(key = "guide", data = "Guide"),
                    ),
            ),
            TreeNode(key = "blog", data = "Blog"),
        )

    @Test
    fun tree_shouldExpandChildrenWhenExpandIconClicked() {
        rule.setContent {
            var expandedKeys by mutableStateOf(emptySet<String>())

            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PTree(
                    nodes = nodes,
                    expandedKeys = expandedKeys,
                    onExpandChange = { expandedKeys = it },
                ) { node ->
                    Text(node.data)
                }
            }
        }

        rule.onAllNodesWithText("API").assertCountEquals(0)
        rule.onNodeWithContentDescription("Expand").performClick()

        rule.onNodeWithText("API").assertTextEquals("API")
        rule.onNodeWithText("Guide").assertTextEquals("Guide")
        rule.onNodeWithContentDescription("Collapse")
    }

    @Test
    fun tree_shouldUpdateSelectedNodeWhenItemClicked() {
        rule.setContent {
            var selectedKey by mutableStateOf<String?>(null)

            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PTree(
                    nodes = nodes,
                    expandedKeys = setOf("docs"),
                    selectedKey = selectedKey,
                    onSelect = { selectedKey = it },
                ) { node ->
                    Text(node.data)
                }
                Text("Selected: ${selectedKey ?: "none"}")
            }
        }

        rule.onNodeWithText("Selected: none").assertTextEquals("Selected: none")
        rule.onNodeWithText("Guide").performClick()
        rule.onNodeWithText("Selected: guide").assertTextEquals("Selected: guide")
    }

    @Test
    fun tree_shouldCollapseChildrenWhenCollapseIconClicked() {
        rule.setContent {
            var expandedKeys by mutableStateOf(setOf("docs"))

            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PTree(
                    nodes = nodes,
                    expandedKeys = expandedKeys,
                    onExpandChange = { expandedKeys = it },
                ) { node ->
                    Text(node.data)
                }
            }
        }

        rule.onNodeWithText("API").assertTextEquals("API")
        rule.onNodeWithContentDescription("Collapse").performClick()
        rule.onAllNodesWithText("API").assertCountEquals(0)
        rule.onAllNodesWithText("Guide").assertCountEquals(0)
    }

    @Test
    fun tree_leafNodeShouldNotRenderExpandCollapseIcon() {
        rule.setContent {
            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PTree(
                    nodes = listOf(TreeNode(key = "leaf", data = "Leaf")),
                ) { node ->
                    Text(node.data)
                }
            }
        }

        rule.onNodeWithText("Leaf").assertTextEquals("Leaf")
        rule.onAllNodesWithContentDescription("Expand").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Collapse").assertCountEquals(0)
    }
}
