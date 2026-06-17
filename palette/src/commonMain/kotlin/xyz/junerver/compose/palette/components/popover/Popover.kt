package xyz.junerver.compose.palette.components.popover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import xyz.junerver.compose.hooks.useState

@Composable
fun PPopover(
    modifier: Modifier = Modifier,
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    containerColor: Color = PopoverDefaults.containerColor(),
    borderColor: Color = PopoverDefaults.borderColor(),
    cornerRadius: Dp = PopoverDefaults.cornerRadius(),
    borderWidth: Dp = PopoverDefaults.borderWidth(),
    elevation: Dp = PopoverDefaults.elevation(),
    contentPadding: Dp = PopoverDefaults.padding(),
    offset: Dp = PopoverDefaults.offset(),
    trigger: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val (localVisible, setLocalVisible) = useState(false)
    val shown = visible ?: localVisible

    fun setShown(next: Boolean) {
        if (visible == null) {
            setLocalVisible(next)
        }
        onVisibleChange?.invoke(next)
    }

    Box(
        modifier = modifier
            .popoverTriggerClick(shown = shown, onToggle = { setShown(!shown) })
    ) {
        trigger()

        if (shown) {
            Popup(onDismissRequest = { setShown(false) }) {
                val shape = RoundedCornerShape(cornerRadius)
                Box(
                    modifier = Modifier
                        .padding(top = offset)
                        .shadow(elevation, shape)
                        .clip(shape)
                        .border(
                            width = borderWidth,
                            color = borderColor,
                            shape = shape
                        )
                        .background(containerColor)
                        .padding(contentPadding)
                ) {
                    content()
                }
            }
        }
    }
}

private fun Modifier.popoverTriggerClick(
    shown: Boolean,
    onToggle: () -> Unit,
): Modifier =
    pointerInput(shown) {
        awaitPointerEventScope {
            while (true) {
                awaitFirstDown(
                    requireUnconsumed = false,
                    pass = PointerEventPass.Initial,
                )
                val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (up != null) {
                    onToggle()
                }
            }
        }
    }.semantics {
        onClick {
            onToggle()
            true
        }
    }
