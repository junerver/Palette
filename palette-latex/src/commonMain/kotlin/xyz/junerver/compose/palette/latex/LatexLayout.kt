package xyz.junerver.compose.palette.latex

/**
 * 数学排版的盒模型。布局引擎把每个 [LatexExpr] 递归转换成一个 [LatexBox]，
 * 渲染层再遍历这些盒绘制字形 / 连线。坐标原点：每个盒的左上角为 (0,0)，
 * 基线相对盒顶部的距离由 [baseline] 给出（基线在盒内部）。
 */
public sealed interface LatexBox {
    /** 盒宽（像素）。 */
    public val width: Float
    /** 盒高（像素）。 */
    public val height: Float
    /** 基线相对盒顶部的 y 偏移（像素）。子盒的 baseline 已对齐到同一数学基线。 */
    public val baseline: Float
    /** 该盒作为原子参与数学间距规则时的类型，默认 [LatexAtom.ORD]。 */
    public val atom: LatexAtom
        get() = LatexAtom.ORD
}

/** 可绘制文本（字符 / 符号 / 文本片段）。 */
public data class LatexGlyphBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    /** 要绘制的字符串（单个字符或一段文本）。 */
    public val text: String,
    /** 是否斜体。 */
    public val italic: Boolean,
    /** 是否数字 / 正体符号（影响颜色，可由渲染层区分）。 */
    public val upright: Boolean,
    /** 字号（像素）。 */
    public val sizePx: Float,
    /** 该字形的原子类型，用于数学间距规则。 */
    override val atom: LatexAtom = LatexAtom.ORD,
) : LatexBox

/** 水平拼接的盒序列（行内公式主体、分组等）。 */
public data class LatexHorizontalBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    /** 子盒及其相对本盒左上角的偏移。 */
    public val items: List<LatexPlacedBox>,
) : LatexBox

/** 分数盒：带一条横线。 */
public data class LatexFractionBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    /** 分子盒（相对本盒左上角的偏移）。 */
    public val numerator: LatexPlacedBox,
    /** 分母盒（相对本盒左上角的偏移）。 */
    public val denominator: LatexPlacedBox,
    /** 分数线相对本盒左上角的 y 偏移。 */
    public val ruleY: Float,
    /** 分数线粗细（像素）。 */
    public val ruleThickness: Float,
) : LatexBox

/** 根号盒：被开方数 + 顶部勾 + 上方横线 + 可选左上角指数。 */
public data class LatexRootBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    /** 被开方数（含上方横线区域）相对本盒左上角的偏移。 */
    public val radicand: LatexPlacedBox,
    /** 可选指数（n 次根），相对本盒左上角的偏移。 */
    public val index: LatexPlacedBox?,
    /** 根号主体（勾 + 斜升 + 斜降折线）的左上角 x 偏移与尺寸。 */
    public val signOffsetX: Float,
    public val signWidth: Float,
    public val signHeight: Float,
    /** 被开方数实际高度（用于绘制斜降段到底部）。 */
    public val radicandHeight: Float,
    /** 顶部横线相对本盒左上角的 y 偏移与起始 x、长度。 */
    public val ruleY: Float,
    public val ruleX: Float,
    public val ruleWidth: Float,
    public val ruleThickness: Float,
) : LatexBox

/** 大算符盒：符号居中 + 上下标居中堆叠。 */
public data class LatexBigOperatorBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    public val glyph: String,
    public val glyphOffsetX: Float,
    public val glyphOffsetY: Float,
    public val sizePx: Float,
    public val sub: LatexPlacedBox?,
    public val sup: LatexPlacedBox?,
) : LatexBox

/** 定界符盒：左右伸缩符号 + 内部内容。 */
public data class LatexDelimitedBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    public val leftGlyph: String,
    public val rightGlyph: String,
    public val leftOffsetX: Float,
    public val rightOffsetX: Float,
    public val inner: LatexPlacedBox,
    public val sizePx: Float,
) : LatexBox

