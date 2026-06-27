package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the C4 renderer: renders without crashing, elements/labels appear. */
class C4DiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun c4_rendersElementsAndRelationships() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        C4Context
                            title Internet Banking System
                            Person(customer, "Banking Customer", "A customer of the bank")
                            System_Ext(email, "E-mail System", "Sends notifications")
                            Rel(customer, email, "Sends e-mails to", "SMTP")
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Internet Banking System").assertIsDisplayed()
        rule.onNodeWithText("Banking Customer").assertIsDisplayed()
        rule.onNodeWithText("E-mail System").assertIsDisplayed()
        rule.onNodeWithText("Sends e-mails to").assertIsDisplayed()
    }

    @Test
    fun c4_rendersEmptyWithoutCrashing() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(source = "C4Container")
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Empty C4 diagram").assertExists()
    }
}
