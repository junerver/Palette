package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class GitGraphDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun gitGraph_rendersBranchesAndCommitLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        gitGraph
                           commit id: "A"
                           commit
                           branch develop
                           checkout develop
                           commit id: "B" tag: "v1"
                           checkout main
                           merge develop
                    """.trimIndent()
                )
            }
        }

        // Branch labels render.
        rule.onNodeWithText("main").assertIsDisplayed()
        rule.onNodeWithText("develop").assertIsDisplayed()
        // Commit id/tag labels render.
        rule.onNodeWithText("A").assertIsDisplayed()
        rule.onNodeWithText("v1").assertIsDisplayed()
    }
}
