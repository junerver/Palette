package xyz.junerver.compose.palette.components.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay

@Composable
fun PMessage(
    visible: Boolean,
    text: String,
    type: MessageType = MessageType.Info,
    duration: Long = MessageDefaults.DefaultDuration,
    onClose: () -> Unit,
) {
    var localVisible by remember { mutableStateOf(visible) }

    LaunchedEffect(visible, text, duration) {
        if (visible && duration > 0) {
            delay(duration)
            onClose()
        }
    }

    LaunchedEffect(visible) {
        if (!visible) delay(MessageDefaults.AnimationDuration.toLong())
        localVisible = visible
    }

    if (!visible && !localVisible) return

    Popup(alignment = Alignment.TopCenter) {
        AnimatedVisibility(
            visible = visible && localVisible,
            enter = slideInVertically { -it / 2 } + fadeIn(),
            exit = slideOutVertically { -it / 2 } + fadeOut(),
        ) {
            val shape = RoundedCornerShape(MessageDefaults.BorderRadius)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MessageDefaults.TopPadding)
                    .padding(horizontal = 16.dp)
                    .clip(shape)
                    .border(
                        width = MessageDefaults.BorderWidth,
                        color = MessageDefaults.borderColor(type),
                        shape = shape
                    )
                    .background(MessageDefaults.containerColor())
                    .padding(
                        horizontal = MessageDefaults.HorizontalPadding,
                        vertical = MessageDefaults.VerticalPadding
                    ),
                horizontalArrangement = Arrangement.spacedBy(MessageDefaults.IconSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconForType(type),
                    contentDescription = null,
                    tint = MessageDefaults.textColor(type)
                )
                Text(
                    text = text,
                    color = MessageDefaults.textColor(type)
                )
            }
        }
    }
}

@Stable
interface MessageState {
    val visible: Boolean
    fun show(
        text: String,
        type: MessageType = MessageType.Info,
        duration: Long = MessageDefaults.DefaultDuration,
    )

    fun hide()
}

@Composable
fun rememberMessageState(): MessageState {
    val state = remember { MessageStateImpl() }

    state.props?.let { props ->
        PMessage(
            visible = state.visible,
            text = props.text,
            type = props.type,
            duration = props.duration,
            onClose = { state.hide() }
        )
    }

    return state
}

private class MessageStateImpl : MessageState {
    override var visible by mutableStateOf(false)
    var props by mutableStateOf<MessageProps?>(null)

    override fun show(text: String, type: MessageType, duration: Long) {
        props = MessageProps(text = text, type = type, duration = duration)
        visible = true
    }

    override fun hide() {
        visible = false
    }
}

private data class MessageProps(
    val text: String,
    val type: MessageType,
    val duration: Long,
)

private fun iconForType(type: MessageType): ImageVector = when (type) {
    MessageType.Info -> Icons.Default.Info
    MessageType.Success -> Icons.Outlined.CheckCircle
    MessageType.Warning -> Icons.Default.Warning
    MessageType.Error -> Icons.Default.Error
}
