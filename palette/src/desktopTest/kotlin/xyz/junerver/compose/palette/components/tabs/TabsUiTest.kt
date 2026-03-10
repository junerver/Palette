package xyz.junerver.compose.palette.components.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class TabsUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        TabItem(key = "overview", label = "Overview"),
        TabItem(key = "billing", label = "Billing"),
        TabItem(key = "archive", label = "Archive", disabled = true),
    )

    @Test
    fun tabs_shouldRenderAllLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PTabs(
                    items = items,
                    selectedKey = "overview",
                )
            }
        }

        rule.onNodeWithText("Overview").assertTextEquals("Overview")
        rule.onNodeWithText("Billing").assertTextEquals("Billing")
        rule.onNodeWithText("Archive").assertTextEquals("Archive")
    }

    @Test
    fun tabs_shouldUpdateSelectedKeyWhenEnabledTabClicked() {
        var selected by mutableStateOf("overview")

        rule.setContent {
            PaletteMaterialTheme {
                PTabs(
                    items = items,
                    selectedKey = selected,
                    onTabChange = { selected = it },
                )
                Text("Selected tab: $selected")
            }
        }

        rule.onNodeWithText("Billing").performClick()
        rule.onNodeWithText("Selected tab: billing").assertTextEquals("Selected tab: billing")
    }

    @Test
    fun tabs_shouldIgnoreDisabledTabClick() {
        var selected by mutableStateOf("overview")

        rule.setContent {
            PaletteMaterialTheme {
                PTabs(
                    items = items,
                    selectedKey = selected,
                    onTabChange = { selected = it },
                )
                Text("Selected tab: $selected")
            }
        }

        rule.onNodeWithText("Archive").performClick()
        assertEquals("overview", selected)
    }
}