/** 装饰盒（上划线 / 下划线 / 箭头 / 帽子等）。 */
public data class LatexAccentBox(
    override val width: Float,
    override val height: Float,
    override val baseline: Float,
    public val base: LatexPlacedBox,
    public val accent: LatexAccentKind,
    /** 装饰（横线/箭头）相对本盒左上角的 y 偏移。 */
    public val accentY: Float,
    public val accentWidth: Float,
    public val ruleThickness: Float,
) : LatexBox

/** 空白盒（间距）。 */
public data class LatexSpacingBox(
    override val width: Float,
) : LatexBox {
    override val height: Float get() = 0f
    override val baseline: Float get() = 0f
}

/** 一个盒与其相对父盒左上角的放置偏移。 */
public data class LatexPlacedBox(
    public val box: LatexBox,
    public val x: Float,
    public val y: Float,
)

/**
 * 布局结果：根盒 + 渲染所需的元信息。
 */
public data class LatexLayout(
    public val box: LatexBox,
    /** 是否显示模式（块级 `$$...$$`），影响字号放大。 */
    public val displayStyle: Boolean,
)

/**
 * LaTeX 盒模型布局引擎。把 [LatexExpr] 递归转成 [LatexBox]，所有尺寸通过
 * [LatexTextMeasurer] 获取真实字体度量，确保跨平台对齐一致。
 */
public object LatexLayoutEngine {
    /** 上下标缩放系数。 */
    private const val SCRIPT_SCALE = 0.7f
    /** 二级上下标（上下标的上下标）缩放系数。 */
    private const val SCRIPT_SCRIPT_SCALE = 0.5f
    /** 分数线粗细相对字号的比例。 */
    private const val FRACTION_RULE_RATIO = 0.045f
    /** 分数分子 / 分母与分数线之间的间隙相对字号的比例。 */
    private const val FRACTION_GAP_RATIO = 0.15f
    /** 根号横线粗细相对字号的比例。 */
    private const val RADICAL_RULE_RATIO = 0.045f
    /** 根号顶部间隙相对字号的比例。 */
    private const val RADICAL_GAP_RATIO = 0.08f
    /** 行内项之间的额外间隙（像素），避免符号粘连。 */
    private const val ITEM_GAP = 0.6f

    /**
     * 布局 [expr]。
     *
     * @param measurer 真实字体度量器。
     * @param baseSizePx 基础字号（像素）。
     * @param displayStyle 是否显示模式（块级公式，字号放大 1.2 倍）。
     */
    public fun layout(
        expr: LatexExpr,
        measurer: LatexTextMeasurer,
        baseSizePx: Float,
        displayStyle: Boolean = false,
    ): LatexLayout {
        val size = if (displayStyle) baseSizePx * 1.2f else baseSizePx
        val box = layoutExpr(expr, measurer, size)
        return LatexLayout(box, displayStyle)
    }

    /**
     * 递归布局。返回的盒基线对齐到统一数学基线。
     */
    private fun layoutExpr(expr: LatexExpr, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        return when (expr) {
            is LatexCharacter -> measureGlyph(expr.char.toString(), expr.italic, upright = false, measurer, sizePx, expr.atom)
            is LatexSymbol -> measureGlyph(expr.glyph, expr.italic, upright = true, measurer, sizePx, expr.atom)
            is LatexTextRun -> measureGlyph(expr.text, italic = false, upright = true, measurer, sizePx)
            is LatexText -> measureGlyph(expr.content, italic = false, upright = true, measurer, sizePx)
            is LatexSpacing -> LatexSpacingBox(width = sizePx * expr.emWidth)
            is LatexRaw -> if (expr.source.isEmpty()) LatexSpacingBox(0f) else measureGlyph(expr.source, italic = false, upright = true, measurer, sizePx)
            is LatexGroup -> layoutHorizontal(expr.children, measurer, sizePx)
            is LatexFraction -> layoutFraction(expr, measurer, sizePx)
            is LatexRoot -> layoutRoot(expr, measurer, sizePx)
            is LatexSubSup -> layoutSubSup(expr, measurer, sizePx)
            is LatexBigOperator -> layoutBigOperator(expr, measurer, sizePx)
            is LatexDelimited -> layoutDelimited(expr, measurer, sizePx)
            is LatexAccent -> layoutAccent(expr, measurer, sizePx)
            is LatexBinom -> layoutBinom(expr, measurer, sizePx)
            is LatexOverset -> layoutOversetUnderset(expr.over, expr.base, measurer, sizePx, isOver = true)
            is LatexUnderset -> layoutOversetUnderset(expr.under, expr.base, measurer, sizePx, isOver = false)
            is LatexSubstack -> layoutSubstack(expr, measurer, sizePx)
        }
    }

