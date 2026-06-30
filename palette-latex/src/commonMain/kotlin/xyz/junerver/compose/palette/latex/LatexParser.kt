package xyz.junerver.compose.palette.latex

/**
 * LaTeX 数学表达式解析器。
 *
 * 采用递归下降：`expression → term { (sub|sup) }`，`term → atom`，
 * `atom → group | command | character`。支持上/下标合并（`x_a^b` → 同一 [LatexSubSup]）、
 * 结构命令（`\frac \sqrt \left...\right \text \binom`）、装饰命令与希腊字母 / 运算符表查询。
 *
 * 未识别的命令会回退为 [LatexRaw]，保证渲染不中断。
 */
public object LatexParser {
    /**
     * 解析 [source] 为 [LatexExpr]。空串返回空分组。
     */
    public fun parse(source: String): LatexExpr {
        val tokens = tokenizeLatex(source)
        val ctx = ParseContext(tokens)
        val exprs = parseExprList(ctx, stop = null)
        return if (exprs.size == 1) exprs[0] else LatexGroup(exprs)
    }

    /**
     * 解析至遇到 [stop]（用于 group 内遇 `}`）或结尾为止，返回水平序列。
     */
    private fun parseExprList(ctx: ParseContext, stop: ((LatexToken) -> Boolean)?): List<LatexExpr> {
        val out = ArrayList<LatexExpr>()
        while (ctx.hasNext()) {
            val tk = ctx.peek()
            if (stop != null && stop(tk)) break
            when {
                tk is LatexToken.RBrace || tk is LatexToken.RBracket -> ctx.next()
                tk is LatexToken.Sup || tk is LatexToken.Sub -> {
                    // 上下标：合并到前一个原子（若有），否则以空为底
                    val base = if (out.isNotEmpty()) out.removeAt(out.lastIndex) else LatexRaw("")
                    out.add(attachOptionalScripts(ctx, base))
                }
                tk is LatexToken.CommandSymbol -> {
                    out.add(commandSymbolAtom(ctx.next() as LatexToken.CommandSymbol))
                }
                tk is LatexToken.Command -> {
                    val expr = commandAtom(ctx)
                    out.add(attachOptionalScripts(ctx, expr))
                }
                tk is LatexToken.LBrace -> {
                    out.add(attachOptionalScripts(ctx, parseGroup(ctx)))
                }
                tk is LatexToken.LBracket -> {
                    out.add(LatexCharacter('[', italic = false))
                    ctx.next()
                }
                tk is LatexToken.Ampersand -> {
                    out.add(LatexCharacter('&', italic = false))
                    ctx.next()
                }
                tk is LatexToken.Word -> {
                    ctx.next()
                    val atoms = wordToAtoms(tk.text)
                    // 最后一个原子可承接上下标，其余直接加入
                    if (atoms.size <= 1) {
                        out.add(attachOptionalScripts(ctx, atoms.firstOrNull() ?: LatexRaw("")))
                    } else {
                        out.addAll(atoms.dropLast(1))
                        out.add(attachOptionalScripts(ctx, atoms.last()))
                    }
                }
                else -> ctx.next() // 容错
            }
        }
        return out
    }

    /**
     * 解析一个原子表达式（无前导 ^/_），由调用方负责附加上下标。
     */
    private fun parseAtom(ctx: ParseContext): LatexExpr {
        val tk = ctx.peek()
        return when {
            tk is LatexToken.LBrace -> parseGroup(ctx)
            tk is LatexToken.Command -> commandAtom(ctx)
            tk is LatexToken.CommandSymbol -> commandSymbolAtom(ctx.next() as LatexToken.CommandSymbol)
            tk is LatexToken.Word -> {
                ctx.next()
                val atoms = wordToAtoms(tk.text)
                if (atoms.size == 1) atoms[0] else LatexGroup(atoms)
            }
            tk is LatexToken.LBracket -> {
                // 在需要原子的位置遇到 [ ]，按字面量
                ctx.next()
                LatexCharacter('[', italic = false)
            }
            tk is LatexToken.Ampersand -> {
                ctx.next()
                LatexCharacter('&', italic = false)
            }
            tk is LatexToken.RBrace || tk is LatexToken.RBracket -> {
                // 不应到达；返回占位
                ctx.next()
                LatexRaw("")
            }
            tk is LatexToken.Sup || tk is LatexToken.Sub -> {
                // 原子位置却遇到 ^/_：以空为底
                attachScript(ctx, base = LatexRaw(""))
            }
            else -> {
                ctx.next()
                LatexRaw("")
            }
        }
    }

