package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object YamlLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        var blockScalarParentIndent: Int? = null
        return lines.map { line ->
            val activeIndent = blockScalarParentIndent
            if (activeIndent != null) {
                if (line.trim().isEmpty() || line.leadingYamlIndentWidth() > activeIndent) {
                    listOf(CodeToken(CodeTokenType.StringLiteral, line))
                } else {
                    blockScalarParentIndent = null
                    val highlighted = highlightLine(line)
                    if (line.startsYamlBlockScalar()) {
                        blockScalarParentIndent = line.leadingYamlIndentWidth()
                    }
                    highlighted
                }
            } else {
                val highlighted = highlightLine(line)
                if (line.startsYamlBlockScalar()) {
                    blockScalarParentIndent = line.leadingYamlIndentWidth()
                }
                highlighted
            }
        }
    }

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            val current = line[index]
            when {
                line.trim() in YamlDocumentMarkers && index == line.indexOfFirst { !it.isWhitespace() } -> {
                    tokens += CodeToken(CodeTokenType.Operator, line.trim())
                    index = line.length
                }

                current.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                current == '#' -> {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    index = line.length
                }

                current == '%' && index == line.indexOfFirst { !it.isWhitespace() } -> {
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index))
                    index = line.length
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '!' -> {
                    val end = line.nextYamlTagEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                (current == '&' || current == '*') && line.getOrNull(index + 1)?.isYamlWordStart() == true -> {
                    val end = line.nextYamlWordEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                (current == '|' || current == '>') && line.isYamlBlockScalarIndicatorAt(index) -> {
                    val end = line.nextYamlBlockScalarIndicatorEnd(index)
                    tokens += CodeToken(CodeTokenType.Operator, line.substring(index, end))
                    index = end
                }

                current == '-' && line.getOrNull(index + 1)?.isWhitespace() == true -> {
                    tokens += CodeToken(CodeTokenType.Operator, "-")
                    index += 1
                }

                current.isDigit() || (current == '-' && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextYamlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isYamlWordStart() -> {
                    val end = line.nextYamlWordEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            line.nextNonWhitespace(end) == ':' -> CodeTokenType.Keyword
                            text in YamlKeywords -> CodeTokenType.Keyword
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current == ':' -> {
                    tokens += CodeToken(CodeTokenType.Operator, ":")
                    index += 1
                }

                current in YamlPunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                    index += 1
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, current.toString())
                    index += 1
                }
            }
        }

        return tokens
    }

    private val YamlDocumentMarkers = setOf("---", "...")
    private val YamlPunctuation = setOf('[', ']', '{', '}', ',')

    private fun String.startsYamlBlockScalar(): Boolean {
        val source = substringBefore('#').trimEnd()
        return YamlBlockScalarLineRegex.containsMatchIn(source)
    }

    private fun String.isYamlBlockScalarIndicatorAt(index: Int): Boolean {
        val source = substring(index)
        return YamlBlockScalarIndicatorRegex.find(source)?.range?.first == 0
    }

    private fun String.nextYamlBlockScalarIndicatorEnd(start: Int): Int {
        val match = YamlBlockScalarIndicatorRegex.find(substring(start)) ?: return start + 1
        return start + match.range.last + 1
    }

    private fun String.nextYamlTagEnd(start: Int): Int =
        nextWhile(start) { !it.isWhitespace() && it !in YamlPunctuation && it != '#' && it != ':' }

    private fun String.leadingYamlIndentWidth(): Int {
        var width = 0
        for (char in this) {
            when (char) {
                ' ' -> width += 1
                '\t' -> width += 4
                else -> return width
            }
        }
        return width
    }

    private val YamlBlockScalarLineRegex = Regex("""(?:^|\s|:\s*)[|>][+-]?\d?$""")
    private val YamlBlockScalarIndicatorRegex = Regex("""^[|>][+-]?\d?""")
}
