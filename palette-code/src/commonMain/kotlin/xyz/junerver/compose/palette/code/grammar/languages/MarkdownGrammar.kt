package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarTokenTypeMapping
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.GrammarTokenValue
import xyz.junerver.compose.palette.code.grammar.TokenType
import xyz.junerver.compose.palette.code.grammar.grammarOf
/**
 * Markdown grammar — declarative replacement for
 * [xyz.junerver.compose.palette.code.lexer.MarkdownLexer].
 *
 * Markdown's only stateful feature is fenced code blocks (`` ```lang … ``` ``), whose body is
 * highlighted in the embedded language. That language may itself be lexer-backed (e.g. `kotlin`,
 * which hasn't migrated to a grammar yet), so the fence rule uses [GrammarToken.embeddedTokens]
 * — the most general embedding hook — which delegates to the full
 * [PaletteCodeHighlighter] (grammar-first, lexer fallback). This closes the gap that left
 * Markdown on the hand-written lexer.
 *
 * Classification mirrors the original lexer (see PaletteCodeHighlighterTest's markdown cases):
 * heading markers (`##`) → `keyword`, list markers (`-`/`*`/`+`/`1.`) → `operator`, task
 * checkboxes (`[x]`/`[ ]`) → `annotation`, inline code `` `…` `` → `string`, link text → `type`
 * and link URLs → `string`, blockquote `>` → `operator`, fence delimiters → `annotation`, and
 * the fence info string (e.g. `kotlin`) → `type`.
 */
internal val MarkdownGrammar: Grammar = grammarOf(
    // Fenced code block: opening fence (```` ``` ```` or `~~~`) + optional info string, body,
    // closing fence. The whole block matches and is fully tokenized by [highlightMarkdownFence],
    // which classifies the wrapper lines (delimiters → annotation, info string → type) and
    // re-tokenizes the body in the embedded language via the full PaletteCodeHighlighter.
    "fence" to GrammarToken(
        pattern = Regex("(?m)^[ \\t]*(`{3,}|~{3,})[^\\n]*\\n(?:.*\\n)*?[ \\t]*\\1[ \\t]*$"),
        embeddedTokens = ::highlightMarkdownFence,
    ),
    // ATX heading marker (`#`–`######`) at line start → keyword.
    "keyword" to GrammarToken(
        pattern = Regex("(?m)^#{1,6}(?=\\s)"),
    ),
    // Blockquote marker `>` at line start → operator.
    "operator" to GrammarToken(
        pattern = Regex("(?m)^>\\s?"),
    ),
    // Task-list checkbox `[x]` / `[ ]` / `[X]` → annotation.
    "annotation" to GrammarToken(
        pattern = Regex("(?i)\\[[ xX]\\]"),
    ),
    // List marker: `-`, `*`, `+`, or `N.` at line start (after optional indent) → operator.
    // The marker token is just the symbol/number+dot; the trailing space falls through to plain.
    "operator" to GrammarToken(
        pattern = Regex("(?m)^\\s*(?:[-*+]|\\d+\\.)(?=\\s)"),
    ),
    // Inline code `…` → string.
    "string" to GrammarToken(
        pattern = Regex("``(?:[^`]|`(?!`))*``|`[^`\\n]*`"),
    ),
    // Link: [text](url). Recurse via `inside` so text → type, url → string, brackets plain.
    "link" to GrammarToken(
        pattern = Regex("\\[[^\\]]*\\]\\([^\\n)]*\\)"),
        inside = grammarOf(
            // Bracketed link text [text]: group 1 is the `[` (stripped as plain via lookbehind),
            // the token is the inner text → type. The closing `]` falls through to punctuation.
            "type" to GrammarToken(
                pattern = Regex("(\\[)[^\\]]+"),
                lookbehind = true,
            ),
            // Parenthesised URL (url): match the whole `(...)` and recurse to get the url → string
            // without the brackets.
            "string" to GrammarToken(
                pattern = Regex("[(][^\\n)]*[)]"),
                inside = grammarOf(
                    "string" to GrammarToken(pattern = Regex("[^()]+")),
                ),
            ),
            "punctuation" to GrammarToken(pattern = Regex("[\\[\\]()]")),
        ),
    ),
)

