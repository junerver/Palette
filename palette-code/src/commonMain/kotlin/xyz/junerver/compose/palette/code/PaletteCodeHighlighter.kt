package xyz.junerver.compose.palette.code

data class HighlightedCode(
    val language: String,
    val tokens: List<List<CodeToken>>,
    val diagnostics: List<PaletteCodeDiagnostic> = emptyList(),
)

data class CodeToken(
    val type: CodeTokenType,
    val text: String,
)

data class PaletteCodeDiagnostic(
    val code: PaletteCodeDiagnosticCode,
    val message: String,
    val severity: PaletteCodeDiagnosticSeverity = PaletteCodeDiagnosticSeverity.Warning,
    val line: Int? = null,
    val column: Int? = null,
)

enum class PaletteCodeDiagnosticCode {
    BlankLanguage,
    UnsupportedLanguage,
    HighlighterFailure,
}

enum class PaletteCodeDiagnosticSeverity {
    Warning,
    Error,
}

fun interface PaletteCodeLanguageHighlighter {
    fun highlight(lines: List<String>): List<List<CodeToken>>
}

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
    Inserted,
    Deleted,
    /** Object/instance property access, e.g. `obj.prop` */
    Property,
    /** Local or parameter variable */
    Variable,
    /** Compile-time constant, companion object constant */
    Constant,
    /** Language built-in function or identifier, e.g. `println`, `listOf` */
    Builtin,
    /** Class, interface, object, enum declaration name */
    ClassName,
    /** Package or namespace qualifier */
    Namespace,
}

object PaletteCodeHighlighter {
    private val customHighlighters = mutableMapOf<String, PaletteCodeLanguageHighlighter>()

    fun registerLanguage(
        language: String,
        aliases: Set<String> = emptySet(),
        highlighter: PaletteCodeLanguageHighlighter,
    ) {
        val normalizedLanguages =
            (listOf(language) + aliases)
                .map { it.trim().lowercase() }
                .filter { it.isNotEmpty() }
                .distinct()
        require(normalizedLanguages.isNotEmpty()) { "At least one non-blank language name is required." }
        normalizedLanguages.forEach { normalizedLanguage ->
            customHighlighters[normalizedLanguage] = highlighter
        }
    }

    fun unregisterLanguage(language: String) {
        customHighlighters.remove(language.trim().lowercase())
    }

    fun highlight(
        code: String,
        language: String,
    ): HighlightedCode = highlightWithDiagnostics(code = code, language = language)

    fun highlightWithDiagnostics(
        code: String,
        language: String,
    ): HighlightedCode {
        val normalizedLanguage = language.trim().lowercase()
        val effectiveLanguage = normalizedLanguage.ifEmpty { "plain" }
        val lines = code.lines()
        val diagnostics = mutableListOf<PaletteCodeDiagnostic>()

        if (normalizedLanguage.isEmpty()) {
            diagnostics +=
                PaletteCodeDiagnostic(
                    code = PaletteCodeDiagnosticCode.BlankLanguage,
                    message = "No language was provided; code was highlighted as plain text.",
                )
            return HighlightedCode(
                language = effectiveLanguage,
                tokens = lines.toPlainCodeTokens(),
                diagnostics = diagnostics,
            )
        }

        val highlighter =
            customHighlighters[normalizedLanguage]
                ?: normalizedLanguage.builtInHighlighterOrNull()

        if (highlighter == null) {
            diagnostics +=
                PaletteCodeDiagnostic(
                    code = PaletteCodeDiagnosticCode.UnsupportedLanguage,
                    message = "Language '$normalizedLanguage' is not supported; code was highlighted as plain text.",
                )
            return HighlightedCode(
                language = effectiveLanguage,
                tokens = lines.toPlainCodeTokens(),
                diagnostics = diagnostics,
            )
        }

        val tokens =
            runCatching { highlighter.highlight(lines) }
                .getOrElse {
                    diagnostics +=
                        PaletteCodeDiagnostic(
                            code = PaletteCodeDiagnosticCode.HighlighterFailure,
                            message = "Highlighter for '$normalizedLanguage' failed; code was highlighted as plain text.",
                            severity = PaletteCodeDiagnosticSeverity.Error,
                        )
                    lines.toPlainCodeTokens()
                }
        return HighlightedCode(language = effectiveLanguage, tokens = tokens, diagnostics = diagnostics)
    }

    private fun String.builtInHighlighterOrNull(): PaletteCodeLanguageHighlighter? =
        when (this) {
            "kt", "kts", "kotlin" ->
                PaletteCodeLanguageHighlighter { lines ->
                    KotlinLikeLexer(
                        keywords = KotlinKeywords,
                        primitiveTypes = KotlinPrimitiveTypes,
                        builtins = KotlinBuiltins,
                        supportNestedBlockComments = true,
                        supportStringInterpolation = true,
                    ).highlight(lines)
                }
            "java" -> PaletteCodeLanguageHighlighter { lines -> KotlinLikeLexer(JavaKeywords, JavaPrimitiveTypes).highlight(lines) }
            "js", "javascript" -> PaletteCodeLanguageHighlighter { lines -> KotlinLikeLexer(JavaScriptKeywords, JavaScriptPrimitiveTypes).highlight(lines) }
            "ts", "typescript" -> PaletteCodeLanguageHighlighter { lines -> KotlinLikeLexer(TypeScriptKeywords, TypeScriptPrimitiveTypes).highlight(lines) }
            "json" -> PaletteCodeLanguageHighlighter { lines -> JsonLexer.highlight(lines) }
            "css" -> PaletteCodeLanguageHighlighter { lines -> CssLexer().highlight(lines) }
            "py", "python" -> PaletteCodeLanguageHighlighter { lines -> PythonLexer().highlight(lines) }
            "html", "xml", "svg" -> PaletteCodeLanguageHighlighter { lines -> HtmlLexer().highlight(lines) }
            "bash", "sh", "shell", "zsh" -> PaletteCodeLanguageHighlighter { lines -> ShellLexer.highlight(lines) }
            "yaml", "yml" -> PaletteCodeLanguageHighlighter { lines -> YamlLexer.highlight(lines) }
            "toml" -> PaletteCodeLanguageHighlighter { lines -> TomlLexer.highlight(lines) }
            "ini", "properties", "props", "conf" -> PaletteCodeLanguageHighlighter { lines -> IniPropertiesLexer.highlight(lines) }
            "graphql", "gql" -> PaletteCodeLanguageHighlighter { lines -> GraphQlLexer.highlight(lines) }
            "diff", "patch" -> PaletteCodeLanguageHighlighter { lines -> DiffLexer.highlight(lines) }
            "md", "markdown", "mkd" -> PaletteCodeLanguageHighlighter { lines -> MarkdownLexer.highlight(lines) }
            "dockerfile", "containerfile", "docker" -> PaletteCodeLanguageHighlighter { lines -> DockerfileLexer.highlight(lines) }
            "sql", "mysql", "postgresql", "postgres" -> PaletteCodeLanguageHighlighter { lines -> SqlLexer().highlight(lines) }
            else -> null
        }

