package xyz.junerver.compose.palette.mermaid

object MermaidLayoutEngine {
    fun layout(diagram: MermaidDiagram): MermaidLayout {
        if (diagram.type == MermaidDiagramType.Sequence) return layoutSequence(diagram)
        if (diagram.type == MermaidDiagramType.ClassDiagram) return layoutClassDiagram(diagram)
        if (diagram.type == MermaidDiagramType.ErDiagram) return layoutErDiagram(diagram)
        if (diagram.type == MermaidDiagramType.StateDiagram) return layoutStateDiagram(diagram)
        if (diagram.type == MermaidDiagramType.PieDiagram) return layoutPieDiagram(diagram)
        if (diagram.type == MermaidDiagramType.GanttDiagram) return layoutGanttDiagram(diagram)
        if (diagram.type == MermaidDiagramType.GitGraphDiagram) return layoutGitGraphDiagram(diagram)
        if (diagram.type == MermaidDiagramType.MindmapDiagram) return layoutMindmapDiagram(diagram)
        if (diagram.type == MermaidDiagramType.Timeline) return layoutTimelineDiagram(diagram)
        if (diagram.type == MermaidDiagramType.QuadrantChart) return layoutQuadrantDiagram(diagram)
        if (diagram.type == MermaidDiagramType.XYChart) return layoutXyDiagram(diagram)
        if (diagram.type == MermaidDiagramType.RequirementDiagram) return layoutRequirementDiagram(diagram)
        if (diagram.type == MermaidDiagramType.BlockDiagram) return layoutBlockDiagram(diagram)
        if (diagram.type == MermaidDiagramType.C4Diagram) return layoutC4Diagram(diagram)
        if (diagram.type == MermaidDiagramType.Journey) return layoutEmptyGeometry(diagram, MermaidDiagramType.Journey)
        if (diagram.type == MermaidDiagramType.Packet) return layoutEmptyGeometry(diagram, MermaidDiagramType.Packet)
        if (diagram.type == MermaidDiagramType.Sankey) return layoutSankeyDiagram(diagram)
        if (diagram.type == MermaidDiagramType.Architecture) return layoutArchitectureDiagram(diagram)
        if (diagram.type == MermaidDiagramType.Flowchart) return layoutFlowchartDiagram(diagram)

        // Unreachable for known diagram types (all handled above); kept as a safe fallback
        // mirroring the flowchart path for any future default-routed type.
        return layoutFlowchartDiagram(diagram)
    }

