package xyz.junerver.compose.palette.components.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ListDefaults {
    val ItemHeight: Dp = 56.dp
    val DividerHeight: Dp = 1.dp
    val ContentPadding: Dp = 16.dp

    @Composable
    fun dividerColor(): Color = PaletteTheme.colors.border
}
