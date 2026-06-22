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
}
