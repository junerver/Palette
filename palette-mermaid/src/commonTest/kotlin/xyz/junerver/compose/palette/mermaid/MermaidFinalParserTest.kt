package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Parser + layout tests for the four phase-4 diagram types: Journey, Packet, Sankey,
 * Architecture. Each covers happy path, edge cases, and layout-doesn't-crash.
 */
class MermaidFinalParserTest {

    // ── Journey ──────────────────────────────────────────────────────

    @Test
    fun parsesJourneyWithTitleSectionsAndTasks() {
        val diagram = MermaidParser.parse(
            """
            journey
                title My working day
                section Go to work
                  Make tea: 5: Me
                  Do work: 1: Me, Cat
                section Go home
                  Sit down: 5: Me
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.Journey, diagram.type)
        assertEquals("My working day", diagram.journeyTitle)
        assertEquals(2, diagram.journeySections.size)
        val first = diagram.journeySections.first()
        assertEquals("Go to work", first.title)
        assertEquals(2, first.tasks.size)
        assertEquals("Make tea", first.tasks[0].name)
        assertEquals(5, first.tasks[0].score)
        assertEquals(listOf("Me"), first.tasks[0].actors)
        // Multi-actor split on comma.
        assertEquals(listOf("Me", "Cat"), first.tasks[1].actors)
    }

    @Test
    fun parsesJourneyScoreClampedToRange() {
        val diagram = MermaidParser.parse(
            """
            journey
                section X
                  task1: 9: a
                  task2: 0: b
            """.trimIndent(),
        )
        // Out-of-range scores clamp to [1,5].
        assertEquals(5, diagram.journeySections.first().tasks[0].score)
        assertEquals(1, diagram.journeySections.first().tasks[1].score)
    }

    @Test
    fun laysOutJourneyWithoutCrashing() {
        val diagram = MermaidParser.parse("journey\nsection X\ntask: 3: a")
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.Journey, layout.type)
        assertTrue(layout.nodes.isEmpty())
    }

    // ── Packet ───────────────────────────────────────────────────────

    @Test
    fun parsesPacketExplicitAndRelativeFields() {
        val diagram = MermaidParser.parse(
            """
            packet
            title UDP Header
            0-15: "Source Port"
            16-31: "Destination Port"
            +16: "Length"
            +16: "Checksum"
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.Packet, diagram.type)
        assertEquals("UDP Header", diagram.packetTitle)
        assertEquals(4, diagram.packetFields.size)
        // Explicit range form.
        val first = diagram.packetFields[0]
        assertEquals("Source Port", first.label)
        assertEquals(0, first.startBit)
        assertEquals(15, first.endBit)
        assertEquals(16, first.bits)
        // Relative form continues from the previous field's end+1.
        val rel = diagram.packetFields[2]
        assertEquals("Length", rel.label)
        assertEquals(32, rel.startBit) // 31 + 1
        assertEquals(47, rel.endBit)
        assertEquals(16, rel.bits)
    }

    @Test
    fun parsesPacketBothKeywords() {
        listOf("packet", "packet-beta").forEach { kw ->
            val diagram = MermaidParser.parse("$kw\n0: \"Flag\"")
            assertEquals(MermaidDiagramType.Packet, diagram.type, "keyword $kw")
            assertEquals(1, diagram.packetFields.size)
            // Single-bit field (no `-end`): start == end == 0, bits == 1.
            val f = diagram.packetFields.first()
            assertEquals(0, f.startBit)
            assertEquals(0, f.endBit)
            assertEquals(1, f.bits)
        }
    }

