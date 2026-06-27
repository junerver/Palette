package xyz.junerver.compose.palette.code.grammar

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType
import xyz.junerver.compose.palette.code.PaletteCodeLanguageHighlighter

/**
 * Bridges the declarative [Grammar] world to the line-oriented
 * [PaletteCodeLanguageHighlighter] API that the rest of the codebase consumes.
 *
 * It tokenizes the whole source at once (so multi-line constructs work naturally), then
 * re-splits the flat token stream back into per-line token lists to match the existing
 * `highlight(lines) -> List<List<CodeToken>>` contract.
 *
 * Token *type names* (Prism-style strings like "string", "property") are mapped to
 * [CodeTokenType] via [GrammarTokenTypeMapping]; unknown names fall back to [CodeTokenType.Plain].
 */
internal class GrammarHighlighter(
    private val grammar: Grammar,
) : PaletteCodeLanguageHighlighter {
    override fun highlight(lines: List<String>): List<List<CodeToken>> {
        if (lines.isEmpty()) return emptyList()
        val joined = lines.joinToString("\n")
        val rawTokens = GrammarTokenizer.tokenize(joined, grammar)

        // Split the flat token stream into per-line buckets. A token's text may itself span
        // newlines; in that case each line segment becomes its own token of the same type.
        val result = ArrayList<MutableList<CodeToken>>(lines.size)
        repeat(lines.size) { result.add(mutableListOf()) }
        var lineIndex = 0
        rawTokens.forEach { token ->
            val codeType = GrammarTokenTypeMapping.toCodeTokenType(token.type)
            val segments = token.text.split("\n")
            segments.forEachIndexed { segIndex, segment ->
                if (segIndex > 0) lineIndex += 1
                if (lineIndex >= result.size) return@forEachIndexed
                if (segment.isNotEmpty()) {
                    result[lineIndex].add(CodeToken(codeType, segment))
                }
            }
        }
        return result
    }
}

/** Maps Prism-style token type names to [CodeTokenType]. Extensible as new grammars land. */
internal object GrammarTokenTypeMapping {
    private val map: Map<String, CodeTokenType> = mapOf(
        "plain" to CodeTokenType.Plain,
        "keyword" to CodeTokenType.Keyword,
        "string" to CodeTokenType.StringLiteral,
        // Triple-quoted strings are emitted as a separate rule name (Kotlin/Python) but classify
        // as the same literal type.
        "triple" to CodeTokenType.StringLiteral,
        "number" to CodeTokenType.NumberLiteral,
        "comment" to CodeTokenType.Comment,
        "function" to CodeTokenType.Function,
        "function-definition" to CodeTokenType.Function,
        "boolean" to CodeTokenType.Boolean,
        "builtin" to CodeTokenType.Builtin,
        "class-name" to CodeTokenType.ClassName,
        "constant" to CodeTokenType.Constant,
        "operator" to CodeTokenType.Operator,
        // YAML list markers share the operator type but use a distinct rule name (lookbehind).
        "list-marker" to CodeTokenType.Operator,
        "punctuation" to CodeTokenType.Punctuation,
        "property" to CodeTokenType.Property,
        "variable" to CodeTokenType.Variable,
        "annotation" to CodeTokenType.Annotation,
        "type" to CodeTokenType.Type,
        "namespace" to CodeTokenType.Namespace,
        "char" to CodeTokenType.Char,
        "regex" to CodeTokenType.Regex,
        "symbol" to CodeTokenType.Symbol,
        "url" to CodeTokenType.Url,
        "selector" to CodeTokenType.Selector,
        "tag" to CodeTokenType.Tag,
        "attr-name" to CodeTokenType.AttrName,
        "attr-value" to CodeTokenType.AttrValue,
        "doctype" to CodeTokenType.Doctype,
        "entity" to CodeTokenType.Entity,
        "prolog" to CodeTokenType.Prolog,
        "cdata" to CodeTokenType.Cdata,
        "atrule" to CodeTokenType.Atrule,
        "bold" to CodeTokenType.Bold,
        "italic" to CodeTokenType.Italic,
        "important" to CodeTokenType.Important,
        // diff
        "inserted" to CodeTokenType.Inserted,
        "deleted" to CodeTokenType.Deleted,
    )

    fun toCodeTokenType(type: TokenType): CodeTokenType = map[type.name] ?: CodeTokenType.Plain

    /** Reverse lookup: best Prism-style type name for a [CodeTokenType]. Used when a lexer's
     *  output is fed back into the grammar engine (e.g. Markdown fenced-code embedding via
     *  [PaletteCodeHighlighter], which may resolve to a lexer-backed language). */
    private val reverse: Map<CodeTokenType, String> =
        map.entries.associate { (name, codeType) -> codeType to name }

    fun toTokenTypeName(codeType: CodeTokenType): String = reverse[codeType] ?: "plain"
}