    private fun List<String>.toPlainCodeTokens(): List<List<CodeToken>> =
        map { line -> listOf(CodeToken(CodeTokenType.Plain, line)) }

    private val KotlinKeywords =
        setOf(
            "abstract",
            "as",
            "actual",
            "annotation",
            "break",
            "by",
            "catch",
            "class",
            "companion",
            "const",
            "continue",
            "context",
            "crossinline",
            "data",
            "delegate",
            "do",
            "dynamic",
            "else",
            "enum",
            "expect",
            "external",
            "false",
            "final",
            "finally",
            "for",
            "fun",
            "get",
            "if",
            "import",
            "in",
            "infix",
            "init",
            "inline",
            "inner",
            "interface",
            "internal",
            "is",
            "lateinit",
            "noinline",
            "null",
            "object",
            "open",
            "operator",
            "out",
            "override",
            "package",
            "private",
            "protected",
            "public",
            "reified",
            "return",
            "sealed",
            "set",
            "super",
            "suspend",
            "tailrec",
            "this",
            "throw",
            "try",
            "true",
            "typealias",
            "val",
            "value",
            "var",
            "vararg",
            "when",
            "where",
            "while",
            "constructor",
        )

    private val KotlinBuiltins =
        setOf(
            "println", "print", "readLine", "readln",
            "listOf", "mutableListOf", "arrayListOf",
            "mapOf", "mutableMapOf", "hashMapOf",
            "setOf", "mutableSetOf", "hashSetOf",
            "arrayOf", "intArrayOf", "longArrayOf", "floatArrayOf", "doubleArrayOf", "booleanArrayOf",
            "emptyList", "emptyMap", "emptySet",
            "listOfNotNull", "setOfNotNull",
            "buildList", "buildMap", "buildSet",
            "sequence", "generateSequence",
            "lazy", "lazyOf",
            "error", "require", "check",
            "TODO",
            "Pair", "Triple",
            "Regex",
        )

