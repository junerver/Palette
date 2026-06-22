package xyz.junerver.compose.palette.mermaid

data class MermaidDiagram(
    val direction: MermaidDirection,
    val nodes: Map<String, MermaidNode>,
    val edges: List<MermaidEdge>,
    val notes: List<MermaidNote> = emptyList(),
    val type: MermaidDiagramType = MermaidDiagramType.Flowchart,
    val subgraphs: List<MermaidSubgraph> = emptyList(),
)

data class MermaidNode(
    val id: String,
    val label: String,
    val shape: MermaidNodeShape,
)

data class MermaidEdge(
    val from: String,
    val to: String,
    val label: String? = null,
    val style: MermaidEdgeStyle = MermaidEdgeStyle.Solid,
    val arrow: MermaidEdgeArrow = MermaidEdgeArrow.Forward,
    val sequenceIndex: Int = 0,
)

data class MermaidNote(
    val position: MermaidNotePosition,
    val participants: List<String>,
    val text: String,
    val sequenceIndex: Int,
)

data class MermaidSubgraph(
    val id: String,
    val label: String,
    val nodeIds: List<String>,
)

enum class MermaidEdgeStyle {
    Solid,
    Dotted,
    Thick,
}

enum class MermaidEdgeArrow {
    Forward,
    None,
    Bidirectional,
}

enum class MermaidNotePosition {
    LeftOf,
    RightOf,
    Over,
}

enum class MermaidDiagramType {
    Flowchart,
    Sequence,
}

enum class MermaidDirection {
    TopDown,
    BottomTop,
    LeftRight,
    RightLeft,
}

enum class MermaidNodeShape {
    Rectangle,
    Rounded,
    Stadium,
    Diamond,
    Circle,
}

object MermaidParser {
    fun parse(source: String): MermaidDiagram {
        val nodes = linkedMapOf<String, MermaidNode>()
        val edges = mutableListOf<MermaidEdge>()
        val notes = mutableListOf<MermaidNote>()
        var direction = MermaidDirection.TopDown
        var type = MermaidDiagramType.Flowchart
        var sequenceIndex = 0
        val subgraphs = mutableListOf<MermaidSubgraphBuilder>()
        var currentSubgraph: MermaidSubgraphBuilder? = null

        source
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("%%") }
            .forEachIndexed { index, line ->
                if (line.equals("sequenceDiagram", ignoreCase = true)) {
                    type = MermaidDiagramType.Sequence
                    direction = MermaidDirection.LeftRight
                    return@forEachIndexed
                }

                val participant = parseSequenceParticipant(line)
                if (participant != null) {
                    if (participant.id !in nodes) nodes[participant.id] = participant
                    return@forEachIndexed
                }

                val sequenceNote = parseSequenceNote(line, sequenceIndex)
                if (sequenceNote != null) {
                    sequenceNote.participants.forEach { participant ->
                        if (participant !in nodes) {
                            nodes[participant] = MermaidNode(participant, participant, MermaidNodeShape.Rectangle)
                        }
                    }
                    notes += sequenceNote
                    sequenceIndex += 1
                    return@forEachIndexed
                }

                val sequenceMessage = parseSequenceMessage(line)
                if (sequenceMessage != null) {
                    if (sequenceMessage.from.id !in nodes) nodes[sequenceMessage.from.id] = sequenceMessage.from
                    if (sequenceMessage.to.id !in nodes) nodes[sequenceMessage.to.id] = sequenceMessage.to
                    edges +=
                        MermaidEdge(
                            from = sequenceMessage.from.id,
                            to = sequenceMessage.to.id,
                            label = sequenceMessage.label,
                            style = sequenceMessage.style,
                            arrow = sequenceMessage.arrow,
                            sequenceIndex = sequenceIndex,
                        )
                    sequenceIndex += 1
                    return@forEachIndexed
                }

                val header = parseHeader(line)
                if (header != null) {
                    direction = header
                    return@forEachIndexed
                }

                if (line.equals("end", ignoreCase = true)) {
                    currentSubgraph = null
                    return@forEachIndexed
                }

                val subgraph = parseSubgraphStart(line)
                if (subgraph != null) {
                    subgraphs += subgraph
                    currentSubgraph = subgraph
                    return@forEachIndexed
                }

                val edge = parseEdge(line)
                if (edge != null) {
                    if (edge.from.id !in nodes) nodes[edge.from.id] = edge.from
                    if (edge.to.id !in nodes) nodes[edge.to.id] = edge.to
                    currentSubgraph?.add(edge.from.id)
                    currentSubgraph?.add(edge.to.id)
                    edges +=
                        MermaidEdge(
                            from = edge.from.id,
                            to = edge.to.id,
                            label = edge.label,
                            style = edge.style,
                            arrow = edge.arrow,
                        )
                    return@forEachIndexed
                }

                val standaloneNode = parseStandaloneNode(line) ?: return@forEachIndexed
                if (standaloneNode.id !in nodes) nodes[standaloneNode.id] = standaloneNode
                currentSubgraph?.add(standaloneNode.id)

                if (index == 0) {
                    direction = MermaidDirection.TopDown
                }
            }

