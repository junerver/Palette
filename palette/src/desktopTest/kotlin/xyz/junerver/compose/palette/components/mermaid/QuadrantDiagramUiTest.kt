package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/**
 * UI smoke test for the Quadrant chart renderer: parses, lays out, renders without crashing,
 * and key text (title, axis labels, quadrant labels, point labels) appears.
 */
class QuadrantDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun quadrant_rendersPointsAndLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        quadrantChart
                            title Reach and engagement
                            x-axis Low Reach --> High Reach
                            y-axis Low Engagement --> High Engagement
                            quadrant-1 Expand
                            quadrant-2 Promote
                            quadrant-3 Re-evaluate
                            quadrant-4 Improve
                            Campaign A: [0.3, 0.6]
                            Campaign B: [0.7, 0.8]
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Reach and engagement").assertIsDisplayed()
        rule.onNodeWithText("Low Reach").assertIsDisplayed()
        rule.onNodeWithText("High Reach").assertIsDisplayed()
        rule.onNodeWithText("Expand").assertIsDisplayed()
        rule.onNodeWithText("Campaign A").assertIsDisplayed()
        rule.onNodeWithText("Campaign B").assertIsDisplayed()
    }

    @Test
    fun quadrant_rendersEmptyWithoutCrashing() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(source = "quadrantChart")
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Empty quadrant chart").assertExists()
    }
}
