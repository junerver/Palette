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
    fun itemHeight(): Dp = PaletteTheme.componentThemes.dataDisplay.listItemHeight

    @Composable
    fun dividerHeight(): Dp = PaletteTheme.componentThemes.dataDisplay.listDividerHeight

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.dataDisplay.listContentPadding

    @Composable
    fun dividerColor(): Color = PaletteTheme.componentThemes.dataDisplay.listDividerColor
}
