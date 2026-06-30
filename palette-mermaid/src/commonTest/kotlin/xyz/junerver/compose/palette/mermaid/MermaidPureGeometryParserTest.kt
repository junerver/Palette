package xyz.junerver.compose.palette.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Parser + layout tests for the three pure-geometry diagram types added in roadmap phase 2:
 * Timeline, QuadrantChart, XYChart. Each parser is exercised for happy path, edge cases, and
 * layout-doesn't-crash (these types return an empty layout — the renderer computes geometry).
 */
class MermaidPureGeometryParserTest {

    // ── Timeline ─────────────────────────────────────────────────────

    @Test
    fun parsesTimelineWithTitleSectionAndInlineEvents() {
        val diagram = MermaidParser.parse(
            """
            timeline
                title History
                section Early
                    2014 : Initial release
                    2018 : Rewrite : New types
                section Growth
                    2021 : Timeline added
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.Timeline, diagram.type)
        assertEquals("History", diagram.title)
        assertEquals(3, diagram.timelinePeriods.size)
        // First period in "Early" section.
        val first = diagram.timelinePeriods.first()
        assertEquals("Early", first.section)
        assertEquals("2014", first.time)
        assertEquals(listOf("Initial release"), first.events)
        // Inline chaining splits into two events.
        val second = diagram.timelinePeriods[1]
        assertEquals(listOf("Rewrite", "New types"), second.events)
        // Section context switches.
        assertEquals("Growth", diagram.timelinePeriods.last().section)
    }

    @Test
    fun parsesTimelineContinuationLinesAppendToCurrentPeriod() {
        val diagram = MermaidParser.parse(
            """
            timeline
                2020 : Event A
                      : Event B
                      : Event C
            """.trimIndent(),
        )
        assertEquals(1, diagram.timelinePeriods.size)
        assertEquals(listOf("Event A", "Event B", "Event C"), diagram.timelinePeriods.first().events)
        // No section directive → empty section name.
        assertEquals("", diagram.timelinePeriods.first().section)
    }

    @Test
    fun parsesTimelineWithoutCrashingOnEmptyAndHeaderDirection() {
        // Empty body.
        val empty = MermaidParser.parse("timeline")
        assertEquals(MermaidDiagramType.Timeline, empty.type)
        assertTrue(empty.timelinePeriods.isEmpty())
        assertNull(empty.title)

        // Header may carry an LR/TD direction token.
        val withDir = MermaidParser.parse(
            """
            timeline TD
                2021 : Launch
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.Timeline, withDir.type)
        assertEquals(1, withDir.timelinePeriods.size)
    }

