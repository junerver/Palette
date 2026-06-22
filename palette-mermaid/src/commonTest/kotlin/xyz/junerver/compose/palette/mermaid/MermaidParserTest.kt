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
}
