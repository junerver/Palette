package xyz.junerver.compose.palette.markdown

import xyz.junerver.compose.palette.code.HighlightedCode
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.code.PaletteCodeDiagnostic
import xyz.junerver.compose.palette.code.PaletteCodeDiagnosticSeverity
import xyz.junerver.compose.palette.mermaid.MermaidDiagram
import xyz.junerver.compose.palette.mermaid.MermaidDiagnosticSeverity
import xyz.junerver.compose.palette.mermaid.MermaidParser
import xyz.junerver.compose.palette.mermaid.MermaidParseDiagnostic

object MarkdownParser {
    fun parse(source: String): MarkdownDocument {
        val lines = source.lines()
        val references = collectReferenceDefinitions(lines)
        val blocks = mutableListOf<MarkdownBlock>()
        var index = 0

        while (index < lines.size) {
            val line = lines[index]
            val trimmed = line.trim()
            val fence = trimmed.toFenceStart()
            when {
                trimmed.isEmpty() -> index += 1
                line.isIndentedCodeLine() -> {
                    val content = mutableListOf<String>()
                    while (index < lines.size && (lines[index].isIndentedCodeLine() || lines[index].trim().isEmpty())) {
                        content +=
                            if (lines[index].trim().isEmpty()) {
                                ""
                            } else {
                                lines[index].withoutCodeIndent()
                            }
                        index += 1
                    }
                    while (content.lastOrNull()?.isEmpty() == true) {
                        content.removeAt(content.lastIndex)
                    }
                    blocks += MarkdownCodeBlock(language = "plain", content = content.joinToString("\n"))
                }

                trimmed.toReferenceDefinition() != null -> index += 1
                fence != null -> {
                    val fenceInfo = CodeFenceInfo.parse(fence.info)
                    val content = mutableListOf<String>()
                    index += 1
                    while (index < lines.size && !lines[index].trim().isFenceEnd(fence)) {
                        content += lines[index]
                        index += 1
                    }
                    if (index < lines.size) index += 1
                    val blockContent = content.joinToString("\n")
                    blocks +=
                        if (fenceInfo.language == "mermaid") {
                            MarkdownMermaidBlock(blockContent)
                        } else {
                            MarkdownCodeBlock(
                                language = fenceInfo.language,
                                content = blockContent,
                                title = fenceInfo.title,
                                showLineNumbers = fenceInfo.showLineNumbers,
                                highlightedLines = fenceInfo.highlightedLines,
                            )
                        }
                }

                trimmed.startsWith(">") -> {
                    val quoteLines = mutableListOf<String>()
                    while (index < lines.size && lines[index].trim().startsWith(">")) {
                        quoteLines += lines[index].trim().removePrefix(">").trim()
                        index += 1
                    }
                    val nestedContent = quoteLines.joinToString("\n")
                    val nestedDoc = MarkdownParser.parse(nestedContent)
                    val firstParagraph = nestedDoc.blocks.filterIsInstance<MarkdownParagraph>().firstOrNull()
                    val text = firstParagraph?.text ?: ""
                    blocks += MarkdownBlockQuote(
                        text = text,
                        inlines = MarkdownInlineParser.parseWithTargets(text, references),
                        children = nestedDoc.blocks,
                    )
                }

                trimmed.isHtmlBlockLine() -> {
                    val start = index
                    val html = mutableListOf<String>()
                    while (index < lines.size && (lines[index].trim().isHtmlBlockLine() || lines[index].trim().isEmpty())) {
                        html += lines[index]
                        index += 1
                    }
                    while (html.lastOrNull()?.isEmpty() == true) {
                        html.removeAt(html.lastIndex)
                    }
                    blocks += MarkdownHtmlBlock(html = html.joinToString("\n"))
                }

                trimmed.isTaskListItem() -> {
                    val items = mutableListOf<MarkdownTaskItem>()
                    while (index < lines.size && lines[index].trim().isTaskListItem()) {
                        val match = TaskListRegex.matchEntire(lines[index].trim())
                        if (match != null) {
                            val text = match.groupValues[3].trim()
                            items +=
                                MarkdownTaskItem(
                                    text = text,
                                    checked = match.groupValues[2].equals("x", ignoreCase = true),
                                    inlines = MarkdownInlineParser.parseWithTargets(text, references),
                                )
                        }
                        index += 1
                    }
                    blocks += MarkdownTaskListBlock(items)
                }

                trimmed.isTableHeader(lines.getOrNull(index + 1)?.trim()) -> {
                    val headers = trimmed.tableCells()
                    val alignments = lines.getOrNull(index + 1).orEmpty().trim().tableAlignments(headers.size)
                    index += 2
                    val rows = mutableListOf<List<String>>()
                    while (index < lines.size && lines[index].trim().isTableRow()) {
                        val cells = lines[index].trim().tableCells()
                        // Normalize column count to match header
                        val normalizedCells = if (cells.size < headers.size) {
                            cells + List(headers.size - cells.size) { "" }
                        } else if (cells.size > headers.size) {
                            cells.take(headers.size)
                        } else {
                            cells
                        }
                        rows += normalizedCells
                        index += 1
                    }
                    blocks +=
                        MarkdownTableBlock(
                            headers = headers,
                            rows = rows,
                            alignments = alignments,
                            headerInlines = headers.map { MarkdownInlineParser.parseWithTargets(it, references) },
                            rowInlines = rows.map { row -> row.map { MarkdownInlineParser.parseWithTargets(it, references) } },
                        )
                }

                trimmed.canStartSetextHeading(lines.getOrNull(index + 1)?.trim()) -> {
                    val text = trimmed
                    blocks +=
                        MarkdownHeading(
                            level = lines[index + 1].trim().toSetextHeadingLevel(),
                            text = text,
                            inlines = MarkdownInlineParser.parseWithTargets(text, references),
                        )
                    index += 2
                }

                trimmed.matches(ThematicBreakRegex) -> {
                    blocks += MarkdownThematicBreak
                    index += 1
                }

                trimmed.startsWith("#") -> {
                    val match = HeadingRegex.matchEntire(trimmed)
                    if (match != null) {
                        val text = match.groupValues[2].trim().removeAtxClosingHashes()
                        blocks +=
                            MarkdownHeading(
                                level = match.groupValues[1].length.coerceIn(1, 6),
                                text = text,
                                inlines = MarkdownInlineParser.parseWithTargets(text, references),
                            )
                        index += 1
                    } else {
                        val paragraph = readParagraph(lines, index)
                        blocks +=
                            MarkdownParagraph(
                                text = paragraph.text,
                                inlines = MarkdownInlineParser.parseWithTargets(paragraph.text, references),
                            )
                        index = paragraph.nextIndex
                    }
                }

                trimmed.isListItem() -> {
                    val ordered = OrderedListRegex.matches(trimmed)
                    val startNumber = if (ordered) trimmed.orderedListStartNumber() else 1
                    val itemContents = mutableListOf<String>()
                    val itemChildren = mutableListOf<List<MarkdownBlock>>()
                    while (index < lines.size && lines[index].trim().isListItem() &&
                        !lines[index].trim().isTaskListItem()) {
                        val itemFirstLine = lines[index].trim().removeListMarker()
                        val continuationLines = mutableListOf<String>()
                        index += 1
                        // Collect continuation lines (indented lines that are not new list items)
                        while (index < lines.size) {
                            val nextLine = lines[index]
                            val nextTrimmed = nextLine.trim()
                            if (nextTrimmed.isEmpty()) {
                                // Blank line - check if next non-blank line continues the list item
                                val peekIndex = index + 1
                                if (peekIndex < lines.size) {
                                    val peekLine = lines[peekIndex]
                                    val peekTrimmed = peekLine.trim()
                                    if (peekTrimmed.isNotEmpty() && (peekLine.startsWith("  ") || peekLine.startsWith("\t")) && !peekTrimmed.isListItem()) {
                                        continuationLines += ""
                                        index += 1
                                        continue
                                    }
                                }
                                break
                            }
                            if ((nextTrimmed.isListItem() || nextTrimmed.isTaskListItem()) && !nextLine.startsWith("  ") && !nextLine.startsWith("\t")) {
                                break
                            }
                            if (nextLine.startsWith("  ") || nextLine.startsWith("\t")) {
                                continuationLines += nextLine.trimStart().let { if (it.startsWith("  ")) it.drop(2) else it }
                                index += 1
                            } else {
                                break
                            }
                        }
                        if (continuationLines.isNotEmpty()) {
                            val nestedContent = continuationLines.joinToString("\n")
                            val nestedDoc = MarkdownParser.parse(nestedContent)
                            itemChildren.add(nestedDoc.blocks)
                            itemContents += itemFirstLine
                        } else {
                            itemChildren.add(emptyList())
                            itemContents += itemFirstLine
                        }
                    }
                    blocks +=
                        MarkdownListBlock(
                            items = itemContents,
                            ordered = ordered,
                            itemInlines = itemContents.map { MarkdownInlineParser.parseWithTargets(it, references) },
                            startNumber = startNumber,
                            listItems = itemContents.mapIndexed { i, text ->
                                MarkdownListItem(
                                    text = text,
                                    inlines = MarkdownInlineParser.parseWithTargets(text, references),
                                    children = itemChildren[i],
                                )
                            },
                        )
                }

                else -> {
                    val paragraph = readParagraph(lines, index)
                    blocks +=
                        MarkdownParagraph(
                            text = paragraph.text,
                            inlines = MarkdownInlineParser.parseWithTargets(paragraph.text, references),
                        )
                    index = paragraph.nextIndex
                }
            }
        }

        return MarkdownDocument(blocks)
    }

