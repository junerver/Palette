package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.markdown.MarkdownTocEntry

/**
 * Rendered table-of-contents entry, built from the markdown document headings.
 *
 * Re-exported as [xyz.junerver.compose.palette.MarkdownTocEntry] via typealias.
 */

/**
 * Navigation callback signature for a TOC entry click.
 *
 * The [String] argument is the heading anchor id, matching the viewer's `testTag("heading:<id>")`,
 * so callers can scroll to the heading via `PMarkdownViewer`'s `onAnchorClick`.
 */
typealias OnTocNavigate = (String) -> Unit

/**
 * Default values for [PMarkdownToc].
 *
 * All visual values derive from top-level theme tokens so the whole document family can be retuned
 * from `PaletteTheme`/`PaletteMaterialTheme`. Indentation scales with the heading level relative to
 * the document's shallowest visible heading.
 */
object MarkdownTocDefaults {
    /** Indentation applied per heading level (the gap between a level-2 and level-3 entry). */
    @Composable
    fun indentStep(): Dp = PaletteTheme.spacing.small

    /** Vertical padding around each entry, from the theme spacing token. */
    @Composable
    fun entryVerticalPadding(): Dp = PaletteTheme.spacing.extraSmall

    /** Base font size (level-1 entries); deeper levels shrink slightly. */
    @Composable
    fun baseFontSize(): TextUnit = PaletteTheme.typography.body.fontSize

    /** Per-level font size shrink factor, applied for levels below the base. */
    val levelFontSizeShrink: TextUnit = 1.sp

    /** Minimum font size so deeply nested entries stay legible. */
    val minFontSize: TextUnit = 12.sp
}

/**
 * A Markdown table of contents.
 *
 * Renders a clickable, indented list of [entries]. Clicking an entry invokes [onNavigate] with the
 * heading anchor id, which the caller can route to [PMarkdownViewer]'s `onAnchorClick` to scroll
 * the matching heading into view.
 *
 * @param entries TOC entries, typically `MarkdownRenderer.toRenderModel(...).toc`.
 * @param onNavigate invoked with the heading id on click.
 * @param modifier outer modifier.
 * @param maxLevel deepest heading level to render (deeper entries are hidden). Defaults to showing all.
 * @param contentPadding padding around the whole list.
 */
@Composable
fun PMarkdownToc(
    entries: List<MarkdownTocEntry>,
    onNavigate: OnTocNavigate,
    modifier: Modifier = Modifier,
    maxLevel: Int = Int.MAX_VALUE,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (entries.isEmpty()) return
    val visible = entries.filter { it.level <= maxLevel }
    if (visible.isEmpty()) return

    val baseLevel = visible.minOf { it.level }
    val indentStep = MarkdownTocDefaults.indentStep()
    val entryVerticalPadding = MarkdownTocDefaults.entryVerticalPadding()
    val baseFontSize = MarkdownTocDefaults.baseFontSize()
    val shrink = MarkdownTocDefaults.levelFontSizeShrink
    val minFontSize = MarkdownTocDefaults.minFontSize

    Column(modifier = modifier.padding(contentPadding)) {
        visible.forEach { entry ->
            val depth = (entry.level - baseLevel).coerceAtLeast(0)
            // Shrink font size per depth level, clamped to the minimum for legibility. TextUnit arithmetic
            // uses the platform-provided operators in androidx.compose.ui.unit.
            val fontSize = (baseFontSize.value - shrink.value * depth).coerceAtLeast(minFontSize.value).sp
            Text(
                text = entry.text,
                fontSize = fontSize,
                color = PaletteTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = (indentStep.value * depth).dp,
                        top = entryVerticalPadding,
                        bottom = entryVerticalPadding,
                    )
                    .testTag("toc:${entry.id}")
                    .semantics { contentDescription = "toc entry: ${entry.text}" }
                    .clickable { onNavigate(entry.id) },
            )
        }
    }
}
