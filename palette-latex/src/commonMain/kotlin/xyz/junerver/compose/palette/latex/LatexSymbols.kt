@file:Suppress("ConstPropertyName", "ObjectPropertyName")

package xyz.junerver.compose.palette.latex

/**
 * LaTeX 命令名 → 符号字形 / 装饰类型 / 原子类型的映射表。
 *
 * 符号映射借鉴 KaTeX（https://github.com/KaTeX/KaTeX，MIT License）的符号表
 * （`src/symbols.ts`），按命令名给出 Unicode 字形（[glyph]）与原子类型（[atom]）。
 * Unicode 字形通过 [LatexTextMeasurer] 实时度量并经系统字体渲染，**不依赖 KaTeX 专用字体**，
 * 因此本表是字体无关的纯数据。新增命令只需在此登记一行即可。
 *
 * 注意：跳过了 KaTeX 中映射到 AMS 私用区（PUA, `\ue0xx`）的命令（系统字体无字形），
 * 以及 `text` 模式的文本符号（与数学排版无关）。
 */
internal object LatexSymbols {
    /**
     * 希腊字母（小写默认斜体，大写正体）。值 = (字形, 是否斜体)。
     */
    val greek: Map<String, Pair<String, Boolean>> = mapOf(
        // 小写（斜体）
        "alpha" to ("α" to true),
        "beta" to ("β" to true),
        "gamma" to ("γ" to true),
        "delta" to ("δ" to true),
        // 注意：\epsilon 在 TeX/KaTeX 中映射到 U+03F5（lunate ϵ），但系统默认字体常缺该字形，
        // 导致渲染空白。这里映射到 U+03B5（普通 ε），保证系统字体可见；\varepsilon 同字形。
        "epsilon" to ("ε" to true),
        "varepsilon" to ("ε" to true),
        "zeta" to ("ζ" to true),
        "eta" to ("η" to true),
        "theta" to ("θ" to true),
        "vartheta" to ("ϑ" to true),
        "iota" to ("ι" to true),
        "kappa" to ("κ" to true),
        "lambda" to ("λ" to true),
        "mu" to ("μ" to true),
        "nu" to ("ν" to true),
        "xi" to ("ξ" to true),
        "omicron" to ("ο" to true),
        "pi" to ("π" to true),
        "varpi" to ("ϖ" to true),
        "rho" to ("ρ" to true),
        "varrho" to ("ϱ" to true),
        "sigma" to ("σ" to true),
        "varsigma" to ("ς" to true),
        "tau" to ("τ" to true),
        "upsilon" to ("υ" to true),
        "phi" to ("ϕ" to true),
        "varphi" to ("φ" to true),
        "chi" to ("χ" to true),
        "psi" to ("ψ" to true),
        "omega" to ("ω" to true),
        "digamma" to ("ϝ" to true),
        "varkappa" to ("ϰ" to true),
        // 大写（正体）
        "Gamma" to ("Γ" to false),
        "Delta" to ("Δ" to false),
        "Theta" to ("Θ" to false),
        "Lambda" to ("Λ" to false),
        "Xi" to ("Ξ" to false),
        "Pi" to ("Π" to false),
        "Sigma" to ("Σ" to false),
        "Upsilon" to ("Υ" to false),
        "Phi" to ("Φ" to false),
        "Psi" to ("Ψ" to false),
        "Omega" to ("Ω" to false),
    )

