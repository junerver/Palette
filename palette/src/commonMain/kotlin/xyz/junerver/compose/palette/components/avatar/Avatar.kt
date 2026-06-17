package xyz.junerver.compose.palette.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun PAvatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    content: (@Composable BoxScope.() -> Unit)? = null,
    text: String? = null,
    shape: Shape = AvatarDefaults.shape(),
    backgroundColor: Color = AvatarDefaults.backgroundColor(),
    textColor: Color = AvatarDefaults.textColor(),
    painter: Painter? = null,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier
            .size(AvatarDefaults.size(size))
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        } else if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (text != null) {
            val initials = if (text.isNotEmpty()) text.take(1).uppercase() else ""
            Text(
                text = initials,
                color = textColor,
                style = AvatarDefaults.textStyle(size)
            )
        }
    }
}
