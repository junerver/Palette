package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kotlinx.coroutines.launch
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.components.code.PCodeBlock
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram
import xyz.junerver.compose.palette.components.latex.PLatexFormula
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.markdown.MarkdownInlineCode
import xyz.junerver.compose.palette.markdown.MarkdownInlineEmphasis
import xyz.junerver.compose.palette.markdown.MarkdownInlineHardBreak
import xyz.junerver.compose.palette.markdown.MarkdownInlineHtml
import xyz.junerver.compose.palette.markdown.MarkdownInlineImage
import xyz.junerver.compose.palette.markdown.MarkdownInlineLink
import xyz.junerver.compose.palette.markdown.MarkdownInlineLatex
import xyz.junerver.compose.palette.markdown.MarkdownInlineSubscript
import xyz.junerver.compose.palette.markdown.MarkdownInlineSuperscript
import xyz.junerver.compose.palette.markdown.MarkdownInlineHighlight
import xyz.junerver.compose.palette.markdown.MarkdownInlineNode
import xyz.junerver.compose.palette.markdown.MarkdownInlineSoftBreak
import xyz.junerver.compose.palette.markdown.MarkdownInlineStrikethrough
import xyz.junerver.compose.palette.markdown.MarkdownInlineStrong
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
    renderModel: MarkdownRenderModel? = null,
    onLinkClick: ((String) -> Unit)? = null,
    onAnchorClick: ((String) -> Unit)? = null,
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit = { image -> DefaultInlineImage(image) },
    // 阶段 C：Viewer 内置纵向滚动。嵌入到自身已可滚动容器（如页面 LazyColumn）时设为 false。
    verticalScroll: Boolean = true,
    // 阶段 C：透传给 fenced code 块的复制按钮（PCodeBlock.showCopyAction）。
    showCopyAction: Boolean = true,
) {
    val resolvedRenderModel =
        useCreation(renderModel, markdown) {
            renderModel ?: MarkdownRenderer.toRenderModel(MarkdownParser.parse(markdown))
        }.current

    var taskCounter by remember(markdown) { mutableIntStateOf(0) }

    // 内置滚动状态 + 用于锚点跳转的协程作用域。
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    // heading slug -> 在滚动容器内的 y 偏移，由各 heading 的 onGloballyPositioned 填充。
    val headingOffsets = remember(markdown) { mutableStateMapOf<String, Int>() }

    // 锚点跳转默认行为：调用方未提供 onAnchorClick 时，滚动到对应 heading。
    val effectiveAnchorClick: (String) -> Unit = onAnchorClick ?: { slug ->
        val target = headingOffsets[slug]
        if (target != null) {
            scope.launch { scrollState.animateScrollTo(target.coerceAtLeast(0)) }
        }
    }

    // 自动防嵌套滚动崩溃：父容器已提供无限高约束（即父容器自身可纵向滚动，如页面
    // LazyColumn / Column(Modifier.verticalScroll)）时，禁用 Viewer 内置滚动，
    // 把无限高约束透传给内容让其按需测量。调用方显式传 verticalScroll=false 时同样关闭。
    BoxWithConstraints(modifier = modifier) {
        // maxHeight == Infinity 表示父容器不限高度（自身可滚动），此时不能再嵌一层滚动。
        val effectiveScroll = verticalScroll && maxHeight != Dp.Infinity
        key(markdown) {
            MarkdownBlocks(
                blocks = resolvedRenderModel.blocks,
                modifier = if (effectiveScroll) {
                    Modifier.verticalScroll(scrollState)
                } else {
                    Modifier
                },
                onLinkClick = onLinkClick,
                onAnchorClick = effectiveAnchorClick,
                inlineImageContent = inlineImageContent,
                taskCheckboxEnabled = onTaskCheckedChange != null,
                nextTaskIndex = { taskCounter++ },
                onTaskCheckedChange = onTaskCheckedChange,
                showCopyAction = showCopyAction,
                onHeadingPositioned = { slug, y -> headingOffsets[slug] = y },
            )
        }
    }
}

