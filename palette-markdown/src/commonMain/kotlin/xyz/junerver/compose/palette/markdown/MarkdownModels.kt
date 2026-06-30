package xyz.junerver.compose.palette.markdown

import xyz.junerver.compose.palette.code.HighlightedCode
import xyz.junerver.compose.palette.mermaid.MermaidDiagram

data class MarkdownDocument(
    val blocks: List<MarkdownBlock>,
)

data class MarkdownSourceRange(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
    val source: String,
)

sealed interface MarkdownBlock

data class MarkdownHeading(
    val level: Int,
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownParagraph(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownListBlock(
    val items: List<String>,
    val ordered: Boolean,
    val itemInlines: List<List<MarkdownInlineNode>> = items.map(MarkdownInlineParser::parse),
    val startNumber: Int = 1,
    val tight: Boolean = true,
    val listItems: List<MarkdownListItem> =
        items.mapIndexed { index, item ->
            MarkdownListItem(
                text = item,
                inlines = itemInlines.getOrElse(index) { MarkdownInlineParser.parse(item) },
            )
        },
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownListItem(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val children: List<MarkdownBlock> = emptyList(),
    val taskChecked: Boolean? = null,
)

data class MarkdownTaskItem(
    val text: String,
    val checked: Boolean,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
)

data class MarkdownTaskListBlock(
    val items: List<MarkdownTaskItem>,
) : MarkdownBlock

data class MarkdownBlockQuote(
    val text: String,
    val inlines: List<MarkdownInlineNode> = MarkdownInlineParser.parse(text),
    val children: List<MarkdownBlock> = emptyList(),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownTableBlock(
    val headers: List<String>,
    val rows: List<List<String>>,
    val alignments: List<MarkdownTableAlignment> = List(headers.size) { MarkdownTableAlignment.Start },
    val headerInlines: List<List<MarkdownInlineNode>> = headers.map(MarkdownInlineParser::parse),
    val rowInlines: List<List<List<MarkdownInlineNode>>> = rows.map { row -> row.map(MarkdownInlineParser::parse) },
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

enum class MarkdownTableAlignment {
    Start,
    Center,
    End,
}

data class MarkdownCodeBlock(
    val language: String,
    val content: String,
    val title: String? = null,
    val showLineNumbers: Boolean = false,
    val highlightedLines: Set<Int> = emptySet(),
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownMermaidBlock(
    val source: String,
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data class MarkdownHtmlBlock(
    val html: String,
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

data object MarkdownThematicBreak : MarkdownBlock

sealed interface MarkdownInlineNode {
    val text: String
}

data class MarkdownInlineText(
    override val text: String,
) : MarkdownInlineNode

data class MarkdownInlineStrong(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

data class MarkdownInlineEmphasis(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

data class MarkdownInlineStrikethrough(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

data class MarkdownInlineCode(
    override val text: String,
) : MarkdownInlineNode

data object MarkdownInlineHardBreak : MarkdownInlineNode {
    override val text: String = "\n"
}

data object MarkdownInlineSoftBreak : MarkdownInlineNode {
    override val text: String = " "
}

data class MarkdownInlineHtml(
    val html: String,
) : MarkdownInlineNode {
    override val text: String
        get() = html
}

data class MarkdownInlineLink(
    val label: String,
    val destination: String,
    val title: String? = null,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(label)),
) : MarkdownInlineNode {
    override val text: String
        get() = label
}

data class MarkdownInlineImage(
    val alt: String,
    val destination: String,
    val title: String? = null,
) : MarkdownInlineNode {
    override val text: String
        get() = alt
}

/**
 * 行内 LaTeX 公式（`$...$`）。[tex] 为去除外层定界符后的 LaTeX 源码。
 */
data class MarkdownInlineLatex(
    val tex: String,
) : MarkdownInlineNode {
    override val text: String
        get() = tex
}

/**
 * 下标（`H~2~O`）。下标内容按行内节点递归解析（支持下标内嵌套强调 / 代码等）。
 */
data class MarkdownInlineSubscript(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

/**
 * 上标（`X^2^`）。
 */
data class MarkdownInlineSuperscript(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

/**
 * 高亮（`==KEY==`）。
 */
data class MarkdownInlineHighlight(
    override val text: String,
    val children: List<MarkdownInlineNode> = listOf(MarkdownInlineText(text)),
) : MarkdownInlineNode

internal data class MarkdownLinkTarget(
    val destination: String,
    val title: String? = null,
)

data class MarkdownRenderModel(
    val blocks: List<MarkdownRenderBlock>,
    val diagnostics: List<MarkdownRenderDiagnostic> = emptyList(),
    val frontmatter: Map<String, String> = emptyMap(),
    val toc: List<MarkdownTocEntry> = emptyList(),
)

data class MarkdownRenderDiagnostic(
    val code: MarkdownRenderDiagnosticCode,
    val message: String,
    val severity: MarkdownRenderDiagnosticSeverity = MarkdownRenderDiagnosticSeverity.Warning,
    val originCode: String? = null,
    val blockIndex: Int? = null,
    val line: Int? = null,
    val column: Int? = null,
    val endColumn: Int? = null,
    val source: String? = null,
)

enum class MarkdownRenderDiagnosticCode {
    CodeHighlighterDiagnostic,
    MermaidDiagnostic,
    MermaidParserFailure
}

enum class MarkdownRenderDiagnosticSeverity {
    Warning,
    Error
}

data class MarkdownRenderListItem(
    val text: String,
    val inlines: List<MarkdownInlineNode>,
    val children: List<MarkdownRenderBlock> = emptyList(),
    val taskChecked: Boolean? = null,
)

sealed interface MarkdownRenderBlock {
    data class Heading(
        val level: Int,
        val text: String,
        val inlines: List<MarkdownInlineNode>,
        val sourceRange: MarkdownSourceRange? = null,
        val id: String = "",
    ) : MarkdownRenderBlock

    data class Paragraph(
        val text: String,
        val inlines: List<MarkdownInlineNode>,
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class ListBlock(
        val items: List<String>,
        val ordered: Boolean,
        val itemInlines: List<List<MarkdownInlineNode>>,
        val startNumber: Int = 1,
        val tight: Boolean = true,
        val listItems: List<MarkdownRenderListItem> =
            items.mapIndexed { index, item ->
                MarkdownRenderListItem(
                    text = item,
                    inlines = itemInlines.getOrElse(index) { MarkdownInlineParser.parse(item) },
                )
            },
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class TaskList(
        val items: List<MarkdownTaskItem>,
    ) : MarkdownRenderBlock

    data class BlockQuote(
        val text: String,
        val inlines: List<MarkdownInlineNode>,
        val children: List<MarkdownRenderBlock> = emptyList(),
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Table(
        val headers: List<String>,
        val rows: List<List<String>>,
        val alignments: List<MarkdownTableAlignment>,
        val headerInlines: List<List<MarkdownInlineNode>>,
        val rowInlines: List<List<List<MarkdownInlineNode>>>,
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Code(
        val language: String,
        val highlighted: HighlightedCode,
        val title: String?,
        val showLineNumbers: Boolean,
        val highlightedLines: Set<Int>,
        val diagnostics: List<MarkdownRenderDiagnostic> = emptyList(),
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Mermaid(
        val source: String,
        val diagram: MermaidDiagram,
        val diagnostics: List<MarkdownRenderDiagnostic> = emptyList(),
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data class Html(
        val html: String,
        val sourceRange: MarkdownSourceRange? = null,
    ) : MarkdownRenderBlock

    data object ThematicBreak : MarkdownRenderBlock
}

/**
 * A YAML frontmatter block (`---\n...\n---`) appearing at the very start of a document.
 *
 * `fields` holds a flat `key: value` view of the most common frontmatter shape (title, author, date, …).
 * The renderer strips frontmatter from the rendered body and surfaces it as structured metadata on
 * [MarkdownRenderModel.frontmatter]; it is never a [MarkdownRenderBlock].
 */
data class MarkdownFrontmatter(
    val rawYaml: String,
    val fields: Map<String, String>,
    val sourceRange: MarkdownSourceRange? = null,
) : MarkdownBlock

/**
 * A single table-of-contents entry derived from a [MarkdownHeading].
 *
 * [id] matches the rendered heading's id (and therefore its `testTag("heading:<id>")`), so navigating
 * to an entry reuses the viewer's existing anchor-scroll mechanism.
 */
data class MarkdownTocEntry(
    val level: Int,
    val text: String,
    val id: String,
)

internal fun String.toHeadingSlug(): String {
    val slug = trim().lowercase().replace(Regex("""[^\w\s-]"""), "").replace(Regex("""\s+"""), "-").trim('-')
    return slug.ifEmpty { "heading" }
}
