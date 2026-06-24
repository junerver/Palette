package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object GraphQlLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

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

                line.startsWith("\"\"\"", index) -> {
                    val close = line.indexOf("\"\"\"", startIndex = index + 3)
                    val end = if (close == -1) line.length else close + 3
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '"' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '$' -> {
                    val end = line.nextGraphQlVariableEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                current == '@' -> {
                    val end = line.nextGraphQlVariableEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                line.startsWith("...", index) -> {
                    tokens += CodeToken(CodeTokenType.Operator, "...")
                    index += 3
                }

                current in GraphQlPunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                    index += 1
                }

                current in GraphQlOperators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current.isDigit() || (current == '-' && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextGraphQlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isGraphQlNameStart() -> {
                    val end = line.nextGraphQlNameEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            text in GraphQlKeywords -> CodeTokenType.Keyword
                            text.firstOrNull()?.isUpperCase() == true -> CodeTokenType.Type
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, current.toString())
                    index += 1
                }
            }
        }

        return tokens
    }

    private val GraphQlPunctuation = setOf('{', '}', '(', ')', '[', ']', ':', ',', '=')
    private val GraphQlOperators = setOf('!', '|', '&')
}
