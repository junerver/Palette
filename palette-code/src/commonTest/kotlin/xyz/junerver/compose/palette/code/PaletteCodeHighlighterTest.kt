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
}
