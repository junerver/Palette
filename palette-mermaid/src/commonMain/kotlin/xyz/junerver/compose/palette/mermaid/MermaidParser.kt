package xyz.junerver.compose.palette.mermaid

data class MermaidDiagram(
    val direction: MermaidDirection,
    val nodes: Map<String, MermaidNode>,
    val edges: List<MermaidEdge>,
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
)

enum class MermaidEdgeStyle {
    Solid,
    Dotted,
    Thick,
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
        var direction = MermaidDirection.TopDown

        source
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("%%") }
            .forEachIndexed { index, line ->
                val header = parseHeader(line)
                if (header != null) {
                    direction = header
                    return@forEachIndexed
                }
                val edge = parseEdge(line) ?: return@forEachIndexed
                if (edge.from.id !in nodes) nodes[edge.from.id] = edge.from
                if (edge.to.id !in nodes) nodes[edge.to.id] = edge.to
                edges += MermaidEdge(from = edge.from.id, to = edge.to.id, label = edge.label, style = edge.style)

                if (index == 0) {
                    direction = MermaidDirection.TopDown
                }
            }

        return MermaidDiagram(direction = direction, nodes = nodes, edges = edges)
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
}

data class MermaidLayout(
    val direction: MermaidDirection,
    val nodes: Map<String, PositionedMermaidNode>,
    val edges: List<MermaidEdge>,
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

        return MermaidLayout(direction = diagram.direction, nodes = positioned, edges = diagram.edges)
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
