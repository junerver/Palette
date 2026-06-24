package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal class SqlLexer {
    private var inBlockComment = false
    private var dollarQuoteDelimiter: String? = null

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            dollarQuoteDelimiter?.let { delimiter ->
                val end = line.indexOf(delimiter, startIndex = index)
                if (end == -1) {
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index))
                    return tokens
                }
                tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end + delimiter.length))
                dollarQuoteDelimiter = null
                index = end + delimiter.length
                continue
            }

            if (inBlockComment) {
                val end = line.indexOf("*/", startIndex = index)
                if (end == -1) {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    return tokens
                }
                tokens += CodeToken(CodeTokenType.Comment, line.substring(index, end + 2))
                inBlockComment = false
                index = end + 2
                continue
            }

            val current = line[index]
            when {
                current.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                line.startsWith("--", index) -> {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    index = line.length
                }

                line.startsWith("/*", index) -> {
                    val end = line.indexOf("*/", startIndex = index + 2)
                    if (end == -1) {
                        tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                        inBlockComment = true
                        index = line.length
                    } else {
                        tokens += CodeToken(CodeTokenType.Comment, line.substring(index, end + 2))
                        index = end + 2
                    }
                }

                current == '`' -> {
                    val end = line.indexOf('`', startIndex = index + 1)
                    val tokenEnd = if (end == -1) line.length else end + 1
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, tokenEnd))
                    index = tokenEnd
                }

                current == '$' -> {
                    val delimiter = line.sqlDollarQuoteDelimiterAt(index)
                    if (delimiter != null) {
                        val close = line.indexOf(delimiter, startIndex = index + delimiter.length)
                        if (close == -1) {
                            tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index))
                            dollarQuoteDelimiter = delimiter
                            index = line.length
                        } else {
                            tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, close + delimiter.length))
                            index = close + delimiter.length
                        }
                    } else {
                        tokens += CodeToken(CodeTokenType.Plain, current.toString())
                        index += 1
                    }
                }

                current == '\'' || current == '"' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current.isDigit() -> {
                    val end = line.nextSqlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isSqlIdentifierStart() -> {
                    val end = line.nextSqlIdentifierEnd(index)
                    val text = line.substring(index, end)
                    val normalized = text.uppercase()
                    val type =
                        when {
                            normalized in SqlTypes -> CodeTokenType.Type
                            normalized in SqlKeywords -> CodeTokenType.Keyword
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current in SqlOperators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in SqlPunctuation -> {
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

    private fun String.sqlDollarQuoteDelimiterAt(start: Int): String? {
        if (getOrNull(start) != '$') return null
        var index = start + 1
        while (index < length && (this[index].isLetterOrDigit() || this[index] == '_')) {
            index += 1
        }
        if (getOrNull(index) != '$') return null
        return substring(start, index + 1)
    }
}
