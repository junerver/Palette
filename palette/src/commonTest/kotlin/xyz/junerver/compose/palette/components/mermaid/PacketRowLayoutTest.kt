package xyz.junerver.compose.palette.components.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import xyz.junerver.compose.palette.mermaid.PacketField

class PacketRowLayoutTest {

    @Test
    fun splitsFieldAcrossRowsUsingActualBitBounds() {
        val rows = buildPacketRowLayouts(
            fields = listOf(
                PacketField(label = "Sequence Number", startBit = 32, endBit = 63, bits = 32),
                PacketField(label = "Wide", startBit = 64, endBit = 111, bits = 48),
                PacketField(label = "Window", startBit = 112, endBit = 127, bits = 16),
            ),
            bitsPerRow = 32,
        )

        assertEquals(3, rows.size)

        assertEquals(32, rows[0].rowStartBit)
        assertEquals(listOf(32 to 63), rows[0].segments.map { it.startBit to it.endBit })

        assertEquals(64, rows[1].rowStartBit)
        assertEquals(listOf(64 to 95), rows[1].segments.map { it.startBit to it.endBit })

        assertEquals(96, rows[2].rowStartBit)
        assertEquals(
            listOf(96 to 111, 112 to 127),
            rows[2].segments.map { it.startBit to it.endBit },
        )
    }

    @Test
    fun preservesGapsForSparseExplicitPacketRows() {
        val rows = buildPacketRowLayouts(
            fields = listOf(
                PacketField(label = "Late", startBit = 8, endBit = 15, bits = 8),
                PacketField(label = "Last", startBit = 24, endBit = 31, bits = 8),
            ),
            bitsPerRow = 32,
        )

        assertEquals(1, rows.size)
        assertEquals(0, rows.first().rowStartBit)
        assertEquals(31, rows.first().rowEndBit)
        assertEquals(listOf(8 to 15, 24 to 31), rows.first().segments.map { it.startBit to it.endBit })
    }

    @Test
    fun addsVisualGapBetweenAdjacentSegmentsInSameRow() {
        val row = buildPacketRowLayouts(
            fields = listOf(
                PacketField(label = "A", startBit = 0, endBit = 15, bits = 16),
                PacketField(label = "B", startBit = 16, endBit = 31, bits = 16),
            ),
            bitsPerRow = 32,
        ).single()

        val visualSegments = buildPacketVisualSegmentLayouts(row = row, bitsPerRow = 32)

        assertEquals(2, visualSegments.size)
        assertTrue(visualSegments[0].widthFraction < 16f / 32f)
        assertTrue(visualSegments[1].startFraction > 16f / 32f)
    }

    @Test
    fun doesNotAddExtraVisualGapForActualSparseBits() {
        val row = buildPacketRowLayouts(
            fields = listOf(
                PacketField(label = "A", startBit = 0, endBit = 7, bits = 8),
                PacketField(label = "B", startBit = 16, endBit = 23, bits = 8),
            ),
            bitsPerRow = 32,
        ).single()

        val visualSegments = buildPacketVisualSegmentLayouts(row = row, bitsPerRow = 32)

        assertEquals(0f, visualSegments[0].startFraction)
        assertEquals(8f / 32f, visualSegments[0].widthFraction)
        assertEquals(16f / 32f, visualSegments[1].startFraction)
        assertEquals(8f / 32f, visualSegments[1].widthFraction)
    }
}
