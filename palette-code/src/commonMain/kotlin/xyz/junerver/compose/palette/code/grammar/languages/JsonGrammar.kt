package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * JSON grammar — the simplest pilot grammar: pure token classification with no nesting.
 *
 * Rules (ordered by priority): strings (keys vs values), booleans/null, numbers, and
 * punctuation. Declared declaratively instead of the hand-written [JsonLexer], demonstrating
 * the grammar-engine workflow that new languages follow.
 */
internal val JsonGrammar: Grammar = grammarOf(
    // Strings, including property keys. Classified as "string" (→ StringLiteral) to match
    // the original JsonLexer's behaviour where keys are also strings.
    "string" to GrammarToken(
        pattern = Regex(""""(?:\\.|[^"\\]*)""""),
    ),
    "number" to GrammarToken(
        pattern = Regex("""-?\b\d+(?:\.\d+)?(?:[eE][+-]?\d+)?\b"""),
    ),
    // Booleans/null map to keyword (matching the original lexer) rather than Prism's boolean/
    // constant, so existing JSON highlighting stays identical.
    "keyword" to GrammarToken(
        pattern = Regex("""\b(?:true|false|null)\b"""),
    ),
    // ':' is an operator; structural punctuation (braces/brackets/comma) is punctuation.
    "operator" to GrammarToken(
        pattern = Regex(""":"""),
    ),
    "punctuation" to GrammarToken(
        pattern = Regex("""[{}\[\],]"""),
    ),
)
