package xyz.junerver.compose.palette.mermaid

// ── Pie Chart models ──────────────────────────────────────────────────

/** A single slice of a pie chart: a label and its numeric value. */
data class PieSlice(
    val label: String,
    val value: Double,
)

// ── Gantt Chart models ────────────────────────────────────────────────

/** Execution status of a gantt task, derived from the `done`/`active`/`crit` tags. */
enum class GanttTaskStatus {
    /** Default, not yet started. */
    Todo,
    /** `active` — currently in progress. */
    Active,
    /** `done` — completed. */
    Done,
    /** `crit` — on the critical path (orthogonal to active/done, encoded via [isCritical]). */
    ;
}

/**
 * A single gantt task. For the core implementation we capture the raw start/end tokens
 * (dates or durations) plus an optional dependency (`after <id>`); concrete date math is
 * deferred to the renderer/layout which has access to a clock and date formatting.
 */
data class GanttTask(
    val id: String?,
    val title: String,
    val status: GanttTaskStatus,
    val isCritical: Boolean,
    val isMilestone: Boolean,
    /** Raw start token as written: a date string, a duration, or `after <taskId>`. */
    val startToken: String?,
    /** Raw end token as written: a date string or a duration. */
    val endToken: String?,
    /** Parsed duration in days when the end token is a duration (e.g. "30d"); null otherwise. */
    val durationDays: Double?,
    /** IDs this task depends on, populated from `after a b` syntax. */
    val dependsOn: List<String> = emptyList(),
)

/** A named group of gantt tasks. */
data class GanttSection(
    val name: String,
    val tasks: List<GanttTask>,
)

/** Top-level gantt configuration parsed from the header region. */
data class GanttConfig(
    val title: String? = null,
    val dateFormat: String? = null,
    val axisFormat: String? = null,
    val excludes: List<String> = emptyList(),
)

// ── GitGraph models ───────────────────────────────────────────────────

/** Visual emphasis of a git commit, mirroring mermaid's commit `type:` attribute. */
enum class GitCommitType {
    /** Default — solid circle. */
    Normal,
    /** Reverse commit — crossed circle. */
    Reverse,
    /** Highlighted — filled rectangle. */
    Highlight,
}

/** A single git commit. [seq] is its order on the timeline (0-based, left to right). */
data class GitCommit(
    val id: String,
    val seq: Int,
    val branch: String,
    val type: GitCommitType,
    val tag: String? = null,
    /** True for the auto-created merge commit; rendered as a double circle. */
    val isMerge: Boolean = false,
)

/** A git branch with the commits that live on it (in timeline order). */
data class GitBranch(
    val name: String,
    val commits: List<GitCommit>,
)

/** A merge: `merge X` joins branch [from] into [into] at the merge commit. */
data class GitMerge(
    val from: String,
    val into: String,
    val mergeCommitId: String,
)

// ── Mindmap models ────────────────────────────────────────────────────

/** Shape of a mindmap node, mirroring mermaid's mindmap shape syntax. */
enum class MindmapNodeShape {
    /** Default — plain text, no delimiters. */
    Default,
    /** `id[text]` — square. */
    Square,
    /** `id(text)` — rounded square. */
    Rounded,
    /** `id((text))` — circle. */
    Circle,
    /** `id{{text}}` — hexagon. */
    Hexagon,
    /** `id)text(` — cloud. */
    Cloud,
    /** `id))text((` — bang. */
    Bang,
}

/** A mindmap node. [depth] is the indentation level (0 = root). [parentId] is null for root. */
data class MindmapNode(
    val id: String,
    val label: String,
    val shape: MindmapNodeShape,
    val depth: Int,
    val parentId: String?,
)

// ── Timeline models ──────────────────────────────────────────────────

/**
 * One time period in a mermaid timeline. [section] is the owning section name (empty string when
 * no `section` directive precedes it). [events] holds the one-or-more events for that period
 * (mermaid allows `period : ev1 : ev2` inline chaining and `:` continuation lines).
 */
data class TimelinePeriod(
    val section: String,
    val time: String,
    val events: List<String>,
)

// ── Quadrant chart models ────────────────────────────────────────────

/** A quadrant axis: [lowLabel] at the origin, [highLabel] at the far end. */
data class QuadrantAxis(
    val lowLabel: String,
    val highLabel: String,
)

/**
 * A plotted point on a quadrant chart. [x]/[y] are normalized to `[0,1]` (0 = low, 1 = high);
 * the parser clamps out-of-range values. [color]/[radius] come from inline `color:`/`radius:`
 * styling or a `classDef`; null means "use the renderer default".
 */
data class QuadrantPoint(
    val label: String,
    val x: Float,
    val y: Float,
    val color: UInt? = null,
    val radius: Float? = null,
)

