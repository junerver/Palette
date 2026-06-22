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
                "py", "python" -> PythonLexer.highlight(lines)
                "html", "xml", "svg" -> HtmlLexer().highlight(lines)
                "bash", "sh", "shell", "zsh" -> ShellLexer.highlight(lines)
                "yaml", "yml" -> YamlLexer.highlight(lines)
                "sql", "mysql", "postgresql", "postgres" -> SqlLexer().highlight(lines)
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

    private val PythonKeywords =
        setOf(
            "False",
            "None",
            "True",
            "and",
            "as",
            "assert",
            "async",
            "await",
            "break",
            "class",
            "continue",
            "def",
            "del",
            "elif",
            "else",
            "except",
            "finally",
            "for",
            "from",
            "global",
            "if",
            "import",
            "in",
            "is",
            "lambda",
            "nonlocal",
            "not",
            "or",
            "pass",
            "raise",
            "return",
            "try",
            "while",
            "with",
            "yield",
        )

    private val PythonPrimitiveTypes =
        setOf("bool", "bytes", "dict", "float", "frozenset", "int", "list", "set", "str", "tuple")

    private object PythonLexer {
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

                    current == '@' -> {
                        val end = line.nextDottedIdentifierEnd(index + 1)
                        tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                        index = end
                    }

                    current.isPythonStringPrefixStart() && line.getOrNull(index + 1) in setOf('"', '\'') -> {
                        val end = scanQuotedString(line, index + 1, line[index + 1])
                        tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                        index = end
                    }

                    current == '"' || current == '\'' -> {
                        val end = scanQuotedString(line, index, current)
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

        private val PythonOperators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '^', '~', ':')
        private val PythonPunctuation = setOf('(', ')', '[', ']', '{', '}', '.', ',', ';')
    }
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

private class HtmlLexer {
    private var inComment = false

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            if (inComment) {
                val end = line.indexOf("-->", startIndex = index)
                if (end == -1) {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    return tokens
                }
                tokens += CodeToken(CodeTokenType.Comment, line.substring(index, end + 3))
                inComment = false
                index = end + 3
                continue
            }

            when {
                line.startsWith("<!--", index) -> {
                    val end = line.indexOf("-->", startIndex = index + 4)
                    if (end == -1) {
                        tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                        inComment = true
                        index = line.length
                    } else {
                        tokens += CodeToken(CodeTokenType.Comment, line.substring(index, end + 3))
                        index = end + 3
                    }
                }

                line.startsWith("<!", index) -> {
                    val end = line.nextHtmlIdentifierEnd(index + 2)
                    tokens += CodeToken(CodeTokenType.Keyword, line.substring(index, end))
                    index = highlightTagContent(line = line, index = end, tokens = tokens)
                }

                line.startsWith("</", index) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "</")
                    val nameEnd = line.nextHtmlIdentifierEnd(index + 2)
                    if (nameEnd > index + 2) {
                        tokens += CodeToken(CodeTokenType.Type, line.substring(index + 2, nameEnd))
                    }
                    index = highlightTagContent(line = line, index = nameEnd, tokens = tokens)
                }

                line[index] == '<' -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "<")
                    val nameEnd = line.nextHtmlIdentifierEnd(index + 1)
                    if (nameEnd > index + 1) {
                        tokens += CodeToken(CodeTokenType.Type, line.substring(index + 1, nameEnd))
                    }
                    index = highlightTagContent(line = line, index = nameEnd, tokens = tokens)
                }

                else -> {
                    val end = line.indexOf('<', startIndex = index).let { if (it == -1) line.length else it }
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }
            }
        }

        return tokens
    }

    private fun highlightTagContent(
        line: String,
        index: Int,
        tokens: MutableList<CodeToken>,
    ): Int {
        var currentIndex = index
        while (currentIndex < line.length) {
            val current = line[currentIndex]
            when {
                current.isWhitespace() -> {
                    val end = line.nextWhile(currentIndex, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(currentIndex, end))
                    currentIndex = end
                }

                line.startsWith("/>", currentIndex) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "/>")
                    return currentIndex + 2
                }

                current == '>' -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, ">")
                    return currentIndex + 1
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, currentIndex, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(currentIndex, end))
                    currentIndex = end
                }

                current == '=' -> {
                    tokens += CodeToken(CodeTokenType.Operator, "=")
                    currentIndex += 1
                }

                current.isHtmlIdentifierStart() -> {
                    val end = line.nextHtmlIdentifierEnd(currentIndex)
                    val text = line.substring(currentIndex, end)
                    val type =
                        if (line.nextNonWhitespace(end) == '=') {
                            CodeTokenType.Annotation
                        } else {
                            CodeTokenType.Type
                        }
                    tokens += CodeToken(type, text)
                    currentIndex = end
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, current.toString())
                    currentIndex += 1
                }
            }
        }
        return currentIndex
    }
}

