package xyz.junerver.compose.palette.mermaid

// ── Class Diagram models ──────────────────────────────────────────────

enum class MermaidClassMemberKind {
    Field,
    Method,
}

enum class MermaidClassVisibility {
    Public,
    Private,
    Protected,
    Package,
}

data class MermaidClassMember(
    val kind: MermaidClassMemberKind,
    val visibility: MermaidClassVisibility = MermaidClassVisibility.Package,
    val name: String,
    val type: String? = null,
    val isAbstract: Boolean = false,
    val isStatic: Boolean = false,
)

data class MermaidClassDefinition(
    val id: String,
    val label: String,
    val annotation: String? = null,
    val members: List<MermaidClassMember> = emptyList(),
    val cssClass: String? = null,
    val sourceRange: MermaidSourceRange? = null,
)

enum class MermaidClassRelationType {
    Inheritance,       // <|--  (solid line, hollow triangle)
    Composition,       // *--   (solid line, filled diamond)
    Aggregation,       // o--   (solid line, hollow diamond)
    Association,       // -->   (solid line, open arrow)
    Link,              // --    (solid line, no arrow)
    Dependency,        // ..>   (dotted line, open arrow)
    Realization,       // ..|>  (dotted line, hollow triangle)
    DependencyLink,    // ..    (dotted line, no arrow)
}

data class MermaidClassRelationship(
    val from: String,
    val to: String,
    val type: MermaidClassRelationType,
    val label: String? = null,
    val fromCardinality: String? = null,
    val toCardinality: String? = null,
    val sourceRange: MermaidSourceRange? = null,
)

// ── ER Diagram models ──────────────────────────────────────────────

data class ErEntity(
    val name: String,
    val attributes: List<ErAttribute> = emptyList(),
    val sourceRange: MermaidSourceRange? = null,
)

data class ErAttribute(
    val name: String,
    val type: String,
    val comment: String? = null,
    val isPrimaryKey: Boolean = false,
    val isForeignKey: Boolean = false,
)

enum class ErRelationshipKind {
    OneToOne,
    OneToManyZeroOrMore,
    OneToManyOneOrMore,
    ManyToManyZeroOrMore,
    ManyToManyOneOrMore,
    ManyToOneZeroOrMore,
    ManyToOneOneOrMore,
    NonIdentifyingOneToOne,
    NonIdentifyingOneToMany,
    NonIdentifyingManyToOne,
    NonIdentifyingManyToMany,
}

data class ErRelationship(
    val from: String,
    val to: String,
    val kind: ErRelationshipKind,
    val label: String? = null,
    val sourceRange: MermaidSourceRange? = null,
)

// ── State Diagram models ──────────────────────────────────────────────

data class StateDefinition(
    val id: String,
    val label: String? = null,
    val isStart: Boolean = false,
    val isEnd: Boolean = false,
    val isFork: Boolean = false,
    val isJoin: Boolean = false,
    val children: List<StateDefinition> = emptyList(),
    val sourceRange: MermaidSourceRange? = null,
)

data class StateTransition(
    val from: String,
    val to: String,
    val event: String? = null,
    val sourceRange: MermaidSourceRange? = null,
)

data class StateNote(
    val stateId: String,
    val text: String,
    val position: StateNotePosition = StateNotePosition.Right,
)

enum class StateNotePosition {
    Left, Right,
}

data class MermaidDiagram(
    val direction: MermaidDirection,
    val nodes: Map<String, MermaidNode>,
    val edges: List<MermaidEdge>,
    val notes: List<MermaidNote> = emptyList(),
    val type: MermaidDiagramType = MermaidDiagramType.Flowchart,
    val subgraphs: List<MermaidSubgraph> = emptyList(),
    val sequenceFragments: List<MermaidSequenceFragment> = emptyList(),
    val sequenceActivations: List<MermaidSequenceActivation> = emptyList(),
    val sequenceLifecycleEvents: List<MermaidSequenceLifecycleEvent> = emptyList(),
    val sequenceRegions: List<MermaidSequenceRegion> = emptyList(),
    val sequenceLinks: List<MermaidSequenceLink> = emptyList(),
    val title: String? = null,
    val sequenceAutonumber: Boolean = false,
    val diagnostics: List<MermaidParseDiagnostic> = emptyList(),
    val directives: List<MermaidDirective> = emptyList(),
    val frontmatter: MermaidFrontmatter? = null,
    val flowchartClassDefs: List<MermaidFlowchartClassDef> = emptyList(),
    val flowchartClassAssignments: List<MermaidFlowchartClassAssignment> = emptyList(),
    val flowchartNodeStyles: List<MermaidFlowchartNodeStyle> = emptyList(),
    val flowchartLinkStyles: List<MermaidFlowchartLinkStyle> = emptyList(),
    val flowchartClicks: List<MermaidFlowchartClick> = emptyList(),
    val classDefinitions: List<MermaidClassDefinition> = emptyList(),
    val classRelationships: List<MermaidClassRelationship> = emptyList(),
    val erEntities: List<ErEntity> = emptyList(),
    val erRelationships: List<ErRelationship> = emptyList(),
    val stateDefinitions: List<StateDefinition> = emptyList(),
    val stateTransitions: List<StateTransition> = emptyList(),
    val stateNotes: List<StateNote> = emptyList(),
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
    val startArrow: MermaidEdgeArrow = MermaidEdgeArrow.None,
    val visible: Boolean = true,
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
    val direction: MermaidDirection? = null,
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
    Open,
    Circle,
    Cross,
}