    /**
     * 运算符 / 关系符 / 箭头等命令 → (字形, 原子类型)。
     * 原子类型对齐 KaTeX group 分类（rel/bin/open/close/punct/textord/op），用于数学间距规则。
     */
    val symbols: Map<String, Pair<String, LatexAtom>> = buildMap {
        // ===== Relation Symbols (rel) =====
        rel("equiv", "≡"); rel("prec", "≺"); rel("succ", "≻"); rel("sim", "∼")
        rel("perp", "⊥"); rel("preceq", "⪯"); rel("succeq", "⪰"); rel("simeq", "≃")
        rel("mid", "∣"); rel("ll", "≪"); rel("gg", "≫"); rel("asymp", "≍")
        rel("parallel", "∥"); rel("bowtie", "⋈"); rel("smile", "⌣"); rel("sqsubseteq", "⊑")
        rel("sqsupseteq", "⊒"); rel("doteq", "≐"); rel("frown", "⌢"); rel("ni", "∋")
        rel("propto", "∝"); rel("vdash", "⊢"); rel("dashv", "⊣"); rel("owns", "∋")
        rel("leq", "≤"); rel("le", "≤"); rel("geq", "≥"); rel("ge", "≥")
        rel("neq", "≠"); rel("ne", "≠"); rel("approx", "≈"); rel("cong", "≅")
        rel("subset", "⊂"); rel("supset", "⊃"); rel("subseteq", "⊆"); rel("supseteq", "⊇")
        rel("in", "∈"); rel("notin", "∉"); rel("models", "⊨")
        rel("leftarrow", "←"); rel("gets", "←"); rel("rightarrow", "→"); rel("to", "→")
        rel("leftrightarrow", "↔"); rel("Rightarrow", "⇒"); rel("Leftarrow", "⇐")
        rel("Leftrightarrow", "⇔"); rel("mapsto", "↦"); rel("uparrow", "↑")
        rel("downarrow", "↓"); rel("updownarrow", "↕"); rel("Uparrow", "⇑")
        rel("Downarrow", "⇓"); rel("Updownarrow", "⇕")
        rel("lt", "<"); rel("gt", ">")
        rel("Join", "⋈"); rel("doteqdot", "≑"); rel("risingdotseq", "≓"); rel("fallingdotseq", "≒")
        rel("backsim", "∽"); rel("backsimeq", "⋍"); rel("subseteqq", "⫅"); rel("Subset", "⋐")
        rel("sqsubset", "⊏"); rel("preccurlyeq", "≼"); rel("curlyeqprec", "⋞")
        rel("precsim", "≾"); rel("precapprox", "⪷"); rel("trianglelefteq", "⊴")
        rel("vDash", "⊨"); rel("Vvdash", "⊪"); rel("bumpeq", "≏"); rel("Bumpeq", "≎")
        rel("geqq", "≧"); rel("leqq", "≦"); rel("leqslant", "⩽"); rel("geqslant", "⩾")
        rel("eqslantless", "⪕"); rel("lesssim", "≲"); rel("lessapprox", "⪅"); rel("approxeq", "≊")
        rel("lessdot", "⋖"); rel("lll", "⋘"); rel("lessgtr", "≶"); rel("lesseqgtr", "legt")
        rel("lesseqqgtr", "⪋"); rel("gtrsim", "≳"); rel("gtrapprox", "⪆"); rel("gtrdot", "⋗")
        rel("ggg", "⋙"); rel("gtrless", "≷"); rel("gtreqless", "⋛"); rel("gtreqqless", "⪌")
        rel("eqcirc", "≖"); rel("circeq", "≗"); rel("triangleq", "≜"); rel("supseteqq", "⫆")
        rel("Supset", "⋑"); rel("sqsupset", "⊐"); rel("succcurlyeq", "≽"); rel("curlyeqsucc", "⋟")
        rel("succsim", "≿"); rel("succapprox", "⪸"); rel("trianglerighteq", "⊵")
        rel("Vdash", "⊩"); rel("shortmid", "∣"); rel("shortparallel", "∥"); rel("between", "≬")
        rel("pitchfork", "⋔"); rel("varpropto", "∝"); rel("therefore", "∴"); rel("because", "∵")
        rel("eqsim", "≂"); rel("thicksim", "∼"); rel("thickapprox", "≈"); rel("Doteq", "≑")
        rel("leqq", "≦"); rel("eqslantgtr", "⪖"); rel("lesseqqgtr", "⪋")
        // AMS 长箭头
        rel("longleftarrow", "⟵"); rel("Longleftarrow", "⟸"); rel("longrightarrow", "⟶")
        rel("Longrightarrow", "⟹"); rel("longleftrightarrow", "⟷"); rel("Longleftrightarrow", "⟺")
        rel("longmapsto", "⟼"); rel("nearrow", "↗"); rel("hookleftarrow", "↩")
        rel("hookrightarrow", "↪"); rel("searrow", "↘"); rel("leftharpoonup", "↼")
        rel("rightharpoonup", "⇀"); rel("swarrow", "↙"); rel("leftharpoondown", "↽")
        rel("rightharpoondown", "⇁"); rel("nwarrow", "↖"); rel("rightleftharpoons", "⇌")
        rel("leftrightharpoons", "⇋")
        // AMS 负向关系
        rel("nless", "≮"); rel("nleq", "≰"); rel("lneq", "⪇"); rel("lneqq", "≨")
        rel("lnsim", "⋦"); rel("lnapprox", "⪉"); rel("nprec", "⊀"); rel("npreceq", "⋠")
        rel("precnsim", "⋨"); rel("precnapprox", "⪹"); rel("nsim", "≁"); rel("nmid", "∤")
        rel("nvdash", "⊬"); rel("nvDash", "⊭"); rel("ntriangleleft", "⋪")
        rel("ntrianglelefteq", "⋬"); rel("subsetneq", "⊊"); rel("subsetneqq", "⫋")
        rel("ngtr", "≯"); rel("gneq", "⪈"); rel("gneqq", "≩"); rel("gnsim", "⋧")
        rel("gnapprox", "⪊"); rel("nsucc", "⊁"); rel("nsucceq", "⋡"); rel("succnsim", "⋩")
        rel("succnapprox", "⪺"); rel("ncong", "≇"); rel("nparallel", "∦"); rel("nVDash", "⊯")
        rel("ntriangleright", "⋫"); rel("ntrianglerighteq", "⋭"); rel("supsetneq", "⊋")
        rel("supsetneqq", "⫌"); rel("nVdash", "⊮"); rel("precneqq", "⪵"); rel("succneqq", "⪶")
        rel("nsubseteq", "⊈"); rel("nsupseteq", "⊉"); rel("nleftarrow", "↚")
        rel("nrightarrow", "↛"); rel("nLeftarrow", "⇍"); rel("nRightarrow", "⇏")
        rel("nleftrightarrow", "↮"); rel("nLeftrightarrow", "⇎"); rel("ngeq", "≱")
        rel("nleqslant", "≰"); rel("ngeqslant", "≵")
        // AMS 箭头（其它）
        rel("dashrightarrow", "⇢"); rel("dashleftarrow", "⇠"); rel("leftleftarrows", "⇇")
        rel("leftrightarrows", "⇆"); rel("Lleftarrow", "⇚"); rel("twoheadleftarrow", "↞")
        rel("leftarrowtail", "↢"); rel("looparrowleft", "↫"); rel("curvearrowleft", "↶")
        rel("circlearrowleft", "↺"); rel("Lsh", "↰"); rel("upuparrows", "⇈")
        rel("upharpoonleft", "↿"); rel("downharpoonleft", "⇃"); rel("multimap", "⊸")
        rel("leftrightsquigarrow", "↭"); rel("rightrightarrows", "⇉"); rel("rightleftarrows", "⇄")
        rel("twoheadrightarrow", "↠"); rel("rightarrowtail", "↣"); rel("looparrowright", "↬")
        rel("curvearrowright", "↷"); rel("circlearrowright", "↻"); rel("Rsh", "↱")
        rel("downdownarrows", "⇊"); rel("upharpoonright", "↾"); rel("downharpoonright", "⇂")
        rel("rightsquigarrow", "↝"); rel("leadsto", "↝"); rel("Rrightarrow", "⇛")

        // ===== Binary Operators (bin) =====
        bin("pm", "±"); bin("mp", "∓"); bin("times", "×"); bin("div", "÷"); bin("cdot", "·")
        bin("ast", "∗"); bin("star", "⋆"); bin("circ", "∘"); bin("bullet", "•")
        bin("cap", "∩"); bin("cup", "∪"); bin("setminus", "∖"); bin("sqcap", "⊓")
        bin("sqcup", "⊔"); bin("land", "∧"); bin("lor", "∨"); bin("wedge", "∧"); bin("vee", "∨")
        bin("oplus", "⊕"); bin("ominus", "⊖"); bin("otimes", "⊗"); bin("oslash", "⊘")
        bin("odot", "⊙"); bin("uplus", "⊎"); bin("mp", "∓"); bin("wr", "≀"); bin("amalg", "⨿")
        bin("And", "∧"); bin("diamond", "⋄"); bin("bigtriangleup", "△")
        bin("bigtriangledown", "▽"); bin("triangleleft", "◃"); bin("triangleright", "▹")
        bin("dagger", "†"); bin("ddagger", "‡"); bin("barwedge", "⊼"); bin("veebar", "⊻")
        bin("doublebarwedge", "⩞"); bin("boxminus", "⊟"); bin("boxplus", "⊞")
        bin("boxtimes", "⊠"); bin("boxdot", "⊡"); bin("divideontimes", "⋇")
        bin("ltimes", "⋉"); bin("rtimes", "⋊"); bin("leftthreetimes", "⋋")
        bin("rightthreetimes", "⋌"); bin("curlywedge", "⋏"); bin("curlyvee", "⋎")
        bin("circleddash", "⊝"); bin("circledast", "⊛"); bin("circledcirc", "⊚")
        bin("centerdot", "⋅"); bin("intercal", "⊺"); bin("Cap", "⋒"); bin("Cup", "⋓")
        bin("dotplus", "∔"); bin("smallsetminus", "∖"); bin("unlhd", "⊴"); bin("unrhd", "⊵")
        bin("lhd", "⊲"); bin("rhd", "⊳")

        // ===== Punctuation (punct) =====
        punct("ldotp", "."); punct("cdotp", "·")

        // ===== Open / Close delimiters =====
        open("langle", "⟨"); close("rangle", "⟩"); open("lfloor", "⌊"); close("rfloor", "⌋")
        open("lceil", "⌈"); close("rceil", "⌉"); open("lbrace", "{"); close("rbrace", "}")
        open("lvert", "|"); open("lVert", "‖"); close("rvert", "|"); close("rVert", "‖")
        open("lgroup", "⟮"); close("rgroup", "⟯"); open("lmoustache", "⎰"); close("rmoustache", "⎱")

        // ===== Misc / textord =====
        textord("aleph", "ℵ"); textord("hbar", "ℏ"); textord("imath", "ı"); textord("jmath", "ȷ")
        textord("ell", "ℓ"); textord("wp", "℘"); textord("Re", "ℜ"); textord("Im", "ℑ")
        textord("mho", "℧"); textord("partial", "∂"); textord("nabla", "∇"); textord("infty", "∞")
        textord("forall", "∀"); textord("exists", "∃"); textord("nexists", "∄")
        textord("complement", "∁"); textord("emptyset", "∅"); textord("varnothing", "∅")
        textord("angle", "∠"); textord("measuredangle", "∡"); textord("sphericalangle", "∢")
        textord("spadesuit", "♠"); textord("heartsuit", "♥"); textord("diamondsuit", "♦")
        textord("clubsuit", "♣"); textord("flat", "♭"); textord("natural", "♮"); textord("sharp", "♯")
        textord("triangle", "△"); textord("triangledown", "▽"); textord("lozenge", "◊")
        textord("circledS", "Ⓢ"); textord("circledR", "®"); textord("maltese", "✠")
        textord("bigstar", "★"); textord("blacktriangle", "▲"); textord("blacktriangledown", "▼")
        textord("blacksquare", "■"); textord("blacklozenge", "⬩"); textord("checkmark", "✓")
        textord("diagup", "╱"); textord("diagdown", "╲"); textord("square", "□")
        textord("Box", "□"); textord("Diamond", "◊"); textord("yen", "¥")
        textord("beth", "ℶ"); textord("gimel", "ℷ"); textord("daleth", "ℸ")
        textord("eth", "ð"); textord("hslash", "ℏ")
        textord("top", "⊤"); textord("bot", "⊥"); textord("neg", "¬"); textord("lnot", "¬")
        textord("surd", "√"); textord("S", "§"); textord("P", "¶"); textord("dag", "†")
        textord("ddag", "‡"); textord("copyright", "©"); textord("pounds", "£")
        textord("degree", "°"); textord("prime", "′"); textord("backslash", "\\")
        textord("Finv", "Ⅎ"); textord("Game", "⅁"); textord("vartriangle", "△")
        textord("blacktriangleleft", "◀"); textord("blacktriangleright", "▶")
        textord("varvdots", "⋮")

        // ===== Big operators (op) — 也登记在 symbols 里，便于统一查表（atom=OP） =====
        // 注意：bigOperators 表保持，用于上下标堆叠的特殊布局；这里同步登记字形。
        op("sum", "∑"); op("int", "∫"); op("oint", "∮"); op("prod", "∏"); op("coprod", "∐")
        op("iint", "∬"); op("iiint", "∭"); op("oiint", "∯"); op("oiiint", "∰")
        op("bigcup", "⋃"); op("bigcap", "⋂"); op("bigvee", "⋁"); op("bigwedge", "⋀")
        op("bigoplus", "⨁"); op("bigotimes", "⨂"); op("bigodot", "⨀"); op("biguplus", "⨄")
        op("bigsqcup", "⨆")
    }

