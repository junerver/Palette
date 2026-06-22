package xyz.junerver.compose.palette.code

data class HighlightedCode(
    val language: String,
    val tokens: List<List<CodeToken>>,
)

data class CodeToken(
    val type: CodeTokenType,
    val text: String,
)

enum class CodeTokenType {
    Plain,
    Keyword,
    StringLiteral,
    NumberLiteral,
    Comment,
    Function,
    Type,
    Annotation,
    Operator,
    Punctuation,
}

object PaletteCodeHighlighter {
    fun highlight(
        code: String,
        language: String,
    ): HighlightedCode {
        val normalizedLanguage = language.trim().lowercase()
        val lines = code.lines()
        val tokens =
            when (normalizedLanguage) {
                "kt", "kts", "kotlin" -> KotlinLikeLexer(KotlinKeywords).highlight(lines)
                "java" -> KotlinLikeLexer(JavaKeywords).highlight(lines)
                else -> lines.map { line -> listOf(CodeToken(CodeTokenType.Plain, line)) }
            }
        return HighlightedCode(language = normalizedLanguage.ifEmpty { "plain" }, tokens = tokens)
    }

    private val KotlinKeywords =
        setOf(
            "as",
            "break",
            "class",
            "continue",
            "data",
            "do",
            "else",
            "false",
            "for",
            "fun",
            "if",
            "in",
            "interface",
            "is",
            "null",
            "object",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "sealed",
            "super",
            "this",
            "true",
            "typealias",
            "val",
            "var",
            "when",
            "while",
        )

    private val JavaKeywords =
        setOf(
            "abstract",
            "boolean",
            "break",
            "case",
            "class",
            "continue",
            "else",
            "extends",
            "false",
            "final",
            "for",
            "if",
            "implements",
            "import",
            "interface",
            "new",
            "null",
            "private",
            "protected",
            "public",
            "return",
            "static",
            "switch",
            "this",
            "true",
            "void",
            "while",
        )
}

private class KotlinLikeLexer(
    private val keywords: Set<String>,
) {
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

                line.startsWith("//", index) -> {
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

                current == '"' || current == '\'' -> {
                    val end = scanString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '@' -> {
                    val end = line.nextIdentifierEnd(index + 1)
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
                    val type =
                        when {
                            text in keywords -> CodeTokenType.Keyword
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            text.first().isUpperCase() -> CodeTokenType.Type
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
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

    private fun scanString(
        line: String,
        start: Int,
        quote: Char,
    ): Int {
        var index = start + 1
        var escaped = false
        while (index < line.length) {
            val current = line[index]
            if (!escaped && current == quote) return index + 1
            escaped = !escaped && current == '\\'
            if (current != '\\') escaped = false
            index += 1
        }
        return line.length
    }

    private companion object {
        val Operators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '?', ':')
        val Punctuation = setOf('(', ')', '[', ']', '{', '}', '.', ',', ';')
    }
}

private fun String.nextWhile(
    start: Int,
    predicate: (Char) -> Boolean,
): Int {
    var index = start
    while (index < length && predicate(this[index])) index += 1
    return index
}

private fun String.nextIdentifierEnd(start: Int): Int = nextWhile(start) { it.isLetterOrDigit() || it == '_' }

private fun String.nextNonWhitespace(start: Int): Char? {
    var index = start
    while (index < length) {
        val current = this[index]
        if (!current.isWhitespace()) return current
        index += 1
    }
    return null
}

private fun Char.isIdentifierStart(): Boolean = isLetter() || this == '_'
