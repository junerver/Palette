package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MermaidParserTest {
    @Test
    fun parsesFlowchartNodesEdgesLabelsAndShapes() {
        val diagram =
            MermaidParser.parse(
                """
                flowchart TD
                    Start([Start]) --> Load[Load markdown]
                    Load --> Decision{Has mermaid?}
                    Decision -- yes --> Render[Render diagram]
                    Decision -- no --> Code[Render code]
                """.trimIndent(),
            )

        assertEquals(MermaidDirection.TopDown, diagram.direction)
        assertEquals(5, diagram.nodes.size)
        assertEquals("Start", diagram.nodes.getValue("Start").label)
        assertEquals(MermaidNodeShape.Stadium, diagram.nodes.getValue("Start").shape)
        assertEquals("Has mermaid?", diagram.nodes.getValue("Decision").label)
        assertEquals(MermaidNodeShape.Diamond, diagram.nodes.getValue("Decision").shape)
        assertEquals(4, diagram.edges.size)
        assertTrue(diagram.edges.any { it.from == "Decision" && it.to == "Render" && it.label == "yes" })
    }

    @Test
    fun laysOutFlowchartInStableRanks() {
        val diagram =
            MermaidParser.parse(
                """
                graph LR
                    A[Markdown] --> B[Code]
                    A --> C[Mermaid]
                    B --> D[Viewer]
                    C --> D
                """.trimIndent(),
            )

        val layout = MermaidLayoutEngine.layout(diagram)

        assertEquals(MermaidDiagramType.Flowchart, layout.type)
        assertEquals(MermaidDirection.LeftRight, layout.direction)
        assertEquals(4, layout.nodes.size)
        assertEquals(0, layout.nodes.getValue("A").rank)
        assertEquals(1, layout.nodes.getValue("B").rank)
        assertEquals(1, layout.nodes.getValue("C").rank)
        assertEquals(2, layout.nodes.getValue("D").rank)
    }

    @Test
    fun parsesFlowchartSubgraphsWithMemberNodes() {
        val diagram =
            MermaidParser.parse(
                """
                flowchart LR
                    subgraph Client [Client Layer]
                        Input[Editor] --> Preview[Preview]
                    end
                    Preview --> Renderer[Renderer]
                """.trimIndent(),
            )

        assertEquals(1, diagram.subgraphs.size)
        val subgraph = diagram.subgraphs.single()
        assertEquals("Client", subgraph.id)
        assertEquals("Client Layer", subgraph.label)
        assertEquals(listOf("Input", "Preview"), subgraph.nodeIds)
    }

    @Test
    fun laysOutFlowchartSubgraphBoundsAroundMembers() {
        val layout =
            MermaidLayoutEngine.layout(
                MermaidParser.parse(
                    """
                    flowchart TD
                        subgraph Pipeline [Pipeline]
                            Source[Source] --> Parse[Parse]
                        end
                        Parse --> Render[Render]
                    """.trimIndent(),
                ),
            )

        assertEquals(1, layout.subgraphs.size)
        val subgraph = layout.subgraphs.single()
        val source = layout.nodes.getValue("Source")
        val parse = layout.nodes.getValue("Parse")
        assertTrue(subgraph.x <= source.x)
        assertTrue(subgraph.y <= source.y)
        assertTrue(subgraph.x + subgraph.width >= parse.x + 132f)
        assertTrue(subgraph.y + subgraph.height >= parse.y + 44f)
    }

    @Test
    fun parsesPipeLabelsAndCommonArrowStyles() {
        val diagram =
            MermaidParser.parse(
                """
                flowchart LR
                    A[Start] -->|ok| B[Done]
                    A -. retry .-> C[Retry]
                    C ==> B
                """.trimIndent(),
            )

        assertEquals(3, diagram.edges.size)
        assertTrue(
            diagram.edges.any {
                it.from == "A" &&
                    it.to == "B" &&
                    it.label == "ok" &&
                    it.style == MermaidEdgeStyle.Solid
            },
        )
        assertTrue(
            diagram.edges.any {
                it.from == "A" &&
                    it.to == "C" &&
                    it.label == "retry" &&
                    it.style == MermaidEdgeStyle.Dotted
            },
        )
        assertTrue(
            diagram.edges.any {
                it.from == "C" &&
                    it.to == "B" &&
                    it.style == MermaidEdgeStyle.Thick
            },
        )
    }

    @Test
    fun parsesFlowchartOpenAndBidirectionalArrows() {
        val diagram =
            MermaidParser.parse(
                """
                flowchart LR
                    A[Client] --- B[Cache]
                    B <--> C[Server]
                """.trimIndent(),
            )

        assertEquals(2, diagram.edges.size)
        assertTrue(
            diagram.edges.any {
                it.from == "A" &&
                    it.to == "B" &&
                    it.arrow == MermaidEdgeArrow.None
            },
        )
        assertTrue(
            diagram.edges.any {
                it.from == "B" &&
                    it.to == "C" &&
                    it.arrow == MermaidEdgeArrow.Bidirectional
            },
        )
    }

    @Test
    fun parsesFlowchartOpenAndThickLabeledEdges() {
        val diagram =
            MermaidParser.parse(
                """
                flowchart LR
                    A[Client] -- sync --- B[Cache]
                    B -. stale .- C[Store]
                    C == fast ==> D[Viewer]
                """.trimIndent(),
            )

        assertEquals(3, diagram.edges.size)
        assertTrue(
            diagram.edges.any {
                it.from == "A" &&
                    it.to == "B" &&
                    it.label == "sync" &&
                    it.style == MermaidEdgeStyle.Solid &&
                    it.arrow == MermaidEdgeArrow.None
            },
        )
        assertTrue(
            diagram.edges.any {
                it.from == "B" &&
                    it.to == "C" &&
                    it.label == "stale" &&
                    it.style == MermaidEdgeStyle.Dotted &&
                    it.arrow == MermaidEdgeArrow.None
            },
        )
        assertTrue(
            diagram.edges.any {
                it.from == "C" &&
                    it.to == "D" &&
                    it.label == "fast" &&
                    it.style == MermaidEdgeStyle.Thick &&
                    it.arrow == MermaidEdgeArrow.Forward
            },
        )
    }

    @Test
    fun parsesStandaloneFlowchartNodeDeclarations() {
        val diagram =
            MermaidParser.parse(
                """
                flowchart TD
                    Start([Start])
                    Parser[Markdown parser]
                    Empty
                    Start --> Parser
                """.trimIndent(),
            )

        assertEquals(3, diagram.nodes.size)
        assertEquals("Start", diagram.nodes.getValue("Start").label)
        assertEquals(MermaidNodeShape.Stadium, diagram.nodes.getValue("Start").shape)
        assertEquals("Markdown parser", diagram.nodes.getValue("Parser").label)
        assertEquals(MermaidNodeShape.Rectangle, diagram.nodes.getValue("Parser").shape)
        assertEquals("Empty", diagram.nodes.getValue("Empty").label)
        assertEquals(1, diagram.edges.size)
    }

    @Test
    fun parsesSequenceDiagramParticipantsAndMessages() {
        val diagram =
            MermaidParser.parse(
                """
                sequenceDiagram
                    participant UI as Markdown viewer
                    participant Parser
                    participant Renderer
                    UI->>Parser: parse fenced mermaid
                    Parser-->>Renderer: diagram model
                """.trimIndent(),
            )

        assertEquals(MermaidDiagramType.Sequence, diagram.type)
        assertEquals(MermaidDirection.LeftRight, diagram.direction)
        assertEquals(3, diagram.nodes.size)
        assertEquals("Markdown viewer", diagram.nodes.getValue("UI").label)
        assertEquals("Parser", diagram.nodes.getValue("Parser").label)
        assertEquals(2, diagram.edges.size)
        assertTrue(
            diagram.edges.any {
                it.from == "UI" &&
                    it.to == "Parser" &&
                    it.label == "parse fenced mermaid" &&
                    it.style == MermaidEdgeStyle.Solid
            },
        )
        assertTrue(
            diagram.edges.any {
                it.from == "Parser" &&
                    it.to == "Renderer" &&
                    it.label == "diagram model" &&
                    it.style == MermaidEdgeStyle.Dotted
            },
        )
    }

    @Test
    fun parsesSequenceDiagramNotesInSourceOrder() {
        val diagram =
            MermaidParser.parse(
                """
                sequenceDiagram
                    participant UI
                    participant Parser
                    UI->>Parser: parse markdown
                    Note right of Parser: Builds an AST
                    Parser-->>UI: render model
                    Note over UI,Parser: Preview updates
                """.trimIndent(),
            )

        assertEquals(MermaidDiagramType.Sequence, diagram.type)
        assertEquals(2, diagram.edges.size)
        assertEquals(2, diagram.notes.size)

        val rightNote = diagram.notes.first()
        assertEquals(MermaidNotePosition.RightOf, rightNote.position)
        assertEquals(listOf("Parser"), rightNote.participants)
        assertEquals("Builds an AST", rightNote.text)
        assertEquals(1, rightNote.sequenceIndex)

        val overNote = diagram.notes.last()
        assertEquals(MermaidNotePosition.Over, overNote.position)
        assertEquals(listOf("UI", "Parser"), overNote.participants)
        assertEquals("Preview updates", overNote.text)
        assertEquals(3, overNote.sequenceIndex)

        assertEquals(0, diagram.edges.first().sequenceIndex)
        assertEquals(2, diagram.edges.last().sequenceIndex)
    }

    @Test
    fun laysOutSequenceParticipantsInDeclarationOrder() {
        val layout =
            MermaidLayoutEngine.layout(
                MermaidParser.parse(
                    """
                    sequenceDiagram
                        participant A
                        participant B
                        participant C
                        C-->>A: reply
                    """.trimIndent(),
                ),
            )

        assertEquals(MermaidDiagramType.Sequence, layout.type)
        assertEquals(0, layout.nodes.getValue("A").rank)
        assertEquals(1, layout.nodes.getValue("B").rank)
        assertEquals(2, layout.nodes.getValue("C").rank)
    }

    @Test
    fun keepsSequenceNotesInLayout() {
        val layout =
            MermaidLayoutEngine.layout(
                MermaidParser.parse(
                    """
                    sequenceDiagram
                        A->>B: request
                        Note over A,B: shared context
                    """.trimIndent(),
                ),
            )

        assertEquals(MermaidDiagramType.Sequence, layout.type)
        assertEquals(1, layout.notes.size)
        assertEquals(listOf("A", "B"), layout.notes.single().participants)
        assertEquals("shared context", layout.notes.single().text)
        assertEquals(1, layout.notes.single().sequenceIndex)
    }
}
