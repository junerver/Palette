package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class FlowchartDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun flowchart_rendersNodesAndBranchLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        flowchart TD
                            A[Start] --> B{Check}
                            B -->|yes| C[Process A]
                            B -->|no| D[Process B]
                            C --> E[End]
                            D --> E
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("Start").assertIsDisplayed()
        rule.onNodeWithText("Check").assertIsDisplayed()
        rule.onNodeWithText("Process A").assertIsDisplayed()
        rule.onNodeWithText("yes").assertIsDisplayed()
        rule.onNodeWithText("no").assertIsDisplayed()
    }

    @Test
    fun flowchart_rendersCyclesAndMultipleEdgesWithoutCrash() {
        // Cyclic + multi-edge graph: must not collapse or crash the renderer.
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        flowchart LR
                            A --> B
                            B --> C
                            C --> A
                            A --> C
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("A").assertIsDisplayed()
        rule.onNodeWithText("B").assertIsDisplayed()
        rule.onNodeWithText("C").assertIsDisplayed()
    }
}
