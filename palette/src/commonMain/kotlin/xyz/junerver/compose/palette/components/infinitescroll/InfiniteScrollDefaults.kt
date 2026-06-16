package xyz.junerver.compose.palette.components.infinitescroll

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object InfiniteScrollDefaults {
    val Threshold: Dp = 100.dp
    val LoadingPadding: Dp = 16.dp
    val FontSize: TextUnit = 14.sp

    @Composable
    fun textColor(): Color = PaletteTheme.colors.hint
}
