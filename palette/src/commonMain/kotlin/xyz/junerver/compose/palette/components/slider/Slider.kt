package xyz.junerver.compose.palette.components.slider

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

@Composable
fun PSlider(
    value: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..100f,
    @IntRange(from = 0)
    step: Int = 1,
    disabled: Boolean = false,
    activeTrackColor: Color = SliderDefaults.activeTrackColor(),
    inactiveTrackColor: Color = SliderDefaults.inactiveTrackColor(),
    thumbColor: Color = SliderDefaults.thumbColor(),
    labelColor: Color = SliderDefaults.labelColor(),
    formatter: ((Float) -> String)? = null,
    onChangeFinished: (() -> Unit)? = null
) {
    val min = range.start
    val max = range.endInclusive
    val density = LocalDensity.current
    var sliderWidth by remember { mutableIntStateOf(0) }
    var percent by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableStateOf(Dp(0f)) }

    LaunchedEffect(value, sliderWidth, range) {
        percent = if (max - min > 0f) (value.coerceIn(min, max) - min) / (max - min) else 0f
        offsetX = density.run { (sliderWidth * percent).toDp() }
    }

    fun handleChange(offsetX: Float) {
        if (!disabled && sliderWidth > 0) {
            val newFraction = (offsetX / sliderWidth).coerceIn(0f, 1f)
            val newValue = newFraction * (max - min) + min
            if (step <= 1 || newValue.toInt().rem(step) == 0) {
                onChange(newValue)
            }
        }
    }

    Row(
        modifier = Modifier
            .height(SliderDefaults.Height)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .onSizeChanged {
                    sliderWidth = it.width
                }
                .pointerInput(range) {
                    detectHorizontalDragGestures(onDragEnd = {
                        onChangeFinished?.invoke()
                    }) { change, _ ->
                        handleChange(change.position.x)
                    }
                }
                .pointerInput(range) {
                    detectTapGestures {
                        handleChange(it.x)
                        onChangeFinished?.invoke()
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(SliderDefaults.TrackHeight)
                    .background(inactiveTrackColor),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(percent)
                        .height(SliderDefaults.TrackHeight)
                        .background(activeTrackColor)
                )
            }
            Box(
                Modifier
                    .size(SliderDefaults.ThumbSize)
                    .offset(offsetX - SliderDefaults.ThumbSize / 2)
                    .shadow(
                        SliderDefaults.ThumbShadowElevation,
                        CircleShape,
                        spotColor = SliderDefaults.thumbShadowColor()
                    )
                    .background(thumbColor, CircleShape)
            )
        }

        formatter?.let {
            Spacer(modifier = Modifier.width(SliderDefaults.LabelSpacing))
            Text(
                text = it(value),
                modifier = Modifier.widthIn(SliderDefaults.LabelWidth),
                color = labelColor,
                fontSize = SliderDefaults.LabelFontSize,
                textAlign = TextAlign.End
            )
        }
    }
}

