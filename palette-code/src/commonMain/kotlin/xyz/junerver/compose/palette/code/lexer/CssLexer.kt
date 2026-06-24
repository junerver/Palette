package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal class CssLexer {
    private var inBlockComment = false

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
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

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '@' -> {
                    val end = line.nextCssIdentifierEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                current == '.' && line.getOrNull(index + 1)?.isCssIdentifierStart() == true -> {
                    val end = line.nextCssIdentifierEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Type, line.substring(index, end))
                    index = end
                }

                current == '#' && line.getOrNull(index + 1)?.isCssIdentifierStart() == true -> {
                    val end = line.nextCssIdentifierEnd(index + 1)
                    val text = line.substring(index, end)
                    tokens += CodeToken(if (text.isHexColorToken()) CodeTokenType.NumberLiteral else CodeTokenType.Type, text)
                    index = end
                }

                current.isDigit() -> {
                    val end = line.nextCssNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isCssIdentifierStart() -> {
                    val end = line.nextCssIdentifierEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            line.nextNonWhitespace(end) == ':' -> CodeTokenType.Keyword
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            text in CssValueKeywords -> CodeTokenType.Keyword
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current in CssOperators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in CssPunctuation -> {
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

    private companion object {
        val CssOperators = setOf(':', '>', '+', '~', '=', '|')
        val CssPunctuation = setOf('{', '}', '(', ')', '[', ']', ',', ';')
    }
}
