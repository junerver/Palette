package xyz.junerver.compose.palette.foundation.border

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class BorderContainerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun borderContainer_shouldRenderProvidedContent() {
        rule.setContent {
            PaletteMaterialTheme {
                BorderContainer {
                    Text("Border Content")
                }
            }
        }

        rule.onNodeWithText("Border Content").assertTextEquals("Border Content")
    }
}
