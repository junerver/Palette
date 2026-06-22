package xyz.junerver.compose.palette.components.code

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.code.HighlightedCode
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Suppress("DEPRECATION")
@Composable
fun PCodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String = "kotlin",
    showCopyAction: Boolean = true,
    showLineNumbers: Boolean = false,
    highlightedLines: Set<Int> = emptySet(),
    title: String? = null,
    firstLineNumber: Int = 1,
    colors: CodeBlockColors = CodeBlockDefaults.colors(),
    highlightedCode: HighlightedCode = PaletteCodeHighlighter.highlight(code.trimIndent(), language),
) {
    val clipboardManager = LocalClipboardManager.current
    val (copied, setCopied) = useState(false)
    val shape = RoundedCornerShape(CodeBlockDefaults.cornerRadius())

    LaunchedEffect(copied) {
        if (copied) {
            delay(1600)
            setCopied(false)
        }
    }

    val iconTint by animateColorAsState(
        targetValue = if (copied) PaletteTheme.colors.success else colors.contentColor,
        animationSpec = tween(PaletteTheme.motion.durationFast),
    )
    val headerBackgroundColor =
        if (colors.headerBackgroundColor == Color.Unspecified) {
            colors.backgroundColor
        } else {
            colors.headerBackgroundColor
        }
    val highlightedLineColor =
        if (colors.highlightedLineColor == Color.Unspecified) {
            PaletteTheme.colors.bgSelected
        } else {
            colors.highlightedLineColor
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.backgroundColor)
                .border(CodeBlockDefaults.borderWidth(), colors.borderColor, shape),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(end = if (showCopyAction) 40.dp else 0.dp),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    color = colors.contentColor,
                    style = PaletteTheme.typography.label.copy(fontWeight = FontWeight.SemiBold),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(headerBackgroundColor)
                            .padding(horizontal = CodeBlockDefaults.padding(), vertical = 8.dp),
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(CodeBlockDefaults.padding()),
            ) {
                highlightedCode.tokens.forEachIndexed { index, line ->
                    val lineNumber = firstLineNumber + index
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    if (lineNumber in highlightedLines) {
                                        highlightedLineColor
                                    } else {
                                        Color.Transparent
                                    },
                                )
                                .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        if (showLineNumbers) {
                            Text(
                                text = lineNumber.toString(),
                                color = colors.lineNumberColor,
                                style = CodeBlockDefaults.textStyle().copy(fontFamily = FontFamily.Monospace),
                                textAlign = TextAlign.End,
                                modifier =
                                    Modifier
                                        .width(36.dp)
                                        .padding(end = 12.dp),
                            )
                        }
                        Text(
                            text = line.toAnnotatedString(colors),
                            style = CodeBlockDefaults.textStyle().copy(fontFamily = FontFamily.Monospace),
                        )
                    }
                }
            }
        }

        if (showCopyAction) {
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(code.trimIndent()))
                    setCopied(true)
                },
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
            ) {
                Icon(
                    imageVector = if (copied) Icons.Default.Done else Icons.Default.ContentCopy,
                    contentDescription = if (copied) "已复制" else "复制代码",
                    tint = iconTint,
                )
            }
        }
    }
}

private fun List<CodeToken>.toAnnotatedString(colors: CodeBlockColors): AnnotatedString =
    buildAnnotatedString {
        forEach { token ->
            pushStyle(SpanStyle(color = colors.colorFor(token.type)))
            append(token.text)
            pop()
        }
    }
