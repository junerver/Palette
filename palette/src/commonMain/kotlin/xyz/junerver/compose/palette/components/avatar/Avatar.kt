package xyz.junerver.compose.palette.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PAvatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    content: (@Composable BoxScope.() -> Unit)? = null,
    text: String? = null,
    shape: Shape = CircleShape,
    backgroundColor: Color = AvatarDefaults.backgroundColor(),
    textColor: Color = AvatarDefaults.textColor()
) {
    Box(
        modifier = modifier
            .size(size.size)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        } else if (text != null) {
            val initials = if (text.isNotEmpty()) text.take(1).uppercase() else ""
            Text(
                text = initials,
                color = textColor,
                style = when (size) {
                    AvatarSize.Small -> PaletteTheme.typography.label
                    AvatarSize.Medium -> PaletteTheme.typography.body
                    else -> PaletteTheme.typography.title
                }
            )
        }
    }
}
