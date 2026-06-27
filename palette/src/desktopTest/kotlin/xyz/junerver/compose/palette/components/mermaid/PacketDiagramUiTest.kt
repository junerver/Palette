package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

/** UI smoke test for the Packet renderer: renders without crashing, field labels appear. */
class PacketDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun packet_rendersFields() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        packet
                        title UDP Header
                        0-15: "Source Port"
                        16-31: "Destination Port"
                        +16: "Length"
                        +16: "Checksum"
                    """.trimIndent(),
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("UDP Header").assertExists()
        rule.onNodeWithText("Source Port").assertExists()
        rule.onNodeWithText("Destination Port").assertExists()
        rule.onNodeWithText("Checksum").assertExists()
    }
}
