package xyz.junerver.compose.palette.components.mermaid

import kotlin.test.Test
import kotlin.test.assertTrue
import xyz.junerver.compose.palette.mermaid.SankeyFlow

class SankeyFlowOffsetTest {
    @Test
    fun targetOffsetsFollowSourceVerticalOrder() {
        val flows =
            listOf(
                SankeyFlow("Bio-conversion", "Liquid", 0.597f),
                SankeyFlow("Biofuel imports", "Liquid", 35f),
            )
        val nodes =
            mapOf(
                "Biofuel imports" to SankeyRenderNode("Biofuel imports", 0, 2, 35f, 0f, 0f, 20f),
                "Bio-conversion" to SankeyRenderNode("Bio-conversion", 1, 2, 307.78f, 100f, 100f, 200f),
                "Liquid" to SankeyRenderNode("Liquid", 2, 2, 35.6f, 200f, 0f, 22f),
            )
        val offsets =
            calculateSankeyFlowOffsets(
                flowsByNode = flows.withIndex().groupBy { it.value.target },
                nodeByName = nodes,
                otherNodeName = { it.value.source },
            )

        assertTrue(offsets.getValue(1) < offsets.getValue(0))
    }

    @Test
    fun flowSpanUsesNodeValueInsteadOfIncomingTotal() {
        val bioConversion = SankeyRenderNode("Bio-conversion", 1, 2, 307.78f, 100f, 100f, 200f)

        val (top, bottom) = sankeyFlowSpan(bioConversion, valueOffset = 0f, flowValue = 124.73f)

        assertTrue(bottom - top < bioConversion.height)
        assertTrue(bottom - top > bioConversion.height * 0.35f)
        assertTrue(bottom - top < bioConversion.height * 0.45f)
    }
}