private object ShellLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

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

    private val ShellKeywords =
        setOf(
            "case",
            "do",
            "done",
            "elif",
            "else",
            "esac",
            "export",
            "fi",
            "for",
            "function",
            "if",
            "in",
            "local",
            "then",
            "while",
        )
    private val ShellOperators = setOf('|', '&', '<', '>', '=')
    private val ShellPunctuation = setOf('(', ')', '{', '}', '[', ']', ';', ':')
}

private object YamlLexer {
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

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                (current == '&' || current == '*') && line.getOrNull(index + 1)?.isYamlWordStart() == true -> {
                    val end = line.nextYamlWordEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                current == '-' && line.getOrNull(index + 1)?.isWhitespace() == true -> {
                    tokens += CodeToken(CodeTokenType.Operator, "-")
                    index += 1
                }

                current.isDigit() || (current == '-' && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextYamlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isYamlWordStart() -> {
                    val end = line.nextYamlWordEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            line.nextNonWhitespace(end) == ':' -> CodeTokenType.Keyword
                            text in YamlKeywords -> CodeTokenType.Keyword
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current == ':' -> {
                    tokens += CodeToken(CodeTokenType.Operator, ":")
                    index += 1
                }

                current in YamlPunctuation -> {
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

    private val YamlKeywords = setOf("true", "false", "null", "yes", "no", "on", "off")
    private val YamlPunctuation = setOf('[', ']', '{', '}', ',')
}

private class SqlLexer {
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

                line.startsWith("--", index) -> {
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

                current == '\'' || current == '"' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current.isDigit() -> {
                    val end = line.nextSqlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isSqlIdentifierStart() -> {
                    val end = line.nextSqlIdentifierEnd(index)
                    val text = line.substring(index, end)
                    val normalized = text.uppercase()
                    val type =
                        when {
                            normalized in SqlTypes -> CodeTokenType.Type
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
                            normalized in SqlKeywords -> CodeTokenType.Keyword
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current in SqlOperators -> {
                    tokens += CodeToken(CodeTokenType.Operator, current.toString())
                    index += 1
                }

                current in SqlPunctuation -> {
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
        val SqlKeywords =
            setOf(
                "ADD",
                "ALTER",
                "AND",
                "AS",
                "ASC",
                "BETWEEN",
                "BY",
                "CASE",
                "CREATE",
                "DELETE",
                "DESC",
                "DISTINCT",
                "DROP",
                "ELSE",
                "FALSE",
                "FROM",
                "GROUP",
                "HAVING",
                "IN",
                "INSERT",
                "INTO",
                "IS",
                "JOIN",
                "LIMIT",
                "NOT",
                "NULL",
                "ON",
                "OR",
                "ORDER",
                "PRIMARY",
                "SELECT",
                "SET",
                "TABLE",
                "THEN",
                "TRUE",
                "UNION",
                "UPDATE",
                "VALUES",
                "WHEN",
                "WHERE",
            )
        val SqlTypes =
            setOf(
                "BIGINT",
                "BOOLEAN",
                "CHAR",
                "DATE",
                "DECIMAL",
                "DOUBLE",
                "FLOAT",
                "INTEGER",
                "INT",
                "JSON",
                "NUMERIC",
                "REAL",
                "TEXT",
                "TIME",
                "TIMESTAMP",
                "UUID",
                "VARCHAR",
            )
        val SqlOperators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '|')
        val SqlPunctuation = setOf('(', ')', ',', '.', ';')
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

private fun String.nextDottedIdentifierEnd(start: Int): Int = nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '.' }

private fun String.nextJsonNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' }

private fun String.nextPythonNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '.' }

private fun String.nextCssIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '-' }

private fun String.nextCssNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '.' || it == '%' }

private fun String.nextHtmlIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '-' || it == ':' }

private fun String.nextShellWordEnd(start: Int): Int =
    nextWhile(start) { it.isShellWordPart() }

private fun String.nextShellVariableEnd(start: Int): Int =
    when (getOrNull(start + 1)) {
        '{' -> {
            val end = indexOf('}', startIndex = start + 2)
            if (end == -1) length else end + 1
        }

        else -> nextWhile(start + 1) { it.isLetterOrDigit() || it == '_' }.coerceAtLeast(start + 1)
    }

private fun String.nextYamlWordEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.', '/') }

private fun String.nextYamlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == '_' }

private fun String.nextSqlIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '$' }

private fun String.nextSqlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '.' }

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

private fun Char.isPythonStringPrefixStart(): Boolean = this in setOf('f', 'F', 'r', 'R', 'b', 'B', 'u', 'U')

private fun Char.isCssIdentifierStart(): Boolean = isLetter() || this == '_' || this == '-'

private fun Char.isHtmlIdentifierStart(): Boolean = isLetter() || this == '_' || this == ':'

private fun Char.isShellWordStart(): Boolean = isLetterOrDigit() || this in setOf('_', '.', '/', '~')

private fun Char.isShellWordPart(): Boolean = isShellWordStart() || this in setOf('-', '+')

private fun Char.isYamlWordStart(): Boolean = isLetter() || this == '_' || this == '.'

private fun Char.isSqlIdentifierStart(): Boolean = isLetter() || this == '_'

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
