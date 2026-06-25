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

    @Test
    fun erDiagram_rendersAttributeTextWithoutFalseForeignKeyPrefix() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        erDiagram
                            USER {
                                string name
                                string email
                            }
                            ORDER {
                                int id
                                date created
                            }
                            PRODUCT {
                                string name
                                float price
                            }
                            USER ||--o{ ORDER : places
                            ORDER ||--|{ PRODUCT : contains
                    """.trimIndent()
                )
            }
        }

        // Plain attributes must render as "type name" — not "FK type name".
        rule.onNodeWithText("string email").assertIsDisplayed()
        rule.onNodeWithText("float price").assertIsDisplayed()
        // Relationship label is rendered.
        rule.onNodeWithText("places").assertIsDisplayed()
        rule.onNodeWithText("contains").assertIsDisplayed()
    }

    @Test
    fun erDiagram_rendersPrimaryKeyAndForeignKeyMarkers() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        erDiagram
                            CUSTOMER {
                                int id PK
                                string name
                                int orderId FK
                            }
                            ORDER {
                                int orderNo PK
                            }
                            CUSTOMER ||--o{ ORDER : places
                    """.trimIndent()
                )
            }
        }

        // PK / FK markers appear next to keyed attributes.
        rule.onNodeWithText("PK int id").assertIsDisplayed()
        rule.onNodeWithText("FK int orderId").assertIsDisplayed()
        // Non-keyed attribute has no marker prefix.
        rule.onNodeWithText("string name").assertIsDisplayed()
    }
}
