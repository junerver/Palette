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
                "kt", "kts", "kotlin" -> KotlinLikeLexer(KotlinKeywords, KotlinPrimitiveTypes).highlight(lines)
                "java" -> KotlinLikeLexer(JavaKeywords, JavaPrimitiveTypes).highlight(lines)
                "js", "jsx", "javascript" -> KotlinLikeLexer(JavaScriptKeywords, JavaScriptPrimitiveTypes).highlight(lines)
                "ts", "tsx", "typescript" -> KotlinLikeLexer(TypeScriptKeywords, TypeScriptPrimitiveTypes).highlight(lines)
                "json" -> JsonLexer.highlight(lines)
                "css", "scss", "sass", "less" -> CssLexer().highlight(lines)
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

    private val JavaScriptKeywords =
        setOf(
            "async",
            "await",
            "break",
            "case",
            "catch",
            "class",
            "const",
            "continue",
            "default",
            "else",
            "export",
            "false",
            "for",
            "from",
            "function",
            "if",
            "import",
            "let",
            "new",
            "null",
            "return",
            "switch",
            "this",
            "throw",
            "true",
            "try",
            "undefined",
            "var",
            "while",
        )

    private val TypeScriptKeywords =
        JavaScriptKeywords +
            setOf(
                "as",
                "declare",
                "enum",
                "implements",
                "interface",
                "namespace",
                "private",
                "protected",
                "public",
                "readonly",
                "type",
            )

    private val KotlinPrimitiveTypes =
        setOf("Boolean", "Byte", "Char", "Double", "Float", "Int", "Long", "Short", "String", "Unit")

    private val JavaPrimitiveTypes =
        setOf("boolean", "byte", "char", "double", "float", "int", "long", "short", "String", "void")

    private val JavaScriptPrimitiveTypes =
        setOf("Array", "Boolean", "Date", "Map", "Number", "Object", "Promise", "Set", "String")

    private val TypeScriptPrimitiveTypes =
        JavaScriptPrimitiveTypes + setOf("any", "boolean", "never", "number", "string", "unknown", "void")
}

private class KotlinLikeLexer(
    private val keywords: Set<String>,
    private val primitiveTypes: Set<String>,
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

                current == '"' || current == '\'' || current == '`' -> {
                    val end = scanQuotedString(line, index, current)
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
                            text in primitiveTypes -> CodeTokenType.Type
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

    private companion object {
        val Operators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '?', ':')
        val Punctuation = setOf('(', ')', '[', ']', '{', '}', '.', ',', ';')
    }
}

private object JsonLexer {
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

private class CssLexer {
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
        val CssValueKeywords =
            setOf(
                "absolute",
                "auto",
                "block",
                "flex",
                "grid",
                "hidden",
                "inline",
                "none",
                "relative",
                "solid",
            )
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

private fun String.nextJsonNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' }

private fun String.nextCssIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '-' }

private fun String.nextCssNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '.' || it == '%' }

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

private fun Char.isCssIdentifierStart(): Boolean = isLetter() || this == '_' || this == '-'

private fun String.isHexColorToken(): Boolean {
    val value = drop(1)
    return value.length in setOf(3, 4, 6, 8) && value.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
}

private fun scanQuotedString(
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
