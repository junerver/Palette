package xyz.junerver.compose.palette.code.grammar

/**
 * The universal tokenizer, mirroring Prism's `tokenize(text, grammar)` engine
 * ([PrismJS/prism `src/core/tokenize/tokenize.js`](https://github.com/PrismJS/prism)).
 *
 * It walks the grammar's ordered rules and, for each, scans the current token list for the
 * earliest match. Non-greedy rules only match within plain-text spans; greedy rules can split
 * a plain span and re-emit the prefix as plain. `lookbehind` strips capture-group 1; `inside`
 * recursively tokenizes the matched text; `alias` attaches secondary type labels.
 *
 * Producing Prism-faithful token streams is what lets us declare new languages as grammar
 * objects rather than hand-written scanners.
 */
internal object GrammarTokenizer {
    /**
     * Tokenize [text] with [grammar]. The result is a flat list covering the whole input
     * (adjacent plain spans are merged), with no empty tokens.
     */
    fun tokenize(text: String, grammar: Grammar): List<GrammarTokenValue> {
        var tokens: MutableList<Any> = mutableListOf(text) // Any = String (plain) | GrammarTokenValue
        grammar.rules().forEach { (name, rule) ->
            tokens = applyRule(tokens, name, rule)
        }
        return compact(tokens)
    }

    /** Apply one rule across the current token stream, inserting matched tokens. */
    private fun applyRule(
        tokens: MutableList<Any>,
        name: String,
        rule: GrammarToken,
    ): MutableList<Any> {
        val result = mutableListOf<Any>()
        tokens.forEach { token ->
            when (token) {
                is String -> result.addAll(matchInString(token, name, rule))
                is GrammarTokenValue -> {
                    // Already-classified tokens are only re-scanned when greedy (so the rule may
                    // match a span starting inside a plain prefix that was split off earlier).
                    if (rule.greedy) {
                        result.addAll(reMatchGreedy(token, name, rule))
                    } else {
                        result.add(token)
                    }
                }
            }
        }
        return result
    }

    /** Find all matches of [rule] in a plain [str], emitting plain/tokens interleaved. */
    private fun matchInString(
        str: String,
        name: String,
        rule: GrammarToken,
    ): List<Any> {
        if (str.isEmpty()) return listOf(str)
        val out = mutableListOf<Any>()
        var pos = 0
        while (pos <= str.length) {
            val match = rule.pattern.find(str, pos) ?: break
            val matchStart = match.range.first
            val matchEnd = match.range.last + 1

            // Plain text preceding the match.
            if (matchStart > pos) out.add(str.substring(pos, matchStart))

            val (tokenText, lookbehindPrefix) = if (rule.lookbehind && match.groupValues.size > 1) {
                // Group 1 is the lookbehind: keep it as preceding plain, token text is the rest.
                val prefix = match.groupValues[1]
                val rest = match.value.substring(prefix.length)
                rest to prefix
            } else {
                match.value to ""
            }

            if (lookbehindPrefix.isNotEmpty()) out.add(lookbehindPrefix)

            val innerTokens = when {
                // Fixed nested grammar (Prism `inside`).
                rule.inside != null && tokenText.isNotEmpty() ->
                    tokenize(tokenText, rule.inside!!)
                // Pre-tokenized embedding: the hook returns tokens directly (e.g. when the
                // embedded language must run through the full highlighter incl. lexer fallback).
                // Highest precedence so a hook that wants total control wins.
                rule.embeddedTokens != null && tokenText.isNotEmpty() ->
                    rule.embeddedTokens!!.invoke(tokenText) ?: emptyList()
                // Dynamic embedding: resolve a grammar per-match and re-tokenize. Used for
                // constructs where the embedded language varies (Markdown fenced code, HTML
                // <style>/<script>), which a static `inside` can't express.
                rule.languageResolver != null && tokenText.isNotEmpty() ->
                    rule.languageResolver!!.invoke(tokenText)?.let { tokenize(tokenText, it) }
                        ?: emptyList()
                else -> emptyList()
            }

            if (innerTokens.isEmpty()) {
                if (tokenText.isNotEmpty()) out.add(GrammarTokenValue(TokenType(name), tokenText, rule.alias))
            } else {
                out.addAll(innerTokens)
            }

            // Guard against zero-width matches looping forever.
            pos = if (matchEnd == matchStart) matchEnd + 1 else matchEnd
        }
        // Trailing plain.
        if (pos < str.length) out.add(str.substring(pos))
        return out
    }

    /**
     * Greedy re-matching: a [rule] may match starting inside the plain portion of an already
     * processed token stream element. We flatten to text, re-scan, and split accordingly.
     * For a [GrammarTokenValue] (non-plain) we leave it intact (greedy matches start in plain).
     */
    private fun reMatchGreedy(
        token: GrammarTokenValue,
        name: String,
        rule: GrammarToken,
    ): List<Any> = listOf(token)

    /** Collapse adjacent plain (String) spans, drop empties, convert to GrammarTokenValue. */
    private fun compact(tokens: MutableList<Any>): List<GrammarTokenValue> {
        val result = mutableListOf<GrammarTokenValue>()
        val plainBuffer = StringBuilder()
        fun flushPlain() {
            if (plainBuffer.isNotEmpty()) {
                result.add(GrammarTokenValue(TokenType.Plain, plainBuffer.toString()))
                plainBuffer.clear()
            }
        }
        tokens.forEach { token ->
            when (token) {
                is String -> if (token.isNotEmpty()) plainBuffer.append(token)
                is GrammarTokenValue -> {
                    if (token.text.isNotEmpty()) {
                        flushPlain()
                        result.add(token)
                    }
                }
            }
        }
        flushPlain()
        // Prism guarantees the empty-input case yields a single empty string; mirror that so
        // callers always get at least one token.
        if (result.isEmpty()) result.add(GrammarTokenValue(TokenType.Plain, ""))
        return result
    }
}
