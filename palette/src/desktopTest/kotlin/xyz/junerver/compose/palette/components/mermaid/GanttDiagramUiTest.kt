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

    @Test
    fun ganttDiagram_rendersDateAxisLabelsFromDeclaredDates() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        gantt
                            title Release
                            dateFormat YYYY-MM-DD
                            section Dev
                                Design   :des, 2024-01-01, 5d
                                Build    :after des, 10d
                    """.trimIndent()
                )
            }
        }

        // The timeline starts at the first declared date (2024-01-01). With no explicit
        // axisFormat the renderer falls back to ISO, so the first axis tick label is that
        // date — proving the chart now resolves real dates instead of a synthetic cursor.
        rule.onNodeWithText("2024-01-01").assertIsDisplayed()
    }

    @Test
    fun ganttDiagram_respectsExplicitAxisFormat() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        gantt
                            title Formatted
                            dateFormat YYYY-MM-DD
                            axisFormat %m/%d
                            section Dev
                                Design   :des, 2024-01-01, 5d
                    """.trimIndent()
                )
            }
        }

        // axisFormat %m/%d formats the first tick (2024-01-01) as "01/01".
        rule.onNodeWithText("01/01").assertIsDisplayed()
    }

    @Test
    fun ganttDiagram_chainsUndatedTasksAcrossSections() {
        // Reproduces the exact user scenario: `测试`/`修复` have NO start date and NO `after`
        // dep, yet must chain after the last task (编码, ending 2024-01-16) — NOT fall back to
        // 1970 or "today". The whole timeline must therefore stay within January 2024.
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        gantt
                            title 发版计划
                            dateFormat YYYY-MM-DD
                            section 开发
                                设计   :des, 2024-01-01, 5d
                                编码   :after des, 10d
                            section 测试
                                测试   :crit, active, 3d
                                修复   :2d
                    """.trimIndent()
                )
            }
        }

        // All four tasks render (设计/编码/测试/修复).
        rule.onNodeWithText("设计").assertIsDisplayed()
        rule.onNodeWithText("编码").assertIsDisplayed()
        rule.onNodeWithText("修复").assertIsDisplayed()
        // The 开发 section renders (测试 is both a section name and a task name, so check the
        // unambiguous section header).
        rule.onNodeWithText("开发").assertIsDisplayed()
        // The timeline starts at the first declared date and stays within Jan 2024 (a 1970
        // fallback would push the axis to year-1970 ticks, and a "today" fallback would move
        // the undated tasks out of the chart's date range).
        rule.onNodeWithText("2024-01-01").assertIsDisplayed()
    }
}
