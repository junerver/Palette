package xyz.junerver.compose.palette.latex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LatexLayoutTest {
    /**
     * 可预测的度量器：每个字符宽 0.6*size，ascent 0.8*size，descent 0.2*size。
     * 这样布局断言可基于字符数与缩放系数精确计算。
     */
    private val measurer = LatexTextMeasurer { text, style ->
        val size = style.sizePx
        val charCount = text.length.coerceAtLeast(1)
        LatexMetrics(
            width = size * 0.6f * charCount,
            ascent = size * 0.8f,
            descent = size * 0.2f,
        )
    }

    private val baseSize = 20f

    @Test
    fun singleGlyphBoxHasExpectedMetrics() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("x"), measurer, baseSize)
        val box = layout.box as LatexGlyphBox
        assertEquals(baseSize * 0.6f, box.width)
        assertEquals(baseSize, box.height)
        assertEquals(baseSize * 0.8f, box.baseline)
    }

    @Test
    fun horizontalLayoutAlignsBaselines() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("ab"), measurer, baseSize)
        val h = layout.box as LatexHorizontalBox
        assertEquals(2, h.items.size)
        // 两个字符基线对齐：y 偏移应相等
        assertEquals(h.items[0].y, h.items[1].y)
        // 总宽至少容纳两字符，且第二项在第一项之后
        assertTrue(h.width >= baseSize * 0.6f * 2f, "总宽应 >= 两字符宽之和")
        assertTrue(h.items[1].x > h.items[0].x, "第二项应排在第一项之后")
    }

    @Test
    fun fractionRuleSpansMaxOfNumeratorAndDenominator() {
        val layout = LatexLayoutEngine.layout(
            LatexParser.parse("\\frac{abc}{d}"),
            measurer,
            baseSize,
        )
        val frac = layout.box as LatexFractionBox
        val numWidth = frac.numerator.box.width
        val denWidth = frac.denominator.box.width
        // 分子 "abc" 宽于分母 "d"
        assertTrue(numWidth > denWidth)
        // 分数总宽 = max(分子宽, 分母宽)
        assertEquals(numWidth, frac.width)
        // 分子居中：x = (width - numWidth)/2 = 0
        assertEquals(0f, frac.numerator.x)
        // 分母居中：x = (width - denWidth)/2
        assertEquals((frac.width - denWidth) / 2f, frac.denominator.x)
    }

    @Test
    fun superscriptIsSmallerThanBase() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("x^2"), measurer, baseSize)
        val h = layout.box as LatexHorizontalBox
        // base + sup
        val sup = h.items.first { it.box is LatexGlyphBox && (it.box as LatexGlyphBox).text == "2" }
        val base = h.items.first { it.box is LatexGlyphBox && (it.box as LatexGlyphBox).text == "x" }
        val supBox = sup.box as LatexGlyphBox
        val baseBox = base.box as LatexGlyphBox
        assertTrue(supBox.sizePx < baseBox.sizePx, "上标字号应小于底数字号")
        assertEquals(baseSize * 0.7f, supBox.sizePx)
        // 上标位于底数上方（y 更小）
        assertTrue(sup.y < base.y, "上标应位于底数基线之上")
    }

    @Test
    fun sqrtPlacesRadicandAfterSign() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("\\sqrt{x}"), measurer, baseSize)
        val root = layout.box as LatexRootBox
        // 被开方数 x 偏移在根号符号右侧之后
        assertTrue(root.radicand.x > root.signOffsetX)
        assertEquals(root.signOffsetX + root.signWidth, root.radicand.x)
    }

    @Test
    fun bigOperatorCentersSubAndSup() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("\\sum_{i}^{n}"), measurer, baseSize)
        val op = layout.box as LatexBigOperatorBox
        assertEquals("∑", op.glyph)
        // 符号居中
        assertTrue(op.glyphOffsetX >= 0f)
        // 上标在符号上方，下标在符号下方
        val sup = op.sup!!
        val sub = op.sub!!
        assertTrue(sup.y < op.glyphOffsetY, "上标应在符号上方")
        assertTrue(sub.y > op.glyphOffsetY, "下标应在符号下方")
    }

    @Test
    fun delimitedWrapsInnerWithDelimiters() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("\\left(a\\right)"), measurer, baseSize)
        val del = layout.box as LatexDelimitedBox
        assertEquals("(", del.leftGlyph)
        assertEquals(")", del.rightGlyph)
        // 内部内容在左定界符之后、右定界符之前
        assertTrue(del.inner.x > del.leftOffsetX)
        assertTrue(del.rightOffsetX > del.inner.x)
    }

    @Test
    fun displayStyleEnlargesSize() {
        val inlineLayout = LatexLayoutEngine.layout(LatexParser.parse("x"), measurer, baseSize)
        val displayLayout = LatexLayoutEngine.layout(
            LatexParser.parse("x"),
            measurer,
            baseSize,
            displayStyle = true,
        )
        val inlineBox = inlineLayout.box as LatexGlyphBox
        val displayBox = displayLayout.box as LatexGlyphBox
        assertTrue(displayBox.sizePx > inlineBox.sizePx, "显示模式字号应放大")
        assertEquals(baseSize * 1.2f, displayBox.sizePx)
    }

    @Test
    fun accentCoversBaseWidth() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse("\\overline{x}"), measurer, baseSize)
        val accent = layout.box as LatexAccentBox
        // 装饰宽度至少覆盖 base 宽度
        assertTrue(accent.accentWidth >= accent.base.box.width)
    }

    @Test
    fun emptyExpressionProducesZeroWidth() {
        val layout = LatexLayoutEngine.layout(LatexParser.parse(""), measurer, baseSize)
        assertIs<LatexSpacingBox>(layout.box)
        assertEquals(0f, layout.box.width)
    }
}
