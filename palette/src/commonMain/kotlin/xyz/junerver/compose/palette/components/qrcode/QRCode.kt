package xyz.junerver.compose.palette.components.qrcode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PQRCode(
    value: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    errorCorrectionLevel: Int = 1,
) {
    val resolvedColor = if (color == Color.Unspecified) PaletteTheme.colors.onSurface else color
    val resolvedBg = if (backgroundColor == Color.Unspecified) Color.Transparent else backgroundColor
    val matrix = remember(value, errorCorrectionLevel) {
        QRCodeEncoder.encode(value, errorCorrectionLevel.coerceIn(0, 3))
    }
    Canvas(modifier = modifier.size(size)) {
        if (resolvedBg != Color.Transparent) {
            drawRect(color = resolvedBg, size = this.size)
        }
        val moduleCount = matrix.size
        val totalSize = moduleCount + QRCodeDefaults.QuietZone * 2
        val cellSize = this.size.width / totalSize
        val offset = QRCodeDefaults.QuietZone * cellSize
        for (row in matrix.indices) {
            for (col in matrix.indices) {
                if (matrix[row][col]) {
                    drawRect(
                        color = resolvedColor,
                        topLeft = Offset(offset + col * cellSize, offset + row * cellSize),
                        size = Size(cellSize, cellSize),
                    )
                }
            }
        }
    }
}

internal object QRCodeEncoder {

    fun encode(text: String, ecLevel: Int): Array<BooleanArray> {
        val textBytes = text.encodeToByteArray()
        // 选择版本：根据数据长度选择最小可用版本
        val version = selectVersion(textBytes.size, ecLevel)
        val totalDataCodewords = DATA_CODEWORDS[version][ecLevel]
        val ecCodewordsPerBlock = EC_CODEWORDS_PER_BLOCK[version][ecLevel]
        val numBlocks = NUM_EC_BLOCKS[version][ecLevel]
        // 编码数据为字节
        val dataCodewords = encodeData(textBytes, version, totalDataCodewords)
        val totalCodewords = TOTAL_CODEWORDS[version]
        // 分块
        val remainder = totalDataCodewords % numBlocks
        val blockSizes = IntArray(numBlocks) { i ->
            totalDataCodewords / numBlocks + if (i < remainder) 1 else 0
        }
        val blocks = mutableListOf<IntArray>()
        val ecBlocks = mutableListOf<IntArray>()
        var dataIdx = 0
        for (i in 0 until numBlocks) {
            val block = IntArray(blockSizes[i]) { j ->
                if (dataIdx < dataCodewords.size) dataCodewords[dataIdx++] else 0
            }
            blocks.add(block)
            ecBlocks.add(rsEncode(block, ecCodewordsPerBlock))
        }
        // 交错
        val interleaved = IntArray(totalCodewords)
        var pos = 0
        val maxBlockSize = blockSizes.max()
        for (j in 0 until maxBlockSize) {
            for (i in 0 until numBlocks) {
                if (j < blockSizes[i]) {
                    interleaved[pos++] = blocks[i][j]
                }
            }
        }
        for (j in 0 until ecCodewordsPerBlock) {
            for (i in 0 until numBlocks) {
                interleaved[pos++] = ecBlocks[i][j]
            }
        }
        // 创建矩阵
        val moduleCount = version * 4 + 17
        val matrix = Array(moduleCount) { BooleanArray(moduleCount) }
        val isFunction = Array(moduleCount) { BooleanArray(moduleCount) }
        placeFinders(matrix, isFunction, moduleCount)
        placeTimingPatterns(matrix, isFunction, moduleCount)
        if (version >= 2) {
            placeAlignmentPatterns(matrix, isFunction, version, moduleCount)
        }
        reserveFormatAreas(isFunction, moduleCount)
        if (version >= 7) {
            reserveVersionAreas(isFunction, moduleCount)
        }
        placeData(matrix, interleaved, isFunction, moduleCount)
        val bestMask = selectMask(matrix, isFunction, moduleCount)
        applyMask(matrix, isFunction, bestMask, moduleCount)
        placeFormatInfo(matrix, ecLevel, bestMask, moduleCount)
        if (version >= 7) {
            placeVersionInfo(matrix, version, moduleCount)
        }
        return matrix
    }

