package xyz.junerver.compose.palette.components.tag

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TagUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Composable
    private fun EditableTagGroupHost() {
        var tags by mutableStateOf(listOf("Tag 1", "Tag 2"))

        Column {
            PEditableTagGroup(
                tags = tags,
                onTagsChange = { tags = it },
                placeholder = "Add tag...",
            )
            Text("Tags: ${tags.joinToString()}")
            Text("Count: ${tags.size}")
        }
    }

    @Test
    fun tag_shouldRenderTextAndInvokeClose() {
        var closed by mutableStateOf(false)

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PTag(
                        text = "Closable",
                        closable = true,
                        onClose = { closed = true },
                    )
                    Text(if (closed) "Closed" else "Open")
                }
            }
        }

        rule.onNodeWithText("Closable").assertTextEquals("Closable")
        rule.onNodeWithContentDescription("Close").performClick()
        rule.onNodeWithText("Closed").assertTextEquals("Closed")
    }

    @Test
    fun tag_shouldInvokeClickCallback() {
        var clicked by mutableStateOf(false)

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PTag(
                        text = "Clickable",
                        onClick = { clicked = true },
                    )
                    Text(if (clicked) "Clicked" else "Idle")
                }
            }
        }

        rule.onNodeWithText("Clickable").performClick()
        rule.onNodeWithText("Clicked").assertTextEquals("Clicked")
    }

    @Test
    fun editableTagGroup_shouldRenderExistingTagsAndPlaceholder() {
        rule.setContent {
            PaletteMaterialTheme {
                EditableTagGroupHost()
            }
        }

        rule.onNodeWithText("Tag 1").assertTextEquals("Tag 1")
        rule.onNodeWithText("Tag 2").assertTextEquals("Tag 2")
        rule.onNodeWithContentDescription("Add tag")
        rule.onNodeWithText("Count: 2").assertTextEquals("Count: 2")
    }

    @Test
    fun editableTagGroup_shouldRenderCloseActionsForExistingTags() {
        rule.setContent {
            PaletteMaterialTheme {
                EditableTagGroupHost()
            }
        }

        rule.onAllNodesWithContentDescription("Close").assertCountEquals(2)
    }

    @Test
    fun editableTagGroup_shouldEnterEditingModeAndShowConfirmAction() {
        rule.setContent {
            PaletteMaterialTheme {
                EditableTagGroupHost()
            }
        }

        rule.onNodeWithContentDescription("Add tag").performClick()
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(1)
        rule.onNodeWithContentDescription("Confirm")
    }

    @Test
    fun editableTagGroup_shouldCancelEditingWithoutAddingTag() {
        rule.setContent {
            PaletteMaterialTheme {
                EditableTagGroupHost()
            }
        }

        rule.onNodeWithContentDescription("Add tag").performClick()
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("Temp")
        rule.onNodeWithContentDescription("Cancel").performClick()

        rule.onAllNodesWithText("Temp").assertCountEquals(0)
        rule.onNodeWithContentDescription("Add tag")
        rule.onNodeWithText("Count: 2").assertTextEquals("Count: 2")
    }

    @Test
    fun editableTagGroup_shouldHideAddButtonWhenMaxTagsReached() {
        rule.setContent {
            PaletteMaterialTheme {
                PEditableTagGroup(
                    tags = listOf("Tag 1", "Tag 2"),
                    onTagsChange = {},
                    maxTags = 2,
                )
            }
        }

        rule.onAllNodesWithContentDescription("Add tag").assertCountEquals(0)
    }
}