    private fun readParagraph(
        lines: List<String>,
        start: Int,
    ): ParagraphRead {
        val parts = mutableListOf<String>()
        var index = start
        while (index < lines.size) {
            val trimmed = lines[index].trim()
            if (
                trimmed.isEmpty() ||
                lines[index].isIndentedCodeLine() ||
                trimmed.toFenceStart() != null ||
                trimmed.startsWith(">") ||
                HeadingRegex.matchEntire(trimmed) != null ||
                trimmed.matches(ThematicBreakRegex) ||
                trimmed.isTaskListItem() ||
                trimmed.isListItem() ||
                trimmed.toReferenceDefinition() != null
            ) {
                break
            }
            parts += trimmed
            index += 1
        }
        return ParagraphRead(text = parts.joinToString(" ").trim(), nextIndex = index)
    }

    private fun collectReferenceDefinitions(lines: List<String>): Map<String, MarkdownLinkTarget> {
        val definitions = mutableMapOf<String, MarkdownLinkTarget>()
        var index = 0
        while (index < lines.size) {
            val trimmed = lines[index].trim()
            val fence = trimmed.toFenceStart()
            if (lines[index].isIndentedCodeLine()) {
                index += 1
                while (index < lines.size && (lines[index].isIndentedCodeLine() || lines[index].trim().isEmpty())) {
                    index += 1
                }
            } else if (fence != null) {
                index += 1
                while (index < lines.size && !lines[index].trim().isFenceEnd(fence)) {
                    index += 1
                }
                if (index < lines.size) index += 1
            } else {
                val def = trimmed.toReferenceDefinition()
                if (def != null) {
                    var titleText = def.rawTitle.orEmpty()
                    var nextIndex = index + 1
                    // If no title on first line, check if next line starts a multi-line title
                    if (titleText.isEmpty() && nextIndex < lines.size) {
                        val nextTrimmed = lines[nextIndex].trim()
                        if (nextTrimmed.isNotEmpty() && nextTrimmed.first() in setOf('"', "'", '(')) {
                            titleText = nextTrimmed
                            nextIndex += 1
                        }
                    }
                    // Continue reading multi-line title
                    if (titleText.isNotEmpty() && titleText.first() in setOf('"', "'", '(') && !titleText.isCompleteTitle()) {
                        val continuation = StringBuilder(titleText)
                        while (nextIndex < lines.size) {
                            val nextLine = lines[nextIndex]
                            if (nextLine.trim().isEmpty()) break
                            continuation.append(" ").append(nextLine.trim())
                            nextIndex += 1
                            if (continuation.toString().isCompleteTitle()) break
                        }
                        titleText = continuation.toString()
                    }
                    val completeTitle = titleText.toLinkTitle()
                    definitions[def.label] = MarkdownLinkTarget(def.target.destination, completeTitle)
                    index = nextIndex
                } else {
                    index += 1
                }
            }
        }
        return definitions
    }

