package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object ShellLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        var heredocTerminator: String? = null
        return lines.map { line ->
            val activeTerminator = heredocTerminator
            if (activeTerminator != null) {
                if (line.trim() == activeTerminator) {
                    heredocTerminator = null
                    listOf(CodeToken(CodeTokenType.Operator, line))
                } else {
                    listOf(CodeToken(CodeTokenType.StringLiteral, line))
                }
            } else {
                val highlighted = highlightLine(line)
                heredocTerminator = line.findHeredocTerminator()
                highlighted
            }
        }
    }

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0
        var expectingCommand = true

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

                line.startsWith(TestExpressionStart, index) -> {
                    val end = line.indexOf(TestExpressionEnd, startIndex = index + TestExpressionStart.length)
                    if (end != -1) {
                        tokens += CodeToken(CodeTokenType.Operator, TestExpressionStart)
                        tokens += highlightTestExpression(line.substring(index + TestExpressionStart.length, end))
                        tokens += CodeToken(CodeTokenType.Operator, TestExpressionEnd)
                        index = end + TestExpressionEnd.length
                    } else {
                        tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                        index += 1
                    }
                    expectingCommand = false
                }

                line.startsWith(ProcessSubstitutionInputStart, index) ||
                    line.startsWith(ProcessSubstitutionOutputStart, index) -> {
                    val startToken =
                        if (line.startsWith(ProcessSubstitutionInputStart, index)) {
                            ProcessSubstitutionInputStart
                        } else {
                            ProcessSubstitutionOutputStart
                        }
                    val end = line.findShellSubstitutionEnd(index)
                    if (end != -1) {
                        tokens += CodeToken(CodeTokenType.Operator, startToken)
                        val command = line.substring(index + startToken.length, end).trim()
                        if (command.isNotEmpty()) {
                            tokens += CodeToken(CodeTokenType.Annotation, command)
                        }
                        tokens += CodeToken(CodeTokenType.Operator, ")")
                        index = end + 1
                    } else {
                        tokens += CodeToken(CodeTokenType.Operator, startToken.first().toString())
                        index += 1
                    }
                    expectingCommand = false
                }

                line.startsWith(CommandSubstitutionStart, index) -> {
                    val end = line.findShellSubstitutionEnd(index)
                    if (end != -1) {
                        tokens += CodeToken(CodeTokenType.Operator, CommandSubstitutionStart)
                        val command = line.substring(index + 2, end).trim()
                        if (command.isNotEmpty()) {
                            tokens += CodeToken(CodeTokenType.Annotation, command)
                        }
                        tokens += CodeToken(CodeTokenType.Operator, ")")
                        index = end + 1
                    } else {
                        tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, line.nextShellVariableEnd(index)))
                        index = line.nextShellVariableEnd(index)
                    }
                    expectingCommand = false
                }

                current == '`' -> {
                    val end = line.indexOf('`', startIndex = index + 1)
                    if (end != -1) {
                        tokens += CodeToken(CodeTokenType.Operator, "`")
                        val command = line.substring(index + 1, end).trim()
                        if (command.isNotEmpty()) {
                            tokens += CodeToken(CodeTokenType.Annotation, command)
                        }
                        tokens += CodeToken(CodeTokenType.Operator, "`")
                        index = end + 1
                    } else {
                        tokens += CodeToken(CodeTokenType.Plain, current.toString())
                        index += 1
                    }
                    expectingCommand = false
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                    expectingCommand = false
                }

                current == '$' -> {
                    val end = line.nextShellVariableEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                    expectingCommand = false
                }

                current == '-' && line.getOrNull(index + 1)?.isShellWordPart() == true -> {
                    val end = line.nextShellWordEnd(index)
                    tokens += CodeToken(CodeTokenType.Operator, line.substring(index, end))
                    index = end
                    expectingCommand = false
                }

                current.isDigit() -> {
                    val end = line.nextWhile(index) { it.isDigit() }
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                    expectingCommand = false
                }

                current.isShellWordStart() -> {
                    val end = line.nextShellWordEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            text in ShellKeywords -> CodeTokenType.Keyword
                            text in ShellBuiltins -> CodeTokenType.Builtin
                            expectingCommand -> CodeTokenType.Function
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                    expectingCommand = false
                }

                current in ShellOperators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                    expectingCommand = true
                }

                current in ShellPunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                    index += 1
                    expectingCommand = current in setOf(';', '(', '{')
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, current.toString())
                    index += 1
                    expectingCommand = false
                }
            }
        }

        return tokens
    }

    private fun highlightTestExpression(source: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0
        while (index < source.length) {
            val current = source[index]
            when {
                current.isWhitespace() -> {
                    val end = source.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, source.substring(index, end))
                    index = end
                }

                source.startsWith("&&", index) || source.startsWith("||", index) -> {
                    tokens += CodeToken(CodeTokenType.Operator, source.substring(index, index + 2))
                    index += 2
                }

                source.startsWith("==", index) ||
                    source.startsWith("!=", index) ||
                    source.startsWith("=~", index) -> {
                    tokens += CodeToken(CodeTokenType.Operator, source.substring(index, index + 2))
                    index += 2
                }

                source.startsWith("-eq", index) ||
                    source.startsWith("-ne", index) ||
                    source.startsWith("-gt", index) ||
                    source.startsWith("-ge", index) ||
                    source.startsWith("-lt", index) ||
                    source.startsWith("-le", index) -> {
                    val end = source.nextShellWordEnd(index)
                    tokens += CodeToken(CodeTokenType.Operator, source.substring(index, end))
                    index = end
                }

                current == '-' && source.getOrNull(index + 1)?.isLetter() == true -> {
                    val end = source.nextShellWordEnd(index)
                    tokens += CodeToken(CodeTokenType.Operator, source.substring(index, end))
                    index = end
                }

                current == '$' -> {
                    val end = source.nextShellVariableEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, source.substring(index, end))
                    index = end
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(source, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, source.substring(index, end))
                    index = end
                }

                current in ShellOperators || current == '!' -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in ShellPunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                    index += 1
                }

                current.isDigit() -> {
                    val end = source.nextWhile(index) { it.isDigit() }
                    tokens += CodeToken(CodeTokenType.NumberLiteral, source.substring(index, end))
                    index = end
                }

                current.isShellWordStart() -> {
                    val end = source.nextShellWordEnd(index)
                    tokens += CodeToken(CodeTokenType.Plain, source.substring(index, end))
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

    private fun String.findHeredocTerminator(): String? {
        val match = HeredocRegex.find(this) ?: return null
        return match.groupValues[1]
            .ifEmpty { match.groupValues[2] }
            .ifEmpty { match.groupValues[3] }
            .trim()
            .ifEmpty { null }
    }

    private fun String.findShellSubstitutionEnd(start: Int): Int {
        var index = start + 2
        var depth = 1
        var quote: Char? = null
        var escaped = false
        while (index < length) {
            val char = this[index]
            when {
                escaped -> escaped = false
                char == '\\' -> escaped = true
                quote != null -> if (char == quote) quote = null
                char == '"' || char == '\'' -> quote = char
                startsWith(CommandSubstitutionStart, index) ||
                    startsWith(ProcessSubstitutionInputStart, index) ||
                    startsWith(ProcessSubstitutionOutputStart, index) -> {
                    depth += 1
                    index += 1
                }
                char == ')' -> {
                    depth -= 1
                    if (depth == 0) return index
                }
            }
            index += 1
        }
        return -1
    }

    private val HeredocRegex = Regex("""<<-?\s*(?:"([^"]+)"|'([^']+)'|([A-Za-z_][A-Za-z0-9_]*))""")
    private const val CommandSubstitutionStart = "\$("
    private const val ProcessSubstitutionInputStart = "<("
    private const val ProcessSubstitutionOutputStart = ">("
    private const val TestExpressionStart = "[["
    private const val TestExpressionEnd = "]]"
    private val ShellOperators = setOf('|', '&', '<', '>', '=')
    private val ShellPunctuation = setOf('(', ')', '{', '}', '[', ']', ';', ':')
}
