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
import androidx.compose.foundation.layout.size
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
    duration: Long = NotificationDefaults.defaultDuration(),
    onClose: () -> Unit,
) {
    val (localVisible, setLocalVisible) = useState(visible)
    val latestOnClose = useLatestState(onClose)
    val animationDuration = NotificationDefaults.animationDuration()

    LaunchedEffect(visible, title, content, duration) {
        if (visible && duration > 0) {
            delay(duration)
            latestOnClose.value()
        }
    }

    LaunchedEffect(visible, animationDuration) {
        if (!visible) delay(animationDuration.toLong())
        setLocalVisible(visible)
    }

    if (!visible && !localVisible) return

    Popup(alignment = Alignment.TopEnd) {
        AnimatedVisibility(
            visible = visible && localVisible,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut()
        ) {
            val shape = RoundedCornerShape(NotificationDefaults.cornerRadius())
            Column(
                modifier = Modifier
                    .padding(top = NotificationDefaults.topPadding())
                    .padding(end = NotificationDefaults.endPadding())
                    .widthIn(
                        min = NotificationDefaults.minWidth(),
                        max = NotificationDefaults.maxWidth()
                    )
                    .clip(shape)
                    .border(
                        width = NotificationDefaults.borderWidth(),
                        color = NotificationDefaults.accentColor(type).copy(alpha = NotificationDefaults.borderAlpha()),
                        shape = shape
                    )
                    .background(NotificationDefaults.containerColor())
                    .padding(
                        horizontal = NotificationDefaults.horizontalPadding(),
                        vertical = NotificationDefaults.verticalPadding()
                    ),
                verticalArrangement = Arrangement.spacedBy(NotificationDefaults.contentSpacing())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(NotificationDefaults.titleSpacing())
                ) {
                    Icon(
                        imageVector = notificationIcon(type),
                        contentDescription = null,
                        tint = NotificationDefaults.accentColor(type),
                        modifier = Modifier.size(NotificationDefaults.iconSize())
                    )
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        color = NotificationDefaults.titleColor(),
                        style = NotificationDefaults.titleTextStyle()
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = NotificationDefaults.contentColor(),
                            modifier = Modifier.size(NotificationDefaults.closeIconSize())
                        )
                    }
                }
                if (!content.isNullOrBlank()) {
                    Text(
                        text = content,
                        color = NotificationDefaults.contentColor(),
                        style = NotificationDefaults.contentTextStyle(),
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
