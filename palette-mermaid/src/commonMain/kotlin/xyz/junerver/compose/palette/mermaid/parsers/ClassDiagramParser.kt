package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.MermaidClassDefinition
import xyz.junerver.compose.palette.mermaid.MermaidClassMember
import xyz.junerver.compose.palette.mermaid.MermaidClassMemberKind
import xyz.junerver.compose.palette.mermaid.MermaidClassRelationType
import xyz.junerver.compose.palette.mermaid.MermaidClassRelationship
import xyz.junerver.compose.palette.mermaid.MermaidClassVisibility
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * Class diagram parser. Extracted verbatim from the old monolithic `MermaidParser.parse()`
 * class branch: class blocks (single-line and multi-line), members with
 * visibility/abstract/static modifiers, annotations, and UML relationships.
 */
internal object ClassDiagramParser : MermaidDiagramParser {
    override val keyword: String = "classDiagram"
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown

    // Relationship patterns in priority order; first match wins.
    private val relationshipPatterns = listOf(
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(<\|--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(<\|\.\.)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(\*--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(o--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(-->)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(--)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(\.\.>)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
        Regex("""^(\S+)(?:\s+"([^"]*)")?\s+(\.\.)\s+(?:"([^"]*)"\s+)?(\S+)(?:\s*:\s*(.+))?$"""),
    )

    override fun parse(lines: List<String>): ParseResult.ClassDiagram {
        val classDefinitions = mutableListOf<MermaidClassDefinition>()
        val classRelationships = mutableListOf<MermaidClassRelationship>()
        var currentClassId: String? = null
        var currentClassMembers = mutableListOf<MermaidClassMember>()
        var currentClassAnnotation: String? = null
        var currentClassName: String? = null

        fun flushCurrentClass() {
            val id = currentClassId ?: return
            classDefinitions.add(
                MermaidClassDefinition(
                    id = id,
                    label = currentClassName ?: id,
                    annotation = currentClassAnnotation,
                    members = currentClassMembers.toList(),
                ),
            )
            currentClassId = null
            currentClassMembers = mutableListOf()
            currentClassAnnotation = null
            currentClassName = null
        }

        lines.forEach { line ->
            val isClassStart = line.startsWith("class ", ignoreCase = true)
            val isAnnotationLine = line.startsWith("<<") && line.endsWith(">>")
            val isOpenBrace = line == "{"
            val isCloseBrace = line == "}"

            // Handle closing brace.
            if (isCloseBrace && currentClassId != null) {
                flushCurrentClass()
                return@forEach
            }

            // Handle opening brace (inside class block, already handled by "class X {").
            if (isOpenBrace && currentClassId != null) {
                return@forEach
            }

            // Handle annotation on separate line.
            if (isAnnotationLine && currentClassId != null) {
                currentClassAnnotation = line.removeSurrounding("<<", ">>").trim()
                return@forEach
            }

            // Handle "class ClassName" or "class ClassName {".
            if (isClassStart) {
                flushCurrentClass()

                val afterClass = line.substringAfter("class ").trim()
                val braceIndex = afterClass.indexOf('{')
                val id = if (braceIndex > 0) afterClass.substring(0, braceIndex).trim() else afterClass.trim()
                if (id.isNotEmpty()) {
                    if (braceIndex >= 0) {
                        // Inline braces: parse members and close immediately.
                        val body = afterClass.substring(braceIndex + 1).removeSuffix("}").trim()
                        val members = mutableListOf<MermaidClassMember>()
                        if (body.isNotEmpty()) {
                            body.lines().forEach { memberLine ->
                                parseClassMember(memberLine.trim())?.let { members.add(it) }
                            }
                        }
                        if (afterClass.endsWith("}")) {
                            // Single-line class with braces.
                            classDefinitions.add(
                                MermaidClassDefinition(
                                    id = id,
                                    label = id,
                                    members = members,
                                ),
                            )
                        } else {
                            // Multi-line class block starts.
                            currentClassId = id
                            currentClassName = id
                            currentClassMembers = members
                            currentClassAnnotation = null
                        }
                    } else {
                        // No braces: class declaration without block.
                        currentClassId = id
                        currentClassName = id
                        currentClassMembers = mutableListOf()
                        currentClassAnnotation = null
                    }
                    return@forEach
                }
            }

            // Try to parse as relationship first (before member check).
            parseClassRelationship(line)?.let { rel ->
                flushCurrentClass()
                classRelationships.add(rel)
                return@forEach
            }

            // Handle members inside class block.
            if (currentClassId != null) {
                parseClassMember(line)?.let { currentClassMembers.add(it) }
            }
        }

        flushCurrentClass()
        return ParseResult.ClassDiagram(
            direction = defaultDirection,
            classDefinitions = classDefinitions,
            classRelationships = classRelationships,
        )
    }

    private fun parseClassMember(line: String): MermaidClassMember? {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed == "{" || trimmed == "}") return null
        // Skip annotations.
        if (trimmed.startsWith("<<") && trimmed.endsWith(">>")) return null

        var visibility = MermaidClassVisibility.Package
        var isAbstract = false
        var isStatic = false
        var rest = trimmed

        // Parse visibility prefix.
        when {
            rest.startsWith("+") -> { visibility = MermaidClassVisibility.Public; rest = rest.substring(1) }
            rest.startsWith("-") -> { visibility = MermaidClassVisibility.Private; rest = rest.substring(1) }
            rest.startsWith("#") -> { visibility = MermaidClassVisibility.Protected; rest = rest.substring(1) }
            rest.startsWith("~") -> { visibility = MermaidClassVisibility.Package; rest = rest.substring(1) }
        }

        // Parse modifiers.
        if (rest.startsWith("*")) { isAbstract = true; rest = rest.substring(1) }
        if (rest.startsWith("$")) { isStatic = true; rest = rest.substring(1) }

        rest = rest.trim()
        if (rest.isEmpty()) return null

        // Check if method (has parentheses).
        val parenIndex = rest.indexOf('(')
        if (parenIndex >= 0) {
            val name = rest.substring(0, parenIndex).trim()
            val afterParen = rest.substring(parenIndex)
            val type = if (afterParen.contains(")")) {
                afterParen.substringAfter(")").trim().removePrefix(":").trim().ifEmpty { null }
            } else null
            return MermaidClassMember(
                kind = MermaidClassMemberKind.Method,
                visibility = visibility,
                name = name,
                type = type,
                isAbstract = isAbstract,
                isStatic = isStatic,
            )
        }

        // Otherwise it's a field — Mermaid format is {type} {name} or just {name}.
        val parts = rest.split(Regex("\\s+"))
        val name: String
        val type: String?
        if (parts.size >= 2) {
            // Last word is the name, everything before is the type.
            name = parts.last().trim()
            type = parts.dropLast(1).joinToString(" ").trim().ifEmpty { null }
        } else {
            name = rest.trim()
            type = null
        }
        return MermaidClassMember(
            kind = MermaidClassMemberKind.Field,
            visibility = visibility,
            name = name,
            type = type,
            isAbstract = isAbstract,
            isStatic = isStatic,
        )
    }

    private fun parseClassRelationship(line: String): MermaidClassRelationship? {
        for (pattern in relationshipPatterns) {
            val match = pattern.matchEntire(line) ?: continue
            val from = match.groupValues[1]
            val fromCard = match.groupValues[2].ifEmpty { null }
            val relTypeStr = match.groupValues[3]
            val toCard = match.groupValues[4].ifEmpty { null }
            val to = match.groupValues[5]
            val label = match.groupValues[6].ifEmpty { null }

            val relType = when (relTypeStr) {
                "<|--" -> MermaidClassRelationType.Inheritance
                "<|.." -> MermaidClassRelationType.Realization
                "*--" -> MermaidClassRelationType.Composition
                "o--" -> MermaidClassRelationType.Aggregation
                "-->" -> MermaidClassRelationType.Association
                "--" -> MermaidClassRelationType.Link
                "..>" -> MermaidClassRelationType.Dependency
                ".." -> MermaidClassRelationType.DependencyLink
                else -> continue
            }

            return MermaidClassRelationship(
                from = from,
                to = to,
                type = relType,
                label = label?.trim(),
                fromCardinality = fromCard,
                toCardinality = toCard,
            )
        }
        return null
    }
}
