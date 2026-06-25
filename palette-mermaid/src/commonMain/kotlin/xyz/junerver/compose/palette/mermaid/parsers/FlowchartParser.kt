package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidEdge
import xyz.junerver.compose.palette.mermaid.MermaidEdgeArrow
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartClassAssignment
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartClassDef
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartClick
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartLinkStyle
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartNodeStyle
import xyz.junerver.compose.palette.mermaid.MermaidNode
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.MermaidSubgraph
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Flowchart parser, also the default fallback when a source has no recognised diagram
 * header (bare node/edge lines are treated as a top-down flowchart).
 *
 * Extracted verbatim from the old monolithic `MermaidParser.parse()` flowchart path:
 * header/direction, subgraphs (with per-subgraph direction), edges (all labelled/unlabelled
 * variants), standalone nodes, and flowchart style directives (classDef/class/apply,
 * nodeStyle, linkStyle, click).
 */
@Suppress("ComplexMethod", "LongMethod")
internal object FlowchartParser : MermaidDiagramParser {
    override val keyword: String = "flowchart"
    override val aliases: List<String> = listOf("graph")
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown

    private val standaloneNodeRegex =
        Regex("""^([A-Za-z0-9_]+\[.+]|.+?\(.+\)|.+?\{.+}|.+?\(\(.+\)\)|[A-Za-z0-9_]+)$""")

    override fun parse(lines: List<String>): ParseResult.Flowchart {
        val nodes = linkedMapOf<String, MermaidNode>()
        val edges = mutableListOf<MermaidEdge>()
        val notes = mutableListOf<xyz.junerver.compose.palette.mermaid.MermaidNote>()
        val subgraphs = mutableListOf<MermaidSubgraphBuilder>()
        var currentSubgraph: MermaidSubgraphBuilder? = null
        var direction = defaultDirection
        val classDefs = mutableListOf<MermaidFlowchartClassDef>()
        val classAssignments = mutableListOf<MermaidFlowchartClassAssignment>()
        val nodeStyles = mutableListOf<MermaidFlowchartNodeStyle>()
        val linkStyles = mutableListOf<MermaidFlowchartLinkStyle>()
        val clicks = mutableListOf<MermaidFlowchartClick>()

        lines.forEachIndexed { index, line ->
            // Header: graph/flowchart [TD|TB|BT|LR|RL].
            parseHeader(line)?.let { headerDir ->
                direction = headerDir
                return@forEachIndexed
            }

            // Subgraph-local direction.
            if (line.startsWith("direction ", ignoreCase = true) && currentSubgraph != null) {
                val dir = when (line.substringAfter("direction ").trim().uppercase()) {
                    "TD", "TB" -> MermaidDirection.TopDown
                    "BT" -> MermaidDirection.BottomTop
                    "LR" -> MermaidDirection.LeftRight
                    "RL" -> MermaidDirection.RightLeft
                    else -> null
                }
                if (dir != null) currentSubgraph!!.direction = dir
                return@forEachIndexed
            }

            if (line.equals("end", ignoreCase = true)) {
                currentSubgraph = null
                return@forEachIndexed
            }

            // Flowchart style directives.
            parseFlowchartClassDef(line)?.let { classDefs.add(it); return@forEachIndexed }
            parseFlowchartClassAssignment(line)?.let { classAssignments.add(it); return@forEachIndexed }
            parseFlowchartNodeStyle(line)?.let { nodeStyles.add(it); return@forEachIndexed }
            parseFlowchartLinkStyle(line)?.let { linkStyles.add(it); return@forEachIndexed }
            parseFlowchartClick(line)?.let { clicks.add(it); return@forEachIndexed }

            // Subgraph start.
            parseSubgraphStart(line)?.let { subgraph ->
                subgraphs += subgraph
                currentSubgraph = subgraph
                return@forEachIndexed
            }

            // Edge.
            parseEdge(line)?.let { edge ->
                if (edge.from.id !in nodes) nodes[edge.from.id] = edge.from
                if (edge.to.id !in nodes) nodes[edge.to.id] = edge.to
                currentSubgraph?.add(edge.from.id)
                currentSubgraph?.add(edge.to.id)
                edges += MermaidEdge(
                    from = edge.from.id,
                    to = edge.to.id,
                    label = edge.label,
                    style = edge.style,
                    arrow = edge.arrow,
                )
                return@forEachIndexed
            }

            // Standalone node.
            val standaloneNode = parseStandaloneNode(line) ?: return@forEachIndexed
            if (standaloneNode.id !in nodes) nodes[standaloneNode.id] = standaloneNode
            currentSubgraph?.add(standaloneNode.id)

            // A bare single line with no header defaults to top-down (matches old behaviour).
            if (index == 0) {
                direction = MermaidDirection.TopDown
            }
        }

        return ParseResult.Flowchart(
            direction = direction,
            nodes = nodes,
            edges = edges,
            notes = notes,
            subgraphs = subgraphs.map { it.toSubgraph() }.filter { it.nodeIds.isNotEmpty() },
            classDefs = classDefs,
            classAssignments = classAssignments,
            nodeStyles = nodeStyles,
            linkStyles = linkStyles,
            clicks = clicks,
        )
    }