/**
 * 渲染原子：将一组 [MarkdownRenderBlock] 迭代为带间距的 [Column]。
 * 声明为 public，便于自定义 UI 直接渲染解析后的 markdown 模型（而非走一体化的 [PMarkdownViewer]）。
 *
 * 注：[taskCheckboxEnabled]/[nextTaskIndex]/[onTaskCheckedChange]/[onHeadingPositioned]
 * 用于在块树间协调任务复选框状态与标题锚点定位，均已提供安全默认值（空操作），
 * 无需该联动时直接省略即可。
 */
@Composable
fun MarkdownBlocks(
    blocks: List<MarkdownRenderBlock>,
    modifier: Modifier = Modifier,
    blockSpacing: Dp = MarkdownDefaults.blockSpacing(),
    onLinkClick: ((String) -> Unit)?,
    onAnchorClick: ((String) -> Unit)? = null,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
    taskCheckboxEnabled: Boolean = false,
    nextTaskIndex: () -> Int = { -1 },
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
    showCopyAction: Boolean = true,
    onHeadingPositioned: (slug: String, y: Int) -> Unit = { _, _ -> },
) {
    // Ensure callbacks are always fresh even if composable skips recomposition
    val latestOnTaskCheckedChange by rememberUpdatedState(onTaskCheckedChange)
    val latestOnHeadingPositioned by rememberUpdatedState(onHeadingPositioned)

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
                taskCheckboxEnabled = taskCheckboxEnabled,
                nextTaskIndex = nextTaskIndex,
                onTaskCheckedChange = latestOnTaskCheckedChange,
                showCopyAction = showCopyAction,
                onHeadingPositioned = latestOnHeadingPositioned,
            )
        }
    }
}

/**
 * 渲染原子：通过 `when` 将单个 [MarkdownRenderBlock] 分发给对应的渲染器
 * （标题、代码、列表、表格、引用等）。声明为 public，便于自定义 markdown UI 组合使用。
 */
@Composable
fun MarkdownBlock(
    block: MarkdownRenderBlock,
    onLinkClick: ((String) -> Unit)?,
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
    taskCheckboxEnabled: Boolean = false,
    nextTaskIndex: () -> Int = { -1 },
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
    showCopyAction: Boolean = true,
    onHeadingPositioned: (slug: String, y: Int) -> Unit = { _, _ -> },
) {
    val latestOnTaskCheckedChange by rememberUpdatedState(onTaskCheckedChange)

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
                            .onGloballyPositioned { coords ->
                                onHeadingPositioned(block.id, coords.positionInRoot().y.toInt())
                            }
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
                taskCheckboxEnabled = taskCheckboxEnabled,
                nextTaskIndex = nextTaskIndex,
                onTaskCheckedChange = onTaskCheckedChange,
            )

        is MarkdownRenderBlock.TaskList ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                block.items.forEach { item ->
                    val taskIndex = nextTaskIndex()
                    key(item.checked) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        TaskCheckbox(
                            checked = item.checked,
                            enabled = taskCheckboxEnabled,
                            onCheckedChange = if (taskCheckboxEnabled) {
                                { latestOnTaskCheckedChange?.invoke(taskIndex) }
                            } else {
                                null
                            },
                            modifier = if (taskCheckboxEnabled) {
                                Modifier.testTag("task-checkbox:$taskIndex")
                            } else {
                                Modifier
                            },
                        )
                        InlineMarkdownText(
                            inlines = item.inlines,
                            color = PaletteTheme.colors.textPrimary,
                            style = PaletteTheme.typography.body,
                            onLinkClick = onLinkClick,
                            inlineImageContent = inlineImageContent,
                        )
                    }
                    } // key(item.checked)
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
                        taskCheckboxEnabled = taskCheckboxEnabled,
                        nextTaskIndex = nextTaskIndex,
                        onTaskCheckedChange = onTaskCheckedChange,
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
                showCopyAction = showCopyAction,
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
    taskCheckboxEnabled: Boolean = false,
    nextTaskIndex: () -> Int = { -1 },
    onTaskCheckedChange: ((taskIndex: Int) -> Unit)? = null,
) {
    val latestOnTaskCheckedChange by rememberUpdatedState(onTaskCheckedChange)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        block.listItems.forEachIndexed { index, item ->
            key(item.taskChecked) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                if (item.taskChecked != null) {
                    val taskIndex = nextTaskIndex()
                    TaskCheckbox(
                        checked = item.taskChecked!!,
                        enabled = taskCheckboxEnabled,
                        onCheckedChange = if (taskCheckboxEnabled) {
                            { latestOnTaskCheckedChange?.invoke(taskIndex) }
                        } else {
                            null
                        },
                        modifier = if (taskCheckboxEnabled) {
                            Modifier.testTag("task-checkbox:$taskIndex")
                        } else {
                            Modifier
                        },
                    )
                } else {
                    Text(
                        text = if (block.ordered) "${block.startNumber + index}." else "•",
                        style = PaletteTheme.typography.body.copy(color = PaletteTheme.colors.textSecondary),
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
                            taskCheckboxEnabled = taskCheckboxEnabled,
                            nextTaskIndex = nextTaskIndex,
                            onTaskCheckedChange = latestOnTaskCheckedChange,
                        )
                    }
                }
            }
            } // key(item.taskChecked)
        }
    }
}

