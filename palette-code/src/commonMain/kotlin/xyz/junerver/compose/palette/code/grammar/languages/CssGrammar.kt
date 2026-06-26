package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * CSS grammar — declarative replacement for [xyz.junerver.compose.palette.code.lexer.CssLexer].
 *
 * Line-oriented with only one stateful construct (block comments `/* … */`), which a `(?s)`
 * regex spans directly. Classification mirrors the original lexer (see
 * PaletteCodeHighlighterTest's CSS case): at-rules (`@media`) → `annotation`, class selectors
 * (`.card`) → `type`, id selectors → `type` (or `number` when a hex colour like `#fff`),
 * numbers/units (`8px`) → `number`, strings → `string`, `: > + ~ = |` → `operator`,
 * `{ } ( ) [ ] , ;` → `punctuation`, comments → `comment`.
 *
 * NOTE: regex patterns are plain (non-raw) Kotlin strings so backslashes escape cleanly.
 */
internal val CssGrammar: Grammar = grammarOf(
    // Block comments, possibly spanning lines.
    "comment" to GrammarToken(
        pattern = Regex("(?s)/\\*.*?\\*/"),
    ),
    // Strings: basic ("…") and literal ('…').
    "string" to GrammarToken(
        pattern = Regex("\"(?:\\\\.|[^\"\\\\\\n])*\"|'[^'\\n]*'"),
    ),
    // At-rules: @media, @import, @keyframes, …
    "annotation" to GrammarToken(
        pattern = Regex("@[-\\w]+"),
    ),
    // Numbers: hex colour (#fff / #aabbcc) and numbers-with-unit (8px, 1.5em, 100%). Combined
    // into one rule because the Grammar map keys are unique per token name; hex is listed
    // first so a 3/4/6/8 hex-digit `#xxx` is a colour, not an id selector.
    "number" to GrammarToken(
        pattern = Regex(
            "#[0-9a-fA-F]{8}\\b|#[0-9a-fA-F]{6}\\b|#[0-9a-fA-F]{4}\\b|#[0-9a-fA-F]{3}\\b" +
                "|-?\\b\\d+(?:\\.\\d+)?(?:px|em|rem|vh|vw|%|deg|s|ms|fr|pt|pc|in|cm|mm|ex|ch)?\\b",
        ),
    ),
    // Class (.card) and id (#main) selectors → type. (Hex colours already consumed above.)
    "type" to GrammarToken(
        pattern = Regex("\\.[-\\w]+|#[-\\w]+"),
    ),
    // Function call: identifier immediately followed by `(` → function (e.g. `rgb(`, `calc(`).
    "function" to GrammarToken(
        pattern = Regex("[-\\w]+(?=\\()"),
    ),
    // Property name (identifier followed, after optional whitespace, by `:`) and CSS value
    // keywords (flex/solid/…) — both classify as `keyword`, matching the original lexer. They
    // share one rule because the Grammar map keys are unique per token name.
    "keyword" to GrammarToken(
        pattern = Regex(
            "[-\\w]+(?=\\s*:)" + // property name
            "|\\b(?:absolute|auto|block|flex|grid|hidden|inline|none|relative|solid|static|sticky|visible|initial|inherit|unset|revert)\\b", // value keyword
        ),
    ),
    // Operators and punctuation.
    "operator" to GrammarToken(
        pattern = Regex("[:>+~=|]"),
    ),
    "punctuation" to GrammarToken(
        pattern = Regex("[{}()\\[\\],;]"),
    ),
)
