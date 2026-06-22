package xyz.junerver.compose.palette.markdown

import xyz.junerver.compose.palette.code.HighlightedCode
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.mermaid.MermaidDiagram
import xyz.junerver.compose.palette.mermaid.MermaidParser

data class MarkdownDocument(
    val blocks: List<MarkdownBlock>,
)

sealed interface MarkdownBlock

data class MarkdownHeading(
    val level: Int,
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
) : MarkdownBlock

data class MarkdownParagraph(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
) : MarkdownBlock

data class MarkdownListBlock(
    val items: List<String>,
    val ordered: Boolean,
    val itemInlines: List<List<MarkdownInlineNode>> = items.map(MarkdownInlineParser::parse),
) : MarkdownBlock

data class MarkdownTaskItem(
    val text: String,
    val checked: Boolean,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
)

data class MarkdownTaskListBlock(
    val items: List<MarkdownTaskItem>,
) : MarkdownBlock

data class MarkdownBlockQuote(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
) : MarkdownBlock

data class MarkdownTableBlock(
    val headers: List<String>,
    val rows: List<List<String>>,
    val headerInlines: List<List<MarkdownInlineNode>> = headers.map(MarkdownInlineParser::parse),
    val rowInlines: List<List<List<MarkdownInlineNode>>> = rows.map { row -> row.map(MarkdownInlineParser::parse) },
) : MarkdownBlock

data class MarkdownCodeBlock(
    val language: String,
    val content: String,
) : MarkdownBlock

data class MarkdownMermaidBlock(
    val source: String,
) : MarkdownBlock

data object MarkdownThematicBreak : MarkdownBlock

sealed interface MarkdownInlineNode {
    val text: String
}

data class MarkdownInlineText(
    override val text: String,
) : MarkdownInlineNode

data class MarkdownInlineStrong(
    override val text: String,
) : MarkdownInlineNode

data class MarkdownInlineEmphasis(
    override val text: String,
) : MarkdownInlineNode

data class MarkdownInlineStrikethrough(
    override val text: String,
) : MarkdownInlineNode

data class MarkdownInlineCode(
    override val text: String,
) : MarkdownInlineNode

data class MarkdownInlineLink(
    val label: String,
    val destination: String,
) : MarkdownInlineNode {
    override val text: String
        get() = label
}

data class MarkdownInlineImage(
    val alt: String,
    val destination: String,
) : MarkdownInlineNode {
    override val text: String
        get() = alt
}

