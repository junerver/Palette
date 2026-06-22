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
    fun renderModelDispatchesTildeFencedCodeAndMermaidBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ~~~kotlin
                    val title = "Palette"
                    ~~~

                    ~~~mermaid
                    flowchart LR
                        A --> B
                    ~~~
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks[0])
        assertEquals("kotlin", code.language)
        assertTrue(code.highlighted.tokens.flatten().any { it.text == "val" && it.type == CodeTokenType.Keyword })
        val mermaid = assertIs<MarkdownRenderBlock.Mermaid>(model.blocks[1])
        assertEquals("A", mermaid.diagram.edges.single().from)
        assertEquals("B", mermaid.diagram.edges.single().to)
    }

    @Test
    fun renderModelParsesMermaidStandaloneNodes() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```mermaid
                    flowchart TD
                        Empty[Standalone node]
                    ```
                    """.trimIndent(),
                ),
            )

        val mermaid = assertIs<MarkdownRenderBlock.Mermaid>(model.blocks.single())
        assertEquals("Standalone node", mermaid.diagram.nodes.getValue("Empty").label)
        assertTrue(mermaid.diagram.edges.isEmpty())
    }

    @Test
    fun renderModelParsesMermaidSequenceNotes() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```mermaid
                    sequenceDiagram
                        Viewer->>Parser: parse markdown
                        Note right of Parser: Builds an AST
                    ```
                    """.trimIndent(),
                ),
            )

        val mermaid = assertIs<MarkdownRenderBlock.Mermaid>(model.blocks.single())
        assertEquals(1, mermaid.diagram.notes.size)
        assertEquals("Builds an AST", mermaid.diagram.notes.single().text)
        assertEquals(1, mermaid.diagram.notes.single().sequenceIndex)
    }

    @Test
    fun renderModelHighlightsHtmlFencedCodeBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```html
                    <section class="card">Palette</section>
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("html", code.language)
        val tokens = code.highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "section" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "class" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "\"card\"" && it.type == CodeTokenType.StringLiteral })
    }

    @Test
    fun renderModelHighlightsShellFencedCodeBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```bash
                    export APP_NAME="Palette"
                    echo ${'$'}APP_NAME
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("bash", code.language)
        val tokens = code.highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "export" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "echo" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "${'$'}APP_NAME" && it.type == CodeTokenType.Annotation })
    }

    @Test
    fun renderModelHighlightsYamlFencedCodeBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```yaml
                    name: Palette
                    enabled: true
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("yaml", code.language)
        val tokens = code.highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "name" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun renderModelHighlightsSqlFencedCodeBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```sql
                    SELECT COUNT(*) FROM users WHERE active = TRUE;
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("sql", code.language)
        val tokens = code.highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "SELECT" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "COUNT" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "TRUE" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun parsesCodeFenceInfoForTitleLineNumbersAndHighlightedLines() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```kotlin title="Greeting.kt" showLineNumbers {1,3-4}
                    fun greeting(name: String) {
                        println("Hello, ${'$'}name")
                    }
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("kotlin", code.language)
        assertEquals("Greeting.kt", code.title)
        assertEquals(true, code.showLineNumbers)
        assertEquals(setOf(1, 3, 4), code.highlightedLines)
        assertTrue(code.highlighted.tokens.flatten().any { it.text == "fun" && it.type == CodeTokenType.Keyword })
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
    fun parsesUnderscoreStrongAndEmphasis() {
        val inline = MarkdownInlineParser.parse("Use __Palette__ with _Compose_.")

        assertEquals(
            listOf(
                MarkdownInlineText("Use "),
                MarkdownInlineStrong("Palette"),
                MarkdownInlineText(" with "),
                MarkdownInlineEmphasis("Compose"),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun keepsIntrawordUnderscoresAsPlainText() {
        val inline = MarkdownInlineParser.parse("Use snake_case_value as an identifier.")

        assertEquals(
            listOf(MarkdownInlineText("Use snake_case_value as an identifier.")),
            inline,
        )
    }

    @Test
    fun keepsIntrawordDoubleUnderscoresAsPlainText() {
        val inline = MarkdownInlineParser.parse("Use snake__case__value as an identifier.")

        assertEquals(
            listOf(MarkdownInlineText("Use snake__case__value as an identifier.")),
            inline,
        )
    }

    @Test
    fun keepsIntrawordUnderscoresInsideEmphasis() {
        val inline = MarkdownInlineParser.parse("Use _snake_case_ value.")

        assertEquals(
            listOf(
                MarkdownInlineText("Use "),
                MarkdownInlineEmphasis("snake_case"),
                MarkdownInlineText(" value."),
            ),
            inline,
        )
    }

    @Test
    fun parsesEscapesStrikethroughAndAutolinks() {
        val inline =
            MarkdownInlineParser.parse(
                """Keep \*literal\*, remove ~~old API~~, visit <https://example.com>, mail <team@example.com>.""",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Keep *literal*, remove "),
                MarkdownInlineStrikethrough("old API"),
                MarkdownInlineText(", visit "),
                MarkdownInlineLink(label = "https://example.com", destination = "https://example.com"),
                MarkdownInlineText(", mail "),
                MarkdownInlineLink(label = "team@example.com", destination = "mailto:team@example.com"),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun parsesBareUrlAutolinksAndKeepsTrailingPunctuation() {
        val inline =
            MarkdownInlineParser.parse(
                "Visit https://example.com/docs, then https://example.org.",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Visit "),
                MarkdownInlineLink(label = "https://example.com/docs", destination = "https://example.com/docs"),
                MarkdownInlineText(", then "),
                MarkdownInlineLink(label = "https://example.org", destination = "https://example.org"),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun parsesReferenceStyleLinksAndImages() {
        val document =
            MarkdownParser.parse(
                """
                Read [the guide][guide], open [guide], and inspect ![diagram][asset].

                [guide]: https://example.com/guide
                [asset]: https://example.com/diagram.png "Architecture diagram"
                """.trimIndent(),
            )

        val paragraph = assertIs<MarkdownParagraph>(document.blocks.single())
        assertEquals(
            listOf(
                MarkdownInlineText("Read "),
                MarkdownInlineLink(label = "the guide", destination = "https://example.com/guide"),
                MarkdownInlineText(", open "),
                MarkdownInlineLink(label = "guide", destination = "https://example.com/guide"),
                MarkdownInlineText(", and inspect "),
                MarkdownInlineImage(alt = "diagram", destination = "https://example.com/diagram.png"),
                MarkdownInlineText("."),
            ),
            paragraph.inlines,
        )
    }

    @Test
    fun ignoresReferenceDefinitionsInsideFencedCodeBlocks() {
        val document =
            MarkdownParser.parse(
                """
                [docs]: https://example.com/docs

                ```text
                [docs]: https://example.com/from-code
                ```

                Read [docs].
                """.trimIndent(),
            )

        val paragraph = assertIs<MarkdownParagraph>(document.blocks.last())
        assertEquals(
            listOf(
                MarkdownInlineText("Read "),
                MarkdownInlineLink(label = "docs", destination = "https://example.com/docs"),
                MarkdownInlineText("."),
            ),
            paragraph.inlines,
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
    fun parsesAtxHeadingsWithClosingHashes() {
        val document =
            MarkdownParser.parse(
                """
                ## **Viewer** ##

                ### Keep trailing hash# ###
                """.trimIndent(),
            )

        assertEquals(2, document.blocks.size)
        val first = assertIs<MarkdownHeading>(document.blocks[0])
        assertEquals(2, first.level)
        assertEquals("**Viewer**", first.text)
        assertEquals(listOf(MarkdownInlineStrong("Viewer")), first.inlines)

        val second = assertIs<MarkdownHeading>(document.blocks[1])
        assertEquals(3, second.level)
        assertEquals("Keep trailing hash#", second.text)
    }

    @Test
    fun keepsAtxHeadingHashWithoutClosingSeparator() {
        val document = MarkdownParser.parse("## Version C#")

        assertEquals(MarkdownHeading(level = 2, text = "Version C#"), document.blocks.single())
    }

    @Test
    fun parsesSetextHeadings() {
        val document =
            MarkdownParser.parse(
                """
                Palette Markdown
                ================

                Viewer **Preview**
                ------------------
                """.trimIndent(),
            )

        assertEquals(2, document.blocks.size)
        assertEquals(MarkdownHeading(level = 1, text = "Palette Markdown"), document.blocks[0])
        val second = assertIs<MarkdownHeading>(document.blocks[1])
        assertEquals(2, second.level)
        assertEquals("Viewer **Preview**", second.text)
        assertEquals(
            listOf(
                MarkdownInlineText("Viewer "),
                MarkdownInlineStrong("Preview"),
            ),
            second.inlines,
        )
    }

    @Test
    fun keepsStandaloneDashLineAsThematicBreak() {
        val document =
            MarkdownParser.parse(
                """
                Before

                ---

                After
                """.trimIndent(),
            )

        assertEquals(
            listOf(
                MarkdownParagraph("Before"),
                MarkdownThematicBreak,
                MarkdownParagraph("After"),
            ),
            document.blocks,
        )
    }

    @Test
    fun preservesOrderedListStartNumber() {
        val document =
            MarkdownParser.parse(
                """
                3. third
                4. fourth
                """.trimIndent(),
            )

        val parsedList = assertIs<MarkdownListBlock>(document.blocks.single())
        assertEquals(true, parsedList.ordered)
        assertEquals(3, parsedList.startNumber)

        val model = MarkdownRenderer.toRenderModel(document)
        val renderList = assertIs<MarkdownRenderBlock.ListBlock>(model.blocks.single())
        assertEquals(3, renderList.startNumber)
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

    @Test
    fun parsesTableColumnAlignments() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    | Name | Count | Status |
                    | :--- | ---: | :---: |
                    | API | 3 | Ready |
                    """.trimIndent(),
                ),
            )

        val table = assertIs<MarkdownRenderBlock.Table>(model.blocks.single())
        assertEquals(
            listOf(
                MarkdownTableAlignment.Start,
                MarkdownTableAlignment.End,
                MarkdownTableAlignment.Center,
            ),
            table.alignments,
        )
    }
}
