package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompatibilityFixtureTest {
    private fun loadResource(path: String): String =
        checkNotNull(compatibilityFixtures[path]) { "Missing compatibility fixture: $path" }

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

    private companion object {
        val compatibilityFixtures = mapOf(
            "compatibility/flowchart-basic.mmd" to
                """
                %%{init: {"theme": "base"}}%%
                flowchart TD
                    A[Markdown] -->|Render| B[Viewer]
                    B --> C{Preview}
                    C -->|OK| D((Done))
                    C -->|Fail| E[/Fallback/]
                    subgraph S[Group]
                        direction LR
                        F[[Subroutine]] --> G[(Database)]
                    end
                """.trimIndent(),
            "compatibility/sequence-basic.mmd" to
                """
                sequenceDiagram
                    autonumber
                    participant U as User
                    participant V as Viewer
                    U->>V: render markdown
                    activate V
                    V-->>U: show preview
                    deactivate V
                    rect rgb(200, 220, 255)
                        Note over U,V: compatibility fixture
                    end
                """.trimIndent(),
            "compatibility/flowchart-extended.mmd" to
                """
                flowchart LR
                    A[Node A] --> B[Node B]
                    B --> C[Node C]
                    A -.-> D{Decision}
                    D -->|Yes| E((Circle))
                    D -->|No| F[[Subroutine]]
                    
                    subgraph group1[Group 1]
                        direction TB
                        G[Inside] --> H[Also Inside]
                    end
                    
                    subgraph group2[Group 2]
                        I[Node I]
                    end
                    
                    A --> G
                    H --> I
                    
                    classDef default fill:#f9f,stroke:#333
                    class A,B default
                    style C fill:#bbf,stroke:#333
                    linkStyle 0 stroke:red
                    click A "https://example.com" "Tooltip"
                """.trimIndent(),
            "compatibility/sequence-extended.mmd" to
                """
                sequenceDiagram
                    autonumber
                    title Sequence Extended
                    
                    participant A as Alice
                    participant B as Bob
                    
                    rect rgb(200, 200, 200)
                        note right of A: Alice thinks
                        A->>B: Hello Bob
                        activate B
                        B-->>A: Hi Alice
                        deactivate B
                    end
                    
                    loop Every minute
                        A->>B: Ping
                        B->>A: Pong
                    end
                    
                    alt success
                        A->>B: Done
                    else failure
                        A->>B: Error
                    end
                    
                    par Action 1
                        A->>B: Do this
                    and Action 2
                        A->>B: Do that
                    end
                    
                    opt Optional
                        B->>A: Maybe
                    end
                    
                    create participant C as Charlie
                    A->>C: Hello Charlie
                    destroy C
                    
                    link A: Dashboard @ https://example.com
                    links B: {"Website": "https://bob.com", "Docs": "https://docs.bob.com"}
                    
                    Note over A,B: Final note
                """.trimIndent(),
            "compatibility/classdiagram-basic.mmd" to
                """
                classDiagram
                    class Animal {
                        +String name
                        +int age
                        +isMammal() bool
                    }
                    class Dog {
                        +bark() void
                    }
                    class Cat {
                        +meow() void
                    }
                    Animal <|-- Dog
                    Animal <|-- Cat
                    Dog o-- Cat : friends
                """.trimIndent(),
        )
    }
}
