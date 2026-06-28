package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownTocTest {
    @Test
    fun tocIsDerivedFromAllHeadings() {
        val model = MarkdownRenderer.toRenderModel(
            MarkdownParser.parse(
                """
                # Title
                ## Section
                ### Sub
                ## Another
                """.trimIndent(),
            ),
        )
        assertEquals(4, model.toc.size)
        assertEquals(1, model.toc[0].level)
        assertEquals("Title", model.toc[0].text)
        assertEquals(2, model.toc[3].level)
        assertEquals("Another", model.toc[3].text)
    }

    @Test
    fun tocEntryIdsMatchRenderedHeadingIds() {
        val model = MarkdownRenderer.toRenderModel(
            MarkdownParser.parse("# Hello World"),
        )
        assertEquals(1, model.toc.size)
        val heading = model.blocks[0] as MarkdownRenderBlock.Heading
        assertEquals(heading.id, model.toc[0].id)
        assertEquals("hello-world", model.toc[0].id)
    }

    @Test
    fun duplicateHeadingsGetDisambiguatedIdsInBothTocAndBlocks() {
        val model = MarkdownRenderer.toRenderModel(
            MarkdownParser.parse(
                """
                # Intro
                # Intro
                """.trimIndent(),
            ),
        )
        assertEquals("intro", model.toc[0].id)
        assertEquals("intro-2", model.toc[1].id)
        assertEquals("intro-2", (model.blocks[1] as MarkdownRenderBlock.Heading).id)
    }

    @Test
    fun tocCapturesHeadingsNestedInBlockquotes() {
        val model = MarkdownRenderer.toRenderModel(
            MarkdownParser.parse(
                """
                > # Quoted Heading

                paragraph
                """.trimIndent(),
            ),
        )
        assertTrue(model.toc.any { it.text == "Quoted Heading" })
    }

    @Test
    fun emptyDocumentHasEmptyToc() {
        val model = MarkdownRenderer.toRenderModel(MarkdownParser.parse(""))
        assertTrue(model.toc.isEmpty())
    }

    @Test
    fun frontmatterDoesNotPolluteToc() {
        val model = MarkdownRenderer.toRenderModel(
            MarkdownParser.parse(
                """
                ---
                title: Foo
                ---
                # Real Heading
                """.trimIndent(),
            ),
        )
        assertEquals(1, model.toc.size)
        assertEquals("Real Heading", model.toc[0].text)
    }
}
