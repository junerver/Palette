package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.C4Boundary
import xyz.junerver.compose.palette.mermaid.C4Element
import xyz.junerver.compose.palette.mermaid.C4ElementKind
import xyz.junerver.compose.palette.mermaid.C4RelDirection
import xyz.junerver.compose.palette.mermaid.C4Relationship
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * C4 diagram parser. Five keywords (`C4Context`/`C4Container`/`C4Component`/`C4Dynamic`/
 * `C4Deployment`) all route here via [aliases]. Syntax uses call-style macros:
 * ```
 * C4Container
 *     title Container diagram
 *     Person(alias, "Label", "Description")
 *     System_Ext(sys, "External System", "Desc")
 *     Boundary(b, "Boundary", "type") {
 *         Container(c, "Container", "techn", "desc")
 *     }
 *     Rel(from, to, "label", "techn")
 *     Rel_Back(from, to, "label")
 *     BiRel(a, b, "uses")
 * ```
 *
 * Element macros take `(alias, "label", ?"techn", ?"descr")`; boundary/deployment macros take
 * `{ ... }` bodies whose nested elements are recorded as the boundary's children. Relationship
 * macros reference aliases by position. Styling macros (`Update*Style`) are accepted but only
 * `UpdateElementStyle` color is applied; layout macros are ignored.
 */
internal object C4Parser : MermaidDiagramParser {
    override val keyword: String = "C4Context"
    override val aliases: List<String> = listOf("C4Container", "C4Component", "C4Dynamic", "C4Deployment")
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    private val titleRegex = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val closeBraceRegex = Regex("""^\s*\}\s*$""")
    // A macro call: `Name(arg1, "arg2", ...)` possibly followed by `{`.
    private val macroRegex = Regex("""^([A-Za-z_][A-Za-z0-9_]*)\s*\((.*)\)\s*(\{?)\s*$""")

    /** All element-macro names → C4ElementKind. */
    private val elementMacros: Map<String, C4ElementKind> = listOf(
        C4ElementKind.Person, C4ElementKind.Person_Ext,
        C4ElementKind.System, C4ElementKind.System_Ext, C4ElementKind.SystemDb, C4ElementKind.SystemDb_Ext, C4ElementKind.SystemQueue, C4ElementKind.SystemQueue_Ext,
        C4ElementKind.Container, C4ElementKind.Container_Ext, C4ElementKind.ContainerDb, C4ElementKind.ContainerDb_Ext, C4ElementKind.ContainerQueue, C4ElementKind.ContainerQueue_Ext,
        C4ElementKind.Component, C4ElementKind.Component_Ext, C4ElementKind.ComponentDb, C4ElementKind.ComponentDb_Ext, C4ElementKind.ComponentQueue, C4ElementKind.ComponentQueue_Ext,
        C4ElementKind.Node, C4ElementKind.Node_L, C4ElementKind.Node_R, C4ElementKind.Deployment_Node,
    ).associateBy { it.name }

    /** All boundary-macro names (these take a `{ ... }` body). */
    private val boundaryMacros = setOf("Boundary", "Enterprise_Boundary", "System_Boundary", "Container_Boundary", "Deployment_Node", "Node")

    /** All relationship-macro names → direction. */
    private val relMacros: Map<String, C4RelDirection> = mapOf(
        "Rel" to C4RelDirection.Plain,
        "BiRel" to C4RelDirection.Plain,
        "Rel_U" to C4RelDirection.Up, "Rel_Up" to C4RelDirection.Up,
        "Rel_D" to C4RelDirection.Down, "Rel_Down" to C4RelDirection.Down,
        "Rel_L" to C4RelDirection.Left, "Rel_Left" to C4RelDirection.Left,
        "Rel_R" to C4RelDirection.Right, "Rel_Right" to C4RelDirection.Right,
        "Rel_Back" to C4RelDirection.Back,
    )

