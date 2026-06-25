package xyz.junerver.compose.palette.mermaid

/**
 * Pluggable parser for one Mermaid diagram type. Each concrete diagram (flowchart,
 * sequence, class, ER, state, ...) ships its own implementation and registers via
 * [MermaidParser]. Adding a new diagram type is a pure "add a file" operation — no
 * existing code needs to change (open/closed).
 *
 * The parser owns its own mutable state; it never sees another diagram type's state,
 * which removes the cross-type coupling the old monolithic `parse()` had.
 */
internal interface MermaidDiagramParser {
    /** Diagram declaration keyword, e.g. "erDiagram", "stateDiagram-v2". */
    val keyword: String

    /** Alternate header spellings that route to this parser, e.g. ["stateDiagram"]. */
    val aliases: List<String>
        get() = emptyList()

    /**
     * When `true`, the dispatcher hands this parser the *entire* source (header line
     * included) instead of dropping the first line. Use for diagrams whose header carries
     * inline arguments on the same line (e.g. `pie title X`, `gantt title Y`).
     */
    val consumesHeaderLine: Boolean
        get() = false

    /** Direction implied by this diagram family before any `direction` line overrides it. */
    val defaultDirection: MermaidDirection

    /**
     * Parse the body lines (header already stripped) into a [ParseResult].
     */
    fun parse(lines: List<String>): ParseResult
}

/**
 * Output of a single diagram parser. The dispatcher ([MermaidParser.parse]) merges
 * whichever variant is produced into a backward-compatible [MermaidDiagram].
 */
internal sealed interface ParseResult {
    val diagramType: MermaidDiagramType
    val direction: MermaidDirection

    data class Flowchart(
        override val direction: MermaidDirection,
        val nodes: Map<String, MermaidNode>,
        val edges: List<MermaidEdge>,
        val notes: List<MermaidNote>,
        val subgraphs: List<MermaidSubgraph>,
        val classDefs: List<MermaidFlowchartClassDef>,
        val classAssignments: List<MermaidFlowchartClassAssignment>,
        val nodeStyles: List<MermaidFlowchartNodeStyle>,
        val linkStyles: List<MermaidFlowchartLinkStyle>,
        val clicks: List<MermaidFlowchartClick>,
    ) : ParseResult {
        override val diagramType = MermaidDiagramType.Flowchart
    }

    data class Sequence(
        override val direction: MermaidDirection,
        val nodes: Map<String, MermaidNode>,
        val edges: List<MermaidEdge>,
        val notes: List<MermaidNote>,
        val fragments: List<MermaidSequenceFragment>,
        val activations: List<MermaidSequenceActivation>,
        val lifecycleEvents: List<MermaidSequenceLifecycleEvent>,
        val regions: List<MermaidSequenceRegion>,
        val links: List<MermaidSequenceLink>,
        val title: String?,
        val autonumber: Boolean,
    ) : ParseResult {
        override val diagramType = MermaidDiagramType.Sequence
    }

    data class ClassDiagram(
        override val direction: MermaidDirection,
        val classDefinitions: List<MermaidClassDefinition>,
        val classRelationships: List<MermaidClassRelationship>,
    ) : ParseResult {
        override val diagramType = MermaidDiagramType.ClassDiagram
    }

    data class ErDiagram(
        override val direction: MermaidDirection,
        val erEntities: List<ErEntity>,
        val erRelationships: List<ErRelationship>,
    ) : ParseResult {
        override val diagramType = MermaidDiagramType.ErDiagram
    }

    data class StateDiagram(
        override val direction: MermaidDirection,
        val stateDefinitions: List<StateDefinition>,
        val stateTransitions: List<StateTransition>,
        val stateNotes: List<StateNote>,
    ) : ParseResult {
        override val diagramType = MermaidDiagramType.StateDiagram
    }

    data class PieDiagram(
        override val direction: MermaidDirection,
        val title: String?,
        val slices: List<PieSlice>,
        val showData: Boolean,
    ) : ParseResult {
        override val diagramType = MermaidDiagramType.PieDiagram
    }
}

/**
 * Merge a [ParseResult] back into the flat [MermaidDiagram] shape that the rest of the
 * codebase (layout engine, renderers, tests) already depends on. This keeps the
 * refactoring purely internal: every external field is still populated exactly as before.
 */
internal fun ParseResult.toMermaidDiagram(): MermaidDiagram =
    when (this) {
        is ParseResult.Flowchart -> MermaidDiagram(
            direction = direction,
            nodes = nodes,
            edges = edges,
            notes = notes,
            type = diagramType,
            subgraphs = subgraphs,
            flowchartClassDefs = classDefs,
            flowchartClassAssignments = classAssignments,
            flowchartNodeStyles = nodeStyles,
            flowchartLinkStyles = linkStyles,
            flowchartClicks = clicks,
        )

        is ParseResult.Sequence -> MermaidDiagram(
            direction = direction,
            nodes = nodes,
            edges = edges,
            notes = notes,
            type = diagramType,
            sequenceFragments = fragments,
            sequenceActivations = activations,
            sequenceLifecycleEvents = lifecycleEvents,
            sequenceRegions = regions,
            sequenceLinks = links,
            title = title,
            sequenceAutonumber = autonumber,
        )

        is ParseResult.ClassDiagram -> MermaidDiagram(
            direction = direction,
            nodes = emptyMap(),
            edges = emptyList(),
            type = diagramType,
            classDefinitions = classDefinitions,
            classRelationships = classRelationships,
        )

        is ParseResult.ErDiagram -> MermaidDiagram(
            direction = direction,
            nodes = emptyMap(),
            edges = emptyList(),
            type = diagramType,
            erEntities = erEntities,
            erRelationships = erRelationships,
        )

        is ParseResult.StateDiagram -> MermaidDiagram(
            direction = direction,
            nodes = emptyMap(),
            edges = emptyList(),
            type = diagramType,
            stateDefinitions = stateDefinitions,
            stateTransitions = stateTransitions,
            stateNotes = stateNotes,
        )

        is ParseResult.PieDiagram -> MermaidDiagram(
            direction = direction,
            nodes = emptyMap(),
            edges = emptyList(),
            type = diagramType,
            title = title,
            pieSlices = slices,
            pieShowData = showData,
        )
    }