    /**
     * Flowchart layout, dagre-style: topological ranks → barycenter crossing-reduction →
     * per-rank horizontal centering. Each rank is a row (TD/BT) or column (LR/RL); nodes in
     * the same rank share a common midpoint so the drawing reads balanced instead of
     * left-stacked. Coordinates stay on the top-left-corner convention the renderer expects.
     *
     * Subgraphs become bounding boxes around their (possibly shared) members — members can
     * overlap across subgraphs, which matches how mermaid.live renders cross-cluster nodes.
     */
    private fun layoutFlowchartDiagram(diagram: MermaidDiagram): MermaidLayout {
        val rankById = calculateRanks(diagram)
        val nodesByRank: Map<Int, List<String>> = diagram.nodes.keys.groupBy { rankById[it] ?: 0 }

        // Order within each rank via barycenter heuristic to reduce edge crossings.
        val orderById = computeBarycenterOrder(diagram, rankById, nodesByRank)

        // Primary subgraph ownership (first-declaration wins) so clusters render disjoint.
        // Used to inflate the vertical gap between ranks that cross a cluster boundary,
        // giving consecutive subgraph boxes breathing room like mermaid.live.
        val nodePrimarySubgraph = HashMap<String, String>()
        diagram.subgraphs.forEach { sg ->
            sg.nodeIds.forEach { id -> if (id !in nodePrimarySubgraph) nodePrimarySubgraph[id] = sg.id }
        }
        fun subgraphOfRank(rank: Int): String? =
            nodesByRank[rank]?.firstOrNull { it in nodePrimarySubgraph }?.let { nodePrimarySubgraph[it] }

        // Per-rank vertical offset. The base stride is [FlowchartRankHeight]; ranks that
        // straddle a cluster boundary get an extra [SubgraphClusterGap] so adjacent subgraph
        // boxes don't touch.
        val sortedRanks = nodesByRank.keys.sorted()
        val rankYOffset = HashMap<Int, Float>()
        var yCursor = 0f
        var prevSubgraph: String? = null
        sortedRanks.forEachIndexed { _, rank ->
            val sg = subgraphOfRank(rank)
            if (yCursor > 0f && sg != null && sg != prevSubgraph && prevSubgraph != null) {
                yCursor += SubgraphClusterGap
            }
            rankYOffset[rank] = yCursor
            yCursor += FlowchartRankHeight.toFloat()
            prevSubgraph = sg
        }

        // Assign x/y. The widest rank defines the canvas width; every rank is centered on
        // the same horizontal midpoint so parents sit above the middle of their children.
        val maxNodesInRank = nodesByRank.values.maxOfOrNull { it.size } ?: 1
        val step = FlowchartNodeWidth + FlowchartNodeGap
        val spanCenter = (maxNodesInRank * step) / 2f

        val orderByRank = mutableMapOf<Int, Int>()
        val positionedNodes = linkedMapOf<String, PositionedMermaidNode>()
        diagram.nodes.values.forEach { node ->
            val rank = rankById[node.id] ?: 0
            val orderInRank = orderByRank.getOrPut(rank) { 0 }
            orderByRank[rank] = orderInRank + 1
            val rankSize = nodesByRank[rank]?.size ?: 1
            // Center this rank's nodes around `spanCenter`: left edge so the block is balanced.
            val rankLeft = spanCenter - (rankSize * step) / 2f
            val along = rankLeft + orderById.getValue(node.id) * step // horizontal position
            val across = rankYOffset[rank] ?: 0f                   // vertical position

            val (x, y) = when (diagram.direction) {
                MermaidDirection.TopDown, MermaidDirection.BottomTop -> along to across
                MermaidDirection.LeftRight, MermaidDirection.RightLeft -> across to along
            }
            positionedNodes[node.id] =
                PositionedMermaidNode(node = node, rank = rank, order = orderInRank, x = x, y = y)
        }

        val positioned =
            if (diagram.subgraphs.isEmpty()) {
                positionedNodes
            } else {
                positionedNodes.mapValues { (_, node) ->
                    node.copy(x = node.x + SubgraphHorizontalPadding, y = node.y + SubgraphTopPadding)
                }
            }
        val subgraphs = layoutSubgraphs(diagram.subgraphs, positioned)

        return MermaidLayout(
            type = MermaidDiagramType.Flowchart,
            direction = diagram.direction,
            nodes = positioned,
            edges = diagram.edges,
            notes = diagram.notes,
            subgraphs = subgraphs,
            flowchartClassDefs = diagram.flowchartClassDefs,
            flowchartClassAssignments = diagram.flowchartClassAssignments,
            flowchartNodeStyles = diagram.flowchartNodeStyles,
            flowchartLinkStyles = diagram.flowchartLinkStyles,
            // Fan out edges that share an endpoint pair so they don't overlap into one line.
            stateEdgeOffsets = calculateStateEdgeOffsets(diagram.edges),
        )
    }