    /**
     * `\overset` / `\underset`：缩小表达式（scriptSize）居中堆叠在底数上方 / 下方。
     */
    private fun layoutOversetUnderset(
        annotation: LatexExpr,
        base: LatexExpr,
        measurer: LatexTextMeasurer,
        sizePx: Float,
        isOver: Boolean,
    ): LatexBox {
        val baseBox = layoutExpr(base, measurer, sizePx)
        val annBox = layoutExpr(annotation, measurer, sizePx * SCRIPT_SCALE)
        val gap = sizePx * 0.1f
        val width = maxOf(baseBox.width, annBox.width)
        val annX = (width - annBox.width) / 2f
        val baseX = (width - baseBox.width) / 2f
        val items = ArrayList<LatexPlacedBox>(2)
        val totalHeight: Float
        val baseline: Float
        if (isOver) {
            val annY = 0f
            val baseY = annBox.height + gap
            totalHeight = annBox.height + gap + baseBox.height
            baseline = annBox.height + gap + baseBox.baseline
            items.add(LatexPlacedBox(annBox, annX, annY))
            items.add(LatexPlacedBox(baseBox, baseX, baseY))
        } else {
            val baseY = 0f
            val annY = baseBox.height + gap
            totalHeight = baseBox.height + gap + annBox.height
            baseline = baseBox.baseline
            items.add(LatexPlacedBox(baseBox, baseX, baseY))
            items.add(LatexPlacedBox(annBox, annX, annY))
        }
        return LatexHorizontalBox(width = width, height = totalHeight, baseline = baseline, items = items)
    }

    /**
     * `\substack`：多行垂直堆叠，每行居中，行间留 0.3em 间隙。整体宽度取最宽行。
     */
    private fun layoutSubstack(expr: LatexSubstack, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val scriptSize = sizePx * SCRIPT_SCALE
        val rowGap = scriptSize * 0.3f
        val rows = expr.rows.map { layoutHorizontal(it, measurer, scriptSize) }
        if (rows.isEmpty()) return LatexSpacingBox(0f)
        val width = rows.maxOf { it.width }
        var cursorY = 0f
        val items = ArrayList<LatexPlacedBox>(rows.size)
        for ((index, row) in rows.withIndex()) {
            val x = (width - row.width) / 2f
            items.add(LatexPlacedBox(row, x, cursorY))
            cursorY += row.height
            if (index < rows.lastIndex) cursorY += rowGap
        }
        val totalHeight = cursorY
        val baseline = totalHeight / 2f
        return LatexHorizontalBox(width = width, height = totalHeight, baseline = baseline, items = items)
    }

    private fun measureGlyph(
        text: String,
        italic: Boolean,
        upright: Boolean,
        measurer: LatexTextMeasurer,
        sizePx: Float,
        atom: LatexAtom = LatexAtom.ORD,
    ): LatexGlyphBox {
        val m = measurer.measure(text, LatexFontStyle(sizePx, italic))
        return LatexGlyphBox(
            width = m.width,
            height = m.ascent + m.descent,
            baseline = m.ascent,
            text = text,
            italic = italic,
            upright = upright,
            sizePx = sizePx,
            atom = atom,
        )
    }

