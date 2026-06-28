package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkdownFrontmatterTest {
    @Test
    fun leadingFrontmatterIsParsedAsFrontmatterNotThematicBreak() {
        val document = MarkdownParser.parse(
            """
            ---
            title: Hello
            author: Palette
            ---
            # Heading
            """.trimIndent(),
        )
        // Frontmatter + heading = 2 blocks; previously this produced 2 thematic breaks + 2 paragraphs.
        assertEquals(2, document.blocks.size)
        assertIs<MarkdownFrontmatter>(document.blocks[0])
        assertIs<MarkdownHeading>(document.blocks[1])
    }

    @Test
    fun frontmatterFieldsAreParsedIntoKeyValues() {
        val document = MarkdownParser.parse(
            """
            ---
            title: Hello
            author: Palette
            ---
            body
            """.trimIndent(),
        )
        val frontmatter = document.blocks[0] as MarkdownFrontmatter
        assertEquals("Hello", frontmatter.fields["title"])
        assertEquals("Palette", frontmatter.fields["author"])
        assertTrue(frontmatter.rawYaml.contains("title: Hello"))
    }

    @Test
    fun quotedValuesAreUnwrapped() {
        val document = MarkdownParser.parse(
            """
            ---
            title: "Quoted Title"
            single: 'Single'
            ---
            body
            """.trimIndent(),
        )
        val frontmatter = document.blocks[0] as MarkdownFrontmatter
        assertEquals("Quoted Title", frontmatter.fields["title"])
        assertEquals("Single", frontmatter.fields["single"])
    }

    @Test
    fun tomlDelimitersAreAlsoRecognised() {
        val document = MarkdownParser.parse(
            """
            +++
            title: TOML-style
            +++
            body
            """.trimIndent(),
        )
        assertIs<MarkdownFrontmatter>(document.blocks[0])
        assertEquals("TOML-style", (document.blocks[0] as MarkdownFrontmatter).fields["title"])
    }

    @Test
    fun nonLeadingHorizontalRuleStaysThematicBreak() {
        // A `---` not at document start must remain a thematic break (regression guard).
        val document = MarkdownParser.parse(
            """
            # Heading

            ---

            paragraph
            """.trimIndent(),
        )
        assertTrue(document.blocks.none { it is MarkdownFrontmatter })
        assertTrue(document.blocks.any { it === MarkdownThematicBreak })
    }

    @Test
    fun unterminatedFrontmatterFallsBackToThematicBreak() {
        // No closing delimiter → lenient fallback, no crash, no half-eaten body.
        val document = MarkdownParser.parse(
            """
            ---
            title: dangling
            # Heading
            """.trimIndent(),
        )
        assertIs<MarkdownThematicBreak>(document.blocks.first())
        assertTrue(document.blocks.none { it is MarkdownFrontmatter })
    }

    @Test
    fun commentAndBlankLinesInFrontmatterAreSkipped() {
        val document = MarkdownParser.parse(
            """
            ---
            # a YAML comment
            title: Kept

            ignored-no-colon-line
            ---
            body
            """.trimIndent(),
        )
        val frontmatter = document.blocks[0] as MarkdownFrontmatter
        assertEquals("Kept", frontmatter.fields["title"])
        assertNull(frontmatter.fields["# a YAML comment"])
    }

    @Test
    fun rendererStripsFrontmatterFromBodyAndExposesItAsMetadata() {
        val model = MarkdownRenderer.toRenderModel(
            MarkdownParser.parse(
                """
                ---
                title: Hello
                ---
                # Heading
                paragraph
                """.trimIndent(),
            ),
        )
        // Body blocks exclude frontmatter.
        assertTrue(model.blocks.none { it is MarkdownRenderBlock.Code })
        assertEquals(2, model.blocks.size) // heading + paragraph
        assertEquals("Hello", model.frontmatter["title"])
    }
}
