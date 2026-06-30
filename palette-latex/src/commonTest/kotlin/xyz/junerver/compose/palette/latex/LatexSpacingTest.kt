package xyz.junerver.compose.palette.latex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 验证 TeX 数学间距规则（TeXbook §442–446）。
 *
 * 用确定性 fake measurer（每字符宽 10px、ascent/descent 固定），断言：
 * - 二元/关系运算符两侧有间距
 * - 标点之后有间距、之前无间距
 * - 开/闭定界符紧贴内容
 * - 行首运算符（+/-）降级为 ORD，不加左间距
 */
class LatexSpacingTest {
    private val measurer = LatexTextMeasurer { text, style ->
        val size = style.sizePx
        val n = text.length.coerceAtLeast(1)
        LatexMetrics(width = size * n, ascent = size * 0.8f, descent = size * 0.2f)
    }
    private val size = 10f

    private fun widthOf(src: String): Float =
        LatexLayoutEngine.layout(LatexParser.parse(src), measurer, size).box.width

    @Test
    fun binaryOperatorAddsSpaceOnBothSides() {
        // ab（两个普通原子）之间间距为 0，宽 = 2 字符
        val noSpace = widthOf("ab")
        assertEquals(2f * size, noSpace, 0.01f)
        // a+b：+ 是 BIN，两侧各 0.2222em。三个字符 + 两侧间距
        val withBin = widthOf("a+b")
        assertTrue(withBin > noSpace, "a+b 应比 ab 更宽（BIN 两侧有间距）")
        assertEquals(3f * size + 2f * 0.2222f * size, withBin, 0.01f)
    }

    @Test
    fun relationOperatorHasWiderSpacing() {
        val rel = widthOf("a=b") // REL 两侧各 0.2778em
        val bin = widthOf("a+b") // BIN 两侧各 0.2222em
        assertTrue(rel > bin, "关系符间距（0.2778em）应宽于二元运算符（0.2222em）")
    }

    @Test
    fun punctuationAddsSpaceAfterOnly() {
        // a,b：三个字符；逗号（PUNCT）之后加 0.1667em，之前无间距
        val punct = widthOf("a,b")
        // ab 两字符 = 20；a,b = 30（三字符）+ 0.1667*size
        assertEquals(3f * size + 0.1667f * size, punct, 0.01f)
    }

    @Test
    fun openDelimiterHasNoSpaceAfter() {
        // (a：开定界符之后不加间距
        val open = widthOf("(a")
        assertEquals(2f * size, open, 0.01f, "开定界符 ( 之后不加间距")
    }

    @Test
    fun closeDelimiterHasNoSpaceBefore() {
        // a)：闭定界符之前不加间距
        val close = widthOf("a)")
        assertEquals(2f * size, close, 0.01f, "闭定界符 ) 之前不加间距")
    }

    @Test
    fun leadingAndTrailingBinaryOperatorDowngradesToOrd() {
        // 行首 +a 与 行尾 a+：+ 都因无左 / 右操作数降级为 ORD，不加间距 → 宽 = 2*size。
        // 而 a+b（+ 两侧有操作数）才有 BIN 间距。这是 TeX 规则的关键。
        val leading = widthOf("+a")
        val trailing = widthOf("a+")
        val middle = widthOf("a+b")
        assertEquals(2f * size, leading, 0.01f, "行首 +a 应无间距")
        assertEquals(2f * size, trailing, 0.01f, "行尾 a+ 应无间距")
        assertTrue(leading < middle, "行首 +a 应窄于 a+b（后者有 BIN 间距）")
    }

    @Test
    fun parenthesesAreTightAroundContent() {
        // (a+b)：内部 a+b 有 BIN 间距，但 ( 之后、) 之前均无间距
        val tight = widthOf("(a+b)")
        val loose = widthOf("a+b")
        // 宽度差 = 两个定界符宽（各 size）— 内部间距相同
        assertEquals(loose + 2f * size, tight, 0.01f, "定界符内外的额外宽度应仅来自定界符本身")
    }


    @Test
    fun greekVariableFollowedByRel() {
        // \alpha = x：REL 两侧间距
        val spaced = widthOf("\\alpha=x")
        val tight = widthOf("\\alpha x")
        assertTrue(spaced > tight, "希腊字母 + 关系符应有间距")
    }
}
