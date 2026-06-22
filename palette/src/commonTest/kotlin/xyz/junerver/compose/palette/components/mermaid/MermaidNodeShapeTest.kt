package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.ui.geometry.Offset
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
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
}
