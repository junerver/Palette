package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object DockerfileLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        if (line.isEmpty()) return listOf(CodeToken(CodeTokenType.Plain, line))

        val tokens = mutableListOf<CodeToken>()
        var index = 0
        val leadingEnd = line.nextWhile(0, Char::isWhitespace)
        if (leadingEnd > 0) {
            tokens += CodeToken(CodeTokenType.Plain, line.substring(0, leadingEnd))
            index = leadingEnd
        }

        if (line.getOrNull(index) == '#') {
            tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
            return tokens
        }

        val instructionEnd = line.nextDockerfileWordEnd(index)
        if (instructionEnd > index) {
            val instruction = line.substring(index, instructionEnd)
            if (instruction.uppercase() in DockerfileInstructions) {
                tokens += CodeToken(CodeTokenType.Keyword, instruction)
                index = instructionEnd
            }
        }

        while (index < line.length) {
            val char = line[index]
            when {
                char.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                char == '#' && line.getOrNull(index - 1)?.isWhitespace() != false -> {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    index = line.length
                }

                char == '"' || char == '\'' -> {
                    val end = scanQuotedString(line, index, char)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                char == '$' -> {
                    val end = line.nextDockerfileVariableEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                line.startsWith("--", index) -> {
                    val end = line.nextDockerfileFlagEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                char == '\\' -> {
                    tokens += CodeToken(CodeTokenType.Operator, char.toString())
                    index += 1
                }

                char in DockerfilePunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, char.toString())
                    index += 1
                }

                char.isDigit() -> {
                    val end = line.nextWhile(index) { it.isDigit() || it == '.' }
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                char.isDockerfileWordStart() -> {
                    val end = line.nextDockerfileWordEnd(index)
                    val text = line.substring(index, end)
                    tokens +=
                        CodeToken(
                            type = if (text.uppercase() in DockerfileSecondaryKeywords) CodeTokenType.Keyword else CodeTokenType.Plain,
                            text = text,
                        )
                    index = end
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, char.toString())
                    index += 1
                }
            }
        }

        return tokens
    }

    private val DockerfilePunctuation = setOf('[', ']', '{', '}', '(', ')', ',', ':', '=')
}
