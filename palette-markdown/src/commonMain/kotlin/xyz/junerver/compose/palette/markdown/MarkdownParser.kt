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
) : MarkdownBlock

data class MarkdownParagraph(
    val text: String,
) : MarkdownBlock

data class MarkdownListBlock(
    val items: List<String>,
    val ordered: Boolean,
) : MarkdownBlock

data class MarkdownCodeBlock(
    val language: String,
    val content: String,
) : MarkdownBlock

data class MarkdownMermaidBlock(
    val source: String,
) : MarkdownBlock

data object MarkdownThematicBreak : MarkdownBlock

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
    ) : MarkdownRenderBlock

    data class Paragraph(
        val text: String,
    ) : MarkdownRenderBlock

    data class ListBlock(
        val items: List<String>,
        val ordered: Boolean,
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
                        is MarkdownHeading -> MarkdownRenderBlock.Heading(block.level, block.text)
                        is MarkdownParagraph -> MarkdownRenderBlock.Paragraph(block.text)
                        is MarkdownListBlock -> MarkdownRenderBlock.ListBlock(block.items, block.ordered)
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
