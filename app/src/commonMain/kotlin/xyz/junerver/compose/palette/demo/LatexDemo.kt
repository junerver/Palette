package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.latex.PLatexFormula
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.hooks.useState

@Composable
fun LatexDemo() {
    val text = latexDemoText()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 交互式：用户可自行输入公式实时预览
        InteractiveFormulaSection(
            title = text.interactiveTitle,
            placeholder = text.interactivePlaceholder,
            initialSource = text.interactiveInitial,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 基础运算与关系
        DemoSection(title = text.arithmeticTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PLatexFormula(source = text.arithmeticInline, displayStyle = false)
                PLatexFormula(source = text.arithmeticDisplay, displayStyle = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 分数、根号、上下标
        DemoSection(title = text.fractionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PLatexFormula(source = text.fraction, displayStyle = true)
                PLatexFormula(source = text.root, displayStyle = true)
                PLatexFormula(source = text.subsup, displayStyle = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 希腊字母
        DemoSection(title = text.greekTitle) {
            PLatexFormula(source = text.greek, displayStyle = true)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 微积分与大算符
        DemoSection(title = text.calculusTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PLatexFormula(source = text.summation, displayStyle = true)
                PLatexFormula(source = text.integral, displayStyle = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 装饰与矩阵标签
        DemoSection(title = text.accentTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PLatexFormula(source = text.vector, displayStyle = false)
                PLatexFormula(source = text.overline, displayStyle = false)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 经典公式
        DemoSection(title = text.classicTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PLatexFormula(source = text.pythagorean, displayStyle = true)
                PLatexFormula(source = text.einstein, displayStyle = true)
                PLatexFormula(source = text.quadratic, displayStyle = true)
            }
        }
    }
}

/**
 * 交互式公式输入区：一个输入框 + 实时渲染预览。
 */
@Composable
private fun InteractiveFormulaSection(
    title: String,
    placeholder: String,
    initialSource: String,
) {
    val (source, setSource) = useState(initialSource)
    DemoSection(title = title) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = source,
                onValueChange = setSource,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { PText(text = placeholder) },
                singleLine = false,
                minLines = 2,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PLatexFormula(source = source, displayStyle = true)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun latexDemoText(): LatexDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            LatexDemoText(
                title = "LaTeX",
                subtitle = "基于 Canvas 自绘制的 LaTeX 数学公式渲染，支持行内与块级公式、分数、根号、上下标、希腊字母、大算符与装饰。",
                interactiveTitle = "交互式预览 (Interactive)",
                interactivePlaceholder = "输入 LaTeX 公式…",
                interactiveInitial = "\\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}",
                arithmeticTitle = "基础运算 (Arithmetic)",
                arithmeticInline = "a + b = c,\\quad a - b = c,\\quad a \\times b = c",
                arithmeticDisplay = "1 + 2 \\neq 4 \\leq 5 \\geq 3",
                fractionTitle = "分数 / 根号 / 上下标 (Fraction · Root · Scripts)",
                fraction = "\\frac{1}{2} + \\frac{3}{4} = \\frac{5}{4}",
                root = "\\sqrt{x+1} \\quad \\sqrt[3]{8} = 2",
                subsup = "x_a^2 + y_b^2 = z^2",
                greekTitle = "希腊字母 (Greek Letters)",
                greek = "\\alpha, \\beta, \\gamma, \\delta, \\epsilon, \\theta, \\lambda, \\mu, \\pi, \\sigma, \\omega \\\\ \\Gamma, \\Delta, \\Theta, \\Lambda, \\Sigma, \\Omega",
                calculusTitle = "微积分 (Calculus)",
                summation = "\\sum_{i=1}^{n} i = \\frac{n(n+1)}{2}",
                integral = "\\int_0^{\\infty} e^{-x^2} dx = \\frac{\\sqrt{\\pi}}{2}",
                accentTitle = "装饰 (Accents)",
                vector = "\\vec{a} \\cdot \\vec{b} = |\\vec{a}||\\vec{b}|\\cos\\theta",
                overline = "\\overline{AB} \\quad \\hat{x} \\quad \\bar{z}",
                classicTitle = "经典公式 (Classical Formulas)",
                pythagorean = "a^2 + b^2 = c^2",
                einstein = "E = mc^2",
                quadratic = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}",
            )

        Language.EN_US ->
            LatexDemoText(
                title = "LaTeX",
                subtitle = "Canvas-based LaTeX math formula rendering, supporting inline and block formulas, fractions, roots, sub/superscripts, Greek letters, big operators, and accents.",
                interactiveTitle = "Interactive Preview",
                interactivePlaceholder = "Type a LaTeX formula…",
                interactiveInitial = "\\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}",
                arithmeticTitle = "Arithmetic",
                arithmeticInline = "a + b = c,\\quad a - b = c,\\quad a \\times b = c",
                arithmeticDisplay = "1 + 2 \\neq 4 \\leq 5 \\geq 3",
                fractionTitle = "Fraction · Root · Scripts",
                fraction = "\\frac{1}{2} + \\frac{3}{4} = \\frac{5}{4}",
                root = "\\sqrt{x+1} \\quad \\sqrt[3]{8} = 2",
                subsup = "x_a^2 + y_b^2 = z^2",
                greekTitle = "Greek Letters",
                greek = "\\alpha, \\beta, \\gamma, \\delta, \\epsilon, \\theta, \\lambda, \\mu, \\pi, \\sigma, \\omega \\\\ \\Gamma, \\Delta, \\Theta, \\Lambda, \\Sigma, \\Omega",
                calculusTitle = "Calculus",
                summation = "\\sum_{i=1}^{n} i = \\frac{n(n+1)}{2}",
                integral = "\\int_0^{\\infty} e^{-x^2} dx = \\frac{\\sqrt{\\pi}}{2}",
                accentTitle = "Accents",
                vector = "\\vec{a} \\cdot \\vec{b} = |\\vec{a}||\\vec{b}|\\cos\\theta",
                overline = "\\overline{AB} \\quad \\hat{x} \\quad \\bar{z}",
                classicTitle = "Classical Formulas",
                pythagorean = "a^2 + b^2 = c^2",
                einstein = "E = mc^2",
                quadratic = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}",
            )
    }

private data class LatexDemoText(
    val title: String,
    val subtitle: String,
    val interactiveTitle: String,
    val interactivePlaceholder: String,
    val interactiveInitial: String,
    val arithmeticTitle: String,
    val arithmeticInline: String,
    val arithmeticDisplay: String,
    val fractionTitle: String,
    val fraction: String,
    val root: String,
    val subsup: String,
    val greekTitle: String,
    val greek: String,
    val calculusTitle: String,
    val summation: String,
    val integral: String,
    val accentTitle: String,
    val vector: String,
    val overline: String,
    val classicTitle: String,
    val pythagorean: String,
    val einstein: String,
    val quadratic: String,
)