/**
 * 渲染原子：未提供自定义图片组合时的默认行内图片占位符。声明为 public，
 * 便于调用方在自己的 [inlineImageContent] 槽位中复用。
 */
@Composable
fun DefaultInlineImage(image: MarkdownInlineImage) {
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

/**
 * 渲染原子：将一组行内 markdown 节点布局到 [BasicText] 风格的表面上，并处理链接与行内图片。
 * 声明为 public，便于需要在标准块流之外渲染行内 markdown 的自定义 UI 使用。
 */
@Composable
fun InlineMarkdownText(
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

/**
 * 渲染原子：由 [toAnnotatedContent] 生成的 [AnnotatedString] + 行内内容映射。
 * 声明为 public，因为它是 public 渲染原子 [toAnnotatedContent] 的返回类型
 * （public 成员不能暴露 private 类型）。
 */
data class MarkdownAnnotatedContent(
    val text: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>,
)

/**
 * 渲染原子：从一组 [MarkdownInlineNode] 构建 [AnnotatedString]（及行内内容映射），
 * 并应用 [PaletteTheme] 的样式/配色。声明为 public，便于自定义行内渲染器复用同一标注策略。
 *
 * 注：返回 [MarkdownAnnotatedContent]（同为 public）。
 */
@Composable
fun List<MarkdownInlineNode>.toAnnotatedContent(
    inlineImageContent: @Composable (MarkdownInlineImage) -> Unit,
): MarkdownAnnotatedContent {
    val colors = PaletteTheme.colors
    val tokens = PaletteTheme.componentThemes.utility
    val inlineContent = linkedMapOf<String, InlineTextContent>()
    var imageIndex = 0
    var latexIndex = 0
    // 行内公式 / 高亮 / 上下标的字号与色彩从顶层 token 派生：
    // - 行内公式按 latexInlineScale 放大、上下标缩到 0.7，保证可读且与段落协调。
    val bodyFontPx = PaletteTheme.typography.body.fontSize.value
    val latexFontPx = bodyFontPx * tokens.latexInlineScale
    val highlightColor = tokens.markdownInlineHighlightColor
    // 估算行内公式占位尺寸：高度约 1.6 行高，宽度按字符数粗估（公式居中渲染，容差较大）。
    fun latexWidthFor(tex: String): Float {
        val chars = tex.length.coerceIn(1, 40)
        return (latexFontPx * 0.7f * chars).coerceAtLeast(latexFontPx)
    }
    fun latexHeightFor(@Suppress("UNUSED_PARAMETER") tex: String): Float = latexFontPx * 1.6f
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

                        is MarkdownInlineSubscript -> {
                            pushStyle(
                                SpanStyle(
                                    baselineShift = BaselineShift.Subscript,
                                    fontSize = TextUnit(bodyFontPx * 0.7f, TextUnitType.Sp),
                                ),
                            )
                            appendNodes(node.children)
                            pop()
                        }

                        is MarkdownInlineSuperscript -> {
                            pushStyle(
                                SpanStyle(
                                    baselineShift = BaselineShift.Superscript,
                                    fontSize = TextUnit(bodyFontPx * 0.7f, TextUnitType.Sp),
                                ),
                            )
                            appendNodes(node.children)
                            pop()
                        }

                        is MarkdownInlineHighlight -> {
                            pushStyle(SpanStyle(background = highlightColor))
                            appendNodes(node.children)
                            pop()
                        }

                        is MarkdownInlineLatex -> {
                            val contentId = "$LatexInlineContentTag-${latexIndex++}"
                            append("￼")
                            append(contentId)
                            inlineContent[contentId] =
                                InlineTextContent(
                                    placeholder =
                                        Placeholder(
                                            width = TextUnit(latexWidthFor(node.tex), TextUnitType.Sp),
                                            height = TextUnit(latexHeightFor(node.tex), TextUnitType.Sp),
                                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                                        ),
                                ) {
                                    PLatexFormula(
                                        source = node.tex,
                                        fontSize = TextUnit(latexFontPx, TextUnitType.Sp),
                                    )
                                }
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
                            append("￼")
                            append(contentId)
                            append(" ")
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

@Composable
private fun TaskCheckbox(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val size = 18.dp
    val borderWidth = 1.5.dp
    val cornerRadius = 3.dp
    val fillColor = if (checked) PaletteTheme.colors.primary else PaletteTheme.colors.surface
    val borderColor = if (checked) PaletteTheme.colors.primary else PaletteTheme.colors.border
    val checkColor = PaletteTheme.colors.surface
    val bodyStyle = PaletteTheme.typography.body
    val lineHeightDp = with(LocalDensity.current) { bodyStyle.lineHeight.toDp() }
    val fontSizeDp = with(LocalDensity.current) { bodyStyle.fontSize.toDp() }
    val topPadding = ((lineHeightDp - fontSizeDp) / 2).coerceAtLeast(0.dp)

    Box(
        modifier = modifier
            .padding(top = topPadding, end = 8.dp)
            .defaultMinSize(minWidth = size, minHeight = size)
            .pointerInput(checked, enabled, onCheckedChange) {
                if (enabled && onCheckedChange != null) {
                    detectTapGestures { onCheckedChange() }
                }
            }
            .drawBehind {
                val s = size.toPx()
                val bw = borderWidth.toPx()
                val cr = cornerRadius.toPx()

                // Fill
                drawRoundRect(
                    color = fillColor,
                    size = Size(s, s),
                    cornerRadius = CornerRadius(cr),
                )
                // Border
                drawRoundRect(
                    color = borderColor,
                    size = Size(s, s),
                    cornerRadius = CornerRadius(cr),
                    style = Stroke(width = bw),
                )
                // Checkmark
                if (checked) {
                    val path = Path().apply {
                        val w = s * 0.65f
                        val h = s * 0.45f
                        val sx = s * 0.18f
                        val sy = s * 0.5f
                        moveTo(sx, sy)
                        lineTo(sx + w * 0.35f, sy + h * 0.5f)
                        lineTo(sx + w, sy - h * 0.3f)
                    }
                    drawPath(path, checkColor, style = Stroke(width = bw, cap = StrokeCap.Round))
                }
            }
    )
}

private const val LinkAnnotationTag = "palette-markdown-link"
private const val ImageInlineContentTag = "palette-markdown-image"
private const val LatexInlineContentTag = "palette-markdown-latex"
