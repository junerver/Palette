package xyz.junerver.compose.palette.code.grammar

import xyz.junerver.compose.palette.code.grammar.languages.IniGrammar
import xyz.junerver.compose.palette.code.grammar.languages.TomlGrammar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Grammar-level contracts for the migrated declarative grammars (TOML, INI/properties). These
 * pin the token classification each grammar must produce, independent of the line-splitting
 * done by [GrammarHighlighter]; the end-to-end behaviour is also covered by
 * PaletteCodeHighlighterTest's lexer-parity cases.
 */
class DeclarativeGrammarLanguagesTest {

    // ── TOML ──────────────────────────────────────────────────────────────

    @Test
    fun tomlGrammar_classifiesTableNameAsType() {
        val tokens = GrammarTokenizer.tokenize("[project]", TomlGrammar)
        // Header recurses: '[' punctuation, 'project' type, ']' punctuation.
        assertTrue(tokens.any { it.text == "project" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "[" && it.type.name == "punctuation" })
    }

    @Test
    fun tomlGrammar_classifiesKeyAsKeywordAndValueAsTypes() {
        val tokens = GrammarTokenizer.tokenize("""name = "Palette"""", TomlGrammar)
        assertTrue(tokens.any { it.text == "name" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "=" && it.type.name == "operator" })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type.name == "string" })
    }

    @Test
    fun tomlGrammar_classifiesBooleansAsKeyword() {
        val tokens = GrammarTokenizer.tokenize("enabled = true", TomlGrammar)
        assertTrue(tokens.any { it.text == "true" && it.type.name == "keyword" })
    }

    @Test
    fun tomlGrammar_spansMultilineStringAsOneToken() {
        val src = "description = \"\"\"\nHeavy duty\nhammer\n\"\"\""
        val tokens = GrammarTokenizer.tokenize(src, TomlGrammar)
        // The multi-line body is a single string token (the highlighter splits it per line).
        assertTrue(tokens.any { it.type.name == "string" && it.text.contains("Heavy duty") })
    }

    // ── INI / properties ──────────────────────────────────────────────────

    @Test
    fun iniGrammar_classifiesSectionNameAsType() {
        val tokens = GrammarTokenizer.tokenize("[server.main]", IniGrammar)
        assertTrue(tokens.any { it.text == "server.main" && it.type.name == "type" })
    }

    @Test
    fun iniGrammar_classifiesEqualsKeyAsKeyword() {
        val tokens = GrammarTokenizer.tokenize("host = localhost", IniGrammar)
        assertTrue(tokens.any { it.text == "host" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "=" && it.type.name == "operator" })
    }

    @Test
    fun iniGrammar_classifiesColonKeyAsKeyword() {
        val tokens = GrammarTokenizer.tokenize("app.enabled:false", IniGrammar)
        assertTrue(tokens.any { it.text == "app.enabled" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == ":" && it.type.name == "operator" })
    }

    @Test
    fun iniGrammar_classifiesVariableInterpolationAsAnnotation() {
        val tokens = GrammarTokenizer.tokenize("path = \${APP_HOME}", IniGrammar)
        assertTrue(tokens.any { it.text == "\${APP_HOME}" && it.type.name == "annotation" })
    }

    @Test
    fun iniGrammar_classifiesCommentFromHashOrSemicolon() {
        assertEquals("comment", GrammarTokenizer.tokenize("# note", IniGrammar).first().type.name)
        assertEquals("comment", GrammarTokenizer.tokenize("; note", IniGrammar).first().type.name)
    }
}
