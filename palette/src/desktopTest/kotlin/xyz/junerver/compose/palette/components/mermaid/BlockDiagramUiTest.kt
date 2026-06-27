package xyz.junerver.compose.palette.components.mermaid


import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the Block renderer: renders without crashing, nodes/labels appear. */
class BlockDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun block_rendersNodesAndEdges() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        block-beta
                            columns 3
                            a:3
                            b["Label B"]
                            c
                            space
                            d
                            a --> b
                            c-- "edge" -->d
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("a").assertExists()
        rule.onNodeWithText("Label B").assertExists()
        rule.onNodeWithText("c").assertExists()
        rule.onNodeWithText("d").assertExists()
        rule.onNodeWithText("edge").assertExists()
    }

    @Test
    fun block_rendersEmptyWithoutCrashing() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(source = "block")
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Empty block diagram").assertExists()
    }
}