    private fun encodeData(textBytes: ByteArray, version: Int, capacity: Int): IntArray {
        val len = textBytes.size
        // 位缓冲区
        val bits = mutableListOf<Int>()
        // 模式指示符: 字节模式 = 0100 (4位)
        bits.addAll(listOf(0, 1, 0, 0))
        // 字符计数指示符长度取决于版本
        val countBits = if (version <= 9) 8 else 16
        for (i in countBits - 1 downTo 0) {
            bits.add((len shr i) and 1)
        }
        // 数据 (每个字节8位)
        for (b in textBytes) {
            for (i in 7 downTo 0) {
                bits.add((b.toInt() shr i) and 1)
            }
        }
        // 添加终止符 (最多4位)
        val terminatorLength = minOf(4, capacity * 8 - bits.size)
        repeat(terminatorLength) { bits.add(0) }
        // 填充到字节边界
        while (bits.size % 8 != 0) { bits.add(0) }
        // 转换为字节数组
        val bytes = mutableListOf<Int>()
        var i = 0
        while (i < bits.size) {
            var byte = 0
            for (j in 0 until 8) {
                byte = byte shl 1
                if (i + j < bits.size) {
                    byte = byte or bits[i + j]
                }
            }
            bytes.add(byte)
            i += 8
        }
        // 填充到容量
        val paddingBytes = intArrayOf(0xEC, 0x11)
        var padIdx = 0
        while (bytes.size < capacity) {
            bytes.add(paddingBytes[padIdx % 2])
            padIdx++
        }
        return bytes.toIntArray()
    }

    private fun selectVersion(dataLength: Int, ecLevel: Int): Int {
        // dataLength是原始字节长度，需要加上模式指示符和字符计数的开销
        // 字节模式：4位模式 + 8/16位计数 + 8*dataLength位数据
        for (v in 1..40) {
            val countBits = if (v <= 9) 8 else 16
            val totalBits = 4 + countBits + 8 * dataLength
            val capacityBits = DATA_CODEWORDS[v][ecLevel] * 8
            if (capacityBits >= totalBits) return v
        }
        return 40
    }

    private val LOG = IntArray(256)
    private val EXP = IntArray(512)

    init {
        var x = 1
        for (i in 0 until 255) {
            EXP[i] = x
            LOG[x] = i
            x = x shl 1
            if (x >= 256) x = x xor 0x11d
        }
        for (i in 255 until 512) {
            EXP[i] = EXP[i - 255]
        }
    }

    private fun gfMul(a: Int, b: Int): Int {
        if (a == 0 || b == 0) return 0
        return EXP[(LOG[a] + LOG[b]) % 255]
    }

    internal fun rsEncode(data: IntArray, ecLen: Int): IntArray {
        val gen = rsGeneratorPoly(ecLen)
        val result = IntArray(ecLen)
        for (i in data.indices) {
            val coeff = data[i] xor result[0]
            for (j in 0 until ecLen - 1) {
                result[j] = result[j + 1] xor gfMul(gen[j], coeff)
            }
            result[ecLen - 1] = gfMul(gen[ecLen - 1], coeff)
        }
        return result
    }

    private fun rsGeneratorPoly(degree: Int): IntArray {
        var poly = intArrayOf(1)
        for (i in 0 until degree) {
            val newPoly = IntArray(poly.size + 1)
            for (j in poly.indices) {
                newPoly[j] = newPoly[j] xor gfMul(poly[j], EXP[i])
                newPoly[j + 1] = newPoly[j + 1] xor poly[j]
            }
            poly = newPoly
        }
        return poly.copyOfRange(1, poly.size)
    }

    private fun placeFinders(
        matrix: Array<BooleanArray>,
        isFunction: Array<BooleanArray>,
        size: Int,
    ) {
        for (i in 0..6) {
            for (j in 0..6) {
                val dark = (i == 0 || i == 6 || j == 0 || j == 6) ||
                    (i in 2..4 && j in 2..4)
                matrix[i][j] = dark
                isFunction[i][j] = true
                matrix[i][size - 1 - j] = dark
                isFunction[i][size - 1 - j] = true
                matrix[size - 1 - i][j] = dark
                isFunction[size - 1 - i][j] = true
            }
        }
        matrix[size - 8][8] = true
        isFunction[size - 8][8] = true
        for (i in 0..7) {
            isFunction[7][i] = true
            isFunction[i][7] = true
            isFunction[7][size - 1 - i] = true
            isFunction[size - 1 - i][7] = true
            isFunction[size - 8][i] = true
            isFunction[i][size - 8] = true
        }
    }