    private data class ParagraphRead(
        val text: String,
        val nextIndex: Int,
    )

    private val HeadingRegex = Regex("""^(#{1,6})\s+(.+)$""")
    private val ThematicBreakRegex = Regex("""^(-{3,}|\*{3,}|_{3,})$""")
    private val SetextHeadingUnderlineRegex = Regex("""^(=+|-+)$""")
    private val TaskListRegex = Regex("""^([-*+])\s+\[([ xX])]\s+(.+)$""")
    private val OrderedListRegex = Regex("""^(\d+)[.)]\s+.+$""")
    private val UnorderedListRegex = Regex("""^[-*+]\s+.+$""")
    private val ReferenceDefinitionRegex = Regex("""^\[([^\]]+)]:\s*(\S+)(?:\s+(.+))?$""")

    private fun String.canStartSetextHeading(nextLine: String?): Boolean =
        isNotEmpty() &&
            nextLine?.matches(SetextHeadingUnderlineRegex) == true &&
            HeadingRegex.matchEntire(this) == null &&
            !matches(ThematicBreakRegex) &&
            !isTaskListItem() &&
            !isListItem()

    private fun String.toSetextHeadingLevel(): Int =
        if (startsWith("=")) 1 else 2

    private fun String.removeAtxClosingHashes(): String =
        replace(AtxClosingHashesRegex, "").trim()