    /**
     * Assign each node a within-rank index using the barycenter heuristic: a node's ideal
     * position is the median of its neighbours' positions in the adjacent rank. Two sweeps
     * (top-down then bottom-up) smooth the ordering. This is a simplified dagre crossing
     * reducer — not optimal, but it centers fan-out/fan-in (e.g. one parent → two children)
     * which is the dominant visual case for flowcharts.
     */
    private fun computeBarycenterOrder(
        diagram: MermaidDiagram,
        rankById: Map<String, Int>,
        nodesByRank: Map<Int, List<String>>,
    ): Map<String, Int> {
        val maxRank = nodesByRank.keys.maxOrNull() ?: 0
        val outgoing = diagram.edges.groupBy { edge -> edge.from }.mapValues { it.value.map { e -> e.to } }
        val incoming = diagram.edges.groupBy { edge -> edge.to }.mapValues { it.value.map { e -> e.from } }

        // order[rank] = list of node ids in display order; seeded by discovery order, ranks
        // walked low→high so the sweep loops below can index consecutive ranks.
        val sortedRanks = nodesByRank.keys.sorted()
        val order = LinkedHashMap<Int, MutableList<String>>()
        sortedRanks.forEach { rank -> order[rank] = nodesByRank.getValue(rank).toMutableList() }
        val positionById = HashMap<String, Int>()

        fun refreshPositions() {
            order.values.forEach { ids -> ids.forEachIndexed { idx, id -> positionById[id] = idx } }
        }

        fun barycenter(id: String, neighbours: List<String>): Float {
            val positions = neighbours.mapNotNull { n -> positionById[n] }.sorted()
            if (positions.isEmpty()) return positionById[id]?.toFloat() ?: 0f
            return positions[positions.size / 2].toFloat()
        }

        // Down sweep: each rank ordered by median position of its upper neighbours.
        refreshPositions()
        for (rank in 1..maxRank) {
            val ids = order[rank] ?: continue
            val sorted = ids.sortedBy { node -> barycenter(node, incoming[node].orEmpty()) }
            ids.clear(); ids.addAll(sorted)
        }
        // Up sweep: smooth using lower neighbours.
        refreshPositions()
        for (rank in maxRank downTo 0) {
            val ids = order[rank] ?: continue
            val sorted = ids.sortedBy { node -> barycenter(node, outgoing[node].orEmpty()) }
            ids.clear(); ids.addAll(sorted)
        }

        refreshPositions()
        return positionById
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
                x = if (diagram.direction == MermaidDirection.LeftRight) rank.toFloat() * 240f else order.toFloat() * 240f,
                y = if (diagram.direction == MermaidDirection.LeftRight) order.toFloat() * 180f else rank.toFloat() * 180f,
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

    /**
     * Compute each subgraph's bounding box from its **primary** members only. Because a node
     * can be referenced by several subgraphs (a cross-cluster edge pulls both endpoints into
     * the current subgraph's `nodeIds`), naively bounding all members makes the boxes overlap
     * and their outlines cut through shared nodes. We instead assign every node to the
     * *first* subgraph (in declaration order) that claims it, so each box wraps a disjoint set
     * — matching how mermaid.live clusters nodes into a single owning subgraph.
     */
    private fun layoutSubgraphs(
        subgraphs: List<MermaidSubgraph>,
        nodes: Map<String, PositionedMermaidNode>,
    ): List<PositionedMermaidSubgraph> {
        // First-declaration wins: walk subgraphs in order, claim each node once.
        val owner = HashMap<String, String>()
        subgraphs.forEach { sg ->
            sg.nodeIds.forEach { id -> if (id !in owner) owner[id] = sg.id }
        }

        return subgraphs.mapNotNull { subgraph ->
            val members = subgraph.nodeIds
                .filter { id -> owner[id] == subgraph.id }
                .mapNotNull(nodes::get)
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
    // Horizontal gap between sibling nodes in the same rank, and the vertical stride between
    // ranks. Together with [FlowchartNodeWidth] they set the per-rank step and row height.
    private const val FlowchartNodeGap = 48f
    private const val FlowchartRankHeight = 96
    // Extra vertical space inserted between two ranks whose nodes belong to different
    // subgraphs, so consecutive cluster boxes have a visible gap (mermaid.live spacing).
    private const val SubgraphClusterGap = 40f
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

    /**
     * GitGraph renders as a branch-per-row layout (branches stacked vertically, commits along
     * the horizontal timeline, merge edges connecting them). The renderer computes positions
     * directly from commits/branches, so the layout returns an empty node set.
     */
    private fun layoutGitGraphDiagram(diagram: MermaidDiagram): MermaidLayout =
        MermaidLayout(
            type = MermaidDiagramType.GitGraphDiagram,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
        )

    /**
     * Timeline is pure geometry (a left→right time axis with periods and stacked events). The
     * renderer positions everything from the diagram's `timelinePeriods`, so the layout returns
     * an empty node set.
     */
    private fun layoutTimelineDiagram(diagram: MermaidDiagram): MermaidLayout =
        MermaidLayout(
            type = MermaidDiagramType.Timeline,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
        )

    /**
     * Quadrant chart is pure geometry (a square split into four quadrants with plotted points).
     * The renderer maps each point's normalized [0,1] coords onto the canvas, so no layout work.
     */
    private fun layoutQuadrantDiagram(diagram: MermaidDiagram): MermaidLayout =
        MermaidLayout(
            type = MermaidDiagramType.QuadrantChart,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
        )

    /**
     * XYChart is pure geometry (a coordinate plane with bar/line series). The renderer derives
     * the y-axis range from the series (or uses `xyYAxisRange`) and plots bars/lines, so no
     * layout work.
     */
    private fun layoutXyDiagram(diagram: MermaidDiagram): MermaidLayout =
        MermaidLayout(
            type = MermaidDiagramType.XYChart,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
        )

    /**
     * Requirement diagram layout. Requirements/elements form a DAG of typed relationships, so
     * this mirrors [layoutClassDiagram]: a Kahn topological sort assigns ranks, nodes are placed
     * in a rank×order grid, and relationships become [MermaidEdge]s with the kind carried in
     * [MermaidLayout.requirementRelationTypes] for the renderer's markers.
     */
    private fun layoutRequirementDiagram(diagram: MermaidDiagram): MermaidLayout {
        val nodes = linkedMapOf<String, MermaidNode>()
        diagram.requirementBoxes.forEach { box ->
            nodes[box.id] = MermaidNode(id = box.id, label = box.label, shape = MermaidNodeShape.Rounded)
        }
        // Ensure any referenced (but undeclared) endpoint still gets a node.
        diagram.requirementRelationships.forEach { rel ->
            if (rel.from !in nodes) nodes[rel.from] = MermaidNode(rel.from, rel.from, MermaidNodeShape.Rounded)
            if (rel.to !in nodes) nodes[rel.to] = MermaidNode(rel.to, rel.to, MermaidNodeShape.Rounded)
        }

        val relationTypes = mutableMapOf<Int, RequirementRelationKind>()
        val edges = diagram.requirementRelationships.mapIndexed { index, rel ->
            relationTypes[index] = rel.kind
            MermaidEdge(
                from = rel.from, to = rel.to,
                label = rel.kind.name.lowercase(),
                style = MermaidEdgeStyle.Solid, arrow = MermaidEdgeArrow.None, // markers drawn from kind
            )
        }

        val rankById = calculateRanks(
            MermaidDiagram(direction = diagram.direction, nodes = nodes, edges = edges, type = MermaidDiagramType.RequirementDiagram),
        )
        val orderByRank = mutableMapOf<Int, Int>()
        val positioned = nodes.values.associate { node ->
            val rank = rankById[node.id] ?: 0
            val order = orderByRank.getOrPut(rank) { 0 }; orderByRank[rank] = order + 1
            val lr = diagram.direction == MermaidDirection.LeftRight || diagram.direction == MermaidDirection.RightLeft
            node.id to PositionedMermaidNode(
                node = node, rank = rank, order = order,
                x = if (lr) rank * 260f else order * 220f,
                y = if (lr) order * 140f else rank * 160f,
            )
        }

        return MermaidLayout(
            type = MermaidDiagramType.RequirementDiagram,
            direction = diagram.direction,
            nodes = positioned,
            edges = edges,
            requirementRelationTypes = relationTypes,
        )
    }

    /**
     * Block diagram layout. Nodes occupy a grid of `columns` per scope; each node takes
     * [BlockNode.columnSpan] cells. The top-level grid places nodes in row-major order; nested
     * containers are placed at their grid slot and their children offset within (relative to the
     * container's top-left). Edges connect positioned nodes.
     */
    private fun layoutBlockDiagram(diagram: MermaidDiagram): MermaidLayout {
        val cellW = 160f
        val cellH = 90f
        val gap = 12f
        val columns = diagram.blockContainers // unused here but kept for clarity
        val topColumns = (diagram.blockNodes.maxOfOrNull { it.columnSpan } ?: 1).coerceAtLeast(1)
            .let { maxOf(it, 1) }

        val positioned = linkedMapOf<String, PositionedMermaidNode>()
        var col = 0
        var row = 0
        diagram.blockNodes.forEach { node ->
            val span = node.columnSpan.coerceAtLeast(1)
            // Wrap if the node would overflow the current row.
            if (col + span > topColumns && col > 0) { col = 0; row++ }
            val x = col * (cellW + gap)
            val y = row * (cellH + gap)
            positioned[node.id] = PositionedMermaidNode(
                node = MermaidNode(id = node.id, label = node.label, shape = node.shape),
                rank = row, order = col, x = x, y = y,
            )
            col += span
            if (col >= topColumns) { col = 0; row++ }
        }

        val edges = diagram.blockEdges.map { e ->
            MermaidEdge(from = e.from, to = e.to, label = e.label, style = e.style, arrow = e.arrow)
        }

        return MermaidLayout(
            type = MermaidDiagramType.BlockDiagram,
            direction = diagram.direction,
            nodes = positioned,
            edges = edges,
        )
    }

    /**
     * C4 diagram layout. Elements are placed in declaration order, `c4ShapesPerRow` per row
     * (4 by default). Boundaries render as dashed containers around their declared children —
     * here boundaries are positioned as invisible anchors; the renderer draws the boundary box
     * spanning its children's bounds. Relationships become plain [MermaidEdge]s.
     */
    private fun layoutC4Diagram(diagram: MermaidDiagram): MermaidLayout {
        val cellW = 200f
        val cellH = 110f
        val gap = 16f
        val perRow = 4

        val positioned = linkedMapOf<String, PositionedMermaidNode>()
        diagram.c4Elements.forEachIndexed { index, element ->
            val col = index % perRow
            val row = index / perRow
            val shape = when {
                element.kind.name.startsWith("Person") -> MermaidNodeShape.Circle
                element.kind.name.contains("Db") -> MermaidNodeShape.Database
                else -> MermaidNodeShape.Rounded
            }
            positioned[element.alias] = PositionedMermaidNode(
                node = MermaidNode(id = element.alias, label = element.label, shape = shape),
                rank = row, order = col,
                x = col * (cellW + gap), y = row * (cellH + gap),
            )
        }
        // Place boundaries at their first child's position (renderer expands the box to fit children).
        diagram.c4Boundaries.forEach { boundary ->
            val firstChild = boundary.childAliases.firstNotNullOfOrNull { positioned[it] }
            if (firstChild != null && boundary.alias !in positioned) {
                positioned[boundary.alias] = PositionedMermaidNode(
                    node = MermaidNode(id = boundary.alias, label = boundary.label, shape = MermaidNodeShape.Rounded),
                    rank = firstChild.rank, order = firstChild.order,
                    x = firstChild.x - gap, y = firstChild.y - gap,
                )
            }
        }

        val edges = diagram.c4Relationships.map { rel ->
            MermaidEdge(from = rel.from, to = rel.to, label = rel.label, style = MermaidEdgeStyle.Solid, arrow = MermaidEdgeArrow.Forward)
        }

        return MermaidLayout(
            type = MermaidDiagramType.C4Diagram,
            direction = diagram.direction,
            nodes = positioned,
            edges = edges,
        )
    }

    /**
     * Journey / Packet are pure-geometry diagrams whose renderer computes all positions
     * from the parsed diagram. The layout returns an empty node/edge set (the Pie/Gantt pattern).
     */
    private fun layoutEmptyGeometry(diagram: MermaidDiagram, type: MermaidDiagramType): MermaidLayout =
        MermaidLayout(type = type, direction = diagram.direction, nodes = emptyMap(), edges = emptyList())

    private fun layoutSankeyDiagram(diagram: MermaidDiagram): MermaidLayout {
        val sankeyNodes = calculateSankeyNodeLayouts(diagram.sankeyFlows)
        return MermaidLayout(
            type = MermaidDiagramType.Sankey,
            direction = diagram.direction,
            nodes = emptyMap(),
            edges = emptyList(),
            sankeyNodes = sankeyNodes,
        )
    }

    internal fun calculateSankeyNodeLayouts(flows: List<SankeyFlow>): List<SankeyNodeLayout> {
        val nodeOrder = buildList {
            flows.forEach { flow ->
                if (flow.source !in this) add(flow.source)
                if (flow.target !in this) add(flow.target)
            }
        }
        if (nodeOrder.isEmpty()) return emptyList()
        val incoming = flows.groupBy { it.target }
        val outgoing = flows.groupBy { it.source }
        val levels = linkedMapOf<String, Int>()
        nodeOrder.filter { it !in incoming }.forEach { levels[it] = 0 }
        if (levels.isEmpty()) levels[nodeOrder.first()] = 0
        repeat(nodeOrder.size) {
            var changed = false
            flows.forEach { flow ->
                val sourceLevel = levels[flow.source] ?: return@forEach
                val nextLevel = sourceLevel + 1
                if ((levels[flow.target] ?: -1) < nextLevel) {
                    levels[flow.target] = nextLevel
                    changed = true
                }
            }
            if (!changed) return@repeat
        }
        nodeOrder.forEach { if (it !in levels) levels[it] = 0 }
        val maxLevel = levels.values.maxOrNull()?.coerceAtLeast(1) ?: 1
        nodeOrder.filter { it !in outgoing }.forEach { levels[it] = maxLevel }
        val nodeValues = nodeOrder.associateWith { node ->
            kotlin.math.max(
                incoming[node]?.sumOf { it.value.toDouble() }?.toFloat() ?: 0f,
                outgoing[node]?.sumOf { it.value.toDouble() }?.toFloat() ?: 0f,
            ).coerceAtLeast(1f)
        }
        val firstSeen = nodeOrder.withIndex().associate { it.value to it.index }
        val sinkOrder = nodeOrder
            .filter { it !in outgoing }
            .withIndex()
            .associate { it.value to it.index.toFloat() }
        fun terminalAverage(node: String, visiting: Set<String> = emptySet()): Double {
            if (node in visiting) return firstSeen.getValue(node).toDouble()
            val next = outgoing[node].orEmpty()
            if (next.isEmpty()) return sinkOrder[node]?.toDouble() ?: firstSeen.getValue(node).toDouble()
            val weighted = next.sumOf { flow -> terminalAverage(flow.target, visiting + node) * flow.value.toDouble() }
            val total = next.sumOf { it.value.toDouble() }.coerceAtLeast(1.0)
            return weighted / total
        }
        val rawLevelNodes = nodeOrder.groupBy { levels.getValue(it) }
        val nodesByLevel = rawLevelNodes.mapValues { (level, names) ->
            when {
                level == maxLevel -> names.sortedBy { sinkOrder[it] ?: firstSeen.getValue(it).toFloat() }
                level == 0 -> names.sortedWith(
                    compareBy<String> { terminalAverage(it) }.thenBy { firstSeen.getValue(it) },
                )
                else -> names.sortedWith(
                    compareBy<String> {
                        incoming[it]
                            ?.map { flow -> terminalAverage(flow.source) }
                            ?.average()
                            ?.takeIf { value -> !value.isNaN() }
                            ?: firstSeen.getValue(it).toDouble()
                    }.thenBy { firstSeen.getValue(it) },
                )
            }
        }

        val levelTotals = nodesByLevel.mapValues { (_, names) ->
            names.sumOf { nodeValues.getValue(it).toDouble() }.toFloat()
        }
        val maxLevelTotal = levelTotals.values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        val maxNodesInLevel = nodesByLevel.values.maxOfOrNull { it.size } ?: 1
        val normalizedGap = 0.03f
        val reservedGap = normalizedGap * (maxNodesInLevel - 1).coerceAtLeast(0)
        val valueScale = (1f - reservedGap).coerceAtLeast(0.01f) / maxLevelTotal

        val result = mutableListOf<SankeyNodeLayout>()
        nodesByLevel.forEach { (level, names) ->
            val nodeHeights = names.associateWith { name ->
                (nodeValues.getValue(name) * valueScale).coerceAtLeast(0.02f)
            }
            val usedHeight = nodeHeights.values.sum() + normalizedGap * (names.size - 1).coerceAtLeast(0)
            val slack = (1f - usedHeight).coerceAtLeast(0f)
            val outerGap = if (names.size == 1) slack / 2f else 0f
            val extraInnerGap = if (names.size > 1) slack / (names.size - 1) else 0f
            var cursor = outerGap
            names.forEachIndexed { index, name ->
                val nodeHeight = nodeHeights.getValue(name)
                result.add(
                    SankeyNodeLayout(
                        name = name,
                        level = level,
                        maxLevel = maxLevel,
                        value = nodeValues.getValue(name),
                        order = index,
                        yWeight = cursor,
                        heightWeight = nodeHeight,
                    ),
                )
                cursor += nodeHeight + normalizedGap + extraInnerGap
            }
        }
        return result
    }

    private fun layoutArchitectureDiagram(diagram: MermaidDiagram): MermaidLayout {
        val stepX = 315f
        val stepY = 225f
        val serviceNodes = diagram.archNodes.filter { it.kind != ArchNodeKind.Group }
        if (serviceNodes.isEmpty()) {
            return MermaidLayout(
                type = MermaidDiagramType.Architecture,
                direction = diagram.direction,
                nodes = emptyMap(),
                edges = emptyList(),
                archNodes = diagram.archNodes,
                archEdges = diagram.archEdges,
            )
        }

        val grid = linkedMapOf<String, Pair<Int, Int>>()
        val incomingCounts = diagram.archEdges.groupingBy { it.to }.eachCount()
        val first = serviceNodes
            .filter { it.kind != ArchNodeKind.Junction }
            .minWithOrNull(compareBy<ArchNode> { incomingCounts[it.id] ?: 0 }.thenBy { serviceNodes.indexOf(it) })
            ?: serviceNodes.first()
        grid[first.id] = 0 to 0

        fun delta(fromDir: ArchDir, toDir: ArchDir): Pair<Int, Int> =
            when {
                fromDir == ArchDir.R || toDir == ArchDir.L -> 1 to 0
                fromDir == ArchDir.L || toDir == ArchDir.R -> -1 to 0
                fromDir == ArchDir.B || toDir == ArchDir.T -> 0 to 1
                fromDir == ArchDir.T || toDir == ArchDir.B -> 0 to -1
                else -> 1 to 0
            }

        repeat(serviceNodes.size) {
            var changed = false
            diagram.archEdges.forEach { edge ->
                val from = grid[edge.from]
                val to = grid[edge.to]
                val (dx, dy) = delta(edge.fromDir, edge.toDir)
                when {
                    from != null && to == null -> {
                        grid[edge.to] = from.first + dx to from.second + dy
                        changed = true
                    }
                    from == null && to != null -> {
                        grid[edge.from] = to.first - dx to to.second - dy
                        changed = true
                    }
                }
            }
            if (!changed) return@repeat
        }

        var fallbackIndex = 0
        serviceNodes.forEach { node ->
            if (node.id !in grid) {
                fallbackIndex += 1
                grid[node.id] = fallbackIndex to 0
            }
        }

        val minCol = grid.values.minOf { it.first }
        val minRow = grid.values.minOf { it.second }
        val positioned = linkedMapOf<String, PositionedMermaidNode>()
        serviceNodes.forEach { node ->
            val (col, row) = grid.getValue(node.id)
            positioned[node.id] = PositionedMermaidNode(
                node = MermaidNode(id = node.id, label = node.title ?: node.id, shape = MermaidNodeShape.Rounded),
                rank = row - minRow,
                order = col - minCol,
                x = (col - minCol) * stepX,
                y = (row - minRow) * stepY,
            )
        }

        return MermaidLayout(
            type = MermaidDiagramType.Architecture,
            direction = diagram.direction,
            nodes = positioned,
            edges = diagram.archEdges.map { e ->
                MermaidEdge(from = e.from, to = e.to, label = e.label, style = MermaidEdgeStyle.Solid, arrow = MermaidEdgeArrow.Forward)
            },
            archNodes = diagram.archNodes,
            archEdges = diagram.archEdges,
        )
    }

    /**
     * Mindmap layout. The mindmap is an indentation-defined tree; layout is a classic
     * tidy-tree pass: leaves are laid out along a vertical cursor, each parent centers on
     * the midpoint of its children, and depth drives the horizontal column. For LeftRight
     * the tree grows left→right; for RightLeft the x axis is mirrored at the end.
     *
     * Edges are synthesized as parent→child links so the renderer can draw connectors.
     */
    private fun layoutMindmapDiagram(diagram: MermaidDiagram): MermaidLayout {
        val nodes = diagram.mindmapNodes
        if (nodes.isEmpty()) {
            return MermaidLayout(
                type = MermaidDiagramType.MindmapDiagram,
                direction = diagram.direction,
                nodes = emptyMap(),
                edges = emptyList(),
            )
        }

        val byId = nodes.associateBy { it.id }
        val childrenOf: Map<String, List<MindmapNode>> = nodes
            .filter { it.parentId != null }
            .groupBy { it.parentId!! }
        val roots = nodes.filter { it.parentId == null }

        val yCenter = HashMap<String, Float>()
        var cursor = 0f

        // Place leaves at incrementing cursor positions; parents take the mean of their
        // children's centers. Roots are laid out in order, each contributing its subtree
        // to the running cursor.
        fun layout(node: MindmapNode): Float {
            val kids = childrenOf[node.id].orEmpty()
            val center =
                if (kids.isEmpty()) {
                    val c = cursor + MindmapNodeHeight / 2f
                    cursor += MindmapNodeHeight + MindmapLeafGap
                    c
                } else {
                    val centers = kids.map(::layout)
                    centers.first() / 2f + centers.last() / 2f
                }
            yCenter[node.id] = center
            return center
        }

        roots.forEach { layout(it); cursor += MindmapSubtreeGap }

        val positioned = linkedMapOf<String, PositionedMermaidNode>()
        val edges = mutableListOf<MermaidEdge>()
        nodes.forEach { node ->
            val x = MindmapLeftPadding + node.depth * MindmapColumnWidth
            val y = yCenter[node.id] ?: 0f
            val shape = when (node.shape) {
                MindmapNodeShape.Circle, MindmapNodeShape.Bang -> MermaidNodeShape.Circle
                MindmapNodeShape.Square -> MermaidNodeShape.Rectangle
                MindmapNodeShape.Hexagon -> MermaidNodeShape.Hexagon
                MindmapNodeShape.Cloud, MindmapNodeShape.Rounded, MindmapNodeShape.Default -> MermaidNodeShape.Rounded
            }
            positioned[node.id] =
                PositionedMermaidNode(
                    node = MermaidNode(id = node.id, label = node.label, shape = shape),
                    rank = node.depth,
                    order = 0,
                    x = x,
                    y = y,
                )
            val pid = node.parentId
            if (pid != null && byId[pid] != null) {
                edges +=
                    MermaidEdge(
                        from = pid,
                        to = node.id,
                        style = MermaidEdgeStyle.Solid,
                        arrow = MermaidEdgeArrow.None,
                    )
            }
        }

        // RightLeft: mirror x so the root sits on the right edge.
        val finalNodes =
            if (diagram.direction == MermaidDirection.RightLeft) {
                val maxX = positioned.values.maxOf { it.x }
                positioned.mapValues { (_, v) -> v.copy(x = maxX - v.x + MindmapLeftPadding) }
            } else {
                positioned
            }

        return MermaidLayout(
            type = MermaidDiagramType.MindmapDiagram,
            direction = diagram.direction,
            nodes = finalNodes,
            edges = edges,
        )
    }

    // Mindmap layout treats a node's (x, y) as its center. Leaves are stacked along a
    // vertical cursor; parents center on the midpoint of their first/last child.
    private const val MindmapNodeHeight = 44f
    private const val MindmapLeftPadding = 80f
    private const val MindmapLeafGap = 16f
    private const val MindmapSubtreeGap = 24f
    private const val MindmapColumnWidth = 180f
}

/**
 * Anchors for one class-diagram edge: the start point (child's top edge), the end point
 * (parent's bottom edge, already offset by fan-out when several children share a parent),
 * and the UML marker direction vectors at each end.
 *
 * Coordinate convention matches the layout engine + renderer: node (x, y) is the TOP-LEFT
 * corner, x grows rightward, y grows downward. `u*` is the unit vector *along* the marker
 * (pointing from the tip into the marker body); `p*` is the perpendicular (across the base).
 * `drawClassMarker` consumes them as `back = tip - u*size`, `left/right = base ± p*half`.
 */
data class ClassEdgeAnchors(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    /** Marker direction at the child-side anchor (unused for the standard relations). */
    val startUx: Float,
    val startUy: Float,
    val startPx: Float,
    val startPy: Float,
    /** Marker direction at the parent-side anchor — where every UML marker lives. */
    val endUx: Float,
    val endUy: Float,
    val endPx: Float,
    val endPy: Float,
)

/**
 * Which end of the edge carries the UML marker, per the mermaid/UML convention.
 *
 *  - `Parent` (the `from`/whole node, on top): inheritance triangle, realization triangle,
 *    composition diamond, aggregation diamond. The marker points DOWN toward the child.
 *  - `Child` (the `to` node, on the bottom): association arrow, dependency arrow. The arrow
 *    head sits on the target and points DOWN into it.
 */
enum class ClassMarkerSide { Parent, Child }

/**
 * Pure edge-anchor geometry for class diagrams. Extracted from the renderer so the UML
 * contract is unit-testable instead of being duplicated inline.
 *
 * Geometry rules (see mermaid.live):
 *  - `from` (parent / whole) is always ABOVE `to` (child / part) in the TD layout, because
 *    `calculateRanks` assigns the lower rank to `from`. The connector goes child-top → parent-bottom.
 *  - Both anchors use the SAME downward body vector `u=(0,-1)` so `drawClassMarker`'s
 *    `back = tip - u*size` lands below the tip (in the gap, or inside the child for arrows).
 *  - Fan-out: when `fanCount` siblings attach to one parent, their parent anchors spread evenly
 *    across `[0, nodeWidth]` at offsets `(fanIndex+1)/(fanCount+1)`, so two siblings anchor at
 *    1/3 and 2/3 instead of both stacking on the center. The child-side anchor stays centered.
 */
object ClassEdgeGeometry {

    /** Which end carries the marker for a given relation type. */
    fun markerSide(rel: MermaidClassRelationType): ClassMarkerSide = when (rel) {
        MermaidClassRelationType.Inheritance,
        MermaidClassRelationType.Realization,
        MermaidClassRelationType.Composition,
        MermaidClassRelationType.Aggregation,
        -> ClassMarkerSide.Parent

        // Association `A --> B` and dependency `A ..> B`: arrowhead on the target (child).
        MermaidClassRelationType.Association,
        MermaidClassRelationType.Dependency,
        -> ClassMarkerSide.Child

        // Plain links carry no marker.
        MermaidClassRelationType.Link,
        MermaidClassRelationType.DependencyLink,
        -> ClassMarkerSide.Parent
    }

    fun anchorsFor(
        child: PositionedMermaidNode,
        parent: PositionedMermaidNode,
        childWidth: Float,
        childHeight: Float,
        parentHeight: Float,
        fanIndex: Int,
        fanCount: Int,
        nodeWidth: Float,
        relationType: MermaidClassRelationType,
    ): ClassEdgeAnchors {
        // Start anchor: child's TOP edge, horizontally centered on the child.
        val startX = child.x + childWidth / 2f
        val startY = child.y

        // End anchor: parent's BOTTOM edge, fanned out across the parent's width when siblings exist.
        val fanFraction = if (fanCount <= 1) 0.5f else (fanIndex + 1f) / (fanCount + 1f)
        val endX = parent.x + nodeWidth * fanFraction
        val endY = parent.y + parentHeight

        // Perpendicular vector across the marker base: horizontal, so p=(1,0).
        val px = 1f
        val py = 0f

        // Body vector always points DOWNWARD (toward the child / into the gap):
        // `back = tip - (0,-1)*size = tip + (0,size)` lands below the tip.
        val markerUx = 0f
        val markerUy = -1f

        return when (markerSide(relationType)) {
            ClassMarkerSide.Parent -> ClassEdgeAnchors(
                startX = startX, startY = startY,
                endX = endX, endY = endY,
                startUx = 0f, startUy = 0f, startPx = px, startPy = py,
                endUx = markerUx, endUy = markerUy, endPx = px, endPy = py,
            )
            ClassMarkerSide.Child -> ClassEdgeAnchors(
                startX = startX, startY = startY,
                endX = endX, endY = endY,
                startUx = markerUx, startUy = markerUy, startPx = px, startPy = py,
                endUx = 0f, endUy = 0f, endPx = px, endPy = py,
            )
        }
    }
}
