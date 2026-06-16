package xyz.junerver.compose.palette.components.segmented

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

data class SegmentedOption(
    val value: String,
    val label: String,
    val icon: (@Composable (() -> Unit))? = null,
    val disabled: Boolean = false,
)

@Composable
fun PSegmented(
    options: List<SegmentedOption>,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: ComponentSize = ComponentSize.Medium,
) {
    val shape = RoundedCornerShape(SegmentedDefaults.CornerRadius)

    var indicatorX by remember { mutableStateOf(0.dp) }
    var indicatorWidth by remember { mutableStateOf(0.dp) }

    val animatedX by animateDpAsState(
        targetValue = indicatorX,
        animationSpec = tween(durationMillis = SegmentedDefaults.IndicatorAnimationDuration),
        label = "SegmentedIndicatorX"
    )
    val animatedWidth by animateDpAsState(
        targetValue = indicatorWidth,
        animationSpec = tween(durationMillis = SegmentedDefaults.IndicatorAnimationDuration),
        label = "SegmentedIndicatorWidth"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(SegmentedDefaults.containerColor())
            .padding(
                horizontal = SegmentedDefaults.ItemPaddingHorizontal / 2,
                vertical = SegmentedDefaults.ItemPaddingVertical / 2
            ),
        contentAlignment = Alignment.TopStart
    ) {
        if (animatedWidth > 0.dp) {
            Box(
                modifier = Modifier
                    .offset(x = animatedX)
                    .size(animatedWidth, size.height)
                    .clip(shape)
                    .background(SegmentedDefaults.selectedItemColor())
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                key(option.value) {
                    val isSelected = option.value == value
                    val isDisabled = option.disabled

                    SegmentedItem(
                        option = option,
                        isSelected = isSelected,
                        isDisabled = isDisabled,
                        size = size,
                        onItemClick = {
                            if (!isDisabled) onValueChange(option.value)
                        },
                        onGloballyPositioned = { x, width ->
                            if (isSelected) {
                                indicatorX = x
                                indicatorWidth = width
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentedItem(
    option: SegmentedOption,
    isSelected: Boolean,
    isDisabled: Boolean,
    size: ComponentSize,
    onItemClick: () -> Unit,
    onGloballyPositioned: (x: Dp, width: Dp) -> Unit,
) {
    val density = LocalDensity.current

    val textColor = when {
        isSelected -> SegmentedDefaults.selectedTextColor()
        isDisabled -> SegmentedDefaults.disabledTextColor()
        else -> SegmentedDefaults.textColor()
    }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInParent()
                val w = with(density) { coordinates.size.width.toDp() }
                val x = with(density) { pos.x.toDp() }
                onGloballyPositioned(x, w)
            }
            .alpha(if (isDisabled) SegmentedDefaults.DisabledAlpha else 1f)
            .then(
                if (!isDisabled) {
                    Modifier.clickableWithoutRipple(onClick = onItemClick)
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = SegmentedDefaults.ItemPaddingHorizontal,
                vertical = SegmentedDefaults.ItemPaddingVertical
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            option.icon?.invoke()
            PText(
                text = option.label,
                color = textColor,
                fontSize = size.fontSize,
            )
        }
    }
}