    /**
     * 计算相邻两个原子之间的 TeX 数学间距（单位 em，相对字号）。
     * 遵循 TeXbook §442–446 / KaTeX spacingRules：基于 (左原子, 右原子) 类型查表。
     *
     * 规则要点：
     * - BIN 两侧（当对侧是"可参与运算"的 ORD/OP/INNER/OPEN/CLOSE）均加 0.2222em；
     *   但行首 / OPEN 之后的 BIN 降级为 ORD（无左操作数时不加左间距）。
     * - REL / INNER 与可运算原子间加 0.2778 / 0.2222em。
     * - PUNCT 之前无间距；之后（右侧非 CLOSE）加 0.1667em。
     * - OPEN 之后 / CLOSE 之前无间距。
     */
    private fun texSpacing(left: LatexAtom?, right: LatexAtom): Float {
        // “可参与运算”的左操作数集合
        fun leftOperable(a: LatexAtom?) = a == LatexAtom.ORD || a == LatexAtom.OP ||
            a == LatexAtom.INNER || a == LatexAtom.OPEN || a == LatexAtom.CLOSE
        // BIN 作为右邻时的左间距（行首 / OPEN 之后无左操作数 → 0）
        if (right == LatexAtom.BIN) {
            if (left == null || left == LatexAtom.OPEN || left == LatexAtom.REL ||
                left == LatexAtom.PUNCT || left == LatexAtom.BIN
            ) return 0f
            return if (leftOperable(left)) 0.2222f else 0f
        }
        val l = left ?: LatexAtom.ORD
        // BIN 作为左邻时的右间距
        if (l == LatexAtom.BIN) return if (
            right == LatexAtom.ORD || right == LatexAtom.OP ||
            right == LatexAtom.CLOSE || right == LatexAtom.INNER
        ) 0.2222f else 0f
        // REL：作为右邻
        if (right == LatexAtom.REL) return if (
            l == LatexAtom.ORD || l == LatexAtom.OP || l == LatexAtom.OPEN ||
            l == LatexAtom.INNER || l == LatexAtom.BIN || l == LatexAtom.CLOSE || l == LatexAtom.PUNCT
        ) 0.2778f else 0f
        // REL 作为左邻（右侧非特定）：同样加 0.2778（除非右侧是 REL/PUNCT 等边界）
        if (l == LatexAtom.REL) return if (right == LatexAtom.OPEN) 0f else 0.2778f
        // INNER
        if (right == LatexAtom.INNER) return if (l == LatexAtom.OPEN) 0f else 0.2222f
        if (l == LatexAtom.INNER) return if (
            right == LatexAtom.ORD || right == LatexAtom.OP || right == LatexAtom.CLOSE
        ) 0.1111f else if (right == LatexAtom.BIN || right == LatexAtom.REL) 0.2222f else 0f
        // PUNCT 之后
        if (l == LatexAtom.PUNCT) return if (right != LatexAtom.CLOSE) 0.1667f else 0f
        // 其余（ORD/OP/OPEN/CLOSE 组合）无间距
        return 0f
    }

