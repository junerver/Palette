package xyz.junerver.compose.palette.components.commandpalette

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandPaletteUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val commands =
        listOf(
            CommandAction(
                id = "open-billing",
                title = "Open Billing",
                subtitle = "Finance workspace",
                keywords = listOf("invoice", "payment"),
            ),
            CommandAction(
                id = "open-logs",
                title = "Open Logs",
                subtitle = "Diagnostics",
            ),
        )

    @Test
    fun commandPalette_shouldRenderFilteredResultAndInvokeClickCallback() {
        var selectedId: String? = null

        rule.setContent {
            PaletteMaterialTheme {
                PCommandPalette(
                    commands = commands,
                    query = "invoice",
                    highlightedIndex = 0,
                    onCommandClick = { selectedId = it.id },
                )
            }
        }

        rule.onNodeWithText("Open Billing").assertTextEquals("Open Billing", "Finance workspace")
        rule.onNodeWithText("Open Billing").performClick()

        assertEquals("open-billing", selectedId)
    }

    @Test
    fun commandPalette_shouldHideCommandsWhenQueryHasNoMatch() {
        rule.setContent {
            PaletteMaterialTheme {
                PCommandPalette(
                    commands = commands,
                    query = "missing",
                    highlightedIndex = -1,
                )
            }
        }

        rule.onAllNodesWithText("Open Billing").assertCountEquals(0)
        rule.onAllNodesWithText("Open Logs").assertCountEquals(0)
    }
}