    private fun String.isListItem(): Boolean = OrderedListRegex.matches(this) || UnorderedListRegex.matches(this)

    private fun String.isTaskListItem(): Boolean = TaskListRegex.matches(this)

    private fun String.isHtmlBlockLine(): Boolean = HtmlBlockLineRegex.matches(this)

    private fun String.isIndentedCodeLine(): Boolean =
        startsWith("    ") || startsWith("\t")

    private fun String.withoutCodeIndent(): String =
        when {
            startsWith("\t") -> drop(1)
            startsWith("    ") -> drop(4)
            else -> this
        }

    private fun String.removeListMarker(): String =
        replace(Regex("""^(\d+[.)]|[-*+])\s+"""), "").trim()

    private fun String.orderedListStartNumber(): Int =
        OrderedListRegex.matchEntire(this)?.groupValues?.get(1)?.toIntOrNull() ?: 1

    private fun String.toReferenceDefinition(): MarkdownReferenceDefinition? {
        val match = ReferenceDefinitionRegex.matchEntire(this) ?: return null
        val label = match.groupValues[1].normalizedReferenceLabel().takeIf { it.isNotEmpty() } ?: return null
        val destination = match.groupValues[2].trimReferenceDestination().takeIf { it.isNotEmpty() } ?: return null
        val rawTitle = match.groupValues.getOrNull(3).orEmpty()
        val title = rawTitle.toLinkTitle()
        return MarkdownReferenceDefinition(label = label, target = MarkdownLinkTarget(destination, title), rawTitle = rawTitle)
    }

    private fun String.normalizedReferenceLabel(): String =
        trim()
            .replace(WhitespaceRegex, " ")
            .lowercase()

    private fun String.trimReferenceDestination(): String =
        trim().trim('<', '>')

    private fun String.toLinkTitle(): String? {
        val source = trim()
        if (source.length < 2) return null
        val quote = source.first()
        val endQuote =
            when (quote) {
                '"', '\'' -> quote
                '(' -> ')'
                else -> return null
            }
        if (source.last() != endQuote) return null
        return source.drop(1).dropLast(1).trim()
    }

    private fun String.isTableHeader(nextLine: String?): Boolean =
        isTableRow() && nextLine?.matches(TableDelimiterRegex) == true