    private fun placeTimingPatterns(
        matrix: Array<BooleanArray>,
        isFunction: Array<BooleanArray>,
        size: Int,
    ) {
        // Timing patterns at (6, 8) and (8, 6) intersect with finder separator
        // areas already marked by placeFinders — place them explicitly before the loop
        matrix[6][8] = true
        isFunction[6][8] = true
        matrix[8][6] = true
        isFunction[8][6] = true
        for (i in 8 until size - 8) {
            val dark = i % 2 == 0
            if (!isFunction[6][i]) {
                matrix[6][i] = dark
                isFunction[6][i] = true
            }
            if (!isFunction[i][6]) {
                matrix[i][6] = dark
                isFunction[i][6] = true
            }
        }
    }

    private fun placeAlignmentPatterns(
        matrix: Array<BooleanArray>,
        isFunction: Array<BooleanArray>,
        version: Int,
        size: Int,
    ) {
        val positions = ALIGNMENT_POSITIONS[version] ?: return
        for (row in positions) {
            for (col in positions) {
                if (isFunction[row][col]) continue
                for (dr in -2..2) {
                    for (dc in -2..2) {
                        val dark = dr == -2 || dr == 2 || dc == -2 || dc == 2 || (dr == 0 && dc == 0)
                        val r = row + dr
                        val c = col + dc
                        matrix[r][c] = dark
                        isFunction[r][c] = true
                    }
                }
            }
        }
    }

    private fun reserveFormatAreas(
        isFunction: Array<BooleanArray>,
        size: Int,
    ) {
        // First copy: around top-left finder pattern
        for (i in 0..8) {
            if (i != 6) { // Skip (6, 8) - it's timing pattern
                isFunction[i][8] = true
            }
            if (i != 6) { // Skip (8, 6) - it's timing pattern
                isFunction[8][i] = true
            }
        }
        // Second copy: bottom-left and top-right
        for (i in 0..7) {
            isFunction[size - 1 - i][8] = true
            isFunction[8][size - 1 - i] = true
        }
    }

    private fun reserveVersionAreas(
        isFunction: Array<BooleanArray>,
        size: Int,
    ) {
        for (i in 0..5) {
            for (j in 0..2) {
                isFunction[i][size - 11 + j] = true
                isFunction[size - 11 + j][i] = true
            }
        }
    }

    private fun placeData(
        matrix: Array<BooleanArray>,
        data: IntArray,
        isFunction: Array<BooleanArray>,
        size: Int,
    ) {
        var bitIdx = 0
        var col = size - 1
        var upward = true
        while (col >= 0) {
            if (col == 6) col--
            val rowRange = if (upward) (size - 1 downTo 0) else (0 until size)
            for (row in rowRange) {
                for (dc in 0..1) {
                    val c = col - dc
                    if (c < 0) continue
                    if (isFunction[row][c]) continue
                    if (bitIdx < data.size * 8) {
                        val byteIdx = bitIdx / 8
                        val bitInByte = 7 - (bitIdx % 8)
                        val bit = (data[byteIdx] shr bitInByte) and 1
                        matrix[row][c] = bit == 1
                    }
                    bitIdx++
                }
            }
            col -= 2
            upward = !upward
        }
    }

    private fun selectMask(
        matrix: Array<BooleanArray>,
        isFunction: Array<BooleanArray>,
        size: Int,
    ): Int {
        var bestMask = 0
        var bestPenalty = Int.MAX_VALUE
        for (mask in 0..7) {
            val testMatrix = Array(size) { r -> matrix[r].copyOf() }
            applyMask(testMatrix, isFunction, mask, size)
            val penalty = calculatePenalty(testMatrix, size)
            if (penalty < bestPenalty) {
                bestPenalty = penalty
                bestMask = mask
            }
        }
        return bestMask
    }

