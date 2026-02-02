package xyz.junerver.compose.palette.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Popup
import xyz.junerver.compose.hooks.useState

@Composable
fun PImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: (@Composable () -> Unit)? = null,
    error: (@Composable () -> Unit)? = null,
    previewable: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit
) {
    val (showPreview, setShowPreview) = useState(false)
    val shape = ImageDefaults.Shape
    val containerColor = ImageDefaults.containerColor()

    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .then(
                if (previewable) {
                    Modifier.clickable { setShowPreview(true) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        placeholder?.invoke()

        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier
        )

        error?.invoke()
    }

    if (previewable && showPreview) {
        Popup(
            onDismissRequest = { setShowPreview(false) },
            alignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable { setShowPreview(false) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
