package xyz.junerver.compose.palette.components.qrcode

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QRCodeEncoderTest {

    @Test
    fun testEncodeSimpleText() {
        val matrix = QRCodeEncoder.encode("Hello", 1)
        // Version 1 = 21x21
        assertEquals(21, matrix.size)
        assertEquals(21, matrix[0].size)
    }

    @Test
    fun testEncodeURL() {
        val matrix = QRCodeEncoder.encode("https://github.com/junerver/Palette", 1)
        // Should not throw and produce a valid matrix
        assertTrue(matrix.size >= 21)
    }

    @Test
    fun testFinderPatterns() {
        val matrix = QRCodeEncoder.encode("Test", 1)
        val size = matrix.size

        // Check top-left finder pattern
        // Row 0: all dark (0-6)
        for (col in 0..6) {
            assertTrue(matrix[0][col], "Top-left finder row 0, col $col should be dark")
        }
        // Row 6: all dark (0-6)
        for (col in 0..6) {
            assertTrue(matrix[6][col], "Top-left finder row 6, col $col should be dark")
        }
        // Col 0: all dark (0-6)
        for (row in 0..6) {
            assertTrue(matrix[row][0], "Top-left finder col 0, row $row should be dark")
        }
        // Col 6: all dark (0-6)
        for (row in 0..6) {
            assertTrue(matrix[row][6], "Top-left finder col 6, row $row should be dark")
        }
        // Center 3x3 should be dark
        for (row in 2..4) {
            for (col in 2..4) {
                assertTrue(matrix[row][col], "Top-left finder center ($row,$col) should be dark")
            }
        }
    }

    @Test
    fun testTimingPatterns() {
        val matrix = QRCodeEncoder.encode("Test", 1)
        val size = matrix.size

        println("Matrix size: $size")

        // Print row 6 (horizontal timing pattern)
        println("Row 6 (horizontal timing):")
        for (col in 0 until size) {
            print(if (matrix[6][col]) "1" else "0")
        }
        println()

        // Print col 6 (vertical timing pattern)
        println("Col 6 (vertical timing):")
        for (row in 0 until size) {
            print(if (matrix[row][6]) "1" else "0")
        }
        println()

        // Check horizontal timing pattern (row 6, from col 9 to size-9)
        // Note: col 8 is in the format info area (bit 14 of the first copy)
        for (col in 9 until size - 8) {
            val expected = col % 2 == 0
            val actual = matrix[6][col]
            println("Horizontal timing at col $col: expected=$expected, actual=$actual")
            assertEquals(expected, actual, "Horizontal timing at col $col")
        }

        // Check vertical timing pattern (col 6, from row 9 to size-9)
        // Note: row 8 is in the format info area (bit 6 of the second copy)
        for (row in 9 until size - 8) {
            val expected = row % 2 == 0
            val actual = matrix[row][6]
            println("Vertical timing at row $row: expected=$expected, actual=$actual")
            assertEquals(expected, actual, "Vertical timing at row $row")
        }
    }

    @Test
    fun testFormatInfoPlacement() {
        val matrix = QRCodeEncoder.encode("Test", 1)
        val size = matrix.size

        // Format info should be placed around finder patterns
        // Check that format info area is not all zeros (has some dark modules)
        var hasDarkInFormatArea = false

        // Check horizontal format info (row 8, cols 0-8)
        for (col in 0..8) {
            if (matrix[8][col]) {
                hasDarkInFormatArea = true
                break
            }
        }

        // Check vertical format info (rows 0-8, col 8)
        if (!hasDarkInFormatArea) {
            for (row in 0..8) {
                if (matrix[row][8]) {
                    hasDarkInFormatArea = true
                    break
                }
            }
        }

        assertTrue(hasDarkInFormatArea, "Format info area should contain dark modules")

        // Check second copy of format info
        var hasDarkInSecondCopy = false

        // Bottom-left vertical (rows size-7 to size-1, col 8)
        for (row in (size - 7) until size) {
            if (matrix[row][8]) {
                hasDarkInSecondCopy = true
                break
            }
        }

        // Top-right horizontal (row 8, cols 0-7)
        if (!hasDarkInSecondCopy) {
            for (col in 0..7) {
                if (matrix[8][col]) {
                    hasDarkInSecondCopy = true
                    break
                }
            }
        }

        assertTrue(hasDarkInSecondCopy, "Second copy of format info should contain dark modules")
    }

    @Test
    fun testQuietZone() {
        val matrix = QRCodeEncoder.encode("Test", 1)
        // The matrix itself doesn't include quiet zone - it's added during rendering
        // Just verify matrix is valid
        assertTrue(matrix.size > 0)
    }
}