package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SequenceDiagramUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun sequenceDiagram_rendersSelfMessageLabel() {
        // A self-message (Editor ->> Editor) must still render its label, even though
        // start and end participants are identical (a degenerate zero-length edge).
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        sequenceDiagram
                            participant User
                            participant Editor
                            User->>Editor: 输入
                            Editor->>Editor: 校验
                            Editor->>User: 完成
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("输入").assertIsDisplayed()
        rule.onNodeWithText("校验").assertIsDisplayed()
        rule.onNodeWithText("完成").assertIsDisplayed()
    }

    @Test
    fun sequenceDiagram_rendersDashedReplyAndNote() {
        rule.setContent {
            PaletteMaterialTheme {
                PMermaidDiagram(
                    source = """
                        sequenceDiagram
                            participant A
                            participant B
                            A->>B: 请求
                            B-->>A: 响应
                            Note over A,B: 全程
                    """.trimIndent()
                )
            }
        }

        rule.onNodeWithText("请求").assertIsDisplayed()
        rule.onNodeWithText("响应").assertIsDisplayed()
        rule.onNodeWithText("全程").assertIsDisplayed()
    }
}