        return MermaidDiagram(
            direction = direction,
            nodes = nodes,
            edges = edges,
            notes = notes,
            type = type,
            subgraphs = subgraphs.map { it.toSubgraph() }.filter { it.nodeIds.isNotEmpty() },
        )
    }

    private fun parseHeader(line: String): MermaidDirection? {
        val parts = line.split(Regex("\\s+"))
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
        val pipeLabeled = Regex("""^(.+?)\s*(-->|==>|-\.->|<-->)\|(.+?)\|\s*(.+)$""").matchEntire(line)
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
        if (!StandaloneNodeRegex.matches(line)) return null
        return parseNode(line)
    }

    private fun parseSequenceParticipant(line: String): MermaidNode? {
        val match = Regex("""^(participant|actor)\s+([A-Za-z0-9_]+)(?:\s+as\s+(.+))?$""").matchEntire(line) ?: return null
        val id = match.groupValues[2]
        val label = match.groupValues[3].trim().ifEmpty { id }
        return MermaidNode(id = id, label = label, shape = MermaidNodeShape.Rectangle)
    }

    private fun parseSequenceMessage(line: String): ParsedEdge? {
        val match = Regex("""^([A-Za-z0-9_]+)\s*(-->>|->>|-->|->)\s*([A-Za-z0-9_]+)\s*:\s*(.+)$""")
            .matchEntire(line)
            ?: return null
        return ParsedEdge(
            from = MermaidNode(match.groupValues[1], match.groupValues[1], MermaidNodeShape.Rectangle),
            to = MermaidNode(match.groupValues[3], match.groupValues[3], MermaidNodeShape.Rectangle),
            label = match.groupValues[4].trim().ifEmpty { null },
            style = if (match.groupValues[2].startsWith("--")) MermaidEdgeStyle.Dotted else MermaidEdgeStyle.Solid,
            arrow = MermaidEdgeArrow.Forward,
        )
    }

    private fun parseSequenceNote(
        line: String,
        sequenceIndex: Int,
    ): MermaidNote? {
        val match =
            Regex("""^Note\s+(right of|left of|over)\s+([A-Za-z0-9_,\s]+)\s*:\s*(.+)$""", RegexOption.IGNORE_CASE)
                .matchEntire(line)
                ?: return null
        val position =
            when (match.groupValues[1].lowercase()) {
                "left of" -> MermaidNotePosition.LeftOf
                "right of" -> MermaidNotePosition.RightOf
                else -> MermaidNotePosition.Over
            }
        val participants =
            match.groupValues[2]
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        if (participants.isEmpty()) return null
        return MermaidNote(
            position = position,
            participants = participants,
            text = match.groupValues[3].trim(),
            sequenceIndex = sequenceIndex,
        )
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
    ) {
        fun add(nodeId: String) {
            if (nodeId !in nodeIds) nodeIds += nodeId
        }

        fun toSubgraph(): MermaidSubgraph =
            MermaidSubgraph(
                id = id,
                label = label,
                nodeIds = nodeIds.toList(),
            )
    }

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
            "<-->" -> MermaidEdgeArrow.Bidirectional
            else -> MermaidEdgeArrow.Forward
        }

    private val StandaloneNodeRegex =
        Regex("""^[A-Za-z_][A-Za-z0-9_]*(?:(?:\(\[.+]\))|(?:\(\(.+\)\))|(?:\[.+])|(?:\(.+\))|(?:\{.+}))?$""")
}

data class MermaidLayout(
    val type: MermaidDiagramType,
    val direction: MermaidDirection,
    val nodes: Map<String, PositionedMermaidNode>,
    val edges: List<MermaidEdge>,
    val notes: List<MermaidNote> = emptyList(),
    val subgraphs: List<PositionedMermaidSubgraph> = emptyList(),
)