enum class MermaidNotePosition {
    LeftOf,
    RightOf,
    Over,
}

enum class MermaidDiagramType {
    Flowchart,
    Sequence,
    ClassDiagram,
    ErDiagram,
    StateDiagram,
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
    Subroutine,
    Database,
    Asymmetric,
    Hexagon,
    Parallelogram,
    ParallelogramAlt,
    Trapezoid,
    TrapezoidAlt,
    DoubleCircle,
}

// ── Layout models ─────────────────────────────────────────────────────

data class MermaidLayout(
    val type: MermaidDiagramType,
    val direction: MermaidDirection,
    val nodes: Map<String, PositionedMermaidNode>,
    val edges: List<MermaidEdge>,
    val notes: List<MermaidNote> = emptyList(),
    val subgraphs: List<PositionedMermaidSubgraph> = emptyList(),
    val flowchartClassDefs: List<MermaidFlowchartClassDef> = emptyList(),
    val flowchartClassAssignments: List<MermaidFlowchartClassAssignment> = emptyList(),
    val flowchartNodeStyles: List<MermaidFlowchartNodeStyle> = emptyList(),
    val flowchartLinkStyles: List<MermaidFlowchartLinkStyle> = emptyList(),
    /**
     * State diagram only: horizontal curvature offset per edge index. Edges sharing the
     * same endpoint pair get incrementing offsets so forward/backward links fan out into
     * separate arcs instead of overlapping into one line.
     */
    val stateEdgeOffsets: Map<Int, Float> = emptyMap(),
    /**
     * Class diagram only: the UML relationship type per edge index, so the renderer can
     * draw the correct end marker (hollow triangle for inheritance, filled/hollow diamond
     * for composition/aggregation, open arrow for dependency).
     */
    val classRelationTypes: Map<Int, MermaidClassRelationType> = emptyMap(),
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

// ── Directive / Frontmatter ───────────────────────────────────────────

data class MermaidDirective(
    val kind: String,
    val value: String,
    val source: String,
    val line: Int,
    val column: Int = 1,
    val sourceRange: MermaidSourceRange? = null,
)

data class MermaidFrontmatter(
    val yaml: String,
    val startLine: Int,
    val endLine: Int,
)

// ── Flowchart metadata ────────────────────────────────────────────────

data class MermaidFlowchartClassDef(
    val name: String,
    val declarations: String,
    val line: Int,
    val sourceRange: MermaidSourceRange? = null,
)

data class MermaidFlowchartClassAssignment(
    val nodeIds: List<String>,
    val className: String,
    val line: Int,
    val sourceRange: MermaidSourceRange? = null,
)

data class MermaidFlowchartNodeStyle(
    val nodeId: String,
    val declarations: String,
    val line: Int,
    val sourceRange: MermaidSourceRange? = null,
)

data class MermaidFlowchartLinkStyle(
    val edgeIndexes: List<Int>? = null,
    val declarations: String,
    val line: Int,
    val sourceRange: MermaidSourceRange? = null,
)

data class MermaidFlowchartClick(
    val nodeId: String,
    val href: String? = null,
    val tooltip: String? = null,
    val line: Int,
    val sourceRange: MermaidSourceRange? = null,
)

// ── Sequence diagram models ───────────────────────────────────────────

data class MermaidSequenceFragment(
    val kind: MermaidSequenceFragmentKind,
    val label: String? = null,
    val branches: List<MermaidSequenceBranch> = emptyList(),
    val startSequenceIndex: Int,
    val endSequenceIndex: Int,
    val sourceRange: MermaidSourceRange? = null,
)

enum class MermaidSequenceFragmentKind {
    Alt, Loop, Opt, Par, Critical, Break,
}

data class MermaidSequenceBranch(
    val kind: String,
    val label: String? = null,
    val startSequenceIndex: Int,
    val endSequenceIndex: Int,
)

data class MermaidSequenceActivation(
    val participant: String,
    val startSequenceIndex: Int,
    val endSequenceIndex: Int,
    val sourceRange: MermaidSourceRange? = null,
)

data class MermaidSequenceLifecycleEvent(
    val participant: String,
    val kind: MermaidSequenceLifecycleKind,
    val sequenceIndex: Int,
    val sourceRange: MermaidSourceRange? = null,
)

enum class MermaidSequenceLifecycleKind {
    Create, Destroy,
}

data class MermaidSequenceRegion(
    val kind: MermaidSequenceRegionKind,
    val label: String? = null,
    val color: String? = null,
    val participants: List<String> = emptyList(),
    val startSequenceIndex: Int,
    val endSequenceIndex: Int,
    val sourceRange: MermaidSourceRange? = null,
)

enum class MermaidSequenceRegionKind {
    Rect, Box,
}

data class MermaidSequenceLink(
    val participant: String,
    val url: String,
    val label: String? = null,
    val sourceRange: MermaidSourceRange? = null,
)

// ── Diagnostics ───────────────────────────────────────────────────────

data class MermaidSourceRange(
    val line: Int,
    val column: Int,
    val endColumn: Int? = null,
)

enum class MermaidDiagnosticSeverity {
    Warning,
    Error,
}

enum class MermaidDiagnosticCode {
    UnsupportedStatement,
    UnsupportedDirective,
}

data class MermaidParseDiagnostic(
    val code: MermaidDiagnosticCode,
    val message: String,
    val severity: MermaidDiagnosticSeverity = MermaidDiagnosticSeverity.Warning,
    val line: Int,
    val column: Int = 1,
    val endColumn: Int? = null,
    val source: String? = null,
)