    private val JavaKeywords =
        setOf(
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "extends",
            "false",
            "final",
            "finally",
            "float",
            "for",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "module",
            "native",
            "new",
            "null",
            "opens",
            "open",
            "package",
            "permits",
            "private",
            "protected",
            "public",
            "record",
            "requires",
            "return",
            "sealed",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "to",
            "transient",
            "true",
            "try",
            "uses",
            "var",
            "void",
            "volatile",
            "with",
            "while",
            "yield",
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

    private class PythonLexer {
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
                        val isFString = current == 'f' || current == 'F'
                        val quoteIndex = index + 1
                        val delimiter = line.tripleQuoteDelimiterAt(quoteIndex)
                        val end =
                            if (delimiter != null) {
                                val segmentEnd =
                                    scanMultilineStringSegment(line, quoteIndex, delimiter, skipOpeningDelimiter = true)
                                if (!hasMultilineStringClose(line, quoteIndex, delimiter, skipOpeningDelimiter = true)) {
                                    multilineStringDelimiter = delimiter
                                }
                                segmentEnd
                            } else {
                                scanQuotedString(line, quoteIndex, line[quoteIndex])
                            }
                        val text = line.substring(index, end)
                        if (isFString && delimiter == null) {
                            tokens += tokenizePythonFString(text)
                        } else {
                            tokens += CodeToken(CodeTokenType.StringLiteral, text)
                        }
                        index = end
                    }

                    current == '"' || current == '\'' -> {
                        val delimiter = line.tripleQuoteDelimiterAt(index)
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

        private fun tokenizePythonFString(text: String): List<CodeToken> {
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

                    char == '{' && text.getOrNull(index + 1) == '{' -> {
                        current.append("{{")
                        index += 2
                    }

                    char == '}' && text.getOrNull(index + 1) == '}' -> {
                        current.append("}}")
                        index += 2
                    }

                    char == '{' -> {
                        val end = text.indexOf('}', startIndex = index + 1)
                        if (end == -1) {
                            current.append(char)
                            index += 1
                        } else {
                            flushString()
                            tokens += CodeToken(CodeTokenType.Operator, "{")
                            val expression = text.substring(index + 1, end).trim()
                            if (expression.isNotEmpty()) {
                                tokens += CodeToken(CodeTokenType.Annotation, expression)
                            }
                            tokens += CodeToken(CodeTokenType.Operator, "}")
                            index = end + 1
                        }
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

        private val PythonOperators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '^', '~', ':')
        private val PythonPunctuation = setOf('(', ')', '[', ']', '{', '}', '.', ',', ';')
    }
}

private class KotlinLikeLexer(
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
                        tokens += CodeToken(CodeTokenType.Operator, "${'$'}{")
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

private data class BlockCommentScan(
    val end: Int,
    val depth: Int,
)

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
    private var embeddedLanguage: HtmlEmbeddedLanguage? = null

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            embeddedLanguage?.let { language ->
                val closingTagStart = line.indexOfClosingHtmlTag(language.tagName, startIndex = index)
                val contentEnd = if (closingTagStart == -1) line.length else closingTagStart
                if (contentEnd > index) {
                    tokens += highlightEmbeddedHtmlContent(
                        language = language,
                        content = line.substring(index, contentEnd),
                    )
                    index = contentEnd
                }
                if (closingTagStart == -1) {
                    return tokens
                }
                embeddedLanguage = null
                continue
            }

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
                    val tagName =
                        if (nameEnd > index + 1) {
                            line.substring(index + 1, nameEnd)
                        } else {
                            ""
                        }
                    if (nameEnd > index + 1) {
                        tokens += CodeToken(CodeTokenType.Type, tagName)
                    }
                    val contentEnd = highlightTagContent(line = line, index = nameEnd, tokens = tokens)
                    if (!line.substring(nameEnd, contentEnd).trimEnd().endsWith("/>")) {
                        embeddedLanguage = tagName.toHtmlEmbeddedLanguage()
                    }
                    index = contentEnd
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

    private fun highlightEmbeddedHtmlContent(
        language: HtmlEmbeddedLanguage,
        content: String,
    ): List<CodeToken> =
        when (language) {
            HtmlEmbeddedLanguage.Script ->
                PaletteCodeHighlighter.highlight(content, "javascript").tokens.singleOrNull()
                    ?: listOf(CodeToken(CodeTokenType.Plain, content))

            HtmlEmbeddedLanguage.Style ->
                PaletteCodeHighlighter.highlight(content, "css").tokens.singleOrNull()
                    ?: listOf(CodeToken(CodeTokenType.Plain, content))
        }

    private enum class HtmlEmbeddedLanguage(
        val tagName: String,
    ) {
        Script("script"),
        Style("style"),
    }

    private fun String.toHtmlEmbeddedLanguage(): HtmlEmbeddedLanguage? =
        when (lowercase()) {
            HtmlEmbeddedLanguage.Script.tagName -> HtmlEmbeddedLanguage.Script
            HtmlEmbeddedLanguage.Style.tagName -> HtmlEmbeddedLanguage.Style
            else -> null
        }

    private fun String.indexOfClosingHtmlTag(
        tagName: String,
        startIndex: Int,
    ): Int =
        indexOf("</$tagName", startIndex = startIndex, ignoreCase = true)
}

private object ShellLexer {
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

    private val ShellBuiltins =
        setOf(
            "alias", "bg", "bind", "break", "builtin", "caller", "cd", "command",
            "compgen", "complete", "compopt", "continue", "declare", "dirs", "disown",
            "echo", "enable", "eval", "exec", "exit", "fc", "fg", "getopts", "hash",
            "help", "history", "jobs", "kill", "let", "logout", "mapfile", "popd",
            "printf", "pushd", "pwd", "read", "readarray", "readonly", "return",
            "set", "shift", "shopt", "source", "suspend", "test", "times", "trap",
            "true", "type", "typeset", "ulimit", "umask", "unalias", "unset", "wait",
        )
    private val HeredocRegex = Regex("""<<-?\s*(?:"([^"]+)"|'([^']+)'|([A-Za-z_][A-Za-z0-9_]*))""")
    private const val CommandSubstitutionStart = "\$("
    private const val ProcessSubstitutionInputStart = "<("
    private const val ProcessSubstitutionOutputStart = ">("
    private const val TestExpressionStart = "[["
    private const val TestExpressionEnd = "]]"
    private val ShellOperators = setOf('|', '&', '<', '>', '=')
    private val ShellPunctuation = setOf('(', ')', '{', '}', '[', ']', ';', ':')
}

private object YamlLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        var blockScalarParentIndent: Int? = null
        return lines.map { line ->
            val activeIndent = blockScalarParentIndent
            if (activeIndent != null) {
                if (line.trim().isEmpty() || line.leadingYamlIndentWidth() > activeIndent) {
                    listOf(CodeToken(CodeTokenType.StringLiteral, line))
                } else {
                    blockScalarParentIndent = null
                    val highlighted = highlightLine(line)
                    if (line.startsYamlBlockScalar()) {
                        blockScalarParentIndent = line.leadingYamlIndentWidth()
                    }
                    highlighted
                }
            } else {
                val highlighted = highlightLine(line)
                if (line.startsYamlBlockScalar()) {
                    blockScalarParentIndent = line.leadingYamlIndentWidth()
                }
                highlighted
            }
        }
    }

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            val current = line[index]
            when {
                line.trim() in YamlDocumentMarkers && index == line.indexOfFirst { !it.isWhitespace() } -> {
                    tokens += CodeToken(CodeTokenType.Operator, line.trim())
                    index = line.length
                }

                current.isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                current == '#' -> {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    index = line.length
                }

                current == '%' && index == line.indexOfFirst { !it.isWhitespace() } -> {
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index))
                    index = line.length
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, index, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current == '!' -> {
                    val end = line.nextYamlTagEnd(index)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                (current == '&' || current == '*') && line.getOrNull(index + 1)?.isYamlWordStart() == true -> {
                    val end = line.nextYamlWordEnd(index + 1)
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, end))
                    index = end
                }

                (current == '|' || current == '>') && line.isYamlBlockScalarIndicatorAt(index) -> {
                    val end = line.nextYamlBlockScalarIndicatorEnd(index)
                    tokens += CodeToken(CodeTokenType.Operator, line.substring(index, end))
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
    private val YamlDocumentMarkers = setOf("---", "...")
    private val YamlPunctuation = setOf('[', ']', '{', '}', ',')

    private fun String.startsYamlBlockScalar(): Boolean {
        val source = substringBefore('#').trimEnd()
        return YamlBlockScalarLineRegex.containsMatchIn(source)
    }

    private fun String.isYamlBlockScalarIndicatorAt(index: Int): Boolean {
        val source = substring(index)
        return YamlBlockScalarIndicatorRegex.find(source)?.range?.first == 0
    }

    private fun String.nextYamlBlockScalarIndicatorEnd(start: Int): Int {
        val match = YamlBlockScalarIndicatorRegex.find(substring(start)) ?: return start + 1
        return start + match.range.last + 1
    }

    private fun String.nextYamlTagEnd(start: Int): Int =
        nextWhile(start) { !it.isWhitespace() && it !in YamlPunctuation && it != '#' && it != ':' }

    private fun String.leadingYamlIndentWidth(): Int {
        var width = 0
        for (char in this) {
            when (char) {
                ' ' -> width += 1
                '\t' -> width += 4
                else -> return width
            }
        }
        return width
    }

    private val YamlBlockScalarLineRegex = Regex("""(?:^|\s|:\s*)[|>][+-]?\d?$""")
    private val YamlBlockScalarIndicatorRegex = Regex("""^[|>][+-]?\d?""")
}

