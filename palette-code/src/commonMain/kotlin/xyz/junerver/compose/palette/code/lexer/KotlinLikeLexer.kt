package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal class KotlinLikeLexer(
    private val keywords: Set<String>,
    private val primitiveTypes: Set<String>,
    private val builtins: Set<String> = emptySet(),
    private val supportNestedBlockComments: Boolean = false,
    private val supportStringInterpolation: Boolean = false,
) {
    private var blockCommentDepth = 0
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

            if (blockCommentDepth > 0) {
                val scan = scanBlockComment(line, index, blockCommentDepth)
                tokens += CodeToken(CodeTokenType.Comment, line.substring(index, scan.end))
                blockCommentDepth = scan.depth
                if (blockCommentDepth > 0) {
                    return tokens
                }
                index = scan.end
                continue
            }

            val current = line[index]
            when {
                current.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                line.startsWith("//", index) -> {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    index = line.length
                }

                line.startsWith("/*", index) -> {
                    val scan = scanBlockComment(line, index, initialDepth = 0)
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index, scan.end))
                    blockCommentDepth = scan.depth
                    index = scan.end
                    if (blockCommentDepth > 0) {
                        return tokens
                    }
                }

                current == '"' || current == '\'' || current == '`' -> {
                    val delimiter = if (current == '"') line.tripleQuoteDelimiterAt(index) else null
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
                    val text = line.substring(index, end)
                    if (supportStringInterpolation && current == '"' && delimiter == null) {
                        tokens += tokenizeKotlinStringInterpolation(text)
                    } else {
                        tokens += CodeToken(CodeTokenType.StringLiteral, text)
                    }
                    index = end
                }

                current == '@' -> {
                    val end = line.nextDottedIdentifierEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                current.isDigit() -> {
                    val end = line.nextWhile(index) { it.isDigit() || it == '.' || it == '_' }
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isIdentifierStart() -> {
                    val end = line.nextIdentifierEnd(index)
                    val text = line.substring(index, end)
                    val prevNonWhitespace = if (index > 0) line.substring(0, index).trimEnd().lastOrNull() else null
                    val type =
                        when {
                            text in keywords -> CodeTokenType.Keyword
                            text in primitiveTypes -> CodeTokenType.Type
                            text in builtins -> CodeTokenType.Builtin
                            prevNonWhitespace == '.' -> CodeTokenType.Property
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            text.isAllCapsIdentifier() -> CodeTokenType.Constant
                            text.first().isUpperCase() -> CodeTokenType.ClassName
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                line.multiCharacterOperatorAt(index) != null -> {
                    val operator = line.multiCharacterOperatorAt(index).orEmpty()
                    tokens += CodeToken(CodeTokenType.Operator, operator)
                    index += operator.length
                }

                current in Operators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in Punctuation -> {
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

    private fun scanBlockComment(
        line: String,
        start: Int,
        initialDepth: Int,
    ): BlockCommentScan {
        var index = start
        var depth = initialDepth
        while (index < line.length) {
            when {
                line.startsWith("/*", index) && (supportNestedBlockComments || depth == 0) -> {
                    depth += 1
                    index += 2
                }

                line.startsWith("*/", index) && depth > 0 -> {
                    depth -= 1
                    index += 2
                    if (depth == 0) return BlockCommentScan(end = index, depth = 0)
                }

                else -> index += 1
            }
        }
        return BlockCommentScan(end = line.length, depth = depth)
    }

    private fun String.isAllCapsIdentifier(): Boolean =
        length >= 2 && all { it.isUpperCase() || it == '_' || it.isDigit() }

    private fun String.multiCharacterOperatorAt(index: Int): String? =
        MultiCharacterOperators.firstOrNull { startsWith(it, index) }

    private fun tokenizeKotlinStringInterpolation(text: String): List<CodeToken> {
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

                char == '$' && text.getOrNull(index + 1) == '{' -> {
                    val end = text.indexOf('}', startIndex = index + 2)
                    if (end == -1) {
                        current.append(char)
                        index += 1
                    } else {
                        flushString()
                        tokens += CodeToken(CodeTokenType.Operator, "\${")
                        val expression = text.substring(index + 2, end).trim()
                        if (expression.isNotEmpty()) {
                            tokens += CodeToken(CodeTokenType.Annotation, expression)
                        }
                        tokens += CodeToken(CodeTokenType.Operator, "}")
                        index = end + 1
                    }
                }

                char == '$' && text.getOrNull(index + 1)?.isIdentifierStart() == true -> {
                    val end = text.nextIdentifierEnd(index + 1)
                    flushString()
                    tokens += CodeToken(CodeTokenType.Annotation, text.substring(index, end))
                    index = end
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

    private companion object {
        val MultiCharacterOperators =
            listOf(
                "!==",
                "===",
                "..<",
                "==",
                "!=",
                ">=",
                "<=",
                "&&",
                "||",
                "?:",
                "?.",
                "::",
                "->",
                "=>",
                "??",
                "..",
                "+=",
                "-=",
                "*=",
                "/=",
                "%=",
            )
        val Operators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '?', ':')
        val Punctuation = setOf('(', ')', '[', ']', '{', '}', '.', ',', ';')
    }
}

internal data class BlockCommentScan(
    val end: Int,
    val depth: Int,
)
