package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ClassDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun classDiagram_rendersClassesWithMembers() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        classDiagram
                            class Animal {
                                +String name
                                +int age
                                +isMammal() bool
                            }
                            class Dog {
                                +bark() void
                            }
                            Animal <|-- Dog
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("Animal").assertIsDisplayed()
        rule.onNodeWithText("Dog").assertIsDisplayed()
    }

    @Test
    fun classDiagram_rendersAnnotations() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        classDiagram
                            class IShape {
                                <<interface>>
                                +area() double
                            }
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("IShape").assertIsDisplayed()
        rule.onNodeWithText("<<interface>>").assertIsDisplayed()
    }
}
