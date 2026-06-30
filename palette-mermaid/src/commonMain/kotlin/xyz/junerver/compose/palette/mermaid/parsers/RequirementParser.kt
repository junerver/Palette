package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.RequirementBox
import xyz.junerver.compose.palette.mermaid.RequirementElementType
import xyz.junerver.compose.palette.mermaid.RequirementRelationKind
import xyz.junerver.compose.palette.mermaid.RequirementRelationship

/**
 * Requirement diagram parser. Mermaid syntax (keyword `requirement`/`requirementDiagram`):
 * ```
 * requirementDiagram
 *     requirement test_req {
 *         id: 1
 *         text: the test text.
 *         risk: high
 *         verifymethod: test
 *     }
 *     element test_entity {
 *         type: simulation
 *     }
 *     test_entity - satisfies -> test_req2
 *     test_req <- copies - test_entity2
 *     direction LR
 * ```
 *
 * Six requirement kinds + `element`. Seven relationship kinds, written as
 * `src - kind -> dst` or `dst <- kind - src`. Field keys inside `{...}` blocks are
 * case-insensitive. The header line is dropped (`consumesHeaderLine = false` default behavior
 * drops the first line, which the dispatcher already does for this parser).
 */
internal object RequirementParser : MermaidDiagramParser {
    override val keyword: String = "requirementDiagram"
    override val aliases: List<String> = listOf("requirement")
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown
    // The header carries no inline data, but keeping it lets `parse()` skip index 0 uniformly.
    override val consumesHeaderLine: Boolean = true

    // `<type> <name> {` opens a requirement/element block (the `{` may be on the same line).
    private val blockOpenRegex = Regex(
        """^(requirement|functionalRequirement|interfaceRequirement|performanceRequirement|physicalRequirement|designConstraint|element)\s+(?:"([^"]*)"|(\S+))\s*\{?\s*$""",
        RegexOption.IGNORE_CASE,
    )
    private val fieldRegex = Regex("""^\s*(id|text|risk|verifymethod|verifyMethod|type|docref)\s*:\s*(.+?)\s*$""", RegexOption.IGNORE_CASE)
    private val closeBraceRegex = Regex("""^\s*\}\s*$""")
    private val directionRegex = Regex("""^\s*direction\s+(TB|BT|LR|RL)\s*$""", RegexOption.IGNORE_CASE)
    // `src - kind -> dst`
    private val relForwardRegex = Regex("""^\s*(\S+)\s*-\s*(contains|copies|derives|satisfies|verifies|refines|traces)\s*->\s*(\S+)\s*$""", RegexOption.IGNORE_CASE)
    // `dst <- kind - src`
    private val relReverseRegex = Regex("""^\s*(\S+)\s*<-\s*(contains|copies|derives|satisfies|verifies|refines|traces)\s*-\s*(\S+)\s*$""", RegexOption.IGNORE_CASE)

    private fun parseElementType(name: String): RequirementElementType = when (name.lowercase()) {
        "requirement" -> RequirementElementType.Requirement
        "functionalrequirement" -> RequirementElementType.FunctionalRequirement
        "interfacerequirement" -> RequirementElementType.InterfaceRequirement
        "performancerequirement" -> RequirementElementType.PerformanceRequirement
        "physicalrequirement" -> RequirementElementType.PhysicalRequirement
        "designconstraint" -> RequirementElementType.DesignConstraint
        "element" -> RequirementElementType.Element
        else -> RequirementElementType.Requirement
    }

    private fun parseRelationKind(name: String): RequirementRelationKind = when (name.lowercase()) {
        "contains" -> RequirementRelationKind.Contains
        "copies" -> RequirementRelationKind.Copies
        "derives" -> RequirementRelationKind.Derives
        "satisfies" -> RequirementRelationKind.Satisfies
        "verifies" -> RequirementRelationKind.Verifies
        "refines" -> RequirementRelationKind.Refines
        "traces" -> RequirementRelationKind.Traces
        else -> RequirementRelationKind.Traces
    }

    override fun parse(lines: List<String>): ParseResult.RequirementDiagram {
        val boxes = mutableListOf<RequirementBox>()
        val relationships = mutableListOf<RequirementRelationship>()
        var direction = defaultDirection

        // A pending block being filled from its field lines until `}`.
        data class Pending(val type: RequirementElementType, val id: String, val label: String,
            var text: String = "", var risk: String = "", var verifyMethod: String = "", var docRef: String = "")
        var pending: Pending? = null

        lines.forEachIndexed { index, line ->
            // First line is the `requirement`/`requirementDiagram` header — skip it.
            if (index == 0) return@forEachIndexed
            if (line.isBlank() || line.startsWith("%%") || line.startsWith("#")) return@forEachIndexed

            // `direction X`
            directionRegex.matchEntire(line)?.let { match ->
                direction = when (match.groupValues[1].uppercase()) {
                    "LR" -> MermaidDirection.LeftRight
                    "RL" -> MermaidDirection.RightLeft
                    "BT" -> MermaidDirection.BottomTop
                    else -> MermaidDirection.TopDown
                }
                return@forEachIndexed
            }

            // Close a pending block.
            if (closeBraceRegex.matches(line)) {
                pending?.let { p ->
                    boxes.add(RequirementBox(
                        id = p.id, type = p.type, label = p.label,
                        text = p.text, risk = p.risk, verifyMethod = p.verifyMethod, docRef = p.docRef,
                    ))
                }
                pending = null
                return@forEachIndexed
            }

            // Field inside a block.
            pending?.let { p ->
                fieldRegex.matchEntire(line)?.let { match ->
                    val key = match.groupValues[1].lowercase()
                    val value = match.groupValues[2].trim()
                    when (key) {
                        "id" -> {}
                        "text" -> p.text = value
                        "risk" -> p.risk = value
                        "verifymethod" -> p.verifyMethod = value
                        "type" -> p.docRef = value // element "type" maps to the type/docRef slot
                        "docref" -> p.docRef = value
                    }
                    return@forEachIndexed
                }
            }

            // Open a new block: `<type> <name> {`.
            blockOpenRegex.matchEntire(line)?.let { match ->
                val type = parseElementType(match.groupValues[1])
                val id = match.groupValues[2].ifBlank { match.groupValues[3] }
                pending = Pending(type = type, id = id, label = id)
                return@forEachIndexed
            }

            // Relationship, forward: `src - kind -> dst`.
            relForwardRegex.matchEntire(line)?.let { match ->
                relationships.add(RequirementRelationship(
                    from = match.groupValues[1], to = match.groupValues[3],
                    kind = parseRelationKind(match.groupValues[2])))
                return@forEachIndexed
            }
            // Relationship, reverse: `dst <- kind - src` → from=src, to=dst.
            relReverseRegex.matchEntire(line)?.let { match ->
                relationships.add(RequirementRelationship(
                    from = match.groupValues[3], to = match.groupValues[1],
                    kind = parseRelationKind(match.groupValues[2])))
                return@forEachIndexed
            }
        }

        // A block left open (no closing brace) still commits its data.
        pending?.let { p ->
            boxes.add(RequirementBox(
                id = p.id, type = p.type, label = p.label,
                text = p.text, risk = p.risk, verifyMethod = p.verifyMethod, docRef = p.docRef,
            ))
        }

        return ParseResult.RequirementDiagram(
            direction = direction,
            boxes = boxes,
            relationships = relationships,
        )
    }
}
