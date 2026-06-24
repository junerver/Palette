package xyz.junerver.compose.palette.markdown

import xyz.junerver.compose.palette.code.PaletteCodeDiagnostic
import xyz.junerver.compose.palette.code.PaletteCodeDiagnosticSeverity
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.mermaid.MermaidDiagram
import xyz.junerver.compose.palette.mermaid.MermaidDiagnosticSeverity
import xyz.junerver.compose.palette.mermaid.MermaidParser
import xyz.junerver.compose.palette.mermaid.MermaidParseDiagnostic

object MarkdownRenderer {
    fun toRenderModel(document: MarkdownDocument): MarkdownRenderModel =
        toRenderModel(document, mermaidParser = { MermaidParser.parse(it) })

    internal fun toRenderModel(
        document: MarkdownDocument,
        mermaidParser: (String) -> MermaidDiagram,
    ): MarkdownRenderModel {
        val diagnostics = mutableListOf<MarkdownRenderDiagnostic>()
        val headingIdCounts = mutableMapOf<String, Int>()
        val blocks =
            document.blocks.mapIndexed { index, block ->
                toRenderBlock(
                    block = block,
                    mermaidParser = mermaidParser,
                    diagnostics = diagnostics,
                    blockIndex = index,
                    headingIdCounts = headingIdCounts,
                )
            }
        return MarkdownRenderModel(blocks = blocks, diagnostics = diagnostics)
    }

    private fun toRenderBlock(
        block: MarkdownBlock,
        mermaidParser: (String) -> MermaidDiagram,
        diagnostics: MutableList<MarkdownRenderDiagnostic>,
        blockIndex: Int?,
        headingIdCounts: MutableMap<String, Int>,
    ): MarkdownRenderBlock =
        when (block) {
            is MarkdownHeading -> {
                val baseSlug = block.inlines.toPlainText().toHeadingSlug()
                val count = headingIdCounts.getOrDefault(baseSlug, 0)
                headingIdCounts[baseSlug] = count + 1
                val id = if (count == 0) baseSlug else "${baseSlug}-${count + 1}"
                MarkdownRenderBlock.Heading(
                    level = block.level,
                    text = block.text,
                    inlines = block.inlines,
                    sourceRange = block.sourceRange,
                    id = id,
                )
            }
            is MarkdownParagraph -> MarkdownRenderBlock.Paragraph(block.text, block.inlines, block.sourceRange)
            is MarkdownListBlock ->
                MarkdownRenderBlock.ListBlock(
                    items = block.items,
                    ordered = block.ordered,
                    itemInlines = block.itemInlines,
                    startNumber = block.startNumber,
                    tight = block.tight,
                    listItems =
                        block.listItems.map { item ->
                            MarkdownRenderListItem(
                                text = item.text,
                                inlines = item.inlines,
                                children =
                                    item.children.map { child ->
                                        toRenderBlock(
                                            block = child,
                                            mermaidParser = mermaidParser,
                                            diagnostics = diagnostics,
                                            blockIndex = blockIndex,
                                            headingIdCounts = headingIdCounts,
                                        )
                                    },
                                taskChecked = item.taskChecked,
                            )
                        },
                    sourceRange = block.sourceRange,
                )
            is MarkdownTaskListBlock -> MarkdownRenderBlock.TaskList(block.items)
            is MarkdownBlockQuote ->
                MarkdownRenderBlock.BlockQuote(
                    text = block.text,
                    inlines = block.inlines,
                    children =
                        block.children.map { child ->
                            toRenderBlock(
                                block = child,
                                mermaidParser = mermaidParser,
                                diagnostics = diagnostics,
                                blockIndex = blockIndex,
                                headingIdCounts = headingIdCounts,
                            )
                        },
                    sourceRange = block.sourceRange,
                )
            is MarkdownTableBlock ->
                MarkdownRenderBlock.Table(
                    headers = block.headers,
                    rows = block.rows,
                    alignments = block.alignments,
                    headerInlines = block.headerInlines,
                    rowInlines = block.rowInlines,
                    sourceRange = block.sourceRange,
                )

            is MarkdownCodeBlock -> {
                val highlighted = PaletteCodeHighlighter.highlightWithDiagnostics(block.content, block.language)
                val blockDiagnostics =
                    highlighted.diagnostics.map { diagnostic ->
                        diagnostic.toMarkdownRenderDiagnostic(blockIndex = blockIndex)
                    }
                diagnostics += blockDiagnostics
                MarkdownRenderBlock.Code(
                    language = block.language,
                    highlighted = highlighted,
                    title = block.title,
                    showLineNumbers = block.showLineNumbers,
                    highlightedLines = block.highlightedLines,
                    diagnostics = blockDiagnostics,
                    sourceRange = block.sourceRange,
                )
            }

            is MarkdownMermaidBlock ->
                runCatching {
                    val diagram = mermaidParser(block.source)
                    val blockDiagnostics =
                        diagram.diagnostics.map { diagnostic ->
                            diagnostic.toMarkdownRenderDiagnostic(blockIndex = blockIndex)
                        }
                    diagnostics += blockDiagnostics
                    MarkdownRenderBlock.Mermaid(
                        source = block.source,
                        diagram = diagram,
                        diagnostics = blockDiagnostics,
                        sourceRange = block.sourceRange,
                    )
                }.getOrElse { error ->
                    val diagnostic =
                        MarkdownRenderDiagnostic(
                            code = MarkdownRenderDiagnosticCode.MermaidParserFailure,
                            message = "Mermaid block could not be parsed: ${error.message ?: error::class.simpleName}",
                            severity = MarkdownRenderDiagnosticSeverity.Error,
                            blockIndex = blockIndex,
                            source = block.source,
                        )
                    diagnostics += diagnostic
                    MarkdownRenderBlock.Code(
                        language = "mermaid",
                        highlighted = PaletteCodeHighlighter.highlight(block.source, "mermaid"),
                        title = null,
                        showLineNumbers = false,
                        highlightedLines = emptySet(),
                        diagnostics = listOf(diagnostic),
                        sourceRange = block.sourceRange,
                    )
                }

            is MarkdownHtmlBlock -> MarkdownRenderBlock.Html(block.html, block.sourceRange)

            MarkdownThematicBreak -> MarkdownRenderBlock.ThematicBreak
        }

