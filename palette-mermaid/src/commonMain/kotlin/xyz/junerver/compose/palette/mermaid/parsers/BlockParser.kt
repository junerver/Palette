package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.BlockContainer
import xyz.junerver.compose.palette.mermaid.BlockEdge
import xyz.junerver.compose.palette.mermaid.BlockNode
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidEdgeArrow
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Block diagram parser. Mermaid syntax (keyword `block` or `block-beta`):
 * ```
 * block-beta
 *   columns 3
 *   a:3
 *   block:group1:2
 *     columns 2
 *     h i j k
 *   end
 *   space
 *   D["Label"]
 *   A-- "edge label" -->B
 * ```
 *
 * Grid model: nodes occupy a flat sequence of cells; `columns N` sets the grid width for the
 * current scope; `block:NAME { ... end }` nests a sub-grid; `space`/`space:N` reserve empty
 * cells; `id:N` spans N columns; `id["Label"]` sets a label+shape. Edges use flowchart-style
 * arrows with an optional mid-arrow label.
 */
internal object BlockParser : MermaidDiagramParser {
    override val keyword: String = "block"
    override val aliases: List<String> = listOf("block-beta")
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown
    override val consumesHeaderLine: Boolean = true

    private val columnsRegex = Regex("""^columns\s+(auto|-?\d+)\s*$""", RegexOption.IGNORE_CASE)
    private val spaceRegex = Regex("""^space(?::(\d+))?\s*$""", RegexOption.IGNORE_CASE)
    private val blockOpenRegex = Regex("""^block(?::([A-Za-z_][\w-]*))?(?::(\d+))?\s*$""", RegexOption.IGNORE_CASE)
    private val endRegex = Regex("""^end\s*$""", RegexOption.IGNORE_CASE)
    // A node token: `id`, `id["label"]`, `id(("circle"))`, etc. Optionally with a `:N` span suffix.
    private val nodeTokenRegex = Regex(
        """^([A-Za-z_][\w-]*)\s*(\[[^\]]*\]|\([^()]*\)|\{[^}]*\}|[A-Za-z_]*)?\s*(?::(\d+))?\s*$""",
    )
    // A block edge: `<from> <connector> <to>`. The connector is a sequence of dash/equal chars
    // optionally interrupted by a `"label"`, and flanked by optional `o`/`x`/`<`/`>` end glyphs.
    // e.g. `-->`, `<-->`, `o--o`, `x--x`, `-- "lbl" -->`, `==>`. We capture: from, the whole
    // connector string, the optional label, and to; the connector's leading/trailing glyph drives
    // the marker mapping.
    private val edgeRegex = Regex(
        """^\s*([A-Za-z_][\w-]*)\s*""" + // 1: from id
            """([xo<])?\s*([-=]+)\s*""" + // 2: optional left glyph, 3: dash/equal run (left of label)
            """(?:"([^"]*)"\s*)?""" + // 4: optional mid-arrow label
            """([-=]*)\s*""" + // 5: optional trailing dash/equal run (right of label)
            """([xo>])?\s*""" + // 6: optional right/head glyph
            """([A-Za-z_][\w-]*)\s*$""", // 7: to id
    )

    /** Tracks an open `block { ... }` scope (the root is an implicit scope). */
    private data class Scope(val id: String?, val label: String?, var columns: Int, val childIds: MutableList<String>)

    private fun parseShape(delimiter: String?): MermaidNodeShape = when {
        delimiter == null -> MermaidNodeShape.Rounded
        delimiter.startsWith("[(") -> MermaidNodeShape.Database
        delimiter.startsWith("[[") -> MermaidNodeShape.Subroutine
        delimiter.startsWith("((") -> MermaidNodeShape.Circle
        delimiter.startsWith("([") -> MermaidNodeShape.Stadium
        delimiter.startsWith("{") -> MermaidNodeShape.Diamond
        delimiter.startsWith("(") -> MermaidNodeShape.Rounded
        delimiter.startsWith("[") -> MermaidNodeShape.Rectangle
        else -> MermaidNodeShape.Rounded
    }

    /** Extract the label text from a shape delimiter like `["Label"]` or `(("DB"))`. */
    private fun parseLabel(delimiter: String?): String? {
        if (delimiter == null) return null
        val inner = delimiter.trim('[', ']', '(', ')', '{', '}').trim().removeSurrounding("\"")
        return inner.ifBlank { null }
    }

