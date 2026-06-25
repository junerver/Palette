package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class StateDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun stateDiagram_rendersStates() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        stateDiagram-v2
                            [*] --> Still
                            Still --> [*]
                            Still --> Moving
                            Moving --> Still
                            Moving --> Crash
                            Crash --> [*]
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("Still").assertIsDisplayed()
        rule.onNodeWithText("Moving").assertIsDisplayed()
        rule.onNodeWithText("Crash").assertIsDisplayed()
    }

    @Test
    fun stateDiagram_rendersStatesWithLabels() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        stateDiagram-v2
                            state "Idle" as idle
                            state "Processing" as proc
                            idle --> proc : start
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("Idle").assertIsDisplayed()
        rule.onNodeWithText("Processing").assertIsDisplayed()
    }

    @Test
    fun stateDiagram_rendersTransitionLabelsAndTerminalStates() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        stateDiagram-v2
                            [*] --> Idle
                            Idle --> Loading : fetch data
                            Loading --> Success : success
                            Loading --> Error : failed
                            Success --> Idle : reset
                            Error --> Idle : cancel
                            Success --> [*]
                    """.trimIndent()
                )
            }
        }

        // Regular states render.
        rule.onNodeWithText("Idle").assertIsDisplayed()
        rule.onNodeWithText("Loading").assertIsDisplayed()
        rule.onNodeWithText("Success").assertIsDisplayed()
        rule.onNodeWithText("Error").assertIsDisplayed()
        // Transition labels render.
        rule.onNodeWithText("fetch data").assertIsDisplayed()
        rule.onNodeWithText("reset").assertIsDisplayed()
    }
}
