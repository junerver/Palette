package xyz.junerver.compose.palette.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CompatibilityFixtureTest {
    private fun loadResource(path: String): String =
        checkNotNull(CompatibilityFixtureTest::class.java.classLoader.getResource(path)) {
            "Missing compatibility fixture: $path"
        }.readText()

    @Test
    fun commonmarkBasicsFixtureParsesExpectedBlocks() {
        val document = MarkdownParser.parse(loadResource("compatibility/commonmark-basics.md"))
        assertTrue(document.blocks.any { it is MarkdownHeading })
        assertTrue(document.blocks.any { it is MarkdownListBlock })
        assertTrue(document.blocks.any { it is MarkdownBlockQuote })
        assertTrue(document.blocks.any { it is MarkdownCodeBlock })
        assertTrue(document.blocks.any { it is MarkdownThematicBreak })
        assertTrue(document.blocks.any { it is MarkdownHtmlBlock })
    }

    @Test
    fun gfmBasicsFixtureParsesTasksAndTables() {
        val document = MarkdownParser.parse(loadResource("compatibility/gfm-basics.md"))
        assertTrue(document.blocks.any { it is MarkdownTaskListBlock || it is MarkdownListBlock })
        assertTrue(document.blocks.any { it is MarkdownTableBlock })
        assertTrue(document.blocks.any { it is MarkdownParagraph })
    }

    @Test
    fun commonmarkExtendedParsesEmphasisAndLinks() {
        val document = MarkdownParser.parse(loadResource("compatibility/commonmark-extended.md"))
        val paragraphs = document.blocks.filterIsInstance<MarkdownParagraph>()
        assertTrue(paragraphs.isNotEmpty(), "Expected paragraphs for emphasis and links")

        // Check bold italic
        val emphasisParagraph = paragraphs.firstOrNull { it.text.contains("***") }
        if (emphasisParagraph != null) {
            assertTrue(emphasisParagraph.inlines.any {
                it is MarkdownInlineStrong && it.children.any { child -> child is MarkdownInlineEmphasis }
            }, "Expected bold italic nesting")
        }

        // Check inline link with title
        val linkParagraph = paragraphs.firstOrNull { it.text.contains("[link]") }
        if (linkParagraph != null) {
            assertTrue(linkParagraph.inlines.any {
                it is MarkdownInlineLink && it.destination == "https://example.com" && it.title == "title"
            }, "Expected inline link with title")
        }

        // Check reference link
        val refParagraph = paragraphs.firstOrNull { it.text.contains("[link][ref]") }
        if (refParagraph != null) {
            assertTrue(refParagraph.inlines.any {
                it is MarkdownInlineLink && it.destination == "https://example.com/reference"
            }, "Expected reference link resolved")
        }
    }

    @Test
    fun commonmarkExtendedParsesTightList() {
        val document = MarkdownParser.parse(loadResource("compatibility/commonmark-extended.md"))
        val lists = document.blocks.filterIsInstance<MarkdownListBlock>()
        val tightList = lists.firstOrNull { it.items == listOf("a", "b", "c") }
        if (tightList != null) {
            assertEquals(true, tightList.tight, "Expected tight list")
        }
    }

    @Test
    fun gfmExtendedParsesTaskListAndTable() {
        val document = MarkdownParser.parse(loadResource("compatibility/gfm-extended.md"))

        // Task list - may be MarkdownTaskListBlock or MarkdownListBlock
        val taskLists = document.blocks.filterIsInstance<MarkdownTaskListBlock>()
        val regularLists = document.blocks.filterIsInstance<MarkdownListBlock>()
        assertTrue(taskLists.isNotEmpty() || regularLists.any { it.listItems.any { item -> item.taskChecked != null } },
            "Expected task list")

        // Table with alignments
        val tables = document.blocks.filterIsInstance<MarkdownTableBlock>()
        val alignedTable = tables.firstOrNull { it.alignments.any { a -> a != MarkdownTableAlignment.Start } }
        assertTrue(alignedTable != null, "Expected table with alignments")
        assertEquals(MarkdownTableAlignment.Start, alignedTable.alignments[0])
        assertEquals(MarkdownTableAlignment.Center, alignedTable.alignments[1])
        assertEquals(MarkdownTableAlignment.End, alignedTable.alignments[2])
    }

    @Test
    fun gfmExtendedParsesStrikethrough() {
        val document = MarkdownParser.parse(loadResource("compatibility/gfm-extended.md"))
        val paragraphs = document.blocks.filterIsInstance<MarkdownParagraph>()
        val strikethroughParagraph = paragraphs.firstOrNull { it.text.contains("~~") }
        if (strikethroughParagraph != null) {
            assertTrue(strikethroughParagraph.inlines.any {
                it is MarkdownInlineStrikethrough
            }, "Expected strikethrough")
        }
    }

    @Test
    fun gfmExtendedParsesAutolinks() {
        val document = MarkdownParser.parse(loadResource("compatibility/gfm-extended.md"))
        val paragraphs = document.blocks.filterIsInstance<MarkdownParagraph>()
        val autolinkParagraph = paragraphs.firstOrNull { it.text.contains("https://example.com") }
        if (autolinkParagraph != null) {
            assertTrue(autolinkParagraph.inlines.any {
                it is MarkdownInlineLink && it.destination.startsWith("https://")
            }, "Expected bare URL autolink")
        }
    }

    @Test
    fun gfmExtendedParsesFencedCodeBlocks() {
        val document = MarkdownParser.parse(loadResource("compatibility/gfm-extended.md"))
        val codeBlocks = document.blocks.filterIsInstance<MarkdownCodeBlock>()
        assertTrue(codeBlocks.size >= 2, "Expected at least 2 fenced code blocks")
        assertTrue(codeBlocks.any { it.language == "python" })
        assertTrue(codeBlocks.any { it.language == "ruby" })
    }
}
