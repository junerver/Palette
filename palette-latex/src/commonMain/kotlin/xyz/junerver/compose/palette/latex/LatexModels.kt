package xyz.junerver.compose.palette.latex

/**
 * LaTeX 数学原子类型，对齐 KaTeX / TeXbook 的 group 分类。
 *
 * 用于数学间距规则（TeXbook §442–446）：相邻两个原子的类型决定它们之间的间距。
 */
public enum class LatexAtom {
    /** 普通原子（变量、数字、函数结果）。 */
    ORD,
    /** 大算符（\sum \int \prod）。 */
    OP,
    /** 二元运算符（+ - × \cdot）。 */
    BIN,
    /** 关系符（= < ≤ \to）。 */
    REL,
    /** 开定界符（( [ { \langle）。 */
    OPEN,
    /** 闭定界符（) ] } \rangle）。 */
    CLOSE,
    /** 标点（, ;）。 */
    PUNCT,
    /** 内部原子（\frac、\left...\right 整体、\binom）。 */
    INNER,
}

/**
 * LaTeX 数学表达式的抽象语法树 (AST)。
 *
 * 采用 sealed interface 结构：每条命令对应一个节点类型，新增命令只需新增一个节点 + 一个绘制分支。
 * 解析器把 LaTeX 源码（`LatexParser.parse`）转换为 [LatexExpr]，布局引擎再遍历它生成盒模型。
 */
public sealed interface LatexExpr {
    /**
     * 该表达式的所有子节点，供布局/绘制遍历使用。
     */
    public val children: List<LatexExpr>

    /**
     * 该表达式作为"原子"参与数学间距规则时的类型。复合结构（分数、根号等）默认为 [LatexAtom.ORD]；
     * 单字符 / 符号会根据 KaTeX 命令表填入精确类型。
     */
    public val atom: LatexAtom
        get() = LatexAtom.ORD
}

/**
 * 普通 ASCII 字符（变量、数字、运算符的 ASCII 形式，如 `x` `2` `+`）。
 *
 * 单字符变量默认按数学斜体渲染；数字按正体渲染。
 */
public data class LatexCharacter(
    val char: Char,
    /** 是否按变量斜体渲染；数字/运算符为 false。 */
    val italic: Boolean,
    /** 该字符的原子类型，参与数学间距规则。字母/数字默认 [LatexAtom.ORD]，`+`→BIN、`=`→REL 等。 */
    override val atom: LatexAtom = LatexAtom.ORD,
) : LatexExpr {
    override val children: List<LatexExpr> get() = emptyList()
}

/**
 * 通过 `\command` 引入的特殊符号（希腊字母、运算符 ∑∫√、关系符 ≤≠∈ 等）。
 * [glyph] 是已映射好的 Unicode 字形，[italic] 控制是否斜体（希腊小写默认斜体）。
 */
public data class LatexSymbol(
    val glyph: String,
    val italic: Boolean = false,
    /** 该符号的原子类型，从 KaTeX 命令表查得（如 `\leq`→REL、`\times`→BIN）。 */
    override val atom: LatexAtom = LatexAtom.ORD,
) : LatexExpr {
    override val children: List<LatexExpr> get() = emptyList()
}

/** 一段连续普通字符（来自非斜体上下文，如 `\text{}`、`\mathrm{}` 内部）。 */
public data class LatexTextRun(
    val text: String,
) : LatexExpr {
    override val children: List<LatexExpr> get() = emptyList()
}

/** 分组 `{...}`：仅作为作用域边界，布局上等价于其子节点的水平拼接。 */
public data class LatexGroup(
    override val children: List<LatexExpr>,
) : LatexExpr

/** 分数 `\frac{num}{den}` 或 `{num}\over{den}`。 */
public data class LatexFraction(
    val numerator: LatexExpr,
    val denominator: LatexExpr,
) : LatexExpr {
    override val children: List<LatexExpr> get() = listOf(numerator, denominator)
}

/** 根号 `\sqrt{radicand}` 或 n 次根 `\sqrt[index]{radicand}`。 */
public data class LatexRoot(
    val radicand: LatexExpr,
    val index: LatexExpr? = null,
) : LatexExpr {
    override val children: List<LatexExpr> get() = listOfNotNull(index, radicand)
}

