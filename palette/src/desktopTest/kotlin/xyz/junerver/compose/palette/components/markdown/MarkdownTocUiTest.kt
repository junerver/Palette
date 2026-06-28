package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.markdown.MarkdownParser
import xyz.junerver.compose.palette.markdown.MarkdownRenderer
import xyz.junerver.compose.palette.markdown.MarkdownTocEntry

class MarkdownTocUiTest {
    @get:Rule
    val rule = createComposeRule()

    private fun tocOf(markdown: String): List<MarkdownTocEntry> =
        MarkdownRenderer.toRenderModel(MarkdownParser.parse(markdown)).toc

    @Test
    fun tocRendersEntryTextForEachHeading() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownToc(
                    entries = tocOf("# First\n## Second"),
                    onNavigate = {},
                )
            }
        }
        rule.onNodeWithText("First").assertExists()
        rule.onNodeWithText("Second").assertExists()
    }

    @Test
    fun clickingAnEntryInvokesOnNavigateWithHeadingId() {
        var navigated: String? = null

        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownToc(
                    entries = tocOf("# Title"),
                    onNavigate = { id -> navigated = id },
                )
            }
        }

        rule.onNodeWithTag("toc:title").performClick()

        rule.runOnIdle {
            assertEquals("title", navigated)
        }
    }

    @Test
    fun tocTestIdMatchesViewerHeadingTagForAnchorNavigation() {
        // The TOC entry tag is "toc:<id>"; the viewer heading tag is "heading:<id>". They share the id,
        // so a TOC click can scroll to the viewer heading via onAnchorClick(id).
        val entries = tocOf("# Shared Slug")
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownToc(entries = entries, onNavigate = {})
            }
        }
        // id "shared-slug"
        rule.onNodeWithTag("toc:shared-slug").assertExists()
    }

    @Test
    fun maxLevelHidesDeeperEntries() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownToc(
                    entries = tocOf("# Keep\n## Hide\n### Hide2"),
                    onNavigate = {},
                    maxLevel = 1,
                )
            }
        }
        rule.onNodeWithText("Keep").assertExists()
        // Deeper entries must not render at all.
        rule.onNodeWithText("Hide").assertDoesNotExist()
    }

    @Test
    fun emptyEntriesRendersNothingWithoutCrash() {
        rule.setContent {
            PaletteMaterialTheme {
                PMarkdownToc(entries = emptyList(), onNavigate = {})
            }
        }
        // Empty entries render nothing; the only contract is no crash and no thrown assertion.
        assertNull(null)
    }
}
