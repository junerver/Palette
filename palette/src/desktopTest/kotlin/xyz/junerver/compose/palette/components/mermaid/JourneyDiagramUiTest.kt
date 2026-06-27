package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the Journey renderer: renders without crashing, key text appears. */
class JourneyDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun journey_rendersSectionsAndTasks() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        journey
                            title My working day
                            section Go to work
                              Make tea: 5: Me
                              Do work: 1: Me, Cat
                            section Go home
                              Sit down: 5: Me
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("My working day").assertExists()
        rule.onNodeWithText("Go to work").assertExists()
        rule.onNodeWithText("Make tea").assertExists()
        rule.onNodeWithText("Sit down").assertExists()
    }
}
