package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidEdge
import xyz.junerver.compose.palette.mermaid.MermaidEdgeArrow
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidNode
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.MermaidNote
import xyz.junerver.compose.palette.mermaid.MermaidNotePosition
import xyz.junerver.compose.palette.mermaid.MermaidSequenceActivationBuilder
import xyz.junerver.compose.palette.mermaid.MermaidSequenceBranchBuilder
import xyz.junerver.compose.palette.mermaid.MermaidSequenceFragmentBuilder
import xyz.junerver.compose.palette.mermaid.MermaidSequenceFragmentKind
import xyz.junerver.compose.palette.mermaid.MermaidSequenceLifecycleEvent
import xyz.junerver.compose.palette.mermaid.MermaidSequenceLifecycleKind
import xyz.junerver.compose.palette.mermaid.MermaidSequenceLink
import xyz.junerver.compose.palette.mermaid.MermaidSequenceRegionBuilder
import xyz.junerver.compose.palette.mermaid.MermaidSequenceRegionKind
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Sequence diagram parser. Extracted verbatim from the old monolithic
 * `MermaidParser.parse()` sequence branch: participants, messages (with solid/dashed
 * and arrow variants), notes, activation/deactivation, fragments (alt/loop/opt/par/...),
 * rect/box regions, create/destroy lifecycle, links, title and autonumber.
 *
 * The fragment/region/activation stacks are owned by this parser instance, isolated
 * from every other diagram type's state.
 */
@Suppress("ComplexMethod", "LongMethod")
internal object SequenceParser : MermaidDiagramParser {
    override val keyword: String = "sequenceDiagram"
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight

