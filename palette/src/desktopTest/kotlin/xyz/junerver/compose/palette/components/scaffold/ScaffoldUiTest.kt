package xyz.junerver.compose.palette.components.scaffold

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaffoldUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun scaffold_shouldRenderSlots() {
        rule.setContent {
            PaletteMaterialTheme {
                PScaffold(
                    topBar = { PText("Top") },
                    bottomBar = { PText("Bottom") },
                    floatingActionButton = { PText("Fab") },
                ) {
                    PText("Content")
                }
            }
        }

        rule.onNodeWithText("Top").assertTextEquals("Top")
        rule.onNodeWithText("Content").assertTextEquals("Content")
        rule.onNodeWithText("Bottom").assertTextEquals("Bottom")
        rule.onNodeWithText("Fab").assertTextEquals("Fab")
    }

    @Test
    fun scaffold_shouldKeepFabInteractive() {
        var clicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PScaffold(
                    floatingActionButton = {
                        PButton(text = "Create") {
                            clicks++
                        }
                    },
                ) {
                    PText("Content")
                }
            }
        }

        rule.onNodeWithText("Create").performClick()

        assertEquals(1, clicks)
    }
}
