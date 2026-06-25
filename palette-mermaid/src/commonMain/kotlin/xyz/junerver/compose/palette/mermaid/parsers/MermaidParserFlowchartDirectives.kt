package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidFlowchartClassAssignment
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartClassDef
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartClick
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartLinkStyle
import xyz.junerver.compose.palette.mermaid.MermaidFlowchartNodeStyle

/**
 * Flowchart style-directive parsers, extracted verbatim from the old
 * `MermaidParser` private helpers. Kept as a stateless object so [FlowchartParser]
 * can reuse them without duplicating the regexes.
 */
internal object MermaidParserFlowchartDirectives {
    fun parseClassDef(line: String): MermaidFlowchartClassDef? {
        val match = Regex("^classDef\\s+(\\S+)\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartClassDef(
            name = match.groupValues[1],
            declarations = match.groupValues[2].trim(),
            line = 0,
        )
    }

    fun parseClassAssignment(line: String): MermaidFlowchartClassAssignment? {
        val match = Regex("^class\\s+(\\S+)\\s+(\\S+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartClassAssignment(
            nodeIds = match.groupValues[1].split(",").map { it.trim() },
            className = match.groupValues[2],
            line = 0,
        )
    }

    fun parseNodeStyle(line: String): MermaidFlowchartNodeStyle? {
        val match = Regex("^style\\s+(\\S+)\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartNodeStyle(
            nodeId = match.groupValues[1],
            declarations = match.groupValues[2].trim(),
            line = 0,
        )
    }

    fun parseLinkStyle(line: String): MermaidFlowchartLinkStyle? {
        val match = Regex("^linkStyle\\s+([\\d,\\s]+)\\s+(.+)$", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartLinkStyle(
            edgeIndexes = match.groupValues[1].split(",").mapNotNull { it.trim().toIntOrNull() },
            declarations = match.groupValues[2].trim(),
            line = 0,
        )
    }

    fun parseClick(line: String): MermaidFlowchartClick? {
        val match = Regex("""^click\s+(\S+)\s+"([^"]*)"(?:\s+"([^"]*)")?\s*$""", RegexOption.IGNORE_CASE).matchEntire(line) ?: return null
        return MermaidFlowchartClick(
            nodeId = match.groupValues[1],
            href = match.groupValues[2].trim().ifEmpty { null },
            tooltip = match.groupValues.getOrNull(3)?.trim()?.ifEmpty { null },
            line = 0,
        )
    }
}