    /**
     * 解析 `{ ... }` 分组。
     */
    private fun parseGroup(ctx: ParseContext): LatexExpr {
        ctx.expect(LatexToken.LBrace)
        val inner = parseExprList(ctx) { it is LatexToken.RBrace }
        ctx.consumeIf(LatexToken.RBrace)
        return LatexGroup(inner)
    }

    /**
     * 解析 `\substack{...}` 的多行内容：按 `\\`（换行命令）切分为多行，
     * 每行各自按行内表达式解析。忽略 `&`（substack 无对齐概念）。
     */
    private fun parseSubstackRows(ctx: ParseContext): List<List<LatexExpr>> {
        ctx.expect(LatexToken.LBrace)
        val rows = ArrayList<List<LatexExpr>>()
        var current = ArrayList<LatexExpr>()
        // 停止条件：遇到 } 或 \\ 换行
        fun stop(tk: LatexToken): Boolean = tk is LatexToken.RBrace || isNewlineCommand(tk)
        while (ctx.hasNext() && ctx.peek() !is LatexToken.RBrace) {
            // 消费至行内分隔前
            val seg = parseExprList(ctx, ::stop)
            current.addAll(seg)
            if (ctx.hasNext() && isNewlineCommand(ctx.peek())) {
                ctx.next() // 消费 \\
                rows.add(current)
                current = ArrayList()
            } else {
                break
            }
        }
        if (current.isNotEmpty()) rows.add(current)
        ctx.consumeIf(LatexToken.RBrace)
        return rows
    }

    /** 判定 token 是否为 `\\` 换行命令（CommandSymbol '\\'）。 */
    private fun isNewlineCommand(tk: LatexToken): Boolean =
        tk is LatexToken.CommandSymbol && tk.symbol == '\\'

