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
    fun height(): Dp = PaletteTheme.componentThemes.borderContainer.height

    @Composable
    fun width(): Dp = PaletteTheme.componentThemes.borderContainer.width

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.borderContainer.borderWidth

    @Composable
    fun cornerSize(): Dp = PaletteTheme.componentThemes.borderContainer.cornerRadius

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.borderContainer.contentPadding

    @Composable
    fun borderColor(): Color = PaletteTheme.componentThemes.borderContainer.borderColor

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.borderContainer.backgroundColor
}

@Composable
fun BorderContainer(
    modifier: Modifier = Modifier,
    height: Dp = BorderContainerDefaults.height(),
    width: Dp = BorderContainerDefaults.width(),
    borderWidth: Dp = BorderContainerDefaults.borderWidth(),
    cornerSize: Dp = BorderContainerDefaults.cornerSize(),
    borderColor: Color = BorderContainerDefaults.borderColor(),
    backgroundColor: Color = BorderContainerDefaults.backgroundColor(),
    contentPadding: Dp = BorderContainerDefaults.contentPadding(),
    content: @Composable BoxScope.() -> Unit = {}
) {
    val shape = RoundedCornerShape(cornerSize)
    Box(
        modifier = modifier
            .padding(contentPadding)
            .border(borderWidth, borderColor, shape)
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding)
            .width(width)
            .height(height),
        content = content
    )
}
