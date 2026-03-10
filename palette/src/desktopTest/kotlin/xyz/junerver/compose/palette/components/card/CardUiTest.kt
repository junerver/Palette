package xyz.junerver.compose.palette.components.card

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class CardUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun card_shouldRenderProvidedContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PCard {
                    Text("Revenue")
                    Text("$12,400")
                }
            }
        }

        rule.onNodeWithText("Revenue").assertTextEquals("Revenue", "$12,400")
    }

    @Test
    fun card_shouldInvokeClickHandlerWhenClickable() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PCard(onClick = { clicks++ }) {
                    Text("Open details")
                }
            }
        }

        rule.onNodeWithText("Open details").performClick()
        assertEquals(1, clicks)
    }
}
