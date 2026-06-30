package xyz.junerver.compose.palette.latex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LatexParserTest {
    /**
     * 解包单元素分组：`{a}` → `a`，便于断言内部类型。
     * 解析器会保留显式 `{}` 作为 LatexGroup，这是正确行为；测试侧解包方便断言。
     */
    private fun unwrap(expr: LatexExpr): LatexExpr =
        if (expr is LatexGroup && expr.children.size == 1) expr.children[0] else expr

    @Test
    fun parsesSingleCharacterAsItalicVariable() {
        val expr = LatexParser.parse("x")
        assertIs<LatexCharacter>(expr)
        assertEquals('x', expr.char)
        assertTrue(expr.italic)
    }

    @Test
    fun parsesDigitsAsUprightTextRun() {
        // 多位数字合并为一个 LatexTextRun，并因单一顶层项被 unwrap
        val expr = LatexParser.parse("123") as LatexTextRun
        assertEquals("123", expr.text)
    }

    @Test
    fun parsesSimpleFraction() {
        val expr = LatexParser.parse("\\frac{a}{b}") as LatexFraction
        assertEquals('a', (unwrap(expr.numerator) as LatexCharacter).char)
        assertEquals('b', (unwrap(expr.denominator) as LatexCharacter).char)
    }

    @Test
    fun parsesFractionWithExplicitGroups() {
        val expr = LatexParser.parse("\\frac{1}{2}") as LatexFraction
        assertEquals("1", (unwrap(expr.numerator) as LatexTextRun).text)
        assertEquals("2", (unwrap(expr.denominator) as LatexTextRun).text)
    }

    @Test
    fun parsesSingleTokenFractionArgumentsOneCharacterAtATime() {
        val expr = LatexParser.parse("\\frac12") as LatexFraction
        assertEquals("1", (unwrap(expr.numerator) as LatexTextRun).text)
        assertEquals("2", (unwrap(expr.denominator) as LatexTextRun).text)
    }

    @Test
    fun parsesSuperscript() {
        val expr = LatexParser.parse("x^2") as LatexSubSup
        assertEquals('x', (expr.base as LatexCharacter).char)
        assertEquals("2", (unwrap(expr.sup!!) as LatexTextRun).text)
        assertNull(expr.sub)
    }

    @Test
    fun parsesUngroupedScriptAsSingleCharacterAndKeepsRemainder() {
        val expr = LatexParser.parse("x^12") as LatexGroup
        val scripted = expr.children[0] as LatexSubSup
        assertEquals("1", (unwrap(scripted.sup!!) as LatexTextRun).text)
        assertEquals("2", (expr.children[1] as LatexTextRun).text)
    }

    @Test
    fun parsesSubscript() {
        val expr = LatexParser.parse("x_1") as LatexSubSup
        assertNull(expr.sup)
        assertEquals("1", (unwrap(expr.sub!!) as LatexTextRun).text)
    }

    @Test
    fun mergesSubAndSupIntoSingleNode() {
        // x_a^2 应合并为单个 LatexSubSup
        val expr = LatexParser.parse("x_a^2") as LatexSubSup
        assertEquals('x', (expr.base as LatexCharacter).char)
        assertEquals('a'.toString(), (unwrap(expr.sub!!) as LatexCharacter).char.toString())
        assertEquals("2", (unwrap(expr.sup!!) as LatexTextRun).text)
    }

    @Test
    fun parsesGroupedSuperscript() {
        val expr = LatexParser.parse("x^{ab}") as LatexSubSup
        val sup = expr.sup as LatexGroup
        assertEquals(2, sup.children.size)
    }

    @Test
    fun parsesSquareRoot() {
        val expr = LatexParser.parse("\\sqrt{x}") as LatexRoot
        assertEquals('x', (unwrap(expr.radicand) as LatexCharacter).char)
        assertNull(expr.index)
    }

    @Test
    fun parsesNthRoot() {
        val expr = LatexParser.parse("\\sqrt[3]{x}") as LatexRoot
        assertEquals('x', (unwrap(expr.radicand) as LatexCharacter).char)
        assertEquals("3", (unwrap(expr.index!!) as LatexTextRun).text)
    }

    @Test
    fun parsesGreekLetter() {
        val expr = LatexParser.parse("\\alpha") as LatexSymbol
        assertEquals("α", expr.glyph)
        assertTrue(expr.italic)
    }

    @Test
    fun parsesUppercaseGreekAsUpright() {
        val expr = LatexParser.parse("\\Sigma") as LatexSymbol
        assertEquals("Σ", expr.glyph)
        assertTrue(!expr.italic)
    }

    @Test
    fun parsesOperator() {
        val expr = LatexParser.parse("\\leq") as LatexSymbol
        assertEquals("≤", expr.glyph)
    }

    @Test
    fun parsesBigOperatorWithLimits() {
        val expr = LatexParser.parse("\\sum_{i=0}^{n}") as LatexBigOperator
        assertEquals("∑", expr.operator)
        assertIs<LatexGroup>(expr.sub)
        assertIs<LatexGroup>(expr.sup)
    }

    @Test
    fun parsesDelimited() {
        val expr = LatexParser.parse("\\left( a + b \\right)") as LatexDelimited
        assertEquals("(", expr.left)
        assertEquals(")", expr.right)
        // a, +, b（词法会把 "a" 单独成词，"+" 单独，"b" 单独）
        assertEquals(3, expr.inner.size)
    }

    @Test
    fun parsesText() {
        // 注意：v1 数学模式词法会吞掉普通空白，\text{} 内多词会折叠（多数 MathJax
        // 配置下数学空白同样不可见）。单词 / 含显式间距命令的文本不受影响。
        val expr = LatexParser.parse("\\text{helloworld}") as LatexText
        assertEquals("helloworld", expr.content)
    }

    @Test
    fun parsesAccent() {
        val expr = LatexParser.parse("\\hat{x}") as LatexAccent
        assertEquals(LatexAccentKind.HAT, expr.accent)
        assertEquals('x', (unwrap(expr.base) as LatexCharacter).char)
    }

    @Test
    fun parsesBinom() {
        val expr = LatexParser.parse("\\binom{n}{k}") as LatexBinom
        assertEquals('n', (unwrap(expr.upper) as LatexCharacter).char)
        assertEquals('k', (unwrap(expr.lower) as LatexCharacter).char)
    }

    @Test
    fun parsesSpacingCommands() {
        val expr = LatexParser.parse("a\\,b") as LatexGroup
        // [a, spacing, b]
        assertEquals(3, expr.children.size)
        assertIs<LatexSpacing>(expr.children[1])
    }

    @Test
    fun escapedSymbolsBecomeLiterals() {
        // \% → CommandSymbol，解析为 LatexSymbol
        val expr = LatexParser.parse("\\%") as LatexSymbol
        assertEquals("%", expr.glyph)
    }

    @Test
    fun unknownCommandFallsBackToRaw() {
        val expr = LatexParser.parse("\\foobar") as LatexRaw
        assertEquals("\\foobar", expr.source)
    }

    @Test
    fun functionNamesRenderAsText() {
        val expr = LatexParser.parse("\\sin") as LatexText
        assertEquals("sin", expr.content)
    }

    @Test
    fun supOnGreekLetter() {
        val expr = LatexParser.parse("\\theta^2") as LatexSubSup
        assertEquals("θ", (expr.base as LatexSymbol).glyph)
        assertEquals("2", (unwrap(expr.sup!!) as LatexTextRun).text)
    }

    @Test
    fun emptyInputReturnsEmptyGroup() {
        val expr = LatexParser.parse("") as LatexGroup
        assertTrue(expr.children.isEmpty())
    }

    @Test
    fun mixedExpression() {
        // a^2 + b^2 = c^2
        val expr = LatexParser.parse("a^2+b^2=c^2") as LatexGroup
        // 期望包含 5 个顶层项: a^2, +, b^2, =, c^2
        assertEquals(5, expr.children.size)
        assertIs<LatexSubSup>(expr.children[0])
        assertIs<LatexCharacter>(expr.children[1])
    }

    @Test
    fun parsesOverset() {
        val expr = LatexParser.parse("\\overset{f}{=}") as LatexOverset
        assertEquals('f', (unwrap(expr.over) as LatexCharacter).char)
        assertEquals('=', (unwrap(expr.base) as LatexCharacter).char)
    }

    @Test
    fun parsesUnderset() {
        val expr = LatexParser.parse("\\underset{x}{X}") as LatexUnderset
        assertEquals('x', (unwrap(expr.under) as LatexCharacter).char)
        assertEquals('X', (unwrap(expr.base) as LatexCharacter).char)
    }

    @Test
    fun parsesSubstack() {
        val expr = LatexParser.parse("\\substack{a \\\\ b}") as LatexSubstack
        assertEquals(2, expr.rows.size)
    }

    @Test
    fun symbolCommandsCarryAtomType() {
        // 验证扩表后解析出的符号携带正确的原子类型
        val leq = LatexParser.parse("\\leq")
        assertIs<LatexSymbol>(leq)
        assertEquals(LatexAtom.REL, leq.atom)
        val times = LatexParser.parse("\\times")
        assertIs<LatexSymbol>(times)
        assertEquals(LatexAtom.BIN, times.atom)
    }

    @Test
    fun asciiOperatorsCarryAtomType() {
        val plus = LatexParser.parse("+") as LatexCharacter
        assertEquals(LatexAtom.BIN, plus.atom)
        val eq = LatexParser.parse("=") as LatexCharacter
        assertEquals(LatexAtom.REL, eq.atom)
    }
}
