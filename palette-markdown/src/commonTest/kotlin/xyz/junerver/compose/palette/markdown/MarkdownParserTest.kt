package xyz.junerver.compose.palette.markdown

import xyz.junerver.compose.palette.code.CodeTokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MarkdownParserTest {
    @Test
    fun parsesHeadingsParagraphsListsCodeAndMermaidBlocks() {
        val document =
            MarkdownParser.parse(
                """
                # Palette Markdown

                Compose markdown renderer.

                - viewer
                - editor

                ```kotlin
                fun main() = println("Palette")
                ```

                ```mermaid
                flowchart TD
                    A[Markdown] --> B[Viewer]
                ```
                """.trimIndent(),
            )

        assertEquals(5, document.blocks.size)
        assertEquals(MarkdownHeading(level = 1, text = "Palette Markdown"), document.blocks[0])
        assertEquals(MarkdownParagraph("Compose markdown renderer."), document.blocks[1])
        assertEquals(MarkdownListBlock(items = listOf("viewer", "editor"), ordered = false), document.blocks[2])
        val code = assertIs<MarkdownCodeBlock>(document.blocks[3])
        assertEquals("kotlin", code.language)
        assertTrue(code.content.contains("fun main()"))
        val mermaid = assertIs<MarkdownMermaidBlock>(document.blocks[4])
        assertTrue(mermaid.source.contains("flowchart TD"))
    }

    @Test
    fun renderModelHighlightsCodeAndParsesMermaidBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```kotlin
                    val answer = 42
                    ```

                    ```mermaid
                    flowchart LR
                        A --> B
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks[0])
        assertTrue(code.highlighted.tokens.flatten().any { it.text == "val" && it.type == CodeTokenType.Keyword })
        val mermaid = assertIs<MarkdownRenderBlock.Mermaid>(model.blocks[1])
        assertEquals("A", mermaid.diagram.edges.single().from)
        assertEquals("B", mermaid.diagram.edges.single().to)
    }

    @Test
    fun parsesInlineStrongEmphasisCodeAndLinks() {
        val inline =
            MarkdownInlineParser.parse(
                "Use **Palette** with *Compose*, `PMarkdownViewer`, and [docs](https://example.com).",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Use "),
                MarkdownInlineStrong("Palette"),
                MarkdownInlineText(" with "),
                MarkdownInlineEmphasis("Compose"),
                MarkdownInlineText(", "),
                MarkdownInlineCode("PMarkdownViewer"),
                MarkdownInlineText(", and "),
                MarkdownInlineLink(label = "docs", destination = "https://example.com"),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun renderModelKeepsInlineNodesForParagraphsHeadingsAndLists() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ## **Viewer**

                    Render `code` and [links](https://example.com).

                    1. *first*
                    2. **second**
                    """.trimIndent(),
                ),
            )

        val heading = assertIs<MarkdownRenderBlock.Heading>(model.blocks[0])
        assertEquals(listOf(MarkdownInlineStrong("Viewer")), heading.inlines)
        val paragraph = assertIs<MarkdownRenderBlock.Paragraph>(model.blocks[1])
        assertTrue(paragraph.inlines.any { it is MarkdownInlineCode && it.text == "code" })
        assertTrue(paragraph.inlines.any { it is MarkdownInlineLink && it.destination == "https://example.com" })
        val list = assertIs<MarkdownRenderBlock.ListBlock>(model.blocks[2])
        assertEquals(listOf(MarkdownInlineEmphasis("first")), list.itemInlines.first())
        assertEquals(listOf(MarkdownInlineStrong("second")), list.itemInlines.last())
    }

    @Test
    fun parsesBlockquotesTaskListsTablesAndImages() {
        val document =
            MarkdownParser.parse(
                """
                > **Note:** Markdown supports rich content.
                > Keep related lines together.

                - [x] Parse markdown
                - [ ] Render preview

                | Component | Status |
                | --- | --- |
                | Viewer | Ready |
                | Editor | Draft |

                ![Palette logo](https://example.com/palette.png)
                """.trimIndent(),
            )

        assertEquals(4, document.blocks.size)
        val quote = assertIs<MarkdownBlockQuote>(document.blocks[0])
        assertEquals("**Note:** Markdown supports rich content. Keep related lines together.", quote.text)
        assertTrue(quote.inlines.any { it is MarkdownInlineStrong && it.text == "Note:" })

        val tasks = assertIs<MarkdownTaskListBlock>(document.blocks[1])
        assertEquals(
            listOf(
                MarkdownTaskItem(text = "Parse markdown", checked = true),
                MarkdownTaskItem(text = "Render preview", checked = false),
            ),
            tasks.items,
        )

        val table = assertIs<MarkdownTableBlock>(document.blocks[2])
        assertEquals(listOf("Component", "Status"), table.headers)
        assertEquals(listOf(listOf("Viewer", "Ready"), listOf("Editor", "Draft")), table.rows)

        val image = assertIs<MarkdownParagraph>(document.blocks[3])
        assertEquals(
            listOf(MarkdownInlineImage(alt = "Palette logo", destination = "https://example.com/palette.png")),
            image.inlines,
        )
    }

    @Test
    fun renderModelKeepsRichBlockData() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    > Quote with `code`

                    - [x] **done**

                    | Name | Value |
                    | --- | --- |
                    | alpha | 1 |
                    """.trimIndent(),
                ),
            )

        val quote = assertIs<MarkdownRenderBlock.BlockQuote>(model.blocks[0])
        assertTrue(quote.inlines.any { it is MarkdownInlineCode && it.text == "code" })
        val tasks = assertIs<MarkdownRenderBlock.TaskList>(model.blocks[1])
        assertEquals(true, tasks.items.single().checked)
        assertEquals(listOf(MarkdownInlineStrong("done")), tasks.items.single().inlines)
        val table = assertIs<MarkdownRenderBlock.Table>(model.blocks[2])
        assertEquals(listOf("Name", "Value"), table.headers)
        assertEquals(listOf(listOf("alpha", "1")), table.rows)
    }
}
