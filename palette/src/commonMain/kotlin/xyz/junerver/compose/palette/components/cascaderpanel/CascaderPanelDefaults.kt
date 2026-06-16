package xyz.junerver.compose.palette.components.cascaderpanel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CascaderPanelDefaults {
    val ColumnWidth: Dp = 200.dp
    val ColumnHeight: Dp = 220.dp
    val ItemHeight: Dp = 36.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val FontSize: TextUnit = 14.sp
    val ArrowSize: Dp = 16.dp

    @Composable
    @ReadOnlyComposable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    @ReadOnlyComposable
    fun itemTextColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun selectedItemColor(): Color = PaletteTheme.colors.primary

    @Composable
    @ReadOnlyComposable
    fun hoverColor(): Color = PaletteTheme.colors.border
}
