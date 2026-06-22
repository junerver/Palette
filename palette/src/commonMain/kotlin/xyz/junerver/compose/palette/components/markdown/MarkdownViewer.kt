package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.components.code.PCodeBlock
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.markdown.MarkdownInlineCode
import xyz.junerver.compose.palette.markdown.MarkdownInlineEmphasis
import xyz.junerver.compose.palette.markdown.MarkdownInlineImage
import xyz.junerver.compose.palette.markdown.MarkdownInlineLink
import xyz.junerver.compose.palette.markdown.MarkdownInlineNode
import xyz.junerver.compose.palette.markdown.MarkdownInlineStrong
import xyz.junerver.compose.palette.markdown.MarkdownInlineStrikethrough
import xyz.junerver.compose.palette.markdown.MarkdownInlineText
import xyz.junerver.compose.palette.markdown.MarkdownParser
import xyz.junerver.compose.palette.markdown.MarkdownRenderBlock
import xyz.junerver.compose.palette.markdown.MarkdownRenderModel
import xyz.junerver.compose.palette.markdown.MarkdownRenderer
import xyz.junerver.compose.palette.markdown.MarkdownTableAlignment

@Composable
fun PMarkdownViewer(
    markdown: String,
    modifier: Modifier = Modifier,
    renderModel: MarkdownRenderModel = MarkdownRenderer.toRenderModel(MarkdownParser.parse(markdown)),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        renderModel.blocks.forEach { block ->
            when (block) {
                is MarkdownRenderBlock.Heading ->
                    Text(
                        text = block.inlines.toAnnotatedString(),
                        color = PaletteTheme.colors.textPrimary,
                        style =
                            when (block.level) {
                                1 -> PaletteTheme.typography.title.copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                                2 -> PaletteTheme.typography.title.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                else -> PaletteTheme.typography.body.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            },
                    )

                is MarkdownRenderBlock.Paragraph ->
                    Text(
                        text = block.inlines.toAnnotatedString(),
                        color = PaletteTheme.colors.textPrimary,
                        style = PaletteTheme.typography.body,
                    )

                is MarkdownRenderBlock.ListBlock ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        block.itemInlines.forEachIndexed { index, item ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (block.ordered) "${block.startNumber + index}." else "-",
                                    color = PaletteTheme.colors.textSecondary,
                                    modifier = Modifier.padding(end = 8.dp),
                                )
                                Text(
                                    text = item.toAnnotatedString(),
                                    color = PaletteTheme.colors.textPrimary,
                                    style = PaletteTheme.typography.body,
                                )
                            }
                        }
                    }

                is MarkdownRenderBlock.TaskList ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        block.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .padding(top = 3.dp, end = 8.dp)
                                            .size(16.dp)
                                            .border(1.dp, PaletteTheme.colors.border)
                                            .background(
                                                if (item.checked) {
                                                    PaletteTheme.colors.primary
                                                } else {
                                                    PaletteTheme.colors.surface
                                                },
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (item.checked) {
                                        Text(
                                            text = "x",
                                            color = PaletteTheme.colors.surface,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                                Text(
                                    text = item.inlines.toAnnotatedString(),
                                    color = PaletteTheme.colors.textPrimary,
                                    style = PaletteTheme.typography.body,
                                )
                            }
                        }
                    }

                is MarkdownRenderBlock.BlockQuote ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .background(PaletteTheme.colors.surfaceElevated)
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .width(3.dp)
                                    .fillMaxHeight()
                                    .background(PaletteTheme.colors.primary),
                        )
                        Text(
                            text = block.inlines.toAnnotatedString(),
                            color = PaletteTheme.colors.textSecondary,
                            style = PaletteTheme.typography.body,
                            modifier = Modifier.padding(start = 10.dp),
                        )
                    }

                is MarkdownRenderBlock.Table ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ) {
                        MarkdownTableRow(
                            cells = block.headerInlines,
                            alignments = block.alignments,
                            isHeader = true,
                        )
                        block.rowInlines.forEach { row ->
                            MarkdownTableRow(
                                cells = row.normalizedCellCount(block.headers.size),
                                alignments = block.alignments,
                                isHeader = false,
                            )
                        }
                    }

                is MarkdownRenderBlock.Code ->
                    PCodeBlock(
                        code = block.highlighted.tokens.joinToString("\n") { line -> line.joinToString("") { it.text } },
                        language = block.language,
                        showLineNumbers = block.showLineNumbers,
                        highlightedLines = block.highlightedLines,
                        title = block.title,
                        highlightedCode = block.highlighted,
                    )

                is MarkdownRenderBlock.Mermaid -> PMermaidDiagram(source = block.source)
                MarkdownRenderBlock.ThematicBreak -> Divider(color = PaletteTheme.colors.border)
            }
        }
    }
}

@Composable
private fun MarkdownTableRow(
    cells: List<List<MarkdownInlineNode>>,
    alignments: List<MarkdownTableAlignment>,
    isHeader: Boolean,
) {
    val colors = PaletteTheme.colors
    Row(modifier = Modifier.fillMaxWidth()) {
        cells.forEachIndexed { index, cell ->
            Text(
                text = cell.toAnnotatedString(),
                color = colors.textPrimary,
                textAlign = alignments.getOrElse(index) { MarkdownTableAlignment.Start }.toTextAlign(),
                style =
                    if (isHeader) {
                        PaletteTheme.typography.body.copy(fontWeight = FontWeight.SemiBold)
                    } else {
                        PaletteTheme.typography.body
                    },
                modifier =
                    Modifier
                        .weight(1f)
                        .border(1.dp, colors.border)
                        .background(if (isHeader) colors.surfaceElevated else colors.surface)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
            )
        }
    }
}

private fun MarkdownTableAlignment.toTextAlign(): TextAlign =
    when (this) {
        MarkdownTableAlignment.Start -> TextAlign.Start
        MarkdownTableAlignment.Center -> TextAlign.Center
        MarkdownTableAlignment.End -> TextAlign.End
    }

private fun List<List<MarkdownInlineNode>>.normalizedCellCount(columnCount: Int): List<List<MarkdownInlineNode>> {
    if (size == columnCount) return this
    if (size > columnCount) return take(columnCount)
    return this + List(columnCount - size) { emptyList() }
}

@Composable
private fun List<MarkdownInlineNode>.toAnnotatedString(): AnnotatedString {
    val colors = PaletteTheme.colors
    return buildAnnotatedString {
        forEach { node ->
            when (node) {
                is MarkdownInlineText -> append(node.text)
                is MarkdownInlineStrong -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(node.text)
                    pop()
                }

                is MarkdownInlineEmphasis -> {
                    pushStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                    append(node.text)
                    pop()
                }

                is MarkdownInlineStrikethrough -> {
                    pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    append(node.text)
                    pop()
                }

                is MarkdownInlineCode -> {
                    pushStyle(
                        SpanStyle(
                            color = colors.primary,
                            background = colors.bgSelected,
                        ),
                    )
                    append(node.text)
                    pop()
                }

                is MarkdownInlineLink -> {
                    pushStyle(
                        SpanStyle(
                            color = colors.primary,
                            textDecoration = TextDecoration.Underline,
                        ),
                    )
                    append(node.label)
                    pop()
                }

                is MarkdownInlineImage -> {
                    pushStyle(
                        SpanStyle(
                            color = colors.info,
                            background = colors.bgSelected,
                        ),
                    )
                    append("[image: ${node.alt.ifEmpty { node.destination }}]")
                    pop()
                }
            }
        }
    }
}
