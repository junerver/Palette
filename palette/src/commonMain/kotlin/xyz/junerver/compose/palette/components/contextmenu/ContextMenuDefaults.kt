package xyz.junerver.compose.palette.components.contextmenu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ContextMenuDefaults {
    val MenuWidth: Dp = 160.dp
    val ItemHeight: Dp = 50.dp
    val CornerRadius: Dp = 4.dp
    val ContentPadding: Dp = 8.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 15.sp
    val AnimationDuration: Int = 150
    val DismissAnimationDuration: Int = 160
    val ShadowElevation: Dp = 8.dp

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun textColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.colors.hint
}