    override fun parse(lines: List<String>): ParseResult.BlockDiagram {
        val nodes = mutableListOf<BlockNode>()
        val edges = mutableListOf<BlockEdge>()
        val containers = mutableListOf<BlockContainer>()
        var columns = 1

        // Root scope + stack of nested scopes.
        val root = Scope(id = null, label = null, columns = 1, childIds = mutableListOf())
        val stack = ArrayDeque<Scope>().apply { addLast(root) }

        fun current(): Scope = stack.last()

        lines.forEachIndexed { index, line ->
            // The first line is the `block`/`block-beta` header — skip it.
            if (index == 0) return@forEachIndexed
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("%%")) return@forEachIndexed

            // `columns N` sets the column count for the current scope.
            columnsRegex.matchEntire(trimmed)?.let { match ->
                val n = match.groupValues[1]
                val cols = if (n.equals("auto", ignoreCase = true)) 1 else n.toIntOrNull()?.coerceAtLeast(1) ?: 1
                current().columns = cols
                if (stack.size == 1) columns = cols // record the top-level width
                return@forEachIndexed
            }

            // `block:NAME?:N?` opens a nested composite scope.
            blockOpenRegex.matchEntire(trimmed)?.let { match ->
                val name = match.groupValues[1].ifBlank { "block${containers.size + 1}" }
                val span = match.groupValues[2].toIntOrNull() ?: 1
                val scope = Scope(id = name, label = name, columns = 1, childIds = mutableListOf())
                stack.addLast(scope)
                current().takeIf { it !== root }?.let { /* parent will record child id below */ }
                // Record this block's id in the parent scope AND pre-register a container entry.
                root.childIds.takeIf { stack.size == 2 }?.add(name)
                containers.add(BlockContainer(id = name, label = name, columns = 1, childIds = mutableListOf()))
                // Track the pending container for child accumulation; span is recorded on the node side.
                // (The composite's node is added as a placeholder BlockNode so the layout can size it.)
                nodes.add(BlockNode(id = name, label = name, shape = MermaidNodeShape.Rectangle, columnSpan = span))
                return@forEachIndexed
            }

            // `end` closes the current nested scope.
            if (endRegex.matches(trimmed)) {
                if (stack.size > 1) {
                    val closed = stack.removeLast()
                    // Attach the closed scope's children to its container entry (matched by id).
                    val idx = containers.indexOfFirst { it.id == closed.id }
                    if (idx >= 0) containers[idx] = containers[idx].copy(columns = closed.columns, childIds = closed.childIds)
                }
                return@forEachIndexed
            }

            // `space` / `space:N` reserves empty cells — skip (grid layout accounts for gaps via order).
            if (spaceRegex.matches(trimmed)) return@forEachIndexed

            // An edge between two nodes.
            edgeRegex.matchEntire(trimmed)?.let { match ->
                val from = match.groupValues[1]
                val leftGlyph = match.groupValues[2] // optional o/x/<
                val runLeft = match.groupValues[3] // dash/equal run (left of label)
                val lbl = match.groupValues[4]
                val runRight = match.groupValues[5] // optional trailing run (right of label)
                val headGlyph = match.groupValues[6] // optional o/x/>
                val to = match.groupValues[7]
                val run = runLeft + runRight
                val isDouble = run.contains("=") // ==> thick
                val hasHead = headGlyph.isNotEmpty()
                val hasLeft = leftGlyph.isNotEmpty()
                val style = if (isDouble) MermaidEdgeStyle.Thick else MermaidEdgeStyle.Solid
                val arrow = when {
                    leftGlyph == "o" || headGlyph == "o" -> MermaidEdgeArrow.Circle
                    leftGlyph == "x" || headGlyph == "x" -> MermaidEdgeArrow.Cross
                    hasLeft && hasHead -> MermaidEdgeArrow.Bidirectional
                    else -> MermaidEdgeArrow.Forward
                }
                // `<--` (left glyph, no head): the second id is the source.
                val (effFrom, effTo) = if (hasLeft && !hasHead) to to from else from to to
                edges.add(BlockEdge(from = effFrom, to = effTo, label = lbl.ifBlank { null }, style = style, arrow = arrow))
                return@forEachIndexed
            }

            // Otherwise: one or more whitespace-separated node tokens (e.g. `h i j k` or `D["Label"]:2`).
            // Split on whitespace but respect bracketed labels (don't split inside [...]).
            val tokens = tokenizeNodeLine(trimmed)
            tokens.forEach { token ->
                nodeTokenRegex.matchEntire(token)?.let { match ->
                    val (id, delim, spanStr) = match.destructured
                    val span = spanStr.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    val label = parseLabel(delim) ?: id
                    val shape = parseShape(if (delim.isBlank()) null else delim)
                    nodes.add(BlockNode(id = id, label = label, shape = shape, columnSpan = span))
                    current().childIds.add(id)
                }
            }
        }

        return ParseResult.BlockDiagram(
            direction = defaultDirection,
            nodes = nodes,
            edges = edges,
            containers = containers,
            columns = root.columns,
        )
    }

    /**
     * Split a node line into tokens, keeping bracketed labels attached to their ids.
     * `D["Label"]:2` stays one token; `h i j k` splits into four.
     */
    private fun tokenizeNodeLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val current = StringBuilder()
        var depth = 0
        for (ch in line) {
            when (ch) {
                '[', '(', '{' -> { depth++; current.append(ch) }
                ']', ')', '}' -> { depth = (depth - 1).coerceAtLeast(0); current.append(ch) }
                ' ', '\t' -> {
                    if (depth > 0) current.append(ch) else if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() }
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) tokens.add(current.toString())
        return tokens
    }
}
