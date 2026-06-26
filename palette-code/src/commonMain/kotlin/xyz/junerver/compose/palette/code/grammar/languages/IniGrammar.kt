package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * INI / properties grammar — declarative replacement for
 * [xyz.junerver.compose.palette.code.lexer.IniPropertiesLexer].
 *
 * Both formats are line-oriented and stateless, so a single grammar serves `ini`, `properties`,
 * `props`, and `conf`. Classification mirrors the original lexer (see
 * PaletteCodeHighlighterTest's ini/properties cases): section names `[x]` → `type`, keys
 * (followed by `=`, `:`, or a whitespace separator) → `keyword`, boolean-ish literals
 * (`true`/`false`/`yes`/`no`/`on`/`off`/`null`) → `keyword`, `${VAR}` interpolations →
 * `annotation`, strings → `string`, numbers → `number`, separators `=`/`:` → `operator`,
 * brackets/comma/dot → `punctuation`, `;`/`#` comments → `comment`.
 *
 * NOTE: regex patterns are written as plain (non-raw) Kotlin strings so the embedded `${` and
 * `}` of variable interpolation don't collide with Kotlin string templates.
 */
internal val IniGrammar: Grammar = grammarOf(
    // Comments: `;` or `#` to end of line, at line start or after whitespace.
    "comment" to GrammarToken(
        pattern = Regex("(?m)(?:^|(?<=\\s))[;#][^\\n]*"),
    ),
    // Strings: basic ("…") and literal ('…'), single line.
    "string" to GrammarToken(
        pattern = Regex("\"(?:\\\\.|[^\"\\\\\\n])*\"|'[^'\\n]*'"),
    ),
    // Variable interpolation ${VAR} → annotation. The dollar-brace would start a Kotlin string
    // template inside a raw literal, so this stays a plain escaped string.
    "annotation" to GrammarToken(
        pattern = Regex("\\$\\{[^}]*\\}"),
    ),
    // Section header [name]: brackets are punctuation, the name is `type`. Recurse via `inside`.
    "header" to GrammarToken(
        pattern = Regex("(?m)^\\[[^\\]\\n]*]"),
        inside = grammarOf(
            "punctuation" to GrammarToken(pattern = Regex("\\[\\]")),
            "type" to GrammarToken(pattern = Regex("[^\\[\\]]+")),
        ),
    ),
    // Numbers (incl. leading sign and dots, e.g. versions/IPs in values).
    "number" to GrammarToken(
        pattern = Regex("[+-]?\\b\\d[\\d.]*(?:[eE][+-]?\\d+)?\\b"),
    ),
    // A key-value pair at line start: the key (keyword) + its separator. INI uses `=`/`:`;
    // .properties also allows a whitespace separator (`key value`). The separator is an
    // operator. Matched together via `inside` so the key and separator get separate
    // classifications even when the separator is a single space.
    "pair" to GrammarToken(
        pattern = Regex("(?m)^[ \\t]*[^\\s;#=:\\[\\]]+[ \\t]*(?:=|:)[ \\t]*"),
        inside = grammarOf(
            "keyword" to GrammarToken(pattern = Regex("[^\\s;#=:\\[\\]]+")),
            "operator" to GrammarToken(pattern = Regex("[=:]")),
        ),
    ),
    // Whitespace-separated key in the properties style: `key<spaces>value`. The key is a
    // keyword and the separating spaces are the operator.
    "pair-ws" to GrammarToken(
        pattern = Regex("(?m)^[ \\t]*[^\\s;#=:\\[\\]]+[ \\t]+(?=\\S)"),
        inside = grammarOf(
            "keyword" to GrammarToken(pattern = Regex("[^\\s;#=:\\[\\]]+")),
            "operator" to GrammarToken(pattern = Regex("[ \\t]+")),
        ),
    ),
    // Boolean-ish literals.
    "keyword" to GrammarToken(
        pattern = Regex("(?i)\\b(?:true|false|yes|no|on|off|null)\\b"),
    ),
    // Structural punctuation.
    "punctuation" to GrammarToken(
        pattern = Regex("[{}\\[\\],.]"),
    ),
)