object MarkdownInlineParser {
    fun parse(source: String): List<MarkdownInlineNode> {
        val nodes = mutableListOf<MarkdownInlineNode>()
        val plain = StringBuilder()
        var index = 0

        fun flushPlain() {
            if (plain.isNotEmpty()) {
                nodes += MarkdownInlineText(plain.toString())
                plain.clear()
            }
        }

        while (index < source.length) {
            when {
                source[index] == '\\' -> {
                    val next = source.getOrNull(index + 1)
                    if (next != null && next in EscapableMarkdownChars) {
                        plain.append(next)
                        index += 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.startsWith("![", index) -> {
                    val altEnd = source.indexOf(']', startIndex = index + 2)
                    val destinationStart = altEnd + 1
                    if (
                        altEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '('
                    ) {
                        val destinationEnd = source.indexOf(')', startIndex = destinationStart + 1)
                        if (destinationEnd != -1) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineImage(
                                    alt = source.substring(index + 2, altEnd),
                                    destination = source.substring(destinationStart + 1, destinationEnd),
                                )
                            index = destinationEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.startsWith("~~", index) -> {
                    val end = source.indexOf("~~", startIndex = index + 2)
                    if (end != -1) {
                        flushPlain()
                        nodes += MarkdownInlineStrikethrough(source.substring(index + 2, end))
                        index = end + 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.startsWith("**", index) -> {
                    val end = source.indexOf("**", startIndex = index + 2)
                    if (end != -1) {
                        flushPlain()
                        nodes += MarkdownInlineStrong(source.substring(index + 2, end))
                        index = end + 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '*' -> {
                    val end = source.indexOf('*', startIndex = index + 1)
                    if (end != -1) {
                        flushPlain()
                        nodes += MarkdownInlineEmphasis(source.substring(index + 1, end))
                        index = end + 1
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '`' -> {
                    val end = source.indexOf('`', startIndex = index + 1)
                    if (end != -1) {
                        flushPlain()
                        nodes += MarkdownInlineCode(source.substring(index + 1, end))
                        index = end + 1
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '<' -> {
                    val end = source.indexOf('>', startIndex = index + 1)
                    val autolink = if (end != -1) source.substring(index + 1, end).toAutolink() else null
                    if (autolink != null) {
                        flushPlain()
                        nodes += autolink
                        index = end + 1
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '[' -> {
                    val labelEnd = source.indexOf(']', startIndex = index + 1)
                    val destinationStart = labelEnd + 1
                    if (
                        labelEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '('
                    ) {
                        val destinationEnd = source.indexOf(')', startIndex = destinationStart + 1)
                        if (destinationEnd != -1) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = source.substring(index + 1, labelEnd),
                                    destination = source.substring(destinationStart + 1, destinationEnd),
                                )
                            index = destinationEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                else -> {
                    plain.append(source[index])
                    index += 1
                }
            }
        }

        flushPlain()
        return nodes
    }

    private fun String.toAutolink(): MarkdownInlineLink? =
        when {
            matches(AutolinkUrlRegex) -> MarkdownInlineLink(label = this, destination = this)
            matches(AutolinkEmailRegex) -> MarkdownInlineLink(label = this, destination = "mailto:$this")
            else -> null
        }

    private val AutolinkUrlRegex = Regex("""https?://[^\s<>]+""")
    private val AutolinkEmailRegex = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
    private val EscapableMarkdownChars = setOf('\\', '`', '*', '_', '{', '}', '[', ']', '(', ')', '#', '+', '-', '.', '!', '|', '~')
}

object MarkdownParser {
    fun parse(source: String): MarkdownDocument {
        val lines = source.lines()
        val blocks = mutableListOf<MarkdownBlock>()
        var index = 0

        while (index < lines.size) {
            val line = lines[index]
            val trimmed = line.trim()
            when {
                trimmed.isEmpty() -> index += 1
                trimmed.startsWith("```") -> {
                    val language = trimmed.removePrefix("```").trim().lowercase()
                    val content = mutableListOf<String>()
                    index += 1
                    while (index < lines.size && !lines[index].trim().startsWith("```")) {
                        content += lines[index]
                        index += 1
                    }
                    if (index < lines.size) index += 1
                    val blockContent = content.joinToString("\n")
                    blocks +=
                        if (language == "mermaid") {
                            MarkdownMermaidBlock(blockContent)
                        } else {
                            MarkdownCodeBlock(language = language.ifEmpty { "plain" }, content = blockContent)
                        }
                }

                trimmed.startsWith(">") -> {
                    val quote = mutableListOf<String>()
                    while (index < lines.size && lines[index].trim().startsWith(">")) {
                        quote += lines[index].trim().removePrefix(">").trim()
                        index += 1
                    }
                    blocks += MarkdownBlockQuote(quote.joinToString(" ").trim())
                }

                trimmed.isTaskListItem() -> {
                    val items = mutableListOf<MarkdownTaskItem>()
                    while (index < lines.size && lines[index].trim().isTaskListItem()) {
                        val match = TaskListRegex.matchEntire(lines[index].trim())
                        if (match != null) {
                            items +=
                                MarkdownTaskItem(
                                    text = match.groupValues[3].trim(),
                                    checked = match.groupValues[2].equals("x", ignoreCase = true),
                                )
                        }
                        index += 1
                    }
                    blocks += MarkdownTaskListBlock(items)
                }

                trimmed.isTableHeader(lines.getOrNull(index + 1)?.trim()) -> {
                    val headers = trimmed.tableCells()
                    index += 2
                    val rows = mutableListOf<List<String>>()
                    while (index < lines.size && lines[index].trim().isTableRow()) {
                        rows += lines[index].trim().tableCells()
                        index += 1
                    }
                    blocks += MarkdownTableBlock(headers = headers, rows = rows)
                }

                trimmed.matches(ThematicBreakRegex) -> {
                    blocks += MarkdownThematicBreak
                    index += 1
                }

                trimmed.startsWith("#") -> {
                    val match = HeadingRegex.matchEntire(trimmed)
                    if (match != null) {
                        blocks +=
                            MarkdownHeading(
                                level = match.groupValues[1].length.coerceIn(1, 6),
                                text = match.groupValues[2].trim(),
                            )
                        index += 1
                    } else {
                        val paragraph = readParagraph(lines, index)
                        blocks += MarkdownParagraph(paragraph.text)
                        index = paragraph.nextIndex
                    }
                }

                trimmed.isListItem() -> {
                    val ordered = OrderedListRegex.matches(trimmed)
                    val items = mutableListOf<String>()
                    while (index < lines.size && lines[index].trim().isListItem()) {
                        items += lines[index].trim().removeListMarker()
                        index += 1
                    }
                    blocks += MarkdownListBlock(items = items, ordered = ordered)
                }

                else -> {
                    val paragraph = readParagraph(lines, index)
                    blocks += MarkdownParagraph(paragraph.text)
                    index = paragraph.nextIndex
                }
            }
        }

        return MarkdownDocument(blocks)
    }

    private fun readParagraph(
        lines: List<String>,
        start: Int,
    ): ParagraphRead {
        val parts = mutableListOf<String>()
        var index = start
        while (index < lines.size) {
            val trimmed = lines[index].trim()
            if (
                trimmed.isEmpty() ||
                trimmed.startsWith("```") ||
                trimmed.startsWith(">") ||
                HeadingRegex.matchEntire(trimmed) != null ||
                trimmed.matches(ThematicBreakRegex) ||
                trimmed.isTaskListItem() ||
                trimmed.isTableHeader(lines.getOrNull(index + 1)?.trim()) ||
                trimmed.isListItem()
            ) {
                break
            }
            parts += trimmed
            index += 1
        }
        return ParagraphRead(text = parts.joinToString(" ").trim(), nextIndex = index)
    }

    private data class ParagraphRead(
        val text: String,
        val nextIndex: Int,
    )

    private val HeadingRegex = Regex("""^(#{1,6})\s+(.+)$""")
    private val ThematicBreakRegex = Regex("""^(-{3,}|\*{3,}|_{3,})$""")
    private val TaskListRegex = Regex("""^([-*+])\s+\[([ xX])]\s+(.+)$""")
    private val OrderedListRegex = Regex("""^\d+[.)]\s+.+$""")
    private val UnorderedListRegex = Regex("""^[-*+]\s+.+$""")

    private fun String.isListItem(): Boolean = OrderedListRegex.matches(this) || UnorderedListRegex.matches(this)

    private fun String.isTaskListItem(): Boolean = TaskListRegex.matches(this)

    private fun String.removeListMarker(): String =
        replace(Regex("""^(\d+[.)]|[-*+])\s+"""), "").trim()

    private fun String.isTableHeader(nextLine: String?): Boolean =
        isTableRow() && nextLine?.matches(TableDelimiterRegex) == true

    private fun String.isTableRow(): Boolean = contains("|") && tableCells().isNotEmpty()

    private fun String.tableCells(): List<String> =
        trim()
            .trim('|')
            .split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    private val TableDelimiterRegex = Regex("""^\|?\s*:?-{3,}:?\s*(\|\s*:?-{3,}:?\s*)+\|?$""")
}

data class MarkdownRenderModel(
    val blocks: List<MarkdownRenderBlock>,
)

sealed interface MarkdownRenderBlock {
    data class Heading(
        val level: Int,
        val text: String,
        val inlines: List<MarkdownInlineNode>,
    ) : MarkdownRenderBlock

    data class Paragraph(
        val text: String,
        val inlines: List<MarkdownInlineNode>,
    ) : MarkdownRenderBlock

    data class ListBlock(
        val items: List<String>,
        val ordered: Boolean,
        val itemInlines: List<List<MarkdownInlineNode>>,
    ) : MarkdownRenderBlock

    data class TaskList(
        val items: List<MarkdownTaskItem>,
    ) : MarkdownRenderBlock

    data class BlockQuote(
        val text: String,
        val inlines: List<MarkdownInlineNode>,
    ) : MarkdownRenderBlock

    data class Table(
        val headers: List<String>,
        val rows: List<List<String>>,
        val headerInlines: List<List<MarkdownInlineNode>>,
        val rowInlines: List<List<List<MarkdownInlineNode>>>,
    ) : MarkdownRenderBlock

    data class Code(
        val language: String,
        val highlighted: HighlightedCode,
    ) : MarkdownRenderBlock

    data class Mermaid(
        val source: String,
        val diagram: MermaidDiagram,
    ) : MarkdownRenderBlock

    data object ThematicBreak : MarkdownRenderBlock
}

object MarkdownRenderer {
    fun toRenderModel(document: MarkdownDocument): MarkdownRenderModel =
        MarkdownRenderModel(
            blocks =
                document.blocks.map { block ->
                    when (block) {
                        is MarkdownHeading -> MarkdownRenderBlock.Heading(block.level, block.text, block.inlines)
                        is MarkdownParagraph -> MarkdownRenderBlock.Paragraph(block.text, block.inlines)
                        is MarkdownListBlock -> MarkdownRenderBlock.ListBlock(block.items, block.ordered, block.itemInlines)
                        is MarkdownTaskListBlock -> MarkdownRenderBlock.TaskList(block.items)
                        is MarkdownBlockQuote -> MarkdownRenderBlock.BlockQuote(block.text, block.inlines)
                        is MarkdownTableBlock ->
                            MarkdownRenderBlock.Table(
                                headers = block.headers,
                                rows = block.rows,
                                headerInlines = block.headerInlines,
                                rowInlines = block.rowInlines,
                            )

                        is MarkdownCodeBlock ->
                            MarkdownRenderBlock.Code(
                                language = block.language,
                                highlighted = PaletteCodeHighlighter.highlight(block.content, block.language),
                            )

                        is MarkdownMermaidBlock ->
                            MarkdownRenderBlock.Mermaid(
                                source = block.source,
                                diagram = MermaidParser.parse(block.source),
                            )

                        MarkdownThematicBreak -> MarkdownRenderBlock.ThematicBreak
                    }
                },
        )
}