    /**
     * 水平拼接多个表达式，基线对齐，相邻原子之间套用 TeX 数学间距规则。
     */
    private fun layoutHorizontal(
        exprs: List<LatexExpr>,
        measurer: LatexTextMeasurer,
        sizePx: Float,
    ): LatexBox {
        if (exprs.isEmpty()) return LatexSpacingBox(0f)
        // 先布局所有子盒并取其原子类型
        val laidOut = exprs.map { layoutExpr(it, measurer, sizePx) }
        // 计算用于间距规则的"有效原子"序列：行首 / OPEN 后的 BIN、行尾的 BIN 降级为 ORD。
        // 这是 TeX 规则的核心 —— 没有左 / 右操作数的二元运算符按普通原子处理。
        val effectiveAtoms = laidOut.mapIndexed { index, box ->
            val prev = laidOut.getOrNull(index - 1)?.atom
            val next = laidOut.getOrNull(index + 1)?.atom
            if (box.atom == LatexAtom.BIN) {
                val hasLeft = prev != null && prev != LatexAtom.OPEN && prev != LatexAtom.REL &&
                    prev != LatexAtom.PUNCT && prev != LatexAtom.BIN
                val hasRight = next != null && next != LatexAtom.CLOSE && next != LatexAtom.REL &&
                    next != LatexAtom.PUNCT && next != LatexAtom.BIN
                if (!hasLeft || !hasRight) LatexAtom.ORD else LatexAtom.BIN
            } else {
                box.atom
            }
        }
        val placed = ArrayList<LatexPlacedBox>(exprs.size)
        var cursorX = 0f
        var maxAscent = 0f
        var maxDescent = 0f
        for (box in laidOut) {
            maxAscent = maxOf(maxAscent, box.baseline)
            maxDescent = maxOf(maxDescent, box.height - box.baseline)
        }
        val baseline = maxAscent
        val totalHeight = maxAscent + maxDescent
        var prevAtom: LatexAtom? = null
        for ((index, box) in laidOut.withIndex()) {
            // 该盒之前插入的间距（基于左邻有效原子 与 当前有效原子），含 PUNCT 之后间距
            val before = texSpacing(prevAtom, effectiveAtoms[index]) * sizePx
            cursorX += before
            val y = baseline - box.baseline
            placed.add(LatexPlacedBox(box, cursorX, y))
            cursorX += box.width
            prevAtom = effectiveAtoms[index]
        }
        return LatexHorizontalBox(
            width = cursorX,
            height = totalHeight,
            baseline = baseline,
            items = placed,
        )
    }

    private fun layoutFraction(expr: LatexFraction, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val num = layoutExpr(expr.numerator, measurer, sizePx)
        val den = layoutExpr(expr.denominator, measurer, sizePx)
        val ruleThickness = sizePx * FRACTION_RULE_RATIO
        val gap = sizePx * FRACTION_GAP_RATIO
        val width = maxOf(num.width, den.width)
        // 分子放在分数线上方，分母放在下方
        val totalHeight = num.height + gap + ruleThickness + gap + den.height
        val baseline = num.height + gap + ruleThickness / 2f
        val numX = (width - num.width) / 2f
        val denX = (width - den.width) / 2f
        val numY = 0f
        val denY = num.height + gap + ruleThickness + gap
        return LatexFractionBox(
            width = width,
            height = totalHeight,
            baseline = baseline,
            numerator = LatexPlacedBox(num, numX, numY),
            denominator = LatexPlacedBox(den, denX, denY),
            ruleY = num.height + gap,
            ruleThickness = ruleThickness,
        )
    }

