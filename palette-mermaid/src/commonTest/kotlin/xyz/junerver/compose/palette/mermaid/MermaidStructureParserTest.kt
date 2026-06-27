package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Parser + layout tests for the three relationship/structure diagram types added in roadmap
 * phase 3: Requirement, Block, C4. Each parser is exercised for happy path, edge cases, and
 * layout-doesn't-crash.
 */
class MermaidStructureParserTest {

    // ── Requirement ──────────────────────────────────────────────────

    @Test
    fun parsesRequirementWithBoxesAndRelationships() {
        val diagram = MermaidParser.parse(
            """
            requirementDiagram
                direction LR
                requirement test_req {
                    id: 1
                    text: the test text.
                    risk: high
                    verifymethod: test
                }
                functionalRequirement test_req2 {
                    id: 1.1
                    text: the second text.
                    risk: low
                    verifymethod: inspection
                }
                element test_entity {
                    type: simulation
                }
                test_entity - satisfies -> test_req2
                test_req - contains -> test_req3
                test_req <- copies - test_entity
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.RequirementDiagram, diagram.type)
        assertEquals(MermaidDirection.LeftRight, diagram.direction)
        assertEquals(3, diagram.requirementBoxes.size)
        val req = diagram.requirementBoxes.first()
        assertEquals(RequirementElementType.Requirement, req.type)
        assertEquals("test_req", req.id)
        assertEquals("the test text.", req.text)
        assertEquals("high", req.risk)
        val elem = diagram.requirementBoxes.first { it.type == RequirementElementType.Element }
        assertEquals("test_entity", elem.id)
        // Relationships: 3 parsed.
        assertEquals(3, diagram.requirementRelationships.size)
        val sat = diagram.requirementRelationships.first { it.kind == RequirementRelationKind.Satisfies }
        assertEquals("test_entity", sat.from)
        assertEquals("test_req2", sat.to)
        // Reverse form `dst <- copies - src` → from=src(test_entity), to=dst(test_req).
        val copies = diagram.requirementRelationships.first { it.kind == RequirementRelationKind.Copies }
        assertEquals("test_entity", copies.from)
        assertEquals("test_req", copies.to)
    }

    @Test
    fun parsesRequirementAllSixTypesAndSevenRelations() {
        val diagram = MermaidParser.parse(
            """
            requirementDiagram
                requirement r1 {
                    text: a
                }
                functionalRequirement r2 {
                    text: b
                }
                interfaceRequirement r3 {
                    text: c
                }
                performanceRequirement r4 {
                    text: d
                }
                physicalRequirement r5 {
                    text: e
                }
                designConstraint r6 {
                    text: f
                }
                element el {
                    type: x
                }
                r1 - contains -> r2
                r2 - copies -> r3
                r3 - derives -> r4
                r4 - satisfies -> r5
                r5 - verifies -> r6
                r6 - refines -> el
                el - traces -> r1
            """.trimIndent(),
        )
        assertEquals(7, diagram.requirementBoxes.size)
        assertEquals(7, diagram.requirementRelationships.size)
        // All 7 relation kinds present.
        RequirementRelationKind.values().forEach { kind ->
            assertTrue(diagram.requirementRelationships.any { it.kind == kind }, "missing relation kind $kind")
        }
    }

    @Test
    fun laysOutRequirementWithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            requirementDiagram
                requirement a { text: x }
                element b { type: y }
                b - satisfies -> a
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.RequirementDiagram, layout.type)
        assertTrue(layout.nodes.isNotEmpty())
        assertTrue(layout.requirementRelationTypes.isNotEmpty())
    }

    // ── Block ────────────────────────────────────────────────────────

    @Test
    fun parsesBlockWithColumnsSpansAndEdges() {
        val diagram = MermaidParser.parse(
            """
            block-beta
                columns 3
                a:3
                b["Label"]
                c
                space
                d
                a --> b
                c-- "edge" -->d
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.BlockDiagram, diagram.type)
        // Nodes: a(span3), b(labeled), c, d. (space skipped)
        assertEquals(4, diagram.blockNodes.size)
        val a = diagram.blockNodes.first { it.id == "a" }
        assertEquals(3, a.columnSpan)
        val b = diagram.blockNodes.first { it.id == "b" }
        assertEquals("Label", b.label)
        // Edges.
        assertEquals(2, diagram.blockEdges.size)
        val labeled = diagram.blockEdges.first { it.label != null }
        assertEquals("edge", labeled.label)
    }

    @Test
    fun parsesBlockNestedContainers() {
        val diagram = MermaidParser.parse(
            """
            block-beta
                columns 3
                block:group1:2
                    columns 2
                    h i j k
                end
                g
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.BlockDiagram, diagram.type)
        // Nested block recorded as a container.
        val group = diagram.blockContainers.firstOrNull { it.id == "group1" }
        assertNotNull(group)
        assertEquals(2, group.columns)
        // h i j k are the nested children.
        assertTrue(group.childIds.contains("h"))
        assertTrue(group.childIds.contains("k"))
    }

    @Test
    fun laysOutBlockWithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            block
                columns 2
                a b
                a --> b
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.BlockDiagram, layout.type)
        assertTrue(layout.nodes.isNotEmpty())
    }

    // ── C4 ──────────────────────────────────────────────────────────

    @Test
    fun parsesC4ContainerWithAllKeywordsAndRelationships() {
        // All 5 keywords route to the C4 parser.
        listOf("C4Context", "C4Container", "C4Component", "C4Dynamic", "C4Deployment").forEach { kw ->
            val diagram = MermaidParser.parse(
                """
                $kw
                    title $kw
                    Person(p, "Person", "desc")
                    System_Ext(s, "External", "desc")
                    Rel(p, s, "uses", "https")
                """.trimIndent(),
            )
            assertEquals(MermaidDiagramType.C4Diagram, diagram.type, "keyword $kw")
            assertEquals(2, diagram.c4Elements.size)
            assertEquals("p", diagram.c4Elements.first().alias)
            assertEquals(1, diagram.c4Relationships.size)
        }
    }

    @Test
    fun parsesC4NestedBoundaryAndRelBack() {
        val diagram = MermaidParser.parse(
            """
            C4Container
                title Container diagram
                Boundary(b, "Internet Banking") {
                    Container(spa, "SPA", "Angular", "Provides banking")
                    ContainerDb(db, "Database", "SQL", "Stores data")
                }
                Rel(spa, db, "reads/writes", "JDBC")
                Rel_Back(db, spa, "reverse relationship")
                BiRel(spa, db, "symmetric")
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.C4Diagram, diagram.type)
        assertEquals(2, diagram.c4Elements.size)
        assertEquals(1, diagram.c4Boundaries.size)
        val boundary = diagram.c4Boundaries.first()
        assertEquals("b", boundary.alias)
        // The boundary recorded its two children.
        assertTrue(boundary.childAliases.contains("spa"))
        assertTrue(boundary.childAliases.contains("db"))
        // 3 relationships.
        assertEquals(3, diagram.c4Relationships.size)
        // Rel_Back swaps from/to: db→spa becomes spa→db.
        val back = diagram.c4Relationships.first { it.direction == C4RelDirection.Back }
        assertEquals("spa", back.from)
        assertEquals("db", back.to)
    }

    @Test
    fun laysOutC4WithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            C4Context
                Person(a, "A")
                System(b, "B")
                Rel(a, b, "uses")
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.C4Diagram, layout.type)
        assertTrue(layout.nodes.isNotEmpty())
    }
}
