package xyz.junerver.compose.palette.mermaid

import xyz.junerver.compose.palette.mermaid.parsers.ClassDiagramParser
import xyz.junerver.compose.palette.mermaid.parsers.ErDiagramParser
import xyz.junerver.compose.palette.mermaid.parsers.FlowchartParser
import xyz.junerver.compose.palette.mermaid.parsers.GanttParser
import xyz.junerver.compose.palette.mermaid.parsers.GitGraphParser
import xyz.junerver.compose.palette.mermaid.parsers.PieParser
import xyz.junerver.compose.palette.mermaid.parsers.SequenceParser
import xyz.junerver.compose.palette.mermaid.parsers.StateDiagramParser

object MermaidParser {
    /**
     * Registered diagram parsers, keyed by lowercased header keyword (including aliases).
     * A header matching an entry short-circuits to that parser's [MermaidDiagramParser.parse];
     * anything else falls through to the flowchart path below.
     */
    private val registeredParsers: Map<String, MermaidDiagramParser> =
        listOf(StateDiagramParser, ErDiagramParser, ClassDiagramParser, SequenceParser, PieParser, GanttParser, GitGraphParser)
            .flatMap { parser -> (parser.aliases + parser.keyword).map { it.lowercase() to parser } }
            .toMap()

    fun parse(source: String): MermaidDiagram {
        val lines = source
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("%%") }

        // Dispatcher: route to a dedicated parser for each diagram family. The header's
        // first keyword decides the family (e.g. "pie title X" -> pie). Adding a new
        // diagram type is a "register a parser" operation; this function stays a thin
        // dispatcher. Flowchart doubles as the default fallback.
        val firstWord = lines.firstOrNull()?.substringBefore(' ')?.lowercase()
        val handler =
            if (firstWord in setOf("flowchart", "graph")) {
                FlowchartParser
            } else {
                firstWord?.let { registeredParsers[it] }
            }
        return when (handler) {
            null -> FlowchartParser.parse(lines).toMermaidDiagram() // headerless fallback
            FlowchartParser -> FlowchartParser.parse(lines).toMermaidDiagram() // keeps the header
            else -> {
                val body = if (handler!!.consumesHeaderLine) lines else lines.drop(1)
                handler.parse(body).toMermaidDiagram()
            }
        }
    }
}
