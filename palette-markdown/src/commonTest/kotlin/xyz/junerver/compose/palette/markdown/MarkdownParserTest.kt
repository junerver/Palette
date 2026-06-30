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
    fun parsesIndentedFencedCodeBlocksUpToThreeSpaces() {
        val document =
            MarkdownParser.parse(
                "   ```kotlin\n" +
                    "   val answer = 42\n" +
                    "     println(answer)\n" +
                    "   ```",
            )

        val code = assertIs<MarkdownCodeBlock>(document.blocks.single())
        assertEquals("kotlin", code.language)
        assertEquals("val answer = 42\n  println(answer)", code.content)
    }

    @Test
    fun keepsFourSpaceIndentedFenceAsIndentedCode() {
        val document =
            MarkdownParser.parse(
                "    ```kotlin\n" +
                    "    val answer = 42\n" +
                    "    ```",
            )

        val code = assertIs<MarkdownCodeBlock>(document.blocks.single())
        assertEquals("plain", code.language)
        assertEquals("```kotlin\nval answer = 42\n```", code.content)
    }

    @Test
    fun parsesIndentedCodeBlocksAsPlainCode() {
        val document =
            MarkdownParser.parse(
                """
                Before

                    val answer = 42
                        println(answer)
                ${'\t'}return answer

                After
                """.trimIndent(),
            )

        assertEquals(3, document.blocks.size)
        assertEquals(MarkdownParagraph("Before"), document.blocks[0])
        val code = assertIs<MarkdownCodeBlock>(document.blocks[1])
        assertEquals("plain", code.language)
        assertEquals("val answer = 42\n    println(answer)\nreturn answer", code.content)
        assertEquals(MarkdownParagraph("After"), document.blocks[2])
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
        assertTrue(tokens.any { it.text == "echo" && it.type == CodeTokenType.Builtin })
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
    fun renderModelHighlightsDiffFencedCodeBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```diff
                    @@ -1,2 +1,2 @@
                    -OldButton()
                    +NewButton()
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("diff", code.language)
        val tokens = code.highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "@@" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "-OldButton()" && it.type == CodeTokenType.Deleted })
        assertTrue(tokens.any { it.text == "+NewButton()" && it.type == CodeTokenType.Inserted })
    }

    @Test
    fun renderModelHighlightsMarkdownFencedCodeBlocks() {
        val model =
            MarkdownRenderer.toRenderModel(
                MarkdownParser.parse(
                    """
                    ```markdown
                    ## Nested Markdown

                    - [x] Render `inline code`
                    - [ ] Open [docs](https://example.com/docs)
                    ```
                    """.trimIndent(),
                ),
            )

        val code = assertIs<MarkdownRenderBlock.Code>(model.blocks.single())
        assertEquals("markdown", code.language)
        val tokens = code.highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "##" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "[x]" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "`inline code`" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "docs" && it.type == CodeTokenType.Type })
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
    fun parsesNestedInlineNodesInsideEmphasisAndLinks() {
        val inline =
            MarkdownInlineParser.parse(
                "Use **Palette `tokens`** with [**docs** and `code`](https://example.com).",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Use "),
                MarkdownInlineStrong(
                    text = "Palette `tokens`",
                    children =
                        listOf(
                            MarkdownInlineText("Palette "),
                            MarkdownInlineCode("tokens"),
                        ),
                ),
                MarkdownInlineText(" with "),
                MarkdownInlineLink(
                    label = "**docs** and `code`",
                    destination = "https://example.com",
                    children =
                        listOf(
                            MarkdownInlineStrong("docs"),
                            MarkdownInlineText(" and "),
                            MarkdownInlineCode("code"),
                        ),
                ),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun parsesCodeSpansDelimitedByMatchingBacktickRuns() {
        val inline = MarkdownInlineParser.parse("Use `` `literal` `` and `plain` code.")

        assertEquals(
            listOf(
                MarkdownInlineText("Use "),
                MarkdownInlineCode("`literal`"),
                MarkdownInlineText(" and "),
                MarkdownInlineCode("plain"),
                MarkdownInlineText(" code."),
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
    fun parsesBareUrlAutolinksWithBalancedParentheses() {
        val inline =
            MarkdownInlineParser.parse(
                "Open https://example.com/a_(b), then https://example.org/(docs).",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Open "),
                MarkdownInlineLink(label = "https://example.com/a_(b)", destination = "https://example.com/a_(b)"),
                MarkdownInlineText(", then "),
                MarkdownInlineLink(label = "https://example.org/(docs)", destination = "https://example.org/(docs)"),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun parsesInlineLinkAndImageTitles() {
        val inline =
            MarkdownInlineParser.parse(
                """Open [docs](https://example.com/docs "Palette docs") and ![logo](https://example.com/logo.png 'Palette logo').""",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Open "),
                MarkdownInlineLink(
                    label = "docs",
                    destination = "https://example.com/docs",
                    title = "Palette docs",
                ),
                MarkdownInlineText(" and "),
                MarkdownInlineImage(
                    alt = "logo",
                    destination = "https://example.com/logo.png",
                    title = "Palette logo",
                ),
                MarkdownInlineText("."),
            ),
            inline,
        )
    }

    @Test
    fun parsesInlineLinksAndImagesWithParenthesesInDestinations() {
        val inline =
            MarkdownInlineParser.parse(
                "Open [docs](https://example.com/a_(b)) and ![chart](https://example.com/chart_(v2).png).",
            )

        assertEquals(
            listOf(
                MarkdownInlineText("Open "),
                MarkdownInlineLink(label = "docs", destination = "https://example.com/a_(b)"),
                MarkdownInlineText(" and "),
                MarkdownInlineImage(alt = "chart", destination = "https://example.com/chart_(v2).png"),
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
                MarkdownInlineImage(
                    alt = "diagram",
                    destination = "https://example.com/diagram.png",
                    title = "Architecture diagram",
                ),
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
    fun parsesMixedRegularAndTaskListItems() {
        val document =
            MarkdownParser.parse(
                """
                - item one
                - [x] done
                - [ ] open
                """.trimIndent(),
            )

        // Debug: check all block types
        val blockTypes = document.blocks.map { it::class.simpleName }
        assertEquals(2, document.blocks.size, "Expected 2 blocks, got ${document.blocks.size}: $blockTypes")

        val listBlock = document.blocks.filterIsInstance<MarkdownListBlock>()
        val taskBlock = document.blocks.filterIsInstance<MarkdownTaskListBlock>()

        assertEquals(1, listBlock.size, "Expected one regular list block, got types: $blockTypes")
        assertEquals(1, taskBlock.size, "Expected one task list block, got types: $blockTypes")
        assertEquals("item one", listBlock[0].listItems[0].text)
        assertEquals("done", taskBlock[0].items[0].text)
        assertEquals(true, taskBlock[0].items[0].checked)
        assertEquals("open", taskBlock[0].items[1].text)
        assertEquals(false, taskBlock[0].items[1].checked)
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

    @Test
    fun parsesEscapedPipesAndInlineCodePipesInTableCells() {
        val document =
            MarkdownParser.parse(
                """
                | Name | Expression | Result |
                | --- | --- | --- |
                | Pipe | a\|b | `x|y` |
                """.trimIndent(),
            )

        val table = assertIs<MarkdownTableBlock>(document.blocks.single())
        assertEquals(listOf("Name", "Expression", "Result"), table.headers)
        assertEquals(listOf(listOf("Pipe", "a|b", "`x|y`")), table.rows)
        assertEquals(
            listOf(MarkdownInlineCode("x|y")),
            table.rowInlines.single()[2],
        )
    }

    @Test
    fun preservesEmptyTableCells() {
        val document =
            MarkdownParser.parse(
                """
                | Name | Notes | Status |
                | --- | --- | --- |
                | Parser |  | Ready |
                | Renderer | Uses `code` |  |
                """.trimIndent(),
            )

        val table = assertIs<MarkdownTableBlock>(document.blocks.single())
        assertEquals(listOf("Name", "Notes", "Status"), table.headers)
        assertEquals(
            listOf(
                listOf("Parser", "", "Ready"),
                listOf("Renderer", "Uses `code`", ""),
            ),
            table.rows,
        )
        assertEquals(emptyList(), table.rowInlines.first()[1])
        assertEquals(emptyList(), table.rowInlines.last()[2])
    }

    @Test
    fun parsesInlineLinkWithBalancedParenthesesInDestination() {
        val inline = MarkdownInlineParser.parse("[wiki](https://en.wikipedia.org/wiki/Foo_(bar))")
        val link = assertIs<MarkdownInlineLink>(inline.single())
        assertEquals("wiki", link.label)
        assertEquals("https://en.wikipedia.org/wiki/Foo_(bar)", link.destination)
    }

    @Test
    fun parsesInlineLinkWithBalancedParenthesesAndTitle() {
        val inline = MarkdownInlineParser.parse("[wiki](https://en.wikipedia.org/wiki/Foo_(bar) \"Title\")")
        val link = assertIs<MarkdownInlineLink>(inline.single())
        assertEquals("wiki", link.label)
        assertEquals("https://en.wikipedia.org/wiki/Foo_(bar)", link.destination)
        assertEquals("Title", link.title)
    }

    @Test
    fun normalizesTableRowColumnCountToMatchHeader() {
        val document = MarkdownParser.parse(
            """
            | A | B | C |
            | --- | --- | --- |
            | 1 | 2 |
            | 4 | 5 | 6 | 7 |
            """.trimIndent(),
        )
        val table = assertIs<MarkdownTableBlock>(document.blocks.single())
        assertEquals(listOf("A", "B", "C"), table.headers)
        assertEquals(2, table.rows.size)
        // Row with fewer columns gets empty cells
        assertEquals(listOf("1", "2", ""), table.rows[0])
        // Row with more columns gets truncated
        assertEquals(listOf("4", "5", "6"), table.rows[1])
    }

    @Test
    fun parsesEmphasisWithPunctuationBoundaries() {
        // Test that emphasis works with punctuation boundaries
        val inline = MarkdownInlineParser.parse("a**bold**c")
        val strong = assertIs<MarkdownInlineStrong>(inline[1])
        assertEquals("bold", strong.text)
    }

    @Test
    fun parsesBareWwwAutolinkWithHttpPrefix() {
        val inline = MarkdownInlineParser.parse("Visit www.example.com for more info.")
        val link = assertIs<MarkdownInlineLink>(inline[1])
        assertEquals("www.example.com", link.label)
        assertEquals("http://www.example.com", link.destination)
    }

    @Test
    fun decodesCommonHtmlEntities() {
        val inline = MarkdownInlineParser.parse("Copyright &copy; 2024 &mdash; All rights reserved")
        val text = inline.filterIsInstance<MarkdownInlineText>().joinToString("") { it.text }
        assertTrue(text.contains("\u00A9"))
        assertTrue(text.contains("\u2014"))
    }

    @Test
    fun parsesNestedBlockquoteChildren() {
        val document = MarkdownParser.parse(
            """
            > ## Nested Heading
            >
            > Paragraph inside blockquote.
            """.trimIndent(),
        )
        val quote = assertIs<MarkdownBlockQuote>(document.blocks.single())
        assertTrue(quote.children.isNotEmpty())
        val heading = quote.children.filterIsInstance<MarkdownHeading>().firstOrNull()
        assertEquals(2, heading?.level)
        val paragraph = quote.children.filterIsInstance<MarkdownParagraph>().firstOrNull()
        assertTrue(paragraph?.text?.contains("Paragraph inside blockquote") == true)
    }

    @Test
    fun parsesListItemChildren() {
        val document = MarkdownParser.parse(
            """
            - First item
              - Nested item
              - Another nested
            - Second item
            """.trimIndent(),
        )
        val list = assertIs<MarkdownListBlock>(document.blocks.single())
        assertEquals(2, list.listItems.size)
        assertTrue(list.listItems[0].children.isNotEmpty())
        val nestedList = list.listItems[0].children.filterIsInstance<MarkdownListBlock>().firstOrNull()
        assertEquals(2, nestedList?.items?.size)
        assertTrue(list.listItems[1].children.isEmpty())
    }


    @Test
    fun separatesOrderedAndUnorderedListsWhenMixed() {
        val document =
            MarkdownParser.parse(
                """
                1. first ordered
                2. second ordered
                - unordered item
                """.trimIndent(),
            )

        assertEquals(2, document.blocks.size, "Mixed ordered/unordered should produce two separate list blocks")
        val orderedList = assertIs<MarkdownListBlock>(document.blocks[0])
        assertEquals(true, orderedList.ordered)
        assertEquals(2, orderedList.items.size)
        assertEquals("first ordered", orderedList.items[0])
        assertEquals("second ordered", orderedList.items[1])

        val unorderedList = assertIs<MarkdownListBlock>(document.blocks[1])
        assertEquals(false, unorderedList.ordered)
        assertEquals(1, unorderedList.items.size)
        assertEquals("unordered item", unorderedList.items[0])
    }

    @Test
    fun separatesUnorderedAndOrderedListsWhenMixed() {
        val document =
            MarkdownParser.parse(
                """
                - unordered first
                - unordered second
                1. ordered item
                """.trimIndent(),
            )

        assertEquals(2, document.blocks.size, "Mixed unordered/ordered should produce two separate list blocks")
        val unorderedList = assertIs<MarkdownListBlock>(document.blocks[0])
        assertEquals(false, unorderedList.ordered)
        assertEquals(2, unorderedList.items.size)

        val orderedList = assertIs<MarkdownListBlock>(document.blocks[1])
        assertEquals(true, orderedList.ordered)
        assertEquals(1, orderedList.items.size)
        assertEquals("ordered item", orderedList.items[0])
    }

    @Test
    fun parsesMultiLineReferenceDefinitionTitle() {
        val document =
            MarkdownParser.parse(
                """
                Read [the guide][guide].

                [guide]: https://example.com/guide
                  "Multi-line
                  title here"
                """.trimIndent(),
            )

        val paragraph = assertIs<MarkdownParagraph>(document.blocks[0])
        val link = paragraph.inlines.filterIsInstance<MarkdownInlineLink>().first()
        assertEquals("the guide", link.label)
        assertEquals("https://example.com/guide", link.destination)
        assertEquals("Multi-line title here", link.title)
    }

    @Test
    fun parsesInlineLatexFormula() {
        val inline = MarkdownInlineParser.parse("公式 \$a^2 + b^2 = c^2\$。")
        // 去除首尾文本节点后应包含一个 MarkdownInlineLatex
        val latex = inline.filterIsInstance<MarkdownInlineLatex>().single()
        assertEquals("a^2 + b^2 = c^2", latex.tex)
    }

    @Test
    fun parsesInlineLatexWithFraction() {
        val inline = MarkdownInlineParser.parse("half of one is \$\\frac{1}{2}\$.")
        val latex = inline.filterIsInstance<MarkdownInlineLatex>().single()
        assertEquals("\\frac{1}{2}", latex.tex)
    }

    @Test
    fun parsesDisplayLatex() {
        // 块级 $$...$$ 作为单个行内节点（在段落内）
        val inline = MarkdownInlineParser.parse("\$\$E=mc^2\$\$")
        val latex = inline.filterIsInstance<MarkdownInlineLatex>().single()
        assertEquals("E=mc^2", latex.tex)
    }

    @Test
    fun latexLeftBoundaryAvoidsPriceFalsePositive() {
        // "price $5 and $6" 不应把 $5...$ 当作公式：右侧 $ 后无边界 / 左侧非边界
        val inline = MarkdownInlineParser.parse("price \$5 and \$6")
        assertTrue(inline.none { it is MarkdownInlineLatex }, "价格符号不应被误判为行内公式")
    }

    @Test
    fun escapedDollarIsLiteral() {
        val inline = MarkdownInlineParser.parse("cost \\\$5 each")
        assertTrue(inline.none { it is MarkdownInlineLatex })
        assertEquals("cost \$5 each", inline.joinToString("") { it.text })
    }

    @Test
    fun parsesLatexParenDelimiters() {
        val inline = MarkdownInlineParser.parse("\\(x^2\\) inline")
        val latex = inline.filterIsInstance<MarkdownInlineLatex>().single()
        assertEquals("x^2", latex.tex)
    }

    @Test
    fun parsesSubscript() {
        val inline = MarkdownInlineParser.parse("Water is H~2~O.")
        val sub = inline.filterIsInstance<MarkdownInlineSubscript>().single()
        assertEquals("2", sub.text)
    }

    @Test
    fun parsesSuperscript() {
        val inline = MarkdownInlineParser.parse("Energy E^2^.")
        val sup = inline.filterIsInstance<MarkdownInlineSuperscript>().single()
        assertEquals("2", sup.text)
    }

    @Test
    fun parsesHighlight() {
        val inline = MarkdownInlineParser.parse("This is ==KEY==.")
        val hl = inline.filterIsInstance<MarkdownInlineHighlight>().single()
        assertEquals("KEY", hl.text)
    }

    @Test
    fun highlightNotTriggeredOnEquality() {
        // a==b 是等式比较，左侧无边界，不应解析为高亮
        val inline = MarkdownInlineParser.parse("a==b==c")
        assertTrue(inline.none { it is MarkdownInlineHighlight }, "等式 a==b 不应被误判为高亮")
    }

    @Test
    fun strikethroughStillWorksAlongsideSubscript() {
        // ~~删除线~~ 与 H~2~O 下标共存
        val inline = MarkdownInlineParser.parse("~~gone~~ and H~2~O")
        assertTrue(inline.any { it is MarkdownInlineStrikethrough })
        assertTrue(inline.any { it is MarkdownInlineSubscript })
    }

}
