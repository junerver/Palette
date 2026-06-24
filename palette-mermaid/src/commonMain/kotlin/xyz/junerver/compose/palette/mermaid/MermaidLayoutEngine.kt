package xyz.junerver.compose.palette.mermaid

object MermaidLayoutEngine {
    fun layout(diagram: MermaidDiagram): MermaidLayout {
        if (diagram.type == MermaidDiagramType.Sequence) return layoutSequence(diagram)
        if (diagram.type == MermaidDiagramType.ClassDiagram) return layoutClassDiagram(diagram)

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
            flowchartClassDefs = diagram.flowchartClassDefs,
            flowchartClassAssignments = diagram.flowchartClassAssignments,
            flowchartNodeStyles = diagram.flowchartNodeStyles,
            flowchartLinkStyles = diagram.flowchartLinkStyles,
        )
    }



    private fun layoutClassDiagram(diagram: MermaidDiagram): MermaidLayout {
        // Build node map from class definitions
        val nodes = linkedMapOf<String, MermaidNode>()
        diagram.classDefinitions.forEach { cls ->
            val label = cls.annotation?.let { "<<$it>>\n${cls.label}" } ?: cls.label
            nodes[cls.id] = MermaidNode(id = cls.id, label = label, shape = MermaidNodeShape.Rectangle)
        }
        // Also add nodes from relationships that might not have explicit class definitions
        diagram.classRelationships.forEach { rel ->
            if (rel.from !in nodes) nodes[rel.from] = MermaidNode(id = rel.from, label = rel.from, shape = MermaidNodeShape.Rectangle)
            if (rel.to !in nodes) nodes[rel.to] = MermaidNode(id = rel.to, label = rel.to, shape = MermaidNodeShape.Rectangle)
        }

        // Build edges from relationships
        val edges = diagram.classRelationships.map { rel ->
            MermaidEdge(
                from = rel.from,
                to = rel.to,
                label = rel.label,
                style = if (rel.type == MermaidClassRelationType.Dependency || rel.type == MermaidClassRelationType.Realization || rel.type == MermaidClassRelationType.DependencyLink) MermaidEdgeStyle.Dotted else MermaidEdgeStyle.Solid,
                arrow = when (rel.type) {
                    MermaidClassRelationType.Inheritance, MermaidClassRelationType.Realization -> MermaidEdgeArrow.None
                    MermaidClassRelationType.Link, MermaidClassRelationType.DependencyLink -> MermaidEdgeArrow.None
                    else -> MermaidEdgeArrow.Forward
                },
            )
        }

        val rankById = calculateRanks(MermaidDiagram(direction = diagram.direction, nodes = nodes, edges = edges))
        val orderByRank = mutableMapOf<Int, Int>()
        val positionedNodes = nodes.values.associate { node ->
            val rank = rankById.getOrDefault(node.id, 0)
            val order = orderByRank.getOrPut(rank) { 0 }
            orderByRank[rank] = order + 1
            node.id to PositionedMermaidNode(
                node = node,
                rank = rank,
                order = order,
                x = if (diagram.direction == MermaidDirection.LeftRight) rank * 200f else order * 200f,
                y = if (diagram.direction == MermaidDirection.LeftRight) order * 120f else rank * 120f,
            )
        }

        return MermaidLayout(
            type = MermaidDiagramType.ClassDiagram,
            direction = diagram.direction,
            nodes = positionedNodes,
            edges = edges,
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