    override fun parse(lines: List<String>): ParseResult.Sequence {
        val nodes = linkedMapOf<String, MermaidNode>()
        val edges = mutableListOf<MermaidEdge>()
        val notes = mutableListOf<MermaidNote>()
        val fragments = mutableListOf<MermaidSequenceFragmentBuilder>()
        val fragmentStack = mutableListOf<MermaidSequenceFragmentBuilder>()
        val activations = mutableListOf<MermaidSequenceActivationBuilder>()
        val activeActivations = mutableMapOf<String, MutableList<MermaidSequenceActivationBuilder>>()
        val regions = mutableListOf<MermaidSequenceRegionBuilder>()
        val regionStack = mutableListOf<MermaidSequenceRegionBuilder>()
        val lifecycleEvents = mutableListOf<MermaidSequenceLifecycleEvent>()
        val links = mutableListOf<MermaidSequenceLink>()
        var title: String? = null
        var autonumber = false
        var sequenceIndex = 0

        lines.forEach { line ->
            // Title.
            parseSequenceTitle(line)?.let { title = it; return@forEach }

            // Autonumber.
            if (line.startsWith("autonumber", ignoreCase = true)) {
                autonumber = true
                return@forEach
            }

            // Participant / actor.
            parseSequenceParticipant(line)?.let { participant ->
                if (participant.id !in nodes) nodes[participant.id] = participant
                return@forEach
            }

            // Note.
            parseSequenceNote(line, sequenceIndex)?.let { note ->
                note.participants.forEach { participant ->
                    if (participant !in nodes) {
                        nodes[participant] = MermaidNode(participant, participant, MermaidNodeShape.Rectangle)
                    }
                }
                notes += note
                sequenceIndex += 1
                return@forEach
            }

            // Message.
            parseSequenceMessage(line)?.let { message ->
                if (message.from.id !in nodes) nodes[message.from.id] = message.from
                if (message.to.id !in nodes) nodes[message.to.id] = message.to
                edges += MermaidEdge(
                    from = message.from.id,
                    to = message.to.id,
                    label = message.label,
                    style = message.style,
                    arrow = message.arrow,
                    sequenceIndex = sequenceIndex,
                )
                // Track activations touching this message.
                activations.filter { it.participant == message.from.id && it.endSequenceIndex == -1 }
                    .forEach { it.edgeIndexes.add(sequenceIndex) }
                activations.filter { it.participant == message.to.id && it.endSequenceIndex == -1 }
                    .forEach { it.edgeIndexes.add(sequenceIndex) }
                sequenceIndex += 1
                return@forEach
            }

            // Activate / Deactivate.
            Regex("""^activate\s+([A-Za-z0-9_]+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val participant = match.groupValues[1]
                val activation = MermaidSequenceActivationBuilder(
                    participant = participant,
                    startSequenceIndex = sequenceIndex,
                )
                activations.add(activation)
                activeActivations.getOrPut(participant) { mutableListOf() }.add(activation)
                sequenceIndex += 1
                return@forEach
            }
            Regex("""^deactivate\s+([A-Za-z0-9_]+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val participant = match.groupValues[1]
                val stack = activeActivations[participant]
                val active = stack?.removeLastOrNull()
                if (active != null) active.close(sequenceIndex)
                sequenceIndex += 1
                return@forEach
            }

            // Create / Destroy.
            Regex("""^create\s+(participant|actor)\s+([A-Za-z0-9_]+)(?:\s+as\s+(.+))?$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val id = match.groupValues[2]
                val label = match.groupValues[3].trim().ifEmpty { id }
                if (id !in nodes) nodes[id] = MermaidNode(id, label, MermaidNodeShape.Rectangle)
                lifecycleEvents.add(
                    MermaidSequenceLifecycleEvent(
                        participant = id,
                        kind = MermaidSequenceLifecycleKind.Create,
                        sequenceIndex = sequenceIndex,
                    ),
                )
                sequenceIndex += 1
                return@forEach
            }
            Regex("""^destroy\s+([A-Za-z0-9_]+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                lifecycleEvents.add(
                    MermaidSequenceLifecycleEvent(
                        participant = match.groupValues[1],
                        kind = MermaidSequenceLifecycleKind.Destroy,
                        sequenceIndex = sequenceIndex,
                    ),
                )
                sequenceIndex += 1
                return@forEach
            }

            // Fragment start (alt / loop / opt / par / critical / break).
            Regex("""^(alt|loop|opt|par|critical|break)\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val kindStr = match.groupValues[1].lowercase()
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
                    label = match.groupValues[2].trim(),
                    startSequenceIndex = sequenceIndex,
                )
                fragment.branches.add(
                    MermaidSequenceBranchBuilder(
                        kind = kindStr,
                        label = match.groupValues[2].trim(),
                        startSequenceIndex = sequenceIndex,
                    ),
                )
                fragments.add(fragment)
                fragmentStack.add(fragment)
                return@forEach
            }

            // Fragment branch (else / and / option).
            Regex("""^(else|and|option)\s*(.+)?$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                if (fragmentStack.isNotEmpty()) {
                    val kind = match.groupValues[1].lowercase()
                    val label = match.groupValues[2].trim().ifEmpty { null }
                    val currentFragment = fragmentStack.last()
                    currentFragment.branches.lastOrNull()?.let { it.endSequenceIndex = sequenceIndex }
                    currentFragment.branches.add(
                        MermaidSequenceBranchBuilder(
                            kind = kind,
                            label = label,
                            startSequenceIndex = sequenceIndex,
                        ),
                    )
                }
                return@forEach
            }

            // End (fragments or regions).
            if (line.equals("end", ignoreCase = true)) {
                if (fragmentStack.isNotEmpty()) {
                    val fragment = fragmentStack.removeLast()
                    fragment.branches.lastOrNull()?.let { it.endSequenceIndex = sequenceIndex }
                    fragment.endSequenceIndex = sequenceIndex
                } else if (regionStack.isNotEmpty()) {
                    val region = regionStack.removeLast()
                    region.endSequenceIndex = sequenceIndex
                }
                return@forEach
            }

            // Rect / Box region.
            Regex("""^rect\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val region = MermaidSequenceRegionBuilder(
                    kind = MermaidSequenceRegionKind.Rect,
                    color = match.groupValues[1].trim(),
                    startSequenceIndex = sequenceIndex,
                )
                regions.add(region)
                regionStack.add(region)
                return@forEach
            }
            Regex("""^box\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val region = MermaidSequenceRegionBuilder(
                    kind = MermaidSequenceRegionKind.Box,
                    label = match.groupValues[1].trim(),
                    startSequenceIndex = sequenceIndex,
                )
                regions.add(region)
                regionStack.add(region)
                return@forEach
            }

            // Link / Links.
            Regex("""^link\s+([A-Za-z0-9_]+)\s*:\s*(.+?)\s*@\s*(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                links.add(
                    MermaidSequenceLink(
                        participant = match.groupValues[1],
                        url = match.groupValues[3].trim(),
                        label = match.groupValues[2].trim().ifEmpty { null },
                    ),
                )
                return@forEach
            }
            Regex("""^links\s+([A-Za-z0-9_]+)\s*:\s*\{(.+)}$""", RegexOption.IGNORE_CASE).matchEntire(line)?.let { match ->
                val participant = match.groupValues[1]
                val jsonContent = match.groupValues[2]
                Regex(""""([^"]+)"\s*:\s*"([^"]+)"""").findAll(jsonContent).forEach { m ->
                    links.add(
                        MermaidSequenceLink(
                            participant = participant,
                            url = m.groupValues[2],
                            label = m.groupValues[1],
                        ),
                    )
                }
                return@forEach
            }
        }

        return ParseResult.Sequence(
            direction = defaultDirection,
            nodes = nodes,
            edges = edges,
            notes = notes,
            fragments = fragments.map { it.toFragment() },
            activations = activations.map { it.toActivation() },
            lifecycleEvents = lifecycleEvents,
            regions = regions.map { it.toRegion() },
            links = links,
            title = title,
            autonumber = autonumber,
        )
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

    private fun parseSequenceTitle(line: String): String? {
        val match = Regex("^title\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return match.groupValues[1].trim()
    }

    private data class ParsedEdge(
        val from: MermaidNode,
        val to: MermaidNode,
        val label: String?,
        val style: MermaidEdgeStyle,
        val arrow: MermaidEdgeArrow,
    )
}
