package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MindmapNode
import xyz.junerver.compose.palette.mermaid.MindmapNodeShape
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Mindmap parser. Mermaid mindmaps use **indentation to define hierarchy**: each line's
 * leading spaces determine its depth (0 = root), and a node's parent is the most recent
 * preceding node at depth-1.
 *
 * Node shapes mirror flowchart syntax:
 * - `id[text]` square, `id(text)` rounded, `id((text))` circle, `id{{text}}` hexagon,
 *   `id)text(` cloud, `id))text((` bang, bare text = default.
 *
 * `::icon(...)` / `:::class` decoration lines are skipped (not structural).
 */
internal object MindmapParser : MermaidDiagramParser {
    override val keyword: String = "mindmap"
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true
    override val preservesIndentation: Boolean = true

    private val shapes: List<Pair<Regex, MindmapNodeShape>> = listOf(
        // Circle: id((text)) — double parens first (before single-paren rounded matches).
        Regex("""^\S*\(\((.+)\)\)""") to MindmapNodeShape.Circle,
        // Hexagon: id{{text}}
        Regex("""^\S*\{\{(.+)\}\}""") to MindmapNodeShape.Hexagon,
        // Bang: id))text((
        Regex("""^\S*\)\)(.+)\(\(""") to MindmapNodeShape.Bang,
        // Square: id[text]
        Regex("""^\S*\[(.+)]""") to MindmapNodeShape.Square,
        // Cloud: id)text(
        Regex("""^\S*\)(.+)\(""") to MindmapNodeShape.Cloud,
        // Rounded: id(text) — last among paren-based (after circle/cloud).
        Regex("""^\S*\((.+)\)""") to MindmapNodeShape.Rounded,
    )

    override fun parse(lines: List<String>): ParseResult.MindmapDiagram {
        // First pass: collect content lines (drop header + blank/icon/class decoration) so we
        // can normalize indentation. Mindmap depth is *relative*: the shallowest node is the
        // root (depth 0) regardless of how many leading spaces it has.
        val content = lines
            .drop(1) // skip `mindmap` header
            .map { line -> line to line.takeWhile { c -> c == ' ' }.length }
            .filterNot { (line, _) -> line.isBlank() }
            .filterNot { (line, _) -> line.trimStart().startsWith("::icon(") || line.trimStart().startsWith(":::") }

        val nodes = mutableListOf<MindmapNode>()
        if (content.isEmpty()) {
            return ParseResult.MindmapDiagram(direction = defaultDirection, nodes = nodes)
        }

        // Map each distinct indent value to a 0-based depth tier. Mindmap nesting is by
        // indentation *level*, not absolute space count, so two-spaces-per-level and
        // four-spaces-per-level both produce the same tree. The shallowest tier is depth 0.
        val depthByIndent = content.map { it.second }.distinct().sorted().withIndex().associate { (tier, indent) -> indent to tier }

        // Stack of (depth, nodeId) tracking the ancestor chain for parent resolution.
        val ancestorStack = mutableListOf<Pair<Int, String>>()
        var autoId = 0

        content.forEach { (line, rawIndent) ->
            val depth = depthByIndent.getValue(rawIndent)
            val trimmed = line.trim()
            val (label, shape) = parseLabelAndShape(trimmed)
            if (label.isEmpty()) return@forEach

            autoId += 1
            val id = "m$autoId"

            // Resolve parent: pop ancestors whose depth >= current depth, then the top is parent.
            while (ancestorStack.isNotEmpty() && ancestorStack.last().first >= depth) {
                ancestorStack.removeLast()
            }
            val parentId = ancestorStack.lastOrNull()?.second
            nodes.add(MindmapNode(id = id, label = label, shape = shape, depth = depth, parentId = parentId))
            ancestorStack.add(depth to id)
        }

        return ParseResult.MindmapDiagram(
            direction = defaultDirection,
            nodes = nodes,
        )
    }

    private fun parseLabelAndShape(text: String): Pair<String, MindmapNodeShape> {
        for ((regex, shape) in shapes) {
            val match = regex.matchEntire(text) ?: continue
            val label = match.groupValues[1].trim()
            return label to shape
        }
        // Default: bare text (possibly without an id prefix).
        return text to MindmapNodeShape.Default
    }
}
