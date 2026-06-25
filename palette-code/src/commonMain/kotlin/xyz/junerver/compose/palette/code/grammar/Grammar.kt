package xyz.junerver.compose.palette.code.grammar

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
 */
data class GrammarToken(
    val pattern: Regex,
    val lookbehind: Boolean = false,
    val greedy: Boolean = false,
    val alias: List<String> = emptyList(),
    val inside: Grammar? = null,
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
