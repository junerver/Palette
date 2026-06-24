package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object TomlLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        var multilineStringDelimiter: String? = null
        return lines.map { line ->
            val activeDelimiter = multilineStringDelimiter
            if (activeDelimiter != null) {
                val closes =
                    hasMultilineStringClose(line, 0, activeDelimiter, skipOpeningDelimiter = false)
                val end = scanMultilineStringSegment(line, 0, activeDelimiter, skipOpeningDelimiter = false)
                val tokens =
                    mutableListOf(
                        CodeToken(CodeTokenType.StringLiteral, line.substring(0, end)),
                    )
                if (closes) {
                    multilineStringDelimiter = null
                    if (end < line.length) {
                        tokens += highlightLine(line.substring(end))
                    }
                }
                tokens
            } else {
                val highlighted = highlightLine(line)
                multilineStringDelimiter = line.openTomlMultilineStringDelimiter()
                highlighted
            }
        }
    }

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0
        var inSection = false

        while (index < line.length) {
            val current = line[index]
            when {
                current.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                current == '#' -> {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    index = line.length
                }

                line.startsWith("[[", index) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "[[")
                    inSection = true
                    index += 2
                }

                line.startsWith("]]", index) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "]]")
                    inSection = false
                    index += 2
                }

                current == '"' || current == '\'' -> {
                    val delimiter = line.tripleQuoteDelimiterAt(index)
                    val end =
                        if (delimiter != null) {
                            scanMultilineStringSegment(line, index, delimiter, skipOpeningDelimiter = true)
                        } else {
                            scanQuotedString(line, index, current)
                        }
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current.isDigit() ||
                    ((current == '-' || current == '+') && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextTomlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isTomlBareKeyStart() -> {
                    val end = line.nextTomlBareKeyEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            inSection -> CodeTokenType.Type
                            text in TomlKeywords -> CodeTokenType.Keyword
                            line.nextNonWhitespace(end) == '=' -> CodeTokenType.Keyword
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current == '=' -> {
                    tokens += CodeToken(CodeTokenType.Operator, "=")
                    index += 1
                }

                current in TomlPunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                    inSection =
                        when (current) {
                            '[' -> true
                            ']' -> false
                            else -> inSection
                        }
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

    private val TomlPunctuation = setOf('[', ']', '{', '}', ',', '.')

    private fun String.openTomlMultilineStringDelimiter(): String? {
        var index = 0
        while (index < length) {
            val delimiter = tripleQuoteDelimiterAt(index)
            if (delimiter != null) {
                if (!hasMultilineStringClose(this, index, delimiter, skipOpeningDelimiter = true)) {
                    return delimiter
                }
                index = scanMultilineStringSegment(this, index, delimiter, skipOpeningDelimiter = true)
            } else {
                index += 1
            }
        }
        return null
    }
}
