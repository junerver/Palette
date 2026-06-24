package xyz.junerver.compose.palette.markdown

import xyz.junerver.compose.palette.code.HighlightedCode
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.code.PaletteCodeDiagnostic
import xyz.junerver.compose.palette.code.PaletteCodeDiagnosticSeverity
import xyz.junerver.compose.palette.mermaid.MermaidDiagram
import xyz.junerver.compose.palette.mermaid.MermaidDiagnosticSeverity
import xyz.junerver.compose.palette.mermaid.MermaidParser
import xyz.junerver.compose.palette.mermaid.MermaidParseDiagnostic

data class MarkdownDocument(
    val blocks: List<MarkdownBlock>,
)

data class MarkdownSourceRange(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
    val source: String,
)

sealed interface MarkdownBlock

data class MarkdownHeading(
    val level: Int,
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownParagraph(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownListBlock(
    val items: List<String>,
    val ordered: Boolean,
    val itemInlines: List<List<MarkdownInlineNode>> = items.map(MarkdownInlineParser::parse),
    val startNumber: Int = 1,
    val tight: Boolean = true,
    val listItems: List<MarkdownListItem> =
        items.mapIndexed { index, item ->
            MarkdownListItem(
                text = item,
                inlines = itemInlines.getOrElse(index) { MarkdownInlineParser.parse(item) },
            )
        },
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownListItem(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val children: List<MarkdownBlock> = emptyList(),
    val taskChecked: Boolean? = null,
)

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
    val children: List<MarkdownBlock> = emptyList(),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownTableBlock(
    val headers: List<String>,
    val rows: List<List<String>>,
    val alignments: List<MarkdownTableAlignment> = List(headers.size) { MarkdownTableAlignment.Start },
    val headerInlines: List<List<MarkdownInlineNode>> = headers.map(MarkdownInlineParser::parse),
    val rowInlines: List<List<List<MarkdownInlineNode>>> = rows.map { row -> row.map(MarkdownInlineParser::parse) },
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

enum class MarkdownTableAlignment {
    Start,
    Center,
    End,
}

data class MarkdownCodeBlock(
    val language: String,
    val content: String,
    val title: String? = null,
    val showLineNumbers: Boolean = false,
    val highlightedLines: Set<Int> = emptySet(),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownMermaidBlock(
    val source: String,
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownHtmlBlock(
    val html: String,
    val sourceRange: MarkdownSourceRange? = null,
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
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

data class MarkdownInlineEmphasis(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

data class MarkdownInlineStrikethrough(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

data class MarkdownInlineCode(
    override val text: String,
) : MarkdownInlineNode

data object MarkdownInlineHardBreak : MarkdownInlineNode {
    override val text: String = "\n"
}

data object MarkdownInlineSoftBreak : MarkdownInlineNode {
    override val text: String = " "
}

data class MarkdownInlineHtml(
    val html: String,
) : MarkdownInlineNode {
    override val text: String
        get() = html
}

data class MarkdownInlineLink(
    val label: String,
    val destination: String,
    val title: String? = null,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(label)),
) : MarkdownInlineNode {
    override val text: String
        get() = label
}

data class MarkdownInlineImage(
    val alt: String,
    val destination: String,
    val title: String? = null,
) : MarkdownInlineNode {
    override val text: String
        get() = alt
}

internal data class MarkdownLinkTarget(
    val destination: String,
    val title: String? = null,
)

object MarkdownInlineParser {
    fun parse(source: String): List<MarkdownInlineNode> {
        return parseResolvedReferences(source, emptyMap())
    }

    fun parse(
        source: String,
        references: Map<String, String>,
    ): List<MarkdownInlineNode> {
        return parseResolvedReferences(
            source = source,
            references = references.mapValues { (_, destination) -> MarkdownLinkTarget(destination = destination) },
        )
    }

    internal fun parseWithTargets(
        source: String,
        references: Map<String, MarkdownLinkTarget>,
    ): List<MarkdownInlineNode> = parseResolvedReferences(source, references)

    private fun parseResolvedReferences(
        source: String,
        references: Map<String, MarkdownLinkTarget>,
    ): List<MarkdownInlineNode> {
        val nodes = mutableListOf<MarkdownInlineNode>()
        val plain = StringBuilder()
        var index = 0

        fun flushPlain() {
            if (plain.isNotEmpty()) {
                nodes += MarkdownInlineText(plain.toString().decodeMarkdownEntities())
                plain.clear()
            }
        }

        while (index < source.length) {
            when {
                source[index] == '\n' -> {
                    when {
                        plain.endsWith("\\") -> {
                            plain.setLength(plain.length - 1)
                            flushPlain()
                            nodes += MarkdownInlineHardBreak
                        }

                        plain.endsWith("  ") -> {
                            while (plain.isNotEmpty() && plain.last() == ' ') {
                                plain.setLength(plain.length - 1)
                            }
                            flushPlain()
                            nodes += MarkdownInlineHardBreak
                        }

                        else -> {
                            flushPlain()
                            nodes += MarkdownInlineSoftBreak
                        }
                    }
                    index += 1
                }

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
                        val destinationEnd = source.findInlineLinkDestinationEnd(destinationStart)
                        val target =
                            if (destinationEnd != -1) {
                                source.substring(destinationStart + 1, destinationEnd).toLinkTarget()
                            } else {
                                null
                            }
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineImage(
                                    alt = source.substring(index + 2, altEnd),
                                    destination = target.destination,
                                    title = target.title,
                                )
                            index = destinationEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else if (
                        altEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '['
                    ) {
                        val referenceEnd = source.indexOf(']', startIndex = destinationStart + 1)
                        val alt = source.substring(index + 2, altEnd)
                        val referenceLabel =
                            if (referenceEnd != -1) source.substring(destinationStart + 1, referenceEnd).ifEmpty { alt } else ""
                        val target = references[referenceLabel.normalizedReferenceLabel()]
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineImage(
                                    alt = alt,
                                    destination = target.destination,
                                    title = target.title,
                                )
                            index = referenceEnd + 1
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
                        val text = source.substring(index + 2, end)
                        flushPlain()
                        nodes +=
                            MarkdownInlineStrikethrough(
                                text = text,
                                children = parseResolvedReferences(text, references),
                            )
                        index = end + 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '*', length = 3) -> {
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 3,
                        delimiter = '*',
                        length = 3,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 3, end)
                        flushPlain()
                        val inner = parseResolvedReferences(text, references)
                        nodes +=
                            MarkdownInlineStrong(
                                text = text,
                                children = listOf(
                                    MarkdownInlineEmphasis(
                                        text = text,
                                        children = inner,
                                    ),
                                ),
                            )
                        index = end + 3
                    } else if (source.isInlineDelimiterRun(index, '*', length = 2)) {
                        val end2 = source.findInlineDelimiterRun(
                            startIndex = index + 2,
                            delimiter = '*',
                            length = 2,
                        )
                        if (end2 != -1) {
                            val text = source.substring(index + 2, end2)
                            flushPlain()
                            nodes +=
                                MarkdownInlineStrong(
                                    text = text,
                                    children = parseResolvedReferences(text, references),
                                )
                            index = end2 + 2
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '_', length = 3) -> {
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 3,
                        delimiter = '_',
                        length = 3,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 3, end)
                        flushPlain()
                        val inner = parseResolvedReferences(text, references)
                        nodes +=
                            MarkdownInlineStrong(
                                text = text,
                                children = listOf(
                                    MarkdownInlineEmphasis(
                                        text = text,
                                        children = inner,
                                    ),
                                ),
                            )
                        index = end + 3
                    } else if (source.isInlineDelimiterRun(index, '_', length = 2)) {
                        val end2 = source.findInlineDelimiterRun(
                            startIndex = index + 2,
                            delimiter = '_',
                            length = 2,
                        )
                        if (end2 != -1) {
                            val text = source.substring(index + 2, end2)
                            flushPlain()
                            nodes +=
                                MarkdownInlineStrong(
                                    text = text,
                                    children = parseResolvedReferences(text, references),
                                )
                            index = end2 + 2
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '*', length = 2) ||
                    source.isInlineDelimiterRun(index, '_', length = 2) -> {
                    val delimiter = source[index]
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 2,
                        delimiter = delimiter,
                        length = 2,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 2, end)
                        flushPlain()
                        nodes +=
                            MarkdownInlineStrong(
                                text = text,
                                children = parseResolvedReferences(text, references),
                            )
                        index = end + 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '*', length = 1) ||
                    source.isInlineDelimiterRun(index, '_', length = 1) -> {
                    val delimiter = source[index]
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 1,
                        delimiter = delimiter,
                        length = 1,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 1, end)
                        flushPlain()
                        nodes +=
                            MarkdownInlineEmphasis(
                                text = text,
                                children = parseResolvedReferences(text, references),
                            )
                        index = end + 1
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '`' -> {
                    val delimiterLength = source.countRepeatedFrom(index, '`')
                    val end = source.findMatchingBacktickRun(index + delimiterLength, delimiterLength)
                    if (end != -1) {
                        flushPlain()
                        nodes += MarkdownInlineCode(source.substring(index + delimiterLength, end).normalizedCodeSpan())
                        index = end + delimiterLength
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
                        val htmlEnd = source.findInlineHtmlEnd(index)
                        if (htmlEnd != -1) {
                            flushPlain()
                            nodes += MarkdownInlineHtml(source.substring(index, htmlEnd))
                            index = htmlEnd
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    }
                }

                source.startsBareAutolink(index) -> {
                    val autolink = source.bareAutolinkAt(index)
                    if (autolink != null) {
                        flushPlain()
                        nodes += autolink
                        index += autolink.destination.length
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
                        val destinationEnd = source.findInlineLinkDestinationEnd(destinationStart)
                        val target =
                            if (destinationEnd != -1) {
                                source.substring(destinationStart + 1, destinationEnd).toLinkTarget()
                            } else {
                                null
                            }
                        if (target != null) {
                            val label = source.substring(index + 1, labelEnd)
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = label,
                                    destination = target.destination,
                                    title = target.title,
                                    children = parseResolvedReferences(label, references),
                                )
                            index = destinationEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else if (
                        labelEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '['
                    ) {
                        val referenceEnd = source.indexOf(']', startIndex = destinationStart + 1)
                        val label = source.substring(index + 1, labelEnd)
                        val referenceLabel =
                            if (referenceEnd != -1) source.substring(destinationStart + 1, referenceEnd).ifEmpty { label } else ""
                        val target = references[referenceLabel.normalizedReferenceLabel()]
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = label,
                                    destination = target.destination,
                                    title = target.title,
                                    children = parseResolvedReferences(label, references),
                                )
                            index = referenceEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else if (labelEnd != -1) {
                        val label = source.substring(index + 1, labelEnd)
                        val target = references[label.normalizedReferenceLabel()]
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = label,
                                    destination = target.destination,
                                    title = target.title,
                                    children = parseResolvedReferences(label, references),
                                )
                            index = labelEnd + 1
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

    private fun String.toLinkTarget(): MarkdownLinkTarget? {
        val source = trim()
        if (source.isEmpty()) return null
        val (destinationSource, titleSource) =
            if (source.startsWith("<")) {
                val destinationEnd = source.indexOf('>')
                if (destinationEnd == -1) return null
                source.substring(1, destinationEnd) to source.substring(destinationEnd + 1).trim()
            } else {
                // Find destination end respecting balanced parentheses
                var depth = 0
                var index = 0
                var lastSpace = -1
                while (index < source.length) {
                    val char = source[index]
                    when {
                        char == '(' -> depth++
                        char == ')' -> depth--
                        char.isWhitespace() && depth == 0 && lastSpace == -1 -> lastSpace = index
                    }
                    index++
                }
                val destinationEnd = if (lastSpace != -1) lastSpace else source.length
                if (destinationEnd == source.length) {
                    source to ""
                } else {
                    source.substring(0, destinationEnd) to source.substring(destinationEnd + 1).trim()
                }
            }
        val destination = destinationSource.trimReferenceDestination().takeIf { it.isNotEmpty() } ?: return null
        return MarkdownLinkTarget(destination = destination, title = titleSource.toLinkTitle())
    }

    private fun String.toLinkTitle(): String? {
        val source = trim()
        if (source.length < 2) return null
        val quote = source.first()
        val endQuote =
            when (quote) {
                '"', '\'' -> quote
                '(' -> ')'
                else -> return null
            }
        if (source.last() != endQuote) return null
        return source.drop(1).dropLast(1).trim()
    }

    private fun String.findInlineHtmlEnd(start: Int): Int {
        val openingEnd = indexOf('>', startIndex = start + 1)
        if (openingEnd == -1) return -1
        val opening = substring(start, openingEnd + 1)
        val tagMatch = InlineHtmlOpeningTagRegex.matchEntire(opening) ?: return -1
        if (opening.startsWith("</") || opening.endsWith("/>")) return openingEnd + 1
        val tagName = tagMatch.groupValues[1]
        if (tagName.lowercase() in VoidHtmlTags) return openingEnd + 1
        val closing = "</$tagName>"
        val closingStart = indexOf(closing, startIndex = openingEnd + 1, ignoreCase = true)
        return if (closingStart == -1) openingEnd + 1 else closingStart + closing.length
    }

    private fun String.findInlineLinkDestinationEnd(openingParenIndex: Int): Int {
        var index = openingParenIndex + 1
        var nestedParentheses = 0
        var quote: Char? = null
        var escaped = false
        while (index < length) {
            val char = this[index]
            when {
                escaped -> escaped = false
                char == '\\' -> escaped = true
                quote != null -> if (char == quote) quote = null
                char == '"' || char == '\'' -> quote = char
                char == '(' -> nestedParentheses += 1
                char == ')' && nestedParentheses > 0 -> nestedParentheses -= 1
                char == ')' -> return index
            }
            index += 1
        }
        return -1
    }

    private fun String.trimReferenceDestination(): String =
        trim().trim('<', '>')

    private fun String.startsBareAutolink(index: Int): Boolean {
        if (getOrNull(index - 1)?.isBareAutolinkBoundary() == false) return false
        if (startsWith("http://", index) || startsWith("https://", index) || startsWith("www.", index)) return true
        return bareAutolinkCandidateAt(index).trimBareAutolinkEnd().matches(AutolinkEmailRegex)
    }

    private fun String.bareAutolinkAt(index: Int): MarkdownInlineLink? {
        val candidate = bareAutolinkCandidateAt(index)
        val url = candidate.trimBareAutolinkEnd()
        return url
            .takeIf { it.isNotEmpty() }
            ?.let { 
                val destination = if (it.startsWith("www.")) "http://$it" else it
                MarkdownInlineLink(label = it, destination = destination)
            }
    }

    private fun String.bareAutolinkCandidateAt(index: Int): String {
        var end = index
        while (end < length && !this[end].isWhitespace() && this[end] != '<') {
            end += 1
        }
        return substring(index, end)
    }

    private fun String.trimBareAutolinkEnd(): String {
        var value = trimEnd { it in BareAutolinkTrailingPunctuation }
        while (value.endsWith(")") && value.count { it == ')' } > value.count { it == '(' }) {
            value = value.dropLast(1)
        }
        return value
    }

    private val AutolinkUrlRegex = Regex("""https?://[^\s<>]+""")
    private val AutolinkEmailRegex = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
    private val BareWwwAutolinkRegex = Regex("""www\.[A-Za-z0-9.-]+\.[A-Za-z]{2,}[^\s<>]*""")
    private val InlineHtmlOpeningTagRegex = Regex("""</?\s*([A-Za-z][A-Za-z0-9-]*)(?:\s+[^<>]*)?/?>""")
    private val VoidHtmlTags = setOf("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr")
    private val BareAutolinkTrailingPunctuation = setOf('.', ',', ';', ':', '!', '?')
    private val EscapableMarkdownChars = setOf('\\', '`', '*', '_', '{', '}', '[', ']', '(', ')', '#', '+', '-', '.', '!', '|', '~')

    private fun Char.isBareAutolinkBoundary(): Boolean =
        isWhitespace() || this in setOf('(', '[', '{', '<')

    private fun String.decodeMarkdownEntities(): String =
        replace(MarkdownEntityRegex) { match ->
            val body = match.groupValues[1].ifEmpty { match.groupValues[2] }
            when {
                body.startsWith("#x", ignoreCase = true) ->
                    body.drop(2).toIntOrNull(radix = 16)?.toChar()?.toString() ?: match.value
                body.startsWith("#") ->
                    body.drop(1).toIntOrNull()?.toChar()?.toString() ?: match.value
                else -> MarkdownNamedEntities[body] ?: match.value
            }
        }

    private val MarkdownEntityRegex = Regex("""&([A-Za-z][A-Za-z0-9]+|#[0-9]+|#x[0-9A-Fa-f]+);""")
    private val MarkdownNamedEntities = mapOf(
        "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"",
        "apos" to "'", "nbsp" to "\u00A0",
        "copy" to "\u00A9", "reg" to "\u00AE", "trade" to "\u2122",
        "mdash" to "\u2014", "ndash" to "\u2013", "hellip" to "\u2026",
        "laquo" to "\u00AB", "raquo" to "\u00BB",
        "ldquo" to "\u201C", "rdquo" to "\u201D",
        "lsquo" to "\u2018", "rsquo" to "\u2019",
        "bull" to "\u2022", "middot" to "\u00B7",
        "ensp" to "\u2002", "emsp" to "\u2003", "thinsp" to "\u2009",
        "cent" to "\u00A2", "pound" to "\u00A3", "yen" to "\u00A5",
        "euro" to "\u20AC", "sect" to "\u00A7", "para" to "\u00B6",
        "larr" to "\u2190", "rarr" to "\u2192", "uarr" to "\u2191", "darr" to "\u2193",
        "harr" to "\u2194", "crarr" to "\u21B5",
        "times" to "\u00D7", "divide" to "\u00F7",
        "plusmn" to "\u00B1", "micro" to "\u00B5", "deg" to "\u00B0",
    )

    private fun String.normalizedReferenceLabel(): String =
        trim()
            .replace(WhitespaceRegex, " ")
            .lowercase()

    private fun String.isInlineDelimiterRun(
        index: Int,
        delimiter: Char,
        length: Int,
    ): Boolean {
        // Must have enough characters
        if (index + length > this.length) return false
        // Must all be the delimiter character
        if (!substring(index, index + length).all { it == delimiter }) return false
        // Must not be part of a longer delimiter run
        if (getOrNull(index - 1) == delimiter) return false
        if (getOrNull(index + length) == delimiter) return false
        
        val before = getOrNull(index - 1)
        val after = getOrNull(index + length)
        val beforeIsWhitespace = before?.isWhitespace() ?: true
        val afterIsWhitespace = after?.isWhitespace() ?: true
        val beforeIsPunctuation = before != null && before.isPunctuation()
        val afterIsPunctuation = after != null && after.isPunctuation()
        
        // Left-flanking: not followed by whitespace, and (not followed by punctuation OR preceded by whitespace or punctuation)
        val isLeftFlanking = !afterIsWhitespace && (!afterIsPunctuation || beforeIsWhitespace || beforeIsPunctuation)
        // Right-flanking: not preceded by whitespace, and (not preceded by punctuation OR followed by whitespace or punctuation)
        val isRightFlanking = !beforeIsWhitespace && (!beforeIsPunctuation || afterIsWhitespace || afterIsPunctuation)
        
        return when (delimiter) {
            '*' -> isLeftFlanking || isRightFlanking
            '_' -> (isLeftFlanking && (!isRightFlanking || beforeIsPunctuation)) ||
                   (isRightFlanking && (!isLeftFlanking || afterIsPunctuation))
            else -> true
        }
    }
    
    private fun Char.isPunctuation(): Boolean =
        !isLetterOrDigit() && !isWhitespace()

    private fun String.findInlineDelimiterRun(
        startIndex: Int,
        delimiter: Char,
        length: Int,
    ): Int {
        var candidate = startIndex
        while (candidate < this.length) {
            val index = indexOf(delimiter, startIndex = candidate)
            if (index == -1) return -1
            if (isInlineDelimiterRun(index, delimiter, length)) return index
            candidate = index + 1
        }
        return -1
    }

    private fun String.normalizedCodeSpan(): String {
        val normalized = replace(Regex("""\s+"""), " ")
        return if (
            normalized.length >= 2 &&
            normalized.first() == ' ' &&
            normalized.last() == ' ' &&
            normalized.any { !it.isWhitespace() }
        ) {
            normalized.drop(1).dropLast(1)
        } else {
            normalized
        }
    }

    private fun String.findMatchingBacktickRun(
        startIndex: Int,
        length: Int,
    ): Int {
        var candidate = startIndex
        while (candidate < this.length) {
            val index = indexOf('`', startIndex = candidate)
            if (index == -1) return -1
            if (countRepeatedFrom(index, '`') == length) return index
            candidate = index + 1
        }
        return -1
    }

    private fun String.countRepeatedFrom(
        start: Int,
        char: Char,
    ): Int {
        var index = start
        while (index < length && this[index] == char) index += 1
        return index - start
    }

    private val WhitespaceRegex = Regex("""\s+""")
}

object MarkdownParser {
    fun parse(source: String): MarkdownDocument {
        val lines = source.lines()
        val references = collectReferenceDefinitions(lines)
        val blocks = mutableListOf<MarkdownBlock>()
        var index = 0

        while (index < lines.size) {
            val line = lines[index]
            val trimmed = line.trim()
            val fence = trimmed.toFenceStart()
            when {
                trimmed.isEmpty() -> index += 1
                line.isIndentedCodeLine() -> {
                    val content = mutableListOf<String>()
                    while (index < lines.size && (lines[index].isIndentedCodeLine() || lines[index].trim().isEmpty())) {
                        content +=
                            if (lines[index].trim().isEmpty()) {
                                ""
                            } else {
                                lines[index].withoutCodeIndent()
                            }
                        index += 1
                    }
                    while (content.lastOrNull()?.isEmpty() == true) {
                        content.removeAt(content.lastIndex)
                    }
                    blocks += MarkdownCodeBlock(language = "plain", content = content.joinToString("\n"))
                }

                trimmed.toReferenceDefinition() != null -> index += 1
                fence != null -> {
                    val fenceInfo = CodeFenceInfo.parse(fence.info)
                    val content = mutableListOf<String>()
                    index += 1
                    while (index < lines.size && !lines[index].trim().isFenceEnd(fence)) {
                        content += lines[index]
                        index += 1
                    }
                    if (index < lines.size) index += 1
                    val blockContent = content.joinToString("\n")
                    blocks +=
                        if (fenceInfo.language == "mermaid") {
                            MarkdownMermaidBlock(blockContent)
                        } else {
                            MarkdownCodeBlock(
                                language = fenceInfo.language,
                                content = blockContent,
                                title = fenceInfo.title,
                                showLineNumbers = fenceInfo.showLineNumbers,
                                highlightedLines = fenceInfo.highlightedLines,
                            )
                        }
                }

                trimmed.startsWith(">") -> {
                    val quoteLines = mutableListOf<String>()
                    while (index < lines.size && lines[index].trim().startsWith(">")) {
                        quoteLines += lines[index].trim().removePrefix(">").trim()
                        index += 1
                    }
                    val nestedContent = quoteLines.joinToString("\n")
                    val nestedDoc = MarkdownParser.parse(nestedContent)
                    val firstParagraph = nestedDoc.blocks.filterIsInstance<MarkdownParagraph>().firstOrNull()
                    val text = firstParagraph?.text ?: ""
                    blocks += MarkdownBlockQuote(
                        text = text,
                        inlines = MarkdownInlineParser.parseWithTargets(text, references),
                        children = nestedDoc.blocks,
                    )
                }

                trimmed.isHtmlBlockLine() -> {
                    val start = index
                    val html = mutableListOf<String>()
                    while (index < lines.size && (lines[index].trim().isHtmlBlockLine() || lines[index].trim().isEmpty())) {
                        html += lines[index]
                        index += 1
                    }
                    while (html.lastOrNull()?.isEmpty() == true) {
                        html.removeAt(html.lastIndex)
                    }
                    blocks += MarkdownHtmlBlock(html = html.joinToString("\n"))
                }

                trimmed.isTaskListItem() -> {
                    val items = mutableListOf<MarkdownTaskItem>()
                    while (index < lines.size && lines[index].trim().isTaskListItem()) {
                        val match = TaskListRegex.matchEntire(lines[index].trim())
                        if (match != null) {
                            val text = match.groupValues[3].trim()
                            items +=
                                MarkdownTaskItem(
                                    text = text,
                                    checked = match.groupValues[2].equals("x", ignoreCase = true),
                                    inlines = MarkdownInlineParser.parseWithTargets(text, references),
                                )
                        }
                        index += 1
                    }
                    blocks += MarkdownTaskListBlock(items)
                }

                trimmed.isTableHeader(lines.getOrNull(index + 1)?.trim()) -> {
                    val headers = trimmed.tableCells()
                    val alignments = lines.getOrNull(index + 1).orEmpty().trim().tableAlignments(headers.size)
                    index += 2
                    val rows = mutableListOf<List<String>>()
                    while (index < lines.size && lines[index].trim().isTableRow()) {
                        val cells = lines[index].trim().tableCells()
                        // Normalize column count to match header
                        val normalizedCells = if (cells.size < headers.size) {
                            cells + List(headers.size - cells.size) { "" }
                        } else if (cells.size > headers.size) {
                            cells.take(headers.size)
                        } else {
                            cells
                        }
                        rows += normalizedCells
                        index += 1
                    }
                    blocks +=
                        MarkdownTableBlock(
                            headers = headers,
                            rows = rows,
                            alignments = alignments,
                            headerInlines = headers.map { MarkdownInlineParser.parseWithTargets(it, references) },
                            rowInlines = rows.map { row -> row.map { MarkdownInlineParser.parseWithTargets(it, references) } },
                        )
                }

                trimmed.canStartSetextHeading(lines.getOrNull(index + 1)?.trim()) -> {
                    val text = trimmed
                    blocks +=
                        MarkdownHeading(
                            level = lines[index + 1].trim().toSetextHeadingLevel(),
                            text = text,
                            inlines = MarkdownInlineParser.parseWithTargets(text, references),
                        )
                    index += 2
                }

                trimmed.matches(ThematicBreakRegex) -> {
                    blocks += MarkdownThematicBreak
                    index += 1
                }

                trimmed.startsWith("#") -> {
                    val match = HeadingRegex.matchEntire(trimmed)
                    if (match != null) {
                        val text = match.groupValues[2].trim().removeAtxClosingHashes()
                        blocks +=
                            MarkdownHeading(
                                level = match.groupValues[1].length.coerceIn(1, 6),
                                text = text,
                                inlines = MarkdownInlineParser.parseWithTargets(text, references),
                            )
                        index += 1
                    } else {
                        val paragraph = readParagraph(lines, index)
                        blocks +=
                            MarkdownParagraph(
                                text = paragraph.text,
                                inlines = MarkdownInlineParser.parseWithTargets(paragraph.text, references),
                            )
                        index = paragraph.nextIndex
                    }
                }

                trimmed.isListItem() -> {
                    val ordered = OrderedListRegex.matches(trimmed)
                    val startNumber = if (ordered) trimmed.orderedListStartNumber() else 1
                    val itemContents = mutableListOf<String>()
                    val itemChildren = mutableListOf<List<MarkdownBlock>>()
                    while (index < lines.size && lines[index].trim().isListItem() &&
                        !lines[index].trim().isTaskListItem()) {
                        val itemFirstLine = lines[index].trim().removeListMarker()
                        val continuationLines = mutableListOf<String>()
                        index += 1
                        // Collect continuation lines (indented lines that are not new list items)
                        while (index < lines.size) {
                            val nextLine = lines[index]
                            val nextTrimmed = nextLine.trim()
                            if (nextTrimmed.isEmpty()) {
                                // Blank line - check if next non-blank line continues the list item
                                val peekIndex = index + 1
                                if (peekIndex < lines.size) {
                                    val peekLine = lines[peekIndex]
                                    val peekTrimmed = peekLine.trim()
                                    if (peekTrimmed.isNotEmpty() && (peekLine.startsWith("  ") || peekLine.startsWith("\t")) && !peekTrimmed.isListItem()) {
                                        continuationLines += ""
                                        index += 1
                                        continue
                                    }
                                }
                                break
                            }
                            if ((nextTrimmed.isListItem() || nextTrimmed.isTaskListItem()) && !nextLine.startsWith("  ") && !nextLine.startsWith("\t")) {
                                break
                            }
                            if (nextLine.startsWith("  ") || nextLine.startsWith("\t")) {
                                continuationLines += nextLine.trimStart().let { if (it.startsWith("  ")) it.drop(2) else it }
                                index += 1
                            } else {
                                break
                            }
                        }
                        if (continuationLines.isNotEmpty()) {
                            val nestedContent = continuationLines.joinToString("\n")
                            val nestedDoc = MarkdownParser.parse(nestedContent)
                            itemChildren.add(nestedDoc.blocks)
                            itemContents += itemFirstLine
                        } else {
                            itemChildren.add(emptyList())
                            itemContents += itemFirstLine
                        }
                    }
                    blocks +=
                        MarkdownListBlock(
                            items = itemContents,
                            ordered = ordered,
                            itemInlines = itemContents.map { MarkdownInlineParser.parseWithTargets(it, references) },
                            startNumber = startNumber,
                            listItems = itemContents.mapIndexed { i, text ->
                                MarkdownListItem(
                                    text = text,
                                    inlines = MarkdownInlineParser.parseWithTargets(text, references),
                                    children = itemChildren[i],
                                )
                            },
                        )
                }

                else -> {
                    val paragraph = readParagraph(lines, index)
                    blocks +=
                        MarkdownParagraph(
                            text = paragraph.text,
                            inlines = MarkdownInlineParser.parseWithTargets(paragraph.text, references),
                        )
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
                lines[index].isIndentedCodeLine() ||
                trimmed.toFenceStart() != null ||
                trimmed.startsWith(">") ||
                HeadingRegex.matchEntire(trimmed) != null ||
                trimmed.matches(ThematicBreakRegex) ||
                trimmed.isTaskListItem() ||
                trimmed.isListItem() ||
                trimmed.toReferenceDefinition() != null
            ) {
                break
            }
            parts += trimmed
            index += 1
        }
        return ParagraphRead(text = parts.joinToString(" ").trim(), nextIndex = index)
    }

    private fun collectReferenceDefinitions(lines: List<String>): Map<String, MarkdownLinkTarget> {
        val definitions = mutableMapOf<String, MarkdownLinkTarget>()
        var index = 0
        while (index < lines.size) {
            val trimmed = lines[index].trim()
            val fence = trimmed.toFenceStart()
            if (lines[index].isIndentedCodeLine()) {
                index += 1
                while (index < lines.size && (lines[index].isIndentedCodeLine() || lines[index].trim().isEmpty())) {
                    index += 1
                }
            } else if (fence != null) {
                index += 1
                while (index < lines.size && !lines[index].trim().isFenceEnd(fence)) {
                    index += 1
                }
                if (index < lines.size) index += 1
            } else {
                val def = trimmed.toReferenceDefinition()
                if (def != null) {
                    var titleText = def.rawTitle.orEmpty()
                    var nextIndex = index + 1
                    // If no title on first line, check if next line starts a multi-line title
                    if (titleText.isEmpty() && nextIndex < lines.size) {
                        val nextTrimmed = lines[nextIndex].trim()
                        if (nextTrimmed.isNotEmpty() && nextTrimmed.first() in setOf('"', "'", '(')) {
                            titleText = nextTrimmed
                            nextIndex += 1
                        }
                    }
                    // Continue reading multi-line title
                    if (titleText.isNotEmpty() && titleText.first() in setOf('"', "'", '(') && !titleText.isCompleteTitle()) {
                        val continuation = StringBuilder(titleText)
                        while (nextIndex < lines.size) {
                            val nextLine = lines[nextIndex]
                            if (nextLine.trim().isEmpty()) break
                            continuation.append(" ").append(nextLine.trim())
                            nextIndex += 1
                            if (continuation.toString().isCompleteTitle()) break
                        }
                        titleText = continuation.toString()
                    }
                    val completeTitle = titleText.toLinkTitle()
                    definitions[def.label] = MarkdownLinkTarget(def.target.destination, completeTitle)
                    index = nextIndex
                } else {
                    index += 1
                }
            }
        }
        return definitions
    }

    private data class ParagraphRead(
        val text: String,
        val nextIndex: Int,
    )

    private val HeadingRegex = Regex("""^(#{1,6})\s+(.+)$""")
    private val ThematicBreakRegex = Regex("""^(-{3,}|\*{3,}|_{3,})$""")
    private val SetextHeadingUnderlineRegex = Regex("""^(=+|-+)$""")
    private val TaskListRegex = Regex("""^([-*+])\s+\[([ xX])]\s+(.+)$""")
    private val OrderedListRegex = Regex("""^(\d+)[.)]\s+.+$""")
    private val UnorderedListRegex = Regex("""^[-*+]\s+.+$""")
    private val ReferenceDefinitionRegex = Regex("""^\[([^\]]+)]:\s*(\S+)(?:\s+(.+))?$""")

    private fun String.canStartSetextHeading(nextLine: String?): Boolean =
        isNotEmpty() &&
            nextLine?.matches(SetextHeadingUnderlineRegex) == true &&
            HeadingRegex.matchEntire(this) == null &&
            !matches(ThematicBreakRegex) &&
            !isTaskListItem() &&
            !isListItem()

    private fun String.toSetextHeadingLevel(): Int =
        if (startsWith("=")) 1 else 2

    private fun String.removeAtxClosingHashes(): String =
        replace(AtxClosingHashesRegex, "").trim()

    private fun String.isListItem(): Boolean = OrderedListRegex.matches(this) || UnorderedListRegex.matches(this)

    private fun String.isTaskListItem(): Boolean = TaskListRegex.matches(this)

    private fun String.isHtmlBlockLine(): Boolean = HtmlBlockLineRegex.matches(this)

    private fun String.isIndentedCodeLine(): Boolean =
        startsWith("    ") || startsWith("\t")

    private fun String.withoutCodeIndent(): String =
        when {
            startsWith("\t") -> drop(1)
            startsWith("    ") -> drop(4)
            else -> this
        }

    private fun String.removeListMarker(): String =
        replace(Regex("""^(\d+[.)]|[-*+])\s+"""), "").trim()

    private fun String.orderedListStartNumber(): Int =
        OrderedListRegex.matchEntire(this)?.groupValues?.get(1)?.toIntOrNull() ?: 1

    private fun String.toReferenceDefinition(): MarkdownReferenceDefinition? {
        val match = ReferenceDefinitionRegex.matchEntire(this) ?: return null
        val label = match.groupValues[1].normalizedReferenceLabel().takeIf { it.isNotEmpty() } ?: return null
        val destination = match.groupValues[2].trimReferenceDestination().takeIf { it.isNotEmpty() } ?: return null
        val rawTitle = match.groupValues.getOrNull(3).orEmpty()
        val title = rawTitle.toLinkTitle()
        return MarkdownReferenceDefinition(label = label, target = MarkdownLinkTarget(destination, title), rawTitle = rawTitle)
    }

    private fun String.normalizedReferenceLabel(): String =
        trim()
            .replace(WhitespaceRegex, " ")
            .lowercase()

    private fun String.trimReferenceDestination(): String =
        trim().trim('<', '>')

    private fun String.toLinkTitle(): String? {
        val source = trim()
        if (source.length < 2) return null
        val quote = source.first()
        val endQuote =
            when (quote) {
                '"', '\'' -> quote
                '(' -> ')'
                else -> return null
            }
        if (source.last() != endQuote) return null
        return source.drop(1).dropLast(1).trim()
    }

    private fun String.isTableHeader(nextLine: String?): Boolean =
        isTableRow() && nextLine?.matches(TableDelimiterRegex) == true

    private fun String.isTableRow(): Boolean = contains("|") && tableCells().any { it.isNotEmpty() }

    private fun String.tableCells(): List<String> {
        val source = trim().trimTableBoundaryPipes()
        val cells = mutableListOf<String>()
        val current = StringBuilder()
        var index = 0
        var codeFenceLength = 0
        while (index < source.length) {
            when {
                source[index] == '\\' && source.getOrNull(index + 1) == '|' -> {
                    current.append('|')
                    index += 2
                }

                source[index] == '`' -> {
                    val length = source.countRepeatedFrom(index, '`')
                    if (codeFenceLength == 0) {
                        codeFenceLength = length
                    } else if (length == codeFenceLength) {
                        codeFenceLength = 0
                    }
                    current.append(source.substring(index, index + length))
                    index += length
                }

                source[index] == '|' && codeFenceLength == 0 -> {
                    cells += current.toString().trim()
                    current.clear()
                    index += 1
                }

                else -> {
                    current.append(source[index])
                    index += 1
                }
            }
        }
        cells += current.toString().trim()
        return cells
    }

    private fun String.tableAlignments(columnCount: Int): List<MarkdownTableAlignment> {
        val parsed =
            trim()
                .trim('|')
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { delimiter ->
                    when {
                        delimiter.startsWith(":") && delimiter.endsWith(":") -> MarkdownTableAlignment.Center
                        delimiter.endsWith(":") -> MarkdownTableAlignment.End
                        else -> MarkdownTableAlignment.Start
                    }
                }
        return parsed.normalizedAlignments(columnCount)
    }

    private fun List<MarkdownTableAlignment>.normalizedAlignments(columnCount: Int): List<MarkdownTableAlignment> {
        if (size == columnCount) return this
        if (size > columnCount) return take(columnCount)
        return this + List(columnCount - size) { MarkdownTableAlignment.Start }
    }

    private fun String.trimTableBoundaryPipes(): String {
        var start = 0
        var end = length
        if (getOrNull(start) == '|') start += 1
        if (end > start && getOrNull(end - 1) == '|') end -= 1
        return substring(start, end)
    }

    private fun String.countRepeatedFrom(
        start: Int,
        char: Char,
    ): Int {
        var index = start
        while (index < length && this[index] == char) index += 1
        return index - start
    }

    private val HtmlBlockLineRegex = Regex("""^</?\s*[A-Za-z][A-Za-z0-9-]*(?:\s+[^<>]*)?/?>.*$""")
    private val TableDelimiterRegex = Regex("""^\|?\s*:?-{3,}:?\s*(\|\s*:?-{3,}:?\s*)+\|?$""")
    private val AtxClosingHashesRegex = Regex("""\s+#+\s*$""")
    private val WhitespaceRegex = Regex("""\s+""")

    private data class MarkdownReferenceDefinition(
        val label: String,
        val target: MarkdownLinkTarget,
        val rawTitle: String = "",
    )
    
    private fun String.isCompleteTitle(): Boolean {
        val trimmed = trim()
        if (trimmed.length < 2) return false
        val quote = trimmed.first()
        val endQuote = when (quote) {
            '"' -> '"'
            '\'' -> '\''
            '(' -> ')'
            else -> return false
        }
        return trimmed.last() == endQuote && trimmed.length >= 2
    }

    private data class MarkdownFence(
        val marker: Char,
        val length: Int,
        val info: String,
    )

    private fun String.toFenceStart(): MarkdownFence? {
        val marker = firstOrNull()?.takeIf { it == '`' || it == '~' } ?: return null
        val length = takeWhile { it == marker }.length
        if (length < 3) return null
        return MarkdownFence(marker = marker, length = length, info = drop(length).trim())
    }

    private fun String.isFenceEnd(fence: MarkdownFence): Boolean {
        val length = takeWhile { it == fence.marker }.length
        return length >= fence.length && drop(length).trim().isEmpty()
    }

    private data class CodeFenceInfo(
        val language: String,
        val title: String?,
        val showLineNumbers: Boolean,
        val highlightedLines: Set<Int>,
    ) {
        companion object {
            fun parse(source: String): CodeFenceInfo {
                val language = source.substringBefore(' ').trim().lowercase().ifEmpty { "plain" }
                return CodeFenceInfo(
                    language = language,
                    title = TitleRegex.find(source)?.groupValues?.get(1)?.trim()?.ifEmpty { null },
                    showLineNumbers = source.contains("showLineNumbers", ignoreCase = true),
                    highlightedLines = HighlightLinesRegex.find(source)?.groupValues?.get(1).orEmpty().toHighlightedLines(),
                )
            }

            private val TitleRegex = Regex("""title="([^"]+)"""")
            private val HighlightLinesRegex = Regex("""\{([0-9,\-\s]+)}""")
        }
    }

    private fun String.toHighlightedLines(): Set<Int> =
        split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .flatMap { item ->
                val rangeParts = item.split('-', limit = 2).map { it.trim().toIntOrNull() }
                val start = rangeParts.getOrNull(0)
                val end = rangeParts.getOrNull(1)
                when {
                    start == null -> emptyList()
                    end == null -> listOf(start)
                    end >= start -> (start..end).toList()
                    else -> listOf(start)
                }
            }
            .filter { it > 0 }
            .toSet()
}

data class MarkdownRenderModel(
    val blocks: List<MarkdownRenderBlock>,
    val diagnostics: List<MarkdownRenderDiagnostic> = emptyList(),
)

data class MarkdownRenderDiagnostic(
    val code: MarkdownRenderDiagnosticCode,
    val message: String,
    val severity: MarkdownRenderDiagnosticSeverity = MarkdownRenderDiagnosticSeverity.Warning,
    val originCode: String? = null,
    val blockIndex: Int? = null,
    val line: Int? = null,
    val column: Int? = null,
    val endColumn: Int? = null,
    val source: String? = null,
)

enum class MarkdownRenderDiagnosticCode {
    CodeHighlighterDiagnostic,
    MermaidDiagnostic,
    MermaidParserFailure
}

enum class MarkdownRenderDiagnosticSeverity {
    Warning,
    Error
}

data class MarkdownRenderListItem(
    val text: String,
    val inlines: List<MarkdownInlineNode>,
    val children: List<MarkdownRenderBlock> = emptyList(),
    val taskChecked: Boolean? = null,
)

sealed interface MarkdownRenderBlock {
    data class Heading(
        val level: Int,
        val text: String,
        val inlines: List<MarkdownInlineNode>,
        val sourceRange: MarkdownSourceRange? = null,
        val id: String = "",
    ) : MarkdownRenderBlock

    data class Paragraph(
        val text: String,
        val inlines: List<MarkdownInlineNode>,
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class ListBlock(
        val items: List<String>,
        val ordered: Boolean,
        val itemInlines: List<List<MarkdownInlineNode>>,
        val startNumber: Int = 1,
        val tight: Boolean = true,
        val listItems: List<MarkdownRenderListItem> =
            items.mapIndexed { index, item ->
                MarkdownRenderListItem(
                    text = item,
                    inlines = itemInlines.getOrElse(index) { MarkdownInlineParser.parse(item) },
                )
            },
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class TaskList(
        val items: List<MarkdownTaskItem>,
    ) : MarkdownRenderBlock

    data class BlockQuote(
        val text: String,
        val inlines: List<MarkdownInlineNode>,
        val children: List<MarkdownRenderBlock> = emptyList(),
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Table(
        val headers: List<String>,
        val rows: List<List<String>>,
        val alignments: List<MarkdownTableAlignment>,
        val headerInlines: List<List<MarkdownInlineNode>>,
        val rowInlines: List<List<List<MarkdownInlineNode>>>,
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Code(
        val language: String,
        val highlighted: HighlightedCode,
        val title: String?,
        val showLineNumbers: Boolean,
        val highlightedLines: Set<Int>,
        val diagnostics: List<MarkdownRenderDiagnostic> = emptyList(),
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Mermaid(
        val source: String,
        val diagram: MermaidDiagram,
        val diagnostics: List<MarkdownRenderDiagnostic> = emptyList(),
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Html(
        val html: String,
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data object ThematicBreak : MarkdownRenderBlock
}

object MarkdownRenderer {
    fun toRenderModel(document: MarkdownDocument): MarkdownRenderModel =
        toRenderModel(document, mermaidParser = MermaidParser::parse)

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
                val id = if (count == 0) baseSlug else "\$baseSlug-\${count + 1}"
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
                            message = "Mermaid block could not be parsed: \${error.message ?: error::class.simpleName}",
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

    private fun String.toHeadingSlug(): String {
        val slug = trim().lowercase().replace(Regex("""[^\w\s-]"""), "").replace(Regex("""\s+"""), "-").trim('-')
        return slug.ifEmpty { "heading" }
    }
}
