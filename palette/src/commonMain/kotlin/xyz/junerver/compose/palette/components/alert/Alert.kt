package xyz.junerver.compose.palette.components.alert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PAlert(
    message: String,
    modifier: Modifier = Modifier,
    type: AlertType = AlertType.Info,
    description: String? = null,
    closable: Boolean = false,
    onClose: (() -> Unit)? = null,
    icon: (@Composable (() -> Unit))? = null,
    action: (@Composable (() -> Unit))? = null,
) {
    val (visible, setVisible) = useState(true)

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut() + shrinkVertically()
    ) {
        val containerColor = AlertDefaults.containerColor(type)
        val borderColor = AlertDefaults.borderColor(type)
        val contentColor = AlertDefaults.contentColor(type)
        val iconSize = AlertDefaults.iconSize()
        val iconSpacing = AlertDefaults.iconSpacing()
        val closeIconSize = AlertDefaults.closeIconSize()
        val shape = RoundedCornerShape(AlertDefaults.cornerRadius())

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(containerColor)
                .border(AlertDefaults.borderWidth(), borderColor, shape)
                .padding(AlertDefaults.contentPadding()),
            verticalAlignment = Alignment.Top
        ) {
            if (icon != null) {
                icon()
            } else {
                Icon(
                    imageVector = iconForType(type),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
            }

            Spacer(modifier = Modifier.width(iconSpacing))

            Column(modifier = Modifier.weight(1f)) {
                PText(
                    text = message,
                    color = contentColor,
                    style = AlertDefaults.messageTextStyle()
                )
                if (description != null) {
                    Spacer(modifier = Modifier.height(AlertDefaults.messageDescriptionSpacing()))
                    PText(
                        text = description,
                        color = contentColor.copy(alpha = AlertDefaults.descriptionAlpha()),
                        style = AlertDefaults.descriptionTextStyle()
                    )
                }
            }

            if (action != null) {
                Spacer(modifier = Modifier.width(iconSpacing))
                action()
            }

            if (closable) {
                Spacer(modifier = Modifier.width(iconSpacing))
                IconButton(
                    onClick = {
                        setVisible(false)
                        onClose?.invoke()
                    },
                    modifier = Modifier.size(closeIconSize)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = contentColor.copy(alpha = AlertDefaults.closeIconAlpha()),
                        modifier = Modifier.size(closeIconSize * 0.75f)
                    )
                }
            }
        }
    }
}

private fun iconForType(type: AlertType): ImageVector = when (type) {
    AlertType.Info -> Icons.Default.Info
    AlertType.Success -> Icons.Outlined.CheckCircle
    AlertType.Warning -> Icons.Default.Warning
    AlertType.Error -> Icons.Default.Error
}
