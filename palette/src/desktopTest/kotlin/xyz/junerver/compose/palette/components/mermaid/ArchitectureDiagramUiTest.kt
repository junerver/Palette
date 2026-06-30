package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the Architecture renderer: renders without crashing, node titles appear. */
class ArchitectureDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun architecture_rendersNodesAndEdges() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        architecture-beta
                            group api(cloud)[API]
                            service db(database)[Database] in api
                            service server(server)[Server] in api
                            service cache(disk)[Cache] in api
                            junction jc
                            db:L -- R:server
                            cache:T -- B:server
                            server:R --> L:jc
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("API").assertExists()
        rule.onNodeWithText("Database").assertExists()
        rule.onNodeWithText("Server").assertExists()
        rule.onNodeWithText("Cache").assertExists()
        rule.onNodeWithText("jc").assertDoesNotExist()
    }
}