data class PositionedMermaidNode(
    val node: MermaidNode,
    val rank: Int,
    val order: Int,
    val x: Float,
    val y: Float,
)

data class PositionedMermaidSubgraph(
    val subgraph: MermaidSubgraph,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

object MermaidLayoutEngine {
    fun layout(diagram: MermaidDiagram): MermaidLayout {
        if (diagram.type == MermaidDiagramType.Sequence) return layoutSequence(diagram)

        val rankById = calculateRanks(diagram)
        val orderByRank = mutableMapOf<Int, Int>()
        val positionedNodes =
            diagram.nodes.values.associate { node ->
                val rank = rankById.getValue(node.id)
                val order = orderByRank.getOrPut(rank) { 0 }
                orderByRank[rank] = order + 1
                node.id to
                    PositionedMermaidNode(
                        node = node,
                        rank = rank,
                        order = order,
                        x = if (diagram.direction == MermaidDirection.LeftRight) rank * 180f else order * 180f,
                        y = if (diagram.direction == MermaidDirection.LeftRight) order * 96f else rank * 96f,
                    )
            }
        val positioned =
            if (diagram.subgraphs.isEmpty()) {
                positionedNodes
            } else {
                positionedNodes.mapValues { (_, node) ->
                    node.copy(
                        x = node.x + SubgraphHorizontalPadding,
                        y = node.y + SubgraphTopPadding,
                    )
                }
            }
        val subgraphs = layoutSubgraphs(diagram.subgraphs, positioned)

        return MermaidLayout(
            type = diagram.type,
            direction = diagram.direction,
            nodes = positioned,
            edges = diagram.edges,
            notes = diagram.notes,
            subgraphs = subgraphs,
        )
    }

    private fun layoutSequence(diagram: MermaidDiagram): MermaidLayout {
        val positioned =
            diagram.nodes.values.mapIndexed { index, node ->
                node.id to
                    PositionedMermaidNode(
                        node = node,
                        rank = index,
                        order = 0,
                        x = index * 180f,
                        y = 0f,
                    )
            }.toMap()

        return MermaidLayout(
            type = diagram.type,
            direction = diagram.direction,
            nodes = positioned,
            edges = diagram.edges,
            notes = diagram.notes,
        )
    }

    private fun layoutSubgraphs(
        subgraphs: List<MermaidSubgraph>,
        nodes: Map<String, PositionedMermaidNode>,
    ): List<PositionedMermaidSubgraph> =
        subgraphs.mapNotNull { subgraph ->
            val members = subgraph.nodeIds.mapNotNull(nodes::get)
            if (members.isEmpty()) return@mapNotNull null
            val minX = members.minOf { it.x }
            val minY = members.minOf { it.y }
            val maxX = members.maxOf { it.x }
            val maxY = members.maxOf { it.y }
            val x = (minX - SubgraphHorizontalPadding).coerceAtLeast(0f)
            val y = (minY - SubgraphTopPadding).coerceAtLeast(0f)
            PositionedMermaidSubgraph(
                subgraph = subgraph,
                x = x,
                y = y,
                width = maxX - x + FlowchartNodeWidth + SubgraphHorizontalPadding,
                height = maxY - y + FlowchartNodeHeight + SubgraphBottomPadding,
            )
        }

    private fun calculateRanks(diagram: MermaidDiagram): Map<String, Int> {
        val incoming = diagram.nodes.keys.associateWith { 0 }.toMutableMap()
        val outgoing = diagram.nodes.keys.associateWith { mutableListOf<String>() }.toMutableMap()
        diagram.edges.forEach { edge ->
            incoming[edge.to] = incoming.getValue(edge.to) + 1
            outgoing.getValue(edge.from) += edge.to
        }

        val rank = diagram.nodes.keys.associateWith { 0 }.toMutableMap()
        val queue = incoming.filterValues { it == 0 }.keys.toMutableList()
        val visited = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val id = queue.removeAt(0)
            if (!visited.add(id)) continue
            val nextRank = rank.getValue(id) + 1
            outgoing.getValue(id).forEach { target ->
                if (nextRank > rank.getValue(target)) rank[target] = nextRank
                incoming[target] = incoming.getValue(target) - 1
                if (incoming.getValue(target) <= 0) queue += target
            }
        }

        return rank
    }

    private const val FlowchartNodeWidth = 132f
    private const val FlowchartNodeHeight = 44f
    private const val SubgraphHorizontalPadding = 24f
    private const val SubgraphTopPadding = 32f
    private const val SubgraphBottomPadding = 20f
}
