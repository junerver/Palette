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
    "operator" to GrammarToken(
        pattern = Regex("(?m)^(?:---|\\.\\.\\.)(?=\\s|$)|(?m)^[ \\t]*-|(?<=\\s)-|[:,]"),
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
    if (start !in text.indices) return null
    // Confirm [start] points at a `|`/`>` indicator on its own (preceded only by whitespace).
    val nlIdx = text.lastIndexOf('\n', start)
    val lineStart = if (nlIdx < 0) 0 else nlIdx + 1
    if (lineStart > start) return null // start is a newline itself — not an indicator
    val prefix = text.substring(lineStart, start)
    if (prefix.any { !it.isWhitespace() }) return null
    val c = text[start]
    if (c != '|' && c != '>') return null
    // The key's indent = leading whitespace of the indicator line.
    val keyIndent = prefix.length
    // End of the indicator line (consume `|`/`>` plus chomping suffixes and a trailing comment).
    var i = start
    while (i < text.length && text[i] != '\n') i += 1
    val indicatorLineEnd = i // exclusive; text[i] is '\n' or i == text.length
    // Consume following lines while blank or more-indented than keyIndent.
    i = indicatorLineEnd
    while (i <= text.length) {
        val ls = if (i == text.length) i else i + 1 // start of next line (after '\n')
        if (ls >= text.length) break
        val nl = text.indexOf('\n', ls).let { if (it < 0) text.length else it }
        val line = text.substring(ls, nl)
        if (line.isBlank()) {
            i = nl
            continue
        }
        val indent = line.takeWhile { it == ' ' || it == '\t' }.length
        if (indent > keyIndent) {
            i = nl
        } else {
            break
        }
    }
    // Token spans from the indicator's line start to the end of the last consumed content line.
    // End is exclusive; if we stopped at a non-content line, end at the previous line's newline.
    val end = if (i == indicatorLineEnd) indicatorLineEnd else i.coerceAtMost(text.length)
    return if (end <= start) null else end
}
