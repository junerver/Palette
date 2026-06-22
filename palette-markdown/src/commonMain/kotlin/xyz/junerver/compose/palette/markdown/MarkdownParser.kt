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
                HeadingRegex.matchEntire(trimmed) != null ||
                trimmed.matches(ThematicBreakRegex) ||
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
    private val OrderedListRegex = Regex("""^\d+[.)]\s+.+$""")
    private val UnorderedListRegex = Regex("""^[-*+]\s+.+$""")

    private fun String.isListItem(): Boolean = OrderedListRegex.matches(this) || UnorderedListRegex.matches(this)

    private fun String.removeListMarker(): String =
        replace(Regex("""^(\d+[.)]|[-*+])\s+"""), "").trim()
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
