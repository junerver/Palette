package xyz.junerver.compose.palette.mermaid

object MermaidLayoutEngine {
    fun layout(diagram: MermaidDiagram): MermaidLayout {
        if (diagram.type == MermaidDiagramType.Sequence) return layoutSequence(diagram)
        if (diagram.type == MermaidDiagramType.ClassDiagram) return layoutClassDiagram(diagram)
        if (diagram.type == MermaidDiagramType.ErDiagram) return layoutErDiagram(diagram)
        if (diagram.type == MermaidDiagramType.StateDiagram) return layoutStateDiagram(diagram)
        if (diagram.type == MermaidDiagramType.PieDiagram) return layoutPieDiagram(diagram)
        if (diagram.type == MermaidDiagramType.GanttDiagram) return layoutGanttDiagram(diagram)

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
            // Reuse the curvature-offset map to fan out multi-edge endpoint pairs and back
            // edges so they no longer collapse onto a single overlapping line.
            stateEdgeOffsets = calculateStateEdgeOffsets(diagram.edges),
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

        // Build edges from relationships. Preserve the relation type so the renderer can
        // draw the correct UML marker; the marker placement is decided at draw time.
        val relationTypes = mutableMapOf<Int, MermaidClassRelationType>()
        val edges = diagram.classRelationships.mapIndexed { index, rel ->
            relationTypes[index] = rel.type
            MermaidEdge(
                from = rel.from,
                to = rel.to,
                label = rel.label,
                style = if (rel.type == MermaidClassRelationType.Dependency || rel.type == MermaidClassRelationType.Realization || rel.type == MermaidClassRelationType.DependencyLink) MermaidEdgeStyle.Dotted else MermaidEdgeStyle.Solid,
                // No generic arrowhead — UML markers are drawn explicitly from the relation type.
                arrow = MermaidEdgeArrow.None,
            )
        }

        val rankById: Map<String, Int> = calculateRanks(MermaidDiagram(direction = diagram.direction, nodes = nodes, edges = edges))
        val orderByRank = mutableMapOf<Int, Int>()
        val positionedNodes = nodes.values.associate { node ->
            val rank = rankById[node.id] ?: 0
            val order = orderByRank.getOrPut(rank) { 0 }
            orderByRank[rank] = order + 1
            node.id to PositionedMermaidNode(
                node = node,
                rank = rank,
                order = order,
                x = if (diagram.direction == MermaidDirection.LeftRight) rank.toFloat() * 200f else order.toFloat() * 200f,
                y = if (diagram.direction == MermaidDirection.LeftRight) order.toFloat() * 120f else rank.toFloat() * 120f,
            )
        }

        return MermaidLayout(
            type = MermaidDiagramType.ClassDiagram,
            direction = diagram.direction,
            nodes = positionedNodes,
            edges = edges,
            classRelationTypes = relationTypes,
        )
    }

    private fun layoutErDiagram(diagram: MermaidDiagram): MermaidLayout {
        val nodes = linkedMapOf<String, MermaidNode>()
        diagram.erEntities.forEach { entity ->
            nodes[entity.name] = MermaidNode(
                id = entity.name,
                label = entity.name,
                shape = MermaidNodeShape.Rectangle,
            )
        }

        val edges = diagram.erRelationships.map { rel ->
            MermaidEdge(
                from = rel.from,
                to = rel.to,
                label = rel.label,
                style = if (rel.kind.name.startsWith("NonIdentifying")) MermaidEdgeStyle.Dotted else MermaidEdgeStyle.Solid,
                arrow = MermaidEdgeArrow.Forward,
            )
        }

        val rankById: Map<String, Int> = calculateRanks(MermaidDiagram(direction = diagram.direction, nodes = nodes, edges = edges))
        val orderByRank = mutableMapOf<Int, Int>()
        val positionedNodes = nodes.values.associate { node ->
            val rank = rankById[node.id] ?: 0
            val order = orderByRank.getOrPut(rank) { 0 }
            orderByRank[rank] = order + 1
            node.id to PositionedMermaidNode(
                node = node,
                rank = rank,
                order = order,
                x = if (diagram.direction == MermaidDirection.LeftRight) rank.toFloat() * 200f else order.toFloat() * 200f,
                y = if (diagram.direction == MermaidDirection.LeftRight) order.toFloat() * 120f else rank.toFloat() * 120f,
            )
        }

        return MermaidLayout(
            type = MermaidDiagramType.ErDiagram,
            direction = diagram.direction,
            nodes = positionedNodes,
            edges = edges,
        )
    }

