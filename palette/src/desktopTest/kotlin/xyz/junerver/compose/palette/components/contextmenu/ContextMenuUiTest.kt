package xyz.junerver.compose.palette.components.contextmenu

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ContextMenuUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun contextMenu_rendersTriggerContent() {
        rule.setContent {
            PaletteMaterialTheme {
                val state = rememberContextMenuState()
                PContextMenu(
                    state = state,
                    onItemClick = {},
                )
                Text("Right click me")
            }
        }

        rule.onNodeWithText("Right click me").assertIsDisplayed()
    }
}
