package xyz.junerver.compose.palette.components.loading

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PLoadingDots(
    color: Color = LoadingDefaults.outlineColor(),
    animationSpec: DurationBasedAnimationSpec<Float> = tween(durationMillis = 1000),
    width: Dp = LoadingDefaults.MPWidth
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val currentIndex by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = animationSpec,
            repeatMode = RepeatMode.Restart
        ),
        label = "PLoadingDotsAnimation"
    )

    val resolvedWidth = width.coerceAtLeast(LoadingDefaults.MinDotsWidth)
    Canvas(modifier = Modifier.size(width = resolvedWidth, height = LoadingDefaults.MPHeight)) {
        val dotRadius = LoadingDefaults.DotSize.toPx()
        val spacing = (size.width - 2 * dotRadius) / 2

        repeat(3) { index ->
            val isActive = index == currentIndex.roundToInt()
            val dotColor = color.copy(alpha = if (isActive) 0.8f else 0.4f)
            val center = Offset(
                x = dotRadius + spacing * index,
                y = size.height / 2
            )

            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = center
            )
        }
    }
}

@Composable
fun PLoadingBars(color: Color = LoadingDefaults.color()) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animations = List(3) { index ->
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 400,
                    delayMillis = index * 100,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PLoadingBarsAlphaAnimation"
        )
        val scaleY by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 400,
                    delayMillis = index * 100,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PLoadingBarsScaleAnimation"
        )
        Pair(alpha, scaleY)
    }

    Canvas(modifier = Modifier.size(LoadingDefaults.WebSize)) {
        animations.forEachIndexed { index, item ->
            val strokeWidth = LoadingDefaults.StrokeWidth.toPx() * 2
            val spacing = (size.width - (3 * strokeWidth)) / 2

            scale(scaleX = 1f, scaleY = item.second) {
                drawLine(
                    color = color.copy(alpha = item.first),
                    start = Offset(
                        x = strokeWidth / 2 + (strokeWidth + spacing) * index,
                        y = 0f
                    ),
                    end = Offset(
                        x = strokeWidth / 2 + (strokeWidth + spacing) * index,
                        y = size.height
                    ),
                    strokeWidth
                )
            }
        }
    }
}

@Composable
fun PLoadingCircle(
    borderColor: Color = LoadingDefaults.onPrimaryColor(),
    dotColor: Color = borderColor,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(
        durationMillis = 1200,
        easing = LinearEasing
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = animationSpec,
            repeatMode = RepeatMode.Restart
        ),
        label = "PLoadingCircleAnimation"
    )

    Canvas(
        modifier = Modifier
            .size(LoadingDefaults.MobileSize)
            .border(LoadingDefaults.StrokeWidth, borderColor, CircleShape)
    ) {
        val circleRadius = size.minDimension / 2 - Dp(8f).toPx()
        val dotRadius = LoadingDefaults.DotSize.toPx()
        val center = size.center
        val angleRad = angle.value * PI / 180.0
        val dotX = cos(angleRad) * circleRadius + center.x
        val dotY = sin(angleRad) * circleRadius + center.y

        drawCircle(dotColor, radius = dotRadius, center = Offset(dotX.toFloat(), dotY.toFloat()))
    }
}

@Composable
fun PLoadingBounce(
    color: Color = LoadingDefaults.color(),
    width: Dp = LoadingDefaults.MPWidth
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetY = LocalDensity.current.run { 3.toDp().toPx() }
    val animations = List(3) { index ->
        val value by infiniteTransition.animateFloat(
            initialValue = -offsetY,
            targetValue = offsetY,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 400,
                    delayMillis = index * 100,
                    easing = FastOutLinearInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PLoadingBounceAnimation"
        )
        value
    }

    Canvas(modifier = Modifier.size(width = width, height = LoadingDefaults.MPHeight)) {
        val dotRadius = LoadingDefaults.DotSize.toPx()
        val spacing = (size.width - 2 * dotRadius) / 2

        animations.forEachIndexed { index, item ->
            val center = Offset(
                x = dotRadius + spacing * index,
                y = size.height / 2 - item
            )

            drawCircle(
                color,
                radius = dotRadius,
                center = center
            )
        }
    }
}

@Composable
fun PLoading(
    size: Dp = LoadingDefaults.Size,
    color: Color = LoadingDefaults.color()
) {
    PLoadingDots(
        color = color,
        animationSpec = tween(durationMillis = LoadingDefaults.AnimationDuration),
        width = size
    )
}