/**
 * 基线下的下标、基线上的上标。`x_a^b` 会合并到同一个 [LatexSubSup] 节点。
 * [sub] / [sup] 任一可为 null（只有下标或只有上标）。
 */
public data class LatexSubSup(
    val base: LatexExpr,
    val sub: LatexExpr? = null,
    val sup: LatexExpr? = null,
) : LatexExpr {
    override val children: List<LatexExpr>
        get() = listOfNotNull(sub, sup)
}

/** 大算符：`\sum \int \oint \prod \coprod \bigcup \bigcap`，上下标居中堆叠。 */
public data class LatexBigOperator(
    val operator: String,
    val sub: LatexExpr? = null,
    val sup: LatexExpr? = null,
) : LatexExpr {
    override val atom: LatexAtom get() = LatexAtom.OP
    override val children: List<LatexExpr> get() = listOfNotNull(sub, sup)
}

/** `\left(...\right)` 一类定界符，左右符号按内容高度伸缩。 */
public data class LatexDelimited(
    val left: String,
    val inner: List<LatexExpr>,
    val right: String,
) : LatexExpr {
    override val atom: LatexAtom get() = LatexAtom.INNER
    override val children: List<LatexExpr> get() = inner
}

/** `\text{}` / `\mathrm{}`：内部内容按正体段落渲染（不斜体）。 */
public data class LatexText(
    val content: String,
) : LatexExpr {
    override val children: List<LatexExpr> get() = emptyList()
}

/** 显式水平间距 `\,` `\;` `\quad` `\qquad`，单位为 em（相对当前字号）。 */
public data class LatexSpacing(
    val emWidth: Float,
) : LatexExpr {
    override val children: List<LatexExpr> get() = emptyList()
}

/** 装饰：`\overline` `\underline` `\overrightarrow` `\hat` `\bar` `\vec`。 */
public data class LatexAccent(
    val base: LatexExpr,
    val accent: LatexAccentKind,
) : LatexExpr {
    override val children: List<LatexExpr> get() = listOf(base)
}

/** `\overset{over}{base}`：在底数正上方居中放置一个缩小的表达式（如 $A \xrightarrow{f} B$ 的标签）。 */
public data class LatexOverset(
    val over: LatexExpr,
    val base: LatexExpr,
) : LatexExpr {
    override val children: List<LatexExpr> get() = listOf(over, base)
}

/** `\underset{under}{base}`：在底数正下方居中放置一个缩小的表达式（如下标语义的函数限制）。 */
public data class LatexUnderset(
    val under: LatexExpr,
    val base: LatexExpr,
) : LatexExpr {
    override val children: List<LatexExpr> get() = listOf(under, base)
}

/**
 * `\substack{a \\ b \\ c}`：多行垂直堆叠（常用于大算符的上下标）。
 * 每行是独立解析的表达式列表（`\\` 分隔行，`&` 暂忽略）。
 */
public data class LatexSubstack(
    val rows: List<List<LatexExpr>>,
) : LatexExpr {
    override val children: List<LatexExpr> get() = rows.flatten()
}

/** 装饰类型。 */
public enum class LatexAccentKind {
    OVERLINE,
    UNDERLINE,
    OVERRIGHTARROW,
    HAT,
    BAR,
    VEC,
    DOT,
    DDOT,
    TILDE,
    BREVE,
    CHECK,
    ACUTE,
    GRAVE,
    MATHRING,
}

/** 二项式系数 `\binom{n}{k}`：渲染为带圆括号的两行堆叠。 */
public data class LatexBinom(
    val upper: LatexExpr,
    val lower: LatexExpr,
) : LatexExpr {
    override val children: List<LatexExpr> get() = listOf(upper, lower)
}

/** 解析失败时的字面量回退（例如未知命令），原样显示源码片段，保证渲染不中断。 */
public data class LatexRaw(
    val source: String,
) : LatexExpr {
    override val children: List<LatexExpr> get() = emptyList()
}
