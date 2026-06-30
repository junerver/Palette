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
                        Agricultural waste,Bio-conversion,124.729
                        Bio-conversion,Liquid,0.597
                        Bio-conversion,Losses,26.862
                        Bio-conversion,Solid,280.322
                        Biofuel imports,Liquid,35
                        Coal imports,Coal,11.606
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Biofuel imports 35").assertExists()
        rule.onNodeWithText("Agricultural waste 124.73").assertExists()
        rule.onNodeWithText("Bio-conversion 307.78").assertExists()
        rule.onNodeWithText("Coal 11.61").assertExists()
    }
}
