package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/**
 * UI smoke test for the Timeline renderer: the diagram parses, lays out, and renders without
 * crashing, and key text (period time, event, section) appears in the composition tree.
 */
class TimelineDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun timeline_rendersSectionsAndEvents() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        timeline
                            title Project History
                            section Q1
                                2020 : Kickoff
                                2020 : Prototype
                            section Q2
                                2021 : Launch : Scale up
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Project History").assertIsDisplayed()
        rule.onNodeWithText("Kickoff").assertIsDisplayed()
        rule.onNodeWithText("Scale up").assertIsDisplayed()
        rule.onNodeWithText("Q1").assertIsDisplayed()
        rule.onNodeWithText("Q2").assertIsDisplayed()
    }

    @Test
    fun timeline_rendersEmptyWithoutCrashing() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(source = "timeline")
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Empty timeline").assertExists()
    }
}
