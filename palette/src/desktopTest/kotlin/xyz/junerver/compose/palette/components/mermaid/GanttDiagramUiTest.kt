package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class GanttDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun ganttDiagram_rendersTitleSectionAndTasks() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        gantt
                            title Project Plan
                            dateFormat YYYY-MM-DD
                            section Dev
                                Design   :a1, 2024-01-01, 5d
                                Build    :after a1, 10d
                            section QA
                                Test     :3d
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("Project Plan").assertIsDisplayed()
        rule.onNodeWithText("Dev").assertIsDisplayed()
        rule.onNodeWithText("Design").assertIsDisplayed()
        rule.onNodeWithText("Test").assertIsDisplayed()
    }
}
