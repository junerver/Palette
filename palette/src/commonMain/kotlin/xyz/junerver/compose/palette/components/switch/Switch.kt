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
    val offsetX by animateDpAsState(
        targetValue = if (checked) SwitchDefaults.CheckedThumbOffset else SwitchDefaults.ThumbOffset,
        animationSpec = tween(durationMillis = SwitchDefaults.AnimationDuration),
        label = "SwitchAnimation"
    )

    Box(
        modifier
            .size(SwitchDefaults.Width, SwitchDefaults.Height)
            .clip(RoundedCornerShape(SwitchDefaults.BorderRadius))
            .background(
                if (checked) checkedTrackColor else uncheckedTrackColor
            )
            .alpha(if (disabled) SwitchDefaults.DisabledAlpha else 1f)
            .clickableWithoutRipple(!disabled) {
                onChange?.invoke(!checked)
            }
    ) {
        Box(
            Modifier
                .offset(offsetX, SwitchDefaults.ThumbOffset)
                .size(SwitchDefaults.ThumbSize)
                .clip(RoundedCornerShape(SwitchDefaults.ThumbBorderRadius))
                .background(thumbColor)
        )
    }
}
