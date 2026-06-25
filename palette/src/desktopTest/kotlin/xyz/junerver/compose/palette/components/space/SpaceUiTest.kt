package xyz.junerver.compose.palette.components.space

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SpaceUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun space_horizontalDirection_showsContentInRow() {
        rule.setContent {
            PaletteMaterialTheme {
                PSpace(direction = SpaceDirection.Horizontal) {
                    Text("First")
                    Text("Second")
                }
            }
        }

        rule.onNodeWithText("First").assertIsDisplayed()
        rule.onNodeWithText("Second").assertIsDisplayed()
    }

    @Test
    fun space_verticalDirection_showsContentInColumn() {
        rule.setContent {
            PaletteMaterialTheme {
                PSpace(direction = SpaceDirection.Vertical) {
                    Text("Top")
                    Text("Bottom")
                }
            }
        }

        rule.onNodeWithText("Top").assertIsDisplayed()
        rule.onNodeWithText("Bottom").assertIsDisplayed()
    }

    @Test
    fun space_wrapEnabled_showsContentInFlowRow() {
        rule.setContent {
            PaletteMaterialTheme {
                PSpace(wrap = true) {
                    Text("Item1")
                    Text("Item2")
                }
            }
        }

        rule.onNodeWithText("Item1").assertIsDisplayed()
        rule.onNodeWithText("Item2").assertIsDisplayed()
    }
}