private object TomlLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        var multilineStringDelimiter: String? = null
        return lines.map { line ->
            val activeDelimiter = multilineStringDelimiter
            if (activeDelimiter != null) {
                val closes =
                    hasMultilineStringClose(line, 0, activeDelimiter, skipOpeningDelimiter = false)
                val end = scanMultilineStringSegment(line, 0, activeDelimiter, skipOpeningDelimiter = false)
                val tokens =
                    mutableListOf(
                        CodeToken(CodeTokenType.StringLiteral, line.substring(0, end)),
                    )
                if (closes) {
                    multilineStringDelimiter = null
                    if (end < line.length) {
                        tokens += highlightLine(line.substring(end))
                    }
                }
                tokens
            } else {
                val highlighted = highlightLine(line)
                multilineStringDelimiter = line.openTomlMultilineStringDelimiter()
                highlighted
            }
        }
    }

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0
        var inSection = false

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

                line.startsWith("[[", index) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "[[")
                    inSection = true
                    index += 2
                }

                line.startsWith("]]", index) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "]]")
                    inSection = false
                    index += 2
                }

                current == '"' || current == '\'' -> {
                    val delimiter = line.tripleQuoteDelimiterAt(index)
                    val end =
                        if (delimiter != null) {
                            scanMultilineStringSegment(line, index, delimiter, skipOpeningDelimiter = true)
                        } else {
                            scanQuotedString(line, index, current)
                        }
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                current.isDigit() ||
                    ((current == '-' || current == '+') && line.getOrNull(index + 1)?.isDigit() == true) -> {
                    val end = line.nextTomlNumberEnd(index)
                    tokens += CodeToken(CodeTokenType.NumberLiteral, line.substring(index, end))
                    index = end
                }

                current.isTomlBareKeyStart() -> {
                    val end = line.nextTomlBareKeyEnd(index)
                    val text = line.substring(index, end)
                    val type =
                        when {
                            inSection -> CodeTokenType.Type
                            text in TomlKeywords -> CodeTokenType.Keyword
                            line.nextNonWhitespace(end) == '=' -> CodeTokenType.Keyword
                            else -> CodeTokenType.Plain
                        }
                    tokens += CodeToken(type, text)
                    index = end
                }

                current == '=' -> {
                    tokens += CodeToken(CodeTokenType.Operator, "=")
                    index += 1
                }

                current in TomlPunctuation -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, current.toString())
                    inSection =
                        when (current) {
                            '[' -> true
                            ']' -> false
                            else -> inSection
                        }
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

    private val TomlKeywords = setOf("true", "false")
    private val TomlPunctuation = setOf('[', ']', '{', '}', ',', '.')

    private fun String.openTomlMultilineStringDelimiter(): String? {
        var index = 0
        while (index < length) {
            val delimiter = tripleQuoteDelimiterAt(index)
            if (delimiter != null) {
                if (!hasMultilineStringClose(this, index, delimiter, skipOpeningDelimiter = true)) {
                    return delimiter
                }
                index = scanMultilineStringSegment(this, index, delimiter, skipOpeningDelimiter = true)
            } else {
                index += 1
            }
        }
        return null
    }
}

private object IniPropertiesLexer {
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

    private val IniKeywords = setOf("true", "false", "yes", "no", "on", "off", "null")
    private val IniPunctuation = setOf('[', ']', '{', '}', ',', '.')
}

private object GraphQlLexer {
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

    private val GraphQlKeywords =
        setOf(
            "directive",
            "enum",
            "extend",
            "fragment",
            "implements",
            "input",
            "interface",
            "mutation",
            "on",
            "query",
            "repeatable",
            "scalar",
            "schema",
            "subscription",
            "type",
            "union",
            "true",
            "false",
            "null",
        )
    private val GraphQlPunctuation = setOf('{', '}', '(', ')', '[', ']', ':', ',', '=')
    private val GraphQlOperators = setOf('!', '|', '&')
}

