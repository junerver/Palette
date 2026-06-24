package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object IniPropertiesLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = line.nextWhile(0, Char::isWhitespace)
        if (index > 0) {
            tokens += CodeToken(CodeTokenType.Plain, line.substring(0, index))
        }
        if (index >= line.length) return tokens

        if (line[index] == '#' || line[index] == ';') {
            tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
            return tokens
        }

        if (line[index] == '[') {
            val close = line.indexOf(']', startIndex = index + 1)
            if (close != -1) {
                tokens += CodeToken(CodeTokenType.Punctuation, "[")
                tokens += CodeToken(CodeTokenType.Type, line.substring(index + 1, close))
                tokens += CodeToken(CodeTokenType.Punctuation, "]")
                if (close + 1 < line.length) {
                    tokens.addIniValueTokens(line, close + 1)
                }
                return tokens
            }
        }

        val separator = line.findIniPropertySeparator(index)
        if (separator != -1) {
            val keyEnd = line.substring(index, separator).trimEnd().let { index + it.length }
            if (keyEnd > index) {
                tokens += CodeToken(CodeTokenType.Keyword, line.substring(index, keyEnd))
            }
            if (keyEnd < separator) {
                tokens += CodeToken(CodeTokenType.Plain, line.substring(keyEnd, separator))
            }
            val separatorEnd =
                if (line[separator].isWhitespace()) {
                    line.nextWhile(separator, Char::isWhitespace)
                } else {
                    separator + 1
                }
            tokens += CodeToken(CodeTokenType.Operator, line.substring(separator, separatorEnd))
            tokens.addIniValueTokens(line, separatorEnd)
            return tokens
        }

        tokens.addIniValueTokens(line, index)
        return tokens
    }

    private fun MutableList<CodeToken>.addIniValueTokens(
        line: String,
        start: Int,
    ) {
        var index = start
        while (index < line.length) {
            val current = line[index]
            when {
                current.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    this += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                current == '#' || current == ';' -> {
                    if (line.getOrNull(index - 1)?.isWhitespace() != false) {
                        this += CodeToken(CodeTokenType.Comment, line.substring(index))
                        index = line.length
                    } else {
                        this += CodeToken(CodeTokenType.Plain, current.toString())
                        index += 1
                    }
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, index, current)
                    this += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '$' && line.getOrNull(index + 1) == '{' -> {
                    val end = line.indexOf('}', startIndex = index + 2).let { if (it == -1) line.length else it + 1 }
                    this += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                current == '\\' -> {
                    // Handle Unicode escapes: \uXXXX or \UXXXXXXXX
                    if (line.getOrNull(index + 1) == 'u' || line.getOrNull(index + 1) == 'U') {
                        val prefix = if (line.getOrNull(index + 1) == 'u') 4 else 8
                        var hexEnd = index + 2
                        while (hexEnd < line.length && hexEnd < index + 2 + prefix && line[hexEnd] in "0123456789abcdefABCDEF") {
                            hexEnd++
                        }
                        if (hexEnd == index + 2 + prefix) {
                            this += CodeToken(CodeTokenType.StringLiteral, line.substring(index, hexEnd))
                            index = hexEnd
                        } else {
                            // Not enough hex digits, treat as regular escape
                            val end = (index + 2).coerceAtMost(line.length)
                            this += CodeToken(CodeTokenType.Operator, line.substring(index, end))
                            index = end
                        }
                    } else {
                        val end = (index + 2).coerceAtMost(line.length)
                        this += CodeToken(CodeTokenType.Operator, line.substring(index, end))
                        index = end
                    }
                }

                current in IniPunctuation -> {
                    this += CodeToken(CodeTokenType.Punctuation, current.toString())
                    index += 1
                }

                current.isDigit() || (current == '-' && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextWhile(index) { it.isDigit() || it == '-' || it == '+' || it == '.' }
                    this += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isIniWordStart() -> {
                    val end = line.nextIniWordEnd(index)
                    val text = line.substring(index, end)
                    this += CodeToken(if (text.lowercase() in IniKeywords) CodeTokenType.Keyword else CodeTokenType.Plain, text)
                    index = end
                }

                else -> {
                    this += CodeToken(CodeTokenType.Plain, current.toString())
                    index += 1
                }
            }
        }
    }

    private val IniPunctuation = setOf('[', ']', '{', '}', ',', '.')
}
