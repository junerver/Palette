package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.components.code.PCodeBlock
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.markdown.MarkdownInlineCode
import xyz.junerver.compose.palette.markdown.MarkdownInlineEmphasis
import xyz.junerver.compose.palette.markdown.MarkdownInlineHardBreak
import xyz.junerver.compose.palette.markdown.MarkdownInlineSoftBreak
import xyz.junerver.compose.palette.markdown.MarkdownInlineHtml
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun PMarkdownViewer(
    markdown: String,
    modifier: Modifier = Modifier,
    renderModel: MarkdownRenderModel? = null,
    onLinkClick: ((String) -> Unit)? = null,
    onAnchorClick: ((String) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit = { image -> DefaultInlineImage(image) },
) {
    val resolvedRenderModel =
        useCreation(renderModel, markdown) {
            renderModel ?: MarkdownRenderer.toRenderModel(MarkdownParser.parse(markdown))
        }.current

    MarkdownBlocks(
        blocks = resolvedRenderModel.blocks,
        modifier = modifier,
        onLinkClick = onLinkClick,
        onAnchorClick = onAnchorClick,
        inlineImageContent = inlineImageContent,
    )
}

@Composable
private fun MarkdownBlocks(
    blocks: List<MarkdownRenderBlock>,
    modifier: Modifier = Modifier,
    blockSpacing: Dp = MarkdownDefaults.blockSpacing(),
    onLinkClick: ((String) -> Unit)?,
    onAnchorClick: ((String) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(blockSpacing),
    ) {
        blocks.forEach { block ->
            MarkdownBlock(
                block = block,
                onLinkClick = { destination ->
                    if (destination.startsWith("#") && onAnchorClick != null) {
                        onAnchorClick(destination.removePrefix("#"))
                    } else {
                        onLinkClick?.invoke(destination)
                    }
                },
                inlineImageContent = inlineImageContent,
            )
        }
    }
}

@Composable
private fun MarkdownBlock(
    block: MarkdownRenderBlock,
    onLinkClick: ((String) -> Unit)?,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
) {
    when (block) {
        is MarkdownRenderBlock.Heading ->
            InlineMarkdownText(
                inlines = block.inlines,
                color = PaletteTheme.colors.textPrimary,
                style =
                    when (block.level) {
                        1 -> PaletteTheme.typography.title.copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                        2 -> PaletteTheme.typography.title.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        else -> PaletteTheme.typography.body.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    },
                modifier =
                    if (block.id.isNotEmpty()) {
                        Modifier
                            .testTag("heading:${block.id}")
                            .semantics { contentDescription = "heading:${block.id}" }
                    } else {
                        Modifier
                    },
                onLinkClick = onLinkClick,
                inlineImageContent = inlineImageContent,
            )

        is MarkdownRenderBlock.Paragraph ->
            InlineMarkdownText(
                inlines = block.inlines,
                color = PaletteTheme.colors.textPrimary,
                style = PaletteTheme.typography.body,
                onLinkClick = onLinkClick,
                inlineImageContent = inlineImageContent,
            )

        is MarkdownRenderBlock.ListBlock ->
            MarkdownListBlock(
                block = block,
                onLinkClick = onLinkClick,
                inlineImageContent = inlineImageContent,
            )

        is MarkdownRenderBlock.TaskList ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                block.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        TaskCheckbox(checked = item.checked)
                        InlineMarkdownText(
                            inlines = item.inlines,
                            color = PaletteTheme.colors.textPrimary,
                            style = PaletteTheme.typography.body,
                            onLinkClick = onLinkClick,
                            inlineImageContent = inlineImageContent,
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
                if (block.children.isEmpty()) {
                    InlineMarkdownText(
                        inlines = block.inlines,
                        color = PaletteTheme.colors.textSecondary,
                        style = PaletteTheme.typography.body,
                        modifier = Modifier.padding(start = 10.dp),
                        onLinkClick = onLinkClick,
                        inlineImageContent = inlineImageContent,
                    )
                } else {
                    MarkdownBlocks(
                        blocks = block.children,
                        modifier = Modifier.padding(start = 10.dp),
                        blockSpacing = 6.dp,
                        onLinkClick = onLinkClick,
                        inlineImageContent = inlineImageContent,
                    )
                }
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
                    onLinkClick = onLinkClick,
                    inlineImageContent = inlineImageContent,
                )
                block.rowInlines.forEach { row ->
                    MarkdownTableRow(
                        cells = row.normalizedCellCount(block.headers.size),
                        alignments = block.alignments,
                        isHeader = false,
                        onLinkClick = onLinkClick,
                        inlineImageContent = inlineImageContent,
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
        is MarkdownRenderBlock.Html ->
            Text(
                text = block.html,
                color = PaletteTheme.colors.textPrimary,
                style = PaletteTheme.typography.body,
            )
        MarkdownRenderBlock.ThematicBreak -> Divider(color = PaletteTheme.colors.border)
    }
}

@Composable
private fun MarkdownListBlock(
    block: MarkdownRenderBlock.ListBlock,
    onLinkClick: ((String) -> Unit)?,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        block.listItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                if (item.taskChecked != null) {
                    TaskCheckbox(checked = item.taskChecked!!)
                } else {
                    Text(
                        text = if (block.ordered) "${block.startNumber + index}." else "-",
                        color = PaletteTheme.colors.textSecondary,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    InlineMarkdownText(
                        inlines = item.inlines,
                        color = PaletteTheme.colors.textPrimary,
                        style = PaletteTheme.typography.body,
                        onLinkClick = onLinkClick,
                        inlineImageContent = inlineImageContent,
                    )
                    if (item.children.isNotEmpty()) {
                        MarkdownBlocks(
                            blocks = item.children,
                            blockSpacing = 6.dp,
                            onLinkClick = onLinkClick,
                            inlineImageContent = inlineImageContent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCheckbox(checked: Boolean) {
    Box(
        modifier =
            Modifier
                .padding(top = 3.dp, end = 8.dp)
                .size(16.dp)
                .border(1.dp, PaletteTheme.colors.border)
                .background(
                    if (checked) {
                        PaletteTheme.colors.primary
                    } else {
                        PaletteTheme.colors.surface
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text(
                text = "x",
                color = PaletteTheme.colors.surface,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun DefaultInlineImage(image: MarkdownInlineImage) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .border(1.dp, PaletteTheme.colors.border)
                .background(PaletteTheme.colors.surfaceElevated)
                .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = image.alt.ifEmpty { image.destination },
            color = PaletteTheme.colors.textSecondary,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun MarkdownTableRow(
    cells: List<List<MarkdownInlineNode>>,
    alignments: List<MarkdownTableAlignment>,
    isHeader: Boolean,
    onLinkClick: ((String) -> Unit)?,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
) {
    val colors = PaletteTheme.colors
    Row(modifier = Modifier.fillMaxWidth()) {
        cells.forEachIndexed { index, cell ->
            InlineMarkdownText(
                inlines = cell,
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
                onLinkClick = onLinkClick,
                inlineImageContent = inlineImageContent,
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
private fun InlineMarkdownText(
    inlines: List<MarkdownInlineNode>,
    color: androidx.compose.ui.graphics.Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    onLinkClick: ((String) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit = { image -> DefaultInlineImage(image) },
) {
    val annotatedContent = inlines.toAnnotatedContent(inlineImageContent)
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val clickableModifier =
        if (onLinkClick == null) {
            modifier
        } else {
            modifier.pointerInput(annotatedContent.text, layoutResult.value, onLinkClick) {
                detectTapGestures { offset ->
                    val result = layoutResult.value ?: return@detectTapGestures
                    val textOffset = result.getOffsetForPosition(offset)
                    annotatedContent.text
                        .getStringAnnotations(LinkAnnotationTag, textOffset, textOffset)
                        .firstOrNull()
                        ?.let { annotation -> onLinkClick(annotation.item) }
                }
            }
        }

    BasicText(
        text = annotatedContent.text,
        modifier = clickableModifier,
        style =
            style.copy(
                color = color,
                textAlign = textAlign ?: style.textAlign,
            ),
        onTextLayout = { layoutResult.value = it },
        inlineContent = annotatedContent.inlineContent,
    )
}

private data class MarkdownAnnotatedContent(
    val text: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>,
)

@Composable
private fun List<MarkdownInlineNode>.toAnnotatedContent(
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
): MarkdownAnnotatedContent {
    val colors = PaletteTheme.colors
    val inlineContent = linkedMapOf<String, InlineTextContent>()
    var imageIndex = 0
    val text =
        buildAnnotatedString {
            fun appendNodes(nodes: List<MarkdownInlineNode>) {
                nodes.forEach { node ->
                    when (node) {
                        is MarkdownInlineText -> append(node.text)
                        MarkdownInlineHardBreak -> append("\n")
                        MarkdownInlineSoftBreak -> append(" ")
                        is MarkdownInlineHtml -> append(node.html)
                        is MarkdownInlineStrong -> {
                            pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                            appendNodes(node.children)
                            pop()
                        }

                        is MarkdownInlineEmphasis -> {
                            pushStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                            appendNodes(node.children)
                            pop()
                        }

                        is MarkdownInlineStrikethrough -> {
                            pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                            appendNodes(node.children)
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
                            pushStringAnnotation(LinkAnnotationTag, node.destination)
                            pushStyle(
                                SpanStyle(
                                    color = colors.primary,
                                    textDecoration = TextDecoration.Underline,
                                ),
                            )
                            appendNodes(node.children)
                            pop()
                            pop()
                        }

                        is MarkdownInlineImage -> {
                            val contentId = "$ImageInlineContentTag-${imageIndex++}"
                            append("\uFFFC")
                            append(contentId)
                            append("\u0000")
                            inlineContent[contentId] =
                                InlineTextContent(
                                    placeholder =
                                        Placeholder(
                                            width = 12.em,
                                            height = 2.4.em,
                                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                                        ),
                                ) {
                                    inlineImageContent(node)
                                }
                        }
                    }
                }
            }
            appendNodes(this@toAnnotatedContent)
        }
    return MarkdownAnnotatedContent(text = text, inlineContent = inlineContent)
}

private const val LinkAnnotationTag = "palette-markdown-link"
private const val ImageInlineContentTag = "palette-markdown-image"
