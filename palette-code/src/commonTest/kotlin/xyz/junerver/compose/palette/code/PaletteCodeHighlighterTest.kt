package xyz.junerver.compose.palette.code

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaletteCodeHighlighterTest {
    @Test
    fun highlightsKotlinKeywordsStringsNumbersAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    @Composable
                    fun Greeting(name: String) {
                        val count = 42
                        println("Hello, ${'$'}name")
                        // visible comment
                    }
                    """.trimIndent(),
                language = "kotlin",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("kotlin", highlighted.language)
        assertEquals(6, highlighted.tokens.size)
        assertTrue(tokens.any { it.text == "@Composable" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "fun" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "Greeting" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "String" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "42" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "\"Hello, ${'$'}name\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "// visible comment" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun keepsUnknownLanguageAsPlainTextByLine() {
        val highlighted = PaletteCodeHighlighter.highlight("alpha\nbeta", language = "unknown")

        assertEquals("unknown", highlighted.language)
        assertEquals(2, highlighted.tokens.size)
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "alpha")), highlighted.tokens.first())
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "beta")), highlighted.tokens.last())
    }

    @Test
    fun highlightsTypeScriptKeywordsTemplateStringsAndRegexLikeJavaScript() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    export function greet(name: string) {
                        const count = 2
                        return `Hello, ${'$'}{name}`
                    }
                    """.trimIndent(),
                language = "typescript",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("typescript", highlighted.language)
        assertTrue(tokens.any { it.text == "export" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "function" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "greet" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "string" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "const" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "2" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "`Hello, ${'$'}{name}`" && it.type == CodeTokenType.StringLiteral })
    }

    @Test
    fun highlightsJsonStringsNumbersBooleansNullAndPunctuation() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    {
                      "name": "Palette",
                      "enabled": true,
                      "count": 3,
                      "next": null
                    }
                    """.trimIndent(),
                language = "json",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("json", highlighted.language)
        assertTrue(tokens.any { it.text == "\"name\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "3" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "null" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "{" && it.type == CodeTokenType.Punctuation })
        assertTrue(tokens.any { it.text == ":" && it.type == CodeTokenType.Operator })
    }

    @Test
    fun highlightsCssSelectorsPropertiesValuesAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    @media screen {
                      .card {
                        color: #fff;
                        margin: 8px;
                        content: "ready";
                        /* visible comment */
                      }
                    }
                    """.trimIndent(),
                language = "css",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("css", highlighted.language)
        assertTrue(tokens.any { it.text == "@media" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == ".card" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "color" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "#fff" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "8px" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "\"ready\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "/* visible comment */" && it.type == CodeTokenType.Comment })
    }
}