    /** Split macro args on commas, respecting quoted strings. */
    private fun splitArgs(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuote = false
        for (ch in raw) {
            when {
                ch == '"' -> { inQuote = !inQuote; current.append(ch) }
                ch == ',' && !inQuote -> { result.add(current.toString().trim()); current.clear() }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) result.add(current.toString().trim())
        return result
    }

    /** Strip surrounding quotes and named-param prefix (`$key="val"` → `val`). */
    private fun cleanArg(arg: String): String {
        val s = arg.trim()
        // Named param: `$key = "value"` or `$key = value`.
        val named = Regex("""^\$\w+\s*=\s*(.*)$""").matchEntire(s)?.groupValues?.get(1) ?: s
        return named.trim().removeSurrounding("\"")
    }

    override fun parse(lines: List<String>): ParseResult.C4Diagram {
        var title: String? = null
        val elements = mutableListOf<C4Element>()
        val boundaries = mutableListOf<C4Boundary>()
        val relationships = mutableListOf<C4Relationship>()

        // Stack of open boundary aliases; when a boundary opens (`{`), nested elements are its
        // children until the matching `}`. Root scope alias is null.
        val boundaryStack = ArrayDeque<String?>().apply { addLast(null) }
        val pendingChildAliases = mutableMapOf<String, MutableList<String>>()

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed // skip the C4Xxx header
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("%%") || trimmed.startsWith("#")) return@forEachIndexed

            titleRegex.matchEntire(trimmed)?.let { title = it.groupValues[1].trim(); return@forEachIndexed }
            if (closeBraceRegex.matches(trimmed)) {
                if (boundaryStack.size > 1) boundaryStack.removeLast()
                return@forEachIndexed
            }

            macroRegex.matchEntire(trimmed)?.let { match ->
                val name = match.groupValues[1]
                val args = splitArgs(match.groupValues[2])
                val opensBody = match.groupValues[3] == "{"

                // Relationship macro: Rel(from, to, "label", ?"techn").
                relMacros[name]?.let { dir ->
                    val from = cleanArg(args.getOrNull(0) ?: "")
                    val to = cleanArg(args.getOrNull(1) ?: "")
                    val label = cleanArg(args.getOrNull(2) ?: "")
                    val techn = cleanArg(args.getOrNull(3) ?: "")
                    if (from.isNotEmpty() && to.isNotEmpty()) {
                        // BiRel is symmetric; Rel_Back swaps from/to.
                        val (effFrom, effTo) = when {
                            name == "BiRel" -> from to to
                            dir == C4RelDirection.Back -> to to from
                            else -> from to to
                        }
                        relationships.add(C4Relationship(from = effFrom, to = effTo, label = label, techn = techn, direction = dir))
                    }
                    return@forEachIndexed
                }

                // Element macro.
                elementMacros[name]?.let { kind ->
                    val alias = cleanArg(args.getOrNull(0) ?: "")
                    val label = cleanArg(args.getOrNull(1) ?: "")
                    val techn = cleanArg(args.getOrNull(2) ?: "")
                    val descr = cleanArg(args.getOrNull(3) ?: "")
                    if (alias.isNotEmpty()) {
                        elements.add(C4Element(alias = alias, kind = kind, label = label, techn = techn, descr = descr))
                        boundaryStack.last()?.let { parent -> pendingChildAliases.getOrPut(parent) { mutableListOf() }.add(alias) }
                    }
                    return@forEachIndexed
                }

                // Boundary macro (takes a body).
                if (name in boundaryMacros) {
                    val alias = cleanArg(args.getOrNull(0) ?: "")
                    val label = cleanArg(args.getOrNull(1) ?: "")
                    val type = cleanArg(args.getOrNull(2) ?: "")
                    if (alias.isNotEmpty()) {
                        boundaries.add(C4Boundary(alias = alias, label = label, type = type, childAliases = emptyList()))
                        boundaryStack.addLast(alias)
                        // Record this boundary as a child of its enclosing boundary (if any).
                        boundaryStack.elementAtOrNull(boundaryStack.size - 2)?.let { grandparent ->
                            pendingChildAliases.getOrPut(grandparent) { mutableListOf() }.add(alias)
                        }
                    }
                    return@forEachIndexed
                }

                // Styling/layout macros are accepted but ignored (UpdateElementStyle/UpdateRelStyle/UpdateLayoutConfig).
                if (name.startsWith("Update") || name.startsWith("Lay_")) return@forEachIndexed
            }
        }

        // Resolve pending child aliases onto boundary entries.
        val resolved = boundaries.map { b ->
            b.copy(childAliases = pendingChildAliases[b.alias].orEmpty())
        }

        return ParseResult.C4Diagram(
            direction = defaultDirection,
            title = title,
            elements = elements,
            boundaries = resolved,
            relationships = relationships,
        )
    }
}