    private fun String.isTableRow(): Boolean = contains("|") && tableCells().any { it.isNotEmpty() }

    private fun String.tableCells(): List<String> {
        val source = trim().trimTableBoundaryPipes()
        val cells = mutableListOf<String>()
        val current = StringBuilder()
        var index = 0
        var codeFenceLength = 0
        while (index < source.length) {
            when {
                source[index] == '\\' && source.getOrNull(index + 1) == '|' -> {
                    current.append('|')
                    index += 2
                }

                source[index] == '`' -> {
                    val length = source.countRepeatedFrom(index, '`')
                    if (codeFenceLength == 0) {
                        codeFenceLength = length
                    } else if (length == codeFenceLength) {
                        codeFenceLength = 0
                    }
                    current.append(source.substring(index, index + length))
                    index += length
                }

                source[index] == '|' && codeFenceLength == 0 -> {
                    cells += current.toString().trim()
                    current.clear()
                    index += 1
                }

                else -> {
                    current.append(source[index])
                    index += 1
                }
            }
        }
        cells += current.toString().trim()
        return cells
    }

    private fun String.tableAlignments(columnCount: Int): List<MarkdownTableAlignment> {
        val parsed =
            trim()
                .trim('|')
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { delimiter ->
                    when {
                        delimiter.startsWith(":") && delimiter.endsWith(":") -> MarkdownTableAlignment.Center
                        delimiter.endsWith(":") -> MarkdownTableAlignment.End
                        else -> MarkdownTableAlignment.Start
                    }
                }
        return parsed.normalizedAlignments(columnCount)
    }

    private fun List<MarkdownTableAlignment>.normalizedAlignments(columnCount: Int): List<MarkdownTableAlignment> {
        if (size == columnCount) return this
        if (size > columnCount) return take(columnCount)
        return this + List(columnCount - size) { MarkdownTableAlignment.Start }
    }

    private fun String.trimTableBoundaryPipes(): String {
        var start = 0
        var end = length
        if (getOrNull(start) == '|') start += 1
        if (end > start && getOrNull(end - 1) == '|') end -= 1
        return substring(start, end)
    }

    private fun String.countRepeatedFrom(
        start: Int,
        char: Char,
    ): Int {
        var index = start
        while (index < length && this[index] == char) index += 1
        return index - start
    }

    private val HtmlBlockLineRegex = Regex("""^</?\s*[A-Za-z][A-Za-z0-9-]*(?:\s+[^<>]*)?/?>.*$""")
    private val TableDelimiterRegex = Regex("""^\|?\s*:?-{3,}:?\s*(\|\s*:?-{3,}:?\s*)+\|?$""")
    private val AtxClosingHashesRegex = Regex("""\s+#+\s*$""")
    private val WhitespaceRegex = Regex("""\s+""")

    private data class MarkdownReferenceDefinition(
        val label: String,
        val target: MarkdownLinkTarget,
        val rawTitle: String = "",
    )
    
    private fun String.isCompleteTitle(): Boolean {
        val trimmed = trim()
        if (trimmed.length < 2) return false
        val quote = trimmed.first()
        val endQuote = when (quote) {
            '"' -> '"'
            '\'' -> '\''
            '(' -> ')'
            else -> return false
        }
        return trimmed.last() == endQuote && trimmed.length >= 2
    }

    private data class MarkdownFence(
        val marker: Char,
        val length: Int,
        val info: String,
    )

    private fun String.toFenceStart(): MarkdownFence? {
        val marker = firstOrNull()?.takeIf { it == '`' || it == '~' } ?: return null
        val length = takeWhile { it == marker }.length
        if (length < 3) return null
        return MarkdownFence(marker = marker, length = length, info = drop(length).trim())
    }

    private fun String.isFenceEnd(fence: MarkdownFence): Boolean {
        val length = takeWhile { it == fence.marker }.length
        return length >= fence.length && drop(length).trim().isEmpty()
    }

