package xyz.junerver.compose.palette.code.grammar

import xyz.junerver.compose.palette.code.grammar.languages.CssGrammar
import xyz.junerver.compose.palette.code.grammar.languages.HtmlGrammar
import xyz.junerver.compose.palette.code.grammar.languages.IniGrammar
import xyz.junerver.compose.palette.code.grammar.languages.JavaGrammar
import xyz.junerver.compose.palette.code.grammar.languages.JsonGrammar
import xyz.junerver.compose.palette.code.grammar.languages.KotlinGrammar
import xyz.junerver.compose.palette.code.grammar.languages.KotlinLikeGrammar
import xyz.junerver.compose.palette.code.grammar.languages.MarkdownGrammar
import xyz.junerver.compose.palette.code.grammar.languages.PythonGrammar
import xyz.junerver.compose.palette.code.grammar.languages.SqlGrammar
import xyz.junerver.compose.palette.code.grammar.languages.TomlGrammar
import xyz.junerver.compose.palette.code.grammar.languages.TypeScriptGrammar
import xyz.junerver.compose.palette.code.grammar.languages.YamlGrammar
// MarkdownGrammar is intentionally not registered yet (see comment below).

/**
 * Registry of declarative grammars, keyed by lowercased language id (incl. aliases).
 *
 * Used by [xyz.junerver.compose.palette.code.PaletteCodeHighlighter] as a first-choice
 * highlighter: when a language has a registered grammar, a [GrammarHighlighter] wraps it;
 * otherwise the lookup falls back to the hand-written lexer path. This lets grammars and
 * lexers coexist while languages migrate incrementally.
 */
internal object GrammarRegistry {
    private val grammars: Map<String, Grammar> = buildMap {
        putAll(aliases("json", listOf("json"), JsonGrammar))
        putAll(aliases("toml", listOf("toml"), TomlGrammar))
        putAll(aliases("css", listOf("css"), CssGrammar))
        // Kotlin-like grammar serves JavaScript (HTML <script> embedding) for now.
        putAll(aliases("javascript", listOf("javascript", "js"), KotlinLikeGrammar))
        // Java & TypeScript migrate fully (non-nested comments, template literals as one token).
        putAll(aliases("java", listOf("java"), JavaGrammar))
        putAll(aliases("typescript", listOf("typescript", "ts"), TypeScriptGrammar))
        // Kotlin migrates fully now: nested block comments via a custom depth-counting matcher,
        // ${}/$var interpolation via 'inside'. The matcher primitive unblocks the last lexer-only
        // construct.
        putAll(aliases("kotlin", listOf("kotlin", "kt", "kts"), KotlinGrammar))
        // HTML/XML/SVG share one markup grammar; embedding resolvers look css/js up above.
        putAll(aliases("html", listOf("html", "xml", "svg"), HtmlGrammar))
        // SQL dialects share one grammar; dollar-quoted strings use a backreference so the
        // $tag$…$tag$ pair always matches (handles Postgres function bodies).
        putAll(aliases("sql", listOf("sql", "mysql", "postgresql", "postgres", "sqlite"), SqlGrammar))
        // Markdown: fenced-code embedding uses the embeddedTokens hook, which delegates to the
        // full PaletteCodeHighlighter (grammar-first, lexer fallback) so lexer-backed embedded
        // languages like `kotlin` highlight correctly.
        putAll(aliases("markdown", listOf("markdown", "md"), MarkdownGrammar))
        // Python: f-strings modelled via `inside` (string/operator/annotation boundaries),
        // triple-quoted strings via (?s) multi-line regex.
        putAll(aliases("python", listOf("python", "py"), PythonGrammar))
        // YAML: block scalars (|/>) via a custom indentation-scoped matcher; the last non-regular
        // construct. Keys/anchors/aliases/tags/comments classified declaratively.
        putAll(aliases("yaml", listOf("yaml", "yml"), YamlGrammar))
        // INI + .properties share one grammar (same lexer historically served both).
        putAll(aliases("ini", listOf("ini", "properties", "props", "conf"), IniGrammar))
        // Markdown grammar exists and the engine handles it, but it stays on the hand-written
        // MarkdownLexer for now until its token classification is aligned with the existing
        // tests (Phase 2 lexer-migration task). Add it back once the classification matches.
    }

    /** Returns a [GrammarHighlighter] for [language], or null if no grammar is registered. */
    fun highlighterOrNull(language: String): GrammarHighlighter? {
        val grammar = grammars[language.lowercase()] ?: return null
        return GrammarHighlighter(grammar)
    }

    /**
     * Returns the registered [Grammar] for [language], or null. Used by dynamic-embedding
     * resolvers (e.g. the HTML grammar's `<style>`/`<script>` rule) to look up the embedded
     * language's grammar at tokenize time.
     */
    fun grammarOrNull(language: String): Grammar? = grammars[language.lowercase()]

    private fun aliases(
        primary: String,
        alts: List<String>,
        grammar: Grammar,
    ): Map<String, Grammar> = (alts + primary).associate { it.lowercase() to grammar }
}