private object DiffLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> =
        lines.map { line -> highlightLine(line) }

    private fun highlightLine(line: String): List<CodeToken> =
        when {
            line.startsWith("diff --git ") -> highlightGitDiffHeader(line)
            line.startsWith("index ") -> highlightIndexHeader(line)
            line.startsWith("--- ") -> highlightPathHeader(line, marker = "---", markerType = CodeTokenType.Deleted)
            line.startsWith("+++ ") -> highlightPathHeader(line, marker = "+++", markerType = CodeTokenType.Inserted)
            line.startsWith("@@") -> highlightHunkHeader(line)
            line.startsWith("+") -> listOf(CodeToken(CodeTokenType.Inserted, line))
            line.startsWith("-") -> listOf(CodeToken(CodeTokenType.Deleted, line))
            else -> listOf(CodeToken(CodeTokenType.Plain, line))
        }

    private fun highlightGitDiffHeader(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        val parts = line.split(' ')
        if (parts.size >= 4) {
            tokens += CodeToken(CodeTokenType.Keyword, "diff --git")
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.Type, parts[2])
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.Type, parts[3])
            val rest = parts.drop(4).joinToString(" ")
            if (rest.isNotEmpty()) {
                tokens += CodeToken(CodeTokenType.Plain, " $rest")
            }
            return tokens
        }
        return listOf(CodeToken(CodeTokenType.Keyword, line))
    }

    private fun highlightIndexHeader(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        val parts = line.split(' ')
        tokens += CodeToken(CodeTokenType.Keyword, "index")
        if (parts.size >= 2) {
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.Annotation, parts[1])
        }
        if (parts.size >= 3) {
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.NumberLiteral, parts[2])
        }
        if (parts.size > 3) {
            tokens += CodeToken(CodeTokenType.Plain, " ${parts.drop(3).joinToString(" ")}")
        }
        return tokens
    }

    private fun highlightPathHeader(
        line: String,
        marker: String,
        markerType: CodeTokenType,
    ): List<CodeToken> {
        val path = line.removePrefix(marker).trimStart()
        return if (path.isEmpty()) {
            listOf(CodeToken(markerType, line))
        } else {
            listOf(
                CodeToken(markerType, marker),
                CodeToken(CodeTokenType.Plain, " "),
                CodeToken(CodeTokenType.Type, path),
            )
        }
    }

    private fun highlightHunkHeader(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0
        while (index < line.length) {
            when {
                line.startsWith("@@", index) -> {
                    tokens += CodeToken(CodeTokenType.Annotation, "@@")
                    index += 2
                }

                line[index].isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                line[index] == '-' || line[index] == '+' -> {
                    val end = line.nextWhile(index + 1) { it.isDigit() || it == ',' }
                    val type = if (line[index] == '-') CodeTokenType.Deleted else CodeTokenType.Inserted
                    tokens += CodeToken(type, line.substring(index, end))
                    index = end
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index))
                    index = line.length
                }
            }
        }
        return tokens
    }
}

