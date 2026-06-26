package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * TOML grammar — declarative replacement for [xyz.junerver.compose.palette.code.lexer.TomlLexer].
 *
 * TOML is line-oriented and largely stateless from a highlighting view: table headers
 * (`[name]` / `[[name]]`) start with a bracket at line start, keys are bare words followed by
 * `=`, and the only stateful construct (multi-line `"""…"""` / `'''…'''`) has explicit closing
 * delimiters that a `(?s)` regex spans directly. That lets us express the whole language as
 * ordered rules with no hand-written scanner state.
 *
 * Classification mirrors the original lexer (see PaletteCodeHighlighterTest's TOML cases):
 * table/array-of-tables names → `type`, keys (followed by `=`) → `keyword`, `true`/`false` →
 * `keyword`, numbers → `number`, strings → `string`, `=` → `operator`, brackets/comma/dot →
 * `punctuation`.
 */
internal val TomlGrammar: Grammar = grammarOf(
    // Comments run to end of line. Match a leading `#` either at line start or after whitespace
    // so values like `path = x # comment` still classify the trailing comment.
    "comment" to GrammarToken(
        pattern = Regex("""(?m)(?:^|(?<=\s))#[^\n]*"""),
    ),
    // Strings: multi-line basic (""") and literal ('''') first (longer), then single-line
    // basic ("…") and literal ('…'). One alternation keeps them under a single rule name
    // because the Grammar map keys are unique per token name.
    "string" to GrammarToken(
        pattern = Regex(
            "(?s)" +
                "\"\"\"(?:\\\\.|\"(?!\")|[^\"\\\\])*\"\"\"" +
                "|'''[^']*'''" +
                "|\"(?:\\\\.|[^\"\\\\\\n])*\"" +
                "|'[^'\\n]*'",
        ),
    ),
    // Numbers (incl. leading sign, decimals, exponents, digit underscores).
    "number" to GrammarToken(
        pattern = Regex("""[+-]?\b\d[\d_]*(?:\.\d[\d_]*)?(?:[eE][+-]?\d+)?\b"""),
    ),
    // Table / array-of-tables header: the leading `[[`/`[` and trailing `]]`/`]` are
    // punctuation; the bare name inside is `type`. Match the whole header and recurse via
    // `inside` so the brackets and name get separate classifications.
    "header" to GrammarToken(
        pattern = Regex("""(?m)^\[\[?[^\]\n]+\]\]?"""),
        inside = grammarOf(
            "punctuation" to GrammarToken(pattern = Regex("""\[\[?|\]\]?""")),
            "type" to GrammarToken(pattern = Regex("""[^\[\]]+""")),
        ),
    ),
    // Keys (bare word at line start followed by `=`) and boolean literals, both classified as
    // `keyword` to match the original lexer's TomlKeywords + key handling.
    "keyword" to GrammarToken(
        pattern = Regex("""(?m)^[ \t]*[A-Za-z0-9_-]+(?=[ \t]*=)|\b(?:true|false)\b"""),
    ),
    // Assignment operator.
    "operator" to GrammarToken(
        pattern = Regex("""="""),
    ),
    // Structural punctuation: inline arrays/tables, commas, dotted keys.
    "punctuation" to GrammarToken(
        pattern = Regex("""[{}\[\],.]"""),
    ),
)