    private fun layoutRoot(expr: LatexRoot, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val radicand = layoutExpr(expr.radicand, measurer, sizePx)
        val ruleThickness = (sizePx * RADICAL_RULE_RATIO).coerceAtLeast(1f)
        // 根号几何（自绘三段折线 + 顶部横线），与被开方数高度对齐：
        //   顶部横线在 y=0；勾/斜升/斜降占 signHeight；被开方数从 signHeight 顶部下方开始。
        val radicandHeight = radicand.height.coerceAtLeast(sizePx * 0.5f)
        // 根号主体高度：约被开方数高度 + 字号 0.35 倍（顶部斜升空间）
        val signHeight = radicandHeight + sizePx * 0.35f
        // 根号主体宽度：基于 signHeight 取 0.7 倍，使主笔斜升角度约 55°（标准 √ 视觉）。
        // 过窄（如基于字号 0.6）会让斜线接近竖直，畸形。
        val signWidth = (signHeight * 0.7f).coerceAtLeast(sizePx * 0.4f)
        // 指数（n 次根）布局在根号左上角，缩小
        val indexBox = expr.index?.let {
            LatexPlacedBox(layoutExpr(it, measurer, sizePx * SCRIPT_SCRIPT_SCALE), 0f, 0f)
        }
        val indexWidth = indexBox?.box?.width ?: 0f
        // 被开方数 x 起点：根号宽度之后
        val radicandPlaceX = indexWidth + signWidth
        // 被开方数 y：放在根号主体（signHeight）下方，留细缝避免贴边
        val radicandPlaceY = signHeight - radicandHeight
        // 顶部横线：覆盖根号斜升顶点 → 被开方数右沿，y=0
        val ruleX = indexWidth + signWidth
        val ruleWidth = radicand.width + (signWidth * 0.1f) // 横线略长，覆盖斜升顶点
        val totalHeight = signHeight
        val width = radicandPlaceX + radicand.width
        val baseline = radicandPlaceY + radicand.baseline
        return LatexRootBox(
            width = width,
            height = totalHeight,
            baseline = baseline,
            radicand = LatexPlacedBox(radicand, radicandPlaceX, radicandPlaceY),
            index = indexBox,
            signOffsetX = indexWidth,
            signWidth = signWidth,
            signHeight = signHeight,
            ruleY = 0f,
            ruleWidth = ruleWidth,
            ruleThickness = ruleThickness,
            radicandHeight = radicandHeight,
            ruleX = ruleX,
        )
    }

    private fun layoutSubSup(expr: LatexSubSup, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val base = layoutExpr(expr.base, measurer, sizePx)
        val scriptSize = sizePx * SCRIPT_SCALE
        val subBox = expr.sub?.let { layoutExpr(it, measurer, scriptSize) }
        val supBox = expr.sup?.let { layoutExpr(it, measurer, scriptSize) }
        // 上标基线提升到 base 高度的 0.55，下标基线下沉到 base 基线下 0.25
        val supBaselineShift = base.baseline * 0.55f
        val subBaselineShift = (base.height - base.baseline) * 0.25f
        val cursorX = base.width
        var width = base.width
        var ascent = base.baseline
        var descent = base.height - base.baseline
        val items = ArrayList<LatexPlacedBox>(3)
        items.add(LatexPlacedBox(base, 0f, 0f))
        if (supBox != null) {
            // 上标：其基线对齐到 base.baseline - supBaselineShift
            val supY = base.baseline - supBaselineShift - supBox.baseline
            items.add(LatexPlacedBox(supBox, cursorX, supY))
            ascent = maxOf(ascent, base.baseline - supY + 0f)
            descent = maxOf(descent, 0f)
            width = maxOf(width, cursorX + supBox.width)
        }
        if (subBox != null) {
            val subY = base.baseline + subBaselineShift - subBox.baseline
            items.add(LatexPlacedBox(subBox, cursorX, subY))
            descent = maxOf(descent, subY + subBox.height - base.baseline)
            width = maxOf(width, cursorX + subBox.width)
        }
        // 当上下标同时存在，水平上让它们重叠（都从 cursorX 开始）
        val totalHeight = (ascent + descent).coerceAtLeast(base.height)
        return LatexHorizontalBox(
            width = width,
            height = totalHeight,
            baseline = ascent.coerceAtLeast(base.baseline),
            items = items,
        )
    }