private object MarkdownLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        val result = mutableListOf<List<CodeToken>>()
        var index = 0
        var fenceChar: Char? = null
        var fenceLength = 0
        var fenceLanguage: String? = null

        while (index < lines.size) {
            val line = lines[index]
            if (fenceChar != null) {
                if (line.isMarkdownFenceClose(fenceChar, fenceLength)) {
                    result += highlightMarkdownLine(line)
                    fenceChar = null
                    fenceLength = 0
                    fenceLanguage = null
                } else {
                    val language = fenceLanguage
                    val nestedLines = mutableListOf<String>()
                    val nestedStartIndex = index
                    while (index < lines.size) {
                        val candidate = lines[index]
                        if (candidate.isMarkdownFenceClose(fenceChar, fenceLength)) break
                        nestedLines += candidate
                        index += 1
                    }
                    if (index == lines.size) {
                        fenceChar = null
                        fenceLength = 0
                        fenceLanguage = null
                    }
                    val nestedCode = nestedLines.joinToString("\n")
                    val nestedTokens =
                        if (language.isNullOrEmpty()) {
                            nestedLines.map { listOf(CodeToken(CodeTokenType.Plain, it)) }
                        } else {
                            PaletteCodeHighlighter.highlight(
                                code = nestedCode,
                                language = language,
                            ).tokens
                        }
                    val prefix = nestedLines.firstOrNull()?.commonPrefixLength() ?: 0
                    nestedTokens.forEachIndexed { nestedIndex, tokens ->
                        if (nestedIndex == 0) {
                            result += tokens
                        } else {
                            val targetLine = nestedLines.getOrElse(nestedIndex) { "" }
                            val linePrefix = targetLine.commonPrefixLength()
                            val effectivePrefix = minOf(prefix, linePrefix)
                            if (effectivePrefix > 0) {
                                result += listOf(CodeToken(CodeTokenType.Plain, targetLine.substring(0, effectivePrefix))) + tokens
                            } else {
                                result += tokens
                            }
                        }
                    }
                    continue
                }
            } else {
                result += highlightMarkdownLine(line)
                if (line.startsMarkdownFence()) {
                    val trimmedStart = line.indexOfFirst { !it.isWhitespace() }.let { if (it == -1) line.length else it }
                    val fenceEnd = line.nextWhile(trimmedStart) { it == line[trimmedStart] }
                    fenceChar = line[trimmedStart]
                    fenceLength = fenceEnd - trimmedStart
                    val infoStart = line.nextWhile(fenceEnd, Char::isWhitespace)
                    fenceLanguage =
                        if (infoStart < line.length) line.substring(infoStart).parseMarkdownFenceLanguage() else null
                }
            }
            index += 1
        }
        return result
    }

    private fun String.commonPrefixLength(): Int {
        var count = 0
        while (count < length && this[count] == ' ') count += 1
        return count
    }

    private fun String.isMarkdownFenceClose(fenceChar: Char, fenceLength: Int): Boolean {
        val trimmedStart = indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }
        if (trimmedStart >= length || this[trimmedStart] != fenceChar) return false
        val fenceEnd = nextWhile(trimmedStart) { it == fenceChar }
        if (fenceEnd - trimmedStart < fenceLength) return false
        val trailingStart = nextWhile(fenceEnd, Char::isWhitespace)
        return trailingStart == length
    }

    private fun String.startsMarkdownFence(): Boolean {
        val trimmedStart = indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }
        if (trimmedStart >= length) return false
        val char = this[trimmedStart]
        if (char != '`' && char != '~') return false
        val fenceEnd = nextWhile(trimmedStart) { it == char }
        if (fenceEnd - trimmedStart < 3) return false
        val infoStart = nextWhile(fenceEnd, Char::isWhitespace)
        val info = if (infoStart < length) substring(infoStart).trim() else ""
        return info.all { it != char }
    }

    private fun highlightMarkdownLine(
        line: String,
    ): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = line.indexOfFirst { !it.isWhitespace() }.let { if (it == -1) line.length else it }
        if (index > 0) {
            tokens += CodeToken(CodeTokenType.Plain, line.substring(0, index))
        }

        when {
            index == line.length -> return tokens
            line.startsMarkdownFence(index) -> {
                val fenceEnd = line.nextWhile(index) { it == line[index] }
                tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, fenceEnd))
                val infoStart = line.nextWhile(fenceEnd, Char::isWhitespace)
                if (infoStart > fenceEnd) {
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(fenceEnd, infoStart))
                }
                if (infoStart < line.length) {
                    tokens += CodeToken(CodeTokenType.Type, line.substring(infoStart))
                }
            }

            line.isMarkdownHeadingAt(index) -> {
                val markerEnd = line.nextWhile(index) { it == '#' }
                tokens += CodeToken(CodeTokenType.Keyword, line.substring(index, markerEnd))
                index = markerEnd
                tokens.addMarkdownInlineTokens(line, index)
            }

            line.startsWith(">", index) -> {
                tokens += CodeToken(CodeTokenType.Operator, ">")
                tokens.addMarkdownInlineTokens(line, index + 1)
            }

            else -> {
                val listMarker = line.markdownListMarker(index)
                if (listMarker != null) {
                    tokens += CodeToken(CodeTokenType.Operator, listMarker.marker)
                    index += listMarker.marker.length
                    val afterMarker = line.nextWhile(index, Char::isWhitespace)
                    if (afterMarker > index) {
                        tokens += CodeToken(CodeTokenType.Plain, line.substring(index, afterMarker))
                    }
                    index = afterMarker
                    val taskMarker = line.markdownTaskMarker(index)
                    if (taskMarker != null) {
                        tokens += CodeToken(CodeTokenType.Annotation, taskMarker)
                        index += taskMarker.length
                    }
                }
                tokens.addMarkdownInlineTokens(line, index)
            }
        }

        return tokens
    }

    private fun String.parseMarkdownFenceLanguage(): String? {
        val info = trim()
        if (info.isEmpty()) return null
        val attributeMatch = Regex("""\{\.([A-Za-z0-9_-]+)""").find(info)
        if (attributeMatch != null) return attributeMatch.groupValues[1].ifEmpty { null }
        val firstToken = info.substringBefore(' ').trim()
        return firstToken.ifEmpty { null }
    }

    private fun MutableList<CodeToken>.addMarkdownInlineTokens(
        line: String,
        start: Int,
    ) {
        var index = start
        while (index < line.length) {
            when {
                line[index].isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    this += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                line[index] == '`' -> {
                    val markerEnd = line.nextWhile(index) { it == '`' }
                    val marker = line.substring(index, markerEnd)
                    val close = line.indexOf(marker, startIndex = markerEnd)
                    val end = if (close == -1) line.length else close + marker.length
                    this += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                line.startsWith("![", index) -> {
                    val read = addMarkdownLinkTokens(line, index, image = true)
                    if (read == index) {
                        this += CodeToken(CodeTokenType.Plain, line[index].toString())
                        index += 1
                    } else {
                        index = read
                    }
                }

                line[index] == '[' -> {
                    val read = addMarkdownLinkTokens(line, index, image = false)
                    if (read == index) {
                        this += CodeToken(CodeTokenType.Plain, line[index].toString())
                        index += 1
                    } else {
                        index = read
                    }
                }

                line.startsWith("<http://", index) || line.startsWith("<https://", index) -> {
                    val end = line.indexOf('>', startIndex = index + 1)
                    if (end == -1) {
                        this += CodeToken(CodeTokenType.Plain, line[index].toString())
                        index += 1
                    } else {
                        this += CodeToken(CodeTokenType.Punctuation, "<")
                        this += CodeToken(CodeTokenType.StringLiteral, line.substring(index + 1, end))
                        this += CodeToken(CodeTokenType.Punctuation, ">")
                        index = end + 1
                    }
                }

                line.startsWith("**", index) || line.startsWith("__", index) || line.startsWith("~~", index) -> {
                    this += CodeToken(CodeTokenType.Operator, line.substring(index, index + 2))
                    index += 2
                }

                line[index] == '*' || line[index] == '_' -> {
                    this += CodeToken(CodeTokenType.Operator, line[index].toString())
                    index += 1
                }

                else -> {
                    var end = line.nextMarkdownPlainEnd(index)
                    if (end == index + 1 && line[index] in setOf('!', '<', '~')) {
                        end = line.nextMarkdownPlainEnd(end)
                    }
                    this += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }
            }
        }
    }

    private fun MutableList<CodeToken>.addMarkdownLinkTokens(
        line: String,
        start: Int,
        image: Boolean,
    ): Int {
        val labelStart = if (image) start + 2 else start + 1
        val labelEnd = line.indexOf(']', startIndex = labelStart)
        val destinationOpen = labelEnd + 1
        if (
            labelEnd == -1 ||
            destinationOpen >= line.length ||
            line[destinationOpen] != '('
        ) {
            return start
        }

        val destinationEnd = line.findMarkdownLinkDestinationEnd(destinationOpen)
        if (destinationEnd == -1) return start

        if (image) {
            this += CodeToken(CodeTokenType.Punctuation, "!")
        }
        this += CodeToken(CodeTokenType.Punctuation, "[")
        this += CodeToken(CodeTokenType.Type, line.substring(labelStart, labelEnd))
        this += CodeToken(CodeTokenType.Punctuation, "]")
        this += CodeToken(CodeTokenType.Punctuation, "(")
        this += CodeToken(CodeTokenType.StringLiteral, line.substring(destinationOpen + 1, destinationEnd))
        this += CodeToken(CodeTokenType.Punctuation, ")")
        return destinationEnd + 1
    }
}