    private fun parseHeader(line: String): MermaidDirection? {
        val parts = line.split(Regex("\\s++"))
        if (parts.firstOrNull() !in setOf("graph", "flowchart")) return null
        return when (parts.getOrNull(1)?.uppercase()) {
            "TD", "TB" -> MermaidDirection.TopDown
            "BT" -> MermaidDirection.BottomTop
            "LR" -> MermaidDirection.LeftRight
            "RL" -> MermaidDirection.RightLeft
            else -> MermaidDirection.TopDown
        }
    }

    private fun parseSubgraphStart(line: String): MermaidSubgraphBuilder? {
        if (!line.startsWith("subgraph ", ignoreCase = true)) return null
        val source = line.replace(Regex("""^subgraph\s+""", RegexOption.IGNORE_CASE), "").trim()
        if (source.isEmpty()) return null
        val labelStart = source.indexOf('[')
        val labelEnd = source.lastIndexOf(']')
        val id =
            if (labelStart > 0) {
                source.substring(0, labelStart).trim()
            } else {
                source.substringBefore(' ').trim()
            }
        val label =
            if (labelStart != -1 && labelEnd > labelStart) {
                source.substring(labelStart + 1, labelEnd).trim()
            } else {
                source.substringAfter(' ', source).trim()
            }
        val normalizedId = id.ifEmpty { label }.ifEmpty { "subgraph" }
        return MermaidSubgraphBuilder(id = normalizedId, label = label.ifEmpty { normalizedId })
    }

    private fun parseEdge(line: String): ParsedEdge? {
        val pipeLabeled = Regex("""^(.+?)\s*(-->|==>|-\.->|<-->|---)\|(.+?)\|\s*(.+)$""").matchEntire(line)
        if (pipeLabeled != null) {
            val marker = pipeLabeled.groupValues[2]
            return ParsedEdge(
                from = parseNode(pipeLabeled.groupValues[1]),
                to = parseNode(pipeLabeled.groupValues[4]),
                label = pipeLabeled.groupValues[3].trim().ifEmpty { null },
                style = marker.toEdgeStyle(),
                arrow = marker.toEdgeArrow(),
            )
        }

        val dottedLabeled = Regex("""^(.+?)\s+-\.\s+(.+?)\s+\.->\s+(.+)$""").matchEntire(line)
        if (dottedLabeled != null) {
            return ParsedEdge(
                from = parseNode(dottedLabeled.groupValues[1]),
                label = dottedLabeled.groupValues[2].trim().ifEmpty { null },
                to = parseNode(dottedLabeled.groupValues[3]),
                style = MermaidEdgeStyle.Dotted,
                arrow = MermaidEdgeArrow.Forward,
            )
        }

        val labeled = Regex("""^(.+?)\s+--\s+(.+?)\s+-->\s+(.+)$""").matchEntire(line)
        if (labeled != null) {
            return ParsedEdge(
                from = parseNode(labeled.groupValues[1]),
                label = labeled.groupValues[2].trim().ifEmpty { null },
                to = parseNode(labeled.groupValues[3]),
                style = MermaidEdgeStyle.Solid,
                arrow = MermaidEdgeArrow.Forward,
            )
        }

        val openLabeled = Regex("""^(.+?)\s+(--|==|-\.)(.+?)(---|==>|\.->|\.-)\s+(.+)$""").matchEntire(line)
        if (openLabeled != null) {
            val startMarker = openLabeled.groupValues[2]
            val endMarker = openLabeled.groupValues[4]
            return ParsedEdge(
                from = parseNode(openLabeled.groupValues[1]),
                label = openLabeled.groupValues[3].trim().ifEmpty { null },
                to = parseNode(openLabeled.groupValues[5]),
                style = startMarker.toLabeledEdgeStyle(),
                arrow = endMarker.toEdgeArrow(),
            )
        }

        val plain = Regex("""^(.+?)\s*(<-->|---|-->|==>|-\.->)\s*(.+)$""").matchEntire(line) ?: return null
        val marker = plain.groupValues[2]
        return ParsedEdge(
            from = parseNode(plain.groupValues[1]),
            label = null,
            to = parseNode(plain.groupValues[3]),
            style = marker.toEdgeStyle(),
            arrow = marker.toEdgeArrow(),
        )
    }

