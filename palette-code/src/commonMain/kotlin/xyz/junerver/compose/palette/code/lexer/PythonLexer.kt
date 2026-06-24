package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal class PythonLexer {
    private var multilineStringDelimiter: String? = null

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            multilineStringDelimiter?.let { delimiter ->
                val closes =
                    hasMultilineStringClose(line, index, delimiter, skipOpeningDelimiter = false)
                val end = scanMultilineStringSegment(line, index, delimiter, skipOpeningDelimiter = false)
                tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                if (closes) multilineStringDelimiter = null
                index = end
                continue
            }

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

                current == '@' -> {
                    val end = line.nextDottedIdentifierEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                current.isPythonStringPrefixStart() && line.getOrNull(index + 1) in setOf('"', '\'') -> {
                    val isFString = current == 'f' || current == 'F'
                    val quoteIndex = index + 1
                    val delimiter = line.tripleQuoteDelimiterAt(quoteIndex)
                    val end =
                        if (delimiter != null) {
                            val segmentEnd =
                                scanMultilineStringSegment(line, quoteIndex, delimiter, skipOpeningDelimiter = true)
                            if (!hasMultilineStringClose(line, quoteIndex, delimiter, skipOpeningDelimiter = true)) {
                                multilineStringDelimiter = delimiter
                            }
                            segmentEnd
                        } else {
                            scanQuotedString(line, quoteIndex, line[quoteIndex])
                        }
                    val text = line.substring(index, end)
                    if (isFString && delimiter == null) {
                        tokens += tokenizePythonFString(text)
                    } else {
                        tokens += CodeToken(CodeTokenType.StringLiteral, text)
                    }
                    index = end
                }

                current == '"' || current == '\'' -> {
                    val delimiter = line.tripleQuoteDelimiterAt(index)
                    val end =
                        if (delimiter != null) {
                            val segmentEnd =
                                scanMultilineStringSegment(line, index, delimiter, skipOpeningDelimiter = true)
                            if (!hasMultilineStringClose(line, index, delimiter, skipOpeningDelimiter = true)) {
                                multilineStringDelimiter = delimiter
                            }
                            segmentEnd
                        } else {
                            scanQuotedString(line, index, current)
                        }
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current.isDigit() -> {
                    val end = line.nextPythonNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isIdentifierStart() -> {
                    val end = line.nextIdentifierEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            text in PythonKeywords -> CodeTokenType.Keyword
                            text in PythonPrimitiveTypes -> CodeTokenType.Type
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            text.first().isUpperCase() -> CodeTokenType.Type
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current in PythonOperators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in PythonPunctuation -> {
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

    private fun tokenizePythonFString(text: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        val current = StringBuilder()
        var index = 0

        fun flushString() {
            if (current.isNotEmpty()) {
                tokens += CodeToken(CodeTokenType.StringLiteral, current.toString())
                current.clear()
            }
        }

        while (index < text.length) {
            val char = text[index]
            when {
                char == '\\' -> {
                    current.append(char)
                    text.getOrNull(index + 1)?.let { escaped ->
                        current.append(escaped)
                        index += 1
                    }
                    index += 1
                }

                char == '{' && text.getOrNull(index + 1) == '{' -> {
                    current.append("{{")
                    index += 2
                }

                char == '}' && text.getOrNull(index + 1) == '}' -> {
                    current.append("}}")
                    index += 2
                }

                char == '{' -> {
                    val end = text.indexOf('}', startIndex = index + 1)
                    if (end == -1) {
                        current.append(char)
                        index += 1
                    } else {
                        flushString()
                        tokens += CodeToken(CodeTokenType.Operator, "{")
                        val expression = text.substring(index + 1, end).trim()
                        if (expression.isNotEmpty()) {
                            tokens += CodeToken(CodeTokenType.Annotation, expression)
                        }
                        tokens += CodeToken(CodeTokenType.Operator, "}")
                        index = end + 1
                    }
                }

                else -> {
                    current.append(char)
                    index += 1
                }
            }
        }

        flushString()
        return tokens
    }

    private val PythonOperators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '^', '~', ':')
    private val PythonPunctuation = setOf('(', ')', '[', ']', '{', '}', '.', ',', ';')
}