private data class MarkdownListMarker(
    val marker: String,
)

private object DockerfileLexer {
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

    private val DockerfileInstructions =
        setOf(
            "ADD",
            "ARG",
            "CMD",
            "COPY",
            "ENTRYPOINT",
            "ENV",
            "EXPOSE",
            "FROM",
            "HEALTHCHECK",
            "LABEL",
            "MAINTAINER",
            "ONBUILD",
            "RUN",
            "SHELL",
            "STOPSIGNAL",
            "USER",
            "VOLUME",
            "WORKDIR",
        )
    private val DockerfileSecondaryKeywords = setOf("AS")
    private val DockerfilePunctuation = setOf('[', ']', '{', '}', '(', ')', ',', ':', '=')
}

private class SqlLexer {
    private var inBlockComment = false
    private var dollarQuoteDelimiter: String? = null

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            dollarQuoteDelimiter?.let { delimiter ->
                val end = line.indexOf(delimiter, startIndex = index)
                if (end == -1) {
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index))
                    return tokens
                }
                tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end + delimiter.length))
                dollarQuoteDelimiter = null
                index = end + delimiter.length
                continue
            }

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

                current == '`' -> {
                    val end = line.indexOf('`', startIndex = index + 1)
                    val tokenEnd = if (end == -1) line.length else end + 1
                    tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, tokenEnd))
                    index = tokenEnd
                }

                current == '$' -> {
                    val delimiter = line.sqlDollarQuoteDelimiterAt(index)
                    if (delimiter != null) {
                        val close = line.indexOf(delimiter, startIndex = index + delimiter.length)
                        if (close == -1) {
                            tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index))
                            dollarQuoteDelimiter = delimiter
                            index = line.length
                        } else {
                            tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(index, close + delimiter.length))
                            index = close + delimiter.length
                        }
                    } else {
                        tokens += CodeToken(CodeTokenType.Plain, current.toString())
                        index += 1
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
                            normalized in SqlKeywords -> CodeTokenType.Keyword
                            line.nextNonWhitespace(end) == '(' -> CodeTokenType.Function
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
                "ALL",
                "ALTER",
                "AND",
                "ANY",
                "AS",
                "ASC",
                "BETWEEN",
                "BY",
                "CASE",
                "CHECK",
                "CONSTRAINT",
                "CROSS",
                "CURRENT",
                "CREATE",
                "DEFAULT",
                "DELETE",
                "DESC",
                "DISTINCT",
                "DROP",
                "ELSE",
                "END",
                "EXCEPT",
                "EXISTS",
                "FALSE",
                "FETCH",
                "FIRST",
                "FOLLOWING",
                "FOR",
                "FOREIGN",
                "FROM",
                "FULL",
                "GRANT",
                "GROUP",
                "HAVING",
                "IF",
                "IN",
                "INDEX",
                "INNER",
                "INSERT",
                "INTERSECT",
                "INTO",
                "IS",
                "JOIN",
                "KEY",
                "LAST",
                "LEFT",
                "LIKE",
                "LIMIT",
                "NEXT",
                "NO",
                "NOT",
                "NULL",
                "OFFSET",
                "ONLY",
                "OVER",
                "ON",
                "OR",
                "ORDER",
                "OUTER",
                "PARTITION",
                "PRECEDING",
                "PRIMARY",
                "RANGE",
                "REFERENCES",
                "REPLACE",
                "RIGHT",
                "ROWS",
                "SELECT",
                "SET",
                "TABLE",
                "THEN",
                "TO",
                "TRUE",
                "UNBOUNDED",
                "UNION",
                "UNIQUE",
                "UPDATE",
                "USING",
                "VALUES",
                "VIEW",
                "WHEN",
                "WHERE",
                "WITH",
            )
        val SqlTypes =
            setOf(
                "BIGINT",
                "BINARY",
                "BLOB",
                "BOOLEAN",
                "CHAR",
                "CHARACTER",
                "CLOB",
                "DATE",
                "DECIMAL",
                "DOUBLE",
                "FLOAT",
                "INTEGER",
                "INT",
                "JSON",
                "JSONB",
                "NCHAR",
                "NCLOB",
                "NUMERIC",
                "NVARCHAR",
                "REAL",
                "SERIAL",
                "SMALLINT",
                "TEXT",
                "TIME",
                "TIMESTAMP",
                "TINYINT",
                "UUID",
                "VARBINARY",
                "VARCHAR",
            )
        val SqlOperators = setOf('+', '-', '*', '/', '%', '=', '!', '<', '>', '|')
        val SqlPunctuation = setOf('(', ')', ',', '.', ';')
    }

    private fun String.sqlDollarQuoteDelimiterAt(start: Int): String? {
        if (getOrNull(start) != '$') return null
        var index = start + 1
        while (index < length && (this[index].isLetterOrDigit() || this[index] == '_')) {
            index += 1
        }
        if (getOrNull(index) != '$') return null
        return substring(start, index + 1)
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

private fun String.nextTomlBareKeyEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.') }

private fun String.nextTomlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('-', '+', '.', '_', ':') }

private fun String.nextIniWordEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.', '/', ':') }

