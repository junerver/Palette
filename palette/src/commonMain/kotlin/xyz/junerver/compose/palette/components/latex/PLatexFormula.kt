package xyz.junerver.compose.palette.components.latex

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.latex.LatexAccentBox
import xyz.junerver.compose.palette.latex.LatexAccentKind
import xyz.junerver.compose.palette.latex.LatexBigOperatorBox
import xyz.junerver.compose.palette.latex.LatexDelimitedBox
import xyz.junerver.compose.palette.latex.LatexExpr
import xyz.junerver.compose.palette.latex.LatexFontStyle
import xyz.junerver.compose.palette.latex.LatexFractionBox
import xyz.junerver.compose.palette.latex.LatexGlyphBox
import xyz.junerver.compose.palette.latex.LatexHorizontalBox
import xyz.junerver.compose.palette.latex.LatexLayout
import xyz.junerver.compose.palette.latex.LatexLayoutEngine
import xyz.junerver.compose.palette.latex.LatexMetrics
import xyz.junerver.compose.palette.latex.LatexParser
import xyz.junerver.compose.palette.latex.LatexPlacedBox
import xyz.junerver.compose.palette.latex.LatexRootBox
import xyz.junerver.compose.palette.latex.LatexSpacingBox
import xyz.junerver.compose.palette.latex.LatexTextMeasurer

/**
 * 渲染一段 LaTeX 数学公式。基于 Canvas 自绘制：解析 → 盒模型布局 → 逐盒绘制字形 / 线条，
 * 跨平台一致、无外部依赖。
 *
 * 默认样式从 [PaletteTheme] 的 `latex*` token 派生；局部 [colors] / [fontSize] 仅作实例级覆盖。
 *
 * @param source LaTeX 源码，如 `a^2 + b^2 = c^2`、`\frac{1}{2}`。
 * @param displayStyle 显示模式（块级 `$$...$$`），字号更大。
 * @param fontSize 基础字号。
 * @param colors 颜色样式。
 */
@Composable
fun PLatexFormula(
    source: String,
    modifier: Modifier = Modifier,
    displayStyle: Boolean = false,
    fontSize: TextUnit = if (displayStyle) LatexDefaults.displayFontSize else LatexDefaults.inlineFontSize,
    colors: LatexColors = LatexDefaults.colors(),
) {
    val composeMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val baseTextStyle = remember { TextStyle() }
    val latexMeasurer = remember(composeMeasurer, baseTextStyle) {
        ComposeLatexTextMeasurer(composeMeasurer, baseTextStyle)
    }

    val baseSizePx = with(density) { fontSize.toPx() }
    val parsed: LatexExpr = remember(source) { LatexParser.parse(source) }
    val layout: LatexLayout = remember(parsed, latexMeasurer, baseSizePx, displayStyle) {
        LatexLayoutEngine.layout(parsed, latexMeasurer, baseSizePx, displayStyle = displayStyle)
    }

    val widthDp = with(density) { layout.box.width.toDp() }
    val heightDp = with(density) { layout.box.height.toDp() }

    if (layout.box.width <= 0f || layout.box.height <= 0f) {
        // 退化情形：不绘制（避免 0 尺寸 Canvas）
        Canvas(modifier = modifier.size(0.dp)) {}
        return
    }

    Canvas(
        modifier = modifier.size(width = widthDp.coerceAtLeast(1.dp), height = heightDp.coerceAtLeast(1.dp)),
    ) {
        drawLatexBox(layout.box, offsetX = 0f, offsetY = 0f, colors = colors, measurer = composeMeasurer)
    }
}

/**
 * 递归绘制一个盒。坐标 (offsetX, offsetY) 为盒左上角在 DrawScope 中的位置。
 */
