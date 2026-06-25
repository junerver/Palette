package xyz.junerver.compose.palette.code.grammar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GrammarTokenizerTest {
    @Test
    fun matchesBasicPatternAsToken() {
        val grammar = grammarOf(
            "keyword" to token(Regex("""\b(if|else)\b""")),
        )
        val tokens = GrammarTokenizer.tokenize("if x else", grammar)

        assertEquals(TokenType("keyword"), tokens[0].type)
        assertEquals("if", tokens[0].text)
        // The unmatched middle text stays plain.
        assertEquals(TokenType.Plain, tokens[1].type)
        assertEquals(" x ", tokens[1].text)
        assertEquals(TokenType("keyword"), tokens[2].type)
        assertEquals("else", tokens[2].text)
    }

    @Test
    fun unMatchedTextBecomesPlainTokens() {
        val grammar = grammarOf("number" to token(Regex("""\d+""")))
        val tokens = GrammarTokenizer.tokenize("abc 123 def", grammar)

        assertEquals(TokenType.Plain, tokens[0].type)
        assertEquals("abc ", tokens[0].text)
        assertEquals(TokenType("number"), tokens[1].type)
        assertEquals("123", tokens[1].text)
        assertEquals(" def", tokens[2].text)
    }

    @Test
    fun lookbehindStripsCaptureGroupFromToken() {
        // Group 1 ("\\.") is the lookbehind context: it precedes the property name and must be
        // kept as plain text, while the property name becomes the token.
        val grammar = grammarOf(
            "property" to GrammarToken(pattern = Regex("""(\.)[A-Za-z_]\w*"""), lookbehind = true),
        )
        val tokens = GrammarTokenizer.tokenize("obj.prop", grammar)

        // "obj" plus the lookbehind prefix "." merge into a single plain span ("obj."), and
        // "prop" becomes the property token — the lookbehind did not leak into the token text.
        assertEquals(TokenType.Plain, tokens[0].type)
        assertEquals("obj.", tokens[0].text)
        val propToken = tokens.first { it.type == TokenType("property") }
        assertEquals("prop", propToken.text)
    }

    @Test
    fun greedyWinsOverEarlierNonGreedyMatch() {
        // A greedy string pattern can match a span that the earlier word rule would also touch,
        // demonstrating that greedy rules re-scan plain prefixes.
        val grammar = grammarOf(
            "string" to GrammarToken(pattern = Regex(""""[^"]*""""), greedy = true),
            "word" to token(Regex("""\w+""")),
        )
        val tokens = GrammarTokenizer.tokenize("word \"a b\"", grammar)

        // "word" is matched by the word rule; the quoted span (including the inner space) is a
        // single string token rather than being split by the word rule.
        assertEquals(TokenType("word"), tokens[0].type)
        assertEquals("word", tokens[0].text)
        assertTrue(tokens.any { it.type == TokenType("string") && it.text == "\"a b\"" })
    }

    @Test
    fun insideRecursivelyTokenizesMatchedText() {
        // An "attr" token matches `key="value"`; its `inside` grammar splits the quoted value
        // out as a "string" token.
        val grammar = grammarOf(
            "attr" to GrammarToken(
                pattern = Regex("""\w+="[^"]*""""),
                inside = grammarOf("string" to GrammarToken(pattern = Regex(""""[^"]*""""))),
            ),
        )
        val tokens = GrammarTokenizer.tokenize("class=\"main\"", grammar)

        // Result: key="main" split into [class=] plain + ["main"] string.
        assertTrue(tokens.any { it.type == TokenType("string") && it.text == "\"main\"" })
        assertTrue(tokens.any { it.type == TokenType.Plain && it.text == "class=" })
    }

    @Test
    fun aliasAttachesAdditionalType() {
        val grammar = grammarOf(
            "bold" to GrammarToken(
                pattern = Regex("""\*\*[^*]+\*\*"""),
                alias = listOf("important"),
            ),
        )
        val tokens = GrammarTokenizer.tokenize("**hi**", grammar)

        val matched = tokens.first { it.type == TokenType("bold") }
        assertTrue(matched.aliases.contains("important"))
    }

    @Test
    fun tokenizeEmptyTextReturnsSinglePlainToken() {
        val grammar = grammarOf("word" to token(Regex("""\w+""")))
        val tokens = GrammarTokenizer.tokenize("", grammar)
        assertEquals(1, tokens.size)
        assertEquals("", tokens[0].text)
        assertEquals(TokenType.Plain, tokens[0].type)
    }

    private fun grammarOf(vararg pairs: Pair<String, GrammarToken>) = Grammar(pairs.toMap())

    private fun token(pattern: Regex) = GrammarToken(pattern = pattern)
}
