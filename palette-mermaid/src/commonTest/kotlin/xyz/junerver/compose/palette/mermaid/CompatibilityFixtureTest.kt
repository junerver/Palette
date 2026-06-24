package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompatibilityFixtureTest {
    private fun loadResource(path: String): String =
        checkNotNull(CompatibilityFixtureTest::class.java.classLoader.getResource(path)) {
            "Missing compatibility fixture: $path"
        }.readText()

    @Test
    fun flowchartFixtureParsesNodesAndEdges() {
        val diagram = MermaidParser.parse(loadResource("compatibility/flowchart-basic.mmd"))
        assertTrue(diagram.nodes.isNotEmpty())
        assertTrue(diagram.edges.isNotEmpty())
    }

    @Test
    fun sequenceFixtureParsesActivationsAndRegions() {
        val diagram = MermaidParser.parse(loadResource("compatibility/sequence-basic.mmd"))
        assertTrue(diagram.sequenceActivations.isNotEmpty())
        assertTrue(diagram.sequenceRegions.isNotEmpty())
        assertTrue(diagram.notes.isNotEmpty())
    }

    @Test
    fun flowchartExtendedParsesSubgraphsAndStyles() {
        val diagram = MermaidParser.parse(loadResource("compatibility/flowchart-extended.mmd"))
        assertTrue(diagram.nodes.size >= 9, "Expected at least 9 nodes")
        assertTrue(diagram.edges.size >= 7, "Expected at least 7 edges")
        assertTrue(diagram.subgraphs.size >= 2, "Expected at least 2 subgraphs")
        assertTrue(diagram.flowchartClassDefs.isNotEmpty(), "Expected classDef")
        assertTrue(diagram.flowchartClassAssignments.isNotEmpty(), "Expected class assignment")
        assertTrue(diagram.flowchartNodeStyles.isNotEmpty(), "Expected style")
        assertTrue(diagram.flowchartLinkStyles.isNotEmpty(), "Expected linkStyle")
        assertTrue(diagram.flowchartClicks.isNotEmpty(), "Expected click")
    }

    @Test
    fun flowchartExtendedSubgraphDirectionPreserved() {
        val diagram = MermaidParser.parse(loadResource("compatibility/flowchart-extended.mmd"))
        val group1 = diagram.subgraphs.firstOrNull { it.id == "group1" }
        assertTrue(group1 != null, "Expected subgraph group1, got: ${diagram.subgraphs.map { it.id }}")
        assertEquals(MermaidDirection.TopDown, group1.direction, "Expected TB direction")
    }

    @Test
    fun sequenceExtendedParsesAllFeatures() {
        val diagram = MermaidParser.parse(loadResource("compatibility/sequence-extended.mmd"))
        assertEquals(true, diagram.sequenceAutonumber, "Expected autonumber")
        assertEquals("Sequence Extended", diagram.title, "Expected title")
        assertTrue(diagram.nodes.size >= 3, "Expected at least 3 participants, got ${diagram.nodes.size}: ${diagram.nodes.keys}")
        assertTrue(diagram.edges.size >= 6, "Expected at least 6 messages, got ${diagram.edges.size}")
        assertTrue(diagram.sequenceFragments.size >= 4, "Expected at least 4 fragments, got ${diagram.sequenceFragments.size}")
        assertTrue(diagram.sequenceRegions.size >= 1, "Expected at least 1 rect region, got ${diagram.sequenceRegions.size}")
        assertTrue(diagram.sequenceActivations.size >= 1, "Expected at least 1 activation, got ${diagram.sequenceActivations.size}")
        assertTrue(diagram.sequenceLifecycleEvents.size >= 2, "Expected at least 2 lifecycle events, got ${diagram.sequenceLifecycleEvents.size}")
        assertTrue(diagram.sequenceLinks.size >= 1, "Expected at least 1 link, got ${diagram.sequenceLinks.size}")
        assertTrue(diagram.notes.size >= 2, "Expected at least 2 notes, got ${diagram.notes.size}")
    }

    @Test
    fun sequenceExtendedFragmentBranchesParsed() {
        val diagram = MermaidParser.parse(loadResource("compatibility/sequence-extended.mmd"))
        val altFragment = diagram.sequenceFragments.firstOrNull { it.kind == MermaidSequenceFragmentKind.Alt }
        assertTrue(altFragment != null, "Expected alt fragment")
        assertTrue(altFragment.branches.size >= 2, "Expected success and failure branches")
    }

    @Test
    fun classDiagramFixtureParsesClassesAndRelationships() {
        val diagram = MermaidParser.parse(loadResource("compatibility/classdiagram-basic.mmd"))
        assertEquals(MermaidDiagramType.ClassDiagram, diagram.type)
        assertTrue(diagram.classDefinitions.size >= 3, "Expected at least 3 classes, got ${diagram.classDefinitions.size}")
        assertTrue(diagram.classRelationships.size >= 3, "Expected at least 3 relationships, got ${diagram.classRelationships.size}")

        val animal = diagram.classDefinitions.firstOrNull { it.id == "Animal" }
        assertTrue(animal != null, "Expected Animal class")
        assertTrue(animal.members.isNotEmpty(), "Expected Animal to have members")
        assertTrue(animal.members.any { it.kind == MermaidClassMemberKind.Method }, "Expected Animal to have methods")

        assertTrue(diagram.classRelationships.any { it.type == MermaidClassRelationType.Inheritance },
            "Expected at least one inheritance relationship")
    }
}
