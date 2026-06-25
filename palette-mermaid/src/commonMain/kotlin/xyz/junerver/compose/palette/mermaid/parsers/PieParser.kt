package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.PieSlice

/**
 * Pie chart parser. Mermaid syntax:
 * ```
 * pie [showData] [title <Title>]
 *     "<Label>" : <value>
 *     "<Label>" : <value>
 * ```
 * Labels may be quoted or bare; values are numeric. `showData` toggles rendering the
 * raw value alongside each slice.
 */
internal object PieParser : MermaidDiagramParser {
    override val keyword: String = "pie"
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown
    // `pie` may carry `showData`/`title` on the same line, so this parser needs the full source.
    override val consumesHeaderLine: Boolean = true

    // A slice line: an optional-quoted label, a colon, then a numeric value.
    private val sliceRegex = Regex("""^(?:"([^"]+)"|([^\s:]+))\s*:\s*([\d.]+)$""")

    override fun parse(lines: List<String>): ParseResult.PieDiagram {
        var title: String? = null
        var showData = false
        val slices = mutableListOf<PieSlice>()

        lines.forEachIndexed { index, line ->
            // First line is the `pie` header, possibly with inline `showData` / `title X`.
            if (index == 0) {
                val afterPie = line.substringAfter("pie", "").trim()
                if (afterPie.isNotEmpty()) {
                    val tokens = afterPie.split(Regex("\\s+"), limit = 2)
                    if (tokens.firstOrNull()?.equals("showData", ignoreCase = true) == true) {
                        showData = true
                        // A title may follow showData: "pie showData title X".
                        val afterShowData = afterPie.substringAfter("showData", "").trim()
                        val titleOnHeader = extractInlineTitle(afterShowData)
                        if (titleOnHeader.isNotEmpty()) title = titleOnHeader
                    } else {
                        val titleOnHeader = extractInlineTitle(afterPie)
                        if (titleOnHeader.isNotEmpty()) title = titleOnHeader
                    }
                }
                return@forEachIndexed
            }

            // Body-level `showData` flag (some formatters place it on its own line).
            if (line.equals("showData", ignoreCase = true)) {
                showData = true
                return@forEachIndexed
            }

            // Body-level `title <text>`.
            val titleMatch = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE).matchEntire(line)
            if (titleMatch != null) {
                title = titleMatch.groupValues[1].trim()
                return@forEachIndexed
            }

            sliceRegex.matchEntire(line)?.let { match ->
                val label = match.groupValues[1].ifEmpty { match.groupValues[2] }
                val value = match.groupValues[3].toDoubleOrNull() ?: return@forEachIndexed
                slices.add(PieSlice(label = label, value = value))
            }
        }

        return ParseResult.PieDiagram(
            direction = defaultDirection,
            title = title,
            slices = slices,
            showData = showData,
        )
    }

    /** Case-insensitively extract text following a `title` keyword on the header line. */
    private fun extractInlineTitle(source: String): String {
        val match = Regex("""title\s+(.+)""", RegexOption.IGNORE_CASE).find(source) ?: return ""
        return match.groupValues[1].trim()
    }
}