// ── XYChart models ───────────────────────────────────────────────────

enum class XySeriesKind { Bar, Line }

/**
 * One data series of an xychart. [values] align positionally with the x-axis categories (or
 * sequential indices when the axis is numeric).
 */
data class XySeries(
    val kind: XySeriesKind,
    val values: List<Float>,
)

// ── Journey models ───────────────────────────────────────────────────

/** A user-journey task with a 1–5 satisfaction [score] and the actors that perform it. */
data class JourneyTask(
    val name: String,
    val score: Int,
    val actors: List<String>,
)

/** A titled grouping of journey tasks. */
data class JourneySection(
    val title: String,
    val tasks: List<JourneyTask>,
)

// ── Packet models ────────────────────────────────────────────────────

/**
 * A network-packet field spanning bits `[startBit, endBit]`. [bits] is the width
 * (`endBit - startBit + 1`); it's stored explicitly so the renderer can lay out relative
 * (`+bits`) and absolute forms uniformly.
 */
data class PacketField(
    val label: String,
    val startBit: Int,
    val endBit: Int,
    val bits: Int,
)

// ── Sankey models ────────────────────────────────────────────────────

/** A sankey flow of weight [value] from [source] to [target]. Nodes are implied by the flows. */
data class SankeyFlow(
    val source: String,
    val target: String,
    val value: Float,
)

data class SankeyNodeLayout(
    val name: String,
    val level: Int,
    val maxLevel: Int,
    val value: Float,
    val order: Int,
    val yWeight: Float,
    val heightWeight: Float,
)

// ── Architecture models ──────────────────────────────────────────────

enum class ArchNodeKind { Group, Service, Junction }

enum class ArchEdgeKind { Plain, Forward, Back, Bidirectional }

/** A port direction for an architecture edge endpoint (Left/Right/Top/Bottom). */
enum class ArchDir { L, R, T, B }

/**
 * An architecture node. [icon] is the `(...)` icon name (e.g. `database`), null for junctions.
 * [parentId] is the `in <group>` parent; null at the top level.
 */
data class ArchNode(
    val id: String,
    val kind: ArchNodeKind,
    val icon: String? = null,
    val title: String? = null,
    val parentId: String? = null,
)

/**
 * An architecture edge between two nodes. [fromDir]/[toDir] are the L/R/T/B ports.
 * [label] captures the `-<title>-` labeled-edge variant (null for plain `--`).
 */
data class ArchEdge(
    val from: String,
    val to: String,
    val fromDir: ArchDir,
    val toDir: ArchDir,
    val kind: ArchEdgeKind,
    val label: String? = null,
)

// ── Requirement diagram models ───────────────────────────────────────

/** The 6 requirement kinds plus the generic `element` node. */
enum class RequirementElementType {
    Requirement, FunctionalRequirement, InterfaceRequirement,
    PerformanceRequirement, PhysicalRequirement, DesignConstraint, Element,
}

/** The 7 relationship kinds drawn as labelled arrows between requirements/elements. */
enum class RequirementRelationKind {
    Contains, Copies, Derives, Satisfies, Verifies, Refines, Traces,
}

/**
 * A requirement or element node. Only `requirement`-type boxes carry risk/verifyMethod;
 * elements carry type/docRef instead. [text] holds the human-readable requirement text.
 */
data class RequirementBox(
    val id: String,
    val type: RequirementElementType,
    val label: String,
    val text: String = "",
    val risk: String = "",
    val verifyMethod: String = "",
    val docRef: String = "",
)

/** A labelled, typed relationship `A - <kind> -> B` (or the `<-` reverse form). */
data class RequirementRelationship(
    val from: String,
    val to: String,
    val kind: RequirementRelationKind,
    val label: String = "",
)

// ── Block diagram models ─────────────────────────────────────────────

/**
 * A block-diagram node. [columnSpan] is the number of grid columns it occupies (1 unless the
 * `id:N` span suffix is given). [shape] is the node's rendered shape.
 */
data class BlockNode(
    val id: String,
    val label: String,
    val shape: MermaidNodeShape = MermaidNodeShape.Rounded,
    val columnSpan: Int = 1,
)

/** An edge between two blocks. [style] maps to the mermaid arrow glyph family. */
data class BlockEdge(
    val from: String,
    val to: String,
    val label: String? = null,
    val style: MermaidEdgeStyle = MermaidEdgeStyle.Solid,
    val arrow: MermaidEdgeArrow = MermaidEdgeArrow.Forward,
)

/**
 * A nested `block:NAME { ... end }` composite. [columns] is its declared column count (-1 for
 * `auto`); [childIds] are the ids (nodes or nested blocks) declared inside it, in order.
 */
