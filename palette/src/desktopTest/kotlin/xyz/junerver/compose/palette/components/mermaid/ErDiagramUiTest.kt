package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ErDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun erDiagram_rendersEntities() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        erDiagram
                            CUSTOMER {
                                string name
                            }
                            ORDER {
                                int orderNumber
                            }
                            CUSTOMER ||--o{ ORDER : places
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("CUSTOMER").assertIsDisplayed()
        rule.onNodeWithText("ORDER").assertIsDisplayed()
    }
}
