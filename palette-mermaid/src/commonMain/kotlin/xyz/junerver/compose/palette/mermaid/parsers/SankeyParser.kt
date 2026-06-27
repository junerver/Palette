package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.SankeyFlow

/**
 * Sankey parser. Mermaid syntax (keyword `sankey`, alias `sankey-beta`) is RFC-4180-ish CSV with
 * exactly 3 columns: `source,target,value`. Nodes are implied by appearance (no separate node
 * definitions). Blank lines (no commas) and `%%` comment lines are skipped. Double-quoted fields
 * support `""` escapes and may contain commas.
 */
internal object SankeyParser : MermaidDiagramParser {
    override val keyword: String = "sankey"
    override val aliases: List<String> = listOf("sankey-beta")
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    private val numRegex = Regex("""[+-]?(?:\d+\.?\d*|\.\d+)""")

    /**
     * Split a CSV line into fields, honoring double-quoted fields (with `""` escapes) and
     * commas inside quotes. Returns null if the line isn't a 3-column data row (e.g. blank or
     * comment).
     */
    private fun parseCsvLine(line: String): List<String>? {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"'); i += 2; continue
                    }
                    inQuotes = !inQuotes
                }
                ch == ',' && !inQuotes -> { fields.add(current.toString().trim()); current.clear() }
                else -> current.append(ch)
            }
            i++
        }
        fields.add(current.toString().trim())
        return fields
    }

    override fun parse(lines: List<String>): ParseResult.SankeyDiagram {
        val flows = mutableListOf<SankeyFlow>()

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed // skip the `sankey` header
            // Skip blank lines and comments.
            if (line.isBlank() || line.trim().startsWith("%%")) return@forEachIndexed

            val fields = parseCsvLine(line) ?: return@forEachIndexed
            if (fields.size < 3) return@forEachIndexed
            val source = fields[0]
            val target = fields[1]
            // The value is the first numeric token in the 3rd column (tolerates trailing text).
            val value = numRegex.find(fields[2])?.value?.toFloatOrNull() ?: return@forEachIndexed
            if (source.isEmpty() || target.isEmpty()) return@forEachIndexed
            flows.add(SankeyFlow(source = source, target = target, value = value))
        }

        return ParseResult.SankeyDiagram(direction = defaultDirection, flows = flows)
    }
}