    @Test
    fun laysOutTimelineWithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            timeline
                2020 : A : B
                2021 : C
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.Timeline, layout.type)
        assertTrue(layout.nodes.isEmpty())
    }

    // ── QuadrantChart ────────────────────────────────────────────────

    @Test
    fun parsesQuadrantChartAxesLabelsAndPoints() {
        val diagram = MermaidParser.parse(
            """
            quadrantChart
                title Reach and engagement
                x-axis Low Reach --> High Reach
                y-axis Low Engagement --> High Engagement
                quadrant-1 Expand
                quadrant-2 Promote
                quadrant-3 Re-evaluate
                quadrant-4 Improve
                Campaign A: [0.3, 0.6]
                Campaign B: [0.7, 0.8]
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.QuadrantChart, diagram.type)
        assertEquals("Reach and engagement", diagram.quadrantTitle)
        val xAxis = assertNotNull(diagram.quadrantXAxis)
        assertEquals("Low Reach", xAxis.lowLabel)
        assertEquals("High Reach", xAxis.highLabel)
        assertEquals(listOf("Expand", "Promote", "Re-evaluate", "Improve"), diagram.quadrantLabels)
        assertEquals(2, diagram.quadrantPoints.size)
        val p = diagram.quadrantPoints.first()
        assertEquals("Campaign A", p.label)
        assertEquals(0.3f, p.x)
        assertEquals(0.6f, p.y)
    }

    @Test
    fun parsesQuadrantChartPointStylingClampsAndClassDef() {
        val diagram = MermaidParser.parse(
            """
            quadrantChart
                Campaign B: [0.7, 0.8] color: #ff3300, radius: 12
                Outlier: [1.5, 0.0]
                Hot:::hot: [0.9, 0.9]
                classDef hot color: #f00fff, radius: 10
            """.trimIndent(),
        )
        // Inline style on Campaign B.
        val b = diagram.quadrantPoints[0]
        assertEquals(0xFFff3300u, b.color)
        assertEquals(12f, b.radius)
        // Out-of-range coords clamped to [0,1] (mermaid coords are normalized).
        val outlier = diagram.quadrantPoints[1]
        assertEquals(1f, outlier.x)
        assertEquals(0f, outlier.y)
        // classDef applied via :::hot (resolved even though def appears after the point).
        val hot = diagram.quadrantPoints[2]
        assertEquals(0xFFf00fffu, hot.color)
        assertEquals(10f, hot.radius)
    }

    @Test
    fun parsesQuadrantChartBareAxisWithoutArrow() {
        val diagram = MermaidParser.parse(
            """
            quadrantChart
                x-axis Just one label
                Point: [0.5, 0.5]
            """.trimIndent(),
        )
        // Bare axis (no -->) → low and high both equal the label.
        val xAxis = assertNotNull(diagram.quadrantXAxis)
        assertEquals("Just one label", xAxis.lowLabel)
        assertEquals("Just one label", xAxis.highLabel)
    }

    @Test
    fun laysOutQuadrantChartWithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            quadrantChart
                A: [0.1, 0.2]
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.QuadrantChart, layout.type)
        assertTrue(layout.nodes.isEmpty())
    }

    // ── XYChart ──────────────────────────────────────────────────────

    @Test
    fun parsesXyChartWithBothKeywords() {
        // Modern `xychart` keyword.
        val modern = MermaidParser.parse(
            """
            xychart
                bar [1, 2, 3]
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.XYChart, modern.type)
        assertEquals(1, modern.xySeries.size)
        assertEquals(XySeriesKind.Bar, modern.xySeries.first().kind)
        assertEquals(listOf(1f, 2f, 3f), modern.xySeries.first().values)

        // Legacy `xychart-beta` alias.
        val legacy = MermaidParser.parse(
            """
            xychart-beta
                line [0.5, -1.2, .7]
            """.trimIndent(),
        )
        assertEquals(MermaidDiagramType.XYChart, legacy.type)
        assertEquals(XySeriesKind.Line, legacy.xySeries.first().kind)
        // Leading-dot and negative numbers parse.
        assertEquals(listOf(0.5f, -1.2f, 0.7f), legacy.xySeries.first().values)
    }

    @Test
    fun parsesXyChartQuotedTitleAndCategoricalAxis() {
        val diagram = MermaidParser.parse(
            """
            xychart-beta
                title "Quarterly revenue"
                x-axis [Q1, Q2, "Q3 special", Q4]
                y-axis Revenue 0 --> 500
                bar [120, 180, 240, 310]
                line [100, 150, 210, 290]
            """.trimIndent(),
        )
        assertEquals("Quarterly revenue", diagram.xyTitle) // quotes stripped
        assertEquals(listOf("Q1", "Q2", "Q3 special", "Q4"), diagram.xyXCategories)
        assertEquals(0f to 500f, diagram.xyYAxisRange)
        // Title side had a trailing number ("Revenue 0") that must not leak into the range.
        assertEquals("Revenue", diagram.xyYAxisTitle)
        assertEquals(2, diagram.xySeries.size)
    }

    @Test
    fun parsesXyChartNumericXAxisRange() {
        val diagram = MermaidParser.parse(
            """
            xychart
                x-axis Year 2000 --> 2020
                bar [4, 8, 15]
            """.trimIndent(),
        )
        assertEquals(2000f to 2020f, diagram.xyXAxisRange)
        assertEquals("Year", diagram.xyXAxisTitle)
        assertTrue(diagram.xyXCategories.isEmpty())
    }

    @Test
    fun laysOutXyChartWithoutCrashing() {
        val diagram = MermaidParser.parse(
            """
            xychart
                bar [1, 2]
            """.trimIndent(),
        )
        val layout = MermaidLayoutEngine.layout(diagram)
        assertEquals(MermaidDiagramType.XYChart, layout.type)
        assertTrue(layout.nodes.isEmpty())
    }
}
