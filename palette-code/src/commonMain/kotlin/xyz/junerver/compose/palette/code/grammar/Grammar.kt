package xyz.junerver.compose.palette.code.grammar

import kotlin.jvm.JvmInline

/**
 * A token type label. Matches Prism's `TokenName`: a `StandardTokenName` (e.g. "keyword",
 * "string", "class-name") or any custom string. [Plain] represents un-tokenized text.
 *
 * Token types map to render colors via [xyz.junerver.compose.palette.code.CodeTokenType].
 */
@JvmInline
value class TokenType(val name: String) {
    companion object {
        val Plain = TokenType("plain")
    }
}

/**
 * A single token produced by [GrammarTokenizer]: a [type] label, the matched [text], and any
 * extra [aliases] (Prism's `alias`). Aliases let a token carry a secondary classification,
 * e.g. a "bold" token aliased as "important".
 */
data class GrammarTokenValue(
    val type: TokenType,
    val text: String,
    val aliases: List<String> = emptyList(),
)

/**
 * A grammar rule, mirroring Prism's `GrammarToken` (`src/types.d.ts`).
 *
 * @property pattern the regex to match. Capturing group 1 (when [lookbehind] is true) or the
 *   whole match is the token text.
 * @property lookbehind when true, group 1 of [pattern] is treated as a lookbehind: its text is
 *   stripped from the token and kept as preceding plain text (Prism `lookbehind`).
 * @property greedy when true, this token may match a span that starts inside an earlier plain
 *   chunk, re-tokenizing that chunk. Enables correct highlighting of ambiguous constructs like
 *   strings that can appear mid-identifier (Prism `greedy`).
 * @property alias extra type names attached to every produced token of this rule (Prism `alias`).
 * @property inside an optional nested [Grammar] used to recursively tokenize the matched text
 *   (Prism `inside`). Enables language embedding (e.g. CSS inside HTML `<style>`).
 * @property languageResolver an optional dynamic-embedding hook: when [inside] is null and this
 *   is set, the matched text is re-tokenized with the [Grammar] this resolver returns. Unlike
 *   [inside] (a fixed grammar), the resolver decides the grammar per-match — e.g. a Markdown
 *   fenced-code rule inspects the `` ```kotlin `` info string to pick the Kotlin grammar, or an
 *   HTML `<style>`/`<script>` rule picks css/js. This is the engine feature that lets grammar
 *   replace the hand-written embed dispatch in HtmlLexer/MarkdownLexer.
 * @property embeddedTokens the most general embedding hook: returns a pre-tokenized token list
 *   for the matched text directly, bypassing recursive re-tokenization. Used when the embedded
 *   language has no registered [Grammar] and must run through the full
 *   [xyz.junerver.compose.palette.code.PaletteCodeHighlighter] (which falls back to a lexer) —
 *   e.g. Markdown fenced code whose info string is `kotlin`, a lexer-backed language.
 * @property matcher an optional custom scanner used when a [pattern] regex can't express the
 *   construct — i.e. for **non-regular** structures. Given the whole text and a candidate start
 *   position, it returns the exclusive end index of the token (or null if there's no match
 *   starting there). The tokenizer tries [matcher] at each position before [pattern]. This lets
 *   grammars model nested delimiters (Kotlin `/* /* */ */`) and indentation-scoped spans (YAML
 *   block scalars) that a single-pass regex cannot.
 */
data class GrammarToken(
    val pattern: Regex,
    val lookbehind: Boolean = false,
    val greedy: Boolean = false,
    val alias: List<String> = emptyList(),
    val inside: Grammar? = null,
    val languageResolver: ((matchText: String) -> Grammar?)? = null,
    val embeddedTokens: ((matchText: String) -> List<GrammarTokenValue>?)? = null,
    val matcher: ((text: String, start: Int) -> Int?)? = null,
)

/**
 * A grammar: an ordered map of token-name to rule(s). Order matters — earlier rules take
 * priority on overlapping matches, matching Prism's grammar semantics.
 */
class Grammar(rules: Map<String, GrammarToken>) {
    private val entries: List<Pair<String, GrammarToken>> = rules.entries.flatMap { (name, rule) ->
        listOf(name to rule)
    }

    /** The ordered (name → rule) list; iteration order is the grammar's declared order. */
    fun rules(): List<Pair<String, GrammarToken>> = entries

    companion object
}

/** Convenience builder mirroring Prism's grammar literal syntax. */
fun grammarOf(vararg rules: Pair<String, GrammarToken>): Grammar = Grammar(rules.toMap())
