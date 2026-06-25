package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * Markdown grammar — the second pilot grammar. Demonstrates the `inside` nested-grammar
 * mechanism (e.g. inline code spans, bold/italic with nested emphasis) that Prism relies on
 * for embedded/recursive constructs.
 *
 * Covers the most visible Markdown constructs: headings, fenced code blocks, bold, italic,
 * inline code, links, and list markers. Deeper coverage (tables, blockquotes nesting) can be
 * added incrementally.
 */
internal val MarkdownGrammar: Grammar = grammarOf(
    // ATX headings: 1-6 '#'.
    "heading" to GrammarToken(
        pattern = Regex("""(?m)^#{1,6}[^\n]*"""),
        alias = listOf("important"),
    ),
    // Fenced code blocks (``` or ~~~).
    "code-block" to GrammarToken(
        pattern = Regex("""(?ms)^```[\s\S]*?^```\s*$|^(?:~~~)[\s\S]*?^(?:~~~)\s*$"""),
        alias = listOf("block"),
    ),
    // Horizontal rule.
    "hr" to GrammarToken(
        pattern = Regex("""(?m)^[-*_]{3,}\s*$"""),
    ),
    // Blockquote.
    "blockquote" to GrammarToken(
        pattern = Regex("""(?m)^>+[^\n]*(?:\n>[^\n]*)*"""),
    ),
    // Task list checkbox.
    "checkbox" to GrammarToken(
        pattern = Regex("""\[[ xX]\]"""),
        alias = listOf("punctuation"),
    ),
    // Bold (**text** or __text__), with optional nested emphasis.
    "bold" to GrammarToken(
        pattern = Regex("""\*\*[\s\S]+?\*\*|__[\s\S]+?__"""),
        alias = listOf("important"),
        inside = grammarOf(
            "italic" to GrammarToken(
                pattern = Regex("""\*[^*\n]+\*|_[^_\n]+_"""),
            ),
        ),
    ),
    // Italic (*text* or _text_).
    "italic" to GrammarToken(
        pattern = Regex("""\*[^*\n]+\*|_[^_\n]+_"""),
    ),
    // Inline code (`code`).
    "code-inline" to GrammarToken(
        pattern = Regex("""`[^`\n]+`"""),
        alias = listOf("string"),
    ),
    // Links: [text](url) and [ref][ref] / [ref].
    "link" to GrammarToken(
        pattern = Regex("""!?\[[^\]]+\]\([^)]+\)"""),
        alias = listOf("url"),
    ),
    // Reference-style link definitions and references.
    "url" to GrammarToken(
        pattern = Regex("""<?https?://[^\s)]+>?"""),
    ),
    // List markers.
    "list" to GrammarToken(
        pattern = Regex("""(?m)^\s*(?:[-*+]|\d+\.)\s"""),
        alias = listOf("punctuation"),
    ),
)
