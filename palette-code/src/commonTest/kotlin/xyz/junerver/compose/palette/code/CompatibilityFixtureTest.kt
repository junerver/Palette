package xyz.junerver.compose.palette.code

import kotlin.test.Test
import kotlin.test.assertTrue

class CompatibilityFixtureTest {
    private fun loadResource(path: String): String =
        checkNotNull(compatibilityFixtures[path]) { "Missing compatibility fixture: $path" }

    @Test
    fun kotlinSampleHighlightsKeywordsAndStrings() {
        // `.kt.txt` extension avoids AGP stripping the fixture as a compile source under Android unit tests.
        val code = loadResource("compatibility/kotlin-sample.kt.txt")
        val highlighted = PaletteCodeHighlighter.highlight(code = code, language = "kotlin")
        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "fun" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "\"Hello, " && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "// TODO: compatibility fixture" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun javaSampleHighlightsKeywordsAndStrings() {
        // `.java.txt` extension avoids AGP stripping the fixture as a compile source under Android unit tests.
        val code = loadResource("compatibility/java-sample.java.txt")
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

    private companion object {
        val compatibilityFixtures = mapOf(
            "compatibility/kotlin-sample.kt.txt" to
                """
                @Composable
                fun Greeting(name: String) {
                    val version = 2
                    val message = "Hello, ${'$'}{name}!"
                    println(message)
                    // TODO: compatibility fixture
                }
                """.trimIndent(),
            "compatibility/java-sample.java.txt" to
                """
                import java.util.List;

                public final class Sample {
                    public static void main(String[] args) {
                        int count = 1;
                        String name = "Palette";
                        System.out.println("Hello " + name + " " + count);
                        // TODO: compatibility fixture
                    }
                }
                """.trimIndent(),
            "compatibility/python-sample.py" to
                """
                def greeting(name: str) -> str:
                    version = 2
                    message = f"Hello, {name} {version}"
                    # TODO: compatibility fixture
                    return message

                print(greeting("Palette"))
                """.trimIndent(),
            "compatibility/sql-sample.sql" to
                """
                -- SQL compatibility fixture
                SELECT u.name, COUNT(o.id) AS order_count
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                WHERE u.active = true
                GROUP BY u.name
                HAVING COUNT(o.id) > 5
                ORDER BY order_count DESC
                LIMIT 10;

                CREATE TABLE users (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    email TEXT UNIQUE
                );

                INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com');
                """.trimIndent(),
            "compatibility/json-sample.json" to
                """
                {
                  "name": "Palette",
                  "version": "1.0.0",
                  "dependencies": {
                    "compose": "^1.5.0",
                    "kotlin": "1.9.0"
                  },
                  "features": [
                    "highlighting",
                    "mermaid",
                    "markdown"
                  ],
                  "config": {
                    "theme": "default",
                    "lineNumbers": true,
                    "maxLines": 1000
                  }
                }
                """.trimIndent(),
            "compatibility/yaml-sample.yaml" to
                """
                # YAML compatibility fixture
                name: Palette
                version: 1.0.0

                dependencies:
                  compose: "^1.5.0"
                  kotlin: "1.9.0"

                features:
                  - highlighting
                  - mermaid
                  - markdown

                config:
                  theme: default
                  lineNumbers: true
                  maxLines: 1000

                multiline: |
                  This is a
                  multiline string
                """.trimIndent(),
        )
    }
}