/**
 * Tokenizes a whole fenced-code block: the opening fence + info string (delimiters →
 * `annotation`, language id → `type`), the body in the embedded language (via the full
 * [PaletteCodeHighlighter], grammar-first with lexer fallback), and the closing fence
 * (`annotation`). Returns null when the info string is empty/unknown so the block is left as
 * plain text — matching the lexer's behaviour for unrecognised fences.
 */
private fun highlightMarkdownFence(matchText: String): List<GrammarTokenValue>? {
    val lines = matchText.split("\n")
    if (lines.size < 2) return null
    val opening = lines.first()
    val fenceMatch = Regex("^([ \\t]*)(`{3,}|~{3,})").find(opening) ?: return null
    val leadingIndent = fenceMatch.groupValues[1]
    val delimiter = fenceMatch.groupValues[2]
    val afterFence = opening.substring(fenceMatch.range.first + fenceMatch.value.length).trim()
    val language = parseFenceLanguage(afterFence)

    val out = mutableListOf<GrammarTokenValue>()
    val annotationType = TokenType("annotation")
    val typeType = TokenType("type")
    val plainType = TokenType.Plain

    // Opening fence: leading whitespace (plain) + delimiter (annotation) + info string (type).
    if (leadingIndent.isNotEmpty()) out += GrammarTokenValue(plainType, leadingIndent)
    out += GrammarTokenValue(annotationType, delimiter)
    if (afterFence.isNotEmpty()) {
        // The whitespace between delimiter and info is plain; the info itself is type.
        out += GrammarTokenValue(typeType, afterFence)
    }
    out += GrammarTokenValue(plainType, "\n")

    // Body lines (between opening and closing fence).
    val bodyEnd = lines.lastIndexOfClosingFence()
    if (bodyEnd > 1) {
        val body = lines.subList(1, bodyEnd).joinToString("\n")
        if (body.isNotEmpty()) {
            if (language != null) {
                // Highlight the body in the embedded language and bridge its tokens back.
                val highlighted = PaletteCodeHighlighter.highlight(code = body, language = language)
                out += highlighted.tokens.flatten().map { codeToken ->
                    GrammarTokenValue(
                        type = TokenType(GrammarTokenTypeMapping.toTokenTypeName(codeToken.type)),
                        text = codeToken.text,
                    )
                }
            } else {
                out += GrammarTokenValue(plainType, body)
            }
            out += GrammarTokenValue(plainType, "\n")
        }
    }

    // Closing fence: leading whitespace (plain) + delimiter (annotation).
    val closing = lines[bodyEnd]
    val closeFence = Regex("^([ \\t]*)(`{3,}|~{3,})[ \\t]*$").find(closing)
    if (closeFence != null) {
        if (closeFence.groupValues[1].isNotEmpty()) out += GrammarTokenValue(plainType, closeFence.groupValues[1])
        out += GrammarTokenValue(annotationType, closeFence.groupValues[2])
    }
    return out
}

/** Mirror of MarkdownLexer.parseMarkdownFenceLanguage: ``{.lang}`` attribute or first token. */
private fun parseFenceLanguage(info: String): String? {
    if (info.isEmpty()) return null
    val attrMatch = Regex("""\{\.([A-Za-z0-9_-]+)""").find(info)
    if (attrMatch != null) return attrMatch.groupValues[1].ifEmpty { null }
    val first = info.substringBefore(' ').trim()
    return first.ifEmpty { null }
}

// Index of the last line that is a closing fence (>=3 of the same char, only trailing ws).
private fun List<String>.lastIndexOfClosingFence(): Int {
    for (i in indices.reversed()) {
        val line = this[i].trim()
        if (line.length >= 3 && line.all { it == '`' || it == '~' } && line.first() == line.last()) {
            return i
        }
    }
    return size
}