    private fun MermaidParseDiagnostic.toMarkdownRenderDiagnostic(blockIndex: Int?): MarkdownRenderDiagnostic =
        MarkdownRenderDiagnostic(
            code = MarkdownRenderDiagnosticCode.MermaidDiagnostic,
            message = message,
            severity =
                when (severity) {
                    MermaidDiagnosticSeverity.Warning -> MarkdownRenderDiagnosticSeverity.Warning
                    MermaidDiagnosticSeverity.Error -> MarkdownRenderDiagnosticSeverity.Error
                },
            originCode = code.name,
            blockIndex = blockIndex,
            line = line,
            column = column,
            endColumn = endColumn,
            source = source,
        )

    private fun PaletteCodeDiagnostic.toMarkdownRenderDiagnostic(blockIndex: Int?): MarkdownRenderDiagnostic =
        MarkdownRenderDiagnostic(
            code = MarkdownRenderDiagnosticCode.CodeHighlighterDiagnostic,
            message = message,
            severity =
                when (severity) {
                    PaletteCodeDiagnosticSeverity.Warning -> MarkdownRenderDiagnosticSeverity.Warning
                    PaletteCodeDiagnosticSeverity.Error -> MarkdownRenderDiagnosticSeverity.Error
                },
            blockIndex = blockIndex,
            line = line,
            column = column,
        )

    private fun List<MarkdownInlineNode>.toPlainText(): String =
        joinToString("") { node ->
            when (node) {
                is MarkdownInlineText -> node.text
                is MarkdownInlineStrong -> node.children.toPlainText()
                is MarkdownInlineEmphasis -> node.children.toPlainText()
                is MarkdownInlineStrikethrough -> node.children.toPlainText()
                is MarkdownInlineCode -> node.text
                is MarkdownInlineLink -> node.children.toPlainText()
                is MarkdownInlineImage -> node.alt
                is MarkdownInlineHtml -> node.html
                is MarkdownInlineHardBreak -> " "
                is MarkdownInlineSoftBreak -> " "
            }
        }

}