    /**
     * 解析 `\command` 命令（结构 / 希腊 / 运算符 / 大算符 / 装饰 / 间距 / 转义）。
     */
    private fun commandAtom(ctx: ParseContext): LatexExpr {
        val cmd = ctx.next() as LatexToken.Command
        return when (cmd.name) {
            // 结构命令
            "frac", "dfrac", "tfrac" -> {
                val n = parseRequiredGroup(ctx) ?: LatexRaw("")
                val d = parseRequiredGroup(ctx) ?: LatexRaw("")
                LatexFraction(n, d)
            }
            "sqrt" -> parseSqrt(ctx)
            "binom", "dbinom", "tbinom" -> {
                val u = parseRequiredGroup(ctx) ?: LatexRaw("")
                val l = parseRequiredGroup(ctx) ?: LatexRaw("")
                LatexBinom(u, l)
            }
            "overset" -> {
                val over = parseRequiredGroup(ctx) ?: LatexRaw("")
                val base = parseRequiredGroup(ctx) ?: LatexRaw("")
                attachOptionalScripts(ctx, LatexOverset(over, base))
            }
            "underset" -> {
                val under = parseRequiredGroup(ctx) ?: LatexRaw("")
                val base = parseRequiredGroup(ctx) ?: LatexRaw("")
                attachOptionalScripts(ctx, LatexUnderset(under, base))
            }
            "substack" -> {
                val rows = parseSubstackRows(ctx)
                attachOptionalScripts(ctx, LatexSubstack(rows))
            }
            "over" -> LatexRaw("\\over") // 简化：不支持 {a}\over{b} 形式
            "left" -> parseDelimited(ctx)
            "text", "mathrm", "operatorname", "textnormal" -> {
                val content = parseRequiredGroupText(ctx) ?: ""
                LatexText(content)
            }
            "mathit", "mathbf", "mathsf", "mathtt" -> {
                // 简化：当作分组，保留斜体语义
                parseRequiredGroup(ctx) ?: LatexRaw("")
            }
            "lim", "limsup", "liminf", "max", "min", "sup", "inf", "log", "ln", "lg", "exp", "sin", "cos", "tan", "cot", "sec", "csc", "arcsin", "arccos", "arctan", "sinh", "cosh", "tanh", "arg", "deg", "det", "dim", "gcd", "hom", "ker", "Pr" -> {
                // 这些“函数名”按正体文本渲染，并允许其带上 / 下标
                val base: LatexExpr = LatexText(cmd.name)
                attachOptionalScripts(ctx, base)
            }
            else -> {
                // 查表
                LatexSymbols.bigOperatorOf(cmd.name)?.let { glyph ->
                    parseBigOperator(ctx, glyph)
                } ?: LatexSymbols.symbolOf(cmd.name)?.let { (glyph, atom) ->
                    // 普通符号（含运算符/关系符/定界符/标点等）：保留原子类型用于数学间距
                    attachOptionalScripts(ctx, LatexSymbol(glyph, italic = false, atom = atom))
                } ?: LatexSymbols.greekOf(cmd.name)?.let { (glyph, italic) ->
                    attachOptionalScripts(ctx, LatexSymbol(glyph, italic, atom = LatexAtom.ORD))
                } ?: LatexSymbols.accents[cmd.name]?.let { kind ->
                    val base = parseRequiredGroup(ctx) ?: LatexRaw("")
                    LatexAccent(base, kind)
                } ?: LatexSymbols.spacing[cmd.name]?.let { em ->
                    LatexSpacing(em)
                } ?: LatexRaw("\\${cmd.name}")
            }
        }
    }

    private fun parseSqrt(ctx: ParseContext): LatexExpr {
        // 可选 [index]
        val index = if (ctx.peek() is LatexToken.LBracket) {
            ctx.next()
            val inner = parseExprList(ctx) { it is LatexToken.RBracket }
            ctx.consumeIf(LatexToken.RBracket)
            if (inner.isEmpty()) null else if (inner.size == 1) inner[0] else LatexGroup(inner)
        } else null
        val radicand = parseRequiredGroup(ctx) ?: LatexRaw("")
        return LatexRoot(radicand, index)
    }

    /**
     * 解析大算符：符号 + 可选上下标（居中堆叠）。
     */
    private fun parseBigOperator(ctx: ParseContext, glyph: String): LatexExpr {
        var sub: LatexExpr? = null
        var sup: LatexExpr? = null
        while (ctx.hasNext() && (ctx.peek() is LatexToken.Sub || ctx.peek() is LatexToken.Sup)) {
            val isSup = ctx.peek() is LatexToken.Sup
            val operand = parseScriptOperand(ctx)
            if (isSup) sup = operand else sub = operand
        }
        return LatexBigOperator(glyph, sub, sup)
    }

    private fun parseDelimited(ctx: ParseContext): LatexExpr {
        // \left <delim> ... \right <delim>
        val left = parseDelimiter(ctx) ?: "("
        val inner = parseExprList(ctx) { tk ->
            tk is LatexToken.Command && (tk.name == "right" || tk.name == "end")
        }
        // 消费 \right
        val right = if (ctx.peek() is LatexToken.Command && (ctx.peek() as LatexToken.Command).name == "right") {
            ctx.next()
            parseDelimiter(ctx) ?: ")"
        } else {
            ")"
        }
        return LatexDelimited(left, inner, right)
    }

