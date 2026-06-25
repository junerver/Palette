package xyz.junerver.compose.palette.code

import xyz.junerver.compose.palette.code.lexer.*

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
    // ── Prism StandardTokenName alignment (additive; appended to preserve ordinal order) ──
    /** `true`/`false` literals, distinct from Keyword (Prism `boolean`). */
    Boolean,
    /** Single character literal, e.g. `'a'` (Prism `char`). */
    Char,
    /** Regular expression literal, e.g. `/\d+/` (Prism `regex`). */
    Regex,
    /** Symbolic operator or atom, e.g. Lisp/Ruby/Erlang atoms (Prism `symbol`). */
    Symbol,
    /** URL / link literal (Prism `url`). */
    Url,
    /** CSS selector (Prism `selector`). */
    Selector,
    /** HTML/XML tag name (Prism `tag`). */
    Tag,
    /** HTML/XML attribute name (Prism `attr-name`). */
    AttrName,
    /** HTML/XML attribute value (Prism `attr-value`). */
    AttrValue,
    /** `<!DOCTYPE ...>` declaration (Prism `doctype`). */
    Doctype,
    /** HTML entity, e.g. `&amp;` (Prism `entity`). */
    Entity,
    /** XML prolog, e.g. `<?xml ...?>` (Prism `prolog`). */
    Prolog,
    /** CDATA section, e.g. `<![CDATA[...]]>` (Prism `cdata`). */
    Cdata,
    /** HTML/SVG attribute, e.g. `class="..."` (Prism `atrule`). */
    Atrule,
    /** Bold marker, rendered with weight (Prism `bold`). */
    Bold,
    /** Italic marker, rendered with slant (Prism `italic`). */
    Italic,
    /** Important / emphasized, e.g. Markdown `!!text!!` (Prism `important`). */
    Important,
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

    private fun String.builtInHighlighterOrNull(): PaletteCodeLanguageHighlighter? {
        // Declarative grammars take precedence over hand-written lexers. This lets new and
        // migrating languages be defined as a grammar object while the rest keep their lexers
        // during the incremental migration to the grammar engine.
        xyz.junerver.compose.palette.code.grammar.GrammarRegistry
            .highlighterOrNull(this)
            ?.let { return it }
        return when (this) {
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
            "html", "xml", "svg" -> PaletteCodeLanguageHighlighter { lines -> HtmlLexer { content, lang -> highlight(content, lang).tokens.singleOrNull() }.highlight(lines) }
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
    }

    private fun List<String>.toPlainCodeTokens(): List<List<CodeToken>> =
        map { line -> listOf(CodeToken(CodeTokenType.Plain, line)) }
}


