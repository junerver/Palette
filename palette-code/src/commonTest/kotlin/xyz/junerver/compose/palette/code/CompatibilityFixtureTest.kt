package xyz.junerver.compose.palette.code

import kotlin.test.Test
import kotlin.test.assertTrue

class CompatibilityFixtureTest {
    private fun loadResource(path: String): String =
        checkNotNull(CompatibilityFixtureTest::class.java.classLoader.getResource(path)) {
            "Missing compatibility fixture: $path"
        }.readText()

    @Test
    fun kotlinSampleHighlightsKeywordsAndStrings() {
        val code = loadResource("compatibility/kotlin-sample.kt")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "kotlin")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "fun" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "\"Hello, " && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "// TODO: compatibility fixture" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun javaSampleHighlightsKeywordsAndStrings() {
        val code = loadResource("compatibility/java-sample.java")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "java")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "public" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text.contains("Palette") && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "// TODO: compatibility fixture" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun pythonSampleHighlightsKeywordsAndStrings() {
        val code = loadResource("compatibility/python-sample.py")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "python")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "def" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "f\"Hello, " && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "# TODO: compatibility fixture" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun sqlSampleHighlightsKeywordsAndTypes() {
        val code = loadResource("compatibility/sql-sample.sql")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "sql")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "SELECT" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "FROM" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "WHERE" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "VARCHAR" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "-- SQL compatibility fixture" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun jsonSampleHighlightsStringsAndNumbers() {
        val code = loadResource("compatibility/json-sample.json")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "json")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text.contains("Palette") && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun yamlSampleHighlightsKeysAndValues() {
        val code = loadResource("compatibility/yaml-sample.yaml")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "yaml")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "name" || it.text == "version" })
        assertTrue(tokens.any { it.text == "# YAML compatibility fixture" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun unknownLanguageReportsDiagnostic() {
        val result = PaletteCodeHighlighter.highlightWithDiagnostics("code", "unknown-lang")
        assertTrue(result.diagnostics.any { it.code == PaletteCodeDiagnosticCode.UnsupportedLanguage })
    }

    @Test
    fun emptyLanguageReportsDiagnostic() {
        val result = PaletteCodeHighlighter.highlightWithDiagnostics("code", "")
        assertTrue(result.diagnostics.any { it.code == PaletteCodeDiagnosticCode.BlankLanguage })
    }
}