    private fun parseNode(value: String): MermaidNode {
        val trimmed = value.trim()
        val idEnd = trimmed.indexOfFirst { it == '[' || it == '(' || it == '{' || it.isWhitespace() }
            .let { if (it == -1) trimmed.length else it }
        val id = trimmed.substring(0, idEnd).trim()
        val shapeSource = trimmed.substring(idEnd).trim()
        val (label, shape) =
            when {
                shapeSource.startsWith("([") && shapeSource.endsWith("])") ->
                    shapeSource.drop(2).dropLast(2) to MermaidNodeShape.Stadium

                shapeSource.startsWith("((") && shapeSource.endsWith("))") ->
                    shapeSource.drop(2).dropLast(2) to MermaidNodeShape.Circle

                shapeSource.startsWith("[") && shapeSource.endsWith("]") ->
                    shapeSource.drop(1).dropLast(1) to MermaidNodeShape.Rectangle

                shapeSource.startsWith("(") && shapeSource.endsWith(")") ->
                    shapeSource.drop(1).dropLast(1) to MermaidNodeShape.Rounded

                shapeSource.startsWith("{") && shapeSource.endsWith("}") ->
                    shapeSource.drop(1).dropLast(1) to MermaidNodeShape.Diamond

                else -> id to MermaidNodeShape.Rectangle
            }

        return MermaidNode(id = id, label = label.trim().ifEmpty { id }, shape = shape)
    }

    private fun parseStandaloneNode(line: String): MermaidNode? {
        if (!standaloneNodeRegex.matches(line)) return null
        return parseNode(line)
    }

    // Flowchart style directives — delegated to the shared helpers in MermaidParser so the
    // style-parsing regexes stay in one place (they predate this refactor).
    private fun parseFlowchartClassDef(line: String) =
        MermaidParserFlowchartDirectives.parseClassDef(line)

    private fun parseFlowchartClassAssignment(line: String) =
        MermaidParserFlowchartDirectives.parseClassAssignment(line)

    private fun parseFlowchartNodeStyle(line: String) =
        MermaidParserFlowchartDirectives.parseNodeStyle(line)

    private fun parseFlowchartLinkStyle(line: String) =
        MermaidParserFlowchartDirectives.parseLinkStyle(line)

    private fun parseFlowchartClick(line: String) =
        MermaidParserFlowchartDirectives.parseClick(line)

    private fun String.toEdgeStyle(): MermaidEdgeStyle =
        when (this) {
            "==>" -> MermaidEdgeStyle.Thick
            "-.->" -> MermaidEdgeStyle.Dotted
            else -> MermaidEdgeStyle.Solid
        }

    private fun String.toLabeledEdgeStyle(): MermaidEdgeStyle =
        when (this) {
            "==" -> MermaidEdgeStyle.Thick
            "-." -> MermaidEdgeStyle.Dotted
            else -> MermaidEdgeStyle.Solid
        }

    private fun String.toEdgeArrow(): MermaidEdgeArrow =
        when (this) {
            "---", ".-" -> MermaidEdgeArrow.None
            "<-->", "<->" -> MermaidEdgeArrow.Bidirectional
            else -> MermaidEdgeArrow.Forward
        }

    private data class ParsedEdge(
        val from: MermaidNode,
        val to: MermaidNode,
        val label: String?,
        val style: MermaidEdgeStyle,
        val arrow: MermaidEdgeArrow,
    )

    private data class MermaidSubgraphBuilder(
        val id: String,
        val label: String,
        val nodeIds: MutableList<String> = mutableListOf(),
        var direction: MermaidDirection? = null,
    ) {
        fun add(nodeId: String) {
            if (nodeId !in nodeIds) nodeIds += nodeId
        }

        fun toSubgraph(): MermaidSubgraph =
            MermaidSubgraph(
                id = id,
                label = label,
                nodeIds = nodeIds.toList(),
                direction = direction,
            )
    }
}