    private data class CodeFenceInfo(
        val language: String,
        val title: String?,
        val showLineNumbers: Boolean,
        val highlightedLines: Set<Int>,
    ) {
        companion object {
            fun parse(source: String): CodeFenceInfo {
                val language = source.substringBefore(' ').trim().lowercase().ifEmpty { "plain" }
                return CodeFenceInfo(
                    language = language,
                    title = TitleRegex.find(source)?.groupValues?.get(1)?.trim()?.ifEmpty { null },
                    showLineNumbers = source.contains("showLineNumbers", ignoreCase = true),
                    highlightedLines = HighlightLinesRegex.find(source)?.groupValues?.get(1).orEmpty().toHighlightedLines(),
                )
            }

            private val TitleRegex = Regex("""title="([^"]+)"""")
            private val HighlightLinesRegex = Regex("""\{([0-9,\-\s]+)}""")
        }
    }

    private fun String.toHighlightedLines(): Set<Int> =
        split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .flatMap { item ->
                val rangeParts = item.split('-', limit = 2).map { it.trim().toIntOrNull() }
                val start = rangeParts.getOrNull(0)
                val end = rangeParts.getOrNull(1)
                when {
                    start == null -> emptyList()
                    end == null -> listOf(start)
                    end >= start -> (start..end).toList()
                    else -> listOf(start)
                }
            }
            .filter { it > 0 }
            .toSet()
}

    private fun toRenderBlock(
        block: MarkdownBlock,
        mermaidParser: (String) -> MermaidDiagram,
        diagnostics: MutableList<MarkdownRenderDiagnostic>,
        blockIndex: Int?,
        headingIdCounts: MutableMap<String, Int>,
    ): MarkdownRenderBlock =
        when (block) {
            is MarkdownHeading -> {
                val baseSlug = block.inlines.toPlainText().toHeadingSlug()
                val count = headingIdCounts.getOrDefault(baseSlug, 0)
                headingIdCounts[baseSlug] = count + 1
                val id = if (count == 0) baseSlug else "\$baseSlug-\${count + 1}"
                MarkdownRenderBlock.Heading(
                    level = block.level,
                    text = block.text,
                    inlines = block.inlines,
                    sourceRange = block.sourceRange,
                    id = id,
                )
            }
            is MarkdownParagraph -> MarkdownRenderBlock.Paragraph(block.text, block.inlines, block.sourceRange)
            is MarkdownListBlock ->
                MarkdownRenderBlock.ListBlock(
                    items = block.items,
                    ordered = block.ordered,
                    itemInlines = block.itemInlines,
                    startNumber = block.startNumber,
                    tight = block.tight,
                    listItems =
                        block.listItems.map { item ->
                            MarkdownRenderListItem(
                                text = item.text,
                                inlines = item.inlines,
                                children =
                                    item.children.map { child ->
                                        toRenderBlock(
                                            block = child,
                                            mermaidParser = mermaidParser,
                                            diagnostics = diagnostics,
                                            blockIndex = blockIndex,
                                            headingIdCounts = headingIdCounts,
                                        )
                                    },
                                taskChecked = item.taskChecked,
                            )
                        },
                    sourceRange = block.sourceRange,
                )
            is MarkdownTaskListBlock -> MarkdownRenderBlock.TaskList(block.items)
            is MarkdownBlockQuote ->
                MarkdownRenderBlock.BlockQuote(
                    text = block.text,
                    inlines = block.inlines,
                    children =
                        block.children.map { child ->
                            toRenderBlock(
                                block = child,
                                mermaidParser = mermaidParser,
                                diagnostics = diagnostics,
                                blockIndex = blockIndex,
                                headingIdCounts = headingIdCounts,
                            )
                        },
                    sourceRange = block.sourceRange,
                )
            is MarkdownTableBlock ->
                MarkdownRenderBlock.Table(
                    headers = block.headers,
                    rows = block.rows,
                    alignments = block.alignments,
                    headerInlines = block.headerInlines,
                    rowInlines = block.rowInlines,
                    sourceRange = block.sourceRange,
                )

            is MarkdownCodeBlock -> {
                val highlighted = PaletteCodeHighlighter.highlightWithDiagnostics(block.content, block.language)
                val blockDiagnostics =
                    highlighted.diagnostics.map { diagnostic ->
                        diagnostic.toMarkdownRenderDiagnostic(blockIndex = blockIndex)
                    }
                diagnostics += blockDiagnostics
                MarkdownRenderBlock.Code(
                    language = block.language,
                    highlighted = highlighted,
                    title = block.title,
                    showLineNumbers = block.showLineNumbers,
                    highlightedLines = block.highlightedLines,
                    diagnostics = blockDiagnostics,
                    sourceRange = block.sourceRange,
                )
            }

            is MarkdownMermaidBlock ->
                runCatching {
                    val diagram = mermaidParser(block.source)
                    val blockDiagnostics =
                        diagram.diagnostics.map { diagnostic ->
                            diagnostic.toMarkdownRenderDiagnostic(blockIndex = blockIndex)
                        }
                    diagnostics += blockDiagnostics
                    MarkdownRenderBlock.Mermaid(
                        source = block.source,
                        diagram = diagram,
                        diagnostics = blockDiagnostics,
                        sourceRange = block.sourceRange,
                    )
                }.getOrElse { error ->
                    val diagnostic =
                        MarkdownRenderDiagnostic(
                            code = MarkdownRenderDiagnosticCode.MermaidParserFailure,
                            message = "Mermaid block could not be parsed: \${error.message ?: error::class.simpleName}",
                            severity = MarkdownRenderDiagnosticSeverity.Error,
                            blockIndex = blockIndex,
                            source = block.source,
                        )
                    diagnostics += diagnostic
                    MarkdownRenderBlock.Code(
                        language = "mermaid",
                        highlighted = PaletteCodeHighlighter.highlight(block.source, "mermaid"),
                        title = null,
                        showLineNumbers = false,
                        highlightedLines = emptySet(),
                        diagnostics = listOf(diagnostic),
                        sourceRange = block.sourceRange,
                    )
                }

            is MarkdownHtmlBlock -> MarkdownRenderBlock.Html(block.html, block.sourceRange)

            MarkdownThematicBreak -> MarkdownRenderBlock.ThematicBreak
        }