data class BlockContainer(
    val id: String,
    val label: String?,
    val columns: Int,
    val childIds: List<String>,
)

// ── C4 diagram models ────────────────────────────────────────────────

/** C4 element kinds, grouped by level. `*_Ext` variants are external/partner systems. */
enum class C4ElementKind {
    Person, Person_Ext,
    System, System_Ext, SystemDb, SystemDb_Ext, SystemQueue, SystemQueue_Ext,
    Container, Container_Ext, ContainerDb, ContainerDb_Ext, ContainerQueue, ContainerQueue_Ext,
    Component, Component_Ext, ComponentDb, ComponentDb_Ext, ComponentQueue, ComponentQueue_Ext,
    Node, Node_L, Node_R, Deployment_Node,
}

/** Direction of a C4 relationship (explicit via Rel_U/Rel_D/Rel_L/Rel_R, default plain). */
enum class C4RelDirection { Plain, Up, Down, Left, Right, Back }

/** A C4 element (person/system/container/component/deployment node). */
data class C4Element(
    val alias: String,
    val kind: C4ElementKind,
    val label: String,
    val techn: String = "",
    val descr: String = "",
)

/** A grouping boundary whose body contains nested elements/boundaries. */
data class C4Boundary(
    val alias: String,
    val label: String,
    val type: String = "",
    val childAliases: List<String> = emptyList(),
)

/** A `Rel(from, to, "label", ?"techn")` relationship between two C4 elements. */
data class C4Relationship(
    val from: String,
    val to: String,
    val label: String,
    val techn: String = "",
    val direction: C4RelDirection = C4RelDirection.Plain,
)

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
    val pieSlices: List<PieSlice> = emptyList(),
    val pieShowData: Boolean = false,
    val ganttConfig: GanttConfig? = null,
    val ganttSections: List<GanttSection> = emptyList(),
    val gitBranches: List<GitBranch> = emptyList(),
    val gitCommits: List<GitCommit> = emptyList(),
    val gitMerges: List<GitMerge> = emptyList(),
    val mindmapNodes: List<MindmapNode> = emptyList(),
    val timelinePeriods: List<TimelinePeriod> = emptyList(),
    val quadrantTitle: String? = null,
    val quadrantXAxis: QuadrantAxis? = null,
    val quadrantYAxis: QuadrantAxis? = null,
    val quadrantLabels: List<String> = emptyList(),
    val quadrantPoints: List<QuadrantPoint> = emptyList(),
    val xyTitle: String? = null,
    val xyXAxisTitle: String? = null,
    val xyXAxisRange: Pair<Float, Float>? = null,
    val xyXCategories: List<String> = emptyList(),
    val xyYAxisTitle: String? = null,
    val xyYAxisRange: Pair<Float, Float>? = null,
    val xySeries: List<XySeries> = emptyList(),
    val requirementBoxes: List<RequirementBox> = emptyList(),
    val requirementRelationships: List<RequirementRelationship> = emptyList(),
    val blockNodes: List<BlockNode> = emptyList(),
    val blockEdges: List<BlockEdge> = emptyList(),
    val blockContainers: List<BlockContainer> = emptyList(),
    val c4Elements: List<C4Element> = emptyList(),
    val c4Boundaries: List<C4Boundary> = emptyList(),
    val c4Relationships: List<C4Relationship> = emptyList(),
    val journeyTitle: String? = null,
    val journeySections: List<JourneySection> = emptyList(),
    val packetTitle: String? = null,
    val packetFields: List<PacketField> = emptyList(),
    val sankeyFlows: List<SankeyFlow> = emptyList(),
    val archNodes: List<ArchNode> = emptyList(),
    val archEdges: List<ArchEdge> = emptyList(),
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
    PieDiagram,
    GanttDiagram,
    GitGraphDiagram,
    MindmapDiagram,
    Timeline,
    QuadrantChart,
    XYChart,
    RequirementDiagram,
    BlockDiagram,
    C4Diagram,
    Journey,
    Packet,
    Sankey,
    Architecture,
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
    /** Requirement diagram only: relationship kind per edge index (contains/satisfies/...). */
    val requirementRelationTypes: Map<Int, RequirementRelationKind> = emptyMap(),
    /** Sankey diagram only: semantic node columns, ordering, and value-scaled vertical spans. */
    val sankeyNodes: List<SankeyNodeLayout> = emptyList(),
    /** Architecture diagram only: parsed service/group/junction nodes with icon and parent metadata. */
    val archNodes: List<ArchNode> = emptyList(),
    /** Architecture diagram only: parsed edges with port directions. */
    val archEdges: List<ArchEdge> = emptyList(),
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
