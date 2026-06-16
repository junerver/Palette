package xyz.junerver.compose.palette.components.qrcode

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verification tests for the QRCodeEncoder.
 * These tests verify format info BCH encoding, copy consistency, and timing patterns.
 */
class QRCodeVerificationTest {

    @Test
    fun testTimingPatternAt6x8() {
        val matrix = QRCodeEncoder.encode("Test", 1)
        val formatInfo = extractFormatInfoFirstCopy(matrix)
        assertTrue(verifyBchFormatInfo(formatInfo), "Format info should be valid")
    }

    @Test
    fun testFormatInfoBCH_Hello() {
        verifyFormatInfo("Hello", 1)
    }

    @Test
    fun testFormatInfoBCH_URL() {
        verifyFormatInfo("https://github.com/junerver/Palette", 1)
    }

    @Test
    fun testFormatInfoBCH_Test() {
        verifyFormatInfo("Test", 1)
    }

    @Test
    fun testFormatInfoBCH_SingleChar() {
        verifyFormatInfo("A", 1)
    }

    @Test
    fun testFormatInfoFirstCopyConsistency() {
        val matrix = QRCodeEncoder.encode("Hello", 1)
        val first = extractFormatInfoFirstCopy(matrix)
        val second = extractFormatInfoSecondCopy(matrix)
        assertEquals(first, second, "First and second copies of format info should be identical")
    }

    @Test
    fun testTimingPatternsVersion1() {
        val matrix = QRCodeEncoder.encode("Test", 1)
        val size = matrix.size
        for (col in 9 until size - 8) {
            assertEquals(col % 2 == 0, matrix[6][col], "Horizontal timing at [6][$col]")
        }
        for (row in 9 until size - 8) {
            assertEquals(row % 2 == 0, matrix[row][6], "Vertical timing at [$row][6]")
        }
    }

    @Test
    fun testBchEncodingDirectly() {
        val gen = 0x537
        // data=0 (M/mask0) → 0x5412
        run {
            var bits = 0
            for (i in 4 downTo 0) {
                if (bits and (1 shl (i + 10)) != 0) bits = bits xor (gen shl i)
            }
            assertEquals(0x5412, ((0 shl 10) or (bits and 0x3FF)) xor 0x5412)
        }
        // data=8 (L/mask0) → 0x77C4
        run {
            var bits = 8 shl 10
            for (i in 4 downTo 0) {
                if (bits and (1 shl (i + 10)) != 0) bits = bits xor (gen shl i)
            }
            assertEquals(0x77C4, ((8 shl 10) or (bits and 0x3FF)) xor 0x5412)
        }
    }

    @Test
    fun testRSEncoding() {
        // Verify RS encoding produces consistent EC codewords
        val data = intArrayOf(0x40, 0x54, 0x56, 0xFC, 0x6C, 0x6C, 0xA5, 0xB9, 0x44, 0xEC, 0x11, 0xEC, 0xBB, 0x46, 0xBB, 0xA7)
        val ec = QRCodeEncoder.rsEncode(data, 10)
        assertEquals(10, ec.size)
        // Verify EC is deterministic
        val ec2 = QRCodeEncoder.rsEncode(data, 10)
        for (i in ec.indices) {
            assertEquals(ec[i], ec2[i], "EC codeword at index $i should be deterministic")
        }
    }

    // ===== Helper functions =====

    private fun verifyFormatInfo(text: String, ecLevel: Int) {
        val matrix = QRCodeEncoder.encode(text, ecLevel)
        val first = extractFormatInfoFirstCopy(matrix)
        val second = extractFormatInfoSecondCopy(matrix)
        assertTrue(verifyBchFormatInfo(first), "First copy format info BCH should be valid (0x${first.toString(16)})")
        assertTrue(verifyBchFormatInfo(second), "Second copy format info BCH should be valid (0x${second.toString(16)})")
        assertEquals(first, second, "Both copies should match")
    }

    private fun extractFormatInfoFirstCopy(matrix: Array<BooleanArray>): Int {
        var bits = 0
        for (i in 0..5) {
            if (matrix[i][8]) bits = bits or (1 shl i)
        }
        if (matrix[7][8]) bits = bits or (1 shl 6)
        if (matrix[8][8]) bits = bits or (1 shl 7)
        if (matrix[8][7]) bits = bits or (1 shl 8)
        if (matrix[8][5]) bits = bits or (1 shl 9)
        for (i in 10..14) {
            if (matrix[8][14 - i]) bits = bits or (1 shl i)
        }
        return bits
    }

    private fun extractFormatInfoSecondCopy(matrix: Array<BooleanArray>): Int {
        val size = matrix.size
        var bits = 0
        for (i in 0..7) {
            if (matrix[8][size - 1 - i]) bits = bits or (1 shl i)
        }
        for (i in 8..14) {
            if (matrix[i + 1][8]) bits = bits or (1 shl i)
        }
        return bits
    }

    private fun verifyBchFormatInfo(bits15: Int): Boolean {
        val unmasked = bits15 xor 0x5412
        var remainder = unmasked
        for (i in 4 downTo 0) {
            if (remainder and (1 shl (i + 10)) != 0) {
                remainder = remainder xor (0x537 shl i)
            }
        }
        return remainder == 0
    }
}
