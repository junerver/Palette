package xyz.junerver.compose.palette.components.switch

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun PSwitch(
    checked: Boolean,
    onChange: ((checked: Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    checkedTrackColor: Color = SwitchDefaults.checkedTrackColor(),
    uncheckedTrackColor: Color = SwitchDefaults.uncheckedTrackColor(),
    thumbColor: Color = SwitchDefaults.thumbColor()
) {
    val thumbOffset = SwitchDefaults.thumbOffset()
    val offsetX by animateDpAsState(
        targetValue = if (checked) SwitchDefaults.checkedThumbOffset() else thumbOffset,
        animationSpec = tween(durationMillis = SwitchDefaults.animationDuration()),
        label = "SwitchAnimation"
    )
    val trackColor = when {
        disabled -> SwitchDefaults.disabledTrackColor()
        checked -> checkedTrackColor
        else -> uncheckedTrackColor
    }

    Box(
        modifier
            .size(SwitchDefaults.width(), SwitchDefaults.height())
            .clip(RoundedCornerShape(SwitchDefaults.borderRadius()))
            .background(trackColor)
            .alpha(if (disabled) SwitchDefaults.disabledAlpha() else 1f)
            .clickableWithoutRipple(!disabled) {
                onChange?.invoke(!checked)
            }
    ) {
        Box(
            Modifier
                .offset(offsetX, thumbOffset)
                .size(SwitchDefaults.thumbSize())
                .clip(RoundedCornerShape(SwitchDefaults.thumbBorderRadius()))
                .background(thumbColor)
        )
    }
}
