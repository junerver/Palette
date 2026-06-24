package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object JsonLexer {
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

                current == '"' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current.isDigit() || (current == '-' && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextJsonNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isIdentifierStart() -> {
                    val end = line.nextIdentifierEnd(index)
                    val text = line.substring(index, end)
                    tokens +=
                        CodeToken(
                            type = if (text in JsonKeywords) CodeTokenType.Keyword else CodeTokenType.Plain,
                            text = text,
                        )
                    index = end
                }

                current == ':' -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in JsonPunctuation -> {
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

    private val JsonKeywords = setOf("true", "false", "null")
    private val JsonPunctuation = setOf('{', '}', '[', ']', ',')
}
