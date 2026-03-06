package xyz.junerver.compose.palette.components.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.hooks.useLatestState
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.message.MessageType

@Composable
fun PNotification(
    visible: Boolean,
    title: String,
    content: String? = null,
    type: MessageType = MessageType.Info,
    duration: Long = NotificationDefaults.DefaultDuration,
    onClose: () -> Unit,
) {
    val (localVisible, setLocalVisible) = useState(visible)
    val latestOnClose = useLatestState(onClose)

    LaunchedEffect(visible, title, content, duration) {
        if (visible && duration > 0) {
            delay(duration)
            latestOnClose.value()
        }
    }

    LaunchedEffect(visible) {
        if (!visible) delay(NotificationDefaults.AnimationDuration.toLong())
        setLocalVisible(visible)
    }

    if (!visible && !localVisible) return

    Popup(alignment = Alignment.TopEnd) {
        AnimatedVisibility(
            visible = visible && localVisible,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut()
        ) {
            val shape = RoundedCornerShape(NotificationDefaults.CornerRadius)
            Column(
                modifier = Modifier
                    .padding(top = NotificationDefaults.TopPadding)
                    .padding(end = 16.dp)
                    .widthIn(
                        min = NotificationDefaults.MinWidth,
                        max = NotificationDefaults.MaxWidth
                    )
                    .clip(shape)
                    .border(
                        width = NotificationDefaults.BorderWidth,
                        color = NotificationDefaults.accentColor(type).copy(alpha = 0.45f),
                        shape = shape
                    )
                    .background(NotificationDefaults.containerColor())
                    .padding(
                        horizontal = NotificationDefaults.HorizontalPadding,
                        vertical = NotificationDefaults.VerticalPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = notificationIcon(type),
                        contentDescription = null,
                        tint = NotificationDefaults.accentColor(type)
                    )
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        color = NotificationDefaults.titleColor()
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = NotificationDefaults.contentColor()
                        )
                    }
                }
                if (!content.isNullOrBlank()) {
                    Text(
                        text = content,
                        color = NotificationDefaults.contentColor(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Stable
interface NotificationState {
    val visible: Boolean
    fun show(
        title: String,
        content: String? = null,
        type: MessageType = MessageType.Info,
        duration: Long = NotificationDefaults.DefaultDuration,
    )

    fun hide()
}

@Composable
fun rememberNotificationState(): NotificationState {
    val state = useCreation { NotificationStateImpl() }.current

    state.props?.let { props ->
        PNotification(
            visible = state.visible,
            title = props.title,
            content = props.content,
            type = props.type,
            duration = props.duration,
            onClose = { state.hide() }
        )
    }

    return state
}

private class NotificationStateImpl : NotificationState {
    override var visible by mutableStateOf(false)
    var props by mutableStateOf<NotificationProps?>(null)

    override fun show(title: String, content: String?, type: MessageType, duration: Long) {
        props = NotificationProps(
            title = title,
            content = content,
            type = type,
            duration = duration
        )
        visible = true
    }

    override fun hide() {
        visible = false
    }
}

private data class NotificationProps(
    val title: String,
    val content: String?,
    val type: MessageType,
    val duration: Long,
)

private fun notificationIcon(type: MessageType): ImageVector = when (type) {
    MessageType.Info -> Icons.Default.Info
    MessageType.Success -> Icons.Outlined.CheckCircle
    MessageType.Warning -> Icons.Default.Warning
    MessageType.Error -> Icons.Default.Error
}