    private fun MermaidParseDiagnostic.toMarkdownRenderDiagnostic(blockIndex: Int?): MarkdownRenderDiagnostic =
        MarkdownRenderDiagnostic(
            code = MarkdownRenderDiagnosticCode.MermaidDiagnostic,
            message = message,
            severity =
                when (severity) {
                    MermaidDiagnosticSeverity.Warning -> MarkdownRenderDiagnosticSeverity.Warning
                    MermaidDiagnosticSeverity.Error -> MarkdownRenderDiagnosticSeverity.Error
                },
            originCode = code.name,
            blockIndex = blockIndex,
            line = line,
            column = column,
            endColumn = endColumn,
            source = source,
        )

    private fun PaletteCodeDiagnostic.toMarkdownRenderDiagnostic(blockIndex: Int?): MarkdownRenderDiagnostic =
        MarkdownRenderDiagnostic(
            code = MarkdownRenderDiagnosticCode.CodeHighlighterDiagnostic,
            message = message,
            severity =
                when (severity) {
                    PaletteCodeDiagnosticSeverity.Warning -> MarkdownRenderDiagnosticSeverity.Warning
                    PaletteCodeDiagnosticSeverity.Error -> MarkdownRenderDiagnosticSeverity.Error
                },
            blockIndex = blockIndex,
            line = line,
            column = column,
        )

    private fun List<MarkdownInlineNode>.toPlainText(): String =
        joinToString("") { node ->
            when (node) {
                is MarkdownInlineText -> node.text
                is MarkdownInlineStrong -> node.children.toPlainText()
                is MarkdownInlineEmphasis -> node.children.toPlainText()
                is MarkdownInlineStrikethrough -> node.children.toPlainText()
                is MarkdownInlineCode -> node.text
                is MarkdownInlineLink -> node.children.toPlainText()
                is MarkdownInlineImage -> node.alt
                is MarkdownInlineHtml -> node.html
                is MarkdownInlineHardBreak -> " "
                is MarkdownInlineSoftBreak -> " "
            }
        }


