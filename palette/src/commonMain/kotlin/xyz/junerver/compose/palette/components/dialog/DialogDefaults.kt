package xyz.junerver.compose.palette.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DialogDefaults {
    val BorderRadius: Dp = 12.dp
    val TitlePaddingTop: Dp = 32.dp
    val TitlePaddingBottom: Dp = 16.dp
    val ContentPaddingBottom: Dp = 32.dp
    val HorizontalPadding: Dp = 24.dp
    val ButtonHeight: Dp = 56.dp
    val DividerWidth: Dp = 0.5.dp
    val TitleFontSize: TextUnit = 17.sp
    val ContentFontSize: TextUnit = 17.sp
    val ButtonFontSize: TextUnit = 17.sp

    @Composable
    fun okColor(): Color = PaletteTheme.colors.primary
}