    private fun applyMask(
        matrix: Array<BooleanArray>,
        isFunction: Array<BooleanArray>,
        mask: Int,
        size: Int,
    ) {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (isFunction[r][c]) continue
                val invert = when (mask) {
                    0 -> (r + c) % 2 == 0
                    1 -> r % 2 == 0
                    2 -> c % 3 == 0
                    3 -> (r + c) % 3 == 0
                    4 -> (r / 2 + c / 3) % 2 == 0
                    5 -> (r * c) % 2 + (r * c) % 3 == 0
                    6 -> ((r * c) % 2 + (r * c) % 3) % 2 == 0
                    7 -> ((r + c) % 2 + (r * c) % 3) % 2 == 0
                    else -> false
                }
                if (invert) matrix[r][c] = !matrix[r][c]
            }
        }
    }

    private fun calculatePenalty(matrix: Array<BooleanArray>, size: Int): Int {
        var penalty = 0
        for (r in 0 until size) {
            var run = 1
            for (c in 1 until size) {
                if (matrix[r][c] == matrix[r][c - 1]) {
                    run++
                } else {
                    if (run >= 5) penalty += run - 2
                    run = 1
                }
            }
            if (run >= 5) penalty += run - 2
        }
        for (c in 0 until size) {
            var run = 1
            for (r in 1 until size) {
                if (matrix[r][c] == matrix[r - 1][c]) {
                    run++
                } else {
                    if (run >= 5) penalty += run - 2
                    run = 1
                }
            }
            if (run >= 5) penalty += run - 2
        }
        for (r in 0 until size - 1) {
            for (c in 0 until size - 1) {
                val color = matrix[r][c]
                if (color == matrix[r][c + 1] &&
                    color == matrix[r + 1][c] &&
                    color == matrix[r + 1][c + 1]
                ) {
                    penalty += 3
                }
            }
        }
        return penalty
    }

    private fun placeFormatInfo(
        matrix: Array<BooleanArray>,
        ecLevel: Int,
        mask: Int,
        size: Int,
    ) {
        val data = ((ecLevel xor 1) shl 3) or mask
        var bits = data shl 10
        val gen = 0x537
        for (i in 4 downTo 0) {
            if (bits and (1 shl (i + 10)) != 0) {
                bits = bits xor (gen shl i)
            }
        }
        bits = ((data shl 10) or bits) xor 0x5412
        // First copy: bits 0-6 in column 8 (skip row 6), bit 7 at corner, bits 8-14 in row 8
        for (i in 0..5) {
            matrix[i][8] = (bits shr i) and 1 == 1
        }
        matrix[7][8] = (bits shr 6) and 1 == 1
        matrix[8][8] = (bits shr 7) and 1 == 1
        for (i in 8..14) {
            matrix[8][14 - i] = (bits shr i) and 1 == 1
        }
        // Second copy: bits 0-7 in row 8 (right), bits 8-14 in column 8 (below first copy)
        for (i in 0..7) {
            matrix[8][size - 1 - i] = (bits shr i) and 1 == 1
        }
        for (i in 8..14) {
            // Rows 9..15, avoiding first copy positions (rows 0-5, 7, 8)
            matrix[i + 1][8] = (bits shr i) and 1 == 1
        }
    }

    private fun placeVersionInfo(
        matrix: Array<BooleanArray>,
        version: Int,
        size: Int,
    ) {
        if (version < 7) return
        var bits = version.toLong() shl 12
        val gen = 0x1F25L
        for (i in 5 downTo 0) {
            if (bits and (1L shl (i + 12)) != 0L) {
                bits = bits xor (gen shl i)
            }
        }
        bits = (version.toLong() shl 12) or bits
        for (i in 0..17) {
            val row = i / 3
            val col = size - 11 + (i % 3)
            val dark = (bits shr i) and 1L == 1L
            matrix[row][col] = dark
            matrix[col][row] = dark
        }
    }

    internal val ALIGNMENT_POSITIONS: Map<Int, IntArray> = buildMap {
        put(2, intArrayOf(6, 18))
        put(3, intArrayOf(6, 22))
        put(4, intArrayOf(6, 26))
        put(5, intArrayOf(6, 30))
        put(6, intArrayOf(6, 34))
        put(7, intArrayOf(6, 22, 38))
        put(8, intArrayOf(6, 24, 42))
        put(9, intArrayOf(6, 26, 46))
        put(10, intArrayOf(6, 28, 50))
        put(11, intArrayOf(6, 30, 54))
        put(12, intArrayOf(6, 32, 58))
        put(13, intArrayOf(6, 34, 62))
        put(14, intArrayOf(6, 26, 46, 66))
        put(15, intArrayOf(6, 26, 48, 70))
        put(16, intArrayOf(6, 26, 50, 74))
        put(17, intArrayOf(6, 30, 54, 78))
        put(18, intArrayOf(6, 30, 56, 82))
        put(19, intArrayOf(6, 30, 58, 86))
        put(20, intArrayOf(6, 34, 62, 90))
        put(21, intArrayOf(6, 28, 50, 72, 94))
        put(22, intArrayOf(6, 26, 50, 74, 98))
        put(23, intArrayOf(6, 30, 54, 78, 102))
        put(24, intArrayOf(6, 28, 54, 80, 106))
        put(25, intArrayOf(6, 32, 58, 84, 110))
        put(26, intArrayOf(6, 30, 58, 86, 114))
        put(27, intArrayOf(6, 34, 62, 90, 118))
        put(28, intArrayOf(6, 26, 50, 74, 98, 122))
        put(29, intArrayOf(6, 30, 54, 78, 102, 126))
        put(30, intArrayOf(6, 26, 52, 78, 104, 130))
        put(31, intArrayOf(6, 30, 56, 82, 108, 134))
        put(32, intArrayOf(6, 34, 60, 86, 112, 138))
        put(33, intArrayOf(6, 30, 58, 86, 114, 142))
        put(34, intArrayOf(6, 34, 62, 90, 118, 146))
        put(35, intArrayOf(6, 30, 54, 78, 102, 126, 150))
        put(36, intArrayOf(6, 24, 50, 76, 102, 128, 154))
        put(37, intArrayOf(6, 28, 54, 80, 106, 132, 158))
        put(38, intArrayOf(6, 32, 58, 84, 110, 136, 162))
        put(39, intArrayOf(6, 26, 54, 82, 110, 138, 166))
        put(40, intArrayOf(6, 30, 58, 86, 114, 142, 170))
    }

    internal val TOTAL_CODEWORDS: IntArray = intArrayOf(
        0,
        26, 44, 70, 100, 134, 172, 196, 242, 292, 346,
        404, 466, 532, 581, 655, 733, 815, 901, 991, 1085,
        1156, 1258, 1364, 1474, 1588, 1706, 1828, 1921, 2051, 2185,
        2323, 2465, 2611, 2761, 2876, 3034, 3196, 3362, 3532, 3706,
    )

    internal val DATA_CODEWORDS: Array<IntArray> = arrayOf(
        intArrayOf(),
        intArrayOf(19, 16, 13, 9),
        intArrayOf(34, 28, 22, 16),
        intArrayOf(55, 44, 34, 26),
        intArrayOf(80, 64, 48, 36),
        intArrayOf(108, 86, 62, 46),
        intArrayOf(136, 108, 76, 60),
        intArrayOf(156, 124, 88, 66),
        intArrayOf(194, 154, 110, 86),
        intArrayOf(232, 182, 132, 100),
        intArrayOf(274, 216, 154, 122),
        intArrayOf(324, 254, 180, 140),
        intArrayOf(370, 290, 206, 158),
        intArrayOf(428, 334, 244, 180),
        intArrayOf(461, 365, 261, 197),
        intArrayOf(523, 415, 295, 223),
        intArrayOf(589, 453, 325, 253),
        intArrayOf(647, 507, 367, 283),
        intArrayOf(721, 563, 397, 313),
        intArrayOf(795, 627, 445, 341),
        intArrayOf(861, 669, 485, 385),
        intArrayOf(932, 714, 512, 406),
        intArrayOf(1006, 782, 568, 442),
        intArrayOf(1094, 860, 614, 464),
        intArrayOf(1174, 914, 664, 514),
        intArrayOf(1276, 1000, 718, 538),
        intArrayOf(1370, 1062, 754, 596),
        intArrayOf(1468, 1128, 808, 628),
        intArrayOf(1531, 1193, 871, 661),
        intArrayOf(1631, 1267, 911, 701),
        intArrayOf(1735, 1373, 985, 745),
        intArrayOf(1843, 1455, 1033, 793),
        intArrayOf(1955, 1541, 1115, 845),
        intArrayOf(2071, 1631, 1171, 901),
        intArrayOf(2191, 1725, 1231, 961),
        intArrayOf(2306, 1812, 1286, 986),
        intArrayOf(2434, 1914, 1354, 1054),
        intArrayOf(2566, 1992, 1426, 1096),
        intArrayOf(2702, 2102, 1502, 1142),
        intArrayOf(2812, 2216, 1582, 1222),
        intArrayOf(2956, 2334, 1666, 1276),
    )

    internal val EC_CODEWORDS_PER_BLOCK: Array<IntArray> = arrayOf(
        intArrayOf(),
        intArrayOf(7, 10, 13, 17),
        intArrayOf(10, 16, 22, 28),
        intArrayOf(15, 26, 18, 22),
        intArrayOf(20, 18, 26, 16),
        intArrayOf(26, 24, 18, 22),
        intArrayOf(18, 16, 24, 28),
        intArrayOf(20, 18, 18, 26),
        intArrayOf(24, 22, 22, 26),
        intArrayOf(30, 22, 20, 24),
        intArrayOf(18, 26, 24, 28),
        intArrayOf(20, 30, 28, 24),
        intArrayOf(24, 22, 26, 28),
        intArrayOf(26, 22, 24, 22),
        intArrayOf(30, 24, 20, 24),
        intArrayOf(22, 24, 30, 24),
        intArrayOf(24, 28, 24, 30),
        intArrayOf(28, 28, 28, 28),
        intArrayOf(30, 26, 28, 28),
        intArrayOf(28, 26, 26, 26),
        intArrayOf(28, 26, 28, 28),
        intArrayOf(28, 26, 28, 28),
        intArrayOf(28, 28, 28, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(26, 28, 30, 30),
        intArrayOf(28, 28, 28, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
        intArrayOf(30, 28, 30, 30),
    )

    internal val NUM_EC_BLOCKS: Array<IntArray> = arrayOf(
        intArrayOf(),
        intArrayOf(1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1),
        intArrayOf(1, 1, 2, 2),
        intArrayOf(1, 2, 2, 4),
        intArrayOf(1, 2, 4, 4),
        intArrayOf(2, 4, 4, 4),
        intArrayOf(2, 4, 6, 5),
        intArrayOf(2, 4, 6, 6),
        intArrayOf(2, 5, 8, 8),
        intArrayOf(4, 5, 8, 8),
        intArrayOf(4, 5, 8, 11),
        intArrayOf(4, 8, 10, 11),
        intArrayOf(4, 9, 12, 16),
        intArrayOf(4, 9, 16, 16),
        intArrayOf(6, 10, 12, 18),
        intArrayOf(6, 10, 17, 16),
        intArrayOf(6, 11, 16, 19),
        intArrayOf(6, 13, 18, 21),
        intArrayOf(7, 14, 21, 25),
        intArrayOf(8, 16, 20, 25),
        intArrayOf(8, 17, 23, 25),
        intArrayOf(9, 17, 23, 34),
        intArrayOf(9, 18, 25, 30),
        intArrayOf(10, 20, 27, 32),
        intArrayOf(12, 21, 29, 35),
        intArrayOf(12, 23, 34, 37),
        intArrayOf(12, 25, 34, 40),
        intArrayOf(13, 26, 35, 42),
        intArrayOf(14, 28, 38, 45),
        intArrayOf(15, 29, 40, 48),
        intArrayOf(16, 31, 43, 51),
        intArrayOf(17, 33, 45, 54),
        intArrayOf(18, 35, 48, 57),
        intArrayOf(19, 37, 51, 60),
        intArrayOf(19, 38, 53, 63),
        intArrayOf(20, 40, 56, 66),
        intArrayOf(21, 43, 59, 70),
        intArrayOf(22, 45, 62, 74),
        intArrayOf(24, 47, 65, 77),
        intArrayOf(25, 49, 68, 81),
    )
}