    /** 大算符（上下标居中堆叠）：命令名 → 字形。与 symbols 中 op 项重复登记，便于布局分支查找。 */
    val bigOperators: Map<String, String> = mapOf(
        "sum" to "∑", "prod" to "∏", "coprod" to "∐", "int" to "∫", "oint" to "∮",
        "iint" to "∬", "iiint" to "∭", "oiint" to "∯", "oiiint" to "∰",
        "bigcup" to "⋃", "bigcap" to "⋂", "bigvee" to "⋁", "bigwedge" to "⋀",
        "bigoplus" to "⨁", "bigotimes" to "⨂", "bigodot" to "⨀", "biguplus" to "⨄",
        "bigsqcup" to "⨆",
    )

    /** 装饰命令：命令名 → 装饰类型。 */
    val accents: Map<String, LatexAccentKind> = mapOf(
        "overline" to LatexAccentKind.OVERLINE,
        "underline" to LatexAccentKind.UNDERLINE,
        "overrightarrow" to LatexAccentKind.OVERRIGHTARROW,
        "hat" to LatexAccentKind.HAT,
        "bar" to LatexAccentKind.BAR,
        "vec" to LatexAccentKind.VEC,
        "dot" to LatexAccentKind.DOT,
        "ddot" to LatexAccentKind.DDOT,
        "tilde" to LatexAccentKind.TILDE,
        "breve" to LatexAccentKind.BREVE,
        "check" to LatexAccentKind.CHECK,
        "acute" to LatexAccentKind.ACUTE,
        "grave" to LatexAccentKind.GRAVE,
        "mathring" to LatexAccentKind.MATHRING,
    )

