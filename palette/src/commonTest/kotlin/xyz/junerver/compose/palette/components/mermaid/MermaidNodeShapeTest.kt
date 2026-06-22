package xyz.junerver.compose.palette.components.mermaid

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
}
