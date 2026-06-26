package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Tests for [ClassEdgeGeometry] — the pure edge-anchor/marker-vector geometry for class diagrams.
 *
 * These pin down the UML rendering contract that was broken (inheritance triangles overlapping
 * at the parent's bottom-center, and markers pointing the wrong way). See mermaid.live for the
 * reference: every UML marker (triangle, diamond, arrow) lives on the PARENT's bottom edge with
 * its body hanging into the gap below; when several children attach to one parent, their anchors
 * fan out across that edge.
 */
class ClassEdgeGeometryTest {

    private val nodeWidth = 180f
    private val parentHeight = 96f
    private val childHeight = 96f

    private fun parent(x: Float, y: Float) = PositionedMermaidNode(
        node = MermaidNode(id = "P", label = "P", shape = MermaidNodeShape.Rectangle),
        rank = 0, order = 0, x = x, y = y,
    )

    private fun child(id: String, x: Float, y: Float) = PositionedMermaidNode(
        node = MermaidNode(id = id, label = id, shape = MermaidNodeShape.Rectangle),
        rank = 1, order = 0, x = x, y = y,
    )

    @Test
    fun inheritanceTriangleSitsOnParentBottomEdgeAndBodyPointsDown() {
        // Parent at (0,0), child directly below at (0, 180). Single child → parent anchor is centered.
        val a = ClassEdgeGeometry.anchorsFor(
            child = child("C", x = 0f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Inheritance,
        )

        // End (parent-side) anchor: on the parent's BOTTOM edge, horizontally centered.
        assertEquals(parentHeight, a.endY)
        assertEquals(nodeWidth / 2f, a.endX)

        // Inheritance marker vector must point the body DOWNWARD (into the gap below the parent):
        // uy negative so drawClassMarker's `back = tip - u*size` lands below the tip.
        assertEquals(0f, a.endUx)
        assertEquals(-1f, a.endUy)
    }

    @Test
    fun compositionDiamondSitsOnParentBottomEdgeAndBodyPointsDown() {
        // `A *-- B`: A is the whole, so the diamond is on A — the parent (top) node.
        val a = ClassEdgeGeometry.anchorsFor(
            child = child("C", x = 0f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Composition,
        )

        // Diamond on the PARENT's bottom edge, body down into the gap.
        assertEquals(parentHeight, a.endY)
        assertEquals(nodeWidth / 2f, a.endX)
        assertEquals(0f, a.endUx)
        assertEquals(-1f, a.endUy)
    }

    @Test
    fun dependencyArrowSitsOnChildTopEdgeBodyPointsDown() {
        // `A ..> B`: the dependency arrowhead is on the target B (the child), pointing down into it.
        val a = ClassEdgeGeometry.anchorsFor(
            child = child("C", x = 0f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Dependency,
        )
        // Marker on the child-side (start) anchor, body down into the child.
        assertEquals(180f, a.startY)
        assertEquals(-1f, a.startUy)
        // Parent side carries no marker here.
        assertEquals(0f, a.endUy)
    }

    @Test
    fun associationArrowSitsOnChildTopEdgeBodyPointsDown() {
        val a = ClassEdgeGeometry.anchorsFor(
            child = child("C", x = 0f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Association,
        )
        assertEquals(180f, a.startY)
        assertEquals(-1f, a.startUy)
    }

    @Test
    fun multipleChildrenFanOutAcrossParentBottomEdge() {
        // Two children of the same parent must NOT share the same parent anchor x; they spread
        // evenly across the parent's bottom edge.
        val left = ClassEdgeGeometry.anchorsFor(
            child = child("L", x = 0f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 0, fanCount = 2, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Inheritance,
        )
        val right = ClassEdgeGeometry.anchorsFor(
            child = child("R", x = 240f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 1, fanCount = 2, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Inheritance,
        )

        assertNotEquals(left.endX, right.endX, "fan-out must separate the two parent anchors")

        // Even distribution: fan of 2 → anchors at 1/3 and 2/3 of the parent width.
        assertEquals(nodeWidth / 3.0, left.endX.toDouble(), 0.001)
        assertEquals(nodeWidth * 2.0 / 3.0, right.endX.toDouble(), 0.001)

        // Both still on the parent's bottom edge.
        assertEquals(parentHeight, left.endY)
        assertEquals(parentHeight, right.endY)
    }

    @Test
    fun singleChildAnchoredAtParentCenter() {
        // fan-out with only one child must collapse to the center (no asymmetric offset).
        val a = ClassEdgeGeometry.anchorsFor(
            child = child("C", x = 0f, y = 180f),
            parent = parent(x = 0f, y = 0f),
            childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
            fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
            relationType = MermaidClassRelationType.Inheritance,
        )
        assertEquals(nodeWidth / 2.0, a.endX.toDouble(), 0.001)
    }

    @Test
    fun markerSideMatchesRelationType() {
        // Inheritance / realization / composition / aggregation → marker on the PARENT (whole) end.
        listOf(
            MermaidClassRelationType.Inheritance,
            MermaidClassRelationType.Realization,
            MermaidClassRelationType.Composition,
            MermaidClassRelationType.Aggregation,
        ).forEach { rel ->
            assertEquals(ClassMarkerSide.Parent, ClassEdgeGeometry.markerSide(rel))
            val a = ClassEdgeGeometry.anchorsFor(
                child = child("C", x = 0f, y = 180f),
                parent = parent(x = 0f, y = 0f),
                childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
                fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
                relationType = rel,
            )
            assertEquals(-1f, a.endUy, message = "parent marker body down for $rel")
        }
        // Association / dependency → marker on the CHILD (target) end.
        listOf(
            MermaidClassRelationType.Association,
            MermaidClassRelationType.Dependency,
        ).forEach { rel ->
            assertEquals(ClassMarkerSide.Child, ClassEdgeGeometry.markerSide(rel))
            val a = ClassEdgeGeometry.anchorsFor(
                child = child("C", x = 0f, y = 180f),
                parent = parent(x = 0f, y = 0f),
                childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
                fanIndex = 0, fanCount = 1, nodeWidth = nodeWidth,
                relationType = rel,
            )
            assertEquals(-1f, a.startUy, message = "child marker body down for $rel")
        }
    }

    @Test
    fun fanOutStaysWithinParentWidth() {
        // 4 children: anchors at 1/5, 2/5, 3/5, 4/5 — all strictly inside the parent's width.
        val fanCount = 4
        repeat(fanCount) { i ->
            val a = ClassEdgeGeometry.anchorsFor(
                child = child("C$i", x = i * 240f, y = 180f),
                parent = parent(x = 0f, y = 0f),
                childWidth = nodeWidth, childHeight = childHeight, parentHeight = parentHeight,
                fanIndex = i, fanCount = fanCount, nodeWidth = nodeWidth,
                relationType = MermaidClassRelationType.Inheritance,
            )
            assertTrue(a.endX > 0f, "fan anchor $i must be inside parent (left)")
            assertTrue(a.endX < nodeWidth, "fan anchor $i must be inside parent (right)")
        }
    }

    @Test
    fun demoScenario_componentWithTwoChildrenFansOutAcrossParentBottom() {
        // The exact demo scenario from MermaidDemo: Component is inherited by Button AND TextField.
        // Parses → lays out → the two inheritance triangles must anchor at DIFFERENT x on
        // Component's bottom edge (the bug had both stacking at center).
        val diagram = MermaidParser.parse(
            """
            classDiagram
                class Component
                class Button
                class TextField
                Component <|-- Button
                Component <|-- TextField
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)

        // Component is the parent (rank 0, on top); both edges share from=Component.
        val parentEdges = layout.edges.withIndex().filter { it.value.from == "Component" }
        assertEquals(2, parentEdges.count(), "Component should have two inheritance edges")

        // Reproduce the renderer's fan-out grouping (by parent id) and compute the two anchors.
        val parent = layout.nodes.getValue("Component")
        val nodeWidth = 180f
        val parentHeight = 48f + 8f * 2f // headerHeight + padding*2 for a memberless class
        val anchors = parentEdges.mapIndexed { pos, indexed ->
            ClassEdgeGeometry.anchorsFor(
                child = layout.nodes.getValue(indexed.value.to),
                parent = parent,
                childWidth = nodeWidth,
                childHeight = parentHeight,
                parentHeight = parentHeight,
                fanIndex = pos,
                fanCount = 2,
                nodeWidth = nodeWidth,
                relationType = MermaidClassRelationType.Inheritance,
            )
        }
        // Both on Component's bottom edge.
        anchors.forEach { assertEquals(parent.y + parentHeight, it.endY) }
        // But at different x (fan-out) — this is the regression guard for the overlapping-triangle bug.
        assertNotEquals(anchors[0].endX, anchors[1].endX)
    }
}
