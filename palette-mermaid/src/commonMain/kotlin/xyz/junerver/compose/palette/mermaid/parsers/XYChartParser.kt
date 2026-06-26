package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.XySeries
import xyz.junerver.compose.palette.mermaid.XySeriesKind

/**
 * XY chart parser. Mermaid syntax:
 * ```
 * xychart / xychart-beta
 *     title "Quarterly revenue"
 *     x-axis <title> <min> --> <max>           (numeric range)
 *     x-axis <title> [cat, "cat two", cat3]    (categorical)
 *     y-axis <title> <min> --> <max>           (numeric range)
 *     line [1.2, 3, .5, -2.1]
 *     bar [4, 5, 6]
 * ```
 *
 * Both `xychart` and `xychart-beta` keywords are accepted (newer mermaid graduated off `-beta`).
 * Titles/categories may be bare single words or quoted (`"multi word"`). This parser only
 * handles body syntax; frontmatter-only options (chart orientation, data labels) are out of scope.
 */
internal object XYChartParser : MermaidDiagramParser {
    override val keyword: String = "xychart"
    override val aliases: List<String> = listOf("xychart-beta")
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown
    override val consumesHeaderLine: Boolean = true

    private val numRegex = Regex("""[+-]?(?:\d+\.?\d*|\.\d+)""")
    private val titleRegex = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val xAxisRegex = Regex("""^x-axis\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val yAxisRegex = Regex("""^y-axis\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val seriesRegex = Regex("""^(line|bar)\s+(.+)$""", RegexOption.IGNORE_CASE)

    /** Strip surrounding double quotes if present. */
    private fun unquote(text: String): String {
        val trimmed = text.trim()
        return if (trimmed.length >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            trimmed.removeSurrounding("\"")
        } else {
            trimmed
        }
    }

    /** Parse a bracketed numeric list like `[1.2, 3, .5]`. */
    private fun parseNumberList(text: String): List<Float> =
        numRegex.findAll(text).map { it.value.toFloatOrNull() ?: 0f }.toList()

    /** Parse a bracketed category list like `[cat, "cat two", cat3]` honoring quotes. */
    private fun parseCategoryList(text: String): List<String> {
        val inner = text.trim().removeSurrounding("[", "]").trim()
        if (inner.isEmpty()) return emptyList()
        return inner.split(",").map { unquote(it) }.filter { it.isNotEmpty() }
    }

    /**
     * Split an axis line body into (title, range?, categories?).
     *  - `"title" [a, b]` → categorical
     *  - `title 0 --> 100` / `title min --> max` → numeric range (non-numeric min/max default to 0/max-value)
     *  - `title` → bare (no range, no categories)
     */
    private data class AxisParse(
        val title: String,
        val range: Pair<Float, Float>?,
        val categories: List<String>,
    )

    private fun parseAxisBody(body: String): AxisParse {
        val trimmed = body.trim()
        // Categorical: a `[...]` block present.
        val bracketIndex = trimmed.indexOf('[')
        if (bracketIndex >= 0) {
            val titlePart = trimmed.substring(0, bracketIndex).trim()
            val listPart = trimmed.substring(bracketIndex)
            return AxisParse(title = unquote(titlePart), range = null, categories = parseCategoryList(listPart))
        }
        // Numeric range: "title <min> --> <max>". The min sits on the title side (after the title
        // text, before the arrow); the max is after the arrow.
        val arrow = trimmed.indexOf("-->")
        if (arrow >= 0) {
            val beforeArrow = trimmed.substring(0, arrow).trim()
            val afterArrow = trimmed.substring(arrow + 3).trim()
            // min = the last number token before the arrow; max = the first number token after.
            val beforeNums = numRegex.findAll(beforeArrow).mapNotNull { it.value.toFloatOrNull() }.toList()
            val afterNums = numRegex.findAll(afterArrow).mapNotNull { it.value.toFloatOrNull() }.toList()
            val min = beforeNums.lastOrNull() ?: 0f
            val max = afterNums.firstOrNull() ?: min
            // Title = the text before the arrow with the trailing min number stripped off.
            val cleanTitle = stripTrailingNumber(beforeArrow)
            return AxisParse(title = unquote(cleanTitle), range = min to max, categories = emptyList())
        }
        // Bare title only.
        return AxisParse(title = unquote(trimmed), range = null, categories = emptyList())
    }

    /** Remove a trailing numeric token from a title like "Revenue 0" → "Revenue". */
    private fun stripTrailingNumber(text: String): String {
        val m = Regex("""^(.*?)\s+[+-]?(?:\d+\.?\d*|\.\d+)\s*$""").matchEntire(text.trim()) ?: return text.trim()
        return m.groupValues[1].trim()
    }

    override fun parse(lines: List<String>): ParseResult.XYChartDiagram {
        var title: String? = null
        var xAxisTitle: String? = null
        var xAxisRange: Pair<Float, Float>? = null
        var xCategories: List<String> = emptyList()
        var yAxisTitle: String? = null
        var yAxisRange: Pair<Float, Float>? = null
        val series = mutableListOf<XySeries>()

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed
            if (line.isBlank() || line.startsWith("%%")) return@forEachIndexed

            titleRegex.matchEntire(line)?.let { title = unquote(it.groupValues[1]); return@forEachIndexed }
            xAxisRegex.matchEntire(line)?.let {
                val parsed = parseAxisBody(it.groupValues[1])
                xAxisTitle = parsed.title.takeIf { it.isNotEmpty() }
                xAxisRange = parsed.range
                xCategories = parsed.categories
                return@forEachIndexed
            }
            yAxisRegex.matchEntire(line)?.let {
                val parsed = parseAxisBody(it.groupValues[1])
                yAxisTitle = parsed.title.takeIf { it.isNotEmpty() }
                yAxisRange = parsed.range
                return@forEachIndexed
            }
            seriesRegex.matchEntire(line)?.let { match ->
                val kind = if (match.groupValues[1].equals("bar", ignoreCase = true)) XySeriesKind.Bar else XySeriesKind.Line
                series.add(XySeries(kind = kind, values = parseNumberList(match.groupValues[2])))
                return@forEachIndexed
            }
        }

        return ParseResult.XYChartDiagram(
            direction = defaultDirection,
            title = title,
            xAxisTitle = xAxisTitle,
            xAxisRange = xAxisRange,
            xCategories = xCategories,
            yAxisTitle = yAxisTitle,
            yAxisRange = yAxisRange,
            series = series,
        )
    }
}