    /**
     * 读取 `\left` / `\right` 之后的定界符。返回其字形（如 "(" "[" "|" "." 表示空定界符）。
     */
    private fun parseDelimiter(ctx: ParseContext): String? {
        if (!ctx.hasNext()) return null
        return when (val tk = ctx.peek()) {
            is LatexToken.CommandSymbol -> {
                ctx.next()
                if (tk.symbol == '.') "" else tk.symbol.toString()
            }
            is LatexToken.Command -> {
                ctx.next()
                when (tk.name) {
                    "vert", "lvert", "rvert" -> "|"
                    "Vert", "lVert", "rVert" -> "‖"
                    "lceil", "rceil" -> "⌈"
                    "lfloor", "rfloor" -> "⌊"
                    "langle", "rangle" -> "⟨"
                    "lbrace" -> "{"
                    "rbrace" -> "}"
                    "uparrow" -> "↑"
                    "downarrow" -> "↓"
                    else -> ""
                }
            }
            is LatexToken.Word -> {
                ctx.next()
                tk.text.firstOrNull()?.toString()
            }
            is LatexToken.LBrace -> { ctx.next(); "{" }
            is LatexToken.RBrace -> { ctx.next(); "}" }
            is LatexToken.LBracket -> { ctx.next(); "[" }
            is LatexToken.RBracket -> { ctx.next(); "]" }
            else -> null
        }
    }

    /** ASCII 字符 → 原子类型（对齐 KaTeX `math` 模式分组），用于数学间距规则。 */
    private fun charAtom(c: Char): LatexAtom = when (c) {
        '+', '-', '*', '/', '⋅' -> LatexAtom.BIN
        '=', '<', '>', ':' -> LatexAtom.REL
        '(', '[', '{' -> LatexAtom.OPEN
        ')', ']', '}' -> LatexAtom.CLOSE
        ',', ';' -> LatexAtom.PUNCT
        else -> LatexAtom.ORD
    }

