package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PieDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pieDiagram_rendersTitleSlicesAndLegend() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        pie title Pets
                            "Dogs" : 386
                            "Cats" : 85
                            "Rats" : 15
                    """.trimIndent()
                )
            }
        }

        // Title renders.
        rule.onNodeWithText("Pets").assertIsDisplayed()
        // Legend labels render (each with a percentage).
        rule.onNodeWithText("Dogs", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Cats", substring = true).assertIsDisplayed()
    }

    @Test
    fun pieDiagram_rendersShowDataValues() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        pie showData
                            A : 1
                            B : 3
                    """.trimIndent()
                )
            }
        }

        // With showData, the raw value appears in the legend.
        rule.onNodeWithText("A : 1.0", substring = true).assertIsDisplayed()
    }
}