    @Test
    fun laysOutPacketWithoutCrashing() {
        val diagram = MermaidParser.parse("packet\n0-7: \"A\"")
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.Packet, layout.type)
    }

    // ── Sankey ───────────────────────────────────────────────────────

    @Test
    fun parsesSankeyCsvWithDecimalsAndBlankLines() {
        val diagram = MermaidParser.parse(
            """
            sankey

            Agricultural waste,Bio-conversion,124.729
            Bio-conversion,Liquid,0.597

            Coal imports,Coal,11.606
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.Sankey, diagram.type)
        assertEquals(3, diagram.sankeyFlows.size)
        assertEquals("Agricultural waste", diagram.sankeyFlows.first().source)
        assertEquals("Bio-conversion", diagram.sankeyFlows.first().target)
        assertEquals(124.729f, diagram.sankeyFlows.first().value)
    }

    @Test
    fun parsesSankeyQuotedFieldsWithEscapedQuotes() {
        val diagram = MermaidParser.parse(
            """
            sankey-beta
            Pumped heat,"Heating and cooling, homes",193.026
            Source,"Has ""escaped"" quotes",50
            """.trimIndent(),
        )
        // The double-quoted field contains a comma → treated as one field.
        assertEquals(2, diagram.sankeyFlows.size)
        assertEquals("Heating and cooling, homes", diagram.sankeyFlows[0].target)
        // `""` escapes resolve to a single quote.
        assertEquals("Has \"escaped\" quotes", diagram.sankeyFlows[1].target)
    }

    @Test
    fun laysOutSankeyWithoutCrashing() {
        val diagram = MermaidParser.parse("sankey\nA,B,10")
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.Sankey, layout.type)
    }

    // ── Architecture ─────────────────────────────────────────────────

    @Test
    fun parsesArchitectureBetaKeywordOnly() {
        // `architecture-beta` is the valid keyword.
        val diagram = MermaidParser.parse(
            """
            architecture-beta
                group api(cloud)[API]
                service db(database)[Database] in api
                junction jc
                db:L -- R:server
                subnet:R --> L:gateway
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.Architecture, diagram.type)
        assertEquals(3, diagram.archNodes.size)
        val group = diagram.archNodes.first { it.kind == ArchNodeKind.Group }
        assertEquals("api", group.id)
        assertEquals("cloud", group.icon)
        assertEquals("API", group.title)
        val service = diagram.archNodes.first { it.kind == ArchNodeKind.Service }
        assertEquals("api", service.parentId)
        assertEquals("database", service.icon)
        // 2 edges: plain and forward.
        assertEquals(2, diagram.archEdges.size)
        val plain = diagram.archEdges.first { it.kind == ArchEdgeKind.Plain }
        assertEquals("db", plain.from)
        assertEquals(ArchDir.L, plain.fromDir)
        assertEquals(ArchDir.R, plain.toDir)
        val fwd = diagram.archEdges.first { it.kind == ArchEdgeKind.Forward }
        assertEquals("subnet", fwd.from)
        assertEquals("gateway", fwd.to)
    }

    @Test
    fun parsesArchitectureBareKeywordAlsoRoutes() {
        // Bare `architecture` is registered as an alias and routes to the parser.
        val diagram = MermaidParser.parse("architecture\nservice s[Service]")
        assertEquals(MermaidDiagramType.Architecture, diagram.type)
        assertEquals(1, diagram.archNodes.size)
    }

    @Test
    fun parsesArchitectureBidirectionalAndBackEdges() {
        val diagram = MermaidParser.parse(
            """
            architecture-beta
                service a(server)[A]
                service b(server)[B]
                a:L <-- R:b
                a:T <--> B:b
            """.trimIndent(),
        )
        assertEquals(2, diagram.archEdges.size)
        assertNotNull(diagram.archEdges.firstOrNull { it.kind == ArchEdgeKind.Back })
        assertNotNull(diagram.archEdges.firstOrNull { it.kind == ArchEdgeKind.Bidirectional })
    }

    @Test
    fun laysOutArchitectureWithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            architecture-beta
                service a(server)[A]
                service b(server)[B]
                a:L -- R:b
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.Architecture, layout.type)
        assertTrue(layout.nodes.isNotEmpty())
    }
}
