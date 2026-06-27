package xyz.junerver.compose.palette.components.mermaid


import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the Requirement renderer: renders without crashing, key text appears. */
class RequirementDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun requirement_rendersBoxesAndRelations() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        requirementDiagram
                            requirement test_req {
                                id: 1
                                text: the test text.
                                risk: high
                                verifymethod: test
                            }
                            functionalRequirement test_req2 {
                                text: the second text.
                            }
                            element test_entity {
                                type: simulation
                            }
                            test_entity - satisfies -> test_req2
                            test_req - contains -> test_req2
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Requirement").assertExists()
        rule.onNodeWithText("FunctionalRequirement").assertExists()
        rule.onNodeWithText("Element").assertExists()
        // The renderer prefixes fields: "Text: …", "Risk: …".
        rule.onNodeWithText("Text: the test text.", substring = true).assertExists()
        rule.onNodeWithText("satisfies").assertExists()
        rule.onNodeWithText("contains").assertExists()
    }
}