    /**
     * 把一个普通字符序列拆成 [LatexCharacter] 列表：字母 → 斜体变量，数字/符号 → 正体。
     * 数字与紧随的 `.` 仍合并为 [LatexTextRun]；`,` 视为标点单独成原子。
     */
    private fun wordToAtoms(text: String): List<LatexExpr> {
        if (text.isEmpty()) return emptyList()
        val out = ArrayList<LatexExpr>(text.length)
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (c.isDigit()) {
                val sb = StringBuilder()
                while (i < text.length && (text[i].isDigit() || text[i] == '.')) {
                    sb.append(text[i])
                    i++
                }
                out.add(LatexTextRun(sb.toString()))
            } else if (c.isLetter()) {
                out.add(LatexCharacter(c, italic = true))
                i++
            } else {
                // ASCII 符号：+ - = < > ( ) , ; 等，按原子类型归类以便套用 TeX 间距
                out.add(LatexCharacter(c, italic = false, atom = charAtom(c)))
                i++
            }
        }
        return out
    }

    /**
     * 解析必填分组 `{...}`；若不是分组则返回 null（容错）。
     */
    private fun parseRequiredGroup(ctx: ParseContext): LatexExpr? {
        if (!ctx.hasNext()) return null
        if (ctx.peek() is LatexToken.LBrace) return parseGroup(ctx)
        // 允许单 token 作参数：\frac12 → \frac{1}{2}
        return parseAtom(ctx)
    }

    /**
     * 解析必填分组并提取为纯文本（用于 `\text{}`）。
     */
    private fun parseRequiredGroupText(ctx: ParseContext): String? {
        if (ctx.peek() is LatexToken.LBrace) {
            ctx.next()
            val sb = StringBuilder()
            while (ctx.hasNext() && ctx.peek() !is LatexToken.RBrace) {
                val tk = ctx.next()
                when (tk) {
                    is LatexToken.Word -> sb.append(tk.text)
                    is LatexToken.Command -> sb.append('\\').append(tk.name).append(' ')
                    is LatexToken.CommandSymbol -> sb.append(tk.symbol)
                    is LatexToken.Sup -> sb.append('^')
                    is LatexToken.Sub -> sb.append('_')
                    is LatexToken.LBrace -> sb.append('{')
                    is LatexToken.RBrace -> sb.append('}')
                    is LatexToken.LBracket -> sb.append('[')
                    is LatexToken.RBracket -> sb.append(']')
                    is LatexToken.Ampersand -> sb.append('&')
                }
            }
            ctx.consumeIf(LatexToken.RBrace)
            return sb.toString().trim()
        }
        // 单 token
        val tk = ctx.next()
        return when (tk) {
            is LatexToken.Word -> tk.text
            is LatexToken.CommandSymbol -> tk.symbol.toString()
            else -> ""
        }
    }

    /**
     * 解析 `^` / `_` 之后的操作数（单 token 或分组）。
     */
    private fun parseScriptOperand(ctx: ParseContext): LatexExpr {
        // ctx 仍指向 ^ 或 _
        ctx.next() // 消费 ^ 或 _
        if (!ctx.hasNext()) return LatexRaw("")
        return when (ctx.peek()) {
            is LatexToken.LBrace -> parseGroup(ctx)
            is LatexToken.Command -> commandAtom(ctx)
            is LatexToken.CommandSymbol -> commandSymbolAtom(ctx.next() as LatexToken.CommandSymbol)
            is LatexToken.Word -> {
                val tk = ctx.next() as LatexToken.Word
                wordToAtoms(tk.text).firstOrNull() ?: LatexRaw("")
            }
            else -> {
                ctx.next()
                LatexRaw("")
            }
        }
    }

    /**
     * 把连续的 `^...` / `_...` 合并附加到 [base]，形成单个 [LatexSubSup]。
     */
    private fun attachOptionalScripts(ctx: ParseContext, base: LatexExpr): LatexExpr {
        var current = base
        var sub: LatexExpr? = null
        var sup: LatexExpr? = null
        while (ctx.hasNext() && (ctx.peek() is LatexToken.Sub || ctx.peek() is LatexToken.Sup)) {
            val isSup = ctx.peek() is LatexToken.Sup
            val operand = parseScriptOperand(ctx)
            if (isSup) sup = (if (sup == null) operand else LatexGroup(listOf(sup, operand)))
            else sub = (if (sub == null) operand else LatexGroup(listOf(sub, operand)))
        }
        return if (sub == null && sup == null) current else LatexSubSup(current, sub, sup)
    }

    /**
     * 已知 base，附加紧随其后的单个 ^/_（顶层裸 ^/_ 容错用）。
     */
    private fun attachScript(ctx: ParseContext, base: LatexExpr): LatexExpr = attachOptionalScripts(ctx, base)

    /**
     * 转义单符号命令（`\%` 等）→ [LatexSymbol]（正体字面量）。
     */
    private fun commandSymbolAtom(tk: LatexToken.CommandSymbol): LatexExpr {
        return when (tk.symbol) {
            ',', ';', ':', '!' -> {
                LatexSymbols.spacing[tk.symbol.toString()]?.let { LatexSpacing(it) } ?: LatexSpacing(0.17f)
            }
            ' ' -> LatexSpacing(0.27f)
            else -> LatexSymbol(tk.symbol.toString(), italic = false)
        }
    }
}

/**
 * 解析上下文：带游标的 token 列表。
 */
private class ParseContext(private val tokens: List<LatexToken>) {
    private var index = 0
    fun hasNext(): Boolean = index < tokens.size
    fun peek(): LatexToken = tokens[index]
    fun next(): LatexToken = tokens[index++]
    fun expect(token: LatexToken) {
        if (hasNext() && peek() == token) next() else Unit
    }
    fun consumeIf(token: LatexToken) {
        if (hasNext() && peek() == token) next()
    }
}
