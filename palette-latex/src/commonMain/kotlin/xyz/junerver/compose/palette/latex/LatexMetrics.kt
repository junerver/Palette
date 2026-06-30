package xyz.junerver.compose.palette.latex

/**
 * 数学排版所需的字符 / 文本度量结果（像素）。
 *
 * @param width 前进宽度
 * @param ascent 基线之上的高度
 * @param descent 基线之下的深度
 * @param capHeight 大写字母高度（用于符号垂直居中校准；无精确值时可用 ascent 近似）
 */
public data class LatexMetrics(
    public val width: Float,
    public val ascent: Float,
    public val descent: Float,
    public val capHeight: Float = ascent,
)

/**
 * 数学排版字号与字体风格的抽象描述。
 *
 * 由 [LatexTextMeasurer] 实现负责把它映射到平台真实字体（如 Compose 的 TextStyle/FontStyle）。
 * 仅暴露数学排版关心的两个维度：像素字号、是否斜体。这样纯 Kotlin 层无需依赖 Compose。
 */
public data class LatexFontStyle(
    public val sizePx: Float,
    public val italic: Boolean,
)

/**
 * 文本 / 字形度量器抽象。
 *
 * `palette-latex` 是纯 Kotlin 模块，无法直接调用 Compose 字体；布局引擎通过此接口获取
 * 字符串宽度与基线信息。`palette` 模块提供基于 Compose `TextMeasurer` 的实现。
 */
public fun interface LatexTextMeasurer {
    /**
     * 度量 [text] 在 [style] 下的尺寸。返回 [LatexMetrics]。
     */
    public fun measure(text: String, style: LatexFontStyle): LatexMetrics
}
