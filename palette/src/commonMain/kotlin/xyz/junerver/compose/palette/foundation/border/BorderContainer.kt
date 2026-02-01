package xyz.junerver.compose.palette.foundation.border

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object BorderContainerDefaults {
    val Height: Dp = 28.dp
    val Width: Dp = 300.dp
    val BorderWidth: Dp = 0.5.dp
    val CornerSize: Dp = 5.dp
    val ContentPadding: Dp = 8.dp

    @Composable
    fun borderColor(): Color = PaletteTheme.colors.border

    @Composable
    fun backgroundColor(): Color = PaletteTheme.colors.surface
}

@Composable
fun BorderContainer(
    modifier: Modifier = Modifier,
    height: Dp = BorderContainerDefaults.Height,
    width: Dp = BorderContainerDefaults.Width,
    borderWidth: Dp = BorderContainerDefaults.BorderWidth,
    cornerSize: Dp = BorderContainerDefaults.CornerSize,
    borderColor: Color = BorderContainerDefaults.borderColor(),
    backgroundColor: Color = BorderContainerDefaults.backgroundColor(),
    content: @Composable BoxScope.() -> Unit = {}
) {
    val shape = RoundedCornerShape(cornerSize)
    Box(
        modifier = modifier
            .padding(BorderContainerDefaults.ContentPadding)
            .border(borderWidth, borderColor, shape)
            .clip(shape)
            .background(backgroundColor)
            .padding(BorderContainerDefaults.ContentPadding)
            .width(width)
            .height(height),
        content = content
    )
}
