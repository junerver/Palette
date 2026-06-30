package xyz.junerver.compose.palette.mermaid

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SankeyLayoutTest {
    private val demoSource =
        """
        sankey
        Agricultural waste,Bio-conversion,124.729
        Bio-conversion,Liquid,0.597
        Bio-conversion,Losses,26.862
        Bio-conversion,Solid,280.322
        Biofuel imports,Liquid,35
        Coal imports,Coal,11.606
        """.trimIndent()

    @Test
    fun terminalNodesArePlacedOnTheRightmostLevel() {
        val nodes = demoLayout()
        val maxLevel = nodes.maxOf { it.level }

        assertEquals(maxLevel, nodes.first { it.name == "Coal" }.level)
        assertEquals(maxLevel, nodes.first { it.name == "Liquid" }.level)
        assertEquals(maxLevel, nodes.first { it.name == "Solid" }.level)
    }

    @Test
    fun sourcesAreSortedByDownstreamTerminalOrder() {
        val sources = demoLayout().filter { it.level == 0 }.sortedBy { it.yWeight }.map { it.name }

        assertEquals(listOf("Biofuel imports", "Agricultural waste", "Coal imports"), sources)
    }

    @Test
    fun nodeHeightsUseGlobalValueScaleAcrossColumns() {
        val nodes = demoLayout()
        val agriculturalWaste = nodes.first { it.name == "Agricultural waste" }
        val bioConversion = nodes.first { it.name == "Bio-conversion" }
        val solid = nodes.first { it.name == "Solid" }

        assertTrue(bioConversion.heightWeight < 0.9f, "Bio-conversion should not fill the middle column")
        assertTrue(abs(agriculturalWaste.heightWeight / agriculturalWaste.value - bioConversion.heightWeight / bioConversion.value) < 0.0001f)
        assertTrue(abs(solid.heightWeight / solid.value - bioConversion.heightWeight / bioConversion.value) < 0.0001f)
    }

    @Test
    fun nodesInSameColumnKeepVisibleGaps() {
        val sources = demoLayout().filter { it.level == 0 }.sortedBy { it.yWeight }

        sources.zipWithNext().forEach { (top, bottom) ->
            assertTrue(bottom.yWeight - (top.yWeight + top.heightWeight) > 0.02f)
        }
    }

    private fun demoLayout(): List<SankeyNodeLayout> =
        MermaidLayoutEngine.layout(MermaidParser.parse(demoSource)).sankeyNodes
}
