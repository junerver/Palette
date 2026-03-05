package xyz.junerver.compose.palette.components.popover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup

@Composable
fun PPopover(
    modifier: Modifier = Modifier,
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    containerColor: Color = PopoverDefaults.containerColor(),
    borderColor: Color = PopoverDefaults.borderColor(),
    trigger: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var localVisible by remember { mutableStateOf(false) }
    val shown = visible ?: localVisible

    fun setShown(next: Boolean) {
        if (visible == null) {
            localVisible = next
        }
        onVisibleChange?.invoke(next)
    }

    Box(modifier = modifier.clickable { setShown(!shown) }) {
        trigger()

        if (shown) {
            Popup(onDismissRequest = { setShown(false) }) {
                Box(
                    modifier = Modifier
                        .padding(top = PopoverDefaults.Padding)
                        .clip(RoundedCornerShape(PopoverDefaults.CornerRadius))
                        .border(
                            width = PopoverDefaults.BorderWidth,
                            color = borderColor,
                            shape = RoundedCornerShape(PopoverDefaults.CornerRadius)
                        )
                        .background(containerColor)
                        .padding(PopoverDefaults.Padding)
                ) {
                    content()
                }
            }
        }
    }
}
