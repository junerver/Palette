package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the Sankey renderer: renders without crashing, node labels appear. */
class SankeyDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun sankey_rendersFlows() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        sankey
                        Electricity grid,Over generation,104.453
                        Electricity grid,H2 conversion,27.14
                        Bio-conversion,Liquid,0.597
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Electricity grid").assertExists()
        rule.onNodeWithText("Over generation").assertExists()
        rule.onNodeWithText("Bio-conversion").assertExists()
    }
}
