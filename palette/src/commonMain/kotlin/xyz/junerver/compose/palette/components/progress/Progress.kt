package xyz.junerver.compose.palette.components.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun PProgress(
    percent: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = ProgressDefaults.progressColor(),
    trackColor: Color = ProgressDefaults.trackColor(),
    textColor: Color = ProgressDefaults.textColor(),
    formatter: ((percent: Float) -> String)? = { "${it.toInt()}%" }
) {
    val localPercent = percent.coerceIn(0f, 100f)

    Row(
        modifier = modifier.height(ProgressDefaults.LinearContainerHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(ProgressDefaults.LinearHeight)
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        animateFloatAsState(
                            targetValue = localPercent / 100,
                            label = "ProgressAnimation"
                        ).value
                    )
                    .fillMaxHeight()
                    .background(progressColor)
            )
        }
        formatter?.also {
            Spacer(modifier = Modifier.width(ProgressDefaults.LabelSpacing))
            Text(
                text = it(localPercent),
                modifier = Modifier.widthIn(ProgressDefaults.LabelWidth),
                color = textColor,
                fontSize = ProgressDefaults.TextSize,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun PCircleProgress(
    percent: Float,
    modifier: Modifier = Modifier,
    size: Dp = ProgressDefaults.CircleSize,
    strokeWidth: Dp = ProgressDefaults.CircleStrokeWidth,
    fontSize: TextUnit = ProgressDefaults.TextSize,
    progressColor: Color = ProgressDefaults.progressColor(),
    trackColor: Color = ProgressDefaults.trackColor(),
    textColor: Color = ProgressDefaults.circleTextColor(),
    formatter: ((percent: Float) -> String)? = { "${it.toInt()}%" }
) {
    val localPercent = percent.coerceIn(0f, 100f)
    val animatedAngle by animateFloatAsState(
        targetValue = 360 * (localPercent / 100),
        label = "CircleProgressAnimation"
    )
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .padding(vertical = Dp(20f))
            .size(size)
    ) {
        val radius = this.size.width / 2
        drawCircle(
            color = trackColor,
            radius = radius,
            style = Stroke(width = strokeWidth.toPx())
        )
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = animatedAngle,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        )
        formatter?.also {
            val text = AnnotatedString(
                it(localPercent),
                SpanStyle(fontSize = fontSize, color = textColor)
            )
            val textLayoutResult = textMeasurer.measure(text)
            drawText(
                textMeasurer,
                text,
                Offset(
                    x = radius - textLayoutResult.size.width / 2,
                    y = radius - textLayoutResult.size.height / 2
                )
            )
        }
    }
}
