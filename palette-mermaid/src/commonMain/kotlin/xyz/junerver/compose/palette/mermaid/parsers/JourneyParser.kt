package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.JourneySection
import xyz.junerver.compose.palette.mermaid.JourneyTask
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * User-journey parser. Mermaid syntax (keyword `journey`, no `-beta`):
 * ```
 * journey
 *     title My working day
 *     section Go to work
 *       Make tea: 5: Me
 *       Do work: 1: Me, Cat
 *     section Go home
 *       Sit down: 5: Me
 * ```
 *
 * A task line has exactly two colons: `Task name : <1-5 score> : <comma,separated,actors>`.
 */
internal object JourneyParser : MermaidDiagramParser {
    override val keyword: String = "journey"
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    private val titleRegex = Regex("""^title\s+(.+)$""", RegexOption.IGNORE_CASE)
    private val sectionRegex = Regex("""^section\s+(.+)$""", RegexOption.IGNORE_CASE)
    // `Task name : score : actor, actor` — score is matched loosely and clamped to [1,5].
    private val taskRegex = Regex("""^(.+?)\s*:\s*(-?\d+)\s*:\s*(.+)$""")

    override fun parse(lines: List<String>): ParseResult.JourneyDiagram {
        var title: String? = null
        val sections = mutableListOf<JourneySection>()
        // Tasks accumulate into the current section.
        var currentTitle = ""
        var currentTasks = mutableListOf<JourneyTask>()

        lines.forEachIndexed { index, line ->
            if (index == 0) return@forEachIndexed // skip the `journey` header
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("%%")) return@forEachIndexed

            titleRegex.matchEntire(trimmed)?.let { title = it.groupValues[1].trim(); return@forEachIndexed }
            sectionRegex.matchEntire(trimmed)?.let {
                // Commit the previous section if it has tasks.
                if (currentTasks.isNotEmpty()) {
                    sections.add(JourneySection(currentTitle, currentTasks))
                }
                currentTitle = it.groupValues[1].trim()
                currentTasks = mutableListOf()
                return@forEachIndexed
            }
            taskRegex.matchEntire(trimmed)?.let { match ->
                val name = match.groupValues[1].trim()
                val score = match.groupValues[2].toIntOrNull()?.coerceIn(1, 5) ?: 3
                val actors = match.groupValues[3].split(",").map { it.trim() }.filter { it.isNotEmpty() }
                currentTasks.add(JourneyTask(name = name, score = score, actors = actors))
                return@forEachIndexed
            }
        }
        // Commit the final section.
        if (currentTasks.isNotEmpty()) sections.add(JourneySection(currentTitle, currentTasks))

        return ParseResult.JourneyDiagram(direction = defaultDirection, title = title, sections = sections)
    }
}