    private fun layoutStateDiagram(diagram: MermaidDiagram): MermaidLayout {
        val nodes = linkedMapOf<String, MermaidNode>()
        diagram.stateDefinitions.forEach { state ->
            val shape = when {
                state.isStart || state.isEnd -> MermaidNodeShape.Circle
                state.isFork || state.isJoin -> MermaidNodeShape.Rectangle
                else -> MermaidNodeShape.Rounded
            }
            nodes[state.id] = MermaidNode(id = state.id, label = state.label ?: state.id, shape = shape)
        }

        val edges = diagram.stateTransitions.map { transition ->
            MermaidEdge(
                from = transition.from,
                to = transition.to,
                label = transition.event,
                style = MermaidEdgeStyle.Solid,
                arrow = MermaidEdgeArrow.Forward,
            )
        }

        // State diagrams are cyclic; topological layering deadlocks on the loops and
        // collapses the graph into one row. Layer by shortest-path BFS from `start`
        // instead — back-edges hit already-visited targets and are skipped naturally.
        var rankById = calculateStateRanks(nodes.keys, edges)
        // Force the terminal "end" node to the lowest rank so it never sits beside
        // other states — matching mermaid's terminal placement at the bottom.
        if ("end" in nodes) {
            val maxRank = rankById.values.maxOrNull() ?: 0
            rankById = rankById.mapValues { (id, rank) ->
                if (id == "end") maxRank + 1 else rank
            }
        }

        // Center each rank horizontally: all rows share the same midpoint.
        val nodesByRank = nodes.values.groupBy { rankById[it.id] ?: 0 }
        val maxNodesInRank = nodesByRank.values.maxOfOrNull { it.size } ?: 1
        val horizontalStep = StateNodeBoxWidth + StateNodeGap
        val positionedNodes = linkedMapOf<String, PositionedMermaidNode>()
        nodesByRank.forEach { (rank, rankNodes) ->
            val count = rankNodes.size
            // Block left edge so the row is centered relative to the widest row.
            val rowLeft = ((maxNodesInRank - count) * horizontalStep) / 2f
            rankNodes.forEachIndexed { index, node ->
                positionedNodes[node.id] = PositionedMermaidNode(
                    node = node,
                    rank = rank,
                    order = index,
                    x = rowLeft + index * horizontalStep,
                    y = rank.toFloat() * StateRankHeight,
                )
            }
        }

        return MermaidLayout(
            type = MermaidDiagramType.StateDiagram,
            direction = diagram.direction,
            nodes = positionedNodes,
            edges = edges,
            stateEdgeOffsets = calculateStateEdgeOffsets(edges),
        )
    }

    /**
     * Spread edges that share both endpoints into separate arcs. Edges connecting the
     * same pair of states (e.g. `Loading --> Error` and `Error --> Loading`) would
     * otherwise render on top of each other. Within each endpoint-pair group, edges are
     * assigned alternating offsets `0, +k, -k, +2k, -2k...`, so the two directions of a
     * bidirectional link bow in opposite directions and fan out visually.
     */
    private fun calculateStateEdgeOffsets(edges: List<MermaidEdge>): Map<Int, Float> {
        val offsets = mutableMapOf<Int, Float>()
        val groupOrder = mutableMapOf<String, Int>()
        edges.forEachIndexed { index, edge ->
            if (edge.from == edge.to) return@forEachIndexed // self-loop: handled by renderer
            val key = if (edge.from < edge.to) "${edge.from}#${edge.to}" else "${edge.to}#${edge.from}"
            val order = groupOrder.getOrPut(key) { 0 }
            groupOrder[key] = order + 1
            // order 0 => centered (0f); subsequent edges alternate +k / -k with growing magnitude.
            val magnitude = ((order + 1) / 2).toFloat() * StateEdgeOffsetStep
            val sign = if (order % 2 == 1) 1f else -1f
            offsets[index] = if (order == 0) 0f else sign * magnitude
        }
        return offsets
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

    /**
     * Layer cyclic state diagrams by shortest-path BFS from the `start` node.
     *
     * Unlike [calculateRanks] (a topological Kahn sort), this never deadlocks on
     * back-edges: a target that already has a smaller rank is simply skipped, which
     * is exactly the desired behaviour for loops like `Success --> Idle`.
     * Orphan states (unreachable from start) keep rank 0 and are placed at the top.
     */
    private fun calculateStateRanks(
        nodeIds: Set<String>,
        edges: List<MermaidEdge>,
    ): Map<String, Int> {
        val outgoing = nodeIds.associateWith { mutableListOf<String>() }.toMutableMap()
        edges.forEach { edge ->
            outgoing[edge.from]?.add(edge.to)
        }
        val rank = nodeIds.associateWith { 0 }.toMutableMap()
        if ("start" !in nodeIds) return rank

        val queue = ArrayDeque<String>()
        queue.add("start")
        val visited = mutableSetOf("start")
        while (queue.isNotEmpty()) {
            val id = queue.removeFirst()
            val nextRank = rank.getValue(id) + 1
            outgoing.getValue(id).forEach { target ->
                if (target !in visited) {
                    visited.add(target)
                    rank[target] = nextRank
                    queue.add(target)
                }
            }
        }
        return rank
    }

    private const val FlowchartNodeWidth = 132f
    private const val FlowchartNodeHeight = 44f
    private const val SubgraphHorizontalPadding = 24f
    private const val SubgraphTopPadding = 32f
    private const val SubgraphBottomPadding = 20f

    // State diagram layout constants. Box width matches the rendered state node;
    // gap + rank height give the breathing room seen in mermaid.live output.
    private const val StateNodeBoxWidth = 140f
    private const val StateNodeGap = 60f
    private const val StateRankHeight = 110f
    // Horizontal arc step for fanning out multi-edge endpoint pairs.
    private const val StateEdgeOffsetStep = 40f

    /**
     * Pie charts are pure geometry (slices → sweep angles); there is no node/edge layout.
     * Return an empty layout — the renderer computes slice geometry directly from the
     * diagram's `pieSlices`.
     */
    private fun layoutPieDiagram(diagram: MermaidDiagram): MermaidLayout =
        MermaidLayout(
            type = MermaidDiagramType.PieDiagram,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
        )

    /**
     * Gantt charts render as a row-per-task timeline; there is no node/edge graph. The
     * renderer computes each task's horizontal span (from durations and `after` deps) and
     * vertical row, so the layout returns an empty node set.
     */
    private fun layoutGanttDiagram(diagram: MermaidDiagram): MermaidLayout =
        MermaidLayout(
            type = MermaidDiagramType.GanttDiagram,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
        )
}
