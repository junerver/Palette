package xyz.junerver.compose.palette.mermaid.parsers

import xyz.junerver.compose.palette.mermaid.ErAttribute
import xyz.junerver.compose.palette.mermaid.ErEntity
import xyz.junerver.compose.palette.mermaid.ErRelationship
import xyz.junerver.compose.palette.mermaid.ErRelationshipKind
import xyz.junerver.compose.palette.mermaid.MermaidDiagramParser
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.ParseResult

/**
 * ER diagram parser. Extracted verbatim from the old monolithic `MermaidParser.parse()`
 * ER branch — entity blocks, inline entities, attributes (with PK/FK markers) and
 * crow's-foot relationships are parsed with the original regexes and order.
 */
internal object ErDiagramParser : MermaidDiagramParser {
    override val keyword: String = "erDiagram"
    override val defaultDirection: MermaidDirection = MermaidDirection.TopDown

    private val inlineEntityRegex = Regex("""^(\S+)\s*\{(.+)\}\s*$""")
    private val entityBlockRegex = Regex("""^(\S+)\s*\{?\s*$""")
    private val attributeRegex = Regex("""^(\S+)\s+(\S+)(?:\s+"([^"]*)")?(?:\s+(PK|FK))?\s*$""")
    private val solidRelRegex = Regex(
        """^(\S+)\s+(\}?\|?\|?[o|]?--\|?[o|]?\|?\{?)\s+(\S+)(?:\s*:\s*(.+))?$""",
    )
    private val dottedRelRegex = Regex(
        """^(\S+)\s+(\}?\|?\|?[o|]?\.\.\|?[o|]?\|?\{?)\s+(\S+)(?:\s*:\s*(.+))?$""",
    )

    override fun parse(lines: List<String>): ParseResult.ErDiagram {
        val erEntities = mutableListOf<ErEntity>()
        val erRelationships = mutableListOf<ErRelationship>()
        var currentErEntity: String? = null
        var currentErAttributes = mutableListOf<ErAttribute>()

        fun flushCurrentErEntity() {
            if (currentErEntity != null) {
                erEntities.add(
                    ErEntity(
                        name = currentErEntity!!,
                        attributes = currentErAttributes.toList(),
                    ),
                )
                currentErEntity = null
                currentErAttributes = mutableListOf()
            }
        }

        lines.forEach { line ->
            // Close brace ends current entity block.
            if (line == "}" && currentErEntity != null) {
                flushCurrentErEntity()
                return@forEach
            }

            // Open brace continues entity block.
            if (line == "{" && currentErEntity != null) {
                return@forEach
            }

            // Attribute line inside entity block.
            if (currentErEntity != null) {
                parseAttribute(line)?.let { currentErAttributes.add(it) }
                return@forEach
            }

            // Relationship: ENTITY1 <notation> ENTITY2 : label
            // Must check before entity start to avoid treating first entity name as standalone.
            parseRelationship(line)?.let {
                erRelationships.add(it)
                return@forEach
            }

            // Entity with inline braces: ENTITY_NAME { ... }
            inlineEntityRegex.matchEntire(line)?.let { match ->
                val entityName = match.groupValues[1]
                val body = match.groupValues[2].trim()
                val attrs = mutableListOf<ErAttribute>()
                if (body.isNotEmpty()) {
                    body.lines().forEach { part ->
                        parseAttribute(part.trim())?.let { attrs.add(it) }
                    }
                }
                erEntities.add(ErEntity(name = entityName, attributes = attrs))
                return@forEach
            }

            // Entity block start: ENTITY_NAME { or ENTITY_NAME
            entityBlockRegex.matchEntire(line)?.let { match ->
                currentErEntity = match.groupValues[1]
                currentErAttributes = mutableListOf()
                return@forEach
            }
        }

        flushCurrentErEntity()
        return ParseResult.ErDiagram(
            direction = defaultDirection,
            erEntities = erEntities,
            erRelationships = erRelationships,
        )
    }

    private fun parseAttribute(line: String): ErAttribute? {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed == "{" || trimmed == "}") return null
        // Format: type name "comment" or type name PK/FK or type name
        val match = attributeRegex.matchEntire(trimmed) ?: return null
        val keyMarker = match.groupValues[4]
        return ErAttribute(
            name = match.groupValues[2],
            type = match.groupValues[1],
            comment = match.groupValues[3].ifEmpty { null },
            isPrimaryKey = keyMarker == "PK",
            isForeignKey = keyMarker == "FK",
        )
    }

    private fun parseRelationship(line: String): ErRelationship? {
        // Try solid line notations first, then dotted.
        solidRelRegex.matchEntire(line)?.let { return matchRel(it) }
        dottedRelRegex.matchEntire(line)?.let { return matchRel(it) }
        return null
    }

    private fun matchRel(match: MatchResult): ErRelationship? {
        val notation = match.groupValues[2]
        val kind = when (notation) {
            "||--||" -> ErRelationshipKind.OneToOne
            "||--o{" -> ErRelationshipKind.OneToManyZeroOrMore
            "||--|{" -> ErRelationshipKind.OneToManyOneOrMore
            "}o--o{" -> ErRelationshipKind.ManyToManyZeroOrMore
            "}|--|{" -> ErRelationshipKind.ManyToManyOneOrMore
            "}o--||" -> ErRelationshipKind.ManyToOneZeroOrMore
            "}|--||" -> ErRelationshipKind.ManyToOneOneOrMore
            "||..||" -> ErRelationshipKind.NonIdentifyingOneToOne
            "||..o{" -> ErRelationshipKind.NonIdentifyingOneToMany
            "||..|{" -> ErRelationshipKind.NonIdentifyingOneToMany
            "}o..o{" -> ErRelationshipKind.NonIdentifyingManyToMany
            "}|..|{" -> ErRelationshipKind.NonIdentifyingManyToMany
            "}o..||" -> ErRelationshipKind.NonIdentifyingManyToOne
            "}|..||" -> ErRelationshipKind.NonIdentifyingManyToOne
            else -> return null
        }
        return ErRelationship(
            from = match.groupValues[1],
            to = match.groupValues[3],
            kind = kind,
            label = match.groupValues[4].ifEmpty { null }?.trim(),
        )
    }
}