    private fun layoutBigOperator(expr: LatexBigOperator, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val glyphBox = measureGlyph(expr.operator, italic = false, upright = true, measurer, sizePx)
        val scriptSize = sizePx * SCRIPT_SCALE
        val subBox = expr.sub?.let { layoutExpr(it, measurer, scriptSize) }
        val supBox = expr.sup?.let { layoutExpr(it, measurer, scriptSize) }
        val width = maxOf(glyphBox.width, subBox?.width ?: 0f, supBox?.width ?: 0f)
        val gap = sizePx * 0.05f
        val glyphOffsetX = (width - glyphBox.width) / 2f
        var totalHeight = glyphBox.height
        var baseline = glyphBox.baseline
        var glyphOffsetY = 0f
        var subPlace: LatexPlacedBox? = null
        var supPlace: LatexPlacedBox? = null
        if (supBox != null) {
            glyphOffsetY = supBox.height + gap
            totalHeight += supBox.height + gap
            baseline += glyphOffsetY
            supPlace = LatexPlacedBox(supBox, (width - supBox.width) / 2f, 0f)
        }
        if (subBox != null) {
            totalHeight += subBox.height + gap
            subPlace = LatexPlacedBox(subBox, (width - subBox.width) / 2f, glyphOffsetY + glyphBox.height + gap)
        }
        return LatexBigOperatorBox(
            width = width,
            height = totalHeight,
            baseline = baseline,
            glyph = expr.operator,
            glyphOffsetX = glyphOffsetX,
            glyphOffsetY = glyphOffsetY,
            sizePx = sizePx,
            sub = subPlace,
            sup = supPlace,
        )
    }

    private fun layoutDelimited(expr: LatexDelimited, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val inner = layoutHorizontal(expr.inner, measurer, sizePx)
        val left = if (expr.left.isEmpty()) LatexSpacingBox(0f)
        else measureGlyph(expr.left, italic = false, upright = true, measurer, sizePx)
        val right = if (expr.right.isEmpty()) LatexSpacingBox(0f)
        else measureGlyph(expr.right, italic = false, upright = true, measurer, sizePx)
        val gap = sizePx * 0.05f
        val width = left.width + gap + inner.width + gap + right.width
        val height = inner.height
        val baseline = inner.baseline
        val leftX = 0f
        val innerX = left.width + gap
        val rightX = innerX + inner.width + gap
        return LatexDelimitedBox(
            width = width,
            height = height,
            baseline = baseline,
            leftGlyph = expr.left,
            rightGlyph = expr.right,
            leftOffsetX = leftX,
            rightOffsetX = rightX,
            inner = LatexPlacedBox(inner, innerX, baseline - inner.baseline),
            sizePx = sizePx,
        )
    }

    private fun layoutAccent(expr: LatexAccent, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        val base = layoutExpr(expr.base, measurer, sizePx)
        val ruleThickness = sizePx * 0.05f
        // 横线 / 箭头类装饰覆盖 base 宽度；帽子类居中一个符号
        val isLine = expr.accent == LatexAccentKind.OVERLINE ||
            expr.accent == LatexAccentKind.UNDERLINE ||
            expr.accent == LatexAccentKind.OVERRIGHTARROW
        val accentWidth = base.width
        val accentY = when (expr.accent) {
            LatexAccentKind.OVERLINE -> -sizePx * 0.1f          // base 上方
            LatexAccentKind.UNDERLINE -> base.height + sizePx * 0.05f
            LatexAccentKind.OVERRIGHTARROW -> -sizePx * 0.1f
            else -> -sizePx * 0.15f                              // hat/bar/vec 等位于上方
        }
        val topPad = if (isLine || expr.accent != LatexAccentKind.UNDERLINE) sizePx * 0.15f else 0f
        val bottomPad = if (expr.accent == LatexAccentKind.UNDERLINE) sizePx * 0.1f else 0f
        val totalHeight = topPad + base.height + bottomPad
        val baseline = topPad + base.baseline
        return LatexAccentBox(
            width = maxOf(base.width, accentWidth),
            height = totalHeight,
            baseline = baseline,
            base = LatexPlacedBox(base, 0f, topPad),
            accent = expr.accent,
            accentY = accentY + topPad,
            accentWidth = accentWidth,
            ruleThickness = ruleThickness,
        )
    }

    private fun layoutBinom(expr: LatexBinom, measurer: LatexTextMeasurer, sizePx: Float): LatexBox {
        // 简化为分数形式，外围包圆括号
        val frac = LatexFraction(expr.upper, expr.lower)
        val fracBox = layoutFraction(frac, measurer, sizePx)
        val inner = LatexDelimited("(", listOf(frac), ")")
        return layoutDelimited(inner, measurer, sizePx)
    }
}
