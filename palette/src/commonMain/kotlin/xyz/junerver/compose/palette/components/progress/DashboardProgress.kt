package xyz.junerver.compose.palette.components.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun PDashboardProgress(
    percent: Float,
    modifier: Modifier = Modifier,
    size: Dp = DashboardProgressDefaults.Size,
    strokeWidth: Dp = DashboardProgressDefaults.StrokeWidth,
    fontSize: TextUnit = DashboardProgressDefaults.FontSize,
    progressColor: Color = DashboardProgressDefaults.progressColor(),
    trackColor: Color = DashboardProgressDefaults.trackColor(),
    textColor: Color = DashboardProgressDefaults.textColor(),
    formatter: ((Float) -> String)? = DashboardProgressDefaults::defaultFormatter
) {
    val localPercent = percent.coerceIn(0f, 100f)
    val animatedSweep by animateFloatAsState(
        targetValue = DashboardProgressDefaults.SweepAngle * (localPercent / 100f),
        label = "DashboardProgressAnimation"
    )

    Box(
        modifier = modifier.requiredSize(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = trackColor,
                startAngle = DashboardProgressDefaults.StartAngle,
                sweepAngle = DashboardProgressDefaults.SweepAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
            drawArc(
                color = progressColor,
                startAngle = DashboardProgressDefaults.StartAngle,
                sweepAngle = animatedSweep,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        formatter?.also {
            Text(
                text = it(localPercent),
                color = textColor,
                fontSize = fontSize
            )
        }
    }
}
