package xyz.junerver.compose.palette.components.latex

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * LaTeX 数学排版的可调样式。从 [PaletteTheme.componentThemes.utility] 的 `latex*` token 派生，
 * 项目方可通过顶层主题统一调整；[PLatexFormula] 的局部参数仅作实例级覆盖。
 */
@Immutable
data class LatexColors(
    /** 变量 / 普通文本字形颜色。 */
    val text: Color,
    /** 运算符 / 分数线 / 辅助线条颜色。 */
    val operator: Color,
    /** 数字颜色（正体）。 */
    val number: Color,
    /** 根号符号与上方横线颜色。 */
    val radical: Color,
)

/**
 * LaTeX 渲染默认值。所有颜色 / 字号均从顶层主题 token 派生，避免硬编码。
 */
@Stable
object LatexDefaults {
    /** 行内公式默认字号。 */
    val inlineFontSize: TextUnit = 16.sp

    /** 块级（显示模式）公式默认字号。 */
    val displayFontSize: TextUnit = 20.sp

    /** 从主题 token 派生默认颜色。 */
    @androidx.compose.runtime.Composable
    fun colors(): LatexColors {
        val tokens = PaletteTheme.componentThemes.utility
        return LatexColors(
            text = tokens.latexTextColor,
            operator = tokens.latexOperatorColor,
            number = tokens.latexNumberColor,
            radical = tokens.latexRadicalColor,
        )
    }

    /** 行内公式相对段落字号的缩放系数（来自 token）。 */
    @androidx.compose.runtime.Composable
    fun inlineScale(): Float = PaletteTheme.componentThemes.utility.latexInlineScale
}
