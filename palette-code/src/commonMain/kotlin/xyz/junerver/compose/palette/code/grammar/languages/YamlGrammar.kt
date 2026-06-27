package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * YAML grammar — declarative replacement for [xyz.junerver.compose.palette.code.lexer.YamlLexer].
 *
 * YAML's only non-regular construct is **block scalars** (`|` / `>`): a `key: |` line makes all
 * subsequent, more-indented lines part of a multi-line scalar. The lexer models this with a
 * per-line state machine; here a custom [GrammarToken.matcher] consumes the whole block in one
 * pass (the indicator line plus every following line whose indent exceeds the key's), and the
 * highlighter line-splits the resulting token so each content line is its own string token.
 *
 * Classification mirrors the lexer (see PaletteCodeHighlighterTest's yaml cases): keys (bare
 * words followed by `:`) → `keyword`, `%YAML` directives → `annotation`, `---`/`...` document
 * markers → `operator`, `&anchor`/`*alias`/`!tag` → `annotation`, `|`/`>` indicators → `operator`,
 * quoted strings → `string`, numbers → `number`, `# …` comments → `comment`, `-` list markers →
 * `operator`, `true`/`false`/`null` → `keyword`.
 */
internal val YamlGrammar: Grammar = grammarOf(
    // Block scalar: the indicator line (`|`, `>`, `|-`, `>-`, …) plus every following line that is
    // blank or more-indented than the key's indent. Consumed by a custom matcher because the span
    // is indentation-scoped (non-regular); the highlighter line-splits it into per-line strings.
    // The `operator` name classifies just the indicator — handled via `inside` on the matched span.
    "blockscalar" to GrammarToken(
        pattern = Regex("\\A(?!\\A)"), // never-matches; the matcher owns this rule
        matcher = { text, start -> yamlBlockScalarEnd(text, start) },
        inside = grammarOf(
            // The block-scalar indicator (`|`, `>`, with optional chomping suffixes `-`/`+`).
            "operator" to GrammarToken(pattern = Regex("(?m)^[ \t]*[|>][+-]?")),
            // The scalar body (indentation + content) → string. Any non-indicator line text.
            "string" to GrammarToken(pattern = Regex("(?s)[^|>]+")),
        ),
    ),
    // Keys (bare word followed by `:`) and boolean/null-ish literals — both → `keyword`. Must run
    // BEFORE the operator rule so the key's lookahead `:` is still present in the plain stream.
    // Anchor-free: a word immediately followed by `:` is a key regardless of position.
    "keyword" to GrammarToken(
        pattern = Regex("[A-Za-z0-9_.-]+(?=\\s*:)|\\b(?:true|false|yes|no|on|off|null)\\b"),
    ),
    // Document markers (`---`/`...`), list markers (`-`), and `:`/`,` → operator.
    // Document markers (`---`/`...`) and `:`/`,` → operator.
    "operator" to GrammarToken(
        pattern = Regex("(?m)^(?:---|\\.\\.\\.)(?=\\s|$)|[:,]"),
    ),
    // List marker `-` at the start of a (possibly indented) line. Group 1 is the leading
    // whitespace (kept as plain via lookbehind); the `-` is the operator token.
    "list-marker" to GrammarToken(
        pattern = Regex("(?m)^(\\s*)-"),
        lookbehind = true,
        alias = listOf("operator"),
    ),
    // Tags (`!Ref`, `!!str`), anchors (`&defaults`), aliases (`*defaults`), and `%YAML`
    // directives → annotation. Combined into one rule because the Grammar map keys are unique.
    "annotation" to GrammarToken(
        pattern = Regex("%[^\\n]*|!<?[^\\s:,\\]\\}]+|[&*][A-Za-z0-9_.-]+"),
    ),
    // Comments: `# …` (line start or after whitespace).
    "comment" to GrammarToken(
        pattern = Regex("(?m)(?:^|(?<=\\s))#[^\\n]*"),
    ),
    // Quoted strings.
    "string" to GrammarToken(
        pattern = Regex("\"(?:\\\\.|[^\"\\\\\\n])*\"|'[^'\\n]*'"),
    ),
    // Numbers.
    "number" to GrammarToken(
        pattern = Regex("\\b[+-]?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?\\b"),
    ),
)

/**
 * Block-scalar matcher: returns the exclusive end index of a block scalar whose indicator (`|`/`>`)
 * is at column [start] of its line, consuming that line plus every following line that is blank or
 * more-indented than the indicator's line. Returns null if [start] isn't a block-scalar indicator.
 */
private fun yamlBlockScalarEnd(text: String, start: Int): Int? {
    // TEMP DEBUG removed.
    if (start !in text.indices) return null
    val c = text[start]
    if (c != '|' && c != '>') return null
    // The block-scalar indicator must be the value of a mapping (`key: |`) or a bare sequence
    // entry — i.e. preceded on its line only by whitespace OR by `<key>:`. Reject mid-token
    // occurrences (e.g. `a | b` is not a block scalar).
    val nlIdx = text.lastIndexOf('\n', start)
    val lineStart = if (nlIdx < 0) 0 else nlIdx + 1
    val before = text.substring(lineStart, start)
    if (before.isBlank()) return blockScalarSpanEnd(text, start, 0)
    // Allow `<optional-ws>key:` immediately before (with optional whitespace after the colon).
    val m = Regex("^[ \\t]*\\S[\\s\\S]*:[ \\t]*$").find(before) ?: return null
    val keyIndent = before.takeWhile { it == ' ' || it == '\t' }.length
    return blockScalarSpanEnd(text, start, keyIndent)
}

/** Consume the indicator line plus every following line that is blank or more-indented. */
private fun blockScalarSpanEnd(text: String, indicatorPos: Int, keyIndent: Int): Int {
    var i = indicatorPos
    while (i < text.length && text[i] != '\n') i += 1
    val indicatorLineEnd = i
    i = indicatorLineEnd
    while (i <= text.length) {
        val ls = if (i == text.length) i else i + 1
        if (ls >= text.length) break
        val nl = text.indexOf('\n', ls).let { if (it < 0) text.length else it }
        val line = text.substring(ls, nl)
        if (line.isBlank()) { i = nl; continue }
        val indent = line.takeWhile { it == ' ' || it == '\t' }.length
        if (indent > keyIndent) i = nl else break
    }
    val end = if (i == indicatorLineEnd) indicatorLineEnd else i.coerceAtMost(text.length)
    return if (end <= indicatorPos) indicatorPos + 1 else end
}
