package xyz.junerver.compose.palette.mermaid

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
        val sequenceFragments = mutableListOf<MermaidSequenceFragmentBuilder>()
        val sequenceFragmentStack = mutableListOf<MermaidSequenceFragmentBuilder>()
        val sequenceActivations = mutableListOf<MermaidSequenceActivationBuilder>()
        val activeSequenceActivations = mutableMapOf<String, MutableList<MermaidSequenceActivationBuilder>>()
        val sequenceLifecycleEvents = mutableListOf<MermaidSequenceLifecycleEvent>()
        val sequenceRegions = mutableListOf<MermaidSequenceRegionBuilder>()
        val sequenceRegionStack = mutableListOf<MermaidSequenceRegionBuilder>()
        val sequenceLinks = mutableListOf<MermaidSequenceLink>()
        val diagnostics = mutableListOf<MermaidParseDiagnostic>()
        val directives = mutableListOf<MermaidDirective>()
        val flowchartClassDefs = mutableListOf<MermaidFlowchartClassDef>()
        val flowchartClassAssignments = mutableListOf<MermaidFlowchartClassAssignment>()
        val flowchartNodeStyles = mutableListOf<MermaidFlowchartNodeStyle>()
        val flowchartLinkStyles = mutableListOf<MermaidFlowchartLinkStyle>()
        val flowchartClicks = mutableListOf<MermaidFlowchartClick>()
        var title: String? = null
        var sequenceAutonumber = false
        val classDefinitions = mutableListOf<MermaidClassDefinition>()
        val classRelationships = mutableListOf<MermaidClassRelationship>()
        var currentClassId: String? = null
        var currentClassMembers = mutableListOf<MermaidClassMember>()
        var currentClassAnnotation: String? = null
        var currentClassName: String? = null

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

                if (line.equals("classDiagram", ignoreCase = true)) {
                    type = MermaidDiagramType.ClassDiagram
                    direction = MermaidDirection.TopDown
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
                    // Track activations for this message
                    sequenceActivations.filter { it.participant == sequenceMessage.from.id && it.endSequenceIndex == -1 }
                        .forEach { it.edgeIndexes.add(sequenceIndex) }
                    sequenceActivations.filter { it.participant == sequenceMessage.to.id && it.endSequenceIndex == -1 }
                        .forEach { it.edgeIndexes.add(sequenceIndex) }
                    sequenceIndex += 1
                    return@forEachIndexed
                }

                // Sequence title
                if (type == MermaidDiagramType.Sequence) {
                    val sequenceTitle = parseSequenceTitle(line)
                    if (sequenceTitle != null) {
                        title = sequenceTitle
                        return@forEachIndexed
                    }

                    // Autonumber
                    if (line.startsWith("autonumber", ignoreCase = true)) {
                        sequenceAutonumber = true
                        return@forEachIndexed
                    }

                    // Activate/Deactivate
                    val activateMatch = Regex("""^activate\s+([A-Za-z0-9_]+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (activateMatch != null) {
                        val participant = activateMatch.groupValues[1]
                        val activation = MermaidSequenceActivationBuilder(
                            participant = participant,
                            startSequenceIndex = sequenceIndex,
                        )
                        sequenceActivations.add(activation)
                        activeSequenceActivations.getOrPut(participant) { mutableListOf() }.add(activation)
                        sequenceIndex += 1
                        return@forEachIndexed
                    }

                    val deactivateMatch = Regex("""^deactivate\s+([A-Za-z0-9_]+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (deactivateMatch != null) {
                        val participant = deactivateMatch.groupValues[1]
                        val stack = activeSequenceActivations[participant]
                        val active = stack?.removeLastOrNull()
                        if (active != null) {
                            active.close(sequenceIndex)
                        }
                        sequenceIndex += 1
                        return@forEachIndexed
                    }

                    // Create participant
                    val createMatch = Regex("""^create\s+(participant|actor)\s+([A-Za-z0-9_]+)(?:\s+as\s+(.+))?$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (createMatch != null) {
                        val id = createMatch.groupValues[2]
                        val label = createMatch.groupValues[3].trim().ifEmpty { id }
                        if (id !in nodes) {
                            nodes[id] = MermaidNode(id, label, MermaidNodeShape.Rectangle)
                        }
                        sequenceLifecycleEvents.add(MermaidSequenceLifecycleEvent(
                            participant = id,
                            kind = MermaidSequenceLifecycleKind.Create,
                            sequenceIndex = sequenceIndex,
                        ))
                        sequenceIndex += 1
                        return@forEachIndexed
                    }

                    // Destroy
                    val destroyMatch = Regex("""^destroy\s+([A-Za-z0-9_]+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (destroyMatch != null) {
                        val participant = destroyMatch.groupValues[1]
                        sequenceLifecycleEvents.add(MermaidSequenceLifecycleEvent(
                            participant = participant,
                            kind = MermaidSequenceLifecycleKind.Destroy,
                            sequenceIndex = sequenceIndex,
                        ))
                        sequenceIndex += 1
                        return@forEachIndexed
                    }

                    // Fragment start (alt, loop, opt, par, critical, break)
                    val fragmentStartMatch = Regex("""^(alt|loop|opt|par|critical|break)\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (fragmentStartMatch != null) {
                        val kindStr = fragmentStartMatch.groupValues[1].lowercase()
                        val kind = when (kindStr) {
                            "alt" -> MermaidSequenceFragmentKind.Alt
                            "loop" -> MermaidSequenceFragmentKind.Loop
                            "opt" -> MermaidSequenceFragmentKind.Opt
                            "par" -> MermaidSequenceFragmentKind.Par
                            "critical" -> MermaidSequenceFragmentKind.Critical
                            "break" -> MermaidSequenceFragmentKind.Break
                            else -> MermaidSequenceFragmentKind.Alt
                        }
                        val fragment = MermaidSequenceFragmentBuilder(
                            kind = kind,
                            label = fragmentStartMatch.groupValues[2].trim(),
                            startSequenceIndex = sequenceIndex,
                        )
                        fragment.branches.add(MermaidSequenceBranchBuilder(
                            kind = kindStr,
                            label = fragmentStartMatch.groupValues[2].trim(),
                            startSequenceIndex = sequenceIndex,
                        ))
                        sequenceFragments.add(fragment)
                        sequenceFragmentStack.add(fragment)
                        return@forEachIndexed
                    }

                    // Fragment branch (else, and, option)
                    val branchMatch = Regex("""^(else|and|option)\s*(.+)?$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (branchMatch != null && sequenceFragmentStack.isNotEmpty()) {
                        val kind = branchMatch.groupValues[1].lowercase()
                        val label = branchMatch.groupValues[2].trim().ifEmpty { null }
                        val currentFragment = sequenceFragmentStack.last()
                        // Close current branch
                        currentFragment.branches.lastOrNull()?.let { it.endSequenceIndex = sequenceIndex }
                        // Start new branch
                        currentFragment.branches.add(MermaidSequenceBranchBuilder(
                            kind = kind,
                            label = label,
                            startSequenceIndex = sequenceIndex,
                        ))
                        return@forEachIndexed
                    }

                    // End (for fragments and regions)
                    if (line.equals("end", ignoreCase = true)) {
                        if (sequenceFragmentStack.isNotEmpty()) {
                            val fragment = sequenceFragmentStack.removeLast()
                            fragment.branches.lastOrNull()?.let { it.endSequenceIndex = sequenceIndex }
                            fragment.endSequenceIndex = sequenceIndex
                        } else if (sequenceRegionStack.isNotEmpty()) {
                            val region = sequenceRegionStack.removeLast()
                            region.endSequenceIndex = sequenceIndex
                        }
                        return@forEachIndexed
                    }

                    // Rect region
                    val rectMatch = Regex("""^rect\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (rectMatch != null) {
                        val region = MermaidSequenceRegionBuilder(
                            kind = MermaidSequenceRegionKind.Rect,
                            color = rectMatch.groupValues[1].trim(),
                            startSequenceIndex = sequenceIndex,
                        )
                        sequenceRegions.add(region)
                        sequenceRegionStack.add(region)
                        return@forEachIndexed
                    }

                    // Box region
                    val boxMatch = Regex("""^box\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (boxMatch != null) {
                        val region = MermaidSequenceRegionBuilder(
                            kind = MermaidSequenceRegionKind.Box,
                            label = boxMatch.groupValues[1].trim(),
                            startSequenceIndex = sequenceIndex,
                        )
                        sequenceRegions.add(region)
                        sequenceRegionStack.add(region)
                        return@forEachIndexed
                    }

                    // Link
                    val linkMatch = Regex("""^link\s+([A-Za-z0-9_]+)\s*:\s*(.+?)\s*@\s*(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (linkMatch != null) {
                        sequenceLinks.add(MermaidSequenceLink(
                            participant = linkMatch.groupValues[1],
                            url = linkMatch.groupValues[3].trim(),
                            label = linkMatch.groupValues[2].trim().ifEmpty { null },
                        ))
                        return@forEachIndexed
                    }

                    // Links (JSON-like)
                    val linksMatch = Regex("""^links\s+([A-Za-z0-9_]+)\s*:\s*\{(.+)}$""", RegexOption.IGNORE_CASE).matchEntire(line)
                    if (linksMatch != null) {
                        val participant = linksMatch.groupValues[1]
                        // Simple JSON-like parsing for {"label": "url"} format
                        val jsonContent = linksMatch.groupValues[2]
                        Regex(""""([^"]+)"\s*:\s*"([^"]+)"""").findAll(jsonContent).forEach { match ->
                            sequenceLinks.add(MermaidSequenceLink(
                                participant = participant,
                                url = match.groupValues[2],
                                label = match.groupValues[1],
                            ))
                        }
                        return@forEachIndexed
                    }
                }


                // Class Diagram parsing
                if (type == MermaidDiagramType.ClassDiagram) {
                    val isClassStart = line.startsWith("class ", ignoreCase = true)
                    val isAnnotationLine = line.startsWith("<<") && line.endsWith(">>")
                    val isOpenBrace = line == "{"
                    val isCloseBrace = line == "}"

                    // Helper to flush current class block
                    fun flushCurrentClass() {
                        if (currentClassId != null) {
                            classDefinitions.add(MermaidClassDefinition(
                                id = currentClassId!!,
                                label = currentClassName ?: currentClassId!!,
                                annotation = currentClassAnnotation,
                                members = currentClassMembers.toList(),
                            ))
                            currentClassId = null
                            currentClassMembers = mutableListOf()
                            currentClassAnnotation = null
                            currentClassName = null
                        }
                    }

                    // Handle closing brace
                    if (isCloseBrace && currentClassId != null) {
                        flushCurrentClass()
                        return@forEachIndexed
                    }

                    // Handle opening brace (inside class block, already handled by "class X {")
                    if (isOpenBrace && currentClassId != null) {
                        return@forEachIndexed
                    }

                    // Handle annotation on separate line
                    if (isAnnotationLine && currentClassId != null) {
                        currentClassAnnotation = line.removeSurrounding("<<", ">>").trim()
                        return@forEachIndexed
                    }

                    // Handle "class ClassName" or "class ClassName {"
                    if (isClassStart) {
                        // Flush any previous class block
                        flushCurrentClass()

                        val afterClass = line.substringAfter("class ").trim()
                        val braceIndex = afterClass.indexOf('{')
                        val id = if (braceIndex > 0) afterClass.substring(0, braceIndex).trim() else afterClass.trim()
                        if (id.isNotEmpty()) {
                            if (braceIndex >= 0) {
                                // Inline braces: parse members and close immediately
                                val body = afterClass.substring(braceIndex + 1).removeSuffix("}").trim()
                                val members = mutableListOf<MermaidClassMember>()
                                if (body.isNotEmpty()) {
                                    body.lines().forEach { memberLine ->
                                        val member = parseClassMember(memberLine.trim())
                                        if (member != null) members.add(member)
                                    }
                                }
                                if (afterClass.endsWith("}")) {
                                    // Single-line class with braces
                                    classDefinitions.add(MermaidClassDefinition(
                                        id = id,
                                        label = id,
                                        members = members,
                                    ))
                                } else {
                                    // Multi-line class block starts
                                    currentClassId = id
                                    currentClassName = id
                                    currentClassMembers = members
                                    currentClassAnnotation = null
                                }
                            } else {
                                // No braces: class declaration without block
                                // Check if next line is "{" or treat as standalone
                                currentClassId = id
                                currentClassName = id
                                currentClassMembers = mutableListOf()
                                currentClassAnnotation = null
                            }
                            return@forEachIndexed
                        }
                    }

                    // Try to parse as relationship first (before member check)
                    val rel = parseClassRelationship(line)
                    if (rel != null) {
                        flushCurrentClass()
                        classRelationships.add(rel)
                        return@forEachIndexed
                    }

                    // Handle members inside class block
                    if (currentClassId != null) {
                        val member = parseClassMember(line)
                        if (member != null) {
                            currentClassMembers.add(member)
                        }
                        return@forEachIndexed
                    }
                }

                // Flowchart metadata
                if (type == MermaidDiagramType.Flowchart) {
                    val classDef = parseFlowchartClassDef(line)
                    if (classDef != null) {
                        flowchartClassDefs.add(classDef)
                        return@forEachIndexed
                    }

                    val classAssignment = parseFlowchartClassAssignment(line)
                    if (classAssignment != null) {
                        flowchartClassAssignments.add(classAssignment)
                        return@forEachIndexed
                    }

                    val nodeStyle = parseFlowchartNodeStyle(line)
                    if (nodeStyle != null) {
                        flowchartNodeStyles.add(nodeStyle)
                        return@forEachIndexed
                    }

                    val linkStyle = parseFlowchartLinkStyle(line)
                    if (linkStyle != null) {
                        flowchartLinkStyles.add(linkStyle)
                        return@forEachIndexed
                    }

                    val click = parseFlowchartClick(line)
                    if (click != null) {
                        flowchartClicks.add(click)
                        return@forEachIndexed
                    }
                }

                val header = parseHeader(line)
                if (header != null) {
                    direction = header
                    return@forEachIndexed
                }

                // Subgraph direction
                if (line.startsWith("direction ", ignoreCase = true) && currentSubgraph != null) {
                    val dir = when (line.substringAfter("direction ").trim().uppercase()) {
                        "TD", "TB" -> MermaidDirection.TopDown
                        "BT" -> MermaidDirection.BottomTop
                        "LR" -> MermaidDirection.LeftRight
                        "RL" -> MermaidDirection.RightLeft
                        else -> null
                    }
                    if (dir != null) {
                        currentSubgraph!!.direction = dir
                    }
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
            sequenceFragments = sequenceFragments.map { it.toFragment() },
            sequenceActivations = sequenceActivations.map { it.toActivation() },
            sequenceLifecycleEvents = sequenceLifecycleEvents,
            sequenceRegions = sequenceRegions.map { it.toRegion() },
            sequenceLinks = sequenceLinks,
            title = title,
            sequenceAutonumber = sequenceAutonumber,
            diagnostics = diagnostics,
            directives = directives,
            flowchartClassDefs = flowchartClassDefs,
            flowchartClassAssignments = flowchartClassAssignments,
            flowchartNodeStyles = flowchartNodeStyles,
            flowchartLinkStyles = flowchartLinkStyles,
            flowchartClicks = flowchartClicks,
            classDefinitions = classDefinitions,
            classRelationships = classRelationships,
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


    private fun parseSequenceTitle(line: String): String? {
        val match = Regex("^title\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return match.groupValues[1].trim()
    }

    private fun parseFlowchartClassDef(line: String): MermaidFlowchartClassDef? {
        val match = Regex("^classDef\\s+(\\S+)\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartClassDef(
            name = match.groupValues[1],
            declarations = match.groupValues[2].trim(),
            line = 0,
        )
    }

    private fun parseFlowchartClassAssignment(line: String): MermaidFlowchartClassAssignment? {
        val match = Regex("^class\\s+(\\S+)\\s+(\\S+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartClassAssignment(
            nodeIds = match.groupValues[1].split(",").map { it.trim() },
            className = match.groupValues[2],
            line = 0,
        )
    }

    private fun parseFlowchartNodeStyle(line: String): MermaidFlowchartNodeStyle? {
        val match = Regex("^style\\s+(\\S+)\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartNodeStyle(
            nodeId = match.groupValues[1],
            declarations = match.groupValues[2].trim(),
            line = 0,
        )
    }

    private fun parseFlowchartLinkStyle(line: String): MermaidFlowchartLinkStyle? {
        val match = Regex("^linkStyle\\s+([\\d,\\s]+)\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartLinkStyle(
            edgeIndexes = match.groupValues[1].split(",").mapNotNull { it.trim().toIntOrNull() },
            declarations = match.groupValues[2].trim(),
            line = 0,
        )
    }

    private fun parseFlowchartClick(line: String): MermaidFlowchartClick? {
        val match = Regex("""^click\s+(\S+)\s+"([^"]*)"(?:\s+"([^"]*)")?\s*$""", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartClick(
            nodeId = match.groupValues[1],
            href = match.groupValues[2].trim().ifEmpty { null },
            tooltip = match.groupValues.getOrNull(3)?.trim()?.ifEmpty { null },
            line = 0,
        )
    }



    private fun parseClassMember(line: String): MermaidClassMember? {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed == "{" || trimmed == "}") return null
        // Skip annotations
        if (trimmed.startsWith("<<") && trimmed.endsWith(">>")) return null

        var visibility = MermaidClassVisibility.Package
        var isAbstract = false
        var isStatic = false
        var rest = trimmed

        // Parse visibility prefix
        when {
            rest.startsWith("+") -> { visibility = MermaidClassVisibility.Public; rest = rest.substring(1) }
            rest.startsWith("-") -> { visibility = MermaidClassVisibility.Private; rest = rest.substring(1) }
            rest.startsWith("#") -> { visibility = MermaidClassVisibility.Protected; rest = rest.substring(1) }
            rest.startsWith("~") -> { visibility = MermaidClassVisibility.Package; rest = rest.substring(1) }
        }

        // Parse modifiers
        if (rest.startsWith("*")) { isAbstract = true; rest = rest.substring(1) }
        if (rest.startsWith("$")) { isStatic = true; rest = rest.substring(1) }

        rest = rest.trim()
        if (rest.isEmpty()) return null

        // Check if method (has parentheses)
        val parenIndex = rest.indexOf('(')
        if (parenIndex >= 0) {
            val name = rest.substring(0, parenIndex).trim()
            val afterParen = rest.substring(parenIndex)
            val type = if (afterParen.contains(")")) {
                afterParen.substringAfter(")").trim().removePrefix(":").trim().ifEmpty { null }
            } else null
            return MermaidClassMember(
                kind = MermaidClassMemberKind.Method,
                visibility = visibility,
                name = name,
                type = type,
                isAbstract = isAbstract,
                isStatic = isStatic,
            )
        }

        // Otherwise it's a field - Mermaid format is {type} {name} or just {name}
        val parts = rest.split(Regex("\\s+"))
        val name: String
        val type: String?
        if (parts.size >= 2) {
            // Last word is the name, everything before is the type
            name = parts.last().trim()
            type = parts.dropLast(1).joinToString(" ").trim().ifEmpty { null }
        } else {
            name = rest.trim()
            type = null
        }
        return MermaidClassMember(
            kind = MermaidClassMemberKind.Field,
            visibility = visibility,
            name = name,
            type = type,
            isAbstract = isAbstract,
            isStatic = isStatic,
        )
    }

    private fun parseClassRelationship(line: String): MermaidClassRelationship? {
        // Match patterns: A <|-- B, A *-- B, A o-- B, A --> B, A -- B, A ..> B, A ..|> B, A .. B
        // With optional labels: A <|-- B : label
        // With optional cardinality: "1" A --> "n" B

        // Try labeled relationship first: A RELTYPE B : label
        val labeledPattern = Regex(
            """^(?:"([^"]*)"\s+)?(\S+)\s+(<\|--|\*--|o-->|-->|--|\.\.>|\.\.\.\|>|\.\.)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""
        )
        // Simplified: try common patterns
        val patterns = listOf(
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(<\|--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(<\|\.\.)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(\*--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(o--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(-->)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(\.\.>)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
            Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(\.\.)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        )

        for (pattern in patterns) {
            val match = pattern.matchEntire(line) ?: continue
            val from = match.groupValues[1]
            val fromCard = match.groupValues[2].ifEmpty { null }
            val relTypeStr = match.groupValues[3]
            val toCard = match.groupValues[4].ifEmpty { null }
            val to = match.groupValues[5]
            val label = match.groupValues[6].ifEmpty { null }

            val relType = when (relTypeStr) {
                "<|--" -> MermaidClassRelationType.Inheritance
                "<|.." -> MermaidClassRelationType.Realization
                "*--" -> MermaidClassRelationType.Composition
                "o--" -> MermaidClassRelationType.Aggregation
                "-->" -> MermaidClassRelationType.Association
                "--" -> MermaidClassRelationType.Link
                "..>" -> MermaidClassRelationType.Dependency
                ".." -> MermaidClassRelationType.DependencyLink
                else -> continue
            }

            return MermaidClassRelationship(
                from = from,
                to = to,
                type = relType,
                label = label?.trim(),
                fromCardinality = fromCard,
                toCardinality = toCard,
            )
        }
        return null
    }

    private val StandaloneNodeRegex =
        Regex("""^[A-Za-z_][A-Za-z0-9_]*(?:(?:\(\[.+]\))|(?:\(\(.+\)\))|(?:\[.+])|(?:\(.+\))|(?:\{.+}))?$""")
}
