package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.ArchDir
import xyz.junerver.compose.palette.mermaid.ArchEdge
import xyz.junerver.compose.palette.mermaid.ArchEdgeKind
import xyz.junerver.compose.palette.mermaid.ArchNode
import xyz.junerver.compose.palette.mermaid.ArchNodeKind
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Architecture parser. Mermaid syntax (keyword `architecture-beta`; bare `architecture` is
 * detected but the grammar requires the `-beta` token):
 * ```
 * architecture-beta
 *     group api(cloud)[API]
 *     service db(database)[Database] in api
 *     junction junctionCenter
 *     db:L -- R:server
 *     subnet:R --> L:gateway
 *     server{group}:B --> T:subnet{group}
 *     align column db disk1 disk2
 * ```
 *
 * Nodes carry an optional `(icon)` and `[title]`; services/junctions/groups may nest via
 * `in <parent>`. Edges connect `lhs:Dir -- Dir:rhs` with optional `{group}` boundary modifiers
 * and `--`/`-->`/`<--`/`<-->` connectors.
 */
internal object ArchitectureParser : MermaidDiagramParser {
    override val keyword: String = "architecture-beta"
    override val aliases: List<String> = listOf("architecture")
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    private val groupRegex = Regex(
        """^group\s+(\w+)(?:\s*\(([\w-:]+)\))?(?:\s*\[([^\]]*)\])?(?:\s+in\s+(\w+))?\s*$""",
    )
    private val serviceRegex = Regex(
        """^service\s+(\w+)(?:\s*(?:\(([\w-:]+)\)|"([^"]*)"))?(?:\s*\[([^\]]*)\])?(?:\s+in\s+(\w+))?\s*$""",
    )
    private val junctionRegex = Regex("""^junction\s+(\w+)(?:\s+in\s+(\w+))?\s*$""")
    // Edge: `<lhs>{group}?:<dir> <conn> <dir>:<rhs>{group}?`. conn ∈ {--, -->, <--, <-->}.
    private val edgeRegex = Regex(
        """^(\w+)(\{group\})?\s*:\s*([LRTB])\s*(--|-->|<--|<-->)\s*([LRTB])\s*:\s*(\w+)(\{group\})?\s*$""",
    )
    private val alignRegex = Regex("""^align\s+(row|column)\s+(\w+(?:\s+\w+)+)\s*$""", RegexOption.IGNORE_CASE)

    private fun parseDir(d: String): ArchDir = when (d) { "L" -> ArchDir.L; "R" -> ArchDir.R; "T" -> ArchDir.T; else -> ArchDir.B }

    override fun parse(lines: List<String>): ParseResult.ArchitectureDiagram {
        val nodes = mutableListOf<ArchNode>()
        val edges = mutableListOf<ArchEdge>()

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed // skip the `architecture-beta` header
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("%%")) return@forEachIndexed

            groupRegex.matchEntire(trimmed)?.let { m ->
                nodes.add(ArchNode(id = m.groupValues[1], kind = ArchNodeKind.Group,
                    icon = m.groupValues[2].ifBlank { null }, title = m.groupValues[3].ifBlank { null },
                    parentId = m.groupValues[4].ifBlank { null }))
                return@forEachIndexed
            }
            serviceRegex.matchEntire(trimmed)?.let { m ->
                val icon = m.groupValues[2].ifBlank { null } ?: m.groupValues[3].ifBlank { null }
                nodes.add(ArchNode(id = m.groupValues[1], kind = ArchNodeKind.Service,
                    icon = icon, title = m.groupValues[4].ifBlank { null },
                    parentId = m.groupValues[5].ifBlank { null }))
                return@forEachIndexed
            }
            junctionRegex.matchEntire(trimmed)?.let { m ->
                nodes.add(ArchNode(id = m.groupValues[1], kind = ArchNodeKind.Junction,
                    parentId = m.groupValues[2].ifBlank { null }))
                return@forEachIndexed
            }
            edgeRegex.matchEntire(trimmed)?.let { m ->
                val conn = m.groupValues[4]
                val kind = when (conn) {
                    "-->" -> ArchEdgeKind.Forward
                    "<--" -> ArchEdgeKind.Back
                    "<-->" -> ArchEdgeKind.Bidirectional
                    else -> ArchEdgeKind.Plain
                }
                edges.add(ArchEdge(
                    from = m.groupValues[1], to = m.groupValues[6],
                    fromDir = parseDir(m.groupValues[3]), toDir = parseDir(m.groupValues[5]),
                    kind = kind))
                return@forEachIndexed
            }
            // `align` directives are accepted but don't affect layout.
            if (alignRegex.matches(trimmed)) return@forEachIndexed
        }

        return ParseResult.ArchitectureDiagram(direction = defaultDirection, nodes = nodes, edges = edges)
    }
}
