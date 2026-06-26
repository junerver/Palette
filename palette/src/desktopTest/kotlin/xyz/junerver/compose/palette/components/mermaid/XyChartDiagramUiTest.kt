package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/**
 * UI smoke test for the XYChart renderer: parses (both keywords), lays out, renders without
 * crashing, and key text (title, categories, axis title) appears.
 */
class XyChartDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun xychart_rendersBarAndLineSeries() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        xychart-beta
                            title "Quarterly revenue"
                            x-axis [Q1, Q2, Q3, Q4]
                            y-axis Revenue 0 --> 500
                            bar [120, 180, 240, 310]
                            line [100, 150, 210, 290]
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Quarterly revenue").assertIsDisplayed()
        rule.onNodeWithText("Q1").assertIsDisplayed()
        rule.onNodeWithText("Q4").assertIsDisplayed()
        rule.onNodeWithText("Revenue").assertIsDisplayed()
    }

    @Test
    fun xychart_rendersModernKeyword() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        xychart
                            bar [4, 8, 15, 16, 23, 42]
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        // Renders without crashing; no empty-state text should appear.
        rule.onNodeWithText("Empty xy chart").assertDoesNotExist()
    }

    @Test
    fun xychart_rendersEmptyWithoutCrashing() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(source = "xychart")
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Empty xy chart").assertExists()
    }
}
