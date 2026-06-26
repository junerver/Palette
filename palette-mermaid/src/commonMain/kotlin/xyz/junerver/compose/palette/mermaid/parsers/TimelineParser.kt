package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult
import xyz.junerver.compose.palette.mermaid.TimelinePeriod

/**
 * Timeline parser. Mermaid syntax:
 * ```
 * timeline [LR|TD]
 *     title <Title>
 *     section <Section name>
 *         <time period> : <event> [: <event>]
 *         <time period> : <event>
 *                       : <continuation event>
 * ```
 *
 * The optional `LR`/`TD` direction token on the header line is accepted for compatibility but
 * does not affect rendering (timelines always flow left→right). Periods with no preceding
 * `section` are placed in an empty-name section. Events may be chained inline with `:` and/or
 * continued on lines beginning with `:`.
 */
internal object TimelineParser : MermaidDiagramParser {
    override val keyword: String = "timeline"
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    // The header may carry an inline `LR`/`TD` direction token.
    override val consumesHeaderLine: Boolean = true

    private val titleRegex = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val sectionRegex = Regex("""^section\s+(.+)$""", RegexOption.IGNORE_CASE)
    // A continuation line: leading colon then an event.
    private val continuationRegex = Regex("""^:\s*(.+)$""")
    // A period line: "<period> : <event> [: <event>]*" — period is everything before the first
    // " : ", the rest splits into events on " : ".
    private val periodRegex = Regex("""^(.+?)\s*:\s*(.+)$""")

    override fun parse(lines: List<String>): ParseResult.TimelineDiagram {
        var title: String? = null
        var currentSection = ""
        val periods = mutableListOf<TimelinePeriod>()
        // The events accumulated for the most recent period (so continuation lines append to it).
        var pendingEvents: MutableList<String>? = null

        lines.forEachIndexed { index, line ->
            // First line is the `timeline` header (possibly with LR/TD); skip it.
            if (index == 0) return@forEachIndexed
            // Skip blank / comment lines.
            if (line.isBlank() || line.startsWith("%%")) return@forEachIndexed

            titleRegex.matchEntire(line)?.let {
                title = it.groupValues[1].trim()
                return@forEachIndexed
            }
            sectionRegex.matchEntire(line)?.let {
                currentSection = it.groupValues[1].trim()
                pendingEvents = null
                return@forEachIndexed
            }
            // Continuation: append an event to the current period (if any).
            continuationRegex.matchEntire(line)?.let {
                pendingEvents?.add(it.groupValues[1].trim())
                return@forEachIndexed
            }
            // A new period line.
            periodRegex.matchEntire(line)?.let { match ->
                val time = match.groupValues[1].trim()
                // Everything after the first ":" splits into events on ":".
                val events = match.groupValues[2].split(":").map { it.trim() }.filter { it.isNotEmpty() }
                val bucket = events.toMutableList()
                periods.add(TimelinePeriod(section = currentSection, time = time, events = bucket))
                pendingEvents = bucket
                return@forEachIndexed
            }
        }

        return ParseResult.TimelineDiagram(
            direction = defaultDirection,
            title = title,
            periods = periods,
        )
    }
}
