package xyz.junerver.compose.palette.components.popover

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PopoverUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun popover_shouldShowContentAfterTriggerClick() {
        rule.setContent {
            PaletteMaterialTheme {
                PPopover(
                    trigger = { Text("Open") },
                    content = { Text("Popover content") },
                )
            }
        }

        rule.onNodeWithText("Open").performClick()
        rule.onNodeWithText("Popover content").assertTextEquals("Popover content")
    }

    @Test
    fun popover_shouldRespectControlledVisibleParam() {
        rule.setContent {
            PaletteMaterialTheme {
                PPopover(
                    visible = true,
                    trigger = { Text("Open") },
                    content = { Text("Always visible") },
                )
            }
        }

        rule.onNodeWithText("Always visible").assertTextEquals("Always visible")
    }
}