private fun String.nextGraphQlNameEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' }

private fun String.nextGraphQlVariableEnd(start: Int): Int =
    (start + 1).let { nameStart ->
        nextWhile(nameStart) { it.isLetterOrDigit() || it == '_' }.coerceAtLeast(nameStart)
    }

private fun String.nextGraphQlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' }

private fun String.nextSqlIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '$' }

private fun String.nextSqlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '.' }

private fun String.nextDockerfileWordEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.', '/', ':', '@') }

private fun String.nextDockerfileFlagEnd(start: Int): Int =
    nextWhile(start) { !it.isWhitespace() && it !in setOf('[', ']', '{', '}', '(', ')', ',', '"', '\'') }

private fun String.nextDockerfileVariableEnd(start: Int): Int =
    when (getOrNull(start + 1)) {
        '{' -> {
            val end = indexOf('}', startIndex = start + 2)
            if (end == -1) length else end + 1
        }

        else -> nextWhile(start + 1) { it.isLetterOrDigit() || it == '_' }.coerceAtLeast(start + 1)
    }

private fun String.nextMarkdownPlainEnd(start: Int): Int {
    val end = nextWhile(start) { it !in setOf('`', '[', '!', '<', '*', '_', '~') }
    return if (end == start) (start + 1).coerceAtMost(length) else end
}

private fun String.findIniPropertySeparator(start: Int): Int {
    var index = start
    var escaped = false
    while (index < length) {
        val current = this[index]
        when {
            escaped -> escaped = false
            current == '\\' -> escaped = true
            current == '=' || current == ':' -> return index
            current.isWhitespace() -> {
                val next = nextWhile(index, Char::isWhitespace)
                if (next < length && this[next] !in setOf('#', ';')) return index
                index = next
                continue
            }
        }
        index += 1
    }
    return -1
}

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

private fun Char.isTomlBareKeyStart(): Boolean = isLetter() || this == '_' || this == '-'

private fun Char.isIniWordStart(): Boolean = isLetter() || isDigit() || this in setOf('_', '-', '.', '/')

private fun Char.isGraphQlNameStart(): Boolean = isLetter() || this == '_'

private fun Char.isSqlIdentifierStart(): Boolean = isLetter() || this == '_'

private fun Char.isDockerfileWordStart(): Boolean = isLetterOrDigit() || this in setOf('_', '.', '/', '$')

private fun String.startsMarkdownFence(index: Int): Boolean {
    val marker = getOrNull(index)?.takeIf { it == '`' || it == '~' } ?: return false
    return nextWhile(index) { it == marker } - index >= 3
}

private fun String.isMarkdownHeadingAt(index: Int): Boolean {
    if (getOrNull(index) != '#') return false
    val markerEnd = nextWhile(index) { it == '#' }
    return markerEnd - index in 1..6 && getOrNull(markerEnd)?.isWhitespace() == true
}

private fun String.markdownListMarker(index: Int): MarkdownListMarker? {
    val marker =
        when {
            getOrNull(index) in setOf('-', '*', '+') && getOrNull(index + 1)?.isWhitespace() == true ->
                get(index).toString()
            getOrNull(index)?.isDigit() == true -> {
                val numberEnd = nextWhile(index, Char::isDigit)
                if (
                    getOrNull(numberEnd) in setOf('.', ')') &&
                    getOrNull(numberEnd + 1)?.isWhitespace() == true
                ) {
                    substring(index, numberEnd + 1)
                } else {
                    null
                }
            }
            else -> null
        }
    return marker?.let { MarkdownListMarker(it) }
}

private fun String.markdownTaskMarker(index: Int): String? {
    val marker = substring(index, (index + 3).coerceAtMost(length))
    return marker.takeIf {
        length >= index + 3 &&
            marker.first() == '[' &&
            marker.last() == ']' &&
            marker[1] in setOf(' ', 'x', 'X')
    }
}

private fun String.findMarkdownLinkDestinationEnd(openingParenIndex: Int): Int {
    var index = openingParenIndex + 1
    var nestedParentheses = 0
    var quote: Char? = null
    var escaped = false
    while (index < length) {
        val char = this[index]
        when {
            escaped -> escaped = false
            char == '\\' -> escaped = true
            quote != null -> if (char == quote) quote = null
            char == '"' || char == '\'' -> quote = char
            char == '(' -> nestedParentheses += 1
            char == ')' && nestedParentheses > 0 -> nestedParentheses -= 1
            char == ')' -> return index
        }
        index += 1
    }
    return -1
}

private fun String.isHexColorToken(): Boolean {
    val value = drop(1)
    return value.length in setOf(3, 4, 6, 8) && value.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
}

private fun String.tripleQuoteDelimiterAt(start: Int): String? {
    val quote = getOrNull(start) ?: return null
    if (quote != '"' && quote != '\'') return null
    val delimiter = "$quote$quote$quote"
    return delimiter.takeIf { startsWith(it, start) }
}

private fun scanMultilineStringSegment(
    line: String,
    start: Int,
    delimiter: String,
    skipOpeningDelimiter: Boolean,
): Int {
    val end = findMultilineStringClose(line, start, delimiter, skipOpeningDelimiter)
    return if (end == -1) line.length else end + delimiter.length
}

private fun hasMultilineStringClose(
    line: String,
    start: Int,
    delimiter: String,
    skipOpeningDelimiter: Boolean,
): Boolean = findMultilineStringClose(line, start, delimiter, skipOpeningDelimiter) != -1

private fun findMultilineStringClose(
    line: String,
    start: Int,
    delimiter: String,
    skipOpeningDelimiter: Boolean,
): Int {
    val searchStart = if (skipOpeningDelimiter) start + delimiter.length else start
    return line.indexOf(delimiter, startIndex = searchStart)
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
        index += 1
    }
    return line.length
}
