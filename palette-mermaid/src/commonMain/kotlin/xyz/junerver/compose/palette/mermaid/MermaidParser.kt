package xyz.junerver.compose.palette.mermaid

data class MermaidDiagram(
    val direction: MermaidDirection,
    val nodes: Map<String, MermaidNode>,
    val edges: List<MermaidEdge>,
    val notes: List<MermaidNote> = emptyList(),
    val type: MermaidDiagramType = MermaidDiagramType.Flowchart,
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
    val sequenceIndex: Int = 0,
)

data class MermaidNote(
    val position: MermaidNotePosition,
    val participants: List<String>,
    val text: String,
    val sequenceIndex: Int,
)

enum class MermaidEdgeStyle {
    Solid,
    Dotted,
    Thick,
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

                val edge = parseEdge(line)
                if (edge != null) {
                    if (edge.from.id !in nodes) nodes[edge.from.id] = edge.from
                    if (edge.to.id !in nodes) nodes[edge.to.id] = edge.to
                    edges +=
                        MermaidEdge(
                            from = edge.from.id,
                            to = edge.to.id,
                            label = edge.label,
                            style = edge.style,
                        )
                    return@forEachIndexed
                }

                val standaloneNode = parseStandaloneNode(line) ?: return@forEachIndexed
                if (standaloneNode.id !in nodes) nodes[standaloneNode.id] = standaloneNode

                if (index == 0) {
                    direction = MermaidDirection.TopDown
                }
            }

        return MermaidDiagram(direction = direction, nodes = nodes, edges = edges, notes = notes, type = type)
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

    private fun parseEdge(line: String): ParsedEdge? {
        val pipeLabeled = Regex("""^(.+?)\s*(-->|==>|-\.->)\|(.+?)\|\s*(.+)$""").matchEntire(line)
        if (pipeLabeled != null) {
            return ParsedEdge(
                from = parseNode(pipeLabeled.groupValues[1]),
                to = parseNode(pipeLabeled.groupValues[4]),
                label = pipeLabeled.groupValues[3].trim().ifEmpty { null },
                style = pipeLabeled.groupValues[2].toEdgeStyle(),
            )
        }

        val dottedLabeled = Regex("""^(.+?)\s+-\.\s+(.+?)\s+\.->\s+(.+)$""").matchEntire(line)
        if (dottedLabeled != null) {
            return ParsedEdge(
                from = parseNode(dottedLabeled.groupValues[1]),
                label = dottedLabeled.groupValues[2].trim().ifEmpty { null },
                to = parseNode(dottedLabeled.groupValues[3]),
                style = MermaidEdgeStyle.Dotted,
            )
        }

        val labeled = Regex("""^(.+?)\s+--\s+(.+?)\s+-->\s+(.+)$""").matchEntire(line)
        if (labeled != null) {
            return ParsedEdge(
                from = parseNode(labeled.groupValues[1]),
                label = labeled.groupValues[2].trim().ifEmpty { null },
                to = parseNode(labeled.groupValues[3]),
                style = MermaidEdgeStyle.Solid,
            )
        }

        val plain = Regex("""^(.+?)\s*(-->|==>|-\.->)\s*(.+)$""").matchEntire(line) ?: return null
        return ParsedEdge(
            from = parseNode(plain.groupValues[1]),
            label = null,
            to = parseNode(plain.groupValues[3]),
            style = plain.groupValues[2].toEdgeStyle(),
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
    )

    private fun String.toEdgeStyle(): MermaidEdgeStyle =
        when (this) {
            "==>" -> MermaidEdgeStyle.Thick
            "-.->" -> MermaidEdgeStyle.Dotted
            else -> MermaidEdgeStyle.Solid
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
)

data class PositionedMermaidNode(
    val node: MermaidNode,
    val rank: Int,
    val order: Int,
    val x: Float,
    val y: Float,
)

object MermaidLayoutEngine {
    fun layout(diagram: MermaidDiagram): MermaidLayout {
        if (diagram.type == MermaidDiagramType.Sequence) return layoutSequence(diagram)

        val rankById = calculateRanks(diagram)
        val orderByRank = mutableMapOf<Int, Int>()
        val positioned =
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

        return MermaidLayout(
            type = diagram.type,
            direction = diagram.direction,
            nodes = positioned,
            edges = diagram.edges,
            notes = diagram.notes,
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
}
