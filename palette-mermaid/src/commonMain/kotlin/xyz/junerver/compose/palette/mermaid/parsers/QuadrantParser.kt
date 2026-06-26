package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.QuadrantAxis
import xyz.junerver.compose.palette.mermaid.QuadrantPoint

/**
 * Quadrant chart parser. Mermaid syntax:
 * ```
 * quadrantChart
 *     title <Title>
 *     x-axis <low> --> <high>
 *     y-axis <low> --> <high>
 *     quadrant-1 <text>   (top-right)
 *     quadrant-2 <text>   (top-left)
 *     quadrant-3 <text>   (bottom-left)
 *     quadrant-4 <text>   (bottom-right)
 *     <label>: [x, y]            (x,y in [0,1])
 *     <label>: [x, y] radius: 12 color: #ff3300
 *     <label>:::className: [x, y]
 *     classDef className color: #109060, radius: 10
 * ```
 *
 * Only `color` and `radius` styling is carried through (other style keys are accepted but
 * ignored). x/y are clamped to `[0,1]`. Class styles are resolved after the body is parsed so a
 * `classDef` may appear before or after the points that reference it.
 */
internal object QuadrantParser : MermaidDiagramParser {
    override val keyword: String = "quadrantChart"
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown
    override val consumesHeaderLine: Boolean = true

    private val titleRegex = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val xAxisRegex = Regex("""^x-axis\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val yAxisRegex = Regex("""^y-axis\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val quadrantRegex = Regex("""^quadrant-([1-4])\s+(.+)$""", RegexOption.IGNORE_CASE)
    // A class definition: "classDef name key: val, key: val, ...".
    private val classDefRegex = Regex("""^classDef\s+(\S+)\s+(.+)$""", RegexOption.IGNORE_CASE)
    // A point: "<label>:::cls?: [x, y]" with optional trailing "key: val, ..." style props.
    // The label cannot contain "[" ; the coords are inside [...].
    private val pointRegex = Regex("""^(.+?)\s*(?:::([^\s:]+))?\s*:\s*\[\s*([0-9.]+)\s*,\s*([0-9.]+)\s*\]\s*(.*)$""")
    private val hexColorRegex = Regex("""#([0-9a-fA-F]{6})""")
    // Match a color value: a #rrggbb hex (stop at comma/space) or a bare CSS name. The hex
    // alternative is anchored so a trailing comma from a prop list isn't swallowed.
    private val colorPropRegex = Regex("""color\s*:\s*(#[0-9a-fA-F]{6}|[\w()]+)""", RegexOption.IGNORE_CASE)
    private val radiusPropRegex = Regex("""radius\s*:\s*([0-9.]+)""", RegexOption.IGNORE_CASE)
    private val numRegex = Regex("""[+-]?(?:\d+\.?\d*|\.\d+)""")

    /** Parse a hex `#rrggbb` to an ARGB UInt; returns null if not hex. */
    private fun parseHexColor(text: String): UInt? {
        val match = hexColorRegex.matchEntire(text) ?: return null
        return ("FF" + match.groupValues[1]).toUInt(16)
    }

    /** Extract (color, radius) from a trailing style fragment like "radius: 12, color: #ff3300". */
    private fun parseStyleProps(text: String): Pair<UInt?, Float?> {
        if (text.isBlank()) return null to null
        val colorVal = colorPropRegex.find(text)?.groupValues?.get(1)?.let { parseHexColor(it) }
        val radiusVal = radiusPropRegex.find(text)?.groupValues?.get(1)?.toFloatOrNull()
        return colorVal to radiusVal
    }

    private fun clamp01(v: Float): Float = v.coerceIn(0f, 1f)

    override fun parse(lines: List<String>): ParseResult.QuadrantChartDiagram {
        var title: String? = null
        var xAxis: QuadrantAxis? = null
        var yAxis: QuadrantAxis? = null
        val quadrantLabels = arrayOf("", "", "", "") // indices 0..3 = quadrants 1..4
        val classDefs = mutableMapOf<String, Pair<UInt?, Float?>>()

        // Points are collected as raw records (class ref unresolved) so a classDef declared AFTER
        // a point still resolves — mermaid allows either ordering.
        data class RawPoint(val label: String, val x: Float, val y: Float, val cls: String?, val inlineColor: UInt?, val inlineRadius: Float?)
        val rawPoints = mutableListOf<RawPoint>()

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed
            if (line.isBlank() || line.startsWith("%%")) return@forEachIndexed

            titleRegex.matchEntire(line)?.let { title = it.groupValues[1].trim(); return@forEachIndexed }
            // x-axis / y-axis: "<low> --> <high>" or a single "<label>".
            xAxisRegex.matchEntire(line)?.let { xAxis = parseAxis(it.groupValues[1]); return@forEachIndexed }
            yAxisRegex.matchEntire(line)?.let { yAxis = parseAxis(it.groupValues[1]); return@forEachIndexed }
            quadrantRegex.matchEntire(line)?.let { match ->
                val n = match.groupValues[1].toInt()
                quadrantLabels[n - 1] = match.groupValues[2].trim()
                return@forEachIndexed
            }
            classDefRegex.matchEntire(line)?.let { match ->
                classDefs[match.groupValues[1]] = parseStyleProps(match.groupValues[2])
                return@forEachIndexed
            }
            pointRegex.matchEntire(line)?.let { match ->
                val (inlineColor, inlineRadius) = parseStyleProps(match.groupValues[5])
                rawPoints.add(
                    RawPoint(
                        label = match.groupValues[1].trim(),
                        x = clamp01(match.groupValues[3].toFloatOrNull() ?: 0f),
                        y = clamp01(match.groupValues[4].toFloatOrNull() ?: 0f),
                        cls = match.groupValues[2].takeIf { it.isNotEmpty() },
                        inlineColor = inlineColor,
                        inlineRadius = inlineRadius,
                    ),
                )
                return@forEachIndexed
            }
        }

        // Resolve class styles (a classDef may have appeared before or after the point). Inline
        // style always wins over class style.
        val points = rawPoints.map { rp ->
            val (classColor, classRadius) = rp.cls?.let { classDefs[it] } ?: (null to null)
            QuadrantPoint(
                label = rp.label,
                x = rp.x,
                y = rp.y,
                color = rp.inlineColor ?: classColor,
                radius = rp.inlineRadius ?: classRadius,
            )
        }

        return ParseResult.QuadrantChartDiagram(
            direction = defaultDirection,
            title = title,
            xAxis = xAxis,
            yAxis = yAxis,
            quadrantLabels = quadrantLabels.toList(),
            points = points,
        )
    }

    /** `<low> --> <high>` → QuadrantAxis; a bare label → both ends identical. */
    private fun parseAxis(text: String): QuadrantAxis {
        val parts = text.split("-->").map { it.trim() }.filter { it.isNotEmpty() }
        return if (parts.size >= 2) QuadrantAxis(parts.first(), parts.last())
        else QuadrantAxis(text.trim(), text.trim())
    }
}