private fun DrawScope.drawLatexBox(
    box: xyz.junerver.compose.palette.latex.LatexBox,
    offsetX: Float,
    offsetY: Float,
    colors: LatexColors,
    measurer: TextMeasurer,
) {
    when (box) {
        is LatexGlyphBox -> {
            val color = if (box.upright && box.text.firstOrNull()?.isDigit() == true) colors.number else colors.text
            val style = TextStyle(
                color = color,
                fontSize = androidx.compose.ui.unit.TextUnit(box.sizePx, androidx.compose.ui.unit.TextUnitType.Sp),
                fontStyle = if (box.italic) androidx.compose.ui.text.font.FontStyle.Italic else null,
            )
            drawText(measurer, AnnotatedString(box.text), Offset(offsetX, offsetY), style = style, softWrap = false)
        }
        is LatexHorizontalBox -> {
            box.items.forEach { drawPlaced(it, offsetX, offsetY, colors, measurer) }
        }
        is LatexFractionBox -> {
            // 分数线
            drawLine(
                color = colors.operator,
                start = Offset(offsetX, offsetY + box.ruleY),
                end = Offset(offsetX + box.width, offsetY + box.ruleY),
                strokeWidth = box.ruleThickness,
            )
            drawPlaced(box.numerator, offsetX, offsetY, colors, measurer)
            drawPlaced(box.denominator, offsetX, offsetY, colors, measurer)
        }
        is LatexRootBox -> {
            // 自绘根号符号：短入笔 + 低谷 + 长斜升 + 顶部横线。
            val sx = offsetX + box.signOffsetX
            val sy = offsetY
            val signH = box.signHeight
            val stroke = box.ruleThickness
            val apexX = sx + box.signWidth
            val apexY = sy + box.ruleY
            val startX = sx + stroke * 0.5f
            val startY = sy + signH * 0.68f
            val valleyX = sx + box.signWidth * 0.28f
            val valleyY = sy + signH - stroke * 0.65f
            val radicalPath = Path().apply {
                moveTo(startX, startY)
                quadraticTo(
                    sx + box.signWidth * 0.14f,
                    sy + signH * 0.62f,
                    valleyX,
                    valleyY,
                )
                lineTo(apexX, apexY)
            }
            drawPath(
                path = radicalPath,
                color = colors.radical,
                style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round),
            )
            // 顶部横线：从斜升顶点水平延伸到被开方数右沿
            drawLine(
                color = colors.radical,
                start = Offset(apexX, apexY),
                end = Offset(offsetX + box.ruleX + box.ruleWidth, apexY),
                strokeWidth = stroke,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
            )
            box.index?.let { drawPlaced(it, offsetX, offsetY, colors, measurer) }
            drawPlaced(box.radicand, offsetX, offsetY, colors, measurer)
        }
        is LatexBigOperatorBox -> {
            val style = TextStyle(
                color = colors.operator,
                fontSize = androidx.compose.ui.unit.TextUnit(box.sizePx, androidx.compose.ui.unit.TextUnitType.Sp),
            )
            drawText(measurer, AnnotatedString(box.glyph), Offset(offsetX + box.glyphOffsetX, offsetY + box.glyphOffsetY), style = style, softWrap = false)
            box.sub?.let { drawPlaced(it, offsetX, offsetY, colors, measurer) }
            box.sup?.let { drawPlaced(it, offsetX, offsetY, colors, measurer) }
        }
        is LatexDelimitedBox -> {
            val style = TextStyle(
                color = colors.operator,
                fontSize = androidx.compose.ui.unit.TextUnit(box.sizePx, androidx.compose.ui.unit.TextUnitType.Sp),
            )
            if (box.leftGlyph.isNotEmpty()) {
                drawText(measurer, AnnotatedString(box.leftGlyph), Offset(offsetX + box.leftOffsetX, offsetY), style = style, softWrap = false)
            }
            drawPlaced(box.inner, offsetX, offsetY, colors, measurer)
            if (box.rightGlyph.isNotEmpty()) {
                drawText(measurer, AnnotatedString(box.rightGlyph), Offset(offsetX + box.rightOffsetX, offsetY), style = style, softWrap = false)
            }
        }
        is LatexAccentBox -> {
            drawPlaced(box.base, offsetX, offsetY, colors, measurer)
            when (box.accent) {
                LatexAccentKind.OVERLINE, LatexAccentKind.UNDERLINE -> {
                    drawLine(
                        color = colors.text,
                        start = Offset(offsetX, offsetY + box.accentY),
                        end = Offset(offsetX + box.accentWidth, offsetY + box.accentY),
                        strokeWidth = box.ruleThickness,
                    )
                }
                LatexAccentKind.OVERRIGHTARROW -> {
                    drawLine(
                        color = colors.text,
                        start = Offset(offsetX, offsetY + box.accentY),
                        end = Offset(offsetX + box.accentWidth, offsetY + box.accentY),
                        strokeWidth = box.ruleThickness,
                    )
                    // 箭头头部
                    val arrowSize = box.ruleThickness * 2.5f
                    drawLine(
                        color = colors.text,
                        start = Offset(offsetX + box.accentWidth - arrowSize, offsetY + box.accentY - arrowSize / 2f),
                        end = Offset(offsetX + box.accentWidth, offsetY + box.accentY),
                        strokeWidth = box.ruleThickness,
                    )
                }
                LatexAccentKind.VEC -> {
                    drawLine(
                        color = colors.text,
                        start = Offset(offsetX, offsetY + box.accentY),
                        end = Offset(offsetX + box.accentWidth, offsetY + box.accentY),
                        strokeWidth = box.ruleThickness,
                    )
                }
                LatexAccentKind.HAT -> {
                    drawAccentSymbol("ˆ", box, offsetX, offsetY, colors, measurer)
                }
                LatexAccentKind.BAR -> {
                    drawLine(
                        color = colors.text,
                        start = Offset(offsetX, offsetY + box.accentY),
                        end = Offset(offsetX + box.accentWidth, offsetY + box.accentY),
                        strokeWidth = box.ruleThickness,
                    )
                }
                LatexAccentKind.TILDE -> drawAccentSymbol("˜", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.DOT -> drawAccentSymbol("˙", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.DDOT -> drawAccentSymbol("¨", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.BREVE -> drawAccentSymbol("˘", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.CHECK -> drawAccentSymbol("ˇ", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.ACUTE -> drawAccentSymbol("´", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.GRAVE -> drawAccentSymbol("`", box, offsetX, offsetY, colors, measurer)
                LatexAccentKind.MATHRING -> drawAccentSymbol("˚", box, offsetX, offsetY, colors, measurer)
            }
        }
        is LatexSpacingBox -> Unit // 空白无需绘制
    }
}

private fun DrawScope.drawAccentSymbol(
    symbol: String,
    box: LatexAccentBox,
    offsetX: Float,
    offsetY: Float,
    colors: LatexColors,
    measurer: TextMeasurer,
) {
    val style = TextStyle(
        color = colors.text,
        fontSize = androidx.compose.ui.unit.TextUnit(box.base.box.height * 0.35f, androidx.compose.ui.unit.TextUnitType.Sp),
    )
    drawText(
        measurer,
        AnnotatedString(symbol),
        Offset(offsetX + (box.accentWidth) / 2f - 3f, offsetY + box.accentY),
        style = style,
        softWrap = false,
    )
}

private fun DrawScope.drawPlaced(
    placed: LatexPlacedBox,
    parentX: Float,
    parentY: Float,
    colors: LatexColors,
    measurer: TextMeasurer,
) {
    drawLatexBox(placed.box, offsetX = parentX + placed.x, offsetY = parentY + placed.y, colors = colors, measurer = measurer)
}

/**
 * 把 Compose 的 [TextMeasurer] 适配为 [LatexTextMeasurer]，供纯 Kotlin 布局引擎使用。
 */
private class ComposeLatexTextMeasurer(
    private val measurer: TextMeasurer,
    private val baseStyle: TextStyle,
) : LatexTextMeasurer {
    override fun measure(text: String, style: LatexFontStyle): LatexMetrics {
        val textStyle = baseStyle.merge(
            TextStyle(
                fontSize = androidx.compose.ui.unit.TextUnit(style.sizePx, androidx.compose.ui.unit.TextUnitType.Sp),
                fontStyle = if (style.italic) androidx.compose.ui.text.font.FontStyle.Italic else null,
            ),
        )
        val result = measurer.measure(AnnotatedString(text.ifEmpty { " " }), textStyle)
        // result.size / multiline 不需要；单行字形
        val ascent = result.firstBaseline
        val descent = result.size.height - ascent
        return LatexMetrics(
            width = result.size.width.toFloat(),
            ascent = ascent,
            descent = descent.coerceAtLeast(0f),
            capHeight = ascent,
        )
    }
}
