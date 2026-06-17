package xyz.junerver.compose.palette.components.collapse

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CollapseUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        CollapseItemData(key = "profile", title = "Profile") { Text("Profile details") },
        CollapseItemData(key = "security", title = "Security") { Text("Security settings") },
    )

    @Test
    fun collapse_shouldToggleContentWhenTitleClicked() {
        rule.setContent {
            var expandedKeys by mutableStateOf(emptySet<String>())

            PaletteMaterialTheme {
                PCollapse(
                    items = items,
                    expandedKeys = expandedKeys,
                    onExpandChange = { expandedKeys = it },
                )
            }
        }

        rule.onNodeWithText("Profile").performClick()
        rule.onNodeWithText("Profile details").assertTextEquals("Profile details")

        rule.onNodeWithText("Profile").performClick()
        rule.onAllNodesWithText("Profile details").assertCountEquals(0)
    }

    @Test
    fun collapse_shouldKeepOnlyLatestItemExpandedInAccordionMode() {
        rule.setContent {
            var expandedKeys by mutableStateOf(setOf("profile"))

            PaletteMaterialTheme {
                PCollapse(
                    items = items,
                    accordion = true,
                    expandedKeys = expandedKeys,
                    onExpandChange = { expandedKeys = it },
                )
            }
        }

        rule.onNodeWithText("Profile details").assertTextEquals("Profile details")
        rule.onNodeWithText("Security").performClick()

        rule.onNodeWithText("Security settings").assertTextEquals("Security settings")
        rule.onAllNodesWithText("Profile details").assertCountEquals(0)
    }

    @Test
    fun collapse_shouldToggleWithInternalStateWhenExpandedKeysOmitted() {
        rule.setContent {
            PaletteMaterialTheme {
                PCollapse(items = items)
            }
        }

        rule.onAllNodesWithText("Profile details").assertCountEquals(0)

        rule.onNodeWithText("Profile").performClick()
        rule.onNodeWithText("Profile details").assertTextEquals("Profile details")

        rule.onNodeWithText("Profile").performClick()
        rule.onAllNodesWithText("Profile details").assertCountEquals(0)
    }

    @Test
    fun collapse_shouldUseDefaultExpandedKeysInUncontrolledMode() {
        rule.setContent {
            PaletteMaterialTheme {
                PCollapse(
                    items = items,
                    defaultExpandedKeys = setOf("security"),
                )
            }
        }

        rule.onNodeWithText("Security settings").assertTextEquals("Security settings")
        rule.onAllNodesWithText("Profile details").assertCountEquals(0)
    }

    @Test
    fun collapse_shouldFollowExternallyControlledExpandedKeys() {
        var expandedKeys by mutableStateOf(emptySet<String>())

        rule.setContent {
            PaletteMaterialTheme {
                PCollapse(
                    items = items,
                    expandedKeys = expandedKeys,
                    onExpandChange = { expandedKeys = it },
                )
            }
        }

        rule.onAllNodesWithText("Security settings").assertCountEquals(0)

        rule.runOnIdle {
            expandedKeys = setOf("security")
        }

        rule.onNodeWithText("Security settings").assertTextEquals("Security settings")
        rule.onAllNodesWithText("Profile details").assertCountEquals(0)
    }

    @Test
    fun collapse_shouldRenderExternallyUpdatedContentAndAddedItems() {
        var expandedKeys by mutableStateOf(setOf("dynamic"))
        var dynamicItems by mutableStateOf(
            listOf(
                CollapseItemData(key = "dynamic", title = "Dynamic") { Text("Content v1") },
            ),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PCollapse(
                    items = dynamicItems,
                    expandedKeys = expandedKeys,
                    onExpandChange = { expandedKeys = it },
                )
            }
        }

        rule.onNodeWithText("Content v1").assertTextEquals("Content v1")

        rule.runOnIdle {
            dynamicItems =
                listOf(
                    CollapseItemData(key = "dynamic", title = "Dynamic") { Text("Content v2") },
                    CollapseItemData(key = "added", title = "Added") { Text("Added content") },
                )
            expandedKeys = setOf("dynamic", "added")
        }

        rule.onNodeWithText("Content v2").assertTextEquals("Content v2")
        rule.onNodeWithText("Added").assertTextEquals("Added")
        rule.onNodeWithText("Added content").assertTextEquals("Added content")
        rule.onAllNodesWithText("Content v1").assertCountEquals(0)
    }
}
