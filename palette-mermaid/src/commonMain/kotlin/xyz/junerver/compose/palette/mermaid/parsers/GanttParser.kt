package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.GanttConfig
import xyz.junerver.compose.palette.mermaid.GanttSection
import xyz.junerver.compose.palette.mermaid.GanttTask
import xyz.junerver.compose.palette.mermaid.GanttTaskStatus
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Gantt chart parser. Mermaid syntax:
 * ```
 * gantt
 *     title <Title>
 *     dateFormat YYYY-MM-DD
 *     axisFormat  %Y-%m-%d
 *     excludes    weekends
 *     section <Name>
 *         Task title   :<id>, <start>, <duration|end>
 *         Another      :done, active, after <id>, 5d
 * ```
 *
 * Metadata after the colon is comma-separated: optional status tags (`done`/`active`/
 * `crit`/`milestone`) first, then up to three positional items (id, start, end). `after X`
 * introduces a dependency. Duration tokens (`30d`, `4h`, `2w`, …) are parsed to days.
 *
 * This core implementation captures the parsed structure verbatim; concrete date math and
 * axis formatting happen in the renderer.
 */
internal object GanttParser : MermaidDiagramParser {
    override val keyword: String = "gantt"
    override val defaultDirection: MermaidDirection = MermaidDirection.LeftRight
    override val consumesHeaderLine: Boolean = true

    // A duration token: a number (optionally decimal) + a unit suffix.
    private val durationRegex = Regex("""^(\d+(?:\.\d+)?)(ms|s|m|h|d|w|M|y)$""")

    override fun parse(lines: List<String>): ParseResult.GanttDiagram {
        var title: String? = null
        var dateFormat: String? = null
        var axisFormat: String? = null
        val excludes = mutableListOf<String>()
        val sections = mutableListOf<GanttSection>()
        var currentTasks = mutableListOf<GanttTask>()

        fun flushSection(currentName: String?) {
            if (currentName != null && currentTasks.isNotEmpty()) {
                sections.add(GanttSection(name = currentName, tasks = currentTasks.toList()))
            }
            currentTasks = mutableListOf()
        }
        var currentSectionName: String? = null

        lines.forEachIndexed { index, line ->
            // The `gantt` header line itself — ignore.
            if (index == 0 && line.equals("gantt", ignoreCase = true)) return@forEachIndexed

            // Header-region directives.
            when {
                line.startsWith("title ", ignoreCase = true) -> {
                    title = line.substringAfter("title ").trim()
                    return@forEachIndexed
                }
                line.startsWith("dateFormat ", ignoreCase = true) -> {
                    dateFormat = line.substringAfter("dateFormat ").trim()
                    return@forEachIndexed
                }
                line.startsWith("axisFormat ", ignoreCase = true) -> {
                    axisFormat = line.substringAfter("axisFormat ").trim()
                    return@forEachIndexed
                }
                line.startsWith("excludes ", ignoreCase = true) -> {
                    excludes.add(line.substringAfter("excludes ").trim())
                    return@forEachIndexed
                }
            }

            // Section start.
            if (line.startsWith("section ", ignoreCase = true)) {
                flushSection(currentSectionName)
                currentSectionName = line.substringAfter("section ").trim()
                return@forEachIndexed
            }

            // Task line: <title> : <metadata>
            val colonIdx = line.indexOf(':')
            if (colonIdx < 0) return@forEachIndexed
            val taskTitle = line.substring(0, colonIdx).trim()
            if (taskTitle.isEmpty()) return@forEachIndexed
            val metadata = line.substring(colonIdx + 1).trim()
            val task = parseTask(taskTitle, metadata) ?: return@forEachIndexed
            currentTasks.add(task)
        }

        flushSection(currentSectionName)
        // Tasks declared before any section go into an implicit empty-named section.
        if (sections.isEmpty() && currentTasks.isNotEmpty()) {
            sections.add(GanttSection(name = "", tasks = currentTasks.toList()))
        }

        return ParseResult.GanttDiagram(
            direction = defaultDirection,
            config = GanttConfig(
                title = title,
                dateFormat = dateFormat,
                axisFormat = axisFormat,
                excludes = excludes.toList(),
            ),
            sections = sections,
        )
    }

    private fun parseTask(title: String, metadata: String): GanttTask? {
        val parts = metadata.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        var status = GanttTaskStatus.Todo
        var isCritical = false
        var isMilestone = false
        val positional = mutableListOf<String>()

        for (part in parts) {
            when (part.lowercase()) {
                "done" -> status = GanttTaskStatus.Done
                "active" -> status = GanttTaskStatus.Active
                "crit" -> isCritical = true
                "milestone" -> isMilestone = true
                else -> positional.add(part)
            }
        }

        // Interpret the positional items. Up to 3 are allowed: [id], start, end.
        // We distinguish the id (first item, only if there are 3 items) from start/end.
        var id: String? = null
        var startToken: String? = null
        var endToken: String? = null
        when {
            positional.size >= 3 -> {
                id = positional[0]
                startToken = positional[1]
                endToken = positional[2]
            }
            positional.size == 2 -> {
                startToken = positional[0]
                endToken = positional[1]
            }
            positional.size == 1 -> endToken = positional[0]
        }

        // Resolve `after a b` dependency syntax.
        var dependsOn = emptyList<String>()
        if (startToken != null && startToken.startsWith("after ", ignoreCase = true)) {
            dependsOn = startToken.substringAfter("after ").trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            startToken = "after"
        }

        val durationDays = endToken?.let { parseDurationDays(it) }

        return GanttTask(
            id = id,
            title = title,
            status = status,
            isCritical = isCritical,
            isMilestone = isMilestone,
            startToken = startToken,
            endToken = endToken,
            durationDays = durationDays,
            dependsOn = dependsOn,
        )
    }

    /** Convert a duration token (e.g. "30d", "4h", "2w") to its length in days; null if not a duration. */
    private fun parseDurationDays(token: String): Double? {
        val match = durationRegex.matchEntire(token) ?: return null
        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        return when (match.groupValues[2]) {
            "ms" -> value / 86_400_000
            "s" -> value / 86_400
            "m" -> value / 1440
            "h" -> value / 24
            "d" -> value
            "w" -> value * 7
            "M" -> value * 30
            "y" -> value * 365
            else -> null
        }
    }
}
