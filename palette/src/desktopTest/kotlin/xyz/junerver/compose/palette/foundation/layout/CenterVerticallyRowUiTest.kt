package xyz.junerver.compose.palette.foundation.layout

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CenterVerticallyRowUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun centerVerticallyRow_shouldRenderChildren() {
        rule.setContent {
            PaletteMaterialTheme {
                CenterVerticallyRow {
                    Text("Left")
                    Text("Right")
                }
            }
        }

        rule.onNodeWithText("Left").assertTextEquals("Left")
        rule.onNodeWithText("Right").assertTextEquals("Right")
    }
}