    /** 显式间距命令：命令名 → em 宽度。 */
    val spacing: Map<String, Float> = mapOf(
        "," to 0.17f, "!" to -0.17f, ":" to 0.22f, ";" to 0.28f,
        "enspace" to 0.5f, "quad" to 1f, "qquad" to 2f,
    )

    /** 转义生成的字面量字符：`\$` → `$`。命令名（不含反斜杠）→ 字面量字符。 */
    val escapedLiterals: Map<String, String> = mapOf(
        "%" to "%", "\$" to "\$", "&" to "&", "#" to "#", "_" to "_",
        "{" to "{", "}" to "}", "\\" to "\\", "backslash" to "\\", "|" to "|",
    )

    // ---- 构建辅助：按原子类型登记到 symbols 表 ----
    private fun MutableMap<String, Pair<String, LatexAtom>>.rel(name: String, g: String) = put(name, g to LatexAtom.REL)
    private fun MutableMap<String, Pair<String, LatexAtom>>.bin(name: String, g: String) = put(name, g to LatexAtom.BIN)
    private fun MutableMap<String, Pair<String, LatexAtom>>.open(name: String, g: String) = put(name, g to LatexAtom.OPEN)
    private fun MutableMap<String, Pair<String, LatexAtom>>.close(name: String, g: String) = put(name, g to LatexAtom.CLOSE)
    private fun MutableMap<String, Pair<String, LatexAtom>>.punct(name: String, g: String) = put(name, g to LatexAtom.PUNCT)
    private fun MutableMap<String, Pair<String, LatexAtom>>.textord(name: String, g: String) = put(name, g to LatexAtom.ORD)
    private fun MutableMap<String, Pair<String, LatexAtom>>.op(name: String, g: String) = put(name, g to LatexAtom.OP)

    /** 查询命令是否为希腊字母；若是，返回 (字形, 斜体)。 */
    fun greekOf(command: String): Pair<String, Boolean>? = greek[command]

    /** 查询命令是否为普通符号（含运算符/关系符/定界符等）；若是，返回 (字形, 原子类型)。 */
    fun symbolOf(command: String): Pair<String, LatexAtom>? = symbols[command]

    /** 查询命令是否为大算符；若是，返回字形。 */
    fun bigOperatorOf(command: String): String? = bigOperators[command]
}
