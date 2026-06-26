package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarRegistry
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * HTML / XML / SVG grammar — declarative replacement for
 * [xyz.junerver.compose.palette.code.lexer.HtmlLexer].
 *
 * Markup is regular enough for a pure-regex grammar; the only stateful feature of the old
 * lexer is language embedding (`<style>` → CSS, `<script>` → JS), expressed here via the
 * engine's `languageResolver` hook. The resolver builds a composite grammar whose rules are
 * the HTML-tag rule first (so `<style>`/`</style>` wrapper tags get markup classification) and
 * the embedded language's rules after (so the body is highlighted as CSS/JS). The engine then
 * re-tokenizes the whole embedding match with that composite grammar.
 *
 * Classification mirrors the original lexer (see PaletteCodeHighlighterTest's HTML case):
 * `<!doctype` → `keyword`, `<!-- … -->` → `comment`, tag names (`section`, `style`) → `type`,
 * attributes (`class`, `data-count`) → `annotation`, attribute values → `string`,
 * `<`, `</`, `>` → `punctuation`, `=` → `operator`.
 */
internal val HtmlGrammar: Grammar = grammarOf(
    // DOCTYPE declaration.
    "doctype" to GrammarToken(
        pattern = Regex("(?i)<!doctype[^>]*>"),
        inside = grammarOf(
            "keyword" to GrammarToken(pattern = Regex("(?i)<!doctype")),
            "string" to GrammarToken(pattern = Regex("[^<>]+")),
            "punctuation" to GrammarToken(pattern = Regex(">")),
        ),
    ),
    // Comments (span lines).
    "comment" to GrammarToken(
        pattern = Regex("(?s)<!--[\\s\\S]*?-->"),
    ),
    // Embedded `<style>…</style>`: re-tokenize with [embeddedMarkupGrammar] (css), so the
    // wrapper tags keep markup classification and the body is highlighted as CSS.
    "style" to GrammarToken(
        pattern = Regex("(?is)<style[^>]*>[\\s\\S]*?</style>"),
        languageResolver = { _ ->
            GrammarRegistry.grammarOrNull("css")?.let { embeddedMarkupGrammar(it) }
        },
    ),
    // Embedded `<script>…</script>`: re-tokenize with [embeddedMarkupGrammar] (js).
    "script" to GrammarToken(
        pattern = Regex("(?is)<script[^>]*>[\\s\\S]*?</script>"),
        languageResolver = { _ ->
            GrammarRegistry.grammarOrNull("javascript")?.let { embeddedMarkupGrammar(it) }
        },
    ),
    // Tag with attributes: `<section class="card">`, `</section>`. Recurse via `inside` so the
    // name, attributes, and punctuation get separate classifications. The tag-name and
    // attribute rules use the engine's capture-group lookbehind (group 1 stripped) so the name
    // (after `</?`) and the attribute (after whitespace) are matched in position rather than via
    // a regex lookbehind, which the tokenizer doesn't evaluate.
    "tag" to GrammarToken(
        pattern = Regex("</?[A-Za-z][A-Za-z0-9:-]*(?:[^>]*)?>"),
        inside = grammarOf(
            // Opening/closing bracket punctuation: `<`, `</`, `>`, `/>`. Matched first so the
            // brackets classify as punctuation (the HTML tests assert `</` → punctuation).
            "punctuation" to GrammarToken(pattern = Regex("</?|/?>")),
            "string" to GrammarToken(pattern = Regex("\"(?:\\.|[^\"<>])*\"|'(?:\\.|[^'<>])*'")),
            "operator" to GrammarToken(pattern = Regex("=")),
            // Attribute name: identifier preceded by whitespace (lookbehind strips the space).
            "annotation" to GrammarToken(
                pattern = Regex("(\\s)[A-Za-z_:][A-Za-z0-9_:.-]*"),
                lookbehind = true,
            ),
            // Tag name: the bare identifier left after the bracket. Last so attributes (with
            // their whitespace lookbehind) win when applicable; a bare identifier that reaches
            // here is the tag name.
            "type" to GrammarToken(pattern = Regex("[A-Za-z][A-Za-z0-9:-]*")),
        ),
    ),
)

/**
 * Composite grammar for an embedded block: the HTML tag rule first (so wrapper tags classify
 * as markup) followed by the embedded language's rules (so the body highlights as that
 * language). Rules are ordered so tag matches win over body language where they overlap.
 */
private fun embeddedMarkupGrammar(embedded: Grammar): Grammar {
    val tagRule = HtmlGrammar.rules().firstOrNull { it.first == "tag" }?.second ?: return embedded
    // Build a new ordered rule set: tag rule, then embedded rules. A LinkedHashMap preserves
    // insertion (priority) order, with the tag rule first.
    val combined = linkedMapOf<String, GrammarToken>()
    combined["tag"] = tagRule
    embedded.rules().forEach { (name, rule) -> combined[name] = rule }
    return Grammar(combined)
}
