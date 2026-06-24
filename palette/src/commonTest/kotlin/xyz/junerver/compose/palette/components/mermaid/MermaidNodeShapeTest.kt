package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidLayoutEngine
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.MermaidParser
import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidNodeShapeTest {
    @Test
    fun nodeShapesMapToDistinctContainerKinds() {
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.Rectangle.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rounded, MermaidNodeShape.Rounded.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Stadium, MermaidNodeShape.Stadium.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Diamond, MermaidNodeShape.Diamond.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Circle, MermaidNodeShape.Circle.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.Subroutine.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Stadium, MermaidNodeShape.Database.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.Asymmetric.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Diamond, MermaidNodeShape.Hexagon.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.Parallelogram.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.ParallelogramAlt.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.Trapezoid.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Rectangle, MermaidNodeShape.TrapezoidAlt.toContainerKind())
        assertEquals(MermaidNodeContainerKind.Circle, MermaidNodeShape.DoubleCircle.toContainerKind())
    }

    @Test
    fun arrowHeadPointsFaceBackTowardLineStart() {
        val arrowHead =
            calculateMermaidArrowHead(
                start = Offset(0f, 0f),
                end = Offset(10f, 0f),
                size = 4f,
                spread = 0.5f,
            )

        assertEquals(Offset(10f, 0f), arrowHead.tip)
        assertEquals(Offset(6f, 2f), arrowHead.left)
        assertEquals(Offset(6f, -2f), arrowHead.right)
    }

    @Test
    fun arrowHeadHandlesZeroLengthLines() {
        val arrowHead =
            calculateMermaidArrowHead(
                start = Offset(10f, 10f),
                end = Offset(10f, 10f),
            )

        assertEquals(Offset(10f, 10f), arrowHead.tip)
        assertEquals(Offset(10f, 10f), arrowHead.left)
        assertEquals(Offset(10f, 10f), arrowHead.right)
    }

    @Test
    fun flowchartEdgeEndpointsFollowDiagramDirection() {
        val topDown =
            calculateFlowchartEdgeEndpoints(
                direction = MermaidDirection.TopDown,
                fromX = 20f,
                fromY = 10f,
                toX = 20f,
                toY = 110f,
                nodeWidth = 100f,
                nodeHeight = 40f,
            )
        assertEquals(Offset(70f, 50f), topDown.start)
        assertEquals(Offset(70f, 110f), topDown.end)

        val bottomTop =
            calculateFlowchartEdgeEndpoints(
                direction = MermaidDirection.BottomTop,
                fromX = 20f,
                fromY = 110f,
                toX = 20f,
                toY = 10f,
                nodeWidth = 100f,
                nodeHeight = 40f,
            )
        assertEquals(Offset(70f, 110f), bottomTop.start)
        assertEquals(Offset(70f, 50f), bottomTop.end)

        val leftRight =
            calculateFlowchartEdgeEndpoints(
                direction = MermaidDirection.LeftRight,
                fromX = 20f,
                fromY = 10f,
                toX = 180f,
                toY = 10f,
                nodeWidth = 100f,
                nodeHeight = 40f,
            )
        assertEquals(Offset(120f, 30f), leftRight.start)
        assertEquals(Offset(180f, 30f), leftRight.end)

        val rightLeft =
            calculateFlowchartEdgeEndpoints(
                direction = MermaidDirection.RightLeft,
                fromX = 180f,
                fromY = 10f,
                toX = 20f,
                toY = 10f,
                nodeWidth = 100f,
                nodeHeight = 40f,
            )
        assertEquals(Offset(180f, 30f), rightLeft.start)
        assertEquals(Offset(120f, 30f), rightLeft.end)
    }

    @Test
    fun mermaidStyleDeclarationsKeepCommasInsideRgbColors() {
        val declarations = "fill:rgb(10, 20, 30),stroke:#abc;color:#fff;stroke-width:2px"
            .parseMermaidStyleDeclarations()

        assertEquals("rgb(10, 20, 30)", declarations["fill"])
        assertEquals("#abc", declarations["stroke"])
        assertEquals("#fff", declarations["color"])
        assertEquals("2px", declarations["stroke-width"])
    }

    @Test
    fun flowchartVisualStylesApplyClassNodeAndLinkDeclarations() {
        val layout =
            MermaidLayoutEngine.layout(
                MermaidParser.parse(
                    """
                    flowchart LR
                        A[Start] -->|go| B[Done]
                        classDef active fill:#f00,stroke:#333,color:#fff,stroke-width:2px
                        class A active
                        style A fill:#00ff00,stroke-width:3px
                        linkStyle 0 stroke:rgb(10, 20, 30),stroke-width:4px,color:#abc
                    """.trimIndent(),
                ),
            )

        val nodeStyle = resolveFlowchartNodeVisualStyles(layout).getValue("A")
        val edgeStyle = resolveFlowchartEdgeVisualStyles(layout).getValue(0)

        assertEquals(Color(0xFF00FF00), nodeStyle.fill)
        assertEquals(Color(0xFF333333), nodeStyle.stroke)
        assertEquals(Color.White, nodeStyle.color)
        assertEquals(3f, nodeStyle.strokeWidth)
        assertEquals(Color(red = 10, green = 20, blue = 30), edgeStyle.stroke)
        assertEquals(Color(0xFFAABBCC), edgeStyle.color)
        assertEquals(4f, edgeStyle.strokeWidth)
    }
}
