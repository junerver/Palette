package xyz.junerver.compose.palette.latex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LatexSymbolsTest {
    @Test
    fun relationSymbolResolvesToRelAtom() {
        val (glyph, atom) = LatexSymbols.symbolOf("leq")!!
        assertEquals("≤", glyph)
        assertEquals(LatexAtom.REL, atom)
    }

    @Test
    fun binaryOperatorResolvesToBinAtom() {
        val (glyph, atom) = LatexSymbols.symbolOf("times")!!
        assertEquals("×", glyph)
        assertEquals(LatexAtom.BIN, atom)
    }

    @Test
    fun openDelimiterResolvesToOpenAtom() {
        val (glyph, atom) = LatexSymbols.symbolOf("langle")!!
        assertEquals("⟨", glyph)
        assertEquals(LatexAtom.OPEN, atom)
    }

    @Test
    fun closeDelimiterResolvesToCloseAtom() {
        val (_, atom) = LatexSymbols.symbolOf("rfloor")!!
        assertEquals(LatexAtom.CLOSE, atom)
    }

    @Test
    fun bigOperatorResolvesToOpAtom() {
        // \sum 既在 symbols 表（OP）又在 bigOperators 表
        val (_, atom) = LatexSymbols.symbolOf("sum")!!
        assertEquals(LatexAtom.OP, atom)
        assertEquals("∑", LatexSymbols.bigOperatorOf("sum"))
    }

    @Test
    fun amsNegatedRelationResolves() {
        // 借鉴 KaTeX 的 AMS 负向关系
        val (glyph, atom) = LatexSymbols.symbolOf("nless")!!
        assertEquals("≮", glyph)
        assertEquals(LatexAtom.REL, atom)
    }

    @Test
    fun longArrowResolves() {
        val (glyph, _) = LatexSymbols.symbolOf("longrightarrow")!!
        assertEquals("⟶", glyph)
    }

    @Test
    fun punctResolvesToPunctAtom() {
        val (_, atom) = LatexSymbols.symbolOf("ldotp")!!
        assertEquals(LatexAtom.PUNCT, atom)
    }

    @Test
    fun textordSymbolResolvesToOrdAtom() {
        val (glyph, atom) = LatexSymbols.symbolOf("infty")!!
        assertEquals("∞", glyph)
        assertEquals(LatexAtom.ORD, atom)
    }

    @Test
    fun expandedTableCoversManyCommands() {
        // 扩表后命令数应远超首版（首版约 60），验证覆盖广度
        assertTrue(LatexSymbols.symbols.size > 150, "命令表应覆盖 150+ 条，实际 ${LatexSymbols.symbols.size}")
    }

    @Test
    fun greekLowercaseIsItalic() {
        val (glyph, italic) = LatexSymbols.greekOf("alpha")!!
        assertEquals("α", glyph)
        assertTrue(italic)
    }

    @Test
    fun unknownCommandReturnsNull() {
        assertEquals(null, LatexSymbols.symbolOf("notACommand"))
        assertEquals(null, LatexSymbols.greekOf("notAGreek"))
    }

    @Test
    fun accentsCoverCommonDecorations() {
        assertNotNull(LatexSymbols.accents["hat"])
        assertNotNull(LatexSymbols.accents["vec"])
        assertNotNull(LatexSymbols.accents["overline"])
        assertNotNull(LatexSymbols.accents["check"])
    }
}
