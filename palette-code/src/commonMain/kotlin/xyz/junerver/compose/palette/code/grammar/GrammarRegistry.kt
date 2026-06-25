package xyz.junerver.compose.palette.code.grammar

import xyz.junerver.compose.palette.code.grammar.languages.JsonGrammar
// MarkdownGrammar is intentionally not registered yet (see comment below).

/**
 * Registry of declarative grammars, keyed by lowercased language id (incl. aliases).
 *
 Used by [xyz.junerver.compose.palette.code.PaletteCodeHighlighter] as a first-choice
 * highlighter: when a language has a registered grammar, a [GrammarHighlighter] wraps it;
 * otherwise the lookup falls back to the hand-written lexer path. This lets grammars and
 * lexers coexist while languages migrate incrementally.
 */
internal object GrammarRegistry {
    private val grammars: Map<String, Grammar> = buildMap {
        putAll(aliases("json", listOf("json"), JsonGrammar))
        // Markdown grammar exists and the engine handles it, but it stays on the hand-written
        // MarkdownLexer for now until its token classification is aligned with the existing
        // tests (Phase 2 lexer-migration task). Add it back once the classification matches.
    }

    /** Returns a [GrammarHighlighter] for [language], or null if no grammar is registered. */
    fun highlighterOrNull(language: String): GrammarHighlighter? {
        val grammar = grammars[language.lowercase()] ?: return null
        return GrammarHighlighter(grammar)
    }

    private fun aliases(
        primary: String,
        alts: List<String>,
        grammar: Grammar,
    ): Map<String, Grammar> = (alts + primary).associate { it.lowercase() to grammar }
}
