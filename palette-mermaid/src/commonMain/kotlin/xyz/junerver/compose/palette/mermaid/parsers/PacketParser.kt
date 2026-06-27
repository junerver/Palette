package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.PacketField
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Network-packet parser. Mermaid syntax (keyword `packet`, alias `packet-beta`):
 * ```
 * packet
 * title TCP Header
 * 0-15: "Source Port"
 * 16-31: "Destination Port"
 * +16: "Checksum"        // relative form: 16 bits continuing from the previous field's end
 * ```
 *
 * Two field forms: explicit `start-end: "label"` and relative `+bits: "label"` (the parser
 * auto-computes `start` as the previous field's `end + 1`). Labels are double-quoted.
 */
internal object PacketParser : MermaidDiagramParser {
    override val keyword: String = "packet"
    override val aliases: List<String> = listOf("packet-beta")
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    private val titleRegex = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE)
    // Explicit range: `0-15: "label"` or `0: "label"` (single-bit, no `-end`).
    private val rangeRegex = Regex("""^(\d+)(?:\s*-\s*(\d+))?\s*:\s*"((?:[^"\\]|\\.)*)"\s*$""")
    // Relative: `+16: "label"`.
    private val relativeRegex = Regex("""^\+\s*(\d+)\s*:\s*"((?:[^"\\]|\\.)*)"\s*$""")

    override fun parse(lines: List<String>): ParseResult.PacketDiagram {
        var title: String? = null
        val fields = mutableListOf<PacketField>()
        // The next start bit for a relative field; advanced by every field (explicit or relative).
        var nextStart = 0

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed // skip the `packet` header
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("%%")) return@forEachIndexed

            titleRegex.matchEntire(trimmed)?.let { title = it.groupValues[1].trim(); return@forEachIndexed }
            rangeRegex.matchEntire(trimmed)?.let { match ->
                val start = match.groupValues[1].toInt()
                val end = match.groupValues[2].takeIf { it.isNotEmpty() }?.toInt() ?: start
                val label = match.groupValues[3]
                val bits = (end - start + 1).coerceAtLeast(1)
                fields.add(PacketField(label = label, startBit = start, endBit = end, bits = bits))
                nextStart = end + 1
                return@forEachIndexed
            }
            relativeRegex.matchEntire(trimmed)?.let { match ->
                val bits = match.groupValues[1].toInt().coerceAtLeast(1)
                val start = nextStart
                val end = start + bits - 1
                fields.add(PacketField(label = match.groupValues[2], startBit = start, endBit = end, bits = bits))
                nextStart = end + 1
                return@forEachIndexed
            }
        }

        return ParseResult.PacketDiagram(direction = defaultDirection, title = title, fields = fields)
    }
}
