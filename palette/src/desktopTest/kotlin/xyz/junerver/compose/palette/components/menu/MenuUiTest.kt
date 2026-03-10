package xyz.junerver.compose.palette.components.menu

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class MenuUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        MenuItem(key = "overview", label = "Overview"),
        MenuItem(key = "billing", label = "Billing"),
        MenuItem(key = "archive", label = "Archive", disabled = true),
    )

    @Test
    fun menu_shouldRenderAllLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PMenu(items = items)
            }
        }

        rule.onNodeWithText("Overview").assertTextEquals("Overview")
        rule.onNodeWithText("Billing").assertTextEquals("Billing")
        rule.onNodeWithText("Archive").assertTextEquals("Archive")
    }

    @Test
    fun menu_shouldInvokeSelectForEnabledItem() {
        var selectedKey: String? = null

        rule.setContent {
            PaletteMaterialTheme {
                PMenu(
                    items = items,
                    onSelect = { selectedKey = it },
                )
            }
        }

        rule.onNodeWithText("Billing").performClick()
        assertEquals("billing", selectedKey)
    }
}
