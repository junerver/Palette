package xyz.junerver.compose.palette.components.drawer

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class DrawerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun drawer_shouldRenderVisibleContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PDrawer(
                    visible = true,
                    onClose = {},
                ) {
                    Text("Drawer Content")
                }
            }
        }

        rule.onNodeWithText("Drawer Content").assertTextEquals("Drawer Content")
    }

    @Test
    fun drawer_shouldNotRenderContentWhenHidden() {
        rule.setContent {
            PaletteMaterialTheme {
                PDrawer(
                    visible = false,
                    onClose = {},
                ) {
                    Text("Hidden Drawer")
                }
            }
        }

        rule.onAllNodesWithText("Hidden Drawer").assertCountEquals(0)
    }

    @Test
    fun drawer_shouldRenderVisibleContentFromStartPlacement() {
        rule.setContent {
            PaletteMaterialTheme {
                PDrawer(
                    visible = true,
                    placement = DrawerPlacement.Start,
                    onClose = {},
                ) {
                    Text("Start Drawer Content")
                }
            }
        }

        rule.onNodeWithText("Start Drawer Content").assertTextEquals("Start Drawer Content")
    }
}
