package xyz.junerver.compose.palette.components.code

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.backgroundColor)
                .border(CodeBlockDefaults.borderWidth(), colors.borderColor, shape),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(CodeBlockDefaults.padding())
                    .padding(end = if (showCopyAction) 40.dp else 0.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = highlightedCode.toAnnotatedString(colors),
                style = CodeBlockDefaults.textStyle().copy(fontFamily = FontFamily.Monospace),
            )
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

private fun HighlightedCode.toAnnotatedString(colors: CodeBlockColors): AnnotatedString =
    buildAnnotatedString {
        tokens.forEachIndexed { lineIndex, line ->
            line.forEach { token ->
                pushStyle(SpanStyle(color = colors.colorFor(token.type)))
                append(token.text)
                pop()
            }
            if (lineIndex != tokens.lastIndex) append('\n')
        }
    }
