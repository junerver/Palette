package xyz.junerver.compose.palette.components.container

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class ContainerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun container_shouldRenderProvidedContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PContainer {
                    PText("Container content")
                }
            }
        }

        rule.onNodeWithText("Container content").assertTextEquals("Container content")
    }

    @Test
    fun container_shouldInvokeClickHandlerWhenClickable() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PContainer(onClick = { clicks++ }) {
                    PText("Open")
                }
            }
        }

        rule.onNodeWithText("Open").performClick()

        assertEquals(1, clicks)
    }
